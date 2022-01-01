/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command;

import me.zeroX150.atomic.feature.command.impl.ChatSequence;
import me.zeroX150.atomic.feature.command.impl.Config;
import me.zeroX150.atomic.feature.command.impl.ConfigUtils;
import me.zeroX150.atomic.feature.command.impl.DecodeUUID;
import me.zeroX150.atomic.feature.command.impl.Drop;
import me.zeroX150.atomic.feature.command.impl.Effect;
import me.zeroX150.atomic.feature.command.impl.FakeItem;
import me.zeroX150.atomic.feature.command.impl.ForEach;
import me.zeroX150.atomic.feature.command.impl.Gamemode;
import me.zeroX150.atomic.feature.command.impl.Help;
import me.zeroX150.atomic.feature.command.impl.Hologram;
import me.zeroX150.atomic.feature.command.impl.ImDebug;
import me.zeroX150.atomic.feature.command.impl.InventoryCleaner;
import me.zeroX150.atomic.feature.command.impl.Invsee;
import me.zeroX150.atomic.feature.command.impl.ItemStorage;
import me.zeroX150.atomic.feature.command.impl.Panic;
import me.zeroX150.atomic.feature.command.impl.Plugins;
import me.zeroX150.atomic.feature.command.impl.RageQuit;
import me.zeroX150.atomic.feature.command.impl.Rename;
import me.zeroX150.atomic.feature.command.impl.Say;
import me.zeroX150.atomic.feature.command.impl.SpamConsole;
import me.zeroX150.atomic.feature.command.impl.Taco;
import me.zeroX150.atomic.feature.command.impl.Test;
import me.zeroX150.atomic.feature.command.impl.Toggle;
import me.zeroX150.atomic.feature.command.impl.ViewNbt;
import me.zeroX150.atomic.feature.command.impl.Waypoints;
import me.zeroX150.atomic.feature.gui.screen.AtomicConsoleScreen;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandRegistry {

    private static final List<Command> commands = new ArrayList<>();

    static {
        commands.add(new Test());
        commands.add(new Toggle());
        commands.add(new Config());
        commands.add(new Gamemode());
        commands.add(new Effect());
        commands.add(new Hologram());
        commands.add(new Help());
        commands.add(new ForEach());
        commands.add(new Drop());
        commands.add(new Panic());
        commands.add(new Rename());
        commands.add(new ViewNbt());
        commands.add(new ChatSequence());
        commands.add(new Say());
        commands.add(new InventoryCleaner());
        commands.add(new DecodeUUID());
        commands.add(new Waypoints());
        commands.add(new ItemStorage());
        commands.add(new ConfigUtils());
        commands.add(new Invsee());
        commands.add(new RageQuit());
        commands.add(new SpamConsole());
        commands.add(new Plugins());
        commands.add(new ImDebug());
        commands.add(new FakeItem());
        commands.add(new Taco());
    }

    public static void init() {

    }

    public static List<Command> getCommands() {
        return commands;
    }

    public static void execute(String command) {
        String[] spl = command.split(" +");
        String cmd = spl[0].toLowerCase();
        String[] args = Arrays.copyOfRange(spl, 1, spl.length);
        Command c = CommandRegistry.getByAlias(cmd);
        if (c == null) {
            AtomicConsoleScreen.instance().log("Command \"" + cmd + "\" not found", AtomicConsoleScreen.ERROR);
        } else {
            AtomicConsoleScreen.instance().log("> " + command, AtomicConsoleScreen.CLIENT);
            try {
                c.onExecute(args);
            } catch (Exception e) {
                AtomicConsoleScreen.instance().log("Error while running command " + command, AtomicConsoleScreen.ERROR);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                for (String s : sw.toString().split("\n")) {
                    AtomicConsoleScreen.instance().log(s, AtomicConsoleScreen.BACKGROUND);
                }
            }
        }
    }

    public static Command getByAlias(String n) {
        for (Command command : getCommands()) {
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(n)) {
                    return command;
                }
            }
        }
        return null;
    }
}
