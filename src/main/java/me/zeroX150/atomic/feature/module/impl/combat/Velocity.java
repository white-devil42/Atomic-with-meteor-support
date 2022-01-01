/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.combat;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.PacketEvent;
import me.zeroX150.atomic.mixin.network.IEntityVelocityUpdateS2CPacketMixin;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

public class Velocity extends Module {

    final SliderValue multiplierX = (SliderValue) this.config.create("Horizontal velocity", 0.2, -10, 10, 1).sliderMin(-2.5).sliderMax(2.5).description("How much to multiply X and Z velocity by");
    final SliderValue multiplierY = (SliderValue) this.config.create("Vertical velocity", 0.2, -10, 10, 1).sliderMin(-2.5).sliderMax(2.5).description("How much to multiply Y velocity by");
    final MultiValue  mode        = (MultiValue) this.config.create("Mode", "Modify", "Modify", "Ignore").description("What to do with the packets");

    public Velocity() {
        super("Velocity", "Modifies all incoming velocity updates", ModuleType.COMBAT);
        multiplierX.showOnlyIfModeIsSet(mode, "modify");
        multiplierY.showOnlyIfModeIsSet(mode, "modify");
        Events.registerEventHandler(EventType.PACKET_RECEIVE, event -> {
            if (!this.isEnabled() || Atomic.client.player == null) {
                return;
            }
            PacketEvent pe = (PacketEvent) event;
            if (pe.getPacket() instanceof EntityVelocityUpdateS2CPacket packet && packet.getId() == Atomic.client.player.getId()) {
                if (mode.getValue().equalsIgnoreCase("modify")) {
                    double velX = packet.getVelocityX() / 8000d; // don't ask me why they did this
                    double velY = packet.getVelocityY() / 8000d;
                    double velZ = packet.getVelocityZ() / 8000d;
                    velX *= multiplierX.getValue();
                    velY *= multiplierY.getValue();
                    velZ *= multiplierX.getValue();
                    IEntityVelocityUpdateS2CPacketMixin jesusFuckingChrist = (IEntityVelocityUpdateS2CPacketMixin) packet;
                    jesusFuckingChrist.setVelocityX((int) (velX * 8000));
                    jesusFuckingChrist.setVelocityY((int) (velY * 8000));
                    jesusFuckingChrist.setVelocityZ((int) (velZ * 8000));
                } else {
                    event.setCancelled(true);
                }
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

    }

    @Override public void onHudRender() {

    }
}

