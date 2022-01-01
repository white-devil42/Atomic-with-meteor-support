/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import net.minecraft.world.GameMode;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Gamemode extends Command {

    public Gamemode() {
        super("Gamemode", "Switch gamemodes client side", "gamemode", "gm", "gmode", "gamemodespoof", "gmspoof");
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(GameMode.values()).map(GameMode::getName).collect(Collectors.toList()).toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (Atomic.client.interactionManager == null) {
            return;
        }
        if (args.length == 0) {
            message("gamemode pls");
        } else {
            GameMode gm = GameMode.byName(args[0]);
            Atomic.client.interactionManager.setGameMode(gm);
        }
    }
}
