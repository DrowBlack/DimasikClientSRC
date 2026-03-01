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

public class XMapEvent
extends Struct<XMapEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int EVENT;
    public static final int WINDOW;
    public static final int OVERRIDE_REDIRECT;

    protected XMapEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XMapEvent create(long address, @Nullable ByteBuffer container) {
        return new XMapEvent(address, container);
    }

    public XMapEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XMapEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XMapEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XMapEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XMapEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XMapEvent.ndisplay(this.address());
    }

    @NativeType(value="Window")
    public long event() {
        return XMapEvent.nevent(this.address());
    }

    @NativeType(value="Window")
    public long window() {
        return XMapEvent.nwindow(this.address());
    }

    public int override_redirect() {
        return XMapEvent.noverride_redirect(this.address());
    }

    public XMapEvent type(int value) {
        XMapEvent.ntype(this.address(), value);
        return this;
    }

    public XMapEvent serial(@NativeType(value="unsigned long") long value) {
        XMapEvent.nserial(this.address(), value);
        return this;
    }

    public XMapEvent send_event(@NativeType(value="Bool") boolean value) {
        XMapEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XMapEvent display(@NativeType(value="Display *") long value) {
        XMapEvent.ndisplay(this.address(), value);
        return this;
    }

    public XMapEvent event(@NativeType(value="Window") long value) {
        XMapEvent.nevent(this.address(), value);
        return this;
    }

    public XMapEvent window(@NativeType(value="Window") long value) {
        XMapEvent.nwindow(this.address(), value);
        return this;
    }

    public XMapEvent override_redirect(int value) {
        XMapEvent.noverride_redirect(this.address(), value);
        return this;
    }

    public XMapEvent set(int type, long serial, boolean send_event, long display, long event, long window, int override_redirect) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.event(event);
        this.window(window);
        this.override_redirect(override_redirect);
        return this;
    }

    public XMapEvent set(XMapEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XMapEvent malloc() {
        return new XMapEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XMapEvent calloc() {
        return new XMapEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XMapEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XMapEvent(MemoryUtil.memAddress(container), container);
    }

    public static XMapEvent create(long address) {
        return new XMapEvent(address, null);
    }

    @Nullable
    public static XMapEvent createSafe(long address) {
        return address == 0L ? null : new XMapEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XMapEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XMapEvent.__create(capacity, SIZEOF);
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
    public static XMapEvent mallocStack() {
        return XMapEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XMapEvent callocStack() {
        return XMapEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XMapEvent mallocStack(MemoryStack stack) {
        return XMapEvent.malloc(stack);
    }

    @Deprecated
    public static XMapEvent callocStack(MemoryStack stack) {
        return XMapEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XMapEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XMapEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XMapEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XMapEvent.calloc(capacity, stack);
    }

    public static XMapEvent malloc(MemoryStack stack) {
        return new XMapEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XMapEvent calloc(MemoryStack stack) {
        return new XMapEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static long nevent(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)EVENT);
    }

    public static long nwindow(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)WINDOW);
    }

    public static int noverride_redirect(long struct) {
        return UNSAFE.getInt(null, struct + (long)OVERRIDE_REDIRECT);
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

    public static void nevent(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)EVENT, value);
    }

    public static void nwindow(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)WINDOW, value);
    }

    public static void noverride_redirect(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OVERRIDE_REDIRECT, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XMapEvent.__struct(XMapEvent.__member(4), XMapEvent.__member(CLONG_SIZE), XMapEvent.__member(4), XMapEvent.__member(POINTER_SIZE), XMapEvent.__member(CLONG_SIZE), XMapEvent.__member(CLONG_SIZE), XMapEvent.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        EVENT = layout.offsetof(4);
        WINDOW = layout.offsetof(5);
        OVERRIDE_REDIRECT = layout.offsetof(6);
    }

    public static class Buffer
    extends StructBuffer<XMapEvent, Buffer>
    implements NativeResource {
        private static final XMapEvent ELEMENT_FACTORY = XMapEvent.create(-1L);

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
        protected XMapEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XMapEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XMapEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XMapEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XMapEvent.ndisplay(this.address());
        }

        @NativeType(value="Window")
        public long event() {
            return XMapEvent.nevent(this.address());
        }

        @NativeType(value="Window")
        public long window() {
            return XMapEvent.nwindow(this.address());
        }

        public int override_redirect() {
            return XMapEvent.noverride_redirect(this.address());
        }

        public Buffer type(int value) {
            XMapEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XMapEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XMapEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XMapEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer event(@NativeType(value="Window") long value) {
            XMapEvent.nevent(this.address(), value);
            return this;
        }

        public Buffer window(@NativeType(value="Window") long value) {
            XMapEvent.nwindow(this.address(), value);
            return this;
        }

        public Buffer override_redirect(int value) {
            XMapEvent.noverride_redirect(this.address(), value);
            return this;
        }
    }
}
