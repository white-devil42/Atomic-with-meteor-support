/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.manager.HologramManager;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Objects;

public class Hologram extends Command {

    public Hologram() {
        super("Hologram", "generate a hologram", "hologram", "holo", "hlg");
    }

    @Override public void onExecute(String[] args) {
        if (args.length < 2) {
            Utils.Client.sendMessage("i need options and text pls. example: \".hologram eb your text\". specify option \"h\" to show help");
            return;
        }
        String options = args[0].toLowerCase();
        boolean generateAsBaby = false;
        boolean generateAsEgg = false;
        boolean makeGravity = false;
        boolean makeVisible = false;
        boolean marker = true;
        for (char c : options.toCharArray()) {
            switch (c) {
                case 'e' -> generateAsEgg = true;
                case 'b' -> generateAsBaby = true;
                case 'g' -> makeGravity = true;
                case 'v' -> makeVisible = true;
                case 'm' -> marker = false;
                case 'n' -> {
                }
                default -> {
                    Utils.Client.sendMessage("Unknown option \"" + c + "\". Valid options:");
                    Utils.Client.sendMessage("  N = None (Placeholder)");
                    Utils.Client.sendMessage("  E = Makes a spawn egg instead of an armor stand item");
                    Utils.Client.sendMessage("  B = Makes the hologram entity small");
                    Utils.Client.sendMessage("  G = Makes the hologram have gravity");
                    Utils.Client.sendMessage("  V = Makes the hologram entity visible");
                    Utils.Client.sendMessage("  M = Makes the hologram entity not a marker (enable interactions and hitbox)");
                    return;
                }
            }
        }
        String text = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        Vec3d pos = Objects.requireNonNull(Atomic.client.player).getPos();
        BlockPos displayable = Atomic.client.player.getBlockPos();
        Utils.Client.sendMessage("Armor stand config:");
        Utils.Client.sendMessage("  Text: " + text);
        Utils.Client.sendMessage("  Is baby: " + (generateAsBaby ? "Yes" : "No"));
        Utils.Client.sendMessage("  Is egg: " + (generateAsEgg ? "Yes" : "No"));
        Utils.Client.sendMessage("  Is invisible: " + (!makeVisible ? "Yes" : "No"));
        Utils.Client.sendMessage("  Has gravity: " + (makeGravity ? "Yes" : "No"));
        Utils.Client.sendMessage("  Is marker: " + (marker ? "Yes" : "No"));
        Utils.Client.sendMessage("  Pos: " + displayable.getX() + ", " + displayable.getY() + ", " + displayable.getZ());
        HologramManager.Hologram h = HologramManager.generateDefault(text, pos).isEgg(generateAsEgg).isSmall(generateAsBaby).hasGravity(makeGravity).isVisible(makeVisible).isMarker(marker);
        ItemStack stack = h.generate();
        Utils.Client.sendMessage("Dont forget to open your inventory before placing");
        Atomic.client.player.getInventory().addPickBlock(stack);
    }
}
