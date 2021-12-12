package me.zeroX150.atomic.feature.gui.hud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.hud.element.HudElement;
import me.zeroX150.atomic.feature.gui.hud.element.TargetHUD;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.MouseEvent;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.screen.ChatScreen;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HudRenderer {

    static         File        CONFIG = new File(Atomic.client.runDirectory, "hud.atomic");
    private static HudRenderer INSTANCE;
    boolean          isEditing     = false;
    boolean          mouseHeldDown = false;
    List<HudElement> elements      = register();
    double           prevX         = Utils.Mouse.getMouseX();
    double           prevY         = Utils.Mouse.getMouseY();
    double           prevWX        = Atomic.client.getWindow().getScaledWidth();
    double           prevWY        = Atomic.client.getWindow().getScaledHeight();

    private HudRenderer() {
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!isEditing) {
                return;
            }
            MouseEvent me = (MouseEvent) event;
            if (me.getAction() == MouseEvent.MouseEventType.MOUSE_CLICKED) {
                mouseHeldDown = true;
                prevX = Utils.Mouse.getMouseX();
                prevY = Utils.Mouse.getMouseY();
                for (HudElement element : elements) {
                    if (element.mouseClicked(Utils.Mouse.getMouseX(), Utils.Mouse.getMouseY())) {
                        break;
                    }
                }
            } else if (me.getAction() == MouseEvent.MouseEventType.MOUSE_RELEASED) {
                mouseHeldDown = false;
                for (HudElement element : elements) {
                    element.mouseReleased();
                }
            }
        });
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> saveConfig());
        loadConfig();
    }

    public static HudRenderer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HudRenderer();
        }
        return INSTANCE;
    }

    static List<HudElement> register() {
        List<HudElement> he = new ArrayList<>();
        he.add(new TargetHUD());
        return he;
    }

    void saveConfig() {
        Atomic.log(Level.INFO, "Saving hud");
        JsonArray root = new JsonArray();
        for (HudElement element : elements) {
            JsonObject current = new JsonObject();
            current.addProperty("id", element.getId());
            current.addProperty("px", element.getPosX());
            current.addProperty("py", element.getPosY());
            root.add(current);
        }
        try {
            FileUtils.write(CONFIG, root.toString(), StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Failed to write hud file");
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") void loadConfig() {
        Atomic.log(Level.INFO, "Loading hud");
        if (!CONFIG.isFile()) {
            CONFIG.delete();
        }
        if (!CONFIG.exists()) {
            Atomic.log(Level.INFO, "Skipping hud loading because file doesn't exist");
            return;
        }
        try {
            String contents = FileUtils.readFileToString(CONFIG, StandardCharsets.UTF_8);
            JsonArray ja = new JsonParser().parse(contents).getAsJsonArray();
            for (JsonElement jsonElement : ja) {
                JsonObject jo = jsonElement.getAsJsonObject();
                String id = jo.get("id").getAsString();
                int x = jo.get("px").getAsInt();
                int y = jo.get("py").getAsInt();
                for (HudElement element : elements) {
                    if (element.getId().equalsIgnoreCase(id)) {
                        element.setPosX(x);
                        element.setPosY(y);
                    }
                }
            }
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Failed to read hud file - corrupted?");
        }
        Atomic.log(Level.INFO, "Loaded hud");
    }

    public void fastTick() {
        double currentWX = Atomic.client.getWindow().getScaledWidth();
        double currentWY = Atomic.client.getWindow().getScaledHeight();
        if (currentWX != prevWX) {
            for (HudElement element : elements) {
                double px = element.getPosX();
                double perX = px / prevWX;
                element.setPosX(currentWX * perX);
            }
            prevWX = currentWX;
        }
        if (currentWY != prevWY) {
            for (HudElement element : elements) {
                double py = element.getPosY();
                double perY = py / prevWY;
                element.setPosY(currentWY * perY);
            }
            prevWY = currentWY;
        }
        isEditing = Atomic.client.currentScreen instanceof ChatScreen;
        if (mouseHeldDown) {
            for (HudElement element : elements) {
                element.mouseDragged(Utils.Mouse.getMouseX() - prevX, Utils.Mouse.getMouseY() - prevY);
            }
            prevX = Utils.Mouse.getMouseX();
            prevY = Utils.Mouse.getMouseY();
        }
        for (HudElement element : elements) {
            element.fastTick();
        }
    }

    public void render() {
        for (HudElement element : elements) {
            element.render();
            if (isEditing) {
                element.renderOutline();
            }
        }
    }
}
