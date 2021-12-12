/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.keybind;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;

import java.util.HashMap;
import java.util.Map;

public class KeybindManager {

    public static final Map<Module, Keybind> keybindMap = new HashMap<>();

    public static void init() {
        for (Module module : ModuleRegistry.getModules()) {
            if (!module.config.get("Keybind").getValue().equals(-1)) {
                keybindMap.put(module, new Keybind(Integer.parseInt(module.config.get("Keybind").getValue() + "")));
            }
        }
    }

    public static void updateSingle(int kc, int action) {
        if (kc == -1) {
            return; // JESSE WE FUCKED UP
        }
        if (action == 1) { // key pressed
            for (Module o : keybindMap.keySet().toArray(new Module[0])) {
                Keybind kb = keybindMap.get(o);
                if (kb.keycode == kc) {
                    Atomic.client.execute(o::toggle);
                }
            }
        }
    }

    public static void reload() {
        keybindMap.clear();
        init();
    }
}
