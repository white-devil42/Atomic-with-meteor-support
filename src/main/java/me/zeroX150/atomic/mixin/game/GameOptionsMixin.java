/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.NarratorMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(GameOptions.class) public class GameOptionsMixin {

    @Shadow public boolean autoJump;

    @Shadow public NarratorMode narrator;

    @Inject(method = "<init>", at = @At("RETURN")) private void atomic_disableAutojumpAndNarrator(MinecraftClient client, File optionsFile, CallbackInfo ci) {
        this.autoJump = false;
        this.narrator = NarratorMode.OFF;
    }
}
