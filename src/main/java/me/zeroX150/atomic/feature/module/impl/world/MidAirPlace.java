/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class MidAirPlace extends Module {

    public MidAirPlace() {
        super("MidAirPlace", "magic", ModuleType.WORLD);
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
        HitResult hr = Atomic.client.crosshairTarget;
        if (!(hr instanceof BlockHitResult hitresult)) {
            return;
        }
        BlockPos bp = hitresult.getBlockPos();
        BlockState bs = Objects.requireNonNull(Atomic.client.world).getBlockState(bp);
        if (bs.isAir()) {
            Renderer.R3D.renderFilled(new Vec3d(bp.getX(), bp.getY(), bp.getZ()), new Vec3d(1, 1, 1), Utils.getCurrentRGB(), matrices);
            if (Atomic.client.options.keyUse.wasPressed()) {
                Objects.requireNonNull(Atomic.client.interactionManager).interactBlock(Atomic.client.player, Atomic.client.world, Hand.MAIN_HAND, hitresult);
            }
        }
    }

    @Override public void onHudRender() {

    }
}

