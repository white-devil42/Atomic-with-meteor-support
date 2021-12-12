/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.NoRender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameOverlayRenderer.class) public class InGameOverlayRendererMixin {

    private static final Module noRender = ModuleRegistry.getByClass(NoRender.class);

    @Inject(method = "renderUnderwaterOverlay", at = @At("HEAD"), cancellable = true) private static void atomic_cancelWaterOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (Objects.requireNonNull(noRender).isEnabled() && NoRender.waterOverlay.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true) private static void atomic_cancelFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (Objects.requireNonNull(noRender).isEnabled() && NoRender.fire.getValue()) {
            ci.cancel();
        }
    }
}
