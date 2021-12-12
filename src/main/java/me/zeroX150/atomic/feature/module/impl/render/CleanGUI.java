/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import net.minecraft.client.util.math.MatrixStack;

public class CleanGUI extends Module {

    public static MultiValue mode;

    public CleanGUI() {
        super("CleanGUI", "Makes some parts of GUIs cleaner", ModuleType.RENDER);
        mode = (MultiValue) this.config.create("Mode", "Rgb 1", "Rgb 1", "Rgb 2", "Slightly dim", "Transparent").description("How to render the background");
    }

    @Override public void tick() {

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

