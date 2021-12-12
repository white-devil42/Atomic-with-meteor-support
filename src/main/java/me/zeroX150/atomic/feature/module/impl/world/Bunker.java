/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Rotations;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.Objects;

public class Bunker extends Module {

    Vec3d  current;
    double xP = 0;
    double yP = 0;

    public Bunker() {
        super("Bunker", "makes a bunker", ModuleType.WORLD);
    }

    @Override public void tick() {
        boolean isAir = true;
        BlockPos cp1 = null;
        while (isAir) {
            if (incr()) {
                setEnabled(false);
                break;
            }
            double r = Math.toRadians(Math.abs(180 - yP));
            double r1 = Math.toRadians(xP);
            double s = Math.sin(r) * 4;
            double c = Math.cos(r) * 4;
            double s1 = Math.sin(r1) * s;
            double c1 = Math.cos(r1) * s;
            BlockPos p = Objects.requireNonNull(Atomic.client.player).getBlockPos();
            BlockPos cp2 = p.add(s1 + 1, c, c1 + 1);
            BlockState bs = Objects.requireNonNull(Atomic.client.world).getBlockState(cp2);
            isAir = !bs.getMaterial().isReplaceable();
            cp1 = cp2;
        }
        if (cp1 == null) {
            return;
        }
        current = new Vec3d(cp1.getX(), cp1.getY(), cp1.getZ());
        BlockHitResult bhr = new BlockHitResult(current, Direction.DOWN, cp1, false);
        Objects.requireNonNull(Atomic.client.interactionManager).interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, bhr);
        Rotations.lookAtV3(current.add(0.5, 0.5, 0.5));
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (current != null) {
            Renderer.R3D.renderFilled(current, new Vec3d(1, 1, 1), Color.RED, matrices);
        }
    }

    boolean incr() {
        xP += 2;
        if (xP > 360) {
            xP %= 360;
            yP += 2;
        }
        if (yP > 180) {
            yP = 0;
            xP = 0;
            return true;
            //setEnabled(false);
        }
        return false;
    }

    @Override public void onHudRender() {

    }
}

