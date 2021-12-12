/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.widget;

import me.zeroX150.atomic.helper.font.FontRenderers;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SimpleTextWidget extends ClickableWidget {

    double x, y;
    int     color;
    String  text;
    boolean center = false;

    public SimpleTextWidget(double x, double y, String text, int color) {
        super((int) x, (int) y, FontRenderers.normal.getStringWidth(text), 8, Text.of(""));
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (center) {
            FontRenderers.normal.drawCenteredString(matrices, text, x, y, color);
        } else {
            FontRenderers.normal.drawString(matrices, text, x, y, color);
        }
    }
}
