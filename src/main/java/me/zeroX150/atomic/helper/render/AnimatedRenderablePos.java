/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.render;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class AnimatedRenderablePos extends RenderablePosition {

    final long        entry = System.currentTimeMillis();
    final double      duration;
    final CustomColor from;
    final CustomColor to;

    public AnimatedRenderablePos(CustomColor from, CustomColor to, Vec3d position, Vec3d dimensions, int durationMS) {
        super(from, position, dimensions);
        this.duration = durationMS;
        this.from = from;
        this.to = to;
    }

    @Override public CustomColor getColor() {
        long timeExisted = System.currentTimeMillis() - entry;
        double v = timeExisted / duration;
        v = MathHelper.clamp(v, 0, 1);
        v = 1 - v;
        return new CustomColor(Renderer.Util.lerp(from, to, v).getRGB(), super.getColor().isChroma());
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - entry > duration;
    }
}
