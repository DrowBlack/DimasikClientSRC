package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Generic_FinalizerI;

public abstract class FT_Generic_Finalizer
extends Callback
implements FT_Generic_FinalizerI {
    public static FT_Generic_Finalizer create(long functionPointer) {
        FT_Generic_FinalizerI instance = (FT_Generic_FinalizerI)Callback.get(functionPointer);
        return instance instanceof FT_Generic_Finalizer ? (FT_Generic_Finalizer)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Generic_Finalizer createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Generic_Finalizer.create(functionPointer);
    }

    public static FT_Generic_Finalizer create(FT_Generic_FinalizerI instance) {
        return instance instanceof FT_Generic_Finalizer ? (FT_Generic_Finalizer)instance : new Container(instance.address(), instance);
    }

    protected FT_Generic_Finalizer() {
        super(CIF);
    }

    FT_Generic_Finalizer(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Generic_Finalizer {
        private final FT_Generic_FinalizerI delegate;

        Container(long functionPointer, FT_Generic_FinalizerI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long object) {
            this.delegate.invoke(object);
        }
    }
}
