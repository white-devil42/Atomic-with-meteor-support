/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.screen;

import net.minecraft.client.gui.screen.option.NarratorOptionsScreen;
import net.minecraft.client.option.Option;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(NarratorOptionsScreen.class) public abstract class NarratorOptionsScreenMixin {

    @Shadow @Final private Option[] options;

    @Mutable @Accessor("options") abstract void setOptions(Option[] options);

    @Inject(method = "init", at = @At("HEAD")) private void atomic_removeAutoJump(CallbackInfo ci) {
        setOptions(Arrays.stream(this.options).filter(option -> option != Option.AUTO_JUMP).filter(option -> option != Option.NARRATOR).toArray(Option[]::new));
    }

}
