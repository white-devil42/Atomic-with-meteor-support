/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.gui.clickgui.ClickGUIScreen;
import me.zeroX150.atomic.helper.util.Utils;

public class Test extends Command {

    public Test() {
        super("Test", "amogus sus", "among", "sus", "test");
    }

    public static void real() {
        Atomic.client.setScreen(new ClickGUIScreen());
    }

    @Override public void onExecute(String[] args) {
        Utils.TickManager.runInNTicks(10, Test::real);
    }
}
