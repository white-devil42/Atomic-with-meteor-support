/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.notifications.NotificationRenderer;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.BetterCrosshair;
import me.zeroX150.atomic.feature.module.impl.render.Hud;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;
import java.util.Objects;

@Mixin(InGameHud.class) public abstract class AInGameHudMixin extends DrawableHelper {

    final Hud c = ModuleRegistry.getByClass(Hud.class);
    double interpolatedSlotValue = 0;
    @Shadow private int scaledHeight;
    @Shadow private int scaledWidth;

    @Inject(method = "render", at = @At("RETURN")) public void atomic_postRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        for (Module module : ModuleRegistry.getModules()) {
            if (module.isEnabled()) {
                module.onHudRender();
            }
        }
        NotificationRenderer.render();
        Utils.TickManager.render();
    }

    @Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void atomic_renderCrosshair(InGameHud inGameHud, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(BetterCrosshair.class)).isEnabled()) {
            BetterCrosshair.render();
        } else {
            inGameHud.drawTexture(matrices, x, y, u, v, width, height);
        }
    }

    @Inject(method = "renderHotbar", at = @At("HEAD")) public void atomic_renderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        if (!Objects.requireNonNull(c).isEnabled()) {
            return;
        }
        if (!c.betterHotbar.getValue()) {
            return;
        }
        int i = this.scaledWidth / 2;
        double slotDiff = Objects.requireNonNull(Atomic.client.player).getInventory().selectedSlot - interpolatedSlotValue;
        slotDiff = Math.abs(slotDiff) < 0.07 ? (slotDiff * c.smoothSelectTransition.getValue()) : slotDiff;
        interpolatedSlotValue += (slotDiff / c.smoothSelectTransition.getValue());
        DrawableHelper.fill(matrices, 0, Atomic.client.getWindow().getScaledHeight() - 23, Atomic.client.getWindow().getScaledWidth(), Atomic.client.getWindow()
                .getScaledHeight(), new Color(28, 28, 28, 170).getRGB());
        DrawableHelper.fill(matrices, (int) (i - 91 - 1 + interpolatedSlotValue * 20), this.scaledHeight - 23, (int) ((i - 91 - 1 + interpolatedSlotValue * 20) + 24), (this.scaledHeight), new Color(43, 43, 43, 150).brighter()
                .brighter().getRGB());
    }

    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
    private void atomic_blockTexture1(InGameHud inGameHud, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        if (Objects.requireNonNull(c).isEnabled() && c.betterHotbar.getValue()) {
            return;
        }
        drawTexture(matrices, x, y, this.getZOffset(), (float) u, (float) v, width, height, 256, 256);
    }

    @Redirect(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 1))
    private void atomic_blockTexture2(InGameHud inGameHud, MatrixStack matrices, int x, int y, int u, int v, int width, int height) {
        if (Objects.requireNonNull(c).isEnabled() && c.betterHotbar.getValue()) {
            return;
        }
        drawTexture(matrices, x, y, this.getZOffset(), (float) u, (float) v, width, height, 256, 256);
    }
}
