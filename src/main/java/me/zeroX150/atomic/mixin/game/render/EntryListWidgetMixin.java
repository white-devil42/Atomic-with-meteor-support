/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("ConstantConditions") @Mixin(EntryListWidget.class) public class EntryListWidgetMixin {

    @Shadow protected int     width;
    @Shadow protected int     height;
    @Shadow private   boolean renderBackground;
    @Shadow private   boolean renderHorizontalShadows;

    @Inject(method = "render", at = @At("HEAD")) public void atomic_preRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        this.renderBackground = false;
        this.renderHorizontalShadows = false;
        if (!(((Object) this) instanceof PackListWidget)) {
            Renderer.R2D.renderBackgroundTexture();
        }
    }
}
