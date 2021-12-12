/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.helper.manager.DiscordRPCManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

public class DiscordRPC extends Module {

    public static final String               APPID   = "876226113423151134";
    final               DynamicValue<String> details = this.config.create("Details", "Playing on $SRV").description("The title of the discord RPC ($SRV for server ip, $PLR for player)");
    final               DynamicValue<String> state   = this.config.create("State", "$PLR").description("The description of the discord RPC ($SRV for server ip, $PLR for player)");
    DiscordRPCManager manager          = null;
    String            loggedInUsername = "";
    long              lastUpdateTime   = 0;

    public DiscordRPC() {
        super("DiscordRPC", "A configurable discord RPC", ModuleType.MISC);
    }

    String getIP() {
        if (Atomic.client.getNetworkHandler() == null) {
            return "nothing";
        }
        if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().getServer() != null) {
            return "singleplayer";
        }
        String addr = Atomic.client.getNetworkHandler().getConnection().getAddress().toString(); // abc.xyz/123.45.67.89:12345
        String[] addrSplit = addr.split("/");
        String host = addrSplit[0];
        if (host.isEmpty()) {
            host = (addrSplit.length == 1 ? "0.0.0.0:25565" : addrSplit[1]).split(":")[0];
        }
        String port = (addrSplit.length == 1 ? "0.0.0.0:25565" : addrSplit[1]).split(":")[1];
        if (port.equals("25565")) {
            port = "";
        } else {
            port = ":" + port;
        }
        return host + port;
    }

    @Override public void tick() {
        if (manager == null) {
            return;
        }
        manager.setPresenceDetails(details.getValue().replaceAll("\\$SRV", getIP()).replaceAll("\\$PLR", Atomic.client.getSession().getUsername()));
        manager.setPresenceState(state.getValue().replaceAll("\\$SRV", getIP()).replaceAll("\\$PLR", Atomic.client.getSession().getUsername()));
        manager.getPresence().largeImageText = "Atomic swag, 0x150#0150";

        if (System.currentTimeMillis() - lastUpdateTime > 10000) { // each 10 secs, we change pics
            lastUpdateTime = System.currentTimeMillis();
            manager.cycleImages(19);
        }
    }

    @Override public void enable() {
        DiscordEventHandlers handler = new DiscordEventHandlers();
        handler.ready = user -> loggedInUsername = user.username;
        manager = new DiscordRPCManager().setAppID(APPID).setEventHandler(handler).start();
    }

    @Override public void disable() {
        manager.stop();
        loggedInUsername = "";
    }

    @Override public String getContext() {
        return loggedInUsername.isEmpty() ? null : loggedInUsername;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

