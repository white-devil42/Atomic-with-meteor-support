/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.text.Text;

import java.util.Objects;

public class Rename extends Command {

    public Rename() {
        super("Rename", "Renames an item", "rename", "rn", "name");
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            Utils.Client.sendMessage("I need a new name dude");
            Utils.Client.sendMessage("example: rename &c&lthe &afunny");
            return;
        }
        if (Objects.requireNonNull(Atomic.client.player).getInventory().getMainHandStack().isEmpty()) {
            Utils.Client.sendMessage("idk if you're holding anything");
            return;
        }
        Atomic.client.player.getInventory().getMainHandStack().setCustomName(Text.of("§r" + String.join(" ", args).replaceAll("&", "§")));
    }
}
