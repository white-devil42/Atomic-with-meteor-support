package me.zeroX150.atomic.feature.gui.screen;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.impl.render.Hud;
import me.zeroX150.atomic.helper.Timer;
import me.zeroX150.atomic.helper.util.Friends;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InfoScreen extends ImGuiProxyScreen {
    static Map<UUID, String> usernames = new ConcurrentHashMap<>();
    static ExecutorService   resolver  = Executors.newFixedThreadPool(3);
    //    static List<Float> lastTps = new ArrayList<>();
    static FloatList         lastTps   = new FloatArrayList();
    static FloatList         packIn    = new FloatArrayList();
    static FloatList         packOut   = new FloatArrayList();
    static Timer             updater   = new Timer();
    PlayerListEntry            selectedPlayer       = null;
    AbstractClientPlayerEntity selectedLoadedPlayer = null;
    UUID                       selectedFriend       = null;
    long                       copiedShown          = 0;

    float min(float[] in) {
        float m = 0;
        for (float v : in) {
            m = Math.min(v, m);
        }
        return m;
    }

    float max(float[] in) {
        float m = 0;
        for (float v : in) {
            m = Math.max(v, m);
        }
        return m;
    }

    void server() {
        if (updater.hasExpired(1000)) {
            updater.reset();
            lastTps.add((float) Hud.currentTps);
            packIn.add(Atomic.client.getNetworkHandler().getConnection().getAveragePacketsReceived());
            packOut.add(Atomic.client.getNetworkHandler().getConnection().getAveragePacketsSent());
            while (lastTps.size() > 30) {
                lastTps.removeFloat(0);
            }
            while (packIn.size() > 50) {
                packIn.removeFloat(0);
            }
            while (packOut.size() > 50) {
                packOut.removeFloat(0);
            }
        }
        ImGui.begin("Server", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.AlwaysAutoResize);
        float[] tps = lastTps.toFloatArray();
        float[] pin = packIn.toFloatArray();
        float[] pout = packOut.toFloatArray();
        ImGui.text("TPS");
        ImGui.plotLines("", tps, tps.length, 0, "Average: " + (lastTps.stream().reduce(Float::sum).orElse(0f) / lastTps.size()), 0, 20, 0, 100);
        ImGui.text("Packets in");
        ImGui.plotLines("", pin, pin.length, 0, "", min(pin), max(pin), 0, 100);
        ImGui.text("Packets out");
        ImGui.plotLines("", pout, pout.length, 0, "", min(pout), max(pout), 0, 100);
        ImGui.separator();
        ImGui.text("Address: " + Atomic.client.getNetworkHandler().getConnection().getAddress().toString());
        ImGui.text("Tab list players: " + Atomic.client.getNetworkHandler().getPlayerList().size());
        ImGui.text("Loaded players: " + Atomic.client.world.getPlayers().size());
        ImGui.text("Is connection local? " + (Atomic.client.getNetworkHandler().getConnection().isLocal() ? "Yes" : "No"));
        ImGui.end();
    }

    void players() {
        ImGui.begin("Players", ImGuiWindowFlags.NoResize);
        ImGui.setWindowSize(600, 400);

        if (ImGui.beginTabBar("Player lists")) {
            //            ImGui.dummy(0,5);
            if (ImGui.beginTabItem("Tab list")) {
                if (ImGui.beginListBox("", 190, -1)) {
                    for (PlayerListEntry playerListEntry : Atomic.client.getNetworkHandler().getPlayerList()) {
                        if (ImGui.selectable(playerListEntry.getProfile().getName(), playerListEntry.equals(selectedPlayer))) {
                            selectedPlayer = playerListEntry;
                        }
                    }
                    ImGui.endListBox();
                }
                if (selectedPlayer != null) {
                    ImGui.sameLine();
                    ImGui.beginGroup();
                    ImGui.text("Username: " + selectedPlayer.getProfile().getName());
                    ImGui.text("Display username: " + (selectedPlayer.getDisplayName() == null ? "None" : selectedPlayer.getDisplayName().asString()));
                    ImGui.text("UUID: " + selectedPlayer.getProfile().getId().toString());
                    if (ImGui.isItemClicked()) {
                        copiedShown = System.currentTimeMillis();
                        Atomic.client.keyboard.setClipboard(selectedPlayer.getProfile().getId().toString());
                    }
                    if (System.currentTimeMillis() - copiedShown < 2000) {
                        ImGui.sameLine();
                        ImGui.text("Copied!");
                    }
                    ImGui.text("Gamemode: " + (selectedPlayer.getGameMode() == null ? "Null! (probably a bot)" : selectedPlayer.getGameMode().getName()));
                    if (Friends.getFriends().stream().anyMatch(friend -> friend.getUuid().equals(selectedPlayer.getProfile().getId()))) {
                        if (ImGui.button("Remove friend")) {
                            Friends.getFriends().removeIf(friend -> friend.getUuid().equals(selectedPlayer.getProfile().getId()));
                        }
                    } else {
                        if (ImGui.button("Add friend")) {
                            Friends.getFriends().add(new Friends.Friend(selectedPlayer.getProfile().getId()));
                        }
                    }
                    ImGui.endGroup();
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Loaded players")) {

                if (ImGui.beginListBox("", 190, -1)) {
                    for (AbstractClientPlayerEntity player : Atomic.client.world.getPlayers()) {
                        if (ImGui.selectable(player.getGameProfile().getName(), player.equals(selectedLoadedPlayer))) {
                            selectedLoadedPlayer = player;
                        }
                    }
                    ImGui.endListBox();
                }
                if (selectedLoadedPlayer != null) {
                    PlayerListEntry ple = Atomic.client.getNetworkHandler().getPlayerListEntry(selectedLoadedPlayer.getUuid());
                    ImGui.sameLine();
                    ImGui.beginGroup();
                    ImGui.text("Username: " + selectedLoadedPlayer.getGameProfile().getName());
                    ImGui.text("Display username: " + (selectedLoadedPlayer.getDisplayName() == null ? "None" : selectedLoadedPlayer.getDisplayName().asString()));
                    ImGui.text("Health: " + selectedLoadedPlayer.getHealth());
                    ImGui.text("Max health: " + selectedLoadedPlayer.getMaxHealth());
                    //                    selectedLoadedPlayer.
                    ImGui.text("UUID: " + selectedLoadedPlayer.getGameProfile().getId().toString());
                    if (ImGui.isItemClicked()) {
                        copiedShown = System.currentTimeMillis();
                        Atomic.client.keyboard.setClipboard(selectedLoadedPlayer.getGameProfile().getId().toString());
                    }
                    if (System.currentTimeMillis() - copiedShown < 2000) {
                        ImGui.sameLine();
                        ImGui.text("Copied!");
                    }
                    ImGui.text("Gamemode: " + (ple == null || ple.getGameMode() == null ? "Null! (probably a bot)" : ple.getGameMode().getName()));
                    if (Friends.getFriends().stream().anyMatch(friend -> friend.getUuid().equals(selectedLoadedPlayer.getGameProfile().getId()))) {
                        if (ImGui.button("Remove friend")) {
                            Friends.getFriends().removeIf(friend -> friend.getUuid().equals(selectedLoadedPlayer.getGameProfile().getId()));
                        }
                    } else {
                        if (ImGui.button("Add friend")) {
                            Friends.addFriend(selectedLoadedPlayer);
                            //                            Friends.getFriends().add(new Friends.Friend(selectedLoadedPlayer.getGameProfile().getId()));
                        }
                    }
                    ImGui.endGroup();
                }

                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Friend list")) {
                //                ImGui.text("Yep this is the fucking friend list");
                if (ImGui.beginListBox("", 190, -1)) {
                    for (Friends.Friend friend : Friends.getFriends()) {
                        if (!usernames.containsKey(friend.getUuid())) {
                            // do we already know who it is?
                            if (friend.getPlayer() != null) {
                                usernames.put(friend.getUuid(), friend.getPlayer().getGameProfile().getName());
                            } else {
                                usernames.put(friend.getUuid(), friend.getUuid().toString());
                                resolver.execute(() -> usernames.put(friend.getUuid(), Utils.Players.getNameFromUUID(friend.getUuid())));
                            }
                        }
                        if (ImGui.selectable(usernames.get(friend.getUuid()), friend.getUuid().equals(selectedFriend))) {
                            selectedFriend = friend.getUuid();
                        }
                    }
                    ImGui.endListBox();
                }
                if (selectedFriend != null) {
                    ImGui.sameLine();
                    ImGui.beginGroup();
                    ImGui.text("Name: " + usernames.get(selectedFriend));
                    ImGui.text("UUID: " + selectedFriend);
                    if (ImGui.isItemClicked()) {
                        copiedShown = System.currentTimeMillis();
                        Atomic.client.keyboard.setClipboard(selectedFriend.toString());
                    }
                    if (System.currentTimeMillis() - copiedShown < 2000) {
                        ImGui.sameLine();
                        ImGui.text("Copied!");
                    }
                    // show remove if its still in the list, and re-add if we removed it via the interface
                    if (Friends.getFriends().stream().anyMatch(friend -> friend.getUuid().equals(selectedFriend))) {
                        if (ImGui.button("Remove friend")) {
                            Friends.getFriends().removeIf(friend -> friend.getUuid().equals(selectedFriend));
                        }
                    } else {
                        if (ImGui.button("Re-Add friend")) {
                            Friends.getFriends().add(new Friends.Friend(selectedFriend));
                        }
                    }
                    ImGui.endGroup();
                }
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }

        ImGui.end();
    }

    @Override protected void renderInternal() {
        players();
        server();
    }

    @Override public boolean isPauseScreen() {
        return false;
    }
}
