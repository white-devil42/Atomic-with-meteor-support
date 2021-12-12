/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DecodeUUID extends Command {

    public DecodeUUID() {
        super("DecodeUUID", "Decodes a UUID to its 4 integers", "decodeuuid", "duuid", "dcuuid");
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            Utils.Client.sendMessage("No uuid given");
            return;
        }
        try {
            UUID u = UUID.fromString(args[0]);
            List<String> decoded = new ArrayList<>();
            for (int i : Utils.Players.decodeUUID(u)) {
                decoded.add(i + "");
            }
            Utils.Client.sendMessage("Decoded UUID split into their bits: " + String.join(", ", decoded));
            Utils.Client.sendMessage("Copied to clipboard");
            Atomic.client.keyboard.setClipboard("[I;" + String.join(",", decoded) + "]");
        } catch (Exception ignored) {
            Utils.Client.sendMessage("Invalid UUID");
        }
    }
}
