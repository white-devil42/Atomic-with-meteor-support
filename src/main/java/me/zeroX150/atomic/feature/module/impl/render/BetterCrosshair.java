/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.ColorValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.Color;

public class BetterCrosshair extends Module {

    static SliderValue  inset;
    static SliderValue  length;
    static ColorValue   color;
    static BooleanValue gradient;
    static ColorValue   gradientEnd;
    static BooleanValue transparentEnd, transparentStart;

    public BetterCrosshair() {
        super("BetterCrosshair", "a better crosshair", ModuleType.RENDER);
        inset = (SliderValue) this.config.create("Offset", 2, 0, 20, 1).description("How much to offset the segments");
        length = (SliderValue) this.config.create("Length", 10, 0, 50, 1).description("How long the segments are");
        color = (ColorValue) this.config.create("Color", Color.RED, false).description("Which color the segments are");
        gradient = (BooleanValue) this.config.create("Gradient", false).description("Whether or not to render the lines as gradient");
        gradientEnd = (ColorValue) this.config.create("Gradient color", Color.GREEN, false).description("The end color of the gradient");
        gradientEnd.showOnlyIf(() -> gradient.getValue());
        transparentEnd = (BooleanValue) this.config.create("Transparent end", true).description("Whether or not to make the end of the gradient transparent");
        transparentStart = (BooleanValue) this.config.create("Transparent start", false).description("Whether or not to make the start of the gradient transparent");
        transparentStart.showOnlyIf(() -> gradient.getValue());
        transparentEnd.showOnlyIf(() -> gradient.getValue());
    }

    public static void render() {
        int x = Atomic.client.getWindow().getScaledWidth() / 2;
        int y = Atomic.client.getWindow().getScaledHeight() / 2;
        // it didnt work otherwise please fucking forgive me
        int i = (int) Math.floor(inset.getValue());
        int l = (int) Math.floor(length.getValue());
        if (gradient.getValue()) {
            Color start = Renderer.Util.modify(color.getColor(), -1, -1, -1, transparentStart.getValue() ? 0 : -1);
            Color end = Renderer.Util.modify(gradientEnd.getColor(), -1, -1, -1, transparentEnd.getValue() ? 0 : -1);
            Renderer.R2D.gradientLineScreen(start, end, x + i, y, x + i + l, y);
            Renderer.R2D.gradientLineScreen(start, end, x, y + i, x, y + i + l);
            Renderer.R2D.gradientLineScreen(start, end, x - i, y, x - i - l, y);
            Renderer.R2D.gradientLineScreen(start, end, x, y - i, x, y - i - l);
        } else {
            Renderer.R2D.lineScreenD(color.getColor(), x + i - .5, y, x + i + l - .5, y);
            Renderer.R2D.lineScreenD(color.getColor(), x, y + i, x, y + i + l);
            Renderer.R2D.lineScreenD(color.getColor(), x - i, y, x - i - l, y);
            Renderer.R2D.lineScreenD(color.getColor(), x, y - i + .5, x, y - i - l + .5);
        }
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

    }

    @Override public void onHudRender() {

    }
}

