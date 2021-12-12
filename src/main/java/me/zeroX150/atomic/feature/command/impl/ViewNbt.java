/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtByteArray;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtLongArray;
import net.minecraft.text.Text;

import java.util.Objects;

public class ViewNbt extends Command {

    int i = 0;

    public ViewNbt() {
        super("ViewNbt", "Views the nbt data of the current item", "viewnbt", "shownbt");
    }

    @Override public void onExecute(String[] args) {
        boolean formatted = false;
        boolean copy = false;
        boolean noColor = false;
        if (args.length == 0) {
            Utils.Client.sendMessage("pro tip: use \"viewnbt help\" to show additional options");
        } else if (args[0].equalsIgnoreCase("help")) {
            Utils.Client.sendMessage("Use flags like these to control what to do with the nbt:");
            Utils.Client.sendMessage("  N - Nothing (to skip the help message)");
            Utils.Client.sendMessage("  F - Formatted (to format the nbt in a nice way)");
            Utils.Client.sendMessage("  C - Copy (to copy the nbt to clipboard)");
            Utils.Client.sendMessage("  W - White (to show uncolored nbt)");
            Utils.Client.sendMessage("Examples: \".viewnbt FC\" to view a formatted view of the nbt and to copy it to clipboard");
            Utils.Client.sendMessage("\".viewnbt CW\" to copy the nbt and show it without colors");
            return;
        } else {
            for (char c : args[0].toLowerCase().toCharArray()) {
                switch (c) {
                    case 'n' -> {
                    }
                    case 'f' -> formatted = true;
                    case 'c' -> copy = true;
                    case 'w' -> noColor = true;
                    default -> {
                        Utils.Client.sendMessage("Unknown option '" + c + "'.");
                        return;
                    }
                }
            }
        }
        if (Objects.requireNonNull(Atomic.client.player).getInventory().getMainHandStack().isEmpty()) {
            Utils.Client.sendMessage("you're not holding anything");
            return;
        }
        ItemStack stack = Atomic.client.player.getInventory().getMainHandStack();
        NbtCompound c = stack.getNbt();
        if (!stack.hasNbt() || c == null) {
            Utils.Client.sendMessage("stack has no data");
            return;
        }
        if (formatted) {
            parse(c, "(root)");
        } else {
            // gotta use .sendMessage because of monkey minecraft api
            if (noColor) {
                Atomic.client.player.sendMessage(Text.of(c.asString()), false);
            } else {
                Atomic.client.player.sendMessage(NbtHelper.toPrettyPrintedText(c), false);
            }
        }
        if (copy) {
            Atomic.client.keyboard.setClipboard(c.asString());
            Utils.Client.sendMessage("Copied nbt!");
        }
    }

    void parse(NbtElement ne, String componentName) {
        if (ne instanceof NbtByteArray || ne instanceof NbtCompound || ne instanceof NbtIntArray || ne instanceof NbtList || ne instanceof NbtLongArray) {
            Utils.Client.sendMessage(" ".repeat(i) + (componentName == null ? "-" : componentName + ":"));
            i += 2;
            if (ne instanceof NbtByteArray array) {
                for (NbtByte nbtByte : array) {
                    parse(nbtByte, null);
                }
            } else if (ne instanceof NbtCompound compound) {
                for (String key : compound.getKeys()) {
                    NbtElement ne1 = compound.get(key);
                    parse(ne1, key);
                }
            } else if (ne instanceof NbtIntArray nbtIntArray) {
                for (NbtInt nbtInt : nbtIntArray) {
                    parse(nbtInt, null);
                }
            } else if (ne instanceof NbtList nbtList) {
                for (NbtElement nbtElement : nbtList) {
                    parse(nbtElement, null);
                }
            } else {
                NbtLongArray nbtLongArray = (NbtLongArray) ne;
                for (NbtLong nbtLong : nbtLongArray) {
                    parse(nbtLong, null);
                }
            }
            i -= 2;
        } else {
            Utils.Client.sendMessage(" ".repeat(i) + (componentName == null ? "-" : componentName + ":") + " " + ne.toString().replaceAll("§", "&"));
        }
    }
}
