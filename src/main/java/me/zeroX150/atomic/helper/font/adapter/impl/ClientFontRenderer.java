package me.zeroX150.atomic.helper.font.adapter.impl;

import me.zeroX150.atomic.helper.font.adapter.FontAdapter;
import me.zeroX150.atomic.helper.font.render.GlyphPageFontRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class ClientFontRenderer implements FontAdapter {
    final GlyphPageFontRenderer renderer;

    public ClientFontRenderer(GlyphPageFontRenderer gpfr) {
        this.renderer = gpfr;
    }

    @Override public int drawString(MatrixStack matrices, String text, float x, float y, int color) {
        return renderer.drawString(matrices, text, x, y, color);
    }

    @Override public int drawString(MatrixStack matrices, String text, double x, double y, int color) {
        return renderer.drawString(matrices, text, x, y, color);
    }

    @Override public int drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        return renderer.drawCenteredString(matrices, text, x, y, color);
    }

    @Override public float getStringWidth(String text) {
        return renderer.getStringWidth(text);
    }

    @Override public float getFontHeight() {
        return renderer.getFontHeight();
    }

    @Override public float getMarginHeight() {
        return getFontHeight();
    }

    @Override public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
        renderer.drawString(matrices, s, x, y, color, dropShadow);
    }

    public int getSize() {
        return renderer.size;
    }
}
