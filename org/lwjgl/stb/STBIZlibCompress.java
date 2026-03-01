package org.lwjgl.stb;

import javax.annotation.Nullable;
import org.lwjgl.stb.STBIZlibCompressI;
import org.lwjgl.system.Callback;

public abstract class STBIZlibCompress
extends Callback
implements STBIZlibCompressI {
    public static STBIZlibCompress create(long functionPointer) {
        STBIZlibCompressI instance = (STBIZlibCompressI)Callback.get(functionPointer);
        return instance instanceof STBIZlibCompress ? (STBIZlibCompress)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static STBIZlibCompress createSafe(long functionPointer) {
        return functionPointer == 0L ? null : STBIZlibCompress.create(functionPointer);
    }

    public static STBIZlibCompress create(STBIZlibCompressI instance) {
        return instance instanceof STBIZlibCompress ? (STBIZlibCompress)instance : new Container(instance.address(), instance);
    }

    protected STBIZlibCompress() {
        super(CIF);
    }

    STBIZlibCompress(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends STBIZlibCompress {
        private final STBIZlibCompressI delegate;

        Container(long functionPointer, STBIZlibCompressI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public long invoke(long data, int data_len, long out_len, int quality) {
            return this.delegate.invoke(data, data_len, out_len, quality);
        }
    }
}
