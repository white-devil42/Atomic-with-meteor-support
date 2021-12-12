/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.render;

import net.minecraft.util.math.Vec3d;

public class RenderablePosition {

    protected final Vec3d       pos;
    protected final Vec3d       dimensions;
    protected       CustomColor color;

    public RenderablePosition(CustomColor color, Vec3d position, Vec3d dimensions) {
        this.color = color;
        this.pos = position;
        this.dimensions = dimensions;
    }

    public RenderablePosition(CustomColor color, Vec3d position) {
        this(color, position, new Vec3d(1, 1, 1));
    }

    public CustomColor getColor() {
        return color;
    }

    public void setColor(CustomColor color) {
        this.color = color;
    }

    public Vec3d getDimensions() {
        return dimensions;
    }

    public Vec3d getPos() {
        return pos;
    }
}

