package com.mojang.bridge.game;

public interface PerformanceMetrics {
    public int getMinTime();

    public int getMaxTime();

    public int getAverageTime();

    public int getSampleCount();
}
