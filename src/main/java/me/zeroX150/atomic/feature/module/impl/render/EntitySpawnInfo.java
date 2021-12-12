/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.LoreQueryEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;

public class EntitySpawnInfo extends Module {

    public static final Item[] SPAWN_EGGS = new Item[]{Items.AXOLOTL_SPAWN_EGG, Items.BAT_SPAWN_EGG, Items.BEE_SPAWN_EGG, Items.BLAZE_SPAWN_EGG, Items.CAT_SPAWN_EGG, Items.CAVE_SPIDER_SPAWN_EGG,
            Items.CHICKEN_SPAWN_EGG, Items.COD_SPAWN_EGG, Items.COW_SPAWN_EGG, Items.CREEPER_SPAWN_EGG, Items.DOLPHIN_SPAWN_EGG, Items.DONKEY_SPAWN_EGG, Items.DROWNED_SPAWN_EGG,
            Items.ELDER_GUARDIAN_SPAWN_EGG, Items.ENDERMAN_SPAWN_EGG, Items.ENDERMITE_SPAWN_EGG, Items.EVOKER_SPAWN_EGG, Items.FOX_SPAWN_EGG, Items.GHAST_SPAWN_EGG, Items.GLOW_SQUID_SPAWN_EGG,
            Items.GOAT_SPAWN_EGG, Items.GUARDIAN_SPAWN_EGG, Items.HOGLIN_SPAWN_EGG, Items.HORSE_SPAWN_EGG, Items.HUSK_SPAWN_EGG, Items.LLAMA_SPAWN_EGG, Items.MAGMA_CUBE_SPAWN_EGG,
            Items.MOOSHROOM_SPAWN_EGG, Items.MULE_SPAWN_EGG, Items.OCELOT_SPAWN_EGG, Items.PANDA_SPAWN_EGG, Items.PARROT_SPAWN_EGG, Items.PHANTOM_SPAWN_EGG, Items.PIG_SPAWN_EGG,
            Items.PIGLIN_SPAWN_EGG, Items.PIGLIN_BRUTE_SPAWN_EGG, Items.PILLAGER_SPAWN_EGG, Items.POLAR_BEAR_SPAWN_EGG, Items.PUFFERFISH_SPAWN_EGG, Items.RABBIT_SPAWN_EGG, Items.RAVAGER_SPAWN_EGG,
            Items.SALMON_SPAWN_EGG, Items.SHEEP_SPAWN_EGG, Items.SHULKER_SPAWN_EGG, Items.SILVERFISH_SPAWN_EGG, Items.SKELETON_SPAWN_EGG, Items.SKELETON_HORSE_SPAWN_EGG, Items.SLIME_SPAWN_EGG,
            Items.SPIDER_SPAWN_EGG, Items.SQUID_SPAWN_EGG, Items.STRAY_SPAWN_EGG, Items.STRIDER_SPAWN_EGG, Items.TRADER_LLAMA_SPAWN_EGG, Items.TROPICAL_FISH_SPAWN_EGG, Items.TURTLE_SPAWN_EGG,
            Items.VEX_SPAWN_EGG, Items.VILLAGER_SPAWN_EGG, Items.VINDICATOR_SPAWN_EGG, Items.WANDERING_TRADER_SPAWN_EGG, Items.WITCH_SPAWN_EGG, Items.WITHER_SKELETON_SPAWN_EGG, Items.WOLF_SPAWN_EGG,
            Items.ZOGLIN_SPAWN_EGG, Items.ZOMBIE_SPAWN_EGG, Items.ZOMBIE_HORSE_SPAWN_EGG, Items.ZOMBIE_VILLAGER_SPAWN_EGG, Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, Items.ARMOR_STAND};

    public EntitySpawnInfo() {
        super("EntitySpawnInfo", "Shows info about items that spawn entities (spawn eggs, armor stands, etc)", ModuleType.RENDER);
        Events.registerEventHandler(EventType.LORE_QUERY, event -> {
            if (!this.isEnabled()) {
                return;
            }
            LoreQueryEvent ev = (LoreQueryEvent) event;
            if (Arrays.stream(SPAWN_EGGS).anyMatch(item -> item == ev.getSource().getItem())) {
                NbtCompound entityTag = new NbtCompound();
                NbtCompound e = ev.getSource().getSubNbt("EntityTag");
                if (e != null) {
                    entityTag = e;
                }
                ev.addClientLore("Spawns: " + parseSpawnData(entityTag));
                ev.addClientLore("Spawns at: " + parsePositionData(entityTag));
            }
        });
    }

    String parsePositionData(NbtCompound entityTag) {
        if (!entityTag.contains("Pos", NbtCompound.LIST_TYPE)) {
            return "default";
        } else {
            try {
                NbtList pos = entityTag.getList("Pos", NbtCompound.DOUBLE_TYPE); // pos tag is a list of doubles, so double list
                int x = (int) Math.round(pos.getDouble(0));
                int y = (int) Math.round(pos.getDouble(1));
                int z = (int) Math.round(pos.getDouble(2));
                return String.format("X=%s;Y=%s;Z=%s", x, y, z);
            } catch (Exception ignored) {
                return "default (failed parsing)";
            }
        }
    }

    String parseSpawnData(NbtCompound entityTag) {
        if (!entityTag.contains("id", 8)) {
            return "default";
        } else {
            try {
                Identifier i = new Identifier(entityTag.getString("id"));
                if (!Registry.ENTITY_TYPE.containsId(i)) {
                    return "default (invalid id)";
                }
                EntityType<?> et = Registry.ENTITY_TYPE.get(i);
                return et.getName().getString();
            } catch (Exception ignored) {
                return "default (failed parsing)";
            }
        }
    }

    @Override public void tick() {

    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

