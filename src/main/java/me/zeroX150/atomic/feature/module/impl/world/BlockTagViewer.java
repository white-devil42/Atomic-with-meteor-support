/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.impl.world;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.module.Module;
import me.zeroX150.atomic.feature.module.ModuleType;
import me.zeroX150.atomic.helper.font.FontRenderers;
import me.zeroX150.atomic.helper.render.CustomColor;
import me.zeroX150.atomic.helper.render.Renderer;
import me.zeroX150.atomic.helper.util.Transitions;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Property;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BlockTagViewer extends Module {

    List<Entry> entries = new ArrayList<>();
    float       mw      = 0;

    public BlockTagViewer() {
        super("BlockTagViewer", "Shows data about the viewed block", ModuleType.WORLD);
    }

    @Override public void tick() {
        HitResult hr = Atomic.client.crosshairTarget;
        if (hr instanceof BlockHitResult bhr) {
            BlockPos bp = bhr.getBlockPos();
            BlockState state = Objects.requireNonNull(Atomic.client.world).getBlockState(bp);
            List<String> c = new ArrayList<>();
            for (Property<?> property : state.getProperties()) {
                String v = property.getName() + ": " + state.get(property).toString();
                c.add(v);
            }

            for (String s : c) {
                if (entries.stream().noneMatch(entry -> entry.v.equalsIgnoreCase(s))) {
                    entries.add(new Entry(s));
                }
            }
            for (Entry entry : new ArrayList<>(entries)) {
                if (c.stream().noneMatch(entry.v::equals)) {
                    entry.removed = true;
                }
            }
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

    }

    @Override public void onFastTick() {
        for (Entry entry : new ArrayList<>(entries)) {
            double c = 0.05;
            if (entry.removed) {
                c *= -1;
            }
            entry.animProg += c;
            entry.animProg = MathHelper.clamp(entry.animProg, 0, 1);
            if (entry.animProg == 0 && entry.removed) {
                entries.remove(entry);
            }
        }
    }

    double e(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;
    }

    @Override public void onHudRender() {
        List<Entry> l = new ArrayList<>(entries);
        l.sort(Comparator.comparingDouble(value -> -FontRenderers.getMono().getStringWidth(value.v)));
        entries = l;
        if (l.isEmpty()) {
            return;
        }
        float w = Atomic.client.getWindow().getScaledWidth() / 2f;
        float h = Atomic.client.getWindow().getScaledHeight() / 2f;
        MatrixStack s = new MatrixStack();
        s.push();
        s.translate(w, h, 0);
        float r = 0;
        for (Entry entry : l) {
            if (!entry.removed) {
                r = FontRenderers.getMono().getStringWidth(entry.v) + 4;
                break;
            }
        }
        mw = (float) Transitions.transition(mw, r, 7);
        float height = 0;
        for (Entry entry : l.toArray(new Entry[0])) {
            height += 10 * e(entry.animProg);
        }
        s.translate(0, -height, 0);
        for (Entry entry : l.toArray(new Entry[0])) {
            s.push();
            double prog = e(entry.animProg);
            double c = prog * FontRenderers.getMono().getFontHeight();
            s.scale(1, (float) prog, 1);
            Renderer.R2D.fill(s, new CustomColor(0, 0, 0, (int) (prog * 100)), 0, 0, mw, FontRenderers.getMono().getFontHeight());
            FontRenderers.getMono().drawString(s, entry.v, 2, 0.5f, new CustomColor(255, 255, 255, (int) (prog * 255)).getRGB());
            s.pop();
            s.translate(0, c, 0);
        }
        s.pop();
    }

    static class Entry {

        public final String  v;
        public       double  animProg = 0;
        public       boolean removed  = false;

        public Entry(String v) {
            this.v = v;
        }
    }
}

