package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_Module_RequesterI;

public abstract class FT_Module_Requester
extends Callback
implements FT_Module_RequesterI {
    public static FT_Module_Requester create(long functionPointer) {
        FT_Module_RequesterI instance = (FT_Module_RequesterI)Callback.get(functionPointer);
        return instance instanceof FT_Module_Requester ? (FT_Module_Requester)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_Module_Requester createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_Module_Requester.create(functionPointer);
    }

    public static FT_Module_Requester create(FT_Module_RequesterI instance) {
        return instance instanceof FT_Module_Requester ? (FT_Module_Requester)instance : new Container(instance.address(), instance);
    }

    protected FT_Module_Requester() {
        super(CIF);
    }

    FT_Module_Requester(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_Module_Requester {
        private final FT_Module_RequesterI delegate;

        Container(long functionPointer, FT_Module_RequesterI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long module, long name) {
            return this.delegate.invoke(module, name);
        }
    }
}
