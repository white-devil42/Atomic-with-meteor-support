/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.movement;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import net.minecraft.client.input.Input;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

import java.util.Objects;

public class AutoWalk extends Module {

    final BooleanValue autoJump;
    final BooleanValue fixatedLook = (BooleanValue) this.config.create("Fixate look", true).description("Fix look to a perfect angle");
    Input     previous = null;
    Direction look     = null;

    public AutoWalk() {
        super("AutoWalk", "Walks automatically for you, in one direction", ModuleType.MOVEMENT);
        autoJump = (BooleanValue) this.config.create("Auto jump", true).description("Automatically jumps while module is active");
    }

    @Override public void tick() {
        if (fixatedLook.getValue()) {
            Objects.requireNonNull(Atomic.client.player).setYaw(look.asRotation());
        }
        if (autoJump.getValue() && Objects.requireNonNull(Atomic.client.player).horizontalCollision && Atomic.client.player.isOnGround()) {
            Atomic.client.player.jump();
        }
    }

    @Override public void enable() {
        look = Objects.requireNonNull(Atomic.client.player).getMovementDirection();
        if (previous == null) {
            previous = Atomic.client.player.input;
        }
        //Utils.InputManagement.startBlockingMovement();
        Atomic.client.player.input = new Input() {
            @Override public void tick(boolean slowDown) {
                this.movementForward = 1f;
                this.pressingForward = true;
                super.tick(slowDown);
            }
        };
    }

    @Override public void disable() {
        Objects.requireNonNull(Atomic.client.player).input = previous;
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

    }

    @Override public void onHudRender() {

    }
}

