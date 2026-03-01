package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Vector;

public class FT_Outline
extends Struct<FT_Outline>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int N_CONTOURS;
    public static final int N_POINTS;
    public static final int POINTS;
    public static final int TAGS;
    public static final int CONTOURS;
    public static final int FLAGS;

    protected FT_Outline(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Outline create(long address, @Nullable ByteBuffer container) {
        return new FT_Outline(address, container);
    }

    public FT_Outline(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Outline.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="unsigned short")
    public short n_contours() {
        return FT_Outline.nn_contours(this.address());
    }

    @NativeType(value="unsigned short")
    public short n_points() {
        return FT_Outline.nn_points(this.address());
    }

    @NativeType(value="FT_Vector *")
    public FT_Vector.Buffer points() {
        return FT_Outline.npoints(this.address());
    }

    @NativeType(value="unsigned char *")
    public ByteBuffer tags() {
        return FT_Outline.ntags(this.address());
    }

    @NativeType(value="unsigned short *")
    public ShortBuffer contours() {
        return FT_Outline.ncontours(this.address());
    }

    public int flags() {
        return FT_Outline.nflags(this.address());
    }

    public static FT_Outline malloc() {
        return new FT_Outline(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static FT_Outline calloc() {
        return new FT_Outline(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static FT_Outline create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new FT_Outline(MemoryUtil.memAddress(container), container);
    }

    public static FT_Outline create(long address) {
        return new FT_Outline(address, null);
    }

    @Nullable
    public static FT_Outline createSafe(long address) {
        return address == 0L ? null : new FT_Outline(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(FT_Outline.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = FT_Outline.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Outline malloc(MemoryStack stack) {
        return new FT_Outline(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static FT_Outline calloc(MemoryStack stack) {
        return new FT_Outline(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static short nn_contours(long struct) {
        return UNSAFE.getShort(null, struct + (long)N_CONTOURS);
    }

    public static short nn_points(long struct) {
        return UNSAFE.getShort(null, struct + (long)N_POINTS);
    }

    public static FT_Vector.Buffer npoints(long struct) {
        return FT_Vector.create(MemoryUtil.memGetAddress(struct + (long)POINTS), Short.toUnsignedInt(FT_Outline.nn_points(struct)));
    }

    public static ByteBuffer ntags(long struct) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)TAGS), Short.toUnsignedInt(FT_Outline.nn_points(struct)));
    }

    public static ShortBuffer ncontours(long struct) {
        return MemoryUtil.memShortBuffer(MemoryUtil.memGetAddress(struct + (long)CONTOURS), Short.toUnsignedInt(FT_Outline.nn_contours(struct)));
    }

    public static int nflags(long struct) {
        return UNSAFE.getInt(null, struct + (long)FLAGS);
    }

    static {
        Struct.Layout layout = FT_Outline.__struct(FT_Outline.__member(2), FT_Outline.__member(2), FT_Outline.__member(POINTER_SIZE), FT_Outline.__member(POINTER_SIZE), FT_Outline.__member(POINTER_SIZE), FT_Outline.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        N_CONTOURS = layout.offsetof(0);
        N_POINTS = layout.offsetof(1);
        POINTS = layout.offsetof(2);
        TAGS = layout.offsetof(3);
        CONTOURS = layout.offsetof(4);
        FLAGS = layout.offsetof(5);
    }

    public static class Buffer
    extends StructBuffer<FT_Outline, Buffer>
    implements NativeResource {
        private static final FT_Outline ELEMENT_FACTORY = FT_Outline.create(-1L);

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
        protected FT_Outline getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="unsigned short")
        public short n_contours() {
            return FT_Outline.nn_contours(this.address());
        }

        @NativeType(value="unsigned short")
        public short n_points() {
            return FT_Outline.nn_points(this.address());
        }

        @NativeType(value="FT_Vector *")
        public FT_Vector.Buffer points() {
            return FT_Outline.npoints(this.address());
        }

        @NativeType(value="unsigned char *")
        public ByteBuffer tags() {
            return FT_Outline.ntags(this.address());
        }

        @NativeType(value="unsigned short *")
        public ShortBuffer contours() {
            return FT_Outline.ncontours(this.address());
        }

        public int flags() {
            return FT_Outline.nflags(this.address());
        }
    }
}
