/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.hud.HudRenderer;
import me.zeroX150.atomic.feature.gui.notifications.Notification;
import me.zeroX150.atomic.feature.gui.notifications.NotificationRenderer;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Hud extends Module {

    public static double       currentTps             = 0;
    public final  SliderValue  smoothSelectTransition = config.create("Selection smooth", 10, 1, 30, 1);
    public final  BooleanValue betterHotbar           = (BooleanValue) config.create("Better hotbar", true).description("Makes the hotbar sexier");
    final         BooleanValue fps                    = (BooleanValue) config.create("FPS", true).description("Whether or not to show FPS");
    final         BooleanValue tps                    = (BooleanValue) config.create("TPS", true).description("Whether or not to show TPS");
    final         BooleanValue coords                 = (BooleanValue) config.create("Coordinates", true).description("Whether or not to show coordinates");
    final         BooleanValue time                   = (BooleanValue) config.create("Time", true).description("Whether or not to show the current IRL time");
    final         BooleanValue ping                   = (BooleanValue) config.create("Ping", true).description("Whether or not to show your ping");
    final         BooleanValue bps                    = (BooleanValue) config.create("Speed", true).description("Whether or not to show your speed in blocks per second");
    final         BooleanValue modules                = (BooleanValue) config.create("Modules", true).description("Whether or not to show the enabled modules");
    final         DateFormat   df                     = new SimpleDateFormat("h:mm aa");
    final         DateFormat   minSec                 = new SimpleDateFormat("mm:ss");
    long         lastTimePacketReceived;
    double       rNoConnectionPosY   = -10d;
    Notification serverNotResponding = null;

    public Hud() {
        super("Hud", "shows info about shit", ModuleType.RENDER);
        lastTimePacketReceived = System.currentTimeMillis();

        Events.registerEventHandler(EventType.PACKET_RECEIVE, event1 -> {
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
                currentTps = Utils.Math.roundToDecimal(calcTps(System.currentTimeMillis() - lastTimePacketReceived), 2);
                lastTimePacketReceived = System.currentTimeMillis();
            }
        });
    }

    double calcTps(double n) {
        return (20.0 / Math.max((n - 1000.0) / (500.0), 1.0));
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

    }

    boolean shouldNoConnectionDropDown() {
        return System.currentTimeMillis() - lastTimePacketReceived > 2000;
    }

    @Override public void onHudRender() {
        if (Atomic.client.getNetworkHandler() == null) {
            return;
        }
        if (Atomic.client.player == null) {
            return;
        }
        MatrixStack ms = Renderer.R3D.getEmptyMatrixStack();
        if (!shouldNoConnectionDropDown()) {
            if (serverNotResponding != null) {
                serverNotResponding.duration = 0;
            }
        } else {
            if (serverNotResponding == null) {
                serverNotResponding = Notification.create(-1, "", true, "Server not responding! " + minSec.format(System.currentTimeMillis() - lastTimePacketReceived));
            }
            serverNotResponding.contents = new String[]{"Server not responding! " + minSec.format(System.currentTimeMillis() - lastTimePacketReceived)};
        }
        if (!NotificationRenderer.topBarNotifications.contains(serverNotResponding)) {
            serverNotResponding = null;
        }
        //Atomic.fontRenderer.drawCenteredString(ms, "Server not responding! " + minSec.format(System.currentTimeMillis() - lastTimePacketReceived), Atomic.client.getWindow().getScaledWidth() / 2d, rNoConnectionPosY, 0xFF7777);

        List<HudEntry> entries = new ArrayList<>();
        if (coords.getValue()) {
            BlockPos bp = Atomic.client.player.getBlockPos();
            entries.add(new HudEntry("XYZ", bp.getX() + " " + bp.getY() + " " + bp.getZ(), false, false));
        }
        if (fps.getValue()) {
            entries.add(new HudEntry("FPS", Atomic.client.fpsDebugString.split(" ")[0], false, false));
        }
        if (tps.getValue()) {
            entries.add(new HudEntry("TPS", (currentTps == -1 ? "Calculating" : currentTps) + "", false, false));
        }
        if (ping.getValue()) {
            PlayerListEntry e = Atomic.client.getNetworkHandler().getPlayerListEntry(Atomic.client.player.getUuid());
            entries.add(new HudEntry("Ping", (e == null ? "?" : e.getLatency()) + " ms", false, false));
        }
        if (bps.getValue()) {
            double px = Atomic.client.player.prevX;
            double py = Atomic.client.player.prevY;
            double pz = Atomic.client.player.prevZ;
            Vec3d v = new Vec3d(px, py, pz);
            double dist = v.distanceTo(Atomic.client.player.getPos());
            entries.add(new HudEntry("Speed", Utils.Math.roundToDecimal(dist * 20, 2) + "", false, false));
        }
        if (time.getValue()) {
            entries.add(new HudEntry("", df.format(new Date()), true, true));
        }
        //entries.sort(Comparator.comparingInt(entry -> Atomic.client.textRenderer.getWidth((entry.t.isEmpty()?"":entry.t+" ")+entry.v)));
        int yOffset = (int) (23 / 2 + (FontRenderers.getNormal().getMarginHeight()));
        int changedYOffset = -2;
        int xOffset = 2;
        for (HudEntry entry : entries) {
            String t = (entry.t.isEmpty() ? "" : entry.t + " ") + entry.v;
            float width = FontRenderers.getNormal().getStringWidth(t);
            float offsetToUse = Atomic.client.getWindow().getScaledHeight() - (entry.renderTaskBar ? ((23 / 2f + FontRenderers.getNormal().getFontHeight() / 2f)) : yOffset);
            float xL = (entry.renderTaskBar && entry.renderRTaskBar) ? (Atomic.client.getWindow().getScaledWidth() - 5 - width) : xOffset;
            if (xL == xOffset) {
                xOffset += width + FontRenderers.getNormal().getStringWidth(" ");
            }
            changedYOffset++;
            if (!entry.renderTaskBar && changedYOffset == 0) {
                yOffset -= FontRenderers.getNormal().getMarginHeight();
                xOffset = 2;
            }
            //Atomic.client.textRenderer.draw(ms,t,xL,offsetToUse,0xFFFFFF);
            if (!entry.t.isEmpty()) {
                Color rgb = Utils.getCurrentRGB();
                FontRenderers.getNormal().drawString(ms, entry.t, xL, offsetToUse, Utils.getCurrentRGB().getRGB());
                //Atomic.client.textRenderer.draw(ms, entry.t, xL, offsetToUse, Client.getCurrentRGB().getRGB());
                FontRenderers.getNormal().drawString(ms, entry.v, xL + FontRenderers.getNormal().getStringWidth(entry.t + " "), offsetToUse, rgb.darker().getRGB());
                //Atomic.client.textRenderer.draw(ms, entry.v, xL + Atomic.client.textRenderer.getWidth(entry.t + " "), offsetToUse, rgb.darker().getRGB());
            } else {
                FontRenderers.getNormal().drawString(ms, t, xL, offsetToUse, Utils.getCurrentRGB().getRGB());
                //Atomic.client.textRenderer.draw(ms, t, xL, offsetToUse, Client.getCurrentRGB().getRGB());
            }
        }

        if (modules.getValue()) {
            int moduleOffset = 0;
            float rgbIncrementer = 0.03f;
            float currentRgbSeed = (System.currentTimeMillis() % 4500) / 4500f;
            // jesus fuck
            Module[] v = ModuleRegistry.getModules().stream().filter(Module::isEnabled).sorted(Comparator.comparingDouble(value -> FontRenderers.getNormal()
                            .getStringWidth(value.getName() + (value.getContext() != null ? " " + value.getContext() : "")))) // i mean it works?
                    .toArray(Module[]::new);
            ArrayUtils.reverse(v);
            float maxWidth = 0;
            for (Module module : v) {
                currentRgbSeed %= 1f;
                int r = Color.HSBtoRGB(currentRgbSeed, 0.7f, 1f);
                currentRgbSeed += rgbIncrementer;
                String w = module.getName() + (module.getContext() == null ? "" : " " + module.getContext());
                float totalWidth = FontRenderers.getNormal().getStringWidth(w);
                maxWidth = Math.max(maxWidth, totalWidth);
                Color c = new Color(r);
                Color inv = new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
                MatrixStack stack = Renderer.R3D.getEmptyMatrixStack();
                FontRenderers.getNormal().drawString(stack, module.getName(), Atomic.client.getWindow().getScaledWidth() - 4 - totalWidth, moduleOffset + .5, r);
                Renderer.R2D.fill(stack, c, Atomic.client.getWindow().getScaledWidth() - 2, moduleOffset, Atomic.client.getWindow().getScaledWidth(), moduleOffset + FontRenderers.getNormal()
                        .getMarginHeight() + 1);
                if (module.getContext() != null) {
                    FontRenderers.getNormal().drawString(stack, module.getContext(), Atomic.client.getWindow().getScaledWidth() - 4 - totalWidth + FontRenderers.getNormal()
                            .getStringWidth(module.getName() + " "), moduleOffset + .5, inv.getRGB());
                }
                moduleOffset += FontRenderers.getNormal().getMarginHeight() + 1;
            }
        }
        HudRenderer.getInstance().render();
    }

    @Override public void onFastTick() {
        rNoConnectionPosY = Transitions.transition(rNoConnectionPosY, shouldNoConnectionDropDown() ? 10 : -10, 10);
        HudRenderer.getInstance().fastTick();
    }

    static class HudEntry {

        public final String  t;
        public final String  v;
        public final boolean renderTaskBar;
        public final boolean renderRTaskBar;

        public HudEntry(String t, String v, boolean renderInTaskBar, boolean renderRightTaskBar) {
            this.t = t;
            this.v = v;
            this.renderRTaskBar = renderRightTaskBar;
            this.renderTaskBar = renderInTaskBar;
        }
    }
}
