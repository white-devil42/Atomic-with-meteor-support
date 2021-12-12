package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Rotations;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Flattener extends Module {
    BooleanValue      makeSame           = (BooleanValue) this.config.create("Make same", false).description("Make the floor the block you're holding, no matter what.");
    BooleanValue      asyncPlaceBreak    = (BooleanValue) this.config.create("Async place / break", true).description("Whether or not to place blocks and break them at the same time");
    BooleanValue      breakSides         = (BooleanValue) this.config.create("Break sides", true).description("Whether or not to clear the area so you can walk on it");
    SliderValue       amountPerTick      = (SliderValue) this.config.create("Amount Per Tick", 3, 1, 20, 0).description("How many actions to do / tick");
    List<RenderEntry> renders            = new ArrayList<>();
    Vec3d             origin             = null;
    double            range              = 8;
    int               prevSlot           = -1;
    boolean           toBreakEmptyBefore = false;

    public Flattener() {
        super("Flattener", "Makes everything around you flat, good for making a floor or base", ModuleType.WORLD);
    }

    @Override public void tick() {
        Vec3d eyep = Atomic.client.player.getEyePos();
        double rangeMid = range / 2d;
        List<BlockPos> toPlace = new ArrayList<>();
        List<BlockPos> toBreak = new ArrayList<>();
        Block inHand = null;
        if (Atomic.client.player.getInventory().getStack(prevSlot).getItem() instanceof BlockItem e) {
            inHand = e.getBlock();
        }
        for (double x = -rangeMid; x < rangeMid + 1; x++) {
            for (double z = -rangeMid; z < rangeMid + 1; z++) {
                Vec3d offset = eyep.add(x, 0, z);
                Vec3d actual = new Vec3d(offset.x + .5, origin.y - .5, offset.z + .5);
                if (actual.distanceTo(eyep) > Atomic.client.interactionManager.getReachDistance()) {
                    continue;
                }
                BlockPos c = new BlockPos(actual);
                BlockState state = Atomic.client.world.getBlockState(c);
                if (state.getMaterial().isReplaceable()) {
                    toPlace.add(c);
                }
                if (makeSame.getValue() && inHand != null && !state.isAir() && state.getBlock() != inHand && state.getBlock().getHardness() > 0) {
                    toBreak.add(c);
                }
                if (breakSides.getValue()) {
                    for (int y = 1; y < 4; y++) {
                        BlockState real = Atomic.client.world.getBlockState(c.add(0, y, 0));
                        if (!real.isAir() && real.getBlock().getHardness() > 0 && real.getBlock() != Blocks.WATER && real.getBlock() != Blocks.LAVA) {
                            toBreak.add(c.add(0, y, 0));
                        }
                    }
                }

            }
        }
        toPlace.sort(Comparator.comparingDouble(value -> Vec3d.of(value).add(.5, .5, .5).distanceTo(eyep)));
        toBreak.sort(Comparator.comparingDouble(value -> Vec3d.of(value).add(.5, .5, .5).distanceTo(eyep)));
        renders.clear();
        if (!toBreak.isEmpty() && toBreakEmptyBefore) {
            prevSlot = Atomic.client.player.getInventory().selectedSlot;
            toBreakEmptyBefore = false;
        }
        int done = 0;
        for (BlockPos blockPos : toBreak) {
            BlockState bs = Atomic.client.world.getBlockState(blockPos);
            if (ModuleRegistry.getByClass(AutoTool.class).isEnabled()) {
                AutoTool.pick(bs);
            }
            Rotations.lookAtV3(Vec3d.of(blockPos).add(.5, .5, .5));
            Atomic.client.interactionManager.updateBlockBreakingProgress(blockPos, Direction.DOWN);
            renders.add(new RenderEntry(blockPos, new Vec3d(1, 1, 1), new Color(31, 232, 148, 70)));
            done++;
            if (done > amountPerTick.getValue()) {
                if (!asyncPlaceBreak.getValue()) {
                    return;
                } else {
                    break;
                }
            }
        }
        done = 0;
        if (!toBreakEmptyBefore) {
            toBreakEmptyBefore = true;
            Atomic.client.player.getInventory().selectedSlot = prevSlot;
        }
        for (BlockPos blockPos : toPlace) {
            if (Atomic.client.player.getInventory().getMainHandStack().getItem() instanceof BlockItem) {
                renders.add(new RenderEntry(blockPos.up(), new Vec3d(1, -0.01, 1), Utils.getCurrentRGB()));
                Vec3d actual = Vec3d.of(blockPos).add(.5, .5, .5);
                Rotations.lookAtV3(actual);
                Atomic.client.interactionManager.interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, new BlockHitResult(actual, Direction.DOWN, blockPos, false));
            }
            done++;
            if (done > amountPerTick.getValue()) {
                break;
            }
        }
    }

    @Override public void enable() {
        origin = Atomic.client.player.getPos();
        prevSlot = Atomic.client.player.getInventory().selectedSlot;
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        for (RenderEntry render : renders) {
            Renderer.R3D.renderFilled(Vec3d.of(render.pos()), render.dimensions(), render.color(), matrices);
        }
    }

    @Override public void onHudRender() {

    }

    record RenderEntry(BlockPos pos, Vec3d dimensions, Color color) {

    }
}

