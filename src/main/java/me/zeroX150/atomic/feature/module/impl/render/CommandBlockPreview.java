/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.LoreQueryEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.util.Arrays;

public class CommandBlockPreview extends Module {

    public CommandBlockPreview() {
        super("CmdBlockPreview", "Shows you which command a command block runs", ModuleType.RENDER);
        Events.registerEventHandler(EventType.LORE_QUERY, event -> {
            if (!this.isEnabled()) {
                return;
            }
            LoreQueryEvent e = (LoreQueryEvent) event;
            ItemStack s = e.getSource();
            Item i = s.getItem();
            String tagToSearchIn;
            if (i == Items.COMMAND_BLOCK || i == Items.CHAIN_COMMAND_BLOCK || i == Items.REPEATING_COMMAND_BLOCK) {
                tagToSearchIn = "BlockEntityTag";
            } else if (Arrays.stream(EntitySpawnInfo.SPAWN_EGGS).anyMatch(item -> item == i)) {
                tagToSearchIn = "EntityTag";
            } else {
                return;
            }
            try {
                NbtCompound c = s.getOrCreateNbt();
                if (!c.contains(tagToSearchIn, 10)) {
                    throw new Exception();
                }
                NbtCompound blockEntityTag = c.getCompound(tagToSearchIn);
                if (!blockEntityTag.contains("Command", 8)) {
                    throw new Exception();
                }
                String cmd = blockEntityTag.getString("Command");
                if (!cmd.startsWith("/")) {
                    cmd = "/" + cmd;
                }
                e.addClientLore("Runs: " + cmd);
            } catch (Exception ignored) {
            }
        });
    }

    @Override public void tick() {

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

