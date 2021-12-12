/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.CustomColor;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Waypoints extends Module {

    public static final File           WAYPOINTS_FILE;
    static final        String         TOP_NOTE  = """
            // ! WARNING
            // ! THIS IS SENSITIVE INFORMATION
            // ! DO NOT GIVE SOMEONE THIS FILE, IF THEY ASK FOR IT FOR NO REASON
            // ! DO ALSO NOT TAMPER WITH THIS FILE, IT COULD BREAK THE CONFIG
            // as you may be able to tell, this stores waypoints. refer to line nr. 3 to not leak important coords
            """;
    static final        List<Waypoint> waypoints = new ArrayList<>();

    static {
        WAYPOINTS_FILE = new File(Atomic.client.runDirectory.getAbsolutePath() + "/waypoints.atomic");
    }

    final BooleanValue tracers = (BooleanValue) this.config.create("Tracers", true).description("Show tracers to the waypoints");

    public Waypoints() {
        super("Waypoints", "Saves positions", ModuleType.RENDER);
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> { // gets called when we save config files
            Atomic.log(Level.INFO, "Saving " + waypoints.size() + " waypoints...");
            JsonObject base = new JsonObject();
            JsonArray wayp = new JsonArray();
            for (Waypoint waypoint : waypoints) {
                JsonObject current = new JsonObject();
                current.addProperty("posX", waypoint.posX);
                current.addProperty("posZ", waypoint.posZ);
                current.addProperty("color", waypoint.color);
                current.addProperty("name", waypoint.name);
                wayp.add(current);
            }
            base.add("waypoints", wayp);
            try {
                FileUtils.writeStringToFile(WAYPOINTS_FILE, TOP_NOTE + base, StandardCharsets.UTF_8);
            } catch (IOException e) {
                Atomic.log(Level.ERROR, "Failed to save waypoints!");
            }
        });
        Atomic.log(Level.INFO, "Loading waypoints..."); // gets called when we init the modules, before we load the config
        if (!WAYPOINTS_FILE.exists()) {
            Atomic.log(Level.WARN, "Waypoints file not found, first run or reset?");
            return;
        }
        if (!WAYPOINTS_FILE.isFile()) {
            Atomic.log(Level.WARN, "Waypoints \"file\" is not actually a file, resetting..");
            boolean deleted = WAYPOINTS_FILE.delete();
            if (!deleted) {
                Atomic.log(Level.ERROR, "Failed to delete waypoints file, what the fuck is going on?");
            }
        }

        try {
            String data = FileUtils.readFileToString(WAYPOINTS_FILE, StandardCharsets.UTF_8);
            JsonObject jo = new JsonParser().parse(data).getAsJsonObject();
            JsonArray ja = jo.getAsJsonArray("waypoints");
            for (JsonElement jsonElement : ja) {
                JsonObject current = (JsonObject) jsonElement;
                Waypoint w = new Waypoint(current.get("posX").getAsDouble(), current.get("posZ").getAsDouble(), current.get("color").getAsInt(), current.get("name").getAsString());
                waypoints.add(w);
            }
        } catch (Exception e) {
            Atomic.log(Level.ERROR, "Failed to read waypoints file! Is it corrupted?");
            boolean s = WAYPOINTS_FILE.delete();
            if (!s) {
                Atomic.log(Level.ERROR, "Also failed to delete the file, what is going on?");
            }
        }
    }

    public static List<Waypoint> getWaypoints() {
        return waypoints;
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        Vec3d ppos = Objects.requireNonNull(Atomic.client.player).getPos();
        Camera c = Atomic.client.gameRenderer.getCamera();
        for (Waypoint waypoint : new ArrayList<>(getWaypoints())) {
            Vec3d v = new Vec3d(waypoint.posX, Objects.requireNonNull(Atomic.client.world).getBottomY(), waypoint.posZ);
            int r = BackgroundHelper.ColorMixer.getRed(waypoint.color);
            int g = BackgroundHelper.ColorMixer.getGreen(waypoint.color);
            int b = BackgroundHelper.ColorMixer.getBlue(waypoint.color);
            Vec3d vv = new Vec3d(waypoint.posX + .5, c.getPos().y, waypoint.posZ + .5);
            if (tracers.getValue()) {
                Renderer.R3D.line(vv, Renderer.R3D.getCrosshairVector(), new CustomColor(r, g, b), matrices);
            }
            Vec3d screenSpaceCenter = Renderer.R2D.getScreenSpaceCoordinate(vv);
            double distance = vv.distanceTo(ppos);
            int a = 255;
            if (distance < 10) {
                a = (int) ((distance / 10) * 255);
            }
            Renderer.R3D.renderFilled(v, new Vec3d(1, Atomic.client.world.getHeight(), 1), new CustomColor(r, g, b, a), matrices);
            if (Renderer.R2D.isOnScreen(screenSpaceCenter)) {
                Utils.TickManager.runOnNextRender(() -> {
                    float w = FontRenderers.mono.getStringWidth(waypoint.name);
                    float pad = 2;
                    Renderer.R2D.fill(Themes.Theme.ATOMIC.getPalette()
                            .left(), screenSpaceCenter.x - w / 2 - pad, screenSpaceCenter.y - pad, screenSpaceCenter.x + w / 2 + pad, screenSpaceCenter.y + FontRenderers.mono.getFontHeight() + pad);
                    FontRenderers.mono.drawCenteredString(Renderer.R3D.getEmptyMatrixStack(), waypoint.name, screenSpaceCenter.x, screenSpaceCenter.y, 0xFFFFFF);
                });
            }
        }
    }

    @Override public void onHudRender() {

    }

    public static record Waypoint(double posX, double posZ, int color, String name) {

    }
}

