/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021 0x150.
 */

package me.zeroX150.atomic.feature.module.config;

import java.util.ArrayList;
import java.util.List;

public class PropGroup {

    final String                name;
    final List<DynamicValue<?>> children = new ArrayList<>();

    public PropGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addChild(DynamicValue<?> c) {
        children.add(c);
    }

    public void addAll(DynamicValue<?>... children) {
        for (DynamicValue<?> child : children) {
            addChild(child);
        }
    }

    public List<DynamicValue<?>> getChildren() {
        return children;
    }
}
