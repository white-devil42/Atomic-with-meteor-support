/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.MouseEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class) public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD")) public void atomic_dispatchMouseEvent(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window == Atomic.client.getWindow().getHandle()) {
            Events.fireEvent(EventType.MOUSE_EVENT, new MouseEvent(button, action));
        }
    }
}
