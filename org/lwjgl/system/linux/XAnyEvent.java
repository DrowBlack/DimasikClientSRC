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

public class XAnyEvent
extends Struct<XAnyEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int WINDOW;

    protected XAnyEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XAnyEvent create(long address, @Nullable ByteBuffer container) {
        return new XAnyEvent(address, container);
    }

    public XAnyEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XAnyEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XAnyEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XAnyEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XAnyEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XAnyEvent.ndisplay(this.address());
    }

    @NativeType(value="Window")
    public long window() {
        return XAnyEvent.nwindow(this.address());
    }

    public XAnyEvent type(int value) {
        XAnyEvent.ntype(this.address(), value);
        return this;
    }

    public XAnyEvent serial(@NativeType(value="unsigned long") long value) {
        XAnyEvent.nserial(this.address(), value);
        return this;
    }

    public XAnyEvent send_event(@NativeType(value="Bool") boolean value) {
        XAnyEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XAnyEvent display(@NativeType(value="Display *") long value) {
        XAnyEvent.ndisplay(this.address(), value);
        return this;
    }

    public XAnyEvent window(@NativeType(value="Window") long value) {
        XAnyEvent.nwindow(this.address(), value);
        return this;
    }

    public XAnyEvent set(int type, long serial, boolean send_event, long display, long window) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.window(window);
        return this;
    }

    public XAnyEvent set(XAnyEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XAnyEvent malloc() {
        return new XAnyEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XAnyEvent calloc() {
        return new XAnyEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XAnyEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XAnyEvent(MemoryUtil.memAddress(container), container);
    }

    public static XAnyEvent create(long address) {
        return new XAnyEvent(address, null);
    }

    @Nullable
    public static XAnyEvent createSafe(long address) {
        return address == 0L ? null : new XAnyEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XAnyEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XAnyEvent.__create(capacity, SIZEOF);
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
    public static XAnyEvent mallocStack() {
        return XAnyEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XAnyEvent callocStack() {
        return XAnyEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XAnyEvent mallocStack(MemoryStack stack) {
        return XAnyEvent.malloc(stack);
    }

    @Deprecated
    public static XAnyEvent callocStack(MemoryStack stack) {
        return XAnyEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XAnyEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XAnyEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XAnyEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XAnyEvent.calloc(capacity, stack);
    }

    public static XAnyEvent malloc(MemoryStack stack) {
        return new XAnyEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XAnyEvent calloc(MemoryStack stack) {
        return new XAnyEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static long nwindow(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)WINDOW);
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

    public static void nwindow(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)WINDOW, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XAnyEvent.__struct(XAnyEvent.__member(4), XAnyEvent.__member(CLONG_SIZE), XAnyEvent.__member(4), XAnyEvent.__member(POINTER_SIZE), XAnyEvent.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        WINDOW = layout.offsetof(4);
    }

    public static class Buffer
    extends StructBuffer<XAnyEvent, Buffer>
    implements NativeResource {
        private static final XAnyEvent ELEMENT_FACTORY = XAnyEvent.create(-1L);

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
        protected XAnyEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XAnyEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XAnyEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XAnyEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XAnyEvent.ndisplay(this.address());
        }

        @NativeType(value="Window")
        public long window() {
            return XAnyEvent.nwindow(this.address());
        }

        public Buffer type(int value) {
            XAnyEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XAnyEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XAnyEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XAnyEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer window(@NativeType(value="Window") long value) {
            XAnyEvent.nwindow(this.address(), value);
            return this;
        }
    }
}
