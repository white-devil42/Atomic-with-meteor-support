/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.feature.module.config;

import net.minecraft.util.math.MathHelper;

public class SliderValue extends DynamicValue<Double> {

    final int prec;
    double min, max, sliderMin, sliderMax;

    public SliderValue(String key, double value, double min, double max, int precision) {
        super(key, value);
        this.min = min;
        this.max = max;
        this.sliderMin = min;
        this.sliderMax = max;
        this.prec = MathHelper.clamp(precision, 0, 10);
    }

    @Override public void setValue(Object value) {
        if (!(value instanceof Double)) {
            return;
        }
        this.value = MathHelper.clamp((double) value, min, max);

        onValueChanged();
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getSliderMax() {
        return sliderMax;
    }

    public double getSliderMin() {
        return sliderMin;
    }

    public SliderValue sliderMax(double smax) {
        this.sliderMax = smax;
        return this;
    }

    public SliderValue sliderMin(double smin) {
        this.sliderMin = smin;
        return this;
    }

    public int getPrec() {
        return prec;
    }
}
