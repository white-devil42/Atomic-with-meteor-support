/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Plugins extends Command {

    private boolean pendingCmdTree = false;

    public Plugins() {
        super("Plugins", "Finds server plugins via command suggestions", "pl", "plugins");
        if (Atomic.client.getNetworkHandler() != null) {
            Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
                if (!pendingCmdTree) {
                    return;
                }
                PacketEvent packetEvent = (PacketEvent) event;
                if (packetEvent.getPacket() instanceof CommandSuggestionsS2CPacket cmdTree) {
                    pendingCmdTree = false;
                    Suggestions suggestions = cmdTree.getSuggestions();
                    Set<String> plugins = new HashSet<>();
                    for (Suggestion suggestion : suggestions.getList()) {
                        String[] cmd = suggestion.getText().split(":");
                        if (cmd.length > 1) {
                            plugins.add(cmd[0]);
                        }
                    }
                    if (plugins.isEmpty()) {
                        Utils.Client.sendMessage("No plugins found");
                        return;
                    }
                    Iterator<String> iterator = plugins.iterator();
                    StringBuilder message = new StringBuilder(iterator.next());
                    while (iterator.hasNext()) {
                        message.append(", ").append(iterator.next());
                    }
                    Utils.Client.sendMessage(message.toString());
                }

            });
        }
    }

    @Override public void onExecute(String[] args) {
        if (Atomic.client.getNetworkHandler() != null) {
            Atomic.client.getNetworkHandler().sendPacket(new RequestCommandCompletionsC2SPacket(0, "/"));
            pendingCmdTree = true;
        }
    }
}
