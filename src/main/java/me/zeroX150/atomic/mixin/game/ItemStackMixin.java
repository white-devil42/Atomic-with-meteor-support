/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.LoreQueryEvent;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class) public class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"), cancellable = true) void atomic_dispatchTooltipRender(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> cval = cir.getReturnValue();
        LoreQueryEvent event = new LoreQueryEvent((ItemStack) (Object) this, cval);
        Events.fireEvent(EventType.LORE_QUERY, event);
        cir.setReturnValue(event.getLore());
    }
}
