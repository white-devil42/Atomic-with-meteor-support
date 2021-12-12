/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.world.GameMode;

public class Gamemode extends Command {

    public Gamemode() {
        super("Gamemode", "Switch gamemodes client side", "gamemode", "gm", "gmode", "gamemodespoof", "gmspoof");
    }

    @Override public void onExecute(String[] args) {
        if (Atomic.client.interactionManager == null) {
            return;
        }
        if (args.length == 0) {
            Utils.Client.sendMessage("gamemode pls");
        } else {
            GameMode gm = GameMode.byName(args[0]);
            Atomic.client.interactionManager.setGameMode(gm);
        }
    }
}
