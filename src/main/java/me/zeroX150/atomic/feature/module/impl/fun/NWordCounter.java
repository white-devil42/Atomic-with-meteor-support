/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.fun;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import net.minecraft.client.util.math.MatrixStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NWordCounter extends Module {

    final Pattern nwordPattern = Pattern.compile("n[ei](g+)([eoau]r|r[ae]|uh|a|)(s+|ette|)");
    int count = 0;

    public NWordCounter() {
        super("NWordCounter", "Counts the amount of n words in chat", ModuleType.FUN);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof net.minecraft.network.packet.s2c.play.GameMessageS2CPacket packet) {
                Matcher matcher = nwordPattern.matcher(packet.getMessage().getString().toLowerCase());
                count += matcher.results().count();
            }
        });
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return count + "";
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

