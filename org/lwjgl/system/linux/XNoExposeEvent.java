package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class XNoExposeEvent
extends Struct<XNoExposeEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int DRAWABLE;
    public static final int MAJOR_CODE;
    public static final int MINOR_CODE;

    protected XNoExposeEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XNoExposeEvent create(long address, @Nullable ByteBuffer container) {
        return new XNoExposeEvent(address, container);
    }

    public XNoExposeEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XNoExposeEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XNoExposeEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XNoExposeEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XNoExposeEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XNoExposeEvent.ndisplay(this.address());
    }

    @NativeType(value="Drawable")
    public long drawable() {
        return XNoExposeEvent.ndrawable(this.address());
    }

    public int major_code() {
        return XNoExposeEvent.nmajor_code(this.address());
    }

    public int minor_code() {
        return XNoExposeEvent.nminor_code(this.address());
    }

    public XNoExposeEvent type(int value) {
        XNoExposeEvent.ntype(this.address(), value);
        return this;
    }

    public XNoExposeEvent serial(@NativeType(value="unsigned long") long value) {
        XNoExposeEvent.nserial(this.address(), value);
        return this;
    }

    public XNoExposeEvent send_event(@NativeType(value="Bool") boolean value) {
        XNoExposeEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XNoExposeEvent display(@NativeType(value="Display *") long value) {
        XNoExposeEvent.ndisplay(this.address(), value);
        return this;
    }

    public XNoExposeEvent drawable(@NativeType(value="Drawable") long value) {
        XNoExposeEvent.ndrawable(this.address(), value);
        return this;
    }

    public XNoExposeEvent major_code(int value) {
        XNoExposeEvent.nmajor_code(this.address(), value);
        return this;
    }

    public XNoExposeEvent minor_code(int value) {
        XNoExposeEvent.nminor_code(this.address(), value);
        return this;
    }

    public XNoExposeEvent set(int type, long serial, boolean send_event, long display, long drawable, int major_code, int minor_code) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.drawable(drawable);
        this.major_code(major_code);
        this.minor_code(minor_code);
        return this;
    }

    public XNoExposeEvent set(XNoExposeEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XNoExposeEvent malloc() {
        return new XNoExposeEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XNoExposeEvent calloc() {
        return new XNoExposeEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XNoExposeEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XNoExposeEvent(MemoryUtil.memAddress(container), container);
    }

    public static XNoExposeEvent create(long address) {
        return new XNoExposeEvent(address, null);
    }

    @Nullable
    public static XNoExposeEvent createSafe(long address) {
        return address == 0L ? null : new XNoExposeEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XNoExposeEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XNoExposeEvent.__create(capacity, SIZEOF);
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
    public static XNoExposeEvent mallocStack() {
        return XNoExposeEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XNoExposeEvent callocStack() {
        return XNoExposeEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XNoExposeEvent mallocStack(MemoryStack stack) {
        return XNoExposeEvent.malloc(stack);
    }

    @Deprecated
    public static XNoExposeEvent callocStack(MemoryStack stack) {
        return XNoExposeEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XNoExposeEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XNoExposeEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XNoExposeEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XNoExposeEvent.calloc(capacity, stack);
    }

    public static XNoExposeEvent malloc(MemoryStack stack) {
        return new XNoExposeEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XNoExposeEvent calloc(MemoryStack stack) {
        return new XNoExposeEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int ntype(long struct) {
        return UNSAFE.getInt(null, struct + (long)TYPE);
    }

    public static long nserial(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)SERIAL);
    }

    public static int nsend_event(long struct) {
        return UNSAFE.getInt(null, struct + (long)SEND_EVENT);
    }

    public static long ndisplay(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)DISPLAY);
    }

    public static long ndrawable(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DRAWABLE);
    }

    public static int nmajor_code(long struct) {
        return UNSAFE.getInt(null, struct + (long)MAJOR_CODE);
    }

    public static int nminor_code(long struct) {
        return UNSAFE.getInt(null, struct + (long)MINOR_CODE);
    }

    public static void ntype(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)TYPE, value);
    }

    public static void nserial(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)SERIAL, value);
    }

    public static void nsend_event(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SEND_EVENT, value);
    }

    public static void ndisplay(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)DISPLAY, Checks.check(value));
    }

    public static void ndrawable(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)DRAWABLE, value);
    }

    public static void nmajor_code(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)MAJOR_CODE, value);
    }

    public static void nminor_code(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)MINOR_CODE, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XNoExposeEvent.__struct(XNoExposeEvent.__member(4), XNoExposeEvent.__member(CLONG_SIZE), XNoExposeEvent.__member(4), XNoExposeEvent.__member(POINTER_SIZE), XNoExposeEvent.__member(CLONG_SIZE), XNoExposeEvent.__member(4), XNoExposeEvent.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        DRAWABLE = layout.offsetof(4);
        MAJOR_CODE = layout.offsetof(5);
        MINOR_CODE = layout.offsetof(6);
    }

    public static class Buffer
    extends StructBuffer<XNoExposeEvent, Buffer>
    implements NativeResource {
        private static final XNoExposeEvent ELEMENT_FACTORY = XNoExposeEvent.create(-1L);

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
        protected XNoExposeEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XNoExposeEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XNoExposeEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XNoExposeEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XNoExposeEvent.ndisplay(this.address());
        }

        @NativeType(value="Drawable")
        public long drawable() {
            return XNoExposeEvent.ndrawable(this.address());
        }

        public int major_code() {
            return XNoExposeEvent.nmajor_code(this.address());
        }

        public int minor_code() {
            return XNoExposeEvent.nminor_code(this.address());
        }

        public Buffer type(int value) {
            XNoExposeEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XNoExposeEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XNoExposeEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XNoExposeEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer drawable(@NativeType(value="Drawable") long value) {
            XNoExposeEvent.ndrawable(this.address(), value);
            return this;
        }

        public Buffer major_code(int value) {
            XNoExposeEvent.nmajor_code(this.address(), value);
            return this;
        }

        public Buffer minor_code(int value) {
            XNoExposeEvent.nminor_code(this.address(), value);
            return this;
        }
    }
}
