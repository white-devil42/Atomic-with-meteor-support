/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.event;

import me.zeroX150.atomic.helper.event.events.base.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Events {

    static final Map<EventType, List<Consumer<Event>>> HANDLERS = new HashMap<>();

    public static void registerEventHandler(EventType event, Consumer<Event> handler) {
        if (!HANDLERS.containsKey(event)) {
            HANDLERS.put(event, new ArrayList<>());
        }
        HANDLERS.get(event).add(handler);
    }

    public static boolean fireEvent(EventType event, Event argument) {
        if (HANDLERS.containsKey(event)) {
            for (Consumer<Event> handler : HANDLERS.get(event)) {
                handler.accept(argument);
            }
        }
        return argument.isCancelled();
    }
}
