/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutoRepeater extends Module {

    final SliderValue    delay         = (SliderValue) this.config.create("New Delay", 1, 1, 4, 0).description("How much delay to set repeaters to");
    final SliderValue    amountPerTick = (SliderValue) this.config.create("Amount per tick", 5, 1, 20, 0).description("How many repeaters to change per tick");
    final List<BlockPos> bps           = new ArrayList<>();

    public AutoRepeater() {
        super("AutoRepeater", "Automatically sets delay of repeaters near you to n", ModuleType.WORLD);
    }

    @Override public void tick() {
        bps.clear();
        scan();
    }

    void scan() {
        int r = (int) Math.ceil(Objects.requireNonNull(Atomic.client.interactionManager).getReachDistance());
        BlockPos player = Objects.requireNonNull(Atomic.client.player).getBlockPos();
        int i = 0;
        for (int y = r; y > -r - 1; y--) {
            for (int x = -r; x < r + 1; x++) {
                for (int z = -r; z < r + 1; z++) {
                    if (i >= amountPerTick.getValue()) {
                        return;
                    }
                    BlockPos offset = new BlockPos(x, y, z);
                    BlockPos actual = player.add(offset);
                    Vec3d c = new Vec3d(actual.getX() + .5, actual.getY() + .1, actual.getZ() + .5);
                    if (c.distanceTo(Atomic.client.player.getPos()) > Atomic.client.interactionManager.getReachDistance()) {
                        continue;
                    }
                    BlockState state = Objects.requireNonNull(Atomic.client.world).getBlockState(actual);
                    if (state.getBlock() == Blocks.REPEATER) {
                        int currentDelay = state.get(RepeaterBlock.DELAY);
                        if (currentDelay != delay.getValue()) {
                            bps.add(actual);
                            BlockHitResult bhr = new BlockHitResult(c, Direction.DOWN, actual, false);
                            Atomic.client.interactionManager.interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, bhr);
                            i++;
                        }
                    }
                }
            }
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        for (BlockPos blockPos : new ArrayList<>(bps)) {
            Vec3d v = new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            Renderer.R3D.renderFilled(v, new Vec3d(1, 0.2, 1), Utils.getCurrentRGB(), matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

