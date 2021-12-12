/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.keybind.Keybind;
import me.zeroX150.atomic.helper.util.Rotations;
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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class Scaffold extends Module {

    final SliderValue extend = this.config.create("Extend", 3, 0, 5, 1);

    public Scaffold() {
        super("Scaffold", "scaffold", ModuleType.WORLD);
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
        Atomic.client.options.keySneak.setPressed(false);
    }

    @Override public void onHudRender() {

    }

    @Override public void onFastTick() {
        Vec3d ppos = Objects.requireNonNull(Atomic.client.player).getPos().add(0, -1, 0);
        BlockPos bp = new BlockPos(ppos);
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
            boolean sneaking = new Keybind(Atomic.client.options.keySneak.getDefaultKey().getCode()).isHeld();
            if (sneaking) {
                bp = bp.down();
            }
            // fucking multithreading moment
            int finalSelIndex = selIndex;
            BlockPos finalBp = bp;
            Atomic.client.execute(() -> placeBlockWithSlot(finalSelIndex, finalBp));
            if (extend.getValue() != 0) {
                Vec3d dir1 = Atomic.client.player.getVelocity().multiply(3);
                Vec3d dir = new Vec3d(MathHelper.clamp(dir1.getX(), -1, 1), 0, MathHelper.clamp(dir1.getZ(), -1, 1));
                Vec3d v = ppos;
                for (double i = 0; i < extend.getValue(); i += 0.5) {
                    v = v.add(dir);
                    if (v.distanceTo(Atomic.client.player.getPos()) >= Objects.requireNonNull(Atomic.client.interactionManager).getReachDistance()) {
                        break;
                    }
                    if (sneaking) {
                        v = v.add(0, -1, 0);
                    }
                    BlockPos bp1 = new BlockPos(v);
                    Atomic.client.execute(() -> placeBlockWithSlot(finalSelIndex, bp1));
                }

            }
        }
    }

    void placeBlockWithSlot(int s, BlockPos bp) {
        BlockState st = Objects.requireNonNull(Atomic.client.world).getBlockState(bp);
        if (!st.getMaterial().isReplaceable()) {
            return;
        }
        Vec2f py = Rotations.getPitchYaw(new Vec3d(bp.getX() + .5, bp.getY() + .5, bp.getZ() + .5));
        Rotations.setClientPitch(py.x);
        Rotations.setClientYaw(py.y);
        int c = Objects.requireNonNull(Atomic.client.player).getInventory().selectedSlot;
        Atomic.client.player.getInventory().selectedSlot = s;
        BlockHitResult bhr = new BlockHitResult(new Vec3d(bp.getX(), bp.getY(), bp.getZ()), Direction.DOWN, bp, false);
        Objects.requireNonNull(Atomic.client.interactionManager).interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, bhr);
        Atomic.client.player.getInventory().selectedSlot = c;
    }
}
