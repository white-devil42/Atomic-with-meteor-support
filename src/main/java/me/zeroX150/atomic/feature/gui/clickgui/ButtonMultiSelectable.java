/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.helper.font.FontRenderers;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ButtonMultiSelectable extends ButtonWidget {

    final MultiValue parent;

    public ButtonMultiSelectable(int x, int y, int width, MultiValue parent) {
        super(x, y, width, 12, Text.of(parent.getValue()), button -> {
        });
        this.parent = parent;
    }

    @Override public void onPress() {
        parent.cycle();
        this.setMessage(Text.of(parent.getValue()));
    }

    @Override public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        fill(matrices, x, y, x + width, y + height, (this.isHovered() ? Themes.currentActiveTheme.left() : Themes.currentActiveTheme.center()).getRGB());
        FontRenderers.normal.drawCenteredString(matrices, this.getMessage()
                .getString(), x + (width / 2f), y + (height / 2f - FontRenderers.normal.getFontHeight() / 2f), Themes.currentActiveTheme.fontColor().getRGB());
        //DrawableHelper.drawCenteredText(matrices, Atomic.client.textRenderer, this.getMessage(), x + (width / 2), y + (height / 2 - 9 / 2), 0xFFFFFF);
    }
}
