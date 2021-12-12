/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.misc;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.notifications.Notification;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;

public class NoTitles extends Module {

    public NoTitles() {
        super("NoTitles", "Completely removes any titles from rendering", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof TitleS2CPacket packet) {
                Notification.create(8000, "", true, "Blocked title \"" + packet.getTitle().getString() + "§r\"");
                event.setCancelled(true);
            } else if (pe.getPacket() instanceof SubtitleS2CPacket packet) {
                Notification.create(10000, "", true, "Blocked subtitle \"" + packet.getSubtitle().getString() + "§r\"");
                event.setCancelled(true);
            } else if (pe.getPacket() instanceof TitleFadeS2CPacket packet) {
                Notification.create(8000, "", true, "Blocked duration packet: FI:" + packet.getFadeInTicks() + " S:" + packet.getRemainTicks() + " FO:" + packet.getFadeOutTicks());
                event.setCancelled(true);
                Atomic.client.inGameHud.setDefaultTitleFade();
            }
        });
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

    @Override public void onHudRender() {

    }
}

