package net.minecraft.util;

import lombok.Generated;

public class Timer {
    public float renderPartialTicks;
    public float elapsedPartialTicks;
    private long lastSyncSysClock;
    private final float tickLength;
    private float timerSpeed = 1.0f;
    public float speed = 1.0f;

    public Timer(float ticks, long lastSyncSysClock) {
        this.tickLength = 1000.0f / ticks;
        this.lastSyncSysClock = lastSyncSysClock;
    }

    public int getPartialTicks(long gameTime) {
        this.elapsedPartialTicks = (float)(gameTime - this.lastSyncSysClock) / this.tickLength * this.speed;
        this.lastSyncSysClock = gameTime;
        this.renderPartialTicks += this.elapsedPartialTicks;
        int i = (int)this.renderPartialTicks;
        this.renderPartialTicks -= (float)i;
        return i;
    }

    @Generated
    public float getSpeed() {
        return this.speed;
    }

    @Generated
    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
