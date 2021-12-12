/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.mixin.game.IClientPlayerInteractionManagerAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class AutoTool extends Module {

    public AutoTool() {
        super("AutoTool", "Automatically selects the best tool for the job", ModuleType.WORLD);
    }

    public static void pick(BlockState state) {
        float best = 1f;
        int index = -1;
        int optAirIndex = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = Objects.requireNonNull(Atomic.client.player).getInventory().getStack(i);
            if (stack.getItem() == Items.AIR) {
                optAirIndex = i;
            }
            float s = stack.getMiningSpeedMultiplier(state);
            if (s > best) {
                index = i;
            }
        }
        if (index != -1) {
            Atomic.client.player.getInventory().selectedSlot = index;
        } else {
            if (optAirIndex != -1) {
                Atomic.client.player.getInventory().selectedSlot = optAirIndex; // to prevent tools from getting damaged by accident, switch to air if we didnt find anything
            }
        }
    }

    @Override public void tick() {
        if (Objects.requireNonNull(Atomic.client.interactionManager).isBreakingBlock() && !Objects.requireNonNull(ModuleRegistry.getByClass(Nuker.class))
                .isEnabled() && !Objects.requireNonNull(ModuleRegistry.getByClass(Tunnel.class)).isEnabled()) {
            BlockPos breaking = ((IClientPlayerInteractionManagerAccessor) Atomic.client.interactionManager).getCurrentBreakingPos();
            BlockState bs = Objects.requireNonNull(Atomic.client.world).getBlockState(breaking);
            pick(bs);
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

