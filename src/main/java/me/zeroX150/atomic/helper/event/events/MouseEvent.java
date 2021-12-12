/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.event.events;

import me.zeroX150.atomic.helper.event.events.base.NonCancellableEvent;

public class MouseEvent extends NonCancellableEvent {

    final int            button;
    final MouseEventType type;

    public MouseEvent(int button, int action) {
        this.button = button;
        type = action == 1 ? MouseEventType.MOUSE_CLICKED : MouseEventType.MOUSE_RELEASED;
    }

    public int getButton() {
        return button;
    }

    public MouseEventType getAction() {
        return type;
    }

    public enum MouseEventType {
        MOUSE_CLICKED, MOUSE_RELEASED
    }
}
