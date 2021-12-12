/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.fun;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class BHop extends Module {

    boolean stateShiftBefore  = false;
    int     pressedTickAmount = 0;

    public BHop() {
        super("BHop", "Simulates bhop", ModuleType.FUN);
    }

    @Override public void tick() {
        if (Atomic.client.options.keySneak.isPressed()) {
            pressedTickAmount++;
        } else {
            pressedTickAmount = 0;
        }
        if (!Objects.requireNonNull(Atomic.client.player).isOnGround() && stateShiftBefore && pressedTickAmount < 5 && pressedTickAmount > 0) {
            Vec3d v = Atomic.client.player.getRotationVector().multiply(1);
            Atomic.client.player.addVelocity(v.x, 0, v.z);
        }
        stateShiftBefore = Atomic.client.player.isOnGround();
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

