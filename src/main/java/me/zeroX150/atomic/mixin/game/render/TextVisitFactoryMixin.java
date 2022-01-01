/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game.render;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.misc.NameProtect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(net.minecraft.client.font.TextVisitFactory.class) public class TextVisitFactoryMixin {

    @ModifyArg(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/font/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
            ordinal = 0), method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"}, index = 0)
    private static String atomic_protectName(String text) {
        try {
            return ModuleRegistry.getByClass(NameProtect.class).protect(text);
        } catch (Exception ignored) {
            return text;
        }
    }
}
