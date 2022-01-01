/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.fun;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.util.Rotations;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;

import java.util.Objects;

public class SpinAutism extends Module {

    final SliderValue speed = (SliderValue) this.config.create("Timeout", 5, 0, 100, 0).description("How much to wait between rotations");
    final double      r     = 0;
    int timeout = 0;

    public SpinAutism() {
        super("SpinAutism", "Spins around like a maniac and throws whatever you have", ModuleType.FUN);
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

    @Override public void onFastTick() {
        timeout--; // decrease timeout
        if (timeout > 0) {
            return; // if timeout isn't expired, do nothing
        }
        timeout = (int) Math.floor(speed.getValue()); // timeout expired, set it back to full
        Rotations.setClientPitch((float) ((Math.random() * 60) - 30));
        Rotations.setClientYaw((float) (Math.random() * 360));
        PlayerInteractItemC2SPacket p = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND);
        Objects.requireNonNull(Atomic.client.getNetworkHandler()).sendPacket(p);
        PlayerMoveC2SPacket p1 = new PlayerMoveC2SPacket.LookAndOnGround((float) r, Rotations.getClientPitch(), Objects.requireNonNull(Atomic.client.player).isOnGround());
        Atomic.client.getNetworkHandler().sendPacket(p1);
    }

    @Override public void onHudRender() {

    }
}

