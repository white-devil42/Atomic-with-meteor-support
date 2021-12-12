/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Objects;

public class InventoryCleaner extends Command {

    public InventoryCleaner() {
        super("InventoryCleaner", "Config for the inventory cleaner", "inventorycleaner", "invcleaner", "icleaner");
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            Utils.Client.sendMessage("You gotta give me a subcommand");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                Utils.Client.sendMessage("All items currently in:");
                for (Item item : Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).getItems()) {
                    Utils.Client.sendMessage(" - " + Registry.ITEM.getId(item).getPath());
                }
            }
            case "remove" -> {
                if (args.length < 2) {
                    Utils.Client.sendMessage("I need an item to remove please");
                    return;
                }
                List<Item> i = Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).getItems();
                Item a = Registry.ITEM.get(new Identifier(args[1]));
                if (!i.contains(a)) {
                    Utils.Client.sendMessage("That item isnt in the list");
                    return;
                }
                Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).remove(a);
                Utils.Client.sendMessage("Removed item");
            }
            case "add" -> {
                if (args.length < 2) {
                    Utils.Client.sendMessage("I need an item to add please");
                    return;
                }
                List<Item> i = Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).getItems();
                Item a = Registry.ITEM.get(new Identifier(args[1]));
                if (i.contains(a)) {
                    Utils.Client.sendMessage("Item already in the list.");
                    return;
                }
                if (a == Items.AIR) {
                    Utils.Client.sendMessage("Not sure if i can add that");
                    return;
                }
                Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).add(a);
                Utils.Client.sendMessage("Added item " + args[1]);
            }
        }
    }
}
