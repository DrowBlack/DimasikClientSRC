package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraft.profiler.EmptyProfileResult;
import net.minecraft.profiler.IProfileResult;
import net.minecraft.profiler.IResultableProfiler;

public class EmptyProfiler
implements IResultableProfiler {
    public static final EmptyProfiler INSTANCE = new EmptyProfiler();

    private EmptyProfiler() {
    }

    @Override
    public void startTick() {
    }

    @Override
    public void endTick() {
    }

    @Override
    public void startSection(String name) {
    }

    @Override
    public void startSection(Supplier<String> nameSupplier) {
    }

    @Override
    public void endSection() {
    }

    @Override
    public void endStartSection(String name) {
    }

    @Override
    public void endStartSection(Supplier<String> nameSupplier) {
    }

    @Override
    public void func_230035_c_(String p_230035_1_) {
    }

    @Override
    public void func_230036_c_(Supplier<String> p_230036_1_) {
    }

    @Override
    public IProfileResult getResults() {
        return EmptyProfileResult.INSTANCE;
    }
}
