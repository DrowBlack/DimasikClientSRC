package org.lwjgl.stb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

@NativeType(value="struct stbtt_pack_range")
public class STBTTPackRange
extends Struct<STBTTPackRange>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FONT_SIZE;
    public static final int FIRST_UNICODE_CODEPOINT_IN_RANGE;
    public static final int ARRAY_OF_UNICODE_CODEPOINTS;
    public static final int NUM_CHARS;
    public static final int CHARDATA_FOR_RANGE;
    public static final int H_OVERSAMPLE;
    public static final int V_OVERSAMPLE;

    protected STBTTPackRange(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBTTPackRange create(long address, @Nullable ByteBuffer container) {
        return new STBTTPackRange(address, container);
    }

    public STBTTPackRange(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBTTPackRange.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public float font_size() {
        return STBTTPackRange.nfont_size(this.address());
    }

    public int first_unicode_codepoint_in_range() {
        return STBTTPackRange.nfirst_unicode_codepoint_in_range(this.address());
    }

    @Nullable
    @NativeType(value="int *")
    public IntBuffer array_of_unicode_codepoints() {
        return STBTTPackRange.narray_of_unicode_codepoints(this.address());
    }

    public int num_chars() {
        return STBTTPackRange.nnum_chars(this.address());
    }

    @NativeType(value="stbtt_packedchar *")
    public STBTTPackedchar.Buffer chardata_for_range() {
        return STBTTPackRange.nchardata_for_range(this.address());
    }

    @NativeType(value="unsigned char")
    public byte h_oversample() {
        return STBTTPackRange.nh_oversample(this.address());
    }

    @NativeType(value="unsigned char")
    public byte v_oversample() {
        return STBTTPackRange.nv_oversample(this.address());
    }

    public STBTTPackRange font_size(float value) {
        STBTTPackRange.nfont_size(this.address(), value);
        return this;
    }

    public STBTTPackRange first_unicode_codepoint_in_range(int value) {
        STBTTPackRange.nfirst_unicode_codepoint_in_range(this.address(), value);
        return this;
    }

    public STBTTPackRange array_of_unicode_codepoints(@Nullable @NativeType(value="int *") IntBuffer value) {
        STBTTPackRange.narray_of_unicode_codepoints(this.address(), value);
        return this;
    }

    public STBTTPackRange num_chars(int value) {
        STBTTPackRange.nnum_chars(this.address(), value);
        return this;
    }

    public STBTTPackRange chardata_for_range(@NativeType(value="stbtt_packedchar *") STBTTPackedchar.Buffer value) {
        STBTTPackRange.nchardata_for_range(this.address(), value);
        return this;
    }

    public STBTTPackRange h_oversample(@NativeType(value="unsigned char") byte value) {
        STBTTPackRange.nh_oversample(this.address(), value);
        return this;
    }

    public STBTTPackRange v_oversample(@NativeType(value="unsigned char") byte value) {
        STBTTPackRange.nv_oversample(this.address(), value);
        return this;
    }

    public STBTTPackRange set(float font_size, int first_unicode_codepoint_in_range, @Nullable IntBuffer array_of_unicode_codepoints, int num_chars, STBTTPackedchar.Buffer chardata_for_range, byte h_oversample, byte v_oversample) {
        this.font_size(font_size);
        this.first_unicode_codepoint_in_range(first_unicode_codepoint_in_range);
        this.array_of_unicode_codepoints(array_of_unicode_codepoints);
        this.num_chars(num_chars);
        this.chardata_for_range(chardata_for_range);
        this.h_oversample(h_oversample);
        this.v_oversample(v_oversample);
        return this;
    }

    public STBTTPackRange set(STBTTPackRange src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static STBTTPackRange malloc() {
        return new STBTTPackRange(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBTTPackRange calloc() {
        return new STBTTPackRange(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBTTPackRange create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBTTPackRange(MemoryUtil.memAddress(container), container);
    }

    public static STBTTPackRange create(long address) {
        return new STBTTPackRange(address, null);
    }

    @Nullable
    public static STBTTPackRange createSafe(long address) {
        return address == 0L ? null : new STBTTPackRange(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBTTPackRange.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBTTPackRange.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    @Deprecated
    public static STBTTPackRange mallocStack() {
        return STBTTPackRange.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTPackRange callocStack() {
        return STBTTPackRange.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static STBTTPackRange mallocStack(MemoryStack stack) {
        return STBTTPackRange.malloc(stack);
    }

    @Deprecated
    public static STBTTPackRange callocStack(MemoryStack stack) {
        return STBTTPackRange.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return STBTTPackRange.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return STBTTPackRange.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return STBTTPackRange.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return STBTTPackRange.calloc(capacity, stack);
    }

    public static STBTTPackRange malloc(MemoryStack stack) {
        return new STBTTPackRange(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBTTPackRange calloc(MemoryStack stack) {
        return new STBTTPackRange(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static float nfont_size(long struct) {
        return UNSAFE.getFloat(null, struct + (long)FONT_SIZE);
    }

    public static int nfirst_unicode_codepoint_in_range(long struct) {
        return UNSAFE.getInt(null, struct + (long)FIRST_UNICODE_CODEPOINT_IN_RANGE);
    }

    @Nullable
    public static IntBuffer narray_of_unicode_codepoints(long struct) {
        return MemoryUtil.memIntBufferSafe(MemoryUtil.memGetAddress(struct + (long)ARRAY_OF_UNICODE_CODEPOINTS), STBTTPackRange.nnum_chars(struct));
    }

    public static int nnum_chars(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_CHARS);
    }

    public static STBTTPackedchar.Buffer nchardata_for_range(long struct) {
        return STBTTPackedchar.create(MemoryUtil.memGetAddress(struct + (long)CHARDATA_FOR_RANGE), STBTTPackRange.nnum_chars(struct));
    }

    public static byte nh_oversample(long struct) {
        return UNSAFE.getByte(null, struct + (long)H_OVERSAMPLE);
    }

    public static byte nv_oversample(long struct) {
        return UNSAFE.getByte(null, struct + (long)V_OVERSAMPLE);
    }

    public static void nfont_size(long struct, float value) {
        UNSAFE.putFloat(null, struct + (long)FONT_SIZE, value);
    }

    public static void nfirst_unicode_codepoint_in_range(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FIRST_UNICODE_CODEPOINT_IN_RANGE, value);
    }

    public static void narray_of_unicode_codepoints(long struct, @Nullable IntBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)ARRAY_OF_UNICODE_CODEPOINTS, MemoryUtil.memAddressSafe(value));
    }

    public static void nnum_chars(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)NUM_CHARS, value);
    }

    public static void nchardata_for_range(long struct, STBTTPackedchar.Buffer value) {
        MemoryUtil.memPutAddress(struct + (long)CHARDATA_FOR_RANGE, value.address());
    }

    public static void nh_oversample(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)H_OVERSAMPLE, value);
    }

    public static void nv_oversample(long struct, byte value) {
        UNSAFE.putByte(null, struct + (long)V_OVERSAMPLE, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)CHARDATA_FOR_RANGE));
    }

    static {
        Struct.Layout layout = STBTTPackRange.__struct(STBTTPackRange.__member(4), STBTTPackRange.__member(4), STBTTPackRange.__member(POINTER_SIZE), STBTTPackRange.__member(4), STBTTPackRange.__member(POINTER_SIZE), STBTTPackRange.__member(1), STBTTPackRange.__member(1));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FONT_SIZE = layout.offsetof(0);
        FIRST_UNICODE_CODEPOINT_IN_RANGE = layout.offsetof(1);
        ARRAY_OF_UNICODE_CODEPOINTS = layout.offsetof(2);
        NUM_CHARS = layout.offsetof(3);
        CHARDATA_FOR_RANGE = layout.offsetof(4);
        H_OVERSAMPLE = layout.offsetof(5);
        V_OVERSAMPLE = layout.offsetof(6);
    }

    public static class Buffer
    extends StructBuffer<STBTTPackRange, Buffer>
    implements NativeResource {
        private static final STBTTPackRange ELEMENT_FACTORY = STBTTPackRange.create(-1L);

        public Buffer(ByteBuffer container) {
            super(container, container.remaining() / SIZEOF);
        }

        public Buffer(long address, int cap) {
            super(address, null, -1, 0, cap, cap);
        }

        Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
            super(address, container, mark, pos, lim, cap);
        }

        @Override
        protected Buffer self() {
            return this;
        }

        @Override
        protected STBTTPackRange getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public float font_size() {
            return STBTTPackRange.nfont_size(this.address());
        }

        public int first_unicode_codepoint_in_range() {
            return STBTTPackRange.nfirst_unicode_codepoint_in_range(this.address());
        }

        @Nullable
        @NativeType(value="int *")
        public IntBuffer array_of_unicode_codepoints() {
            return STBTTPackRange.narray_of_unicode_codepoints(this.address());
        }

        public int num_chars() {
            return STBTTPackRange.nnum_chars(this.address());
        }

        @NativeType(value="stbtt_packedchar *")
        public STBTTPackedchar.Buffer chardata_for_range() {
            return STBTTPackRange.nchardata_for_range(this.address());
        }

        @NativeType(value="unsigned char")
        public byte h_oversample() {
            return STBTTPackRange.nh_oversample(this.address());
        }

        @NativeType(value="unsigned char")
        public byte v_oversample() {
            return STBTTPackRange.nv_oversample(this.address());
        }

        public Buffer font_size(float value) {
            STBTTPackRange.nfont_size(this.address(), value);
            return this;
        }

        public Buffer first_unicode_codepoint_in_range(int value) {
            STBTTPackRange.nfirst_unicode_codepoint_in_range(this.address(), value);
            return this;
        }

        public Buffer array_of_unicode_codepoints(@Nullable @NativeType(value="int *") IntBuffer value) {
            STBTTPackRange.narray_of_unicode_codepoints(this.address(), value);
            return this;
        }

        public Buffer num_chars(int value) {
            STBTTPackRange.nnum_chars(this.address(), value);
            return this;
        }

        public Buffer chardata_for_range(@NativeType(value="stbtt_packedchar *") STBTTPackedchar.Buffer value) {
            STBTTPackRange.nchardata_for_range(this.address(), value);
            return this;
        }

        public Buffer h_oversample(@NativeType(value="unsigned char") byte value) {
            STBTTPackRange.nh_oversample(this.address(), value);
            return this;
        }

        public Buffer v_oversample(@NativeType(value="unsigned char") byte value) {
            STBTTPackRange.nv_oversample(this.address(), value);
            return this;
        }
    }
}
