/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021 0x150.
 */

package me.zeroX150.atomic.helper.util;

import java.util.ArrayList;
import java.util.List;

public class RGBColorText {

    public static final RGBEntry       NEWLINE = new RGBEntry("\n", 0xFFFFFF);
    final               List<RGBEntry> entries = new ArrayList<>();

    public RGBColorText(RGBColorText text) {
        append(text);
    }

    public RGBColorText(String value) {
        append(value, 0xFFFFFF);
    }

    public RGBColorText(Object value, int color) {
        append(String.valueOf(value), color);
    }

    public RGBColorText append(String text, int color) {
        if (text.equals("\n")) {
            entries.add(NEWLINE);
        } else {
            entries.add(new RGBEntry(text, color));
        }
        return this;
    }

    public RGBColorText append(String text) {
        return append(text, 0xFFFFFF);
    }

    public RGBColorText append(RGBColorText c) {
        entries.addAll(c.getEntries());
        return this;
    }

    public List<RGBEntry> getEntries() {
        return entries;
    }

    public record RGBEntry(String value, int color) {

    }
}
