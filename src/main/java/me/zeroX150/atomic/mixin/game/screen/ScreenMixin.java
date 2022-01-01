/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.MessageScreen;
import me.zeroX150.atomic.feature.gui.screen.NonClearingInit;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.CleanGUI;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(Screen.class) public abstract class ScreenMixin extends DrawableHelper {

    @Shadow public              int             width;
    @Shadow public              int             height;
    @Shadow @Nullable protected MinecraftClient client;

    @Shadow protected abstract void clearChildren();

    @Redirect(method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;fillGradient(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"))
    public void atomic_replaceBackground(Screen screen, MatrixStack matrices, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        if (ModuleRegistry.getByClass(CleanGUI.class).isEnabled()) {
            int i = CleanGUI.mode.getIndex();
            switch (i) {
                case 0 -> {
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.enableBlend();
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                    Matrix4f matrices4 = matrices.peek().getPositionMatrix();
                    float offset = (float) ((System.currentTimeMillis() % 3000) / 3000d);
                    float hsv2p = 0.25f + offset;
                    float hsv3p = 0.5f + offset;
                    float hsv4p = 0.75f + offset;
                    Color hsv1 = Color.getHSBColor(offset % 1, 0.6f, 1f);
                    Color hsv2 = Color.getHSBColor(hsv2p % 1, 0.6f, 1f);
                    Color hsv3 = Color.getHSBColor(hsv3p % 1, 0.6f, 1f);
                    Color hsv4 = Color.getHSBColor(hsv4p % 1, 0.6f, 1f);
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                    bufferBuilder.vertex(matrices4, startX, startY, 0).color(hsv1.getRed(), hsv1.getGreen(), hsv1.getBlue(), 60).next();
                    bufferBuilder.vertex(matrices4, startX, endY, 0).color(hsv2.getRed(), hsv2.getGreen(), hsv2.getBlue(), 60).next();
                    bufferBuilder.vertex(matrices4, endX, endY, 0).color(hsv3.getRed(), hsv3.getGreen(), hsv3.getBlue(), 60).next();
                    bufferBuilder.vertex(matrices4, endX, startY, 0).color(hsv4.getRed(), hsv4.getGreen(), hsv4.getBlue(), 60).next();
                    bufferBuilder.end();
                    BufferRenderer.draw(bufferBuilder);
                    RenderSystem.disableBlend();
                }
                case 1 -> {
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.enableBlend();
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                    BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                    Matrix4f matrices4 = matrices.peek().getPositionMatrix();
                    float offset = (float) ((System.currentTimeMillis() % 3000) / 3000d);
                    float hsv2p = 0.5f + offset;
                    Color hsv1 = Color.getHSBColor(offset % 1, 0.6f, 1f);
                    Color hsv2 = Color.getHSBColor(hsv2p % 1, 0.6f, 1f);
                    bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                    bufferBuilder.vertex(matrices4, startX, startY, 0).color(hsv1.getRed(), hsv1.getGreen(), hsv1.getBlue(), 60).next();
                    bufferBuilder.vertex(matrices4, startX, endY, 0).color(hsv2.getRed(), hsv2.getGreen(), hsv2.getBlue(), 60).next();
                    bufferBuilder.vertex(matrices4, endX, endY, 0).color(hsv2.getRed(), hsv2.getGreen(), hsv2.getBlue(), 60).next();
                    bufferBuilder.vertex(matrices4, endX, startY, 0).color(hsv1.getRed(), hsv1.getGreen(), hsv1.getBlue(), 60).next();
                    bufferBuilder.end();
                    BufferRenderer.draw(bufferBuilder);
                    RenderSystem.disableBlend();
                }
                case 2 -> DrawableHelper.fill(matrices, startX, startY, endX, endY, new Color(0, 0, 0, 60).getRGB());
            }
        } else {
            this.fillGradient(matrices, startX, startY, endX, endY, colorStart, colorEnd);
        }
    }

    @Inject(method = "renderBackgroundTexture", at = @At("HEAD"), cancellable = true) public void atomic_renderBackgroundTexture(int vOffset, CallbackInfo ci) {
        ci.cancel();
        Renderer.R2D.renderBackgroundTexture();
    }

    @Redirect(method = "handleTextClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;sendMessage(Ljava/lang/String;Z)V"))
    public void atomic_warnUserAboutClickedText(Screen screen, String message, boolean toHud) {
        MessageScreen confirmScreen = new MessageScreen(screen, "Careful!", "Clicking that would send this message:\n" + message + "\nDo you want to do that?", t -> {
            if (t) {
                screen.sendMessage(message, toHud);
            } else {
                Utils.Logging.message("Blocked sending of message \"" + message + "\"");
            }
        }, MessageScreen.ScreenType.YESNO);
        Utils.TickManager.runInNTicks(0, () -> Atomic.client.setScreen(confirmScreen));
    }

    @Redirect(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/screen/Screen.clearChildren()V"))
    void atomic_preventChildrenClear(Screen instance) {
        if (!(instance instanceof NonClearingInit)) {
            this.clearChildren();
        }
    }
}
