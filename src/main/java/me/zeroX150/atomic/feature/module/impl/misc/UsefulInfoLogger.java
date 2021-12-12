/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.misc;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.helper.render.AnimatedRenderablePos;
import me.zeroX150.atomic.helper.render.CustomColor;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import me.zeroX150.atomic.mixin.network.IReasonAccessor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.EndCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.EnterCombatS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderCenterChangedS2CPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;

import java.util.ArrayList;
import java.util.List;

public class UsefulInfoLogger extends Module {

    static final Int2ObjectOpenHashMap<String> REASON_MAPPINGS = new Int2ObjectOpenHashMap<>();

    static {
        REASON_MAPPINGS.put(0, "No respawn block");
        REASON_MAPPINGS.put(1, "Rain started");
        REASON_MAPPINGS.put(2, "Rain stopped");
        REASON_MAPPINGS.put(3, "Game mode update");
        REASON_MAPPINGS.put(4, "Game won");
        REASON_MAPPINGS.put(5, "Demo message was shown (haha funny trollar)");
        REASON_MAPPINGS.put(6, "Projectile hit");
        REASON_MAPPINGS.put(7, "Rain type changed");
        REASON_MAPPINGS.put(8, "Thunder type changed");
        REASON_MAPPINGS.put(9, "Pufferfish sting");
        REASON_MAPPINGS.put(10, "Elder guardian effect played");
        REASON_MAPPINGS.put(11, "Instant respawn");
    }

    final List<AnimatedRenderablePos> renders                     = new ArrayList<>();
    final BooleanValue                showGameStateChange         = (BooleanValue) this.config.create("Game state", true).description("Show when the game state changes");
    final BooleanValue                showCommandTreeArrival      = (BooleanValue) this.config.create("Command tree", true).description("Show when command trees arrive");
    final BooleanValue                showEngageInCombat          = (BooleanValue) this.config.create("Combat start", true).description("Shows when you engage in combat");
    final BooleanValue                showEndCombat               = (BooleanValue) this.config.create("Combat end", true).description("Shows when the server considers combat to be over");
    final BooleanValue                showDifficultyChange        = (BooleanValue) this.config.create("New difficulty", true).description("Shows when the server changes difficulty");
    final BooleanValue                showWorldBorderCenterChange = (BooleanValue) this.config.create("World border update", true).description("Shows when the world border changes center");

    public UsefulInfoLogger() {
        super("ServerLogger", "Prints useful information about the server in chat, when it arrives", ModuleType.MISC);
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled()) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            Packet<?> p = pe.getPacket();
            if (p instanceof GameStateChangeS2CPacket packet && showGameStateChange.getValue()) {
                int id = ((IReasonAccessor) packet.getReason()).getId();
                boolean exists = REASON_MAPPINGS.containsKey(id);
                String reason = exists ? REASON_MAPPINGS.get(id) : "Unknown reason";
                Utils.Client.sendMessage("[UIL] [Game state change] Game state changed: " + reason);
            }
            if (p instanceof CommandTreeS2CPacket packet && showCommandTreeArrival.getValue()) {
                Utils.Client.sendMessage("[UIL] [Command tree packet] Command tree packet arrived with " + packet.getCommandTree().getChildren().size() + " commands.");
            }
            if (p instanceof EnterCombatS2CPacket && showEngageInCombat.getValue()) {
                Utils.Client.sendMessage("[UIL] [Combat update] The server considers you now in combat");
            }
            if (p instanceof EndCombatS2CPacket && showEndCombat.getValue()) {
                Utils.Client.sendMessage("[UIL] [Combat update] The server considers you now no longer in combat");
            }
            if (p instanceof DifficultyS2CPacket packet && showDifficultyChange.getValue()) {
                Difficulty d = packet.getDifficulty();
                Utils.Client.sendMessage("[UIL] [Difficulty change] The server is now on " + d.getName() + " difficulty");
            }
            if (p instanceof WorldBorderCenterChangedS2CPacket packet && showWorldBorderCenterChange.getValue()) {
                double centerX = Math.floor(packet.getCenterX());
                double centerZ = Math.floor(packet.getCenterZ());
                Utils.Client.sendMessage("[UIL] [World border center change] The world border center is now at X " + centerX + ", Z " + centerZ);
                renders.add(new AnimatedRenderablePos(new CustomColor(255, 255, 255, true), new CustomColor(0, 0, 0, 0), new Vec3d(centerX, 0, centerZ), new Vec3d(1, 255, 1), 10000));
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
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        renders.removeIf(AnimatedRenderablePos::isExpired);
        for (AnimatedRenderablePos render : renders) {
            Renderer.R3D.renderFilled(render.getPos(), render.getDimensions(), render.getColor(), matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

