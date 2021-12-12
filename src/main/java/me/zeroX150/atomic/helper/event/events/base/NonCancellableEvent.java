/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.event.events.base;

public class NonCancellableEvent extends Event {

    @Override public boolean isCancelled() {
        return false;
    }

    @Override public void setCancelled(boolean cancelled) {
        throw new IllegalStateException("Event cannot be cancelled.");
    }
}
