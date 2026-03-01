package org.lwjgl.system.linux;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.linux.EpollData;

@NativeType(value="struct epoll_event")
public class EpollEvent
extends Struct<EpollEvent>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int EVENTS;
    public static final int DATA;

    protected EpollEvent(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected EpollEvent create(long address, @Nullable ByteBuffer container) {
        return new EpollEvent(address, container);
    }

    public EpollEvent(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), EpollEvent.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="uint32_t")
    public int events() {
        return EpollEvent.nevents(this.address());
    }

    @NativeType(value="epoll_data_t")
    public EpollData data() {
        return EpollEvent.ndata(this.address());
    }

    public EpollEvent events(@NativeType(value="uint32_t") int value) {
        EpollEvent.nevents(this.address(), value);
        return this;
    }

    public EpollEvent data(@NativeType(value="epoll_data_t") EpollData value) {
        EpollEvent.ndata(this.address(), value);
        return this;
    }

    public EpollEvent data(Consumer<EpollData> consumer) {
        consumer.accept(this.data());
        return this;
    }

    public EpollEvent set(int events, EpollData data) {
        this.events(events);
        this.data(data);
        return this;
    }

    public EpollEvent set(EpollEvent src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static EpollEvent malloc() {
        return new EpollEvent(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static EpollEvent calloc() {
        return new EpollEvent(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static EpollEvent create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new EpollEvent(MemoryUtil.memAddress(container), container);
    }

    public static EpollEvent create(long address) {
        return new EpollEvent(address, null);
    }

    @Nullable
    public static EpollEvent createSafe(long address) {
        return address == 0L ? null : new EpollEvent(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(EpollEvent.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = EpollEvent.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static EpollEvent malloc(MemoryStack stack) {
        return new EpollEvent(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static EpollEvent calloc(MemoryStack stack) {
        return new EpollEvent(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int nevents(long struct) {
        return UNSAFE.getInt(null, struct + (long)EVENTS);
    }

    public static EpollData ndata(long struct) {
        return EpollData.create(struct + (long)DATA);
    }

    public static void nevents(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)EVENTS, value);
    }

    public static void ndata(long struct, EpollData value) {
        MemoryUtil.memCopy(value.address(), struct + (long)DATA, EpollData.SIZEOF);
    }

    static {
        Struct.Layout layout = EpollEvent.__struct(EpollEvent.__member(4), EpollEvent.__member(EpollData.SIZEOF, EpollData.ALIGNOF));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        EVENTS = layout.offsetof(0);
        DATA = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<EpollEvent, Buffer>
    implements NativeResource {
        private static final EpollEvent ELEMENT_FACTORY = EpollEvent.create(-1L);

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
        protected EpollEvent getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="uint32_t")
        public int events() {
            return EpollEvent.nevents(this.address());
        }

        @NativeType(value="epoll_data_t")
        public EpollData data() {
            return EpollEvent.ndata(this.address());
        }

        public Buffer events(@NativeType(value="uint32_t") int value) {
            EpollEvent.nevents(this.address(), value);
            return this;
        }

        public Buffer data(@NativeType(value="epoll_data_t") EpollData value) {
            EpollEvent.ndata(this.address(), value);
            return this;
        }

        public Buffer data(Consumer<EpollData> consumer) {
            consumer.accept(this.data());
            return this;
        }
    }
}
