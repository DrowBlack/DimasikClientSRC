package net.minecraft.profiler;

import java.io.File;
import java.util.Collections;
import java.util.List;
import net.minecraft.profiler.DataPoint;
import net.minecraft.profiler.IProfileResult;

public class EmptyProfileResult
implements IProfileResult {
    public static final EmptyProfileResult INSTANCE = new EmptyProfileResult();

    private EmptyProfileResult() {
    }

    @Override
    public List<DataPoint> getDataPoints(String sectionPath) {
        return Collections.emptyList();
    }

    @Override
    public boolean writeToFile(File p_219919_1_) {
        return false;
    }

    @Override
    public long timeStop() {
        return 0L;
    }

    @Override
    public int ticksStop() {
        return 0;
    }

    @Override
    public long timeStart() {
        return 0L;
    }

    @Override
    public int ticksStart() {
        return 0;
    }
}
