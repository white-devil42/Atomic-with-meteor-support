/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.entity;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.movement.IgnoreWorldBorder;
import me.zeroX150.atomic.feature.module.impl.movement.Squake;
import me.zeroX150.atomic.feature.module.impl.render.ESP;
import me.zeroX150.atomic.helper.squake.QuakeClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(Entity.class) public abstract class EntityMixin {

    @Shadow public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, List<VoxelShape> collisions) {
        return null;
    }

    @Redirect(
            method = "adjustMovementForCollisions(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/Box;Lnet/minecraft/world/World;Ljava/util/List;)Lnet/minecraft/util/math/Vec3d;",
            at = @At(value = "INVOKE", target = "net/minecraft/world/border/WorldBorder.canCollide(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;)Z"))
    private static boolean real(WorldBorder instance, Entity entity, Box box) {
        return !ModuleRegistry.getByClass(IgnoreWorldBorder.class).isEnabled() && instance.canCollide(entity, box);
    }
    //    @Redirect(method = "adjustMovementForCollisions(Lnet/minecraft/util/math/Vec3d;)Lnet/minecraft/util/math/Vec3d;",
    //            at = @At(value = "NEW", target = "Lnet/minecraft/util/collection/ReusableStream;<init>")) private ReusableStream<VoxelShape> atomic_overwriteWBCollision(Stream<VoxelShape> stream) {
    //        if (Objects.requireNonNull(ModuleRegistry.getByClass(IgnoreWorldBorder.class)).isEnabled()) {
    //            return new ReusableStream<>(Stream.empty());
    //        }
    //        return new ReusableStream<>(stream);
    //    }

    @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true) void atomic_overwriteEspStats(CallbackInfoReturnable<Boolean> cir) {
        // this is a whole different layer of cursed
        ESP e = ModuleRegistry.getByClass(ESP.class);
        if (Objects.requireNonNull(e).isEnabled() && e.outlineMode.getValue().equalsIgnoreCase("shader") && e.shouldRenderEntity((Entity) (Object) this)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "updateVelocity(FLnet/minecraft/util/math/Vec3d;)V", cancellable = true)
    private void atomic_modifyVelocity(float movementSpeed, Vec3d movementInput, CallbackInfo info) {
        if (!ModuleRegistry.getByClass(Squake.class).isEnabled()) {
            return;
        }

        if (QuakeClientPlayer.updateVelocity((Entity) (Object) this, movementInput, movementSpeed)) {
            info.cancel();
        }
    }
}
