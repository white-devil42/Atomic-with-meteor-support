/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.manager;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.zeroX150.atomic.helper.util.Utils;

public class DiscordRPCManager {

    final DiscordRichPresence presence = new DiscordRichPresence();
    String               appid;
    DiscordEventHandlers handler;
    Thread               runner;
    boolean              isRunning  = false;
    int                  imageIndex = 0;

    public DiscordRPCManager() {
    }

    public DiscordRPCManager start() {
        if (handler == null) {
            throw new IllegalStateException("Handler has to be set");
        }
        if (appid == null) {
            throw new IllegalStateException("App ID has to be set");
        }
        isRunning = true;
        DiscordRPC lib = DiscordRPC.INSTANCE;
        lib.Discord_Initialize(appid, handler, true, "");
        presence.startTimestamp = System.currentTimeMillis() / 1000;
        lib.Discord_UpdatePresence(presence);
        runner = new Thread(() -> {
            while (isRunning) {
                lib.Discord_RunCallbacks();
                lib.Discord_UpdatePresence(presence);
                Utils.sleep(2000);
            }
            lib.Discord_Shutdown();
        });
        runner.start();
        return this;
    }

    public DiscordRPCManager setAppID(String appId) {
        this.appid = appId;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue") public DiscordRPCManager setPresenceDetails(String details) {
        presence.details = details;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue") public DiscordRPCManager setPresenceState(String state) {
        presence.state = state;
        return this;
    }

    public DiscordRichPresence getPresence() {
        return presence;
    }

    public DiscordRPCManager setEventHandler(DiscordEventHandlers handler) {
        this.handler = handler;
        return this;
    }

    public void cycleImages(int max) {
        imageIndex++;
        if (imageIndex > max) {
            imageIndex = 1;
        }
        presence.largeImageKey = "a" + imageIndex;
    }

    public void stop() {
        isRunning = false;
    }
}
