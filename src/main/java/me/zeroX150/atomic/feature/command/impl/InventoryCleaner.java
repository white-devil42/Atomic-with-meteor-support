/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class InventoryCleaner extends Command {

    public InventoryCleaner() {
        super("InventoryCleaner", "Config for the inventory cleaner", "inventorycleaner", "invcleaner", "icleaner");
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"list", "remove", "add"};
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                return new String[]{"(item name)"};
            }
            // god take the sin off me
            else if (args[0].equalsIgnoreCase("remove")) {
                return ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class).getItems().stream().map(item -> Registry.ITEM.getId(item).toString())
                        .collect(Collectors.toList()).toArray(String[]::new);
            }
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            message("You gotta give me a subcommand");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "list" -> {
                message("All items currently in:");
                for (Item item : Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).getItems()) {
                    message(" - " + Registry.ITEM.getId(item).getPath());
                }
            }
            case "remove" -> {
                if (args.length < 2) {
                    error("I need an item to remove please");
                    return;
                }
                List<Item> i = Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).getItems();
                Item a = Registry.ITEM.get(new Identifier(args[1]));
                if (!i.contains(a)) {
                    error("That item isnt in the list");
                    return;
                }
                Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).remove(a);
                success("Removed item");
            }
            case "add" -> {
                if (args.length < 2) {
                    error("I need an item to add please");
                    return;
                }
                List<Item> i = Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).getItems();
                Item a = Registry.ITEM.get(new Identifier(args[1]));
                if (i.contains(a)) {
                    error("Item already in the list.");
                    return;
                }
                if (a == Items.AIR) {
                    error("Not sure if i can add that");
                    return;
                }
                Objects.requireNonNull(ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.InventoryCleaner.class)).add(a);
                success("Added item " + args[1]);
            }
        }
    }
}
