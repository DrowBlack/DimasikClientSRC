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

public class XCirculateRequestEvent
extends Struct<XCirculateRequestEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int PARENT;
    public static final int WINDOW;
    public static final int PLACE;

    protected XCirculateRequestEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XCirculateRequestEvent create(long address, @Nullable ByteBuffer container) {
        return new XCirculateRequestEvent(address, container);
    }

    public XCirculateRequestEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XCirculateRequestEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XCirculateRequestEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XCirculateRequestEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XCirculateRequestEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XCirculateRequestEvent.ndisplay(this.address());
    }

    @NativeType(value="Window")
    public long parent() {
        return XCirculateRequestEvent.nparent(this.address());
    }

    @NativeType(value="Window")
    public long window() {
        return XCirculateRequestEvent.nwindow(this.address());
    }

    public int place() {
        return XCirculateRequestEvent.nplace(this.address());
    }

    public XCirculateRequestEvent type(int value) {
        XCirculateRequestEvent.ntype(this.address(), value);
        return this;
    }

    public XCirculateRequestEvent serial(@NativeType(value="unsigned long") long value) {
        XCirculateRequestEvent.nserial(this.address(), value);
        return this;
    }

    public XCirculateRequestEvent send_event(@NativeType(value="Bool") boolean value) {
        XCirculateRequestEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XCirculateRequestEvent display(@NativeType(value="Display *") long value) {
        XCirculateRequestEvent.ndisplay(this.address(), value);
        return this;
    }

    public XCirculateRequestEvent parent(@NativeType(value="Window") long value) {
        XCirculateRequestEvent.nparent(this.address(), value);
        return this;
    }

    public XCirculateRequestEvent window(@NativeType(value="Window") long value) {
        XCirculateRequestEvent.nwindow(this.address(), value);
        return this;
    }

    public XCirculateRequestEvent place(int value) {
        XCirculateRequestEvent.nplace(this.address(), value);
        return this;
    }

    public XCirculateRequestEvent set(int type, long serial, boolean send_event, long display, long parent, long window, int place) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.parent(parent);
        this.window(window);
        this.place(place);
        return this;
    }

    public XCirculateRequestEvent set(XCirculateRequestEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XCirculateRequestEvent malloc() {
        return new XCirculateRequestEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XCirculateRequestEvent calloc() {
        return new XCirculateRequestEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XCirculateRequestEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XCirculateRequestEvent(MemoryUtil.memAddress(container), container);
    }

    public static XCirculateRequestEvent create(long address) {
        return new XCirculateRequestEvent(address, null);
    }

    @Nullable
    public static XCirculateRequestEvent createSafe(long address) {
        return address == 0L ? null : new XCirculateRequestEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XCirculateRequestEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XCirculateRequestEvent.__create(capacity, SIZEOF);
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
    public static XCirculateRequestEvent mallocStack() {
        return XCirculateRequestEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XCirculateRequestEvent callocStack() {
        return XCirculateRequestEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XCirculateRequestEvent mallocStack(MemoryStack stack) {
        return XCirculateRequestEvent.malloc(stack);
    }

    @Deprecated
    public static XCirculateRequestEvent callocStack(MemoryStack stack) {
        return XCirculateRequestEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XCirculateRequestEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XCirculateRequestEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XCirculateRequestEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XCirculateRequestEvent.calloc(capacity, stack);
    }

    public static XCirculateRequestEvent malloc(MemoryStack stack) {
        return new XCirculateRequestEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XCirculateRequestEvent calloc(MemoryStack stack) {
        return new XCirculateRequestEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static long nparent(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)PARENT);
    }

    public static long nwindow(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)WINDOW);
    }

    public static int nplace(long struct) {
        return UNSAFE.getInt(null, struct + (long)PLACE);
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

    public static void nparent(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)PARENT, value);
    }

    public static void nwindow(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)WINDOW, value);
    }

    public static void nplace(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)PLACE, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XCirculateRequestEvent.__struct(XCirculateRequestEvent.__member(4), XCirculateRequestEvent.__member(CLONG_SIZE), XCirculateRequestEvent.__member(4), XCirculateRequestEvent.__member(POINTER_SIZE), XCirculateRequestEvent.__member(CLONG_SIZE), XCirculateRequestEvent.__member(CLONG_SIZE), XCirculateRequestEvent.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        PARENT = layout.offsetof(4);
        WINDOW = layout.offsetof(5);
        PLACE = layout.offsetof(6);
    }

    public static class Buffer
    extends StructBuffer<XCirculateRequestEvent, Buffer>
    implements NativeResource {
        private static final XCirculateRequestEvent ELEMENT_FACTORY = XCirculateRequestEvent.create(-1L);

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
        protected XCirculateRequestEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XCirculateRequestEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XCirculateRequestEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XCirculateRequestEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XCirculateRequestEvent.ndisplay(this.address());
        }

        @NativeType(value="Window")
        public long parent() {
            return XCirculateRequestEvent.nparent(this.address());
        }

        @NativeType(value="Window")
        public long window() {
            return XCirculateRequestEvent.nwindow(this.address());
        }

        public int place() {
            return XCirculateRequestEvent.nplace(this.address());
        }

        public Buffer type(int value) {
            XCirculateRequestEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XCirculateRequestEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XCirculateRequestEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XCirculateRequestEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer parent(@NativeType(value="Window") long value) {
            XCirculateRequestEvent.nparent(this.address(), value);
            return this;
        }

        public Buffer window(@NativeType(value="Window") long value) {
            XCirculateRequestEvent.nwindow(this.address(), value);
            return this;
        }

        public Buffer place(int value) {
            XCirculateRequestEvent.nplace(this.address(), value);
            return this;
        }
    }
}
