/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.screen.MessageScreen;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.feature.module.config.SliderValue;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MassFillNuke extends Module {

    final SliderValue delay             = this.config.create("Delay", 50, 10, 1000, 0);
    final List<Vec3d> blacklistedChunks = new ArrayList<>();
    Vec3d startPos;
    Vec3d last = null;
    volatile boolean run = false;

    public MassFillNuke() {
        super("MassFillNuke", "Erases your whole render distance one by one [REQUIRES OP]", ModuleType.WORLD);
    }

    @Override public void tick() {
    }

    void execute() {
        Utils.Client.sendMessage("Pre-filtering chunks...");
        for (int y = 245; y > 0; y -= 20) {
            for (int x = -255; x < 255; x += 20) {
                for (int z = -255; z < 255; z += 20) {
                    if (!run) {
                        return;
                    }
                    if (Atomic.client.player == null || Atomic.client.world == null) {
                        setEnabled(false);
                        return;
                    }
                    Utils.sleep(1);
                    Vec3d o = startPos.add(x, 0, z);
                    BlockPos pos = new BlockPos(o.x, y, o.z);
                    last = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    boolean goodChunk = false;
                    for (int xO = -10; xO < 11; xO++) {
                        if (goodChunk) {
                            break;
                        }
                        for (int yO = -10; yO < 11; yO++) {
                            if (goodChunk) {
                                break;
                            }
                            for (int zO = -10; zO < 11; zO++) {
                                BlockPos offset = pos.add(xO, yO, zO);
                                BlockState state = Atomic.client.world.getBlockState(offset);
                                if (!state.isAir()) {
                                    goodChunk = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!goodChunk) {
                        blacklistedChunks.add(last);
                    }
                }
            }
        }
        Utils.Client.sendMessage("Executing...");
        Utils.sleep(1000);
        for (int y = 245; y > 0; y -= 20) {
            for (int x = -255; x < 255; x += 20) {
                for (int z = -255; z < 255; z += 20) {
                    if (!run) {
                        return;
                    }
                    if (Atomic.client.player == null || Atomic.client.world == null) {
                        setEnabled(false);
                        return;
                    }
                    Vec3d o = startPos.add(x, 0, z);
                    BlockPos pos = new BlockPos(o.x, y, o.z);
                    Vec3d v = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
                    if (blacklistedChunks.contains(v)) {
                        continue;
                    }
                    Utils.sleep((long) (delay.getValue() + 0));
                    last = v;
                    String cmd = "/fill " + r(pos.getX() - 10) + " " + MathHelper.clamp(r(pos.getY() - 10), 0, 255) + " " + r(pos.getZ() - 10) + " " + r(pos.getX() + 10) + " " + r(pos.getY() + 10) + " " + r(pos.getZ() + 10) + " " + "minecraft:air";
                    Atomic.client.player.sendChatMessage(cmd);
                }
            }
        }
        setEnabled(false);
    }

    int r(double v) {
        return (int) Math.round(v);
    }

    void startThread() {
        new Thread(this::execute).start();
    }

    @Override public void enable() {
        startPos = Objects.requireNonNull(Atomic.client.player).getPos();
        MessageScreen ms = new MessageScreen(null, "Warning!", "This module is dangerous\nA few requirements:\n1. You need op\n2. You need time\nThis module will destroy absolutely everything. Are you sure you want to continue?", t -> {
            if (!t) {
                setEnabled(false);
            } else {
                run = true;
                startThread();
            }
        }, MessageScreen.ScreenType.YESNO);
        Atomic.client.execute(() -> Atomic.client.setScreen(ms));
    }

    @Override public void disable() {
        run = false;
        blacklistedChunks.clear();
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (last != null) {
            Vec3d origin = last.subtract(0.5, 0.5, 0.5);
            Renderer.R3D.renderFilled(origin, new Vec3d(1, 1, 1), Utils.getCurrentRGB(), matrices);
            Renderer.R3D.line(origin.add(0.5, 0.5, 0.5).subtract(10, 0, 0), origin.add(0.5, 0.5, 0.5).add(10, 0, 0), Color.RED, matrices);
            Renderer.R3D.line(origin.add(0.5, 0.5, 0.5).subtract(0, 0, 10), origin.add(0.5, 0.5, 0.5).add(0, 0, 10), Color.GREEN, matrices);
            Renderer.R3D.line(origin.add(0.5, 0.5, 0.5).subtract(0, 10, 0), origin.add(0.5, 0.5, 0.5).add(0, 10, 0), Color.BLUE, matrices);
            for (Vec3d vec3d : blacklistedChunks.toArray(new Vec3d[0])) {
                if (vec3d.subtract(0.5, 0.5, 0.5).distanceTo(last) > 60) {
                    continue;
                }
                Renderer.R3D.renderFilled(vec3d.subtract(0.5, 0.5, 0.5), new Vec3d(1, 1, 1), Color.RED, matrices);
            }
        }
    }

    @Override public void onHudRender() {

    }
}