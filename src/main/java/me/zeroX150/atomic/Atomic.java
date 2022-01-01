/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic;

import me.zeroX150.atomic.feature.command.CommandRegistry;
import me.zeroX150.atomic.feature.command.impl.ItemStorage;
import me.zeroX150.atomic.feature.gui.clickgui.Themes;
import me.zeroX150.atomic.feature.gui.notifications.NotificationRenderer;
import me.zeroX150.atomic.feature.gui.screen.FastTickable;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PostInitEvent;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.font.adapter.impl.ClientFontRenderer;
import me.zeroX150.atomic.helper.font.adapter.impl.VanillaFontRenderer;
import me.zeroX150.atomic.helper.font.render.GlyphPageFontRenderer;
import me.zeroX150.atomic.helper.keybind.KeybindManager;
import me.zeroX150.atomic.helper.manager.CapeManager;
import me.zeroX150.atomic.helper.util.ConfigManager;
import me.zeroX150.atomic.helper.util.Rotations;
import me.zeroX150.atomic.helper.util.Utils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;

public class Atomic implements ModInitializer {

    public static final String          MOD_NAME         = "Atomic client";
    public static final MinecraftClient client           = MinecraftClient.getInstance();
    public static final Logger          LOGGER           = LogManager.getLogger();
    public static       Thread          MODULE_FTTICKER;
    public static       Thread          FAST_TICKER;
    public static       File            CONFIG_STORAGE;
    public static       long            lastScreenChange = System.currentTimeMillis();

    public static Atomic INSTANCE;


    public boolean initialized = false;

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_NAME + "] " + message);
    }

    void initFonts() {
        FontRenderers.setNormal(new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", 17, false, false, false)));
        FontRenderers.setTitle(new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Font.ttf", 25, false, false, false)));
        FontRenderers.setMono(new ClientFontRenderer(GlyphPageFontRenderer.createFromID("Mono.ttf", 17, false, false, false)));
        FontRenderers.setVanilla(new VanillaFontRenderer());
    }

    void initGroups() {
        FabricItemGroupBuilder.create(new Identifier("atomic", "saveditems")).icon(() -> new ItemStack(Items.COMMAND_BLOCK)).appendItems(itemStacks -> {
            //itemStacks.clear();
            for (ItemStorage.ItemEntry item : ItemStorage.items) {
                ItemStack s = new ItemStack(item.type());
                s.setNbt(item.tag());
                itemStacks.add(s);
            }
        }).build();
    }

    void initCapes() {
        CapeManager.init();
    }

    @Override public void onInitialize() {
        INSTANCE = this;
        log(Level.INFO, "Initializing");
        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::saveState));
        initGroups();
        log(Level.INFO, "Downloading capes in background");
        initCapes();

    }

    public void postWindowInit() {
        initialized = true;
        initFonts();
        CONFIG_STORAGE = new File(Atomic.client.runDirectory + "/atomicConfigs");
        KeybindManager.init();
        ConfigManager.loadState();
        MODULE_FTTICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickModulesNWC(); // always ticks even when we're not in a world
                if (Atomic.client.player == null || Atomic.client.world == null) {
                    continue;
                }
                tickModules(); // only ticks when we're in a world
            }
        }, "100_tps_ticker:modules");
        FAST_TICKER = new Thread(() -> {
            while (true) {
                Utils.sleep(10);
                tickGuiSystem(); // ticks gui elements
                Themes.tickThemes(); // Tick themes
                if (Atomic.client.player == null || Atomic.client.world == null) {
                    continue;
                }
                Rotations.update(); // updates rotations, again only if we are in a world
            }
        }, "100_tps_ticker:gui");
        MODULE_FTTICKER.start();
        FAST_TICKER.start();
        ModuleRegistry.sortModulesPostInit();
        CommandRegistry.init();
        System.out.println("sending post init");
        Events.fireEvent(EventType.POST_INIT, new PostInitEvent());
    }

    void tickModulesNWC() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) {
                    module.onFastTick_NWC();
                }
            } catch (Exception ignored) {
            }
        }
    }

    void tickModules() {
        for (Module module : ModuleRegistry.getModules()) {
            try {
                if (module.isEnabled()) {
                    module.onFastTick();
                }
            } catch (Exception ignored) {
            }
        }
    }

    void tickGuiSystem() {
        NotificationRenderer.onFastTick();
        try {
            if (client.currentScreen != null) {
                if (client.currentScreen instanceof FastTickable tickable) {
                    tickable.onFastTick();
                }
                for (Element child : new ArrayList<>(client.currentScreen.children())) { // wow i hate this
                    if (child instanceof FastTickable t) {
                        t.onFastTick();
                    }
                }
            }
        } catch (Exception ignored) {

        }
    }
}