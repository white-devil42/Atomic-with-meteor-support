package me.zeroX150.authlib.login.mojang;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import me.zeroX150.authlib.exception.AuthFailureException;
import me.zeroX150.authlib.login.microsoft.MicrosoftAuthenticator;
import me.zeroX150.authlib.login.microsoft.XboxToken;
import me.zeroX150.authlib.login.mojang.profile.MinecraftProfile;
import me.zeroX150.authlib.login.mojang.profile.MinecraftProfileCape;
import me.zeroX150.authlib.login.mojang.profile.MinecraftProfileSkin;
import me.zeroX150.authlib.struct.Authenticator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The class Minecraft authenticator is used to log in with normal minecraft details or with microsoft
 */
public class MinecraftAuthenticator extends Authenticator<me.zeroX150.authlib.login.mojang.MinecraftToken> {

    /**
     * The Microsoft authenticator is used for {@link #loginWithMicrosoft(String, String)}
     */
    protected final MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();

    @Override public me.zeroX150.authlib.login.mojang.MinecraftToken login(String email, String password) {
        try {
            URL url = new URL("https://authserver.mojang.com/authenticate");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            JsonObject request = new JsonObject();
            JsonObject agent = new JsonObject();
            agent.addProperty("name", "Minecraft");
            agent.addProperty("version", "1");
            request.add("agent", agent);
            request.addProperty("username", email);
            request.addProperty("password", password);
            request.addProperty("requestUser", false);

            String requestBody = request.toString();

            httpURLConnection.setFixedLengthStreamingMode(requestBody.length());
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Host", "authserver.mojang.com");
            httpURLConnection.connect();

            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(requestBody.getBytes(StandardCharsets.US_ASCII));
            }

            JsonObject jsonObject = parseResponseData(httpURLConnection);
            return new me.zeroX150.authlib.login.mojang.MinecraftToken(jsonObject.get("accessToken").getAsString(), ((JsonObject) jsonObject.get("selectedProfile")).get("name").getAsString());
        } catch (IOException exception) {
            throw new AuthFailureException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.getMessage()));
        }

    }

    /**
     * Login with microsoft email and microsoft password
     *
     * @param email    the email
     * @param password the password
     * @return the minecraft token
     */
    public me.zeroX150.authlib.login.mojang.MinecraftToken loginWithMicrosoft(String email, String password) {
        XboxToken xboxToken = microsoftAuthenticator.login(email, password);

        try {
            URL url = new URL("https://api.minecraftservices.com/authentication/login_with_xbox");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            JsonObject request = new JsonObject();
            request.addProperty("identityToken", "XBL3.0 x=" + xboxToken.getUhs() + ";" + xboxToken.getToken());

            String requestBody = request.toString();

            httpURLConnection.setFixedLengthStreamingMode(requestBody.length());
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Host", "api.minecraftservices.com");
            httpURLConnection.connect();

            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                outputStream.write(requestBody.getBytes(StandardCharsets.US_ASCII));
            }

            JsonObject jsonObject = microsoftAuthenticator.parseResponseData(httpURLConnection);
            return new me.zeroX150.authlib.login.mojang.MinecraftToken(jsonObject.get("access_token").getAsString(), jsonObject.get("username").getAsString());
        } catch (IOException exception) {
            throw new AuthFailureException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.getMessage()));
        }
    }

    /**
     * Check ownership from {@link me.zeroX150.authlib.login.mojang.MinecraftToken} and generate {@link MinecraftProfile}
     *
     * @param minecraftToken the minecraft token
     * @return the minecraft profile
     */
    public MinecraftProfile getGameProfile(MinecraftToken minecraftToken) {
        try {
            URL url = new URL("https://api.minecraftservices.com/minecraft/profile");
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.setRequestProperty("Authorization", "Bearer " + minecraftToken.getAccessToken());
            httpURLConnection.setRequestProperty("Host", "api.minecraftservices.com");
            httpURLConnection.connect();

            JsonObject jsonObject = parseResponseData(httpURLConnection);

            UUID uuid = generateUUID(jsonObject.get("id").getAsString());
            String name = jsonObject.get("name").getAsString();
            List<MinecraftProfileSkin> minecraftSkins = gson.fromJson(jsonObject.get("skins"), new TypeToken<List<MinecraftProfileSkin>>() {
            }.getType());
            List<MinecraftProfileCape> minecraftCapes = gson.fromJson(jsonObject.get("capes"), new TypeToken<List<MinecraftProfileCape>>() {
            }.getType()); // MinecraftCape.class

            return new MinecraftProfile(uuid, name, minecraftSkins, minecraftCapes);
        } catch (IOException exception) {
            throw new AuthFailureException(String.format("Authentication error. Request could not be made! Cause: '%s'", exception.getMessage()));
        }
    }

    /**
     * Parse response data to {@link JsonObject}
     *
     * @param httpURLConnection the http url connection
     * @return the json object
     * @throws IOException the io exception
     */
    public JsonObject parseResponseData(HttpURLConnection httpURLConnection) throws IOException {
        BufferedReader bufferedReader;

        if (httpURLConnection.getResponseCode() != 200) {
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
        } else {
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
        }
        String lines = bufferedReader.lines().collect(Collectors.joining());

        JsonObject jsonObject = gson.fromJson(lines, JsonObject.class);
        if (jsonObject.has("error")) {
            throw new AuthFailureException(String.format("Could not find profile!. Error: '%s'", jsonObject.get("errorMessage").getAsString()));
        }
        return jsonObject;
    }


    /**
     * Generate uuid from trimmedUUID
     *
     * @param trimmedUUID the trimmed uuid
     * @return the uuid
     * @throws IllegalArgumentException the illegal argument exception
     */
    public UUID generateUUID(String trimmedUUID) throws IllegalArgumentException {
        if (trimmedUUID == null) {
            throw new IllegalArgumentException();
        }
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
            return UUID.fromString(builder.toString());
        } catch (StringIndexOutOfBoundsException e) {
            return null;
        }
    }

}
