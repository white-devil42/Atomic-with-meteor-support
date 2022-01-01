/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;
import java.util.stream.Collectors;

public class Invsee extends Command {

    public Invsee() {
        super("Invsee", "Shows you the inv of another player", "invsee", "isee");
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return Atomic.client.world.getPlayers().stream().map(abstractClientPlayerEntity -> abstractClientPlayerEntity.getGameProfile().getName()).collect(Collectors.toList())
                    .toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            message("i need username");
            return;
        }
        PlayerEntity t = null;
        for (Entity player : Objects.requireNonNull(Atomic.client.world).getEntities()) {
            if (player instanceof PlayerEntity player1) {
                if (player1.getGameProfile().getName().equalsIgnoreCase(args[0])) {
                    t = player1;
                    break;
                }
            }
        }
        if (t == null) {
            error("No player found");
            return;
        }
        PlayerEntity finalT = t;
        Utils.TickManager.runOnNextRender(() -> Atomic.client.setScreen(new InventoryScreen(finalT)));
    }
}
