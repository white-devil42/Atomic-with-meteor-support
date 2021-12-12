/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.util;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.base.NonCancellableEvent;
import me.zeroX150.atomic.helper.keybind.KeybindManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored") public class ConfigManager {

    static final  List<Module> toBeEnabled = new ArrayList<>();
    static final  File         CONFIG_FILE;
    static final  String       TOP_NOTE    = """
            // You could break stuff by modifying things in here
            // To make a safe copy of this file, use the .cu save <name> command. To load it, use .cu load <name>
            // Also, only share the saved config using the command above in .minecraft/atomicConfigs
            """;
    public static boolean      loaded      = false;
    public static boolean      enabled     = false;

    static {
        CONFIG_FILE = new File(Atomic.client.runDirectory.getAbsolutePath() + "/config.atomic");
    }

    public static void saveState() {
        if (!loaded || !enabled) {
            Atomic.log(Level.INFO, "Not saving config because we didnt load it yet");
            return;
        }
        Atomic.log(Level.INFO, "Saving config");
        JsonObject base = new JsonObject();
        JsonArray enabled = new JsonArray();
        JsonArray config = new JsonArray();
        for (Module module : ModuleRegistry.getModules()) {
            if (module.isEnabled()) {
                enabled.add(module.getName());
            }
            JsonObject currentConfig = new JsonObject();
            currentConfig.addProperty("name", module.getName());
            JsonArray pairs = new JsonArray();
            for (DynamicValue<?> dynamicValue : module.config.getAll()) {
                JsonObject jesus = new JsonObject();
                jesus.addProperty("key", dynamicValue.getKey());
                jesus.addProperty("value", dynamicValue.getValue() + "");
                pairs.add(jesus);
            }
            currentConfig.add("pairs", pairs);
            config.add(currentConfig);
        }
        base.add("enabled", enabled);
        base.add("config", config);
        try {
            FileUtils.writeStringToFile(CONFIG_FILE, TOP_NOTE + base, Charsets.UTF_8, false);
        } catch (IOException e) {
            e.printStackTrace();
            Atomic.log(Level.ERROR, "Failed to save config!");
        }
        Events.fireEvent(EventType.CONFIG_SAVE, new NonCancellableEvent());
    }

    public static void loadState() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            if (!CONFIG_FILE.isFile()) {
                CONFIG_FILE.delete();
            }
            if (!CONFIG_FILE.exists()) {
                return;
            }
            String retrv = FileUtils.readFileToString(CONFIG_FILE, Charsets.UTF_8);
            JsonObject config = new JsonParser().parse(retrv).getAsJsonObject();
            if (config.has("config") && config.get("config").isJsonArray()) {
                JsonArray configArray = config.get("config").getAsJsonArray();
                for (JsonElement jsonElement : configArray) {
                    if (jsonElement.isJsonObject()) {
                        JsonObject jobj = jsonElement.getAsJsonObject();
                        String name = jobj.get("name").getAsString();
                        name = name.replaceAll("-", "").replaceAll(" ", ""); // make sure we got the new names
                        Module j = ModuleRegistry.getByName(name);
                        if (j == null) {
                            continue;
                        }
                        if (jobj.has("pairs") && jobj.get("pairs").isJsonArray()) {
                            JsonArray pairs = jobj.get("pairs").getAsJsonArray();
                            for (JsonElement pair : pairs) {
                                JsonObject jo = pair.getAsJsonObject();
                                String key = jo.get("key").getAsString();
                                String value = jo.get("value").getAsString();
                                DynamicValue<?> val = j.config.get(key);
                                if (val != null) {
                                    Object newValue = TypeConverter.convert(value, val.getType());
                                    if (newValue != null) {
                                        val.setValue(newValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (config.has("enabled") && config.get("enabled").isJsonArray()) {
                for (JsonElement enabled : config.get("enabled").getAsJsonArray()) {
                    String name = enabled.getAsString().replaceAll("-", "").replaceAll(" ", "");
                    Module m = ModuleRegistry.getByName(name);
                    if (m != null) {
                        toBeEnabled.add(m);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Atomic.log(Level.ERROR, "Failed to load config!");
        } finally {
            KeybindManager.reload();
        }
    }

    public static void enableModules() {
        if (enabled) {
            return;
        }
        enabled = true;
        for (Module module : toBeEnabled) {
            module.setEnabled(true);
            Atomic.log(Level.INFO, "Enabling " + module.getName() + " because config says so");
        }
    }
}
