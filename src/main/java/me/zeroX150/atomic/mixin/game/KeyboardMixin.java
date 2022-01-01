/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.KeyboardEvent;
import me.zeroX150.atomic.helper.keybind.KeybindManager;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Color;

@Mixin(Keyboard.class) public class KeyboardMixin {

    @Shadow private boolean repeatEvents;

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "setRepeatEvents", at = @At("HEAD"), cancellable = true) public void atomic_overwriteRepeatEvents(boolean repeatEvents, CallbackInfo ci) {
        this.repeatEvents = true;
        ci.cancel();
    }

    @Inject(method = "onKey", at = @At("RETURN")) void atomic_postKeyPressed(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (ModuleRegistry.isDebuggerEnabled()) {
            Utils.Logging.message0(String.format("D_KeyEvent window=%s;key=%s;sc=%s;a=%s;mod=%s", window, key, scancode, action, modifiers), Color.GRAY);
            if (ModuleRegistry.getDebugger().disableKeyEvent.getValue()) {
                return;
            }
        }
        if (window == this.client.getWindow()
                .getHandle() && Atomic.client.currentScreen == null && System.currentTimeMillis() - Atomic.lastScreenChange > 10) { // make sure we are in game and the screen has been there for at least 10 ms
            if (Atomic.client.player == null || Atomic.client.world == null) {
                return; // again, make sure we are in game and exist
            }
            KeybindManager.updateSingle(key, action);
            Events.fireEvent(EventType.KEYBOARD, new KeyboardEvent(key, action));
        }
    }
}
