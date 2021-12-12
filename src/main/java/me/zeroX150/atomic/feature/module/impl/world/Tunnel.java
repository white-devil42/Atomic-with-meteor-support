/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.helper.util.Rotations;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;

public class Tunnel extends Module {

    final BooleanValue autotool = (BooleanValue) this.config.create("AutoTool", true).description("Automatically selects the best tool");
    boolean   breakBlock = false;
    Direction startDirection;
    BlockPos  bpToMine   = null;

    public Tunnel() {
        super("Tunnel", "Makes a tunnel for you", ModuleType.WORLD);
    }

    @Override public void tick() {
        Vec3i adder = startDirection.getVector();
        Vec3d next = Objects.requireNonNull(Atomic.client.player).getPos().add(adder.getX(), adder.getY(), adder.getZ());
        BlockPos bp = new BlockPos(next);
        if (Objects.requireNonNull(Atomic.client.world).getBlockState(bp.add(0, 1, 0)).getMaterial().blocksMovement()) {
            bpToMine = bp.add(0, 1, 0);
        } else if (Atomic.client.world.getBlockState(bp).getMaterial().blocksMovement()) {
            bpToMine = bp;
        } else {
            bpToMine = null;
        }
        if (breakBlock && bpToMine != null) {
            if (autotool.getValue()) {
                AutoTool.pick(Atomic.client.world.getBlockState(bpToMine));
            }
            Objects.requireNonNull(Atomic.client.interactionManager).updateBlockBreakingProgress(bpToMine, Direction.DOWN);
        }
        Atomic.client.options.keyForward.setPressed(bpToMine == null);
    }

    @Override public void enable() {
        startDirection = Objects.requireNonNull(Atomic.client.player).getMovementDirection();
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
    }

    @Override public void onFastTick() {
        if (bpToMine != null) {
            Rotations.lookAtPositionSmooth(new Vec3d(bpToMine.getX() + .5, bpToMine.getY() + .5, bpToMine.getZ() + .5), 10);
            if (Atomic.client.crosshairTarget instanceof BlockHitResult bhr) {
                breakBlock = bhr.getBlockPos().equals(bpToMine);
            }
        } else if (startDirection != null) {
            Vec3i e = startDirection.getVector().multiply(3);
            Rotations.lookAtPositionSmooth(Objects.requireNonNull(Atomic.client.player).getEyePos().add(e.getX(), e.getY(), e.getZ()), 5);
        }
    }

    @Override public void onHudRender() {

    }
}

