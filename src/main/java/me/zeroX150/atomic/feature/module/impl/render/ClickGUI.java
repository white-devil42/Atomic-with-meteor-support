/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.ColorValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import net.minecraft.client.util.math.MatrixStack;

public class ClickGUI extends Module {

    public static BooleanValue instant;

    public static ColorValue cCategories;
    public static ColorValue cModules;
    public static ColorValue cConfig;
    public static ColorValue cFont;

    public static SliderValue cOpacity;

    public static MultiValue theme;

    public ClickGUI() {
        super("ClickGUI", "Opens the click gui", ModuleType.RENDER);
        theme = (MultiValue) this.config.create("Theme", "Atomic", "Atomic", "Dark", "Custom").description("The theme of the clickgui");
        instant = (BooleanValue) this.config.create("Skip animation", false).description("Disables the animation and shows the clickgui instantly");

        Themes.Palette p = Themes.Theme.DARK.getPalette();
        cCategories = (ColorValue) this.config.create("Categories", p.left(), false).description("The color for categories");
        cModules = (ColorValue) this.config.create("Modules", p.center(), false).description("The color for modules");
        cConfig = (ColorValue) this.config.create("Config", p.right(), false).description("The color for the config");
        cFont = (ColorValue) this.config.create("Font", p.fontColor(), false).description("The text color");
        cOpacity = (SliderValue) this.config.create("Bg opacity", 1, 0, 1, 3).description("The opacity of the background");

        cCategories.showOnlyIfModeIsSet(theme, "custom");
        cModules.showOnlyIfModeIsSet(theme, "custom");
        cConfig.showOnlyIfModeIsSet(theme, "custom");
        cFont.showOnlyIfModeIsSet(theme, "custom");
        cOpacity.showOnlyIfModeIsSet(theme, "custom");

        this.config.createPropGroup("Theme config", cCategories, cModules, cConfig, cFont, cOpacity);

        this.config.get("Keybind").setValue(344);
    }

    @Override public void tick() {
        if (!(Atomic.client.currentScreen instanceof ClickGUIScreen)) {
            Atomic.client.setScreen(ClickGUIScreen.getInstance());
        } else {
            toggle();
        }
    }

    @Override public void enable() {
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}
