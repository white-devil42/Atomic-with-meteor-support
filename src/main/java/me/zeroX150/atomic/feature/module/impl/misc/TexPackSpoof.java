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
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;

import java.util.Objects;

public class TexPackSpoof extends Module {

    public TexPackSpoof() {
        super("TexPackSpoof", "Tells the server you accepted their shitty resource pack", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event1 -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof ResourcePackSendS2CPacket pack) {
                event.setCancelled(true);
                Notification.create(6000, "AutoServerTexPack", true, "Received texture pack. Spoofing.");
                Utils.Logging.messageChat("Server sent texture pack.");
                Utils.Logging.messageChat("  SHA1: " + pack.getSHA1());
                Utils.Logging.messageChat("  Download: " + pack.getURL());
                Utils.Logging.messageChat("  Is required: " + (pack.isRequired() ? "§cYes" : "§aNo") + "§r");
                Objects.requireNonNull(Atomic.client.getNetworkHandler()).sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.ACCEPTED));
                Atomic.client.getNetworkHandler().sendPacket(new ResourcePackStatusC2SPacket(ResourcePackStatusC2SPacket.Status.SUCCESSFULLY_LOADED));
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

