/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.impl.render.ItemByteSize;
import me.zeroX150.atomic.helper.ByteCounter;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored") public class ItemStorage extends Command {

    public static final List<ItemEntry> items = new ArrayList<>();
    static final        File            CONFIG_FILE;

    static {
        CONFIG_FILE = new File(Atomic.client.runDirectory.getAbsolutePath() + "/items.atomic");
        loadFromFile();
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> dumpToFile());
    }

    public ItemStorage() {
        super("ItemStorage", "Stores items for later usage", "istorage", "itemstorage", "items");

    }

    static void loadFromFile() {
        if (!CONFIG_FILE.exists()) {
            Atomic.log(Level.WARN, "Items file doesnt exist");
            return;
        }
        try {
            String contents = FileUtils.readFileToString(CONFIG_FILE, StandardCharsets.UTF_8);
            JsonElement jo = JsonParser.parseString(contents);
            JsonArray je = jo.getAsJsonArray();
            items.clear();
            for (JsonElement jsonElement : je) {
                JsonObject current = jsonElement.getAsJsonObject();
                String name = current.get("name").getAsString();
                String id = current.get("id").getAsString();
                String nbt = current.get("nbt").getAsString();
                Item i = Registry.ITEM.get(new Identifier(id));
                NbtCompound tag = StringNbtReader.parse(nbt);
                ItemEntry entry = new ItemEntry(name, i, tag);
                items.add(entry);
            }
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Couldnt read items file");
        }
    }

    static void dumpToFile() {
        Atomic.log(Level.INFO, "Saving items");
        if (items.isEmpty()) {
            Atomic.log(Level.INFO, "No items to save");
            return;
        }
        if (!CONFIG_FILE.isFile()) {
            CONFIG_FILE.delete();
        }
        JsonArray flist = new JsonArray();
        for (ItemEntry entry : items) {
            JsonObject bruh = new JsonObject();
            bruh.addProperty("name", entry.name());
            bruh.addProperty("id", Registry.ITEM.getId(entry.type).toString());
            bruh.addProperty("nbt", entry.tag.asString());
            flist.add(bruh);
        }
        try {
            FileUtils.writeStringToFile(CONFIG_FILE, "// actually serious this time\n// do not touch this file\n// please\n" + flist, StandardCharsets.UTF_8);
            Atomic.log(Level.INFO, "Saved items");
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Cant save items file!");
        }
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"save", "list", "get", "delete"};
        }
        if (args.length == 2) {
            return switch (args[0].toLowerCase()) {
                case "save" -> new String[]{"(name)"};
                case "get", "delete" -> items.stream().map(ItemEntry::name).collect(Collectors.toList()).toArray(String[]::new);
                default -> new String[0];
            };
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            onExecute(new String[]{"help"});
            return;
        }
        switch (args[0].toLowerCase()) {
            case "save" -> {
                if (args.length < 2) {
                    error("I need a name for the item");
                    return;
                }
                String n = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                if (items.stream().anyMatch(itemEntry -> itemEntry.name.equalsIgnoreCase(n))) {
                    error("An item with that name already exists bruh");
                    return;
                }
                ItemStack hold = Objects.requireNonNull(Atomic.client.player).getInventory().getMainHandStack();
                if (hold.isEmpty()) {
                    error("You arent holding anything");
                    return;
                }
                items.add(new ItemEntry(n, hold.getItem(), hold.getOrCreateNbt()));
                success("Saved item as " + n);
            }
            case "list" -> {
                if (items.isEmpty()) {
                    error("You have no items saved. save one with \"items save\"");
                } else {
                    message("All saved items:");
                    for (ItemEntry item : items) {
                        ByteCounter inst = ByteCounter.instance();
                        inst.reset();
                        boolean error = false;
                        try {
                            item.tag.write(inst);
                        } catch (Exception ignored) {
                            error = true;
                        }
                        long count = inst.getSize();
                        String fmt;
                        if (error) {
                            fmt = "Unknown";
                        } else {
                            fmt = ItemByteSize.humanReadableByteCountBin(count);
                        }
                        message(item.name + ": " + item.type.getName().getString());
                        message0("  Size: " + fmt, Color.GRAY);
                    }
                }
            }
            case "get" -> {
                if (args.length < 2) {
                    error("I need the name of the item to generate");
                    return;
                }
                String n = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                Optional<ItemEntry> item = items.stream().filter(itemEntry -> itemEntry.name.equalsIgnoreCase(n)).findFirst();
                if (item.isEmpty()) {
                    error("Didnt find that item. do list to view them");
                    return;
                }
                ItemStack stack = new ItemStack(item.get().type());
                stack.setNbt(item.get().tag());
                CreativeInventoryActionC2SPacket p = new CreativeInventoryActionC2SPacket(Utils.Inventory.slotIndexToId(Objects.requireNonNull(Atomic.client.player)
                        .getInventory().selectedSlot), stack);
                Objects.requireNonNull(Atomic.client.getNetworkHandler()).sendPacket(p);
                //Atomic.client.player.getInventory().addPickBlock(stack);
                success("Generated item " + n);
            }
            case "delete" -> {
                if (args.length < 2) {
                    error("I need the name of the item to delete");
                    return;
                }
                String n = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                Optional<ItemEntry> item = items.stream().filter(itemEntry -> itemEntry.name.equalsIgnoreCase(n)).findFirst();
                if (item.isEmpty()) {
                    error("Didnt find that item. do list to view them");
                    return;
                }
                items.remove(item.get());
                //Atomic.client.player.getInventory().addPickBlock(stack);
                success("Deleted item " + n);
            }
            default -> {
                message("Commands:");
                message(" - save: Saves the item you're holding");
                message(" - list: Lists all saved items");
                message(" - delete: Deletes an item from the list");
                message(" - get: Gets an item you saved earlier");
            }
        }
    }

    public record ItemEntry(String name, Item type, NbtCompound tag) {

    }
}
