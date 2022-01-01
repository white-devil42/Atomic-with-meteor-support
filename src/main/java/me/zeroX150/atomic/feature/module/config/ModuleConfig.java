/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.config;

import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ModuleConfig {

    final List<DynamicValue<?>> config = new ArrayList<>();
    final List<PropGroup>       groups = new ArrayList<>();

    public void addProxy(DynamicValue<?> v) {
        config.add(v);
        //if (Atomic.client.textRenderer != null) config.sort(Comparator.comparingInt(value -> Atomic.client.textRenderer.getWidth(value.getKey())));
    }

    public <T> DynamicValue<T> create(String key, T value) {
        DynamicValue<T> nv = new DynamicValue<>(key, value);
        addProxy(nv);
        return nv;
    }

    public SliderValue create(String key, double value, double min, double max, int prc) {
        SliderValue sv = new SliderValue(key, MathHelper.clamp(value, min, max), min, max, prc);
        addProxy(sv);
        return sv;
    }

    public BooleanValue create(String key, boolean initial) {
        BooleanValue bv = new BooleanValue(key, initial);
        addProxy(bv);
        return bv;
    }

    public ColorValue create(String key, Color initial, boolean rgb) {
        ColorValue cv = new ColorValue(key, initial);
        cv.setRGB(rgb);
        addProxy(cv);
        return cv;
    }

    public void addGroup(PropGroup group) {
        this.groups.add(group);
    }

    public List<PropGroup> getGroups() {
        return groups;
    }

    public void createPropGroup(String name, DynamicValue<?>... children) {
        PropGroup e = new PropGroup(name);
        e.addAll(children);
        addGroup(e);
    }

    public MultiValue create(String key, String value, String... possible) {
        MultiValue ev = new MultiValue(key, value, possible);
        addProxy(ev);
        return ev;
    }

    public DynamicValue<?> get(String key) {
        for (DynamicValue<?> dynamicValue : config) {
            if (dynamicValue.getKey().equalsIgnoreCase(key)) {
                return dynamicValue;
            }
        }
        return null;
    }

    public List<DynamicValue<?>> getAll() {
        return config;
    }
}
