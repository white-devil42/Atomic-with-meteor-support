/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.exploit.AntiReducedDebugInfo;
import me.zeroX150.atomic.feature.module.impl.misc.WindowCustomization;
import me.zeroX150.atomic.feature.module.impl.world.FastUse;
import me.zeroX150.atomic.helper.util.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.screen.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(MinecraftClient.class) public class MinecraftClientMixin {

    @Shadow private int itemUseCooldown;

    @Inject(method = "stop", at = @At("HEAD")) public void atomic_preStop(CallbackInfo ci) {
        ConfigManager.saveState();
    }

    @Inject(method = "hasReducedDebugInfo", at = @At("HEAD"), cancellable = true) public void atomic_overwriteReducedDebugInfo(CallbackInfoReturnable<Boolean> cir) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(AntiReducedDebugInfo.class)).isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Redirect(method = "handleInputEvents", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I"))
    public int atomic_replaceItemUseCooldown(MinecraftClient minecraftClient) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(FastUse.class)).isEnabled()) {
            return 0;
        } else {
            return this.itemUseCooldown;
        }
    }

    @Inject(method = "getWindowTitle", at = @At("HEAD"), cancellable = true) public void atomic_replaceWindowTitle(CallbackInfoReturnable<String> cir) {
        if (!Atomic.INSTANCE.initialized) {
            return;
        }
        String v = Objects.requireNonNull(ModuleRegistry.getByClass(WindowCustomization.class)).title.getValue();
        if (Objects.requireNonNull(ModuleRegistry.getByClass(WindowCustomization.class)).isEnabled() && !v.isEmpty()) {
            cir.setReturnValue(v);
        }
    }

    @Inject(method = "setScreen", at = @At("HEAD")) void atomic_preSetScreen(Screen screen, CallbackInfo ci) {
        Atomic.lastScreenChange = System.currentTimeMillis();
    }

    @Inject(method = "<init>", at = @At("TAIL")) void atomic_postInit(RunArgs args, CallbackInfo ci) {
        Atomic.INSTANCE.postWindowInit();
    }
}
