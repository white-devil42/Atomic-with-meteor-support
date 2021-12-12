/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FarmingAura extends Module {

    final SliderValue  max      = this.config.create("Blocks per tick", 3, 1, 20, 0);
    final BooleanValue greifing = (BooleanValue) this.config.create("Greifing mode", false).description("If enabled it will not replant");

    public FarmingAura() {
        super("FarmingAura", "farms", ModuleType.WORLD);
    }

    boolean blockValid(BlockState b, BlockPos bp) {
        if (b.getBlock() instanceof CropBlock e && (e.isMature(b) || greifing.getValue())) {
            return true;
        } else if (b.getBlock() instanceof NetherWartBlock && (b.get(NetherWartBlock.AGE) == 3 || greifing.getValue())) {
            return true;
        } else {
            return b.getBlock() == Blocks.SUGAR_CANE && (!Objects.requireNonNull(Atomic.client.world).getBlockState(bp.down()).getMaterial().blocksMovement() || greifing.getValue());
        }
    }

    @Override public void tick() {
        int r = 4;
        int i = 0;
        double max = this.max.getValue();
        BlockPos ppos = Objects.requireNonNull(Atomic.client.player).getBlockPos();
        Vec3d ppos1 = Atomic.client.player.getPos();
        for (int x = -r; x < r + 1; x++) {
            if (i > max) {
                break;
            }
            for (int y = -r; y < r + 1; y++) {
                if (i > max) {
                    break;
                }
                for (int z = -r; z < r + 1; z++) {
                    if (i > max) {
                        break;
                    }
                    Vec3d off = new Vec3d(x, y, z);
                    Vec3d poff = ppos1.add(off);
                    if (poff.distanceTo(ppos1) >= Objects.requireNonNull(Atomic.client.interactionManager).getReachDistance()) {
                        continue;
                    }
                    BlockPos c = ppos.add(x, y, z);
                    BlockState s = Objects.requireNonNull(Atomic.client.world).getBlockState(c);
                    if (blockValid(s, c)) {
                        i++;
                        Atomic.client.interactionManager.attackBlock(c, Direction.DOWN);
                    }
                }
            }
        }
        if (greifing.getValue()) {
            return;
        }
        Map<Item, Block> blockMap = new HashMap<>();
        blockMap.put(Items.WHEAT_SEEDS, Blocks.FARMLAND);
        blockMap.put(Items.BEETROOT_SEEDS, Blocks.FARMLAND);
        blockMap.put(Items.MELON_SEEDS, Blocks.FARMLAND);
        blockMap.put(Items.PUMPKIN_SEEDS, Blocks.FARMLAND);
        blockMap.put(Items.CARROT, Blocks.FARMLAND);
        blockMap.put(Items.POTATO, Blocks.FARMLAND);
        blockMap.put(Items.NETHER_WART, Blocks.SOUL_SAND);
        blockMap.put(Items.SUGAR_CANE, Blocks.SAND);
        if (blockMap.containsKey(Atomic.client.player.getInventory().getMainHandStack().getItem())) {
            i = 0;
            for (int x = -r; x < r + 1; x++) {
                if (i > max) {
                    break;
                }
                for (int y = -r; y < r + 1; y++) {
                    if (i > max) {
                        break;
                    }
                    for (int z = -r; z < r + 1; z++) {
                        if (i > max) {
                            break;
                        }
                        Vec3d off = new Vec3d(x, y, z);
                        Vec3d poff = ppos1.add(off);
                        if (poff.distanceTo(ppos1) >= Atomic.client.interactionManager.getReachDistance()) {
                            continue;
                        }
                        BlockPos c = ppos.add(x, y, z);
                        BlockState s = Objects.requireNonNull(Atomic.client.world).getBlockState(c);
                        if (s.getBlock() == blockMap.get(Atomic.client.player.getInventory().getMainHandStack().getItem())) {
                            if (!Atomic.client.world.getBlockState(c.up()).isAir()) {
                                continue;
                            }
                            i++;
                            BlockHitResult bhr = new BlockHitResult(poff.add(0, 1, 0), Direction.DOWN, c.add(0, 1, 0), false);
                            Atomic.client.interactionManager.interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, bhr);
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

    }

    @Override public void onHudRender() {

    }
}

