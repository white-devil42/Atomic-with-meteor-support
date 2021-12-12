/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.helper.util.TypeConverter;
import me.zeroX150.atomic.helper.util.Utils;

public class Config extends Command {

    public Config() {
        super("Config", "le poggers", "config", "conf");
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            Utils.Client.sendMessage("Syntax: .config (module) <key> <value>");
            Utils.Client.sendMessage("For a module or key with spaces, use - as a seperator");
            Utils.Client.sendMessage("Example: \".config block-spammer times-per-tick 11\" to set the \"times per tick\" property to 11");
            return;
        }
        Module target = ModuleRegistry.getByName(args[0].replaceAll("-", " "));
        if (target == null) {
            Utils.Client.sendMessage("Module not found");
            return;
        }
        if (args.length == 1) {
            for (DynamicValue<?> dynamicValue : target.config.getAll()) {
                Utils.Client.sendMessage(dynamicValue.getKey() + " is a " + dynamicValue.getType().getName() + " and has value " + dynamicValue.getValue());
            }
        } else if (args.length == 2) {
            DynamicValue<?> val = target.config.get(args[1].replaceAll("-", " ").toLowerCase());
            if (val == null) {
                Utils.Client.sendMessage("Key not found");
                return;
            }
            Utils.Client.sendMessage(val.getKey() + " = " + val.getValue());
        } else if (args.length == 3) {
            DynamicValue<?> val = target.config.get(args[1].replaceAll("-", " ").toLowerCase());
            if (val == null) {
                Utils.Client.sendMessage("Key not found");
                return;
            }
            Object newValue = TypeConverter.convert(args[2], val.getType());
            if (newValue == null) {
                Utils.Client.sendMessage("Cannot set value. Check if you inputted everything correctly.");
                return;
            }
            val.setValue(newValue);
        }
    }
}
