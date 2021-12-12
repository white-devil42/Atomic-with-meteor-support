/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import me.zeroX150.atomic.mixin.game.IClientPlayerInteractionManagerAccessor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InstantBreak extends Module {

    final List<Vec3d>                 positions = new ArrayList<>();
    final MultiValue                  prio      = (MultiValue) this.config.create("Priority", "Speed", "Order", "Speed").description("What to prioritize when breaking blocks");
    final List<PlayerActionC2SPacket> whitelist = new ArrayList<>();

    public InstantBreak() {
        super("InstantBreak", "Sbeed", ModuleType.WORLD);
        Events.registerEventHandler(EventType.PACKET_SEND, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof PlayerActionC2SPacket packet) {
                if (!whitelist.contains(packet)) {
                    if (packet.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK && prio.getValue().equalsIgnoreCase("order")) {
                        event.setCancelled(true);
                    }
                } else {
                    whitelist.remove(packet);
                }
            }
        });
    }

    @Override public void tick() {
        if (Objects.requireNonNull(Atomic.client.interactionManager).isBreakingBlock()) {
            BlockPos last = ((IClientPlayerInteractionManagerAccessor) Atomic.client.interactionManager).getCurrentBreakingPos();
            if (prio.getValue().equalsIgnoreCase("order")) {
                Vec3d p = new Vec3d(last.getX(), last.getY(), last.getZ());
                if (!positions.contains(p)) {
                    positions.add(p);
                }
            } else {
                Objects.requireNonNull(Atomic.client.getNetworkHandler()).sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, last, Direction.DOWN));
                positions.clear();
            }
        }
        Vec3d p = Atomic.client.gameRenderer.getCamera().getPos();
        if (positions.size() == 0) {
            return;
        }
        Vec3d latest = positions.get(0);
        if (latest.add(0.5, 0.5, 0.5).distanceTo(p) >= Atomic.client.interactionManager.getReachDistance()) {
            positions.remove(0);
            return;
        }
        BlockPos bp = new BlockPos(latest);
        if (Objects.requireNonNull(Atomic.client.world).getBlockState(bp).isAir()) {
            positions.remove(0);
            return;
        }
        PlayerActionC2SPacket pstart = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, bp, Direction.DOWN);
        whitelist.add(pstart);
        Objects.requireNonNull(Atomic.client.getNetworkHandler()).sendPacket(pstart);
        Atomic.client.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, bp, Direction.DOWN));
    }

    @Override public void enable() {

    }

    @Override public void disable() {
        positions.clear();
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        for (Vec3d position : new ArrayList<>(positions)) {
            Renderer.R3D.renderOutline(position, new Vec3d(1, 1, 1), Utils.getCurrentRGB(), matrices);
            //Renderer.renderFilled(position,new Vec3d(1,1,1),new Color(0,0,0,150),matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

