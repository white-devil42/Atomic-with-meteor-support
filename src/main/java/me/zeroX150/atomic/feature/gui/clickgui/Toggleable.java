/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.helper.font.FontRenderers;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class Toggleable extends ButtonWidget {

    final BooleanValue parent;

    public Toggleable(int x, int y, int width, BooleanValue parent) {
        super(x, y, width, 12, Text.of(parent.getValue() ? "Enabled" : "Disabled"), (buttonWidget) -> {
        });
        this.parent = parent;
    }

    @Override public void onPress() {
        parent.setValue(!parent.getValue());
    }

    @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.setMessage(Text.of(parent.getValue() ? "Enabled" : "Disabled"));
        fill(matrices, x, y, x + width, y + height, this.parent.getValue() ? Themes.currentActiveTheme.left().getRGB() : Themes.currentActiveTheme.center().getRGB());
        FontRenderers.getNormal()
                .drawCenteredString(matrices, this.getMessage().getString(), x + width / 2f, y + height / 2f - FontRenderers.getNormal().getFontHeight() / 2f, parent.getValue() ? 0x99FF99 : 0xFF9999);
        //        DrawableHelper.drawCenteredText(matrices, Atomic.client.textRenderer, this.getMessage(), x + (width / 2), y + (height / 2 - 9 / 2), 0xFFFFFF);
    }
}
