/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.helper.event.events.PlayerNoClipQueryEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Freecam extends Module {

    final SliderValue speed = (SliderValue) this.config.create("Speed", 1, 0, 10, 1).description("The speed to fly with");
    Vec3d   startloc;
    float   pitch = 0f;
    float   yaw   = 0f;
    boolean flewBefore;

    public Freecam() {
        super("Freecam", "look outside your body", ModuleType.RENDER);
        Events.registerEventHandler(EventType.PACKET_SEND, event1 -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent event = (PacketEvent) event1;
            if (event.getPacket() instanceof PlayerMoveC2SPacket) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof PlayerInputC2SPacket) {
                event.setCancelled(true);
            }
        });
        Events.registerEventHandler(EventType.NOCLIP_QUERY, event -> {
            if (!this.isEnabled() || ((PlayerNoClipQueryEvent) event).getPlayer().isOnGround()) {
                return;
            }
            ((PlayerNoClipQueryEvent) event).setNoClipState(PlayerNoClipQueryEvent.NoClipState.ACTIVE);
        });
    }

    @Override public void tick() {
        Objects.requireNonNull(Atomic.client.player).getAbilities().setFlySpeed((float) (this.speed.getValue() + 0f) / 20f);
        Atomic.client.player.getAbilities().flying = true;
    }

    @Override public void enable() {
        startloc = Objects.requireNonNull(Atomic.client.player).getPos();
        pitch = Atomic.client.player.getPitch();
        yaw = Atomic.client.player.getYaw();
        Atomic.client.gameRenderer.setRenderHand(false);
        flewBefore = Atomic.client.player.getAbilities().flying;
        Atomic.client.player.setOnGround(false);
    }

    @Override public void disable() {
        if (startloc != null) {
            Objects.requireNonNull(Atomic.client.player).updatePosition(startloc.x, startloc.y, startloc.z);
        }
        startloc = null;
        Objects.requireNonNull(Atomic.client.player).setYaw(yaw);
        Atomic.client.player.setPitch(pitch);
        yaw = pitch = 0f;
        Atomic.client.gameRenderer.setRenderHand(true);
        Atomic.client.player.getAbilities().flying = flewBefore;
        Atomic.client.player.getAbilities().setFlySpeed(0.05f);
        Atomic.client.player.setVelocity(0, 0, 0);
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        Objects.requireNonNull(Atomic.client.player).setSwimming(false);
        Atomic.client.player.setPose(EntityPose.STANDING);
    }

    @Override public void onHudRender() {

    }
}

