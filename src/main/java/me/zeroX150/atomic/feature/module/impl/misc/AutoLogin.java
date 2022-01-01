/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.misc;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Objects;

public class AutoLogin extends Module {

    final DynamicValue<String> pw     = this.config.create("Password", RandomStringUtils.randomAlphanumeric(10)).description("The password to use");
    final DynamicValue<String> toggle = this.config.create("Toggle", "login").description("The trigger for the module");

    public AutoLogin() {
        super("AutoLogin", "Logins on servers with a custom password", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof ChatMessageC2SPacket chatMessageC2SPacket) {
                String msg = chatMessageC2SPacket.getChatMessage();
                if (msg.toLowerCase().startsWith(toggle.getValue().toLowerCase())) {
                    register();
                    login();
                    event.setCancelled(true);
                }
            }
        });
    }

    void login() {
        Objects.requireNonNull(Atomic.client.player).sendChatMessage("/login " + pw.getValue());
    }

    void register() {
        Objects.requireNonNull(Atomic.client.player).sendChatMessage("/register " + pw.getValue() + " " + pw.getValue());
    }

    @Override public void tick() {

    }

    @Override public void enable() {
        Utils.Logging.messageChat("Send the sentence you configured in chat to /register and /login");
    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return toggle.getValue();
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

