package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_List_DestructorI;

public abstract class FT_List_Destructor
extends Callback
implements FT_List_DestructorI {
    public static FT_List_Destructor create(long functionPointer) {
        FT_List_DestructorI instance = (FT_List_DestructorI)Callback.get(functionPointer);
        return instance instanceof FT_List_Destructor ? (FT_List_Destructor)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_List_Destructor createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_List_Destructor.create(functionPointer);
    }

    public static FT_List_Destructor create(FT_List_DestructorI instance) {
        return instance instanceof FT_List_Destructor ? (FT_List_Destructor)instance : new Container(instance.address(), instance);
    }

    protected FT_List_Destructor() {
        super(CIF);
    }

    FT_List_Destructor(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_List_Destructor {
        private final FT_List_DestructorI delegate;

        Container(long functionPointer, FT_List_DestructorI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public void invoke(long memory, long data, long user) {
            this.delegate.invoke(memory, data, user);
        }
    }
}
