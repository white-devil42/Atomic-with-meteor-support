/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.ColorValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.Color;

public class ColorConfig extends ClickableWidget {

    final ColorValue prop;
    final Slider     red;
    final Slider     green;
    final Slider     blue;
    final Toggleable isRGB;
    final int        padding = 2;
    boolean selected = false;

    public ColorConfig(int x, int y, int width, ColorValue orig) {
        super(x, y, width, 12, Text.of(""));
        this.prop = orig;
        red = new Slider(x, y - 12, 99 - 10, new SliderValue("0", orig.getColor().getRed(), 0, 255, 0) {
            @Override public void setValue(Object value) {
                super.setValue(value);
                Color v = orig.getColor();
                orig.setValue(Renderer.Util.modify(v, (int) Math.floor(this.getValue()), -1, -1, -1).getRGB() + (orig.isRGB() ? ";" : ""));
            }
        }) {
            @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                if (orig.isRGB()) {
                    this.value = orig.getColor().getRed();
                }
                super.renderButton(matrices, mouseX, mouseY, delta);
            }
        };
        green = new Slider(x, y - 12 - 12, 99 - 10, new SliderValue("0", orig.getColor().getGreen(), 0, 255, 0) {
            @Override public void setValue(Object value) {
                super.setValue(value);
                Color v = orig.getColor();
                orig.setValue(Renderer.Util.modify(v, -1, (int) Math.floor(this.getValue()), -1, -1).getRGB() + (orig.isRGB() ? ";" : ""));
            }
        }) {
            @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                if (orig.isRGB()) {
                    this.value = orig.getColor().getGreen();
                }
                super.renderButton(matrices, mouseX, mouseY, delta);
            }
        };
        blue = new Slider(x, y - 12 - 12 - 12, 99 - 10, new SliderValue("0", orig.getColor().getBlue(), 0, 255, 0) {
            @Override public void setValue(Object value) {
                super.setValue(value);
                Color v = orig.getColor();
                orig.setValue(Renderer.Util.modify(v, -1, -1, (int) Math.floor(this.getValue()), -1).getRGB() + (orig.isRGB() ? ";" : ""));
            }
        }) {
            @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                if (orig.isRGB()) {
                    this.value = orig.getColor().getBlue();
                }
                super.renderButton(matrices, mouseX, mouseY, delta);
            }
        };
        isRGB = new Toggleable(x, y, 90, new BooleanValue("0", orig.isRGB()) {
            @Override public void setValue(Object value) {
                super.setValue(value);
                orig.setRGB(this.value);
                red.value = orig.getColor().getRed();
                green.value = orig.getColor().getGreen();
                blue.value = orig.getColor().getBlue();
            }
        }) {
            @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                parent.setValue(orig.isRGB());
                super.renderButton(matrices, mouseX, mouseY, delta);
            }
        };
    }

    @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Color c = prop.getColor();
        if (selected) {
            int width = 100;
            int x1 = this.x - width - padding * 2;
            int y1 = this.y - 36 - padding;
            int endX1 = this.x + 7;
            int endY1 = this.y + height + padding;
            if (!isHovered() && !(mouseX >= x1 && mouseY >= y1 && mouseX < endX1 && mouseY < endY1)) {
                this.selected = false;
            }

            Renderer.R2D.fill(matrices, Themes.currentActiveTheme.left(), x - width - padding * 2, y - 36 - padding, x, y + height + padding);
            red.x = x - width + 10 - padding;
            red.y = y - 12 - 12 - 12;
            green.x = x - width + 10 - padding;
            green.y = y - 12 - 12;
            blue.x = x - width + 10 - padding;
            blue.y = y - 12;
            isRGB.x = x - width + 10 - padding;
            isRGB.y = y;
            red.render(matrices, mouseX, mouseY, delta);
            green.render(matrices, mouseX, mouseY, delta);
            blue.render(matrices, mouseX, mouseY, delta);
            isRGB.render(matrices, mouseX, mouseY, delta);
            Renderer.R2D.fill(matrices, Themes.currentActiveTheme.center(), x - width - padding, y - 36, x - width - padding + 10, y + height);
            FontRenderers.getMono().drawCenteredString(matrices, "R", red.x - 4.5f, red.y + (red.getHeight() / 2f - FontRenderers.getMono().getFontHeight() / 2f), 0xFFAAAA);
            FontRenderers.getMono().drawCenteredString(matrices, "G", green.x - 4.5f, green.y + (green.getHeight() / 2f - FontRenderers.getMono().getFontHeight() / 2f), 0xAAFFAA);
            FontRenderers.getMono().drawCenteredString(matrices, "B", blue.x - 4.5f, blue.y + (blue.getHeight() / 2f - FontRenderers.getMono().getFontHeight() / 2f), 0xAAAAFF);
            FontRenderers.getMono()
                    .drawCenteredString(matrices, "C", isRGB.x - 4.5f, isRGB.y + (isRGB.getHeight() / 2f - FontRenderers.getMono().getFontHeight() / 2f), Utils.getCurrentRGB().getRGB());
        }
        Renderer.R2D.fill(matrices, c, x, y, x + width, y + height);
        String rHex = Integer.toHexString(c.getRed());
        String gHex = Integer.toHexString(c.getGreen());
        String bHex = Integer.toHexString(c.getBlue());
        String v = "#" + rHex + gHex + bHex;
        FontRenderers.getMono().drawCenteredString(matrices, v, x + (width / 2f), y + 2, new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue()).getRGB());
        //super.renderButton(matrices, mouseX, mouseY, delta);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered()) {
            this.selected = !selected;
        }
        if (selected) {
            red.mouseClicked(mouseX, mouseY, button);
            green.mouseClicked(mouseX, mouseY, button);
            blue.mouseClicked(mouseX, mouseY, button);
            isRGB.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (selected) {
            red.mouseReleased(mouseX, mouseY, button);
            green.mouseReleased(mouseX, mouseY, button);
            blue.mouseReleased(mouseX, mouseY, button);
            isRGB.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (selected) {
            red.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            green.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            blue.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            isRGB.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }
    //public ColorConfig(ColorValue value)
}
