/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.helper.keybind.KeybindManager;
import me.zeroX150.atomic.helper.util.TypeConverter;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigUtils extends Command {

    public ConfigUtils() {
        super("ConfigUtils", "config file management", "configUtils", "confu", "cu");
    }

    @Override public String[] getSuggestions(String fullCommand, String[] args) {
        if (args.length == 1) {
            return new String[]{"load", "save"};
        } else if (args.length == 2 && args[0].equalsIgnoreCase("load")) {
            return Arrays.stream(Objects.requireNonNull(Atomic.CONFIG_STORAGE.listFiles())).map(File::getName).collect(Collectors.toList()).toArray(String[]::new);
        }
        return super.getSuggestions(fullCommand, args);
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            error("I need an action, load or save");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "load" -> {
                if (args.length < 2) {
                    error("I need you to specify a file name of the config");
                    return;
                }
                File f = new File(Atomic.CONFIG_STORAGE.getAbsolutePath() + "/" + String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                if (!f.exists()) {
                    error("That file doesnt exist");
                    return;
                }
                if (!f.isFile()) {
                    error("That's not a file");
                    return;
                }
                try {
                    for (Module module : ModuleRegistry.getModules()) {
                        for (DynamicValue<?> dynamicValue : module.config.getAll()) {
                            dynamicValue.setValue(dynamicValue.defaultValue);
                        }
                    }
                    String config = FileUtils.readFileToString(f, Charsets.UTF_8);
                    JsonObject root = JsonParser.parseString(config).getAsJsonObject();
                    for (JsonElement jsonElement : root.get("config").getAsJsonArray()) {
                        JsonObject current = jsonElement.getAsJsonObject();
                        String moduleName = current.get("name").getAsString();
                        Module m = ModuleRegistry.getByName(moduleName);
                        if (m == null) {
                            warn("Config includes invalid module name \"" + moduleName + "\"");
                            continue;
                        }
                        for (JsonElement pairs : current.get("pairs").getAsJsonArray()) {
                            JsonObject c = pairs.getAsJsonObject();
                            String cname = c.get("key").getAsString();
                            String value = c.get("value").getAsString();
                            DynamicValue<?> val = m.config.get(cname);
                            if (val != null) {
                                Object newValue = TypeConverter.convert(value, val.getType());
                                if (newValue != null) {
                                    val.setValue(newValue);
                                }
                            }
                        }
                    }
                    List<Module> shouldBeEnabled = new ArrayList<>();
                    for (JsonElement enabled : root.get("enabled").getAsJsonArray()) {
                        String n = enabled.getAsString();
                        Module m = ModuleRegistry.getByName(n);
                        if (m == null) {
                            warn("Config includes invalid module name \"" + n + "\"");
                            continue;
                        }
                        shouldBeEnabled.add(m);
                        if (!m.isEnabled()) {
                            m.setEnabled(true);
                        }
                    }
                    for (Module module : ModuleRegistry.getModules()) {
                        if (!shouldBeEnabled.contains(module) && module.isEnabled()) {
                            module.setEnabled(false);
                        }
                    }
                    KeybindManager.reload();
                    success("Reloaded keybind manager");
                    success("Loaded config file!");
                } catch (Exception e) {
                    error("Couldn't load config: " + e.getLocalizedMessage());
                }
            }
            case "save" -> {
                if (args.length < 2) {
                    error("I need the output file name");
                    return;
                }
                File out = new File(Atomic.CONFIG_STORAGE.getAbsolutePath() + "/" + String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                if (out.exists()) {
                    warn("Overwriting file because it already exists");
                    if (!out.delete()) {
                        error("Failed to delete old file! Aborting");
                        return;
                    }
                }
                try {
                    JsonObject base = new JsonObject();
                    JsonArray enabled = new JsonArray();
                    JsonArray config = new JsonArray();
                    for (Module module : ModuleRegistry.getModules()) {
                        if (module.getName().equalsIgnoreCase("Alts")) {
                            continue; // we do NOT want to include this
                        }
                        if (module.isEnabled()) {
                            enabled.add(module.getName());
                        }
                        JsonObject currentConfig = new JsonObject();
                        currentConfig.addProperty("name", module.getName());
                        JsonArray pairs = new JsonArray();
                        for (DynamicValue<?> dynamicValue : module.config.getAll()) {
                            if (dynamicValue.getValue().equals(TypeConverter.convert(dynamicValue.defaultValue, dynamicValue.getType()))) {
                                continue; // no need to save that
                            }
                            JsonObject jesus = new JsonObject();
                            jesus.addProperty("key", dynamicValue.getKey());
                            jesus.addProperty("value", dynamicValue.getValue() + "");
                            pairs.add(jesus);
                        }
                        if (pairs.size() == 0) {
                            continue;
                        }
                        currentConfig.add("pairs", pairs);
                        config.add(currentConfig);
                    }
                    base.add("enabled", enabled);
                    base.add("config", config);
                    FileUtils.writeStringToFile(out, base.toString(), Charsets.UTF_8, false);
                    LiteralText t = new LiteralText("[§9A§r] Saved config! Click to open");
                    Style s = Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to open")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, out.getAbsolutePath()));
                    t.setStyle(s);
                    Objects.requireNonNull(Atomic.client.player).sendMessage(t, false);
                } catch (Exception e) {
                    error("Couldn't save config: " + e.getLocalizedMessage());
                }
            }
            default -> error("Invalid action, need either load or save");
        }
    }
}
