/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.entity;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.combat.ProtectFriends;
import me.zeroX150.atomic.feature.module.impl.world.NoBreakDelay;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.RecipeClickedEvent;
import me.zeroX150.atomic.helper.util.Friends;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Recipe;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayerInteractionManager.class) public class ClientPlayerInteractionManagerMixin {

    @Shadow private int blockBreakingCooldown;

    @Redirect(method = "updateBlockBreakingProgress",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", opcode = Opcodes.GETFIELD, ordinal = 0))
    public int atomic_overwriteCooldown(ClientPlayerInteractionManager clientPlayerInteractionManager) {
        int cd = this.blockBreakingCooldown;
        return Objects.requireNonNull(ModuleRegistry.getByClass(NoBreakDelay.class)).isEnabled() ? 0 : cd;
    }

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true) void atomic_preventEntityAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (Objects.requireNonNull(ModuleRegistry.getByClass(ProtectFriends.class)).isEnabled() && target instanceof PlayerEntity pe && Friends.isAFriend(pe)) {
            ci.cancel();
        }
    }

    @Inject(method = "clickRecipe", at = @At("HEAD"), cancellable = true) void atomic_preClickSlot(int syncId, Recipe<?> recipe, boolean craftAll, CallbackInfo ci) {
        RecipeClickedEvent e = new RecipeClickedEvent(syncId, recipe, craftAll);
        if (Events.fireEvent(EventType.RECIPE_CLICKED, e)) {
            ci.cancel();
        }
    }
}
