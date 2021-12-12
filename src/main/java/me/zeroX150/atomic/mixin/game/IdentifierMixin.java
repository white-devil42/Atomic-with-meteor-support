/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Identifier.class) public class IdentifierMixin {

    @Mutable @Shadow @Final protected String namespace;

    @Mutable @Shadow @Final protected String path;

    @Inject(method = "isNamespaceValid", at = @At("HEAD"), cancellable = true) private static void atomic_overwriteNamespaceValid(String namespace, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "isPathValid", at = @At("HEAD"), cancellable = true) private static void atomic_overwritePathValid(String path, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method = "<init>(Ljava/lang/String;Ljava/lang/String;)V", at = @At("TAIL")) public void atomic_postInit(String namespace, String path, CallbackInfo ci) {
        if (namespace.equals("minecraft") && path.equals("textures/gui/options_background.png")) {
            this.namespace = "atomic";
            this.path = "background.jpg";
        }
    }

    @Inject(method = "getPath", at = @At("HEAD"), cancellable = true) public void atomic_overwritePath(CallbackInfoReturnable<String> cir) {
        if (this.path.startsWith("nomod.")) {
            cir.setReturnValue(this.path.replaceAll("nomod.", ""));
            return;
        }
        if (this.path.equals("textures/gui/options_background.png")) {
            cir.setReturnValue("background.jpg");
        }
    }

    @Inject(method = "getNamespace", at = @At("HEAD"), cancellable = true) public void atomic_overwriteNamespace(CallbackInfoReturnable<String> cir) {
        if (this.path.startsWith("nomod.")) {
            cir.setReturnValue(this.namespace);
            return;
        }
        if (this.path.equals("textures/gui/options_background.png")) {
            cir.setReturnValue("atomic");
        }
    }
}
