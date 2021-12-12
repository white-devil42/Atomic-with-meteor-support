/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.client.ClientConfig;
import me.zeroX150.atomic.feature.module.impl.misc.InfChatLength;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import me.zeroX150.atomic.mixin.game.render.ITextFieldAccessor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@SuppressWarnings("EmptyMethod") @Mixin(ChatScreen.class) public abstract class AChatScreenMixin extends Screen implements FastTickable {

    @Shadow protected TextFieldWidget chatField;
    double yOffset      = 25;
    double targetOffset = 0;

    protected AChatScreenMixin(Text title) {
        super(title);
    }

    @Shadow protected abstract void onChatFieldUpdate(@SuppressWarnings("SameParameterValue") String chatText);

    @Inject(method = "render", at = @At("RETURN")) public void atomic_postRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        int maxLength = ((ITextFieldAccessor) chatField).getMaxLength();
        int cLength = chatField.getText().length();
        boolean showExtra = maxLength != Integer.MAX_VALUE;
        double perUsed = showExtra ? ((double) cLength / maxLength) : 0;
        String v = cLength + (showExtra ? (" / " + maxLength + " " + ((int) Math.round(perUsed * 100)) + "%") : "");
        float w = FontRenderers.mono.getStringWidth(v);
        FontRenderers.mono.drawString(matrices, v, this.width - 2 - w, this.height - 25 + yOffset, Renderer.Util.lerp(new Color(255, 50, 50), new Color(50, 255, 50), perUsed).getRGB());
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V"))
    void atomic_stopFillCall(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        // do nothing
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    void atomic_animateSlideIn(TextFieldWidget textFieldWidget, MatrixStack matrices, int mouseX, int mouseY, float delta) {
        matrices.push();
        matrices.translate(0, yOffset, 0);
        fill(matrices, 2, this.height - 14, this.width - 2, this.height - 2, Atomic.client.options.getTextBackgroundColor(-2147483648));
        textFieldWidget.render(matrices, mouseX, mouseY, delta);
        matrices.pop();
    }

    @Override public void onFastTick() {
        yOffset = Transitions.transition(yOffset, targetOffset, 10, .0001);
    }

    @Inject(method = "onChatFieldUpdate", at = @At("HEAD")) public void atomic_preChatFieldUpdate(String chatText, CallbackInfo ci) {
        chatField.setMaxLength((ModuleRegistry.getByClass(InfChatLength.class).isEnabled() || chatText.startsWith(ClientConfig.chatPrefix.getValue())) ? Integer.MAX_VALUE : 256);
    }

    @Inject(method = "init", at = @At("RETURN")) public void atomic_postInit(CallbackInfo ci) {
        this.onChatFieldUpdate("");
        targetOffset = 0;
    }
}
