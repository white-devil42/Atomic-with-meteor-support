/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Rotations;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Nuker extends Module {

    final List<BlockPos> renders           = new ArrayList<>();
    final SliderValue    range             = (SliderValue) this.config.create("Range", 3, 0, 4, 1).description("The range to nuke by");
    final SliderValue    blocksPerTick     = (SliderValue) this.config.create("Blocks per tick", 1, 1, 20, 0).description("The amount of blocks to destroy per tick");
    final SliderValue    delay             = (SliderValue) this.config.create("Delay", 5, 0, 20, 0).description("The delay before breaking blocks");
    final BooleanValue   ignoreXray        = (BooleanValue) this.config.create("Ignore xray", true).description("Whether or not to ignore xray blocks");
    final MultiValue     mode              = (MultiValue) this.config.create("Mode", "Everything", "Everything", "Torches", "Fire", "Wood", "Grass").description("What to nuke");
    final BooleanValue   autoTool          = (BooleanValue) this.config.create("Auto tool", true).description("Automatically picks the best tool from your inventory, for the block being broken");
    final Block[]        WOOD              = new Block[]{Blocks.ACACIA_LOG, Blocks.BIRCH_LOG, Blocks.DARK_OAK_LOG, Blocks.JUNGLE_LOG, Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.STRIPPED_ACACIA_LOG,
            Blocks.STRIPPED_BIRCH_LOG, Blocks.STRIPPED_DARK_OAK_LOG, Blocks.STRIPPED_JUNGLE_LOG, Blocks.STRIPPED_OAK_LOG, Blocks.STRIPPED_SPRUCE_LOG};
    final MultiValue     mv                = (MultiValue) this.config.create("Sort", "Out -> In", "Out -> In", "In -> Out", "Strength", "Random").description("How to sort");
    final BooleanValue   ignoreUnbreakable = (BooleanValue) this.config.create("Ignore unbreakable", true).description("Ignore survival unbreakable blocks");
    int delayPassed = 0;

    public Nuker() {
        super("Nuker", "breaking block", ModuleType.WORLD);
    }

    boolean isBlockApplicable(Block b) {
        if (mode.getValue().equalsIgnoreCase("everything")) {
            return true;
        } else if (mode.getValue().equalsIgnoreCase("torches")) {
            return b == Blocks.TORCH || b == Blocks.WALL_TORCH || b == Blocks.SOUL_TORCH || b == Blocks.SOUL_WALL_TORCH;
        } else if (mode.getValue().equalsIgnoreCase("fire")) {
            return b == Blocks.FIRE || b == Blocks.SOUL_FIRE;
        } else if (mode.getValue().equalsIgnoreCase("wood")) {
            return Arrays.stream(WOOD).anyMatch(block -> block == b);
        } else if (mode.getValue().equalsIgnoreCase("grass")) {
            return b == Blocks.GRASS || b == Blocks.TALL_GRASS;
        }
        return false;
    }

    @Override public void tick() {
        if (Atomic.client.player == null || Atomic.client.world == null || Atomic.client.interactionManager == null || Atomic.client.getNetworkHandler() == null) {
            return;
        }
        if (delayPassed < delay.getValue()) {
            delayPassed++;
            return;
        }
        delayPassed = 0;
        BlockPos ppos1 = Atomic.client.player.getBlockPos();
        int blocksBroken = 0;
        renders.clear();
        List<BlockPos> toHit = new ArrayList<>();
        for (double y = range.getValue(); y > -range.getValue() - 1; y--) {
            for (double x = -range.getValue(); x < range.getValue() + 1; x++) {
                for (double z = -range.getValue(); z < range.getValue() + 1; z++) {
                    BlockPos vp = new BlockPos(x, y, z);
                    BlockPos np = ppos1.add(vp);
                    Vec3d vp1 = Vec3d.of(np).add(.5, .5, .5);
                    if (vp1.distanceTo(Atomic.client.player.getEyePos()) >= Atomic.client.interactionManager.getReachDistance()) {
                        continue;
                    }
                    toHit.add(np);
                }
            }
        }
        toHit = toHit.stream().sorted(Comparator.comparingDouble(value1 -> {
            Vec3d value = Vec3d.of(value1).add(new Vec3d(.5, .5, .5));
            return switch (mv.getValue().toLowerCase()) {
                case "out -> in":
                    yield value.distanceTo(Atomic.client.player.getPos()) * -1;
                case "in -> out":
                    yield value.distanceTo(Atomic.client.player.getPos());
                case "strength":
                    yield Atomic.client.world.getBlockState(value1).getBlock().getHardness();
                default:
                    yield 1;
            };
        })).collect(Collectors.toList());
        if (mv.getValue().equals("Random")) {
            Collections.shuffle(toHit);
        }
        for (BlockPos np : toHit) {
            if (blocksBroken >= blocksPerTick.getValue()) {
                break;
            }
            BlockState bs = Atomic.client.world.getBlockState(np);
            boolean b = !ignoreXray.getValue() || !XRAY.blocks.contains(bs.getBlock());
            if (!bs.isAir() && bs.getBlock() != Blocks.WATER && bs.getBlock() != Blocks.LAVA && !isUnbreakable(bs.getBlock()) && b && Atomic.client.world.getWorldBorder()
                    .contains(np) && isBlockApplicable(bs.getBlock())) {
                renders.add(np);
                if (autoTool.getValue()) {
                    AutoTool.pick(bs);
                }
                Atomic.client.player.swingHand(Hand.MAIN_HAND);
                if (!Atomic.client.player.getAbilities().creativeMode) {
                    Atomic.client.interactionManager.updateBlockBreakingProgress(np, Direction.DOWN);
                } else {
                    Atomic.client.interactionManager.attackBlock(np, Direction.DOWN);
                }
                Rotations.lookAtV3(new Vec3d(np.getX() + .5, np.getY() + .5, np.getZ() + .5));
                blocksBroken++;
            }
        }
    }

    boolean isUnbreakable(Block b) {
        if (!ignoreUnbreakable.getValue()) {
            return false;
        }
        return b.getHardness() == -1;
    }

    @Override public void enable() {

    }

    @Override public void disable() {
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        for (BlockPos render : renders) {
            Vec3d vp = new Vec3d(render.getX(), render.getY(), render.getZ());
            Renderer.R3D.renderFilled(vp, new Vec3d(1, 1, 1), Renderer.Util.modify(Utils.getCurrentRGB(), -1, -1, -1, 50), matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

