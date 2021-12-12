/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.event.events;

import me.zeroX150.atomic.helper.event.events.base.NonCancellableEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class LoreQueryEvent extends NonCancellableEvent {

    final ItemStack  source;
    final List<Text> lore;

    public LoreQueryEvent(ItemStack stack, List<Text> currentLore) {
        this.source = stack;
        this.lore = currentLore;
    }

    public ItemStack getSource() {
        return source;
    }

    public List<Text> getLore() {
        return lore;
    }

    public void addLore(String v) {
        lore.add(Text.of(v));
    }

    public void addClientLore(String v) {
        addLore("[§9A§r] §7" + v + "§r");
    }
}
