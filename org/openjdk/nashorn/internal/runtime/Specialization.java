package org.openjdk.nashorn.internal.runtime;

import java.lang.invoke.MethodHandle;
import org.openjdk.nashorn.internal.objects.annotations.SpecializedFunction;

public final class Specialization {
    private final MethodHandle mh;
    private final Class<? extends SpecializedFunction.LinkLogic> linkLogicClass;
    private final boolean isOptimistic;
    private final boolean convertsNumericArgs;

    public Specialization(MethodHandle mh) {
        this(mh, false, true);
    }

    public Specialization(MethodHandle mh, boolean isOptimistic, boolean convertsNumericArgs) {
        this(mh, null, isOptimistic, convertsNumericArgs);
    }

    public Specialization(MethodHandle mh, Class<? extends SpecializedFunction.LinkLogic> linkLogicClass, boolean isOptimistic, boolean convertsNumericArgs) {
        this.mh = mh;
        this.isOptimistic = isOptimistic;
        this.convertsNumericArgs = convertsNumericArgs;
        this.linkLogicClass = linkLogicClass != null ? (SpecializedFunction.LinkLogic.isEmpty(linkLogicClass) ? null : linkLogicClass) : null;
    }

    public MethodHandle getMethodHandle() {
        return this.mh;
    }

    public Class<? extends SpecializedFunction.LinkLogic> getLinkLogicClass() {
        return this.linkLogicClass;
    }

    public boolean isOptimistic() {
        return this.isOptimistic;
    }

    public boolean convertsNumericArgs() {
        return this.convertsNumericArgs;
    }
}
