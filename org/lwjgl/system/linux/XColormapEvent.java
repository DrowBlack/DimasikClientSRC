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

public class XColormapEvent
extends Struct<XColormapEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int WINDOW;
    public static final int COLORMAP;
    public static final int NEW;
    public static final int STATE;

    protected XColormapEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XColormapEvent create(long address, @Nullable ByteBuffer container) {
        return new XColormapEvent(address, container);
    }

    public XColormapEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XColormapEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XColormapEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XColormapEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XColormapEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XColormapEvent.ndisplay(this.address());
    }

    @NativeType(value="Window")
    public long window() {
        return XColormapEvent.nwindow(this.address());
    }

    @NativeType(value="Colormap")
    public long colormap() {
        return XColormapEvent.ncolormap(this.address());
    }

    public int new$() {
        return XColormapEvent.nnew$(this.address());
    }

    public int state() {
        return XColormapEvent.nstate(this.address());
    }

    public XColormapEvent type(int value) {
        XColormapEvent.ntype(this.address(), value);
        return this;
    }

    public XColormapEvent serial(@NativeType(value="unsigned long") long value) {
        XColormapEvent.nserial(this.address(), value);
        return this;
    }

    public XColormapEvent send_event(@NativeType(value="Bool") boolean value) {
        XColormapEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XColormapEvent display(@NativeType(value="Display *") long value) {
        XColormapEvent.ndisplay(this.address(), value);
        return this;
    }

    public XColormapEvent window(@NativeType(value="Window") long value) {
        XColormapEvent.nwindow(this.address(), value);
        return this;
    }

    public XColormapEvent colormap(@NativeType(value="Colormap") long value) {
        XColormapEvent.ncolormap(this.address(), value);
        return this;
    }

    public XColormapEvent new$(int value) {
        XColormapEvent.nnew$(this.address(), value);
        return this;
    }

    public XColormapEvent state(int value) {
        XColormapEvent.nstate(this.address(), value);
        return this;
    }

    public XColormapEvent set(int type, long serial, boolean send_event, long display, long window, long colormap, int new$, int state) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.window(window);
        this.colormap(colormap);
        this.new$(new$);
        this.state(state);
        return this;
    }

    public XColormapEvent set(XColormapEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XColormapEvent malloc() {
        return new XColormapEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XColormapEvent calloc() {
        return new XColormapEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XColormapEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XColormapEvent(MemoryUtil.memAddress(container), container);
    }

    public static XColormapEvent create(long address) {
        return new XColormapEvent(address, null);
    }

    @Nullable
    public static XColormapEvent createSafe(long address) {
        return address == 0L ? null : new XColormapEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XColormapEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XColormapEvent.__create(capacity, SIZEOF);
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
    public static XColormapEvent mallocStack() {
        return XColormapEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XColormapEvent callocStack() {
        return XColormapEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XColormapEvent mallocStack(MemoryStack stack) {
        return XColormapEvent.malloc(stack);
    }

    @Deprecated
    public static XColormapEvent callocStack(MemoryStack stack) {
        return XColormapEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XColormapEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XColormapEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XColormapEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XColormapEvent.calloc(capacity, stack);
    }

    public static XColormapEvent malloc(MemoryStack stack) {
        return new XColormapEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XColormapEvent calloc(MemoryStack stack) {
        return new XColormapEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static long ncolormap(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)COLORMAP);
    }

    public static int nnew$(long struct) {
        return UNSAFE.getInt(null, struct + (long)NEW);
    }

    public static int nstate(long struct) {
        return UNSAFE.getInt(null, struct + (long)STATE);
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

    public static void ncolormap(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)COLORMAP, value);
    }

    public static void nnew$(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)NEW, value);
    }

    public static void nstate(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)STATE, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XColormapEvent.__struct(XColormapEvent.__member(4), XColormapEvent.__member(CLONG_SIZE), XColormapEvent.__member(4), XColormapEvent.__member(POINTER_SIZE), XColormapEvent.__member(CLONG_SIZE), XColormapEvent.__member(CLONG_SIZE), XColormapEvent.__member(4), XColormapEvent.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        WINDOW = layout.offsetof(4);
        COLORMAP = layout.offsetof(5);
        NEW = layout.offsetof(6);
        STATE = layout.offsetof(7);
    }

    public static class Buffer
    extends StructBuffer<XColormapEvent, Buffer>
    implements NativeResource {
        private static final XColormapEvent ELEMENT_FACTORY = XColormapEvent.create(-1L);

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
        protected XColormapEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XColormapEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XColormapEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XColormapEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XColormapEvent.ndisplay(this.address());
        }

        @NativeType(value="Window")
        public long window() {
            return XColormapEvent.nwindow(this.address());
        }

        @NativeType(value="Colormap")
        public long colormap() {
            return XColormapEvent.ncolormap(this.address());
        }

        public int new$() {
            return XColormapEvent.nnew$(this.address());
        }

        public int state() {
            return XColormapEvent.nstate(this.address());
        }

        public Buffer type(int value) {
            XColormapEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XColormapEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XColormapEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XColormapEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer window(@NativeType(value="Window") long value) {
            XColormapEvent.nwindow(this.address(), value);
            return this;
        }

        public Buffer colormap(@NativeType(value="Colormap") long value) {
            XColormapEvent.ncolormap(this.address(), value);
            return this;
        }

        public Buffer new$(int value) {
            XColormapEvent.nnew$(this.address(), value);
            return this;
        }

        public Buffer state(int value) {
            XColormapEvent.nstate(this.address(), value);
            return this;
        }
    }
}
