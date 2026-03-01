package org.openjdk.nashorn.internal.runtime;

import org.openjdk.nashorn.internal.objects.annotations.SpecializedFunction;

public interface OptimisticBuiltins {
    public SpecializedFunction.LinkLogic getLinkLogic(Class<? extends SpecializedFunction.LinkLogic> var1);

    public boolean hasPerInstanceAssumptions();
}
