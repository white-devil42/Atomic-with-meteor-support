/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.command.impl;

import me.zeroX150.atomic.feature.command.Command;
import me.zeroX150.atomic.feature.module.ModuleRegistry;
import me.zeroX150.atomic.helper.util.Utils;

import java.util.Arrays;

public class ChatSequence extends Command {

    public ChatSequence() {
        super("ChatSequence", "Configuration for the ChatSequence module", "chatsequence", "csequence", "cseq");
    }

    @Override public void onExecute(String[] args) {
        if (args.length == 0) {
            Utils.Client.sendMessage("you gotta give me a command");
            return;
        }
        switch (args[0].toLowerCase()) {
            case "messages" -> {
                if (args.length == 1) {
                    if (me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.messages.isEmpty()) {
                        Utils.Client.sendMessage("No messages saved rn");
                    } else {
                        Utils.Client.sendMessage("Messages:");
                        for (int i = 0; i < me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.messages.size(); i++) {
                            Utils.Client.sendMessage("  #" + (i + 1) + "  " + me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.messages.get(i));
                        }
                    }
                } else {
                    switch (args[1].toLowerCase()) {
                        case "add" -> {
                            if (args.length < 3) {
                                Utils.Client.sendMessage("You have to provide a message to add");
                                return;
                            }
                            if (ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.class).isEnabled()) {
                                Utils.Client.sendMessage("You cant modify the config while the module is running.");
                                return;
                            }
                            String msg = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                            Utils.Client.sendMessage("Added message \"" + msg + "\"!");
                            me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.messages.add(msg);
                        }
                        case "remove" -> {
                            if (args.length < 3) {
                                Utils.Client.sendMessage("You have to provide a message index to remove! Use .chatSequence messages to list them");
                                return;
                            }
                            if (ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.class).isEnabled()) {
                                Utils.Client.sendMessage("You cant modify the config while the module is running.");
                                return;
                            }
                            String i = args[2];
                            int v = Utils.Math.tryParseInt(i, 0) - 1;
                            if (v < 0 || v > me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.messages.size() - 1) {
                                Utils.Client.sendMessage("Not sure if that message exists");
                                return;
                            }
                            String m = me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.messages.get(v);
                            Utils.Client.sendMessage("Removed message \"" + m + "\"");
                            me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.messages.remove(v);
                        }
                        case "list" -> onExecute(new String[]{"messages"});
                        default -> Utils.Client.sendMessage("chatSequence:messages subcommands: add, remove, list");
                    }
                }
            }
            case "delay" -> {
                if (args.length == 1) {
                    Utils.Client.sendMessage("The current delay is " + me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.delay + " ms");
                } else {
                    if (ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.class).isEnabled()) {
                        Utils.Client.sendMessage("You cant modify the config while the module is running.");
                        return;
                    }
                    String nd = args[1];
                    int v = Utils.Math.tryParseInt(nd, 0);
                    if (v < 1 || v > 6000) {
                        Utils.Client.sendMessage("You have to specify a valid number above 0 and under 6000");
                        return;
                    }
                    Utils.Client.sendMessage("Set the delay to " + v + " ms");
                    me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.delay = v;
                }
            }
            case "start" -> {
                Utils.Client.sendMessage("Starting ChatSequence. Disable the module to stop it, or wait until it runs out of messages.");
                me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.shouldRun = true;
                ModuleRegistry.getByClass(me.zeroX150.atomic.feature.module.impl.misc.ChatSequence.class).setEnabled(true);
            }
            default -> Utils.Client.sendMessage("chatSequence subcommands: messages, delay, start");
        }
    }
}
