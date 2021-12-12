package me.zeroX150.authlib.struct;

import com.google.gson.Gson;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * The class Authenticator is used to log in to mojang or microsoft
 */
public abstract class Authenticator<T> {

    protected final Gson       gson   = new Gson();
    protected final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    /**
     * Login string.
     *
     * @param email    the email
     * @param password the password
     * @return the string
     */
    public abstract T login(String email, String password);

}
