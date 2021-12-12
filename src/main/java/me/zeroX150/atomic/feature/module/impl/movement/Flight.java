/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Random;

public class Flight extends Module {

    final MultiValue   mode            = (MultiValue) this.config.create("Mode", "Static", "Vanilla", "Static", "3D", "Jetpack").description("Which type of fly to do");
    final BooleanValue bypassVanillaAc = (BooleanValue) this.config.create("Vanilla AC bypass", true)
            .description("Whether or not to bypass the vanilla Anticheat (Flying is not enabled on this server)");
    final SliderValue  speed           = (SliderValue) this.config.create("Speed", 2, 0.1, 10, 1).description("The speed of the flight (does not affect vanilla)");

    int     bypassTimer = 0;
    boolean flewBefore  = false;

    public Flight() {
        super("Flight", "i think this explains itself", ModuleType.MOVEMENT);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof ClientCommandC2SPacket p && p.getMode() == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                event.setCancelled(true);
            }
        });
    }

    @Override public void tick() {
        if (Atomic.client.player == null || Atomic.client.world == null || Atomic.client.getNetworkHandler() == null) {
            return;
        }
        double speed = this.speed.getValue();
        if (bypassVanillaAc.getValue()) {
            bypassTimer++;
            if (bypassTimer > 10) {
                bypassTimer = 0;
                Vec3d p = Atomic.client.player.getPos();
                Atomic.client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y - 0.2, p.z, false));
                Atomic.client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y + 0.2, p.z, false));
            }
        }
        switch (mode.getValue().toLowerCase()) {
            case "vanilla":
                Atomic.client.player.getAbilities().setFlySpeed((float) (this.speed.getValue() + 0f) / 20f);
                Atomic.client.player.getAbilities().flying = true;
                break;
            case "static":
                GameOptions go = Atomic.client.options;
                float y = Atomic.client.player.getYaw();
                int mx = 0, my = 0, mz = 0;

                if (go.keyJump.isPressed()) {
                    my++;
                }
                if (go.keyBack.isPressed()) {
                    mz++;
                }
                if (go.keyLeft.isPressed()) {
                    mx--;
                }
                if (go.keyRight.isPressed()) {
                    mx++;
                }
                if (go.keySneak.isPressed()) {
                    my--;
                }
                if (go.keyForward.isPressed()) {
                    mz--;
                }
                double ts = speed / 2;
                double s = Math.sin(Math.toRadians(y));
                double c = Math.cos(Math.toRadians(y));
                double nx = ts * mz * s;
                double nz = ts * mz * -c;
                double ny = ts * my;
                nx += ts * mx * -c;
                nz += ts * mx * -s;
                Vec3d nv3 = new Vec3d(nx, ny, nz);
                Atomic.client.player.setVelocity(nv3);
                break;
            case "jetpack":
                if (Atomic.client.options.keyJump.isPressed()) {
                    assert Atomic.client.player != null;
                    Atomic.client.player.addVelocity(0, speed / 30, 0);
                    Vec3d vp = Atomic.client.player.getPos();
                    for (int i = 0; i < 10; i++) {
                        Random r = new Random();
                        Atomic.client.world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, vp.x, vp.y, vp.z, (r.nextDouble() * 0.25) - .125, (r.nextDouble() * 0.25) - .125, (r.nextDouble() * 0.25) - .125);
                    }
                }
                break;
            case "3d":
                Atomic.client.player.setVelocity(Atomic.client.player.getRotationVector().multiply(speed)
                        .multiply(Atomic.client.player.input.pressingForward ? 1 : (Atomic.client.player.input.pressingBack ? -1 : 0)));
                break;
        }
    }

    @Override public void enable() {
        bypassTimer = 0;
        flewBefore = Objects.requireNonNull(Atomic.client.player).getAbilities().flying;
        Atomic.client.player.setOnGround(false);
        Atomic.client.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(Atomic.client.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }

    @Override public void disable() {
        Objects.requireNonNull(Atomic.client.player).getAbilities().flying = flewBefore;
        Atomic.client.player.getAbilities().setFlySpeed(0.05f);
    }

    @Override public String getContext() {
        return mode.getValue();
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}
