/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.BlockRenderingEvent;
import me.zeroX150.atomic.helper.event.events.EntityRenderEvent;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;

import java.util.Arrays;

public class NoRender extends Module {

    public static BooleanValue weather;
    public static BooleanValue hurtAnimation;
    public static BooleanValue waterOverlay;
    public static BooleanValue fire;
    final         BooleanValue items       = (BooleanValue) this.config.create("Items", false).description("Doesnt render items");
    final         BooleanValue trapdoors   = (BooleanValue) this.config.create("Trapdoors", false).description("Doesnt render trapdoors");
    final         BooleanValue observers   = (BooleanValue) this.config.create("Observers", false).description("Doesnt render observers");
    final         BooleanValue armorStands = (BooleanValue) this.config.create("Armor stands", true).description("Doesnt render armor stands");

    public NoRender() {
        super("NoRender", "doesnt render shit", ModuleType.RENDER);
        Events.registerEventHandler(EventType.ENTITY_RENDER, event1 -> {
            if (!this.isEnabled()) {
                return;
            }
            EntityRenderEvent event = (EntityRenderEvent) event1;
            if ((event.getEntity().getType() == EntityType.ITEM && items.getValue()) || (event.getEntity().getType() == EntityType.ARMOR_STAND && armorStands.getValue())) {
                event.setCancelled(true);
            }
        });
        Block[] trapdoors = new Block[]{Blocks.ACACIA_TRAPDOOR, Blocks.BIRCH_TRAPDOOR, Blocks.CRIMSON_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR, Blocks.IRON_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR,
                Blocks.OAK_TRAPDOOR, Blocks.SPRUCE_TRAPDOOR, Blocks.WARPED_TRAPDOOR};
        Events.registerEventHandler(EventType.BLOCK_RENDER, event1 -> {
            BlockRenderingEvent event = (BlockRenderingEvent) event1;
            if (Arrays.stream(trapdoors).anyMatch(block -> block == event.getBlockState().getBlock()) && this.trapdoors.getValue() && this.isEnabled()) {
                event.setCancelled(true);
            }
            if (observers.getValue() && event.getBlockState().getBlock() == Blocks.OBSERVER) {
                event.setCancelled(true);
            }
        });
        weather = (BooleanValue) this.config.create("Weather", true).description("Doesnt render weather");
        hurtAnimation = (BooleanValue) this.config.create("Hurt animation", true).description("Doesnt render the hurt animation");
        waterOverlay = (BooleanValue) this.config.create("Water overlay", true).description("Doesnt render the water overlay");
        fire = (BooleanValue) this.config.create("Fire overlay", true).description("Doesnt render if you're on fire");
        this.config.createPropGroup("Entities", armorStands, items);
        this.config.createPropGroup("Blocks", observers, this.trapdoors);
        this.config.createPropGroup("Visual", fire, waterOverlay, hurtAnimation, weather);
    }

    @Override public void tick() {

    }

    @Override public void enable() {
        disable();
    }

    @Override public void disable() {
        Atomic.client.worldRenderer.reload();
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

