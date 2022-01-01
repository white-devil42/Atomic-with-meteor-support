/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("ResultOfMethodCallIgnored") public class Friends {

    static final File         CONFIG_FILE;
    static final List<Friend> friends = new ArrayList<>();

    static {
        CONFIG_FILE = new File(Atomic.client.runDirectory.getAbsolutePath() + "/friends.atomic");
        loadFromFile();
        Events.registerEventHandler(EventType.CONFIG_SAVE, event -> dumpToFile());
    }

    static void loadFromFile() {
        if (!CONFIG_FILE.exists()) {
            Atomic.log(Level.WARN, "Friends file doesnt exist");
            return;
        }
        try {
            String contents = FileUtils.readFileToString(CONFIG_FILE, StandardCharsets.UTF_8);
            JsonElement jo = JsonParser.parseString(contents);
            JsonArray je = jo.getAsJsonArray();
            friends.clear();
            for (JsonElement jsonElement : je) {
                String v = jsonElement.getAsString();
                UUID friend = UUID.fromString(v);
                friends.add(new Friend(friend));
            }
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Couldnt read friends file");
        }
    }

    static void dumpToFile() {
        Atomic.log(Level.INFO, "Saving friends");
        if (friends.isEmpty()) {
            Atomic.log(Level.INFO, "No friends to save");
            return;
        }
        if (!CONFIG_FILE.isFile()) {
            CONFIG_FILE.delete();
        }
        JsonArray flist = new JsonArray();
        for (Friend friend : friends) {
            flist.add(friend.uuid.toString());
        }
        try {
            FileUtils.writeStringToFile(CONFIG_FILE, flist.toString(), StandardCharsets.UTF_8);
            Atomic.log(Level.INFO, "Saved friends");
        } catch (Exception ignored) {
            Atomic.log(Level.ERROR, "Cant save friends file!");
        }
    }

    public static boolean isAFriend(PlayerEntity pe) {
        boolean v = false;
        for (Friend friend : new ArrayList<>(friends)) {
            if (friend.is(pe)) {
                v = true;
            }
        }
        return v;
    }

    public static void removeFriend(PlayerEntity pe) {
        friends.removeIf(friend -> friend.is(pe));
    }

    public static void addFriend(PlayerEntity pe) {
        Friend f = new Friend(pe.getUuid());
        boolean c = f.is(pe); // always true, to sync player var
        if (!c) {
            UnsafeAccess.UNSAFE.throwException(new Exception("what the fuck")); // someone broke the universe (or a bitflip happened idk)
        }
        friends.add(f);
    }

    public static List<Friend> getFriends() {
        return friends;
    }

    public static class Friend {

        final UUID uuid;
        PlayerEntity player;

        public Friend(UUID uuid) {
            this.uuid = uuid;
        }

        public boolean is(PlayerEntity pe) {
            if (pe.getUuid().equals(uuid)) {
                player = pe;
                return true;
            }
            return false;
        }

        public PlayerEntity getPlayer() {
            return player;
        }

        public UUID getUuid() {
            return uuid;
        }
    }
}
