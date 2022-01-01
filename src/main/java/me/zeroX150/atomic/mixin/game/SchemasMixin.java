/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import com.mojang.datafixers.DataFixerBuilder;
import me.zeroX150.atomic.helper.LazyDFB;
import net.minecraft.datafixer.Schemas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// mixin to enable lazy dfu loading
// mod that does this on its own and where I got the shit from
// https://github.com/astei/lazydfu
@Mixin(Schemas.class) public class SchemasMixin {

    @Redirect(method = "create", at = @At(value = "NEW", target = "com/mojang/datafixers/DataFixerBuilder")) private static DataFixerBuilder atomic_replaceDFB(int dataVersion) {
        return new LazyDFB(dataVersion);
    }
}
