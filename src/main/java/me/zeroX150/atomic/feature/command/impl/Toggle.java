/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;

import java.util.stream.Collectors;

public class Toggle extends Command {

    public Toggle() {
        super("Toggle", "toggles a module", "toggle", "t");
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return ModuleRegistry.getModules().stream().map(Module::getName).collect(Collectors.toList()).toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            error("ima need the module name");
            return;
        }
        Module m = ModuleRegistry.getByName(String.join(" ", args));
        if (m == null) {
            error("Module not found bruh");
        } else {
            m.toggle();
        }
    }
}
