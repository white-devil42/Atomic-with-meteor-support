/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.entity;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.movement.LongJump;
import me.zeroX150.atomic.feature.module.impl.movement.Squake;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PlayerNoClipQueryEvent;
import me.zeroX150.atomic.helper.squake.QuakeClientPlayer;
import me.zeroX150.atomic.helper.squake.QuakeClientPlayer.IsJumpingGetter;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class) public abstract class PlayerEntityMixin extends LivingEntity implements IsJumpingGetter {

    boolean velocityHack = false;

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;noClip:Z", opcode = Opcodes.PUTFIELD))
    void atomic_tickNoClip(PlayerEntity playerEntity, boolean value) {
        PlayerNoClipQueryEvent q = new PlayerNoClipQueryEvent(playerEntity);
        Events.fireEvent(EventType.NOCLIP_QUERY, q);
        playerEntity.noClip = q.getNoClip();
    }

    @Inject(at = @At("HEAD"), method = "travel(Lnet/minecraft/util/math/Vec3d;)V", cancellable = true) private void atomic_squakeTravel(Vec3d movementInput, CallbackInfo info) {
        if (!ModuleRegistry.getByClass(Squake.class).isEnabled()) {
            return;
        }

        if (QuakeClientPlayer.travel((PlayerEntity) (Object) this, movementInput)) {
            info.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "tick()V") private void atomic_preTick(CallbackInfo info) {
        QuakeClientPlayer.beforeOnLivingUpdate((PlayerEntity) (Object) this);
    }

    @Inject(at = @At("TAIL"), method = "jump()V") private void atomic_postTick(CallbackInfo info) {
        QuakeClientPlayer.afterJump((PlayerEntity) (Object) this);
    }

    @Override public void updateVelocity(float speed, Vec3d movementInput) {
        if (!ModuleRegistry.getByClass(Squake.class).isEnabled() || !world.isClient) {
            super.updateVelocity(speed, movementInput);
            return;
        }

        if (QuakeClientPlayer.updateVelocity(this, movementInput, speed)) {
            return;
        }
        super.updateVelocity(speed, movementInput);
    }

    @Override public boolean isJumping() {
        return this.jumping;
    }

    @Inject(at = @At("HEAD"), method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z")
    private void atomic_preHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        velocityHack = velocityModified;
    }

    @Inject(at = @At("RETURN"), method = "handleFallDamage(FFLnet/minecraft/entity/damage/DamageSource;)Z")
    private void atomic_postHandleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!world.isClient) {
            velocityModified = velocityHack;
        }
    }

    @Inject(method = "jump", at = @At("RETURN")) void atomic_applyLongJump(CallbackInfo ci) {
        if (!this.equals(Atomic.client.player)) {
            return;
        }
        if (ModuleRegistry.getByClass(LongJump.class).isEnabled()) {
            ModuleRegistry.getByClass(LongJump.class).applyLongJumpVelocity();
        }
    }
}