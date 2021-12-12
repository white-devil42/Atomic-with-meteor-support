/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Panic extends Command {

    final List<Module> stored = new ArrayList<>();

    public Panic() {
        super("Panic", "oh shit", "panic", "p", "disableall", "dall", "fuck");
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            stored.clear();
            Utils.Client.sendMessage("Disabling all non-render modules");
            Utils.Client.sendMessage("Specify \"hard\" to disable all modules and wipe chat");
            Utils.Client.sendMessage("Specify \"restore\" to restore all enabled modules before the panic");
            for (Module module : ModuleRegistry.getModules()) {
                if (module.getModuleType() != ModuleType.RENDER && module.isEnabled()) {
                    stored.add(module);
                    module.setEnabled(false);
                }
            }
        } else if (args[0].equalsIgnoreCase("hard")) {
            stored.clear();
            for (Module module : ModuleRegistry.getModules()) {
                if (module.isEnabled()) {
                    stored.add(module);
                    module.setEnabled(false);
                }
            }
            Atomic.client.inGameHud.getChatHud().clear(true);
        } else if (args[0].equalsIgnoreCase("restore")) {
            if (stored.size() == 0) {
                Utils.Client.sendMessage("The stored module list is empty");
            } else {
                for (Module module : stored) {
                    if (!module.isEnabled()) {
                        module.setEnabled(true);
                    }
                }
            }
            stored.clear();
        }
    }
}
