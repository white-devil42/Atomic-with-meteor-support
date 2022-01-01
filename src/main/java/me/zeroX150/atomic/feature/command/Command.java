/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command;

import me.zeroX150.atomic.helper.util.Utils;

public abstract class Command extends Utils.Logging {

    private final String   name;
    private final String   description;
    private final String[] aliases;

    public Command(String n, String d, String... a) {
        this.name = n;
        this.description = d;
        this.aliases = a;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    public abstract void onExecute(String[] args);

    public String[] getSuggestions(String fullCommand, String[] args) {
        return new String[0];
    }

}
