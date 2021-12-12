/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.font.GlyphPageFontRenderer;
import me.zeroX150.atomic.mixin.game.IMinecraftClientAccessor;
import me.zeroX150.atomic.mixin.game.IRenderTickCounterAccessor;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.awt.Color;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Utils {

    public static ServerInfo latestServerInfo;

    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception ignored) {
        }
    }

    public static void setClientTps(float tps) {
        IRenderTickCounterAccessor accessor = ((IRenderTickCounterAccessor) ((IMinecraftClientAccessor) Atomic.client).getRenderTickCounter());
        accessor.setTickTime(1000f / tps);
    }

    public static Color getCurrentRGB() {
        return new Color(Color.HSBtoRGB((System.currentTimeMillis() % 4750) / 4750f, 0.5f, 1));
    }

    public static String[] splitLinesToWidth(String input, double maxWidth, GlyphPageFontRenderer rendererUsed) {
        List<String> dSplit = List.of(input.split("\n"));
        List<String> splits = new ArrayList<>();
        for (String s : dSplit) {
            List<String> splitContent = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (String c : s.split(" ")) {
                if (rendererUsed.getStringWidth(line + c) >= maxWidth - 10) {
                    splitContent.add(line.toString().trim());
                    line = new StringBuilder();
                }
                line.append(c).append(" ");
            }
            splitContent.add(line.toString().trim());
            splits.addAll(splitContent);
        }
        return splits.toArray(new String[0]);
    }

    @SuppressWarnings("UnusedReturnValue") public static NbtCompound putPos(Vec3d pos, NbtCompound comp) {
        NbtList list = new NbtList();
        list.add(NbtDouble.of(pos.x));
        list.add(NbtDouble.of(pos.y));
        list.add(NbtDouble.of(pos.z));
        comp.put("Pos", list);
        return comp;
    }

    public static ItemStack generateItemStackWithMeta(String nbt, Item item) {
        try {
            ItemStack stack = new ItemStack(item);
            stack.setNbt(StringNbtReader.parse(nbt));
            return stack;
        } catch (Exception ignored) {
            return new ItemStack(item);
        }
    }

    public static class Inventory {

        public static int slotIndexToId(int index) {
            int translatedSlotId;
            if (index >= 0 && index < 9) {
                translatedSlotId = 36 + index;
            } else {
                translatedSlotId = index;
            }
            return translatedSlotId;
        }

        public static void drop(int index) {
            int translatedSlotId = slotIndexToId(index);
            Objects.requireNonNull(Atomic.client.interactionManager)
                    .clickSlot(Objects.requireNonNull(Atomic.client.player).currentScreenHandler.syncId, translatedSlotId, 1, SlotActionType.THROW, Atomic.client.player);
        }

        public static void moveStackToOther(int slotIdFrom, int slotIdTo) {
            Objects.requireNonNull(Atomic.client.interactionManager).clickSlot(0, slotIdFrom, 0, SlotActionType.PICKUP, Atomic.client.player); // pick up item from stack
            Atomic.client.interactionManager.clickSlot(0, slotIdTo, 0, SlotActionType.PICKUP, Atomic.client.player); // put item to target
            Atomic.client.interactionManager.clickSlot(0, slotIdFrom, 0, SlotActionType.PICKUP, Atomic.client.player); // (in case target slot had item) put item from target back to from
        }
    }

    public static class Math {

        public static boolean isNumber(String in) {
            try {
                Integer.parseInt(in);
                return true;
            } catch (Exception ignored) {
                return false;
            }
        }

        public static double roundToDecimal(double n, int point) {
            if (point == 0) {
                return java.lang.Math.floor(n);
            }
            double factor = java.lang.Math.pow(10, point);
            return java.lang.Math.round(n * factor) / factor;
        }

        public static int tryParseInt(String input, int defaultValue) {
            try {
                return Integer.parseInt(input);
            } catch (Exception ignored) {
                return defaultValue;
            }
        }

        public static Vec3d getRotationVector(float pitch, float yaw) {
            float f = pitch * 0.017453292F;
            float g = -yaw * 0.017453292F;
            float h = MathHelper.cos(g);
            float i = MathHelper.sin(g);
            float j = MathHelper.cos(f);
            float k = MathHelper.sin(f);
            return new Vec3d(i * j, -k, h * j);
        }
    }

    public static class Mouse {

        public static double getMouseX() {
            return Atomic.client.mouse.getX() / Atomic.client.getWindow().getScaleFactor();
        }

        public static double getMouseY() {
            return Atomic.client.mouse.getY() / Atomic.client.getWindow().getScaleFactor();
        }
    }

    public static class Players {

        static final Map<String, UUID> UUID_CACHE = new HashMap<>();
        static final Map<UUID, String> NAME_CACHE = new HashMap<>();
        static final HttpClient        client     = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

        public static String getNameFromUUID(UUID uuid) {
            if (NAME_CACHE.containsKey(uuid)) {
                return NAME_CACHE.get(uuid);
            }
            try {
                HttpRequest req = HttpRequest.newBuilder().GET().uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid)).build();
                HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 204 || response.statusCode() == 400) {
                    return null; // no user / invalid username
                }
                JsonObject root = new JsonParser().parse(response.body()).getAsJsonObject();
                return root.get("name").getAsString();
            } catch (Exception ignored) {
                return null;
            }
        }

        public static UUID getUUIDFromName(String name) {
            if (!isPlayerNameValid(name)) {
                return null;
            }
            if (UUID_CACHE.containsKey(name.toLowerCase())) {
                return UUID_CACHE.get(name.toLowerCase());
            }
            try {
                HttpRequest req = HttpRequest.newBuilder().GET().uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name)).build();
                HttpResponse<String> response = client.send(req, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 204 || response.statusCode() == 400) {
                    return null; // no user / invalid username
                }
                JsonObject root = new JsonParser().parse(response.body()).getAsJsonObject();
                String id = root.get("id").getAsString();
                String uuid = id.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
                UUID u = UUID.fromString(uuid);
                UUID_CACHE.put(name.toLowerCase(), u);
                return u;
            } catch (Exception ignored) {
                return null;
            }
        }

        public static boolean isPlayerNameValid(String name) {
            if (name.length() < 3 || name.length() > 16) {
                return false;
            }
            String valid = "abcdefghijklmnopqrstuvwxyz0123456789_";
            boolean isValidEntityName = true;
            for (char c : name.toLowerCase().toCharArray()) {
                if (!valid.contains(c + "")) {
                    isValidEntityName = false;
                    break;
                }
            }
            return isValidEntityName;
        }

        public static int[] decodeUUID(UUID uuid) {
            long sigLeast = uuid.getLeastSignificantBits();
            long sigMost = uuid.getMostSignificantBits();
            return new int[]{(int) (sigMost >> 32), (int) sigMost, (int) (sigLeast >> 32), (int) sigLeast};
        }
    }

    public static class Client {

        public static void sendMessage(String n) {
            if (Atomic.client.player == null) {
                return;
            }
            Atomic.client.player.sendMessage(Text.of("[§9A§r] " + n), false);
        }

        public static boolean isABObstructed(Vec3d a, Vec3d b, World world, Entity requester) {
            RaycastContext rcc = new RaycastContext(a, b, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, requester);
            BlockHitResult bhr = world.raycast(rcc);
            return !bhr.getPos().equals(b);
        }

    }

    public static class TickManager {

        static final List<TickEntry> entries         = new ArrayList<>();
        static final List<Runnable>  nextTickRunners = new ArrayList<>();

        public static void runInNTicks(int n, Runnable toRun) {
            entries.add(new TickEntry(n, toRun));
        }

        public static void tick() {
            for (TickEntry entry : entries.toArray(new TickEntry[0])) {
                entry.v--;
                if (entry.v <= 0) {
                    entry.r.run();
                    entries.remove(entry);
                }
            }
        }

        public static void runOnNextRender(Runnable r) {
            nextTickRunners.add(r);
        }

        public static void render() {
            for (Runnable nextTickRunner : nextTickRunners) {
                nextTickRunner.run();
            }
            nextTickRunners.clear();
        }

        static class TickEntry {

            final Runnable r;
            int v;

            public TickEntry(int v, Runnable r) {
                this.v = v;
                this.r = r;
            }
        }
    }
}
