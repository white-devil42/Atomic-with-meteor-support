/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.render;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RenderableBlockPos extends RenderablePosition {

    public RenderableBlockPos(CustomColor color, BlockPos position) {
        super(color, new Vec3d(position.getX() + .5, position.getY() + .5, position.getZ() + .5));
    }
}
