package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.FTC_Face_RequesterI;

public abstract class FTC_Face_Requester
extends Callback
implements FTC_Face_RequesterI {
    public static FTC_Face_Requester create(long functionPointer) {
        FTC_Face_RequesterI instance = (FTC_Face_RequesterI)Callback.get(functionPointer);
        return instance instanceof FTC_Face_Requester ? (FTC_Face_Requester)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static FTC_Face_Requester createSafe(long functionPointer) {
        return functionPointer == 0L ? null : FTC_Face_Requester.create(functionPointer);
    }

    public static FTC_Face_Requester create(FTC_Face_RequesterI instance) {
        return instance instanceof FTC_Face_Requester ? (FTC_Face_Requester)instance : new Container(instance.address(), instance);
    }

    protected FTC_Face_Requester() {
        super(CIF);
    }

    FTC_Face_Requester(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends FTC_Face_Requester {
        private final FTC_Face_RequesterI delegate;

        Container(long functionPointer, FTC_Face_RequesterI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long face_id, long library, long req_data, long aface) {
            return this.delegate.invoke(face_id, library, req_data, aface);
        }
    }
}
