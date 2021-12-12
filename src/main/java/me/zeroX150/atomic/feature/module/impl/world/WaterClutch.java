/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class WaterClutch extends Module {

    public WaterClutch() {
        super("WaterClutch", "Places blocks below you when you are on water", ModuleType.WORLD);
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

    @Override public void onFastTick() {
        Vec3d ppos = Objects.requireNonNull(Atomic.client.player).getPos();
        Vec3d np = ppos.subtract(0, MathHelper.clamp(-Atomic.client.player.getVelocity().getY(), 0, 6), 0);
        BlockPos bp = new BlockPos(np);
        BlockState bs = Objects.requireNonNull(Atomic.client.world).getBlockState(bp);
        if (bs.getFluidState().getLevel() == 0) {
            return;
        }
        int selIndex = Atomic.client.player.getInventory().selectedSlot;
        if (!(Atomic.client.player.getInventory().getStack(selIndex).getItem() instanceof BlockItem)) {
            for (int i = 0; i < 9; i++) {
                ItemStack is = Atomic.client.player.getInventory().getStack(i);
                if (is.getItem() == Items.AIR) {
                    continue;
                }
                if (is.getItem() instanceof BlockItem) {
                    selIndex = i;
                    break;
                }
            }
        }
        if (Atomic.client.player.getInventory().getStack(selIndex).getItem() != Items.AIR) {
            // fucking multithreading moment
            int finalSelIndex = selIndex;
            //Rotations.lookAtV3(new Vec3d(bp.getX()+.5,bp.getY()+.5, bp.getZ()+.5));
            Atomic.client.execute(() -> {
                int c = Atomic.client.player.getInventory().selectedSlot;
                Atomic.client.player.getInventory().selectedSlot = finalSelIndex;
                BlockHitResult bhr = new BlockHitResult(np, Direction.DOWN, bp, false);
                Objects.requireNonNull(Atomic.client.interactionManager).interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, bhr);
                Atomic.client.player.getInventory().selectedSlot = c;
            });
        }
    }
}

