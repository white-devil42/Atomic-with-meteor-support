package me.zeroX150.atomic.feature.gui.screen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.mixin.game.IMinecraftClientAccessor;
import me.zeroX150.authlib.login.mojang.MinecraftAuthenticator;
import me.zeroX150.authlib.login.mojang.MinecraftToken;
import me.zeroX150.authlib.login.mojang.profile.MinecraftProfile;
import me.zeroX150.authlib.login.mojang.profile.MinecraftProfileSkin;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.Session;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.Level;

import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class NewAltManagerScreen extends Screen implements FastTickable {

    public static ExecutorService     backburner   = Executors.newFixedThreadPool(2);
    static        File                ALTS_FILE    = new File(Atomic.client.runDirectory, "alts.atomic");
    static        String              TOP_NOTE     = """
            // DO NOT SHARE THIS FILE
            // This file contains sensitive information about your accounts
            // Unless you REALLY KNOW WHAT YOU ARE DOING, DO NOT SEND THIS TO ANYONE
            """;
    static        NewAltManagerScreen INSTANCE;
    final         int                 WIDGET_WIDTH = 180;
    AltContainer              selectedAlt  = null;
    Mode                      selectedMode = Mode.MOJANG;
    List<AltRenderer>         alts         = new ArrayList<>();
    ButtonWidget              delete;
    double                    renderScroll = 0;
    CyclingButtonWidget<Mode> mode;
    double                    scroll       = 0;

    private NewAltManagerScreen() {
        super(Text.of(""));
        loadAlts();
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> saveAlts());
    }

    public static NewAltManagerScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NewAltManagerScreen();
        }
        return INSTANCE;
    }

    @Override public void filesDragged(List<Path> paths) {
        try {
            for (Path path : paths) {
                File f = path.toFile();
                Mode toUse = mode.getValue();
                for (String s : FileUtils.readLines(f, StandardCharsets.UTF_8)) {
                    String[] pair = s.split(":");
                    if (pair.length < 2) {
                        throw new Exception("Truncated alt entry \"" + s + "\"");
                    }
                    String email = pair[0];
                    String password = pair[1];
                    if (!email.contains("@")) {
                        throw new Exception("Email \"" + email + "\" is invalid");
                    }
                    boolean add = true;
                    for (AltRenderer alt : alts) {
                        if (alt.container.email.equals(email) && alt.container.password.equals(password)) {
                            add = false;
                            break;
                        }
                    }
                    if (!add) {
                        continue;
                    }
                    AltRenderer renderer = new AltRenderer(0, -70, 0, 0, new AltContainer(email, password, toUse));
                    alts.add(renderer);
                }
            }
        } catch (Exception e) {
            Atomic.log(Level.ERROR, "Failed to load dragged alts file: " + e.getMessage());
        }
    }

    @Override public void onClose() {
        saveAlts();
        super.onClose();
    }

    @Override public void onFastTick() {
        for (AltRenderer alt : alts) {
            alt.onFastTick();
        }
        //            logIn.active = false;
        //             logIn.active = true;
        delete.active = selectedAlt != null;
        renderScroll = Transitions.transition(renderScroll, scroll, 7, 0);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") void loadAlts() {
        Atomic.log(Level.INFO, "Loading alts");

        if (!ALTS_FILE.isFile()) {
            ALTS_FILE.delete();
        }
        if (!ALTS_FILE.exists()) {
            Atomic.log(Level.INFO, "Skipping alt loading because file doesn't exist");
            return;
        }
        try {
            String contents = FileUtils.readFileToString(ALTS_FILE, StandardCharsets.UTF_8);
            JsonArray ja = new JsonParser().parse(contents).getAsJsonArray();
            for (JsonElement jsonElement : ja) {
                JsonObject jo = jsonElement.getAsJsonObject();
                AltContainer container = new AltContainer(jo.get("email").getAsString(), jo.get("password").getAsString(), Mode.valueOf(Mode.class, jo.get("type").getAsString()));
                container.usedCount = jo.get("used").getAsInt();
                container.username = jo.has("cachedUsername") && jo.get("cachedUsername") != null && !jo.get("cachedUsername").isJsonNull() ? jo.get("cachedUsername").getAsString() : null;
                try {
                    container.uuid = jo.has("cachedUUID") && jo.get("cachedUUID") != null && !jo.get("cachedUUID").isJsonNull() ? UUID.fromString(jo.get("cachedUUID").getAsString()) : null;
                    if (container.uuid != null) {
                        backburner.execute(() -> AltContainer.getSkin(container.uuid, "", identifier -> container.skinTexture = identifier));
                    }
                } catch (Exception ignored) {

                }
                alts.add(new AltRenderer(0, -70, 100, 100, container));
            }
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Failed to read alts file - corrupted?");
        }
    }

    void saveAlts() {
        Atomic.log(Level.INFO, "Saving alts");
        JsonArray root = new JsonArray();
        for (AltRenderer alt1 : alts) {
            AltContainer alt = alt1.container;
            JsonObject current = new JsonObject();
            current.addProperty("email", alt.email);
            current.addProperty("password", alt.password);
            current.addProperty("type", alt.type.name());
            current.addProperty("used", alt.usedCount);
            current.addProperty("cachedUsername", alt.username);
            current.addProperty("cachedUUID", alt.uuid != null ? alt.uuid.toString() : null);
            root.add(current);
        }
        try {
            FileUtils.write(ALTS_FILE, TOP_NOTE + "\n" + root, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Failed to write alts file");
        }
    }

    public void doLogin() {
        if (!selectedAlt.valid) {
            MessageScreen msc = new MessageScreen(this, "Alt error", "The alt is invalid.", t -> {
            }, MessageScreen.ScreenType.OK);
            Atomic.client.setScreen(msc);
            return;
        }
        if (selectedAlt.username == null || selectedAlt.uuid == null) {
            MessageScreen msc = new MessageScreen(this, "Alt error", "Alt is not logged in yet, please wait", t -> {
            }, MessageScreen.ScreenType.OK);
            Atomic.client.setScreen(msc);
            return;
        }
        //        Session newSession = new Session(selectedAlt.username, selectedAlt.uuid.toString(), selectedAlt.accessToken, "mojang");
        Session newSession = new Session(selectedAlt.username, selectedAlt.uuid.toString(), selectedAlt.accessToken, Optional.empty(), Optional.empty(), Session.AccountType.MOJANG);
        ((IMinecraftClientAccessor) Atomic.client).setSession(newSession);
        selectedAlt.usedCount++;
    }

    @Override protected void init() {
        /*logIn = new ButtonWidget(10, 10, WIDGET_WIDTH, 20, Text.of("Login"), button -> {
            doLogin();
        });*/
        delete = new ButtonWidget(width - WIDGET_WIDTH - 5, height - 25, WIDGET_WIDTH, 20, Text.of("Delete"), button -> {
            alts.removeIf(alt -> alt.container == selectedAlt);
            selectedAlt = null;
        });
        TextFieldWidget email = new TextFieldWidget(this.textRenderer, width - WIDGET_WIDTH - 5, height - 25 - 25 - 25 - 25 - 25, WIDGET_WIDTH, 20, Text.of("SPECIAL:Email"));
        TextFieldWidget password = new TextFieldWidget(this.textRenderer, width - WIDGET_WIDTH - 5, height - 25 - 25 - 25 - 25, WIDGET_WIDTH, 20, Text.of("SPECIAL:Password"));
        mode = CyclingButtonWidget.<Mode>builder(mode1 -> Text.of(mode1.getT())).initially(Mode.MOJANG).values(Mode.MOJANG, Mode.MICROSOFT, Mode.CRACKED)
                .build(width - WIDGET_WIDTH - 5, height - 25 - 25 - 25, WIDGET_WIDTH, 20, Text.of("Type"), (button, value) -> this.selectedMode = value);
        ButtonWidget add = new ButtonWidget(width - WIDGET_WIDTH - 5, height - 25 - 25, WIDGET_WIDTH, 20, Text.of("Add"), button -> {
            if (email.getText().isEmpty()) {
                MessageScreen msc = new MessageScreen(this, "Alt error", "You need to provide an email or username", t -> {
                }, MessageScreen.ScreenType.OK);
                Atomic.client.setScreen(msc);
                return;
            }
            if (password.getText().isEmpty() && selectedMode != Mode.CRACKED) {
                MessageScreen msc = new MessageScreen(this, "Alt error", "You need to provide a password, the alt isn't cracked", t -> {
                }, MessageScreen.ScreenType.OK);
                Atomic.client.setScreen(msc);
                return;
            }
            for (AltRenderer alt : alts) {
                if (alt.container.email.equals(email.getText()) && alt.container.password.equals(password.getText())) {
                    selectedAlt = alt.container;
                    return;
                }
            }
            AltContainer ac = new AltContainer(email.getText(), password.getText(), selectedMode);
            alts.add(new AltRenderer(0, -70, 100, 10, ac));
        });
        //addDrawableChild(logIn);
        addDrawableChild(delete);
        addDrawableChild(email);
        addDrawableChild(password);
        addDrawableChild(add);
        addDrawableChild(mode);
        super.init();
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        int yOffset = 10;
        int widthLeft = Atomic.client.getWindow().getScaledWidth() - 5 - WIDGET_WIDTH - 10;
        widthLeft = Math.max(widthLeft, 200);
        Renderer.R2D.scissor(5, 5, widthLeft, height - 5 - FontRenderers.normal.getFontHeight() - 2);
        matrices.push();
        matrices.translate(0, -renderScroll, 0);
        for (AltRenderer alt : alts) {
            alt.renderX = alt.x = /*WIDGET_WIDTH + 20*/5;
            alt.y = yOffset;
            alt.setWidth(widthLeft);
            alt.setHeight(50);
            yOffset += alt.getHeight() + 5;
            if (alt.y - renderScroll + alt.getHeight() < 0 || alt.y - renderScroll > height) {
                continue;
            }
            alt.render(matrices, mouseX, mouseY, delta);
        }
        Renderer.R2D.unscissor();
        matrices.pop();

        if (selectedAlt != null) {
            int x = widthLeft + 10 + 55;
            int y = 10;

            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, selectedAlt.skinTexture);
            Screen.drawTexture(matrices, x - 55, y - 5, 50, 50, 8.0F, 8, 8, 8, 64, 64);

            FontRenderers.normal.drawString(matrices, ObjectUtils.firstNonNull(selectedAlt.username, selectedAlt.email), x, y, 0xFFFFFF);
            FontRenderers.normal.drawString(matrices, "Pass: " + "*".repeat(selectedAlt.password.length()), x, y + FontRenderers.normal.getFontHeight(), 0xFFFFFF);
            //FontRenderers.mono.drawString(matrices, ObjectUtils.firstNonNull(selectedAlt.uuid, UUID.fromString("0-0-0-0-0")).toString(), x, y + FontRenderers.normal.getFontHeight(), 0xAAAAAA);
            FontRenderers.normal.drawString(matrices, "Used " + selectedAlt.usedCount + " time" + (selectedAlt.usedCount != 1 ? "s" : ""), x, y + FontRenderers.normal.getFontHeight() * 2 + 3, 0xFFFFFF);
            FontRenderers.normal.drawString(matrices, "Is valid? " + (selectedAlt.valid ? "§aYes" : "§cNo"), x, y + FontRenderers.normal.getFontHeight() * 3 + 3, 0xFFFFFF);
        }
        FontRenderers.normal.drawString(matrices, "Drag and drop a .txt with alts to import", 1, height - FontRenderers.normal.getFontHeight(), 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        int max = 0;
        for (AltRenderer alt : alts) {
            max = Math.max(alt.y + alt.getHeight(), max);
        }
        max += 10; // down padding
        max -= height; // height
        max = Math.max(max, 0);
        scroll -= amount * 10;
        scroll = MathHelper.clamp(scroll, 0, max);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element child : children()) {
            child.mouseClicked(-1, -1, button);
        }
        for (AltRenderer alt : alts) {
            alt.mouseClicked(mouseX, mouseY + renderScroll, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    enum Mode {
        MICROSOFT("Microsoft"), MOJANG("Mojang"), CRACKED("Cracked");
        String t;

        Mode(String t) {
            this.t = t;
        }

        public String getT() {
            return t;
        }
    }

    private static class AltRenderer extends ClickableWidget implements FastTickable {


        AltContainer container;
        double       renderX, renderY;

        public AltRenderer(int x, int y, int width, int height, AltContainer container) {
            super(x, y, width, height, Text.of(""));
            this.container = container;
        }

        @Override public void onFastTick() {
            renderX = Transitions.transition(renderX, this.x, 7);
            renderY = Transitions.transition(renderY, this.y, 7);
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (mouseX >= renderX && mouseX <= renderX + width && mouseY >= renderY && mouseY <= renderY + height) {
                if (getInstance().selectedAlt == this.container) {
                    backburner.execute(() -> {
                        this.container.login();
                        Atomic.client.execute(() -> getInstance().doLogin());
                    });
                }
                getInstance().selectedAlt = this.container;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            Color bg = new Color(20, 20, 20, 100);
            if (getInstance().selectedAlt == container) {
                bg = new Color(70, 70, 70, 120);
            }
            Renderer.R2D.fill(matrices, bg, renderX, renderY, renderX + width, renderY + height);
            String username = container.username;
            if (username == null) {
                username = container.email;
            }
            String uuid = container.uuid == null ? "Unknown UUID" : container.uuid.toString();
            Color rc = Color.WHITE;
            if (!container.valid) {
                rc = rc.darker().darker();
            }

            int tDimensions = Math.min(height, width) - 4;
            RenderSystem.setShaderColor(1, 1, 1, 1);
            RenderSystem.setShaderTexture(0, container.skinTexture);
            Screen.drawTexture(matrices, (int) (renderX + 2), (int) (renderY + 2), tDimensions, tDimensions, 8.0F, 8, 8, 8, 64, 64);

            FontRenderers.normal.drawString(matrices, username, renderX + 2 + tDimensions + 2, renderY + 1, rc.getRGB());
            FontRenderers.mono.drawString(matrices, uuid, renderX + 2 + tDimensions + 2, renderY + 1 + FontRenderers.normal.getFontHeight(), 0xAAAAAA);
            FontRenderers.normal.drawString(matrices, "Used " + container.usedCount + " time" + (container.usedCount != 1 ? "s" : ""), renderX + 2 + tDimensions + 2, renderY + 1 + FontRenderers.normal.getFontHeight() + FontRenderers.mono.getFontHeight(), rc.getRGB());
            FontRenderers.normal.drawString(matrices, "Type: " + container.type.t, renderX + 2 + tDimensions + 2, renderY + 1 + FontRenderers.normal.getFontHeight() * 2 + FontRenderers.mono.getFontHeight(), rc.getRGB());
            String t = "";
            if (container.didLogin && !container.loginDone) {
                t = "Logging in...";
            }
            if (Atomic.client.getSession().getProfile().getId().equals(container.uuid)) {
                t = "§aCurrently using";
            }
            float w = FontRenderers.normal.getStringWidth(t) + 2;
            FontRenderers.normal.drawString(matrices, t, renderX + width - w, renderY + height - FontRenderers.normal.getFontHeight() - 2, 0xFFFFFF);
        }


        @Override public void appendNarrations(NarrationMessageBuilder builder) {

        }
    }

    static class AltContainer {

        static Map<UUID, Identifier> skins = new HashMap<>();
        boolean didLogin  = false;
        boolean loginDone = false;
        String  email, password;
        String               username;
        UUID                 uuid;
        boolean              valid       = true;
        Mode                 type;
        int                  usedCount   = 0;
        MinecraftProfileSkin latestSkin;
        Identifier           skinTexture = DefaultSkinHelper.getTexture();
        String               accessToken;

        public AltContainer(String email, String password, Mode type) {
            this.email = email;
            this.password = password;
            this.type = type;
        }

        static void getSkin(UUID uuid, String uname, Consumer<Identifier> callback) {
            if (skins.containsKey(uuid)) {
                callback.accept(skins.get(uuid));
            } else {
                if (uuid == null && uname == null) {
                    callback.accept(DefaultSkinHelper.getTexture());
                    return;
                }
                Atomic.client.getSkinProvider().loadSkin(new GameProfile(uuid, uname), (type, id, texture) -> {
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        skins.put(uuid, id);
                        callback.accept(id);
                    }
                }, true);
            }
        }

        public void login() {
            if (didLogin) {
                return;
            }
            didLogin = true;
            try {
                MinecraftAuthenticator auth = new MinecraftAuthenticator();
                MinecraftToken token = switch (type) {
                    case MOJANG -> auth.login(email, password);
                    case MICROSOFT -> auth.loginWithMicrosoft(email, password);
                    case CRACKED -> null;
                };
                if (token == null && password.equals("")) {
                    valid = true;
                    this.uuid = UUID.randomUUID();
                    this.username = email;
                    this.accessToken = "AtomicOnTop";
                    return;
                }
                if (token == null) {
                    throw new NullPointerException();
                }
                this.accessToken = token.getAccessToken();
                MinecraftProfile profile = auth.getGameProfile(token);
                username = profile.getUsername();
                uuid = profile.getUuid();
                latestSkin = profile.getSkins().get(profile.getSkins().size() - 1);
                getSkin(uuid, username, identifier -> skinTexture = identifier);
            } catch (Exception ignored) {
                valid = false;
            } finally {
                loginDone = true;
            }
        }
    }
}
