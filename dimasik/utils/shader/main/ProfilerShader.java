package dimasik.utils.shader.main;

import lombok.Generated;

public class ProfilerShader {
    long totalTime = 0L;
    long lastStart;
    long lastTime;

    public void start() {
        this.lastStart = System.nanoTime();
    }

    public void stop() {
        this.totalTime += System.nanoTime() - this.lastStart;
        this.start();
    }

    public void reset() {
        this.lastTime = this.totalTime;
        this.totalTime = 0L;
    }

    public long getLastTotalTime() {
        return this.lastTime;
    }

    @Generated
    public long getTotalTime() {
        return this.totalTime;
    }

    @Generated
    public long getLastStart() {
        return this.lastStart;
    }

    @Generated
    public long getLastTime() {
        return this.lastTime;
    }
}
