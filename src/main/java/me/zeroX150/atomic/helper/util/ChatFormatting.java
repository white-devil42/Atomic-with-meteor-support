/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.util;

import java.util.HashMap;
import java.util.Map;

public enum ChatFormatting {
    DARK_RED("4"), RED("c"), GOLD("6"), YELLOW("e"), DARK_GREEN("2"), GREEN("a"), AQUA("b"), DARK_AQUA("3"), DARK_BLUE("1"), BLUE("9"), LIGHT_PURPLE("d"), DARK_PURPLE("5"), WHITE("f"), GRAY("7"),
    DARK_GRAY("8"), BLACK("0"), OBFUSCATED("k"), BOLD("l"), STRIKETHROUGH("m"), UNDERLINE("n"), ITALIC("o"), RESET("r"), CONTROL("");
    final String code;

    ChatFormatting(String code) {
        this.code = "§" + code;
    }

    public static String format(String in) {
        Map<String, ChatFormatting> real = new HashMap<>();
        for (ChatFormatting value : ChatFormatting.values()) {
            real.put("<" + value.name().toLowerCase() + ">", value);
        }
        String t = in;
        for (String s : real.keySet()) {
            t = t.replaceAll(s, real.get(s).toString());
        }
        return t;
    }

    @Override public String toString() {
        return code;
    }
}
