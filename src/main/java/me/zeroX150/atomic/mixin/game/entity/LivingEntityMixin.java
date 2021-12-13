/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.entity;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.movement.Jesus;
import me.zeroX150.atomic.feature.module.impl.movement.NoPush;
import me.zeroX150.atomic.helper.manager.AttackManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@SuppressWarnings("EqualsBetweenInconvertibleTypes") @Mixin(value = LivingEntity.class) public class LivingEntityMixin {

    @Inject(method = "canWalkOnFluid", at = @At("HEAD"), cancellable = true) public void atomic_overwriteCanWalkOnFluid(Fluid fluid, CallbackInfoReturnable<Boolean> cir) {
        if (Atomic.client.player == null) {
            return;
        }
        // shut up monkey these are mixins you fucking idiot
        if (this.equals(Atomic.client.player)) {
            if (Objects.requireNonNull(ModuleRegistry.getByClass(Jesus.class)).isEnabled() && Jesus.mode.getValue().equalsIgnoreCase("solid")) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "pushAwayFrom", at = @At("HEAD"), cancellable = true) public void atomic_cancelPush(Entity entity, CallbackInfo ci) {
        if (Atomic.client.player == null) {
            return;
        }
        if (this.equals(Atomic.client.player)) {
            if (Objects.requireNonNull(ModuleRegistry.getByClass(NoPush.class)).isEnabled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onAttacking", at = @At("HEAD")) public void atomic_setLastAttacked(Entity target, CallbackInfo ci) {
        if (this.equals(Atomic.client.player) && target instanceof LivingEntity entity) {
            AttackManager.registerLastAttacked(entity);
        }
    }
    
}
