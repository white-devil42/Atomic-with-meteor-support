/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.gui.particles;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.render.Renderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlowParticleManager {

    public final List<FlowParticle> particles = new ArrayList<>();

    public FlowParticleManager(int amount) {
        int w = Atomic.client.getWindow().getScaledWidth();
        int h = Atomic.client.getWindow().getScaledHeight();
        Random r = new Random();
        for (int i = 0; i < amount; i++) {
            particles.add(new FlowParticle(new Vec2f(r.nextInt(w - 2) + 1, r.nextInt(h - 2) + 1), new Vec2f((float) Math.random() - 0.5f, (float) Math.random() - 0.5f).multiply(100f), Color.WHITE));
        }
    }

    public void tick() {
        for (FlowParticle particle : particles.toArray(particles.toArray(new FlowParticle[0]))) {
            particle.move();
        }

    }

    public void render() {
        FlowParticle[] pl = particles.toArray(new FlowParticle[0]);
        for (FlowParticle particle : pl) {
            Renderer.R2D.fill(Renderer.Util.modify(particle.color, -1, -1, -1, (int) MathHelper.clamp(particle.brightness * 255, 0, 255)), particle.x - 0.5, particle.y - 0.5, particle.x + .5, particle.y + .5);
            FlowParticle.PosEntry last = null;
            for (int i = 0; i < particle.previousPos.size(); i++) {
                FlowParticle.PosEntry previousPos = particle.previousPos.get(i);
                double v = (double) i / particle.previousPos.size();
                if (last == null) {
                    last = previousPos;
                }
                double dist = Math.sqrt(Math.pow((last.x() - previousPos.x()), 2) + Math.pow((last.y() - previousPos.y()), 2));
                if (dist < 10) {
                    Renderer.R2D.lineScreenD(Renderer.Util.modify(Color.getHSBColor((float) v, 0.6f, 1), -1, -1, -1, (int) (v * 255f)), last.x(), last.y(), previousPos.x(), previousPos.y());
                }
                last = previousPos;
            }
        }
    }

    public void remake() {
        int w = Atomic.client.getWindow().getScaledWidth();
        int h = Atomic.client.getWindow().getScaledHeight();
        Random r = new Random();
        for (FlowParticle o : particles.toArray(new FlowParticle[0])) {
            o.x = r.nextInt(w);
            o.y = r.nextInt(h);
            o.previousPos.clear();
        }
    }
}

