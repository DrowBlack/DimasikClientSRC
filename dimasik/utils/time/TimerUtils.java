package dimasik.utils.time;

import lombok.Generated;
import net.minecraft.client.Minecraft;

public class TimerUtils {
    public long lastMS = System.currentTimeMillis();

    public static float deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (float)(1.0 / (double)Minecraft.getDebugFPS()) : 1.0f;
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public boolean finished(long delay) {
        return System.currentTimeMillis() - delay >= this.lastMS;
    }

    public boolean hasReached(double delay) {
        return (double)(System.currentTimeMillis() - this.lastMS) >= delay;
    }

    public boolean hasTimeElapsed() {
        return this.lastMS < System.currentTimeMillis();
    }

    public void setLastMS(long newValue) {
        this.lastMS = System.currentTimeMillis() + newValue;
    }

    public boolean isReached(long time) {
        return System.currentTimeMillis() - this.lastMS > time;
    }

    public void setLastMC() {
        this.lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - this.lastMS > time;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    public void setTime(long time) {
        this.lastMS = time;
    }

    @Generated
    public long getLastMS() {
        return this.lastMS;
    }
}
