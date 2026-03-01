package org.openjdk.nashorn.api.linker;

import java.util.List;
import jdk.dynalink.linker.GuardingDynamicLinker;
import jdk.dynalink.linker.GuardingDynamicLinkerExporter;
import org.openjdk.nashorn.internal.runtime.linker.Bootstrap;

public final class NashornLinkerExporter
extends GuardingDynamicLinkerExporter {
    @Override
    public List<GuardingDynamicLinker> get() {
        return Bootstrap.getExposedLinkers();
    }
}
