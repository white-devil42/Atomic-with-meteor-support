/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.combat;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Packets;
import me.zeroX150.atomic.helper.util.Rotations;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AutoEndermanAngry extends Module {

    Entity e;
    int    t = 0;

    public AutoEndermanAngry() {
        super("AutoEndermanAngry", "automatically makes the nearest enderman pissed", ModuleType.COMBAT);
    }

    @Override public void tick() {
        t++;
        if (t > 6) {
            t = 0;
        } else {
            return;
        }
        this.e = null;
        List<Entity> e1 = StreamSupport.stream(Objects.requireNonNull(Atomic.client.world).getEntities().spliterator(), false)
                .filter(entity -> entity.getType() == EntityType.ENDERMAN && entity.getPos()
                        .distanceTo(Objects.requireNonNull(Atomic.client.player).getPos()) < 100 && !((EndermanEntity) entity).isProvoked() && this.e != entity).collect(Collectors.toList());
        Collections.shuffle(e1);
        if (e1.size() > 0) {
            this.e = e1.get(0);
            Packets.sendServerSideLook(this.e.getEyePos());
            Rotations.lookAtV3(this.e.getEyePos());
        }
    }

    @Override public void enable() {

    }

    @Override public void disable() {

    }

    @Override public String getContext() {
        return null;
    }

    @Override public void onWorldRender(MatrixStack matrices) {
        if (e != null) {
            Vec3d dim = new Vec3d(e.getWidth() / 2, 0.05, e.getWidth() / 2);
            Renderer.R3D.renderOutline(e.getEyePos().subtract(dim), dim.multiply(2), Color.RED, matrices);
            Renderer.R3D.line(Renderer.R3D.getCrosshairVector(), e.getEyePos(), Color.WHITE, matrices);
        }
    }

    @Override public void onHudRender() {

    }
}

