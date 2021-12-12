/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.network;

import net.minecraft.client.network.MultiplayerServerListPinger;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiplayerServerListPinger.class) public interface IMultiplayerServerListPingerAccessor {

    @SuppressWarnings("unused") @Invoker("createPlayerCountText") static Text createPlayerCountText(int current, int max) {
        throw new RuntimeException("untransformed mixin!");
    }
}
