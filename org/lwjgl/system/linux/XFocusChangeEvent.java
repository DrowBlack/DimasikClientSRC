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

public class XFocusChangeEvent
extends Struct<XFocusChangeEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int WINDOW;
    public static final int MODE;
    public static final int DETAIL;

    protected XFocusChangeEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XFocusChangeEvent create(long address, @Nullable ByteBuffer container) {
        return new XFocusChangeEvent(address, container);
    }

    public XFocusChangeEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XFocusChangeEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XFocusChangeEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XFocusChangeEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XFocusChangeEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XFocusChangeEvent.ndisplay(this.address());
    }

    @NativeType(value="Window")
    public long window() {
        return XFocusChangeEvent.nwindow(this.address());
    }

    public int mode() {
        return XFocusChangeEvent.nmode(this.address());
    }

    public int detail() {
        return XFocusChangeEvent.ndetail(this.address());
    }

    public XFocusChangeEvent type(int value) {
        XFocusChangeEvent.ntype(this.address(), value);
        return this;
    }

    public XFocusChangeEvent serial(@NativeType(value="unsigned long") long value) {
        XFocusChangeEvent.nserial(this.address(), value);
        return this;
    }

    public XFocusChangeEvent send_event(@NativeType(value="Bool") boolean value) {
        XFocusChangeEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XFocusChangeEvent display(@NativeType(value="Display *") long value) {
        XFocusChangeEvent.ndisplay(this.address(), value);
        return this;
    }

    public XFocusChangeEvent window(@NativeType(value="Window") long value) {
        XFocusChangeEvent.nwindow(this.address(), value);
        return this;
    }

    public XFocusChangeEvent mode(int value) {
        XFocusChangeEvent.nmode(this.address(), value);
        return this;
    }

    public XFocusChangeEvent detail(int value) {
        XFocusChangeEvent.ndetail(this.address(), value);
        return this;
    }

    public XFocusChangeEvent set(int type, long serial, boolean send_event, long display, long window, int mode, int detail) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.window(window);
        this.mode(mode);
        this.detail(detail);
        return this;
    }

    public XFocusChangeEvent set(XFocusChangeEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XFocusChangeEvent malloc() {
        return new XFocusChangeEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XFocusChangeEvent calloc() {
        return new XFocusChangeEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XFocusChangeEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XFocusChangeEvent(MemoryUtil.memAddress(container), container);
    }

    public static XFocusChangeEvent create(long address) {
        return new XFocusChangeEvent(address, null);
    }

    @Nullable
    public static XFocusChangeEvent createSafe(long address) {
        return address == 0L ? null : new XFocusChangeEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XFocusChangeEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XFocusChangeEvent.__create(capacity, SIZEOF);
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
    public static XFocusChangeEvent mallocStack() {
        return XFocusChangeEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XFocusChangeEvent callocStack() {
        return XFocusChangeEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XFocusChangeEvent mallocStack(MemoryStack stack) {
        return XFocusChangeEvent.malloc(stack);
    }

    @Deprecated
    public static XFocusChangeEvent callocStack(MemoryStack stack) {
        return XFocusChangeEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XFocusChangeEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XFocusChangeEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XFocusChangeEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XFocusChangeEvent.calloc(capacity, stack);
    }

    public static XFocusChangeEvent malloc(MemoryStack stack) {
        return new XFocusChangeEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XFocusChangeEvent calloc(MemoryStack stack) {
        return new XFocusChangeEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static int nmode(long struct) {
        return UNSAFE.getInt(null, struct + (long)MODE);
    }

    public static int ndetail(long struct) {
        return UNSAFE.getInt(null, struct + (long)DETAIL);
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

    public static void nmode(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)MODE, value);
    }

    public static void ndetail(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)DETAIL, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XFocusChangeEvent.__struct(XFocusChangeEvent.__member(4), XFocusChangeEvent.__member(CLONG_SIZE), XFocusChangeEvent.__member(4), XFocusChangeEvent.__member(POINTER_SIZE), XFocusChangeEvent.__member(CLONG_SIZE), XFocusChangeEvent.__member(4), XFocusChangeEvent.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        WINDOW = layout.offsetof(4);
        MODE = layout.offsetof(5);
        DETAIL = layout.offsetof(6);
    }

    public static class Buffer
    extends StructBuffer<XFocusChangeEvent, Buffer>
    implements NativeResource {
        private static final XFocusChangeEvent ELEMENT_FACTORY = XFocusChangeEvent.create(-1L);

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
        protected XFocusChangeEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XFocusChangeEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XFocusChangeEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XFocusChangeEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XFocusChangeEvent.ndisplay(this.address());
        }

        @NativeType(value="Window")
        public long window() {
            return XFocusChangeEvent.nwindow(this.address());
        }

        public int mode() {
            return XFocusChangeEvent.nmode(this.address());
        }

        public int detail() {
            return XFocusChangeEvent.ndetail(this.address());
        }

        public Buffer type(int value) {
            XFocusChangeEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XFocusChangeEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XFocusChangeEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XFocusChangeEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer window(@NativeType(value="Window") long value) {
            XFocusChangeEvent.nwindow(this.address(), value);
            return this;
        }

        public Buffer mode(int value) {
            XFocusChangeEvent.nmode(this.address(), value);
            return this;
        }

        public Buffer detail(int value) {
            XFocusChangeEvent.ndetail(this.address(), value);
            return this;
        }
    }
}
