/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.FreeLook;
import me.zeroX150.atomic.feature.module.impl.render.NoRender;
import me.zeroX150.atomic.feature.module.impl.render.Zoom;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Rotations;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Debug(export = true) @Mixin(GameRenderer.class) public class GameRendererMixin {

    Module noRender;
    private boolean vb;
    private boolean dis;

    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void atomic_dispatchWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        Renderer.R3D.setLastRenderStack(matrix);
        if (vb) {
            Atomic.client.options.bobView = true;
            vb = false;
        }
        for (Module module : ModuleRegistry.getModules()) {
            if (module.isEnabled()) {
                module.onWorldRender(matrix);
            }
        }
    }

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true) public void atomic_stopHurtAnimation(MatrixStack matrices, float f, CallbackInfo ci) {
        if (noRender == null) {
            noRender = ModuleRegistry.getByClass(NoRender.class);
        }
        if (Objects.requireNonNull(noRender).isEnabled() && NoRender.hurtAnimation.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true) public void atomic_overwriteFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        double zv = Zoom.getZoomValue(cir.getReturnValue());
        cir.setReturnValue(zv);
    }

    @Inject(at = @At("HEAD"), method = "renderWorld") private void atomic_preRenderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        dis = true;
    }

    @Inject(at = @At("HEAD"), method = "bobView", cancellable = true) private void atomic_stopCursorBob(MatrixStack matrices, float f, CallbackInfo ci) {
        if (Atomic.client.options.bobView && dis) {
            vb = true;
            Atomic.client.options.bobView = false;
            dis = false;
            ci.cancel();
        }
    }
    
}
