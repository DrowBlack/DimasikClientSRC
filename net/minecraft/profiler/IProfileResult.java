package net.minecraft.profiler;

import java.io.File;
import java.util.List;
import net.minecraft.profiler.DataPoint;

public interface IProfileResult {
    public List<DataPoint> getDataPoints(String var1);

    public boolean writeToFile(File var1);

    public long timeStop();

    public int ticksStop();

    public long timeStart();

    public int ticksStart();

    default public long nanoTime() {
        return this.timeStart() - this.timeStop();
    }

    default public int ticksSpend() {
        return this.ticksStart() - this.ticksStop();
    }

    public static String decodePath(String p_225434_0_) {
        return p_225434_0_.replace('\u001e', '.');
    }
}
