/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.notifications.Notification;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Rotations;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AutoCone extends Module {

    final SliderValue  rad    = (SliderValue) this.config.create("Radius", 6, 3, 10, 1).description("How wide the cone is");
    final SliderValue  height = (SliderValue) this.config.create("Height", 6, 3, 15, 1).description("How big the cone is");
    final BooleanValue place  = (BooleanValue) this.config.create("Place blocks", false).description("Places the cone automatically, if you're near it");
    Vec3d          startPos = Vec3d.ZERO;
    List<BlockPos> cached   = new ArrayList<>();

    public AutoCone() {
        super("AutoCone", "All hail the cones", ModuleType.WORLD);
    }

    @Override public void tick() {
        cached.clear();
        for (double y = 0; y < height.getValue(); y++) {
            double widener = Math.floor(height.getValue() - y) / height.getValue();
            for (double r = 0; r < 360; r++) {
                double m = Math.toRadians(r);
                double s = Math.sin(m) * rad.getValue() * widener;
                double c = Math.cos(m) * rad.getValue() * widener;
                Vec3d vOff = startPos.add(s, y, c);
                BlockPos bp = new BlockPos(vOff);
                if (!cached.contains(bp)) {
                    cached.add(bp);
                }
            }
        }
        cached = cached.stream().filter(blockPos -> Objects.requireNonNull(Atomic.client.world).getBlockState(blockPos).getMaterial().isReplaceable()).collect(Collectors.toList());
        if (cached.isEmpty()) {
            Notification.create(6000, "AutoCone", "Nowhere to place anymore. Cone built.");
            setEnabled(false);
            return;
        }
        if (!place.getValue()) {
            return;
        }
        List<BlockPos> jesusFuckingChrist = cached.stream()
                .sorted(Comparator.comparingDouble(value -> new Vec3d(value.getX(), value.getY(), value.getZ()).distanceTo(Objects.requireNonNull(Atomic.client.player).getPos())))
                .filter(value -> new Vec3d(value.getX(), value.getY(), value.getZ()).distanceTo(Atomic.client.player.getPos()) < Objects.requireNonNull(Atomic.client.interactionManager)
                        .getReachDistance()).collect(Collectors.toList());
        if (jesusFuckingChrist.isEmpty()) {
            return;
        }
        BlockPos nextPlacement = jesusFuckingChrist.get(0);
        if (Objects.requireNonNull(Atomic.client.player).getInventory().getMainHandStack().isEmpty()) {
            return;
        }
        Vec3d target = new Vec3d(nextPlacement.getX() + .5, nextPlacement.getY() + .5, nextPlacement.getZ() + .5);
        Rotations.lookAtV3(target);
        Atomic.client.player.swingHand(Hand.MAIN_HAND);
        Objects.requireNonNull(Atomic.client.interactionManager)
                .interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, new BlockHitResult(target, Direction.DOWN, nextPlacement, false));
    }

    @Override public void enable() {
        BlockPos pp = Objects.requireNonNull(Atomic.client.player).getBlockPos();
        startPos = new Vec3d(pp.getX() + .5, pp.getY(), pp.getZ());
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        for (BlockPos blockPos : cached.toArray(new BlockPos[0])) {
            Color v = Color.getHSBColor(MathHelper.clamp((float) (new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()).distanceTo(Objects.requireNonNull(Atomic.client.player)
                    .getPos()) / 20f), 0, 1), 0.6f, 1f);
            Renderer.R3D.renderOutline(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()), new Vec3d(1, 1, 1), Renderer.Util.modify(v, -1, -1, -1, 100), matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

