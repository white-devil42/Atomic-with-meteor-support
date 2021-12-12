/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.clickgui;

import me.zeroX150.atomic.feature.module.impl.render.ClickGUI;
import me.zeroX150.atomic.helper.util.Transitions;

import java.awt.Color;

public class Themes {

    public static Themes.Palette currentActiveTheme = Themes.Theme.ATOMIC.getPalette();

    private static Palette getActivePalette() {
        return switch (ClickGUI.theme.getValue()) {
            case "Dark" -> Theme.DARK.getPalette();
            case "Custom" -> new Palette(ClickGUI.cCategories.getColor(), ClickGUI.cModules.getColor(), ClickGUI.cConfig.getColor(), ClickGUI.cFont.getColor(), ClickGUI.cOpacity.getValue());
            default -> Theme.ATOMIC.getPalette();
        };
    }

    public static void tickThemes() {
        Palette newTheme = getActivePalette();
        currentActiveTheme = new Palette(Transitions.transition(currentActiveTheme.left, newTheme.left, 7), Transitions.transition(currentActiveTheme.center, newTheme.center, 7), Transitions.transition(currentActiveTheme.right, newTheme.right, 7), Transitions.transition(currentActiveTheme.fontColor, newTheme.fontColor, 7), Transitions.transition(currentActiveTheme.backgroundOpacity, newTheme.backgroundOpacity, 7));
    }

    public enum Theme {
        ATOMIC(new Palette(new Color(37, 50, 56, 230), new Color(47, 60, 66, 230), new Color(23, 29, 32, 230), Color.WHITE, 1d)),
        DARK(new Palette(new Color(44, 43, 43, 255), new Color(31, 31, 31, 255), new Color(17, 17, 17, 255), Color.WHITE, 1d));
        final Palette p;

        Theme(Palette palette) {
            this.p = palette;
        }

        public Palette getPalette() {
            return p;
        }
    }

    public static record Palette(Color left, Color center, Color right, Color fontColor, double backgroundOpacity) {

    }
}
