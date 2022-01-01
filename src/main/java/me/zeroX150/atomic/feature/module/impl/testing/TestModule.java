/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.testing;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.paths.Pathfinder;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestModule extends Module {
    BlockPos p1, p2;
    ExecutorService esv = Executors.newFixedThreadPool(1);
    Pathfinder      r;

    public TestModule() {
        super("Test", "The dupe has been moved over to Dupe:.d 2 btw", ModuleType.HIDDEN);
    }

    @Override public void tick() {
    }

    @Override public void enable() {
        BlockPos a = Atomic.client.player.getBlockPos();
        if (p1 == null) {
            p1 = a;
            setEnabled(false);
            return;
        } else if (p2 == null) {
            p2 = a;
        }
        //        BlockPos p = new BlockPos(Atomic.client.player.raycast(1000,0,true).getPos());
        //        BlockPos pp = new BlockPos(p.getX(),a.getY(),p.getZ());
        r = new Pathfinder(p1, p2);
        esv.execute(() -> r.go());
    }

    @Override public void disable() {
        if (p1 != null && p2 != null) {
            p1 = p2 = null;
        }
    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {

        //        if (!r.isFound()) {
        for (Pathfinder.Node node : new ArrayList<>(r.getNodes())) {
            if (node.closed) {
                Renderer.R3D.renderOutline(Vec3d.of(node.bp), new Vec3d(1, 1, 1), Color.RED, matrices);
            } else if (!r.isFound()) {
                Renderer.R3D.renderFilled(Vec3d.of(node.bp).add(.4, .4, .4), new Vec3d(.2, .2, .2), Color.GREEN, matrices);
            }
        }
        //        } else {
        //            Pathfinder.Node prev = null;
        //            for (Pathfinder.Node node : r.getPath()) {
        //                if (prev == null) prev = node;
        //                Renderer.R3D.line(Vec3d.of(prev.bp).add(.5,.5,.5),Vec3d.of(node.bp).add(.5,.5,.5), Color.GREEN,matrices);
        //                prev = node;
        //            }
        //        }
        Renderer.R3D.renderFilled(Vec3d.of(r.getStart()), new Vec3d(1, 1, 1), Color.BLUE, matrices);
        Renderer.R3D.renderFilled(Vec3d.of(r.getEnd()), new Vec3d(1, 1, 1), Color.CYAN, matrices);

    }

    @Override public void onHudRender() {

    }

    @Override public void onFastTick() {

    }
}
