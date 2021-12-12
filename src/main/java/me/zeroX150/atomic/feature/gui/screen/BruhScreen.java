/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BruhScreen extends Screen {

    public BruhScreen() {
        super(Text.of(""));
    }

    @Override public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, new Identifier("atomic", "bruh.png"));
        DrawableHelper.drawTexture(matrices, width / 2 - 32, height / 2 - 32, 0, 0, 64, 64, 64, 64);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
