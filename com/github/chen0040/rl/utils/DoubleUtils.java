package com.github.chen0040.rl.utils;

public class DoubleUtils {
    public static boolean equals(double a1, double a2) {
        return Math.abs(a1 - a2) < 1.0E-10;
    }

    public static boolean isZero(double a) {
        return a < 1.0E-20;
    }
}
