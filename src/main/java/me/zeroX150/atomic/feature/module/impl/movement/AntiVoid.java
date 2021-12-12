/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class AntiVoid extends Module {

    final SliderValue distance = (SliderValue) this.config.create("Fall dist", 3, 2, 10, 1).description("how many blocks to fall before tping");
    Vec3d lastOnGround = null;

    public AntiVoid() {
        super("AntiFall", "Prevents you from falling too far", ModuleType.MOVEMENT);
    }

    @Override public void tick() {
        if (Objects.requireNonNull(Atomic.client.world).getBlockState(new BlockPos(Objects.requireNonNull(Atomic.client.player).getPos().subtract(0, 0.4, 0))).getMaterial().blocksMovement()) {
            lastOnGround = Atomic.client.player.getPos();
        }
        if (lastOnGround != null) {
            if (Atomic.client.player.fallDistance > distance.getValue()) {
                Atomic.client.player.updatePosition(Atomic.client.player.getX(), lastOnGround.y, Atomic.client.player.getZ());
                Atomic.client.player.updatePosition(lastOnGround.x, lastOnGround.y, lastOnGround.z);
                Atomic.client.player.setVelocity(0, -1, 0);
                lastOnGround = null;
            }
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        if (Objects.requireNonNull(Atomic.client.player).fallDistance > 2) {
            return Utils.Math.roundToDecimal(Atomic.client.player.fallDistance, 1) + " / " + distance.getValue();
        }
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (lastOnGround != null && Objects.requireNonNull(Atomic.client.player).fallDistance > 2) {
            Renderer.R3D.line(Atomic.client.player.getPos(), lastOnGround, Utils.getCurrentRGB(), matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

