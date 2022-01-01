/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForEach extends Command {

    final ExecutorService runner = Executors.newFixedThreadPool(1);

    public ForEach() {
        super("ForEach", "Do something for each player", "foreach", "for", "fe");
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"(delay in ms)"};
        } else if (args.length == 2) {
            return new String[]{"(message)"};
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length < 2) {
            message("Syntax: foreach (delayMS) (message)");
            message("%s in the message gets replaced with the player name");
            message("Example: .foreach 1000 /msg %s Hi %s! I think you're fucking retarded");
            return;
        }
        int delay = Utils.Math.tryParseInt(args[0], -1);
        if (delay < 0) {
            error("I need a valid int above 0 please");
            return;
        }
        message("Sending the message for every person in the player list, with " + delay + "ms delay");
        for (PlayerListEntry playerListEntry : Objects.requireNonNull(Atomic.client.getNetworkHandler()).getPlayerList()) {
            if (Utils.Players.isPlayerNameValid(playerListEntry.getProfile().getName()) && !playerListEntry.getProfile().getId().equals(Objects.requireNonNull(Atomic.client.player).getUuid())) {
                runner.execute(() -> {
                    try {
                        Atomic.client.player.sendChatMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)).replaceAll("%s", playerListEntry.getProfile().getName()));
                        Thread.sleep(delay);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }
}
