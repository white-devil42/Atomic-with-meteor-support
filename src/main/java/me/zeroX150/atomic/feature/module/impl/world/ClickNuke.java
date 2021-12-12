/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.BooleanValue;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.event.EventType;
import me.zeroX150.atomic.helper.event.Events;
import me.zeroX150.atomic.helper.event.events.MouseEvent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public class ClickNuke extends Module {

    final SliderValue  rangeX  = (SliderValue) this.config.create("Range X", 5, 1, 10, 0).description("How big the affected radius should be in the X dimension");
    final SliderValue  rangeZ  = (SliderValue) this.config.create("Range Z", 5, 1, 10, 0).description("How big the affected radius should be in the Z dimension");
    final SliderValue  rangeY  = (SliderValue) this.config.create("Range Y", 5, 1, 10, 0).description("How big the affected radius should be in the Y dimension");
    final BooleanValue destroy = (BooleanValue) this.config.create("Destroy particles", false).description("Makes particles appear when a block gets destroyed");

    public ClickNuke() {
        super("ClickNuke", "Nukes whatever you click at [REQUIRES OP]", ModuleType.WORLD);
        Events.registerEventHandler(EventType.MOUSE_EVENT, event -> {
            if (!this.isEnabled()) {
                return;
            }
            if (Atomic.client.player == null) {
                return;
            }
            MouseEvent event1 = (MouseEvent) event;
            if (event1.getButton() == 0 && event1.getAction() == MouseEvent.MouseEventType.MOUSE_CLICKED) {
                mousePressed();
            }
        });
    }

    void mousePressed() {
        if (Atomic.client.currentScreen != null) {
            return;
        }
        HitResult hr = Objects.requireNonNull(Atomic.client.player).raycast(200d, 0f, true);
        Vec3d pos1 = hr.getPos();
        BlockPos pos = new BlockPos(pos1);
        int startY = MathHelper.clamp(r(pos.getY() - rangeY.getValue()), 0, 255);
        int endY = MathHelper.clamp(r(pos.getY() + rangeY.getValue()), 0, 255);
        String cmd = "/fill " + r(pos.getX() - rangeX.getValue()) + " " + startY + " " + r(pos.getZ() - rangeZ.getValue()) + " " + r(pos.getX() + rangeX.getValue()) + " " + endY + " " + r(pos.getZ() + rangeZ.getValue()) + " " + "minecraft:air" + (destroy.getValue() ? " destroy" : "");
        Atomic.client.player.sendChatMessage(cmd);
    }

    int r(double v) {
        return (int) Math.round(v);
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

