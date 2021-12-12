/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.particles;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.util.Transitions;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class FlowParticle {

    public static final Vec2f          base        = new Vec2f(2f, 2f);
    final               double         pullStrength;
    final               double         speedMtp;
    final               List<PosEntry> previousPos = new ArrayList<>();
    double x;
    Vec2f  velocity;
    float  brightness;
    Color  color;
    double y;

    public FlowParticle(Vec2f initialPos, Vec2f vel, Color color) {
        x = initialPos.x;
        y = initialPos.y;
        this.color = color;
        brightness = 0f;
        velocity = vel;
        pullStrength = Math.random() * 10 + 2;
        speedMtp = (Math.random()) + 1;
    }

    public void move() {
        double nx = x + velocity.x;
        double ny = y + velocity.y;
        int w = Atomic.client.getWindow().getScaledWidth();
        int h = Atomic.client.getWindow().getScaledHeight();
        if (nx > w) {
            nx = 0;
        }
        if (nx < 0) {
            nx = w;
        }
        if (ny > h) {
            ny = 0;
        }
        if (ny < 0) {
            ny = h;
        }
        x = nx;
        y = ny;
        if (nx > w) {
            x = w - 1;
        }
        if (nx < 0) {
            x = 1;
        }
        if (ny > h) {
            y = h - 1;
        }
        if (ny < 0) {
            y = 1;
        }
        brightness += (Math.random() - 0.5) / 8;
        brightness = brightness > 0.5f ? 0.5f : (brightness < 0 ? 0 : brightness);
        double perX = x / Atomic.client.getWindow().getScaledWidth();
        double perY = y / Atomic.client.getWindow().getScaledHeight();
        int r = (int) Math.floor(perX * 255);
        int g = Math.abs(255 - r);
        int b = (int) Math.floor(perY * 255);
        color = new Color(r, g, b);
        double velXO = 0;
        double velYO = 0;
        velocity = velocity.add(new Vec2f((float) velXO / 2, (float) velYO / 2));
        velocity = new Vec2f(MathHelper.clamp(velocity.x, -3, 3), MathHelper.clamp(velocity.y, -3, 3));
        float newVX = (float) Transitions.transition(velocity.x, base.x * speedMtp, pullStrength, 0);
        float newVY = (float) Transitions.transition(velocity.y, base.y * speedMtp, pullStrength, 0);
        velocity = new Vec2f(newVX, newVY);
        previousPos.add(new PosEntry(x, y));
        if (previousPos.size() > 50) {
            previousPos.remove(0);
        }
    }

    record PosEntry(double x, double y) {

    }
}
