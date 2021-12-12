/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.render;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.DynamicValue;
import me.zeroX150.atomic.feature.module.config.MultiValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.keybind.Keybind;
import me.zeroX150.atomic.helper.util.Rotations;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;

public class FreeLook extends Module {

    final  BooleanValue hold        = (BooleanValue) this.config.create("Hold", true).description("Whether or not to disable the module when the keybind is unpressed");
    final  BooleanValue enableAA    = (BooleanValue) this.config.create("Enable Anti-Aim", false).description("hvh toggle rage nn noob");
    final  MultiValue   aaMode      = this.config.create("AA mode", "Spin", "Spin", "Jitter", "Sway");
    final  SliderValue  aaSpeed     = this.config.create("Anti-Aim Speed", 1f, 0.1f, 6f, 1);
    final  SliderValue  jitterRange = this.config.create("Jitter Range", 90, 15, 90, 0);
    final  SliderValue  swayRange   = this.config.create("Sway Range", 45, 15, 60, 0);
    public float        newyaw, newpitch, oldyaw, oldpitch;
    Perspective before = Perspective.FIRST_PERSON;
    Keybind     kb;

    int jittertimer = 0;
    int swayYaw     = 0;

    public FreeLook() {
        super("FreeLook", "looks around yourself without you looking", ModuleType.RENDER);
        aaMode.showOnlyIf(enableAA::getValue);
        aaSpeed.showOnlyIf(() -> !aaMode.getValue().equals("Jitter") && enableAA.getValue());
        jitterRange.showOnlyIf(() -> aaMode.getValue().equals("Jitter") && enableAA.getValue());
        swayRange.showOnlyIf(() -> aaMode.getValue().equals("Sway") && enableAA.getValue());
    }

    @Override public void tick() {
        if (kb == null) {
            return;
        }
        if (!kb.isHeld() && hold.getValue()) {
            this.setEnabled(false);
        }
        Rotations.setClientPitch(newpitch);
        Rotations.setClientYaw(newyaw);
    }

    @SuppressWarnings("unchecked") @Override public void enable() {
        kb = new Keybind(((DynamicValue<Integer>) this.config.get("Keybind")).getValue());
        before = Atomic.client.options.getPerspective();
        oldyaw = Objects.requireNonNull(Atomic.client.player).getYaw();
        oldpitch = Atomic.client.player.getPitch();
        newyaw = Atomic.client.player.getYaw();
        if (enableAA.getValue()) {
            newpitch = 90;
        } else {
            newpitch = Atomic.client.player.getPitch();
        }
    }

    @Override public void disable() {
        Atomic.client.options.setPerspective(before);
        Objects.requireNonNull(Atomic.client.player).setYaw(oldyaw);
        Atomic.client.player.setPitch(oldpitch);
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        Atomic.client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
    }

    @Override public void onHudRender() {

    }

    @Override public void onFastTick() {
        if (!enableAA.getValue()) {
            return;
        }
        if (aaMode.getValue().equals("Spin")) {
            newyaw = (float) MathHelper.wrapDegrees(newyaw + aaSpeed.getValue());
        } else if (aaMode.getValue().equals("Jitter")) {
            int temp = (int) (jitterRange.getValue() + 0);
            if (jittertimer == 1) {
                temp *= -1;
            }
            if (jittertimer >= 1) {
                jittertimer = -1;
            }
            jittertimer++;
            newyaw = MathHelper.wrapDegrees(Atomic.client.player.getYaw() + 180 + temp);
        } else if (aaMode.getValue().equals("Sway")) {
            int temp = swayYaw;
            if (temp >= swayRange.getValue() * 2) {
                temp = (int) (swayRange.getValue() + 0) - (swayYaw - (int) (swayRange.getValue() * 2));
            } else {
                temp = (int) (swayRange.getValue() * -1) + swayYaw;
            }
            if (swayYaw >= swayRange.getValue() * 4) {
                swayYaw = 0;
            }
            swayYaw += aaSpeed.getValue();
            newyaw = MathHelper.wrapDegrees(Atomic.client.player.getYaw() + 180 + temp);
        }
        Objects.requireNonNull(Atomic.client.getNetworkHandler()).sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(newyaw, newpitch, Objects.requireNonNull(Atomic.client.player).isOnGround()));
    }
}

