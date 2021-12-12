/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.network;

import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.impl.render.oreSim.OreSim;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ClientPlayNetworkHandler.class) public class ClientPlayNetworkHandlerMixin {

    OreSim oresim;

    @Inject(method = "onChunkData", at = @At(value = "TAIL")) private void atomic_onChunkDataReceived(ChunkDataS2CPacket packet, CallbackInfo ci) {
        if (oresim == null) {
            oresim = ModuleRegistry.getByClass(OreSim.class);
        }
        if (Objects.requireNonNull(oresim).isEnabled()) {
            oresim.doMathOnChunk(packet.getX(), packet.getZ());
        }
    }

    @Inject(method = "onGameJoin", at = @At(value = "TAIL")) private void atomic_onGameJoined(GameJoinS2CPacket packet, CallbackInfo ci) {
        reloadOresim();
    }

    @Inject(method = "onPlayerRespawn", at = @At("TAIL")) private void atomic_onPlayerRespawned(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        reloadOresim();
    }

    private void reloadOresim() {
        if (oresim == null) {
            oresim = ModuleRegistry.getByClass(OreSim.class);
        }
        if (Objects.requireNonNull(oresim).isEnabled()) {
            oresim.reload();
        }
    }
}
