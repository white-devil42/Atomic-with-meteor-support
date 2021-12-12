/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.render;

import me.zeroX150.atomic.helper.util.Utils;

@SuppressWarnings("unused") public class CustomColor extends java.awt.Color {

    final   int     alpha;
    private boolean isChroma = false;

    public CustomColor(int r, int g, int b, boolean isChroma) {
        this(r, g, b);
        this.isChroma = isChroma;
    }

    public CustomColor(int r, int g, int b) {
        super(r, g, b);
        alpha = 255;
    }

    public CustomColor(int r, int g, int b, int a, boolean isChroma) {
        this(r, g, b, a);
        this.isChroma = isChroma;
    }

    public CustomColor(int r, int g, int b, int a) {
        super(r, g, b, a);
        alpha = a;
    }

    public CustomColor(int rgb, boolean isChroma) {
        this(rgb);
        this.isChroma = isChroma;
    }

    public CustomColor(int rgb) {
        super(rgb);
        alpha = (rgb >> 24) & 0xFF;
    }

    public boolean isChroma() {
        return isChroma;
    }

    public void setChroma(boolean chroma) {
        isChroma = chroma;
    }

    @Override public int getRGB() {
        if (isChroma) {
            java.awt.Color chroma = Utils.getCurrentRGB();
            return new CustomColor(chroma.getRed(), chroma.getGreen(), chroma.getBlue(), alpha).getRGB();
        }
        return super.getRGB();
    }

    @Override public int getAlpha() {
        return alpha;
    }
}
