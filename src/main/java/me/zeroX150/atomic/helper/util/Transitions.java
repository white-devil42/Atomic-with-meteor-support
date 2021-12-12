/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.helper.util;

import java.awt.Color;

public class Transitions {

    /**
     * @param value The current value
     * @param goal  The value to transition to
     * @param speed The speed of the operation (BIGGER = SLOWER!)
     * @return The new value
     */
    public static double transition(double value, double goal, double speed) {
        return transition(value, goal, speed, 0.02);
    }

    public static double transition(double value, double goal, double speed, double skipSize) {
        speed = speed < 1 ? 1 : speed;
        double diff = goal - value;
        double diffCalc = diff / speed;
        if (Math.abs(diffCalc) < skipSize) {
            diffCalc = diff;
        }
        return value + diffCalc;
    }

    public static Color transition(Color value, Color goal, double speed) {
        int rn = (int) Math.floor(transition(value.getRed(), goal.getRed(), speed));
        int gn = (int) Math.floor(transition(value.getGreen(), goal.getGreen(), speed));
        int bn = (int) Math.floor(transition(value.getBlue(), goal.getBlue(), speed));
        int an = (int) Math.floor(transition(value.getAlpha(), goal.getAlpha(), speed));
        return new Color(rn, gn, bn, an);
    }

    public static double easeOutExpo(double x) {
        return x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2;

    }
}
