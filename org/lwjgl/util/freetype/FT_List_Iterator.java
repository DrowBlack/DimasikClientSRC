package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FT_List_IteratorI;

public abstract class FT_List_Iterator
extends Callback
implements FT_List_IteratorI {
    public static FT_List_Iterator create(long functionPointer) {
        FT_List_IteratorI instance = (FT_List_IteratorI)Callback.get(functionPointer);
        return instance instanceof FT_List_Iterator ? (FT_List_Iterator)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FT_List_Iterator createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FT_List_Iterator.create(functionPointer);
    }

    public static FT_List_Iterator create(FT_List_IteratorI instance) {
        return instance instanceof FT_List_Iterator ? (FT_List_Iterator)instance : new Container(instance.address(), instance);
    }

    protected FT_List_Iterator() {
        super(CIF);
    }

    FT_List_Iterator(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FT_List_Iterator {
        private final FT_List_IteratorI delegate;

        Container(long functionPointer, FT_List_IteratorI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long node, long user) {
            return this.delegate.invoke(node, user);
        }
    }
}
