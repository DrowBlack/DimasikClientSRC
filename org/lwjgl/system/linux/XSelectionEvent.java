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

public class XSelectionEvent
extends Struct<XSelectionEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int TYPE;
    public static final int SERIAL;
    public static final int SEND_EVENT;
    public static final int DISPLAY;
    public static final int REQUESTOR;
    public static final int SELECTION;
    public static final int TARGET;
    public static final int PROPERTY;
    public static final int TIME;

    protected XSelectionEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected XSelectionEvent create(long address, @Nullable ByteBuffer container) {
        return new XSelectionEvent(address, container);
    }

    public XSelectionEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), XSelectionEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public int type() {
        return XSelectionEvent.ntype(this.address());
    }

    @NativeType(value="unsigned long")
    public long serial() {
        return XSelectionEvent.nserial(this.address());
    }

    @NativeType(value="Bool")
    public boolean send_event() {
        return XSelectionEvent.nsend_event(this.address()) != 0;
    }

    @NativeType(value="Display *")
    public long display() {
        return XSelectionEvent.ndisplay(this.address());
    }

    @NativeType(value="Window")
    public long requestor() {
        return XSelectionEvent.nrequestor(this.address());
    }

    @NativeType(value="Atom")
    public long selection() {
        return XSelectionEvent.nselection(this.address());
    }

    @NativeType(value="Atom")
    public long target() {
        return XSelectionEvent.ntarget(this.address());
    }

    @NativeType(value="Atom")
    public long property() {
        return XSelectionEvent.nproperty(this.address());
    }

    @NativeType(value="Time")
    public long time() {
        return XSelectionEvent.ntime(this.address());
    }

    public XSelectionEvent type(int value) {
        XSelectionEvent.ntype(this.address(), value);
        return this;
    }

    public XSelectionEvent serial(@NativeType(value="unsigned long") long value) {
        XSelectionEvent.nserial(this.address(), value);
        return this;
    }

    public XSelectionEvent send_event(@NativeType(value="Bool") boolean value) {
        XSelectionEvent.nsend_event(this.address(), value ? 1 : 0);
        return this;
    }

    public XSelectionEvent display(@NativeType(value="Display *") long value) {
        XSelectionEvent.ndisplay(this.address(), value);
        return this;
    }

    public XSelectionEvent requestor(@NativeType(value="Window") long value) {
        XSelectionEvent.nrequestor(this.address(), value);
        return this;
    }

    public XSelectionEvent selection(@NativeType(value="Atom") long value) {
        XSelectionEvent.nselection(this.address(), value);
        return this;
    }

    public XSelectionEvent target(@NativeType(value="Atom") long value) {
        XSelectionEvent.ntarget(this.address(), value);
        return this;
    }

    public XSelectionEvent property(@NativeType(value="Atom") long value) {
        XSelectionEvent.nproperty(this.address(), value);
        return this;
    }

    public XSelectionEvent time(@NativeType(value="Time") long value) {
        XSelectionEvent.ntime(this.address(), value);
        return this;
    }

    public XSelectionEvent set(int type, long serial, boolean send_event, long display, long requestor, long selection, long target, long property, long time) {
        this.type(type);
        this.serial(serial);
        this.send_event(send_event);
        this.display(display);
        this.requestor(requestor);
        this.selection(selection);
        this.target(target);
        this.property(property);
        this.time(time);
        return this;
    }

    public XSelectionEvent set(XSelectionEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static XSelectionEvent malloc() {
        return new XSelectionEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static XSelectionEvent calloc() {
        return new XSelectionEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static XSelectionEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new XSelectionEvent(MemoryUtil.memAddress(container), container);
    }

    public static XSelectionEvent create(long address) {
        return new XSelectionEvent(address, null);
    }

    @Nullable
    public static XSelectionEvent createSafe(long address) {
        return address == 0L ? null : new XSelectionEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(XSelectionEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = XSelectionEvent.__create(capacity, SIZEOF);
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
    public static XSelectionEvent mallocStack() {
        return XSelectionEvent.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XSelectionEvent callocStack() {
        return XSelectionEvent.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static XSelectionEvent mallocStack(MemoryStack stack) {
        return XSelectionEvent.malloc(stack);
    }

    @Deprecated
    public static XSelectionEvent callocStack(MemoryStack stack) {
        return XSelectionEvent.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return XSelectionEvent.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return XSelectionEvent.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return XSelectionEvent.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return XSelectionEvent.calloc(capacity, stack);
    }

    public static XSelectionEvent malloc(MemoryStack stack) {
        return new XSelectionEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static XSelectionEvent calloc(MemoryStack stack) {
        return new XSelectionEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
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

    public static long nrequestor(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)REQUESTOR);
    }

    public static long nselection(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)SELECTION);
    }

    public static long ntarget(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)TARGET);
    }

    public static long nproperty(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)PROPERTY);
    }

    public static long ntime(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)TIME);
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

    public static void nrequestor(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)REQUESTOR, value);
    }

    public static void nselection(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)SELECTION, value);
    }

    public static void ntarget(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)TARGET, value);
    }

    public static void nproperty(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)PROPERTY, value);
    }

    public static void ntime(long struct, long value) {
        MemoryUtil.memPutCLong(struct + (long)TIME, value);
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)DISPLAY));
    }

    static {
        Struct.Layout layout = XSelectionEvent.__struct(XSelectionEvent.__member(4), XSelectionEvent.__member(CLONG_SIZE), XSelectionEvent.__member(4), XSelectionEvent.__member(POINTER_SIZE), XSelectionEvent.__member(CLONG_SIZE), XSelectionEvent.__member(CLONG_SIZE), XSelectionEvent.__member(CLONG_SIZE), XSelectionEvent.__member(CLONG_SIZE), XSelectionEvent.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        TYPE = layout.offsetof(0);
        SERIAL = layout.offsetof(1);
        SEND_EVENT = layout.offsetof(2);
        DISPLAY = layout.offsetof(3);
        REQUESTOR = layout.offsetof(4);
        SELECTION = layout.offsetof(5);
        TARGET = layout.offsetof(6);
        PROPERTY = layout.offsetof(7);
        TIME = layout.offsetof(8);
    }

    public static class Buffer
    extends StructBuffer<XSelectionEvent, Buffer>
    implements NativeResource {
        private static final XSelectionEvent ELEMENT_FACTORY = XSelectionEvent.create(-1L);

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
        protected XSelectionEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public int type() {
            return XSelectionEvent.ntype(this.address());
        }

        @NativeType(value="unsigned long")
        public long serial() {
            return XSelectionEvent.nserial(this.address());
        }

        @NativeType(value="Bool")
        public boolean send_event() {
            return XSelectionEvent.nsend_event(this.address()) != 0;
        }

        @NativeType(value="Display *")
        public long display() {
            return XSelectionEvent.ndisplay(this.address());
        }

        @NativeType(value="Window")
        public long requestor() {
            return XSelectionEvent.nrequestor(this.address());
        }

        @NativeType(value="Atom")
        public long selection() {
            return XSelectionEvent.nselection(this.address());
        }

        @NativeType(value="Atom")
        public long target() {
            return XSelectionEvent.ntarget(this.address());
        }

        @NativeType(value="Atom")
        public long property() {
            return XSelectionEvent.nproperty(this.address());
        }

        @NativeType(value="Time")
        public long time() {
            return XSelectionEvent.ntime(this.address());
        }

        public Buffer type(int value) {
            XSelectionEvent.ntype(this.address(), value);
            return this;
        }

        public Buffer serial(@NativeType(value="unsigned long") long value) {
            XSelectionEvent.nserial(this.address(), value);
            return this;
        }

        public Buffer send_event(@NativeType(value="Bool") boolean value) {
            XSelectionEvent.nsend_event(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer display(@NativeType(value="Display *") long value) {
            XSelectionEvent.ndisplay(this.address(), value);
            return this;
        }

        public Buffer requestor(@NativeType(value="Window") long value) {
            XSelectionEvent.nrequestor(this.address(), value);
            return this;
        }

        public Buffer selection(@NativeType(value="Atom") long value) {
            XSelectionEvent.nselection(this.address(), value);
            return this;
        }

        public Buffer target(@NativeType(value="Atom") long value) {
            XSelectionEvent.ntarget(this.address(), value);
            return this;
        }

        public Buffer property(@NativeType(value="Atom") long value) {
            XSelectionEvent.nproperty(this.address(), value);
            return this;
        }

        public Buffer time(@NativeType(value="Time") long value) {
            XSelectionEvent.ntime(this.address(), value);
            return this;
        }
    }
}
