/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.combat;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.MouseEvent;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.Objects;

public class ArmorSwitch extends Module {

    public ArmorSwitch() {
        super("ArmorSwitch", "Allows you to put armor on with right click even if you have armor on", ModuleType.COMBAT);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled() || Atomic.client.currentScreen != null) {
                return;
            }
            MouseEvent me = (MouseEvent) event;
            if (me.getButton() == 1 && me.getAction() == MouseEvent.MouseEventType.MOUSE_CLICKED) {
                putArmor();
            }
        });
    }

    void putArmor() {
        // yanderedev
        ItemStack selected = Objects.requireNonNull(Atomic.client.player).getInventory().getMainHandStack();
        int slotToPut = -1;
        if (selected.getItem() instanceof ArmorItem ai) {
            switch (ai.getSlotType()) {
                case HEAD -> slotToPut = 5;
                case CHEST -> slotToPut = 6;
                case LEGS -> slotToPut = 7;
                case FEET -> slotToPut = 8;
            }
        } else if (Arrays.stream(new Item[]{Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.PLAYER_HEAD, Items.ZOMBIE_HEAD}).anyMatch(item -> item == selected.getItem())) {
            slotToPut = 5;
        } else if (selected.getItem() == Items.ELYTRA) {
            slotToPut = 6;
        }
        if (slotToPut == -1) {
            return;
        }
        Utils.Inventory.moveStackToOther(Utils.Inventory.slotIndexToId(Atomic.client.player.getInventory().selectedSlot), slotToPut);

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

