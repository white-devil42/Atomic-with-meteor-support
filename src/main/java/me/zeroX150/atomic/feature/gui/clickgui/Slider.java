/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class Slider extends ClickableWidget {

    final double      min;
    final double      max;
    final SliderValue v;
    final int         prec;
    double  value;
    boolean dragged = false;

    public Slider(int x, int y, int width, SliderValue conf) {
        super(x - 1, y - 1, width + 1, 12, Text.of(conf.getKey()));
        this.min = conf.getSliderMin();
        this.max = conf.getSliderMax();
        this.v = conf;
        this.value = conf.getValue();
        this.prec = conf.getPrec();
    }

    @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        Renderer.R2D.fill(matrices, Themes.currentActiveTheme.center(), (x + (width * getValue())), y, x + width, y + height);
        Renderer.R2D.fill(matrices, Themes.currentActiveTheme.left(), x, y, (x + (width * getValue())), y + height);
        FontRenderers.getNormal().drawCenteredString(matrices, Utils.Math.roundToDecimal(value, prec) + "", x + width / 2f, y + height / 2f - FontRenderers.getNormal()
                .getFontHeight() / 2f, Themes.currentActiveTheme.fontColor().getRGB());
        //drawCenteredText(matrices, Atomic.client.textRenderer, Client.roundToN(value, prec) + "", x + (width / 2), y + (height / 2 - (9 / 2)), 0xFFFFFF);
    }

    double getValue() {
        return MathHelper.clamp((value - min) / (max - min), 0, 1);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered()) {
            dragged = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragged = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragged) {
            double mxTranslated = mouseX - x;
            double perIn = MathHelper.clamp(mxTranslated / width, 0, 1);
            this.value = Utils.Math.roundToDecimal(perIn * (max - min) + min, prec);
            v.setValue(this.value);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
