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

public class XUnmapEvent
extends Struct<XUnmapEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int EVENT;
    public static final int WINDOW;
    public static final int FROM_CONFIGURE;

    protected XUnmapEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XUnmapEvent create(long address, @Nullable ByteBuffer container) {
        return new XUnmapEvent(address, container);
    }

    public XUnmapEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XUnmapEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XUnmapEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XUnmapEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XUnmapEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XUnmapEvent.ndisplay(this.address());
    }

    @NativeType(value="Window")
    public long event() {
        return XUnmapEvent.nevent(this.address());
    }

    @NativeType(value="Window")
    public long window() {
        return XUnmapEvent.nwindow(this.address());
    }

    public int from_configure() {
        return XUnmapEvent.nfrom_configure(this.address());
    }

    public XUnmapEvent type(int value) {
        XUnmapEvent.ntype(this.address(), value);
        return this;
    }

    public XUnmapEvent serial(@NativeType(value="unsigned long") long value) {
        XUnmapEvent.nserial(this.address(), value);
        return this;
    }

    public XUnmapEvent send_event(@NativeType(value="Bool") boolean value) {
        XUnmapEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XUnmapEvent display(@NativeType(value="Display *") long value) {
        XUnmapEvent.ndisplay(this.address(), value);
        return this;
    }

    public XUnmapEvent event(@NativeType(value="Window") long value) {
        XUnmapEvent.nevent(this.address(), value);
        return this;
    }

    public XUnmapEvent window(@NativeType(value="Window") long value) {
        XUnmapEvent.nwindow(this.address(), value);
        return this;
    }

    public XUnmapEvent from_configure(int value) {
        XUnmapEvent.nfrom_configure(this.address(), value);
        return this;
    }

    public XUnmapEvent set(int type, long serial, boolean send_event, long display, long event, long window, int from_configure) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.event(event);
        this.window(window);
        this.from_configure(from_configure);
        return this;
    }

    public XUnmapEvent set(XUnmapEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XUnmapEvent malloc() {
        return new XUnmapEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XUnmapEvent calloc() {
        return new XUnmapEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XUnmapEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XUnmapEvent(MemoryUtil.memAddress(container), container);
    }

    public static XUnmapEvent create(long address) {
        return new XUnmapEvent(address, null);
    }

    @Nullable
    public static XUnmapEvent createSafe(long address) {
        return address == 0L ? null : new XUnmapEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XUnmapEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XUnmapEvent.__create(capacity, SIZEOF);
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
    public static XUnmapEvent mallocStack() {
        return XUnmapEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XUnmapEvent callocStack() {
        return XUnmapEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XUnmapEvent mallocStack(MemoryStack stack) {
        return XUnmapEvent.malloc(stack);
    }

    @Deprecated
    public static XUnmapEvent callocStack(MemoryStack stack) {
        return XUnmapEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XUnmapEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XUnmapEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XUnmapEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XUnmapEvent.calloc(capacity, stack);
    }

    public static XUnmapEvent malloc(MemoryStack stack) {
        return new XUnmapEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XUnmapEvent calloc(MemoryStack stack) {
        return new XUnmapEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static int nfrom_configure(long struct) {
        return UNSAFE.getInt(null, struct + (long)FROM_CONFIGURE);
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

    public static void nfrom_configure(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FROM_CONFIGURE, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XUnmapEvent.__struct(XUnmapEvent.__member(4), XUnmapEvent.__member(CLONG_SIZE), XUnmapEvent.__member(4), XUnmapEvent.__member(POINTER_SIZE), XUnmapEvent.__member(CLONG_SIZE), XUnmapEvent.__member(CLONG_SIZE), XUnmapEvent.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        EVENT = layout.offsetof(4);
        WINDOW = layout.offsetof(5);
        FROM_CONFIGURE = layout.offsetof(6);
    }

    public static class Buffer
    extends StructBuffer<XUnmapEvent, Buffer>
    implements NativeResource {
        private static final XUnmapEvent ELEMENT_FACTORY = XUnmapEvent.create(-1L);

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
        protected XUnmapEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XUnmapEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XUnmapEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XUnmapEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XUnmapEvent.ndisplay(this.address());
        }

        @NativeType(value="Window")
        public long event() {
            return XUnmapEvent.nevent(this.address());
        }

        @NativeType(value="Window")
        public long window() {
            return XUnmapEvent.nwindow(this.address());
        }

        public int from_configure() {
            return XUnmapEvent.nfrom_configure(this.address());
        }

        public Buffer type(int value) {
            XUnmapEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XUnmapEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XUnmapEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XUnmapEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer event(@NativeType(value="Window") long value) {
            XUnmapEvent.nevent(this.address(), value);
            return this;
        }

        public Buffer window(@NativeType(value="Window") long value) {
            XUnmapEvent.nwindow(this.address(), value);
            return this;
        }

        public Buffer from_configure(int value) {
            XUnmapEvent.nfrom_configure(this.address(), value);
            return this;
        }
    }
}
