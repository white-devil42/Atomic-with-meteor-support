/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.misc;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InventoryCleaner extends Module {

    final DynamicValue<String> saved = this.config.create("_", "");

    public InventoryCleaner() {
        super("InvCleaner", "Cleans inventory from useless stuff", ModuleType.MISC);
        saved.showOnlyIf(() -> false);
    }

    @Override public void tick() {
        for (int i = 0; i < 36; i++) {
            ItemStack s = Objects.requireNonNull(Atomic.client.player).getInventory().getStack(i);
            if (getItems().stream().anyMatch(item -> item == s.getItem())) {
                Utils.Inventory.drop(i);
                break;
            }
        }
    }

    public List<Item> getItems() {
        List<Item> r = new ArrayList<>();
        String s = saved.getValue();
        String[] v = s.split(";");
        for (String s1 : v) {
            Item a = Registry.ITEM.get(new Identifier(s1));
            if (a == Items.AIR) {
                continue;
            }
            r.add(a);
        }
        return r;
    }

    public void add(Item i) {
        List<String> a = new ArrayList<>(List.of(saved.getValue().split(";")));
        Identifier bruh = Registry.ITEM.getId(i);
        a.add(bruh.getNamespace() + ":" + bruh.getPath());
        saved.setValue(String.join(";", a));
    }

    public void remove(Item i) {
        List<String> a = new ArrayList<>(List.of(saved.getValue().split(";")));
        Identifier bruh = Registry.ITEM.getId(i);
        String con = bruh.getNamespace() + ":" + bruh.getPath();
        a.remove(con);
        saved.setValue(String.join(";", a));
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

