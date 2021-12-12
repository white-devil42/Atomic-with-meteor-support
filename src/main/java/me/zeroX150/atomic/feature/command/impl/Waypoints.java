/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.gui.screen.WaypointManagerScreen;
import me.zeroX150.atomic.helper.util.Utils;

public class Waypoints extends Command {

    public Waypoints() {
        super("Waypoints", "Manages waypoints", "waypoints", "waypoint", "wp");
    }

    @Override public void onExecute(String[] args) {
        Utils.TickManager.runInNTicks(3, () -> Atomic.client.setScreen(new WaypointManagerScreen()));
    }
}
