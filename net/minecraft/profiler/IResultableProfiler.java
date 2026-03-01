package net.minecraft.profiler;

import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.IProfiler;

public interface IResultableProfiler
extends IProfiler {
    public IProfileResult getResults();
}
