/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module;

public enum ModuleType {
    RENDER("Render"), MOVEMENT("Movement"), MISC("Miscellaneous"), CLIENT("Client"), COMBAT("Combat"), WORLD("World"), EXPLOIT("Exploit"), HIDDEN(""), FUN("Fun");


    final String name;

    ModuleType(String n) {
        this.name = n;
    }

    public String getName() {
        return name;
    }
}
