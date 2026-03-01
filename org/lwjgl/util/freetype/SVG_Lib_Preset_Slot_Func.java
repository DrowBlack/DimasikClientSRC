package org.lwjgl.util.freetype;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;
import org.lwjgl.util.freetype.SVG_Lib_Preset_Slot_FuncI;

public abstract class SVG_Lib_Preset_Slot_Func
extends Callback
implements SVG_Lib_Preset_Slot_FuncI {
    public static SVG_Lib_Preset_Slot_Func create(long functionPointer) {
        SVG_Lib_Preset_Slot_FuncI instance = (SVG_Lib_Preset_Slot_FuncI)Callback.get(functionPointer);
        return instance instanceof SVG_Lib_Preset_Slot_Func ? (SVG_Lib_Preset_Slot_Func)instance : new Container(functionPointer, instance);
    }

    @Nullable
    public static SVG_Lib_Preset_Slot_Func createSafe(long functionPointer) {
        return functionPointer == 0L ? null : SVG_Lib_Preset_Slot_Func.create(functionPointer);
    }

    public static SVG_Lib_Preset_Slot_Func create(SVG_Lib_Preset_Slot_FuncI instance) {
        return instance instanceof SVG_Lib_Preset_Slot_Func ? (SVG_Lib_Preset_Slot_Func)instance : new Container(instance.address(), instance);
    }

    protected SVG_Lib_Preset_Slot_Func() {
        super(CIF);
    }

    SVG_Lib_Preset_Slot_Func(long functionPointer) {
        super(functionPointer);
    }

    private static final class Container
    extends SVG_Lib_Preset_Slot_Func {
        private final SVG_Lib_Preset_Slot_FuncI delegate;

        Container(long functionPointer, SVG_Lib_Preset_Slot_FuncI delegate) {
            super(functionPointer);
            this.delegate = delegate;
        }

        @Override
        public int invoke(long slot, boolean cache, long state) {
            return this.delegate.invoke(slot, cache, state);
        }
    }
}
