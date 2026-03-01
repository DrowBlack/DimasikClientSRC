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

public class XGenericEventCookie
extends Struct<XGenericEventCookie>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int EXTENSION;
    public static final int EVTYPE;
    public static final int COOKIE;
    public static final int DATA;

    protected XGenericEventCookie(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XGenericEventCookie create(long address, @Nullable ByteBuffer container) {
        return new XGenericEventCookie(address, container);
    }

    public XGenericEventCookie(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XGenericEventCookie.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XGenericEventCookie.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XGenericEventCookie.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XGenericEventCookie.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XGenericEventCookie.ndisplay(this.address());
    }

    public int extension() {
        return XGenericEventCookie.nextension(this.address());
    }

    public int evtype() {
        return XGenericEventCookie.nevtype(this.address());
    }

    @NativeType(value="unsigned int")
    public int cookie() {
        return XGenericEventCookie.ncookie(this.address());
    }

    @NativeType(value="void *")
    public ByteBuffer data(int capacity) {
        return XGenericEventCookie.ndata(this.address(), capacity);
    }

    public XGenericEventCookie type(int value) {
        XGenericEventCookie.ntype(this.address(), value);
        return this;
    }

    public XGenericEventCookie serial(@NativeType(value="unsigned long") long value) {
        XGenericEventCookie.nserial(this.address(), value);
        return this;
    }

    public XGenericEventCookie send_event(@NativeType(value="Bool") boolean value) {
        XGenericEventCookie.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XGenericEventCookie display(@NativeType(value="Display *") long value) {
        XGenericEventCookie.ndisplay(this.address(), value);
        return this;
    }

    public XGenericEventCookie extension(int value) {
        XGenericEventCookie.nextension(this.address(), value);
        return this;
    }

    public XGenericEventCookie evtype(int value) {
        XGenericEventCookie.nevtype(this.address(), value);
        return this;
    }

    public XGenericEventCookie cookie(@NativeType(value="unsigned int") int value) {
        XGenericEventCookie.ncookie(this.address(), value);
        return this;
    }

    public XGenericEventCookie data(@NativeType(value="void *") ByteBuffer value) {
        XGenericEventCookie.ndata(this.address(), value);
        return this;
    }

    public XGenericEventCookie set(int type, long serial, boolean send_event, long display, int extension, int evtype, int cookie, ByteBuffer data) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.extension(extension);
        this.evtype(evtype);
        this.cookie(cookie);
        this.data(data);
        return this;
    }

    public XGenericEventCookie set(XGenericEventCookie src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XGenericEventCookie malloc() {
        return new XGenericEventCookie(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XGenericEventCookie calloc() {
        return new XGenericEventCookie(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XGenericEventCookie create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XGenericEventCookie(MemoryUtil.memAddress(container), container);
    }

    public static XGenericEventCookie create(long address) {
        return new XGenericEventCookie(address, null);
    }

    @Nullable
    public static XGenericEventCookie createSafe(long address) {
        return address == 0L ? null : new XGenericEventCookie(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XGenericEventCookie.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XGenericEventCookie.__create(capacity, SIZEOF);
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
    public static XGenericEventCookie mallocStack() {
        return XGenericEventCookie.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XGenericEventCookie callocStack() {
        return XGenericEventCookie.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XGenericEventCookie mallocStack(MemoryStack stack) {
        return XGenericEventCookie.malloc(stack);
    }

    @Deprecated
    public static XGenericEventCookie callocStack(MemoryStack stack) {
        return XGenericEventCookie.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XGenericEventCookie.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XGenericEventCookie.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XGenericEventCookie.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XGenericEventCookie.calloc(capacity, stack);
    }

    public static XGenericEventCookie malloc(MemoryStack stack) {
        return new XGenericEventCookie(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XGenericEventCookie calloc(MemoryStack stack) {
        return new XGenericEventCookie(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static int nextension(long struct) {
        return UNSAFE.getInt(null, struct + (long)EXTENSION);
    }

    public static int nevtype(long struct) {
        return UNSAFE.getInt(null, struct + (long)EVTYPE);
    }

    public static int ncookie(long struct) {
        return UNSAFE.getInt(null, struct + (long)COOKIE);
    }

    public static ByteBuffer ndata(long struct, int capacity) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)DATA), capacity);
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

    public static void nextension(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)EXTENSION, value);
    }

    public static void nevtype(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)EVTYPE, value);
    }

    public static void ncookie(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)COOKIE, value);
    }

    public static void ndata(long struct, ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)DATA, MemoryUtil.memAddress(value));
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DATA));
    }

    static {
        Struct.Layout layout = XGenericEventCookie.__struct(XGenericEventCookie.__member(4), XGenericEventCookie.__member(CLONG_SIZE), XGenericEventCookie.__member(4), XGenericEventCookie.__member(POINTER_SIZE), XGenericEventCookie.__member(4), XGenericEventCookie.__member(4), XGenericEventCookie.__member(4), XGenericEventCookie.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        EXTENSION = layout.offsetof(4);
        EVTYPE = layout.offsetof(5);
        COOKIE = layout.offsetof(6);
        DATA = layout.offsetof(7);
    }

    public static class Buffer
    extends StructBuffer<XGenericEventCookie, Buffer>
    implements NativeResource {
        private static final XGenericEventCookie ELEMENT_FACTORY = XGenericEventCookie.create(-1L);

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
        protected XGenericEventCookie getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XGenericEventCookie.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XGenericEventCookie.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XGenericEventCookie.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XGenericEventCookie.ndisplay(this.address());
        }

        public int extension() {
            return XGenericEventCookie.nextension(this.address());
        }

        public int evtype() {
            return XGenericEventCookie.nevtype(this.address());
        }

        @NativeType(value="unsigned int")
        public int cookie() {
            return XGenericEventCookie.ncookie(this.address());
        }

        @NativeType(value="void *")
        public ByteBuffer data(int capacity) {
            return XGenericEventCookie.ndata(this.address(), capacity);
        }

        public Buffer type(int value) {
            XGenericEventCookie.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XGenericEventCookie.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XGenericEventCookie.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XGenericEventCookie.ndisplay(this.address(), value);
            return this;
        }

        public Buffer extension(int value) {
            XGenericEventCookie.nextension(this.address(), value);
            return this;
        }

        public Buffer evtype(int value) {
            XGenericEventCookie.nevtype(this.address(), value);
            return this;
        }

        public Buffer cookie(@NativeType(value="unsigned int") int value) {
            XGenericEventCookie.ncookie(this.address(), value);
            return this;
        }

        public Buffer data(@NativeType(value="void *") ByteBuffer value) {
            XGenericEventCookie.ndata(this.address(), value);
            return this;
        }
    }
}
