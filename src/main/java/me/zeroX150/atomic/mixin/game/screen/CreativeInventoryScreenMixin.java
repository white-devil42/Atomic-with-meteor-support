/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.ItemsScreen;
import me.zeroX150.atomic.feature.gui.screen.NbtEditScreen;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(CreativeInventoryScreen.class) public class CreativeInventoryScreenMixin extends Screen {

    protected CreativeInventoryScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN")) void atomic_postInit(CallbackInfo ci) {
        ButtonWidget nbtEditor = new ButtonWidget(5, 5, 100, 20, Text.of("NBT editor"), button -> {
            ItemStack hand = Objects.requireNonNull(Atomic.client.player).getInventory().getMainHandStack();
            if (hand.isEmpty()) {
                Utils.Client.sendMessage("You're not holding anything idiot");
                return;
            }
            Atomic.client.setScreen(new NbtEditScreen(hand.getOrCreateNbt()));
        });
        ButtonWidget itemExploits = new ButtonWidget(5, 30, 100, 20, Text.of("Items"), button -> Atomic.client.setScreen(ItemsScreen.instance()));
        addDrawableChild(nbtEditor);
        addDrawableChild(itemExploits);
    }
}
