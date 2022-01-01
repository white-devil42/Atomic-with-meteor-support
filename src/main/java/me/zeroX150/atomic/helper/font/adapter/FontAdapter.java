package me.zeroX150.atomic.helper.font.adapter;

import net.minecraft.client.util.math.MatrixStack;

public interface FontAdapter {
    int drawString(MatrixStack matrices, String text, float x, float y, int color);

    int drawString(MatrixStack matrices, String text, double x, double y, int color);

    int drawCenteredString(MatrixStack matrices, String text, double x, double y, int color);

    float getStringWidth(String text);

    float getFontHeight();

    float getMarginHeight();

    void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow);
}
