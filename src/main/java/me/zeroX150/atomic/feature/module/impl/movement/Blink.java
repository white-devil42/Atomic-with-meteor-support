/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;

import java.util.ArrayList;
import java.util.List;

public class Blink extends Module {

    final MultiValue      mode  = (MultiValue) this.config.create("Mode", "delay", "delay", "drop").description("Whether or not to delay or drop the packets");
    final List<Packet<?>> queue = new ArrayList<>();

    public Blink() {
        super("Blink", "confuses chinese anticheats", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.PACKET_SEND, event1 -> {
            if (!this.isEnabled()) {
                return;
            }
            if (Atomic.client.player == null || Atomic.client.world == null) {
                setEnabled(false);
                return;
            }
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof KeepAliveC2SPacket) {
                return;
            }
            event.setCancelled(true);
            if (mode.getValue().equalsIgnoreCase("delay")) {
                queue.add(event.getPacket());
            }
        });
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {
        if (Atomic.client.player == null || Atomic.client.getNetworkHandler() == null) {
            queue.clear();
            return;
        }
        for (Packet<?> packet : queue.toArray(new Packet<?>[0])) {
            Atomic.client.getNetworkHandler().sendPacket(packet);
        }
        queue.clear();
    }

    @Override public String getContext() {
        return queue.size() + "";
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}
