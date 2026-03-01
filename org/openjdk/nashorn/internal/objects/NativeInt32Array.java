package org.openjdk.nashorn.internal.objects;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collections;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.objects.ArrayBufferView;
import org.openjdk.nashorn.internal.objects.ArrayIterator;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.objects.NativeArrayBuffer;
import org.openjdk.nashorn.internal.runtime.JSType;
import org.openjdk.nashorn.internal.runtime.PropertyMap;
import org.openjdk.nashorn.internal.runtime.ScriptObject;
import org.openjdk.nashorn.internal.runtime.arrays.ArrayData;
import org.openjdk.nashorn.internal.runtime.arrays.TypedArrayData;

public final class NativeInt32Array
extends ArrayBufferView {
    public static final int BYTES_PER_ELEMENT = 4;
    private static PropertyMap $nasgenmap$;
    private static final ArrayBufferView.Factory FACTORY;

    public static NativeInt32Array constructor(boolean newObj, Object self, Object ... args) {
        return (NativeInt32Array)NativeInt32Array.constructorImpl(newObj, args, FACTORY);
    }

    NativeInt32Array(NativeArrayBuffer buffer, int byteOffset, int length) {
        super(buffer, byteOffset, length);
    }

    @Override
    protected ArrayBufferView.Factory factory() {
        return FACTORY;
    }

    protected static Object set(Object self, Object array, Object offset) {
        return ArrayBufferView.setImpl(self, array, offset);
    }

    protected static NativeInt32Array subarray(Object self, Object begin, Object end) {
        return (NativeInt32Array)ArrayBufferView.subarrayImpl(self, begin, end);
    }

    public static Object getIterator(Object self) {
        return ArrayIterator.newArrayValueIterator(self);
    }

    @Override
    protected ScriptObject getPrototype(Global global) {
        return global.getInt32ArrayPrototype();
    }

    static {
        FACTORY = new ArrayBufferView.Factory(4){

            @Override
            public ArrayBufferView construct(NativeArrayBuffer buffer, int byteOffset, int length) {
                return new NativeInt32Array(buffer, byteOffset, length);
            }

            public Int32ArrayData createArrayData(ByteBuffer nb, int start, int length) {
                return new Int32ArrayData(nb.asIntBuffer(), start, length);
            }

            @Override
            public String getClassName() {
                return "Int32Array";
            }
        };
        NativeInt32Array.$clinit$();
    }

    public static void $clinit$() {
        $nasgenmap$ = PropertyMap.newMap(Collections.EMPTY_LIST);
    }

    private static final class Int32ArrayData
    extends TypedArrayData<IntBuffer> {
        private static final MethodHandle GET_ELEM = CompilerConstants.specialCall(MethodHandles.lookup(), Int32ArrayData.class, "getElem", Integer.TYPE, Integer.TYPE).methodHandle();
        private static final MethodHandle SET_ELEM = CompilerConstants.specialCall(MethodHandles.lookup(), Int32ArrayData.class, "setElem", Void.TYPE, Integer.TYPE, Integer.TYPE).methodHandle();

        private Int32ArrayData(IntBuffer nb, int start, int end) {
            super(nb.position(start).limit(end).slice(), end - start);
        }

        @Override
        protected MethodHandle getGetElem() {
            return GET_ELEM;
        }

        @Override
        protected MethodHandle getSetElem() {
            return SET_ELEM;
        }

        private int getElem(int index) {
            try {
                return ((IntBuffer)this.nb).get(index);
            }
            catch (IndexOutOfBoundsException e) {
                throw new ClassCastException();
            }
        }

        private void setElem(int index, int elem) {
            try {
                if (index < ((IntBuffer)this.nb).limit()) {
                    ((IntBuffer)this.nb).put(index, elem);
                }
            }
            catch (IndexOutOfBoundsException e) {
                throw new ClassCastException();
            }
        }

        @Override
        public Class<?> getElementType() {
            return Integer.TYPE;
        }

        @Override
        public Class<?> getBoxedElementType() {
            return Integer.class;
        }

        @Override
        public int getInt(int index) {
            return this.getElem(index);
        }

        @Override
        public int getIntOptimistic(int index, int programPoint) {
            return this.getElem(index);
        }

        @Override
        public double getDouble(int index) {
            return this.getInt(index);
        }

        @Override
        public double getDoubleOptimistic(int index, int programPoint) {
            return this.getElem(index);
        }

        @Override
        public Object getObject(int index) {
            return this.getInt(index);
        }

        @Override
        public ArrayData set(int index, Object value, boolean strict) {
            return this.set(index, JSType.toInt32(value), strict);
        }

        @Override
        public ArrayData set(int index, int value, boolean strict) {
            this.setElem(index, value);
            return this;
        }

        @Override
        public ArrayData set(int index, double value, boolean strict) {
            return this.set(index, (int)value, strict);
        }
    }
}
