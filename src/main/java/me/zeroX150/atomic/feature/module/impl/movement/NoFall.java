/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.mixin.network.IPlayerMoveC2SPacketAccessor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

/**
 * @see IPlayerMoveC2SPacketAccessor
 */
public class NoFall extends Module {

    public static MultiValue  mode;
    final         SliderValue fallDist = (SliderValue) this.config.create("Fall distance", 3, 0, 10, 1).description("The distance to fall for to enable the module");
    public        boolean     enabled  = true;

    public NoFall() {
        super("NoFall", "no fall damage", ModuleType.MOVEMENT);

        mode = this.config.create("Mode", "OnGround", "OnGround", "Packet", "BreakFall");
        mode.description("The mode of the module");
        this.fallDist.showOnlyIf(() -> !mode.getValue().equalsIgnoreCase("onground"));
        Events.registerEventHandler(EventType.PACKET_SEND, event1 -> {
            if (!this.isEnabled() || !enabled) {
                return;
            }
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof PlayerMoveC2SPacket) {
                if (mode.getValue().equalsIgnoreCase("onground")) {
                    ((IPlayerMoveC2SPacketAccessor) event.getPacket()).setOnGround(true);
                }
            }
        });
    }

    @Override public void tick() {
        if (Atomic.client.player == null || Atomic.client.getNetworkHandler() == null) {
            return;
        }
        if (Atomic.client.player.fallDistance > fallDist.getValue()) {
            switch (mode.getValue().toLowerCase()) {
                case "packet" -> Atomic.client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true));
                case "breakfall" -> {
                    Atomic.client.player.setVelocity(0, 0.1, 0);
                    Atomic.client.player.fallDistance = 0;
                }
            }
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return mode.getValue();
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

