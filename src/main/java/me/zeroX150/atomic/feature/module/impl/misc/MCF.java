/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.misc;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.MouseEvent;
import me.zeroX150.atomic.helper.util.Friends;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

public class MCF extends Module {

    public MCF() {
        super("MCF", "Manage friends by clicking the middle mouse button", ModuleType.MISC);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled() || Atomic.client.currentScreen != null || Atomic.client.player == null || Atomic.client.crosshairTarget == null) {
                return;
            }
            MouseEvent me = ((MouseEvent) event);
            if (me.getButton() == 2 && me.getAction() == MouseEvent.MouseEventType.MOUSE_CLICKED) { // middle click
                HitResult hr = Atomic.client.crosshairTarget;
                if (hr instanceof EntityHitResult ehr) {
                    Entity e = ehr.getEntity();
                    if (e instanceof PlayerEntity player) {
                        toggleFriend(player);
                    }
                }
            }
        });
    }

    void toggleFriend(PlayerEntity pe) {
        if (Friends.isAFriend(pe)) {
            Friends.removeFriend(pe);
        } else {
            Friends.addFriend(pe);
        }
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return Friends.getFriends().size() + "";
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

