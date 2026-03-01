package org.lwjgl.system.macosx;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class CGEventTapInformation
extends Struct<CGEventTapInformation>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int EVENTTAPID;
    public static final int TAPPOINT;
    public static final int OPTIONS;
    public static final int EVENTSOFINTEREST;
    public static final int TAPPINGPROCESS;
    public static final int PROCESSBEINGTAPPED;
    public static final int ENABLED;
    public static final int MINUSECLATENCY;
    public static final int AVGUSECLATENCY;
    public static final int MAXUSECLATENCY;

    protected CGEventTapInformation(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CGEventTapInformation create(long address, @Nullable ByteBuffer container) {
        return new CGEventTapInformation(address, container);
    }

    public CGEventTapInformation(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), CGEventTapInformation.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="uint32_t")
    public int eventTapID() {
        return CGEventTapInformation.neventTapID(this.address());
    }

    @NativeType(value="CGEventTapLocation")
    public int tapPoint() {
        return CGEventTapInformation.ntapPoint(this.address());
    }

    @NativeType(value="CGEventTapOptions")
    public int options() {
        return CGEventTapInformation.noptions(this.address());
    }

    @NativeType(value="CGEventMask")
    public long eventsOfInterest() {
        return CGEventTapInformation.neventsOfInterest(this.address());
    }

    @NativeType(value="pid_t")
    public long tappingProcess() {
        return CGEventTapInformation.ntappingProcess(this.address());
    }

    @NativeType(value="pid_t")
    public long processBeingTapped() {
        return CGEventTapInformation.nprocessBeingTapped(this.address());
    }

    @NativeType(value="bool")
    public boolean enabled() {
        return CGEventTapInformation.nenabled(this.address());
    }

    public float minUsecLatency() {
        return CGEventTapInformation.nminUsecLatency(this.address());
    }

    public float avgUsecLatency() {
        return CGEventTapInformation.navgUsecLatency(this.address());
    }

    public float maxUsecLatency() {
        return CGEventTapInformation.nmaxUsecLatency(this.address());
    }

    public static CGEventTapInformation malloc() {
        return new CGEventTapInformation(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static CGEventTapInformation calloc() {
        return new CGEventTapInformation(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static CGEventTapInformation create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new CGEventTapInformation(MemoryUtil.memAddress(container), container);
    }

    public static CGEventTapInformation create(long address) {
        return new CGEventTapInformation(address, null);
    }

    @Nullable
    public static CGEventTapInformation createSafe(long address) {
        return address == 0L ? null : new CGEventTapInformation(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(CGEventTapInformation.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = CGEventTapInformation.__create(capacity, SIZEOF);
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
    public static CGEventTapInformation mallocStack() {
        return CGEventTapInformation.malloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static CGEventTapInformation callocStack() {
        return CGEventTapInformation.calloc(MemoryStack.stackGet());
    }

    @Deprecated
    public static CGEventTapInformation mallocStack(MemoryStack stack) {
        return CGEventTapInformation.malloc(stack);
    }

    @Deprecated
    public static CGEventTapInformation callocStack(MemoryStack stack) {
        return CGEventTapInformation.calloc(stack);
    }

    @Deprecated
    public static Buffer mallocStack(int capacity) {
        return CGEventTapInformation.malloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer callocStack(int capacity) {
        return CGEventTapInformation.calloc(capacity, MemoryStack.stackGet());
    }

    @Deprecated
    public static Buffer mallocStack(int capacity, MemoryStack stack) {
        return CGEventTapInformation.malloc(capacity, stack);
    }

    @Deprecated
    public static Buffer callocStack(int capacity, MemoryStack stack) {
        return CGEventTapInformation.calloc(capacity, stack);
    }

    public static CGEventTapInformation malloc(MemoryStack stack) {
        return new CGEventTapInformation(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static CGEventTapInformation calloc(MemoryStack stack) {
        return new CGEventTapInformation(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static int neventTapID(long struct) {
        return UNSAFE.getInt(null, struct + (long)EVENTTAPID);
    }

    public static int ntapPoint(long struct) {
        return UNSAFE.getInt(null, struct + (long)TAPPOINT);
    }

    public static int noptions(long struct) {
        return UNSAFE.getInt(null, struct + (long)OPTIONS);
    }

    public static long neventsOfInterest(long struct) {
        return UNSAFE.getLong(null, struct + (long)EVENTSOFINTEREST);
    }

    public static long ntappingProcess(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)TAPPINGPROCESS);
    }

    public static long nprocessBeingTapped(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)PROCESSBEINGTAPPED);
    }

    public static boolean nenabled(long struct) {
        return UNSAFE.getByte(null, struct + (long)ENABLED) != 0;
    }

    public static float nminUsecLatency(long struct) {
        return UNSAFE.getFloat(null, struct + (long)MINUSECLATENCY);
    }

    public static float navgUsecLatency(long struct) {
        return UNSAFE.getFloat(null, struct + (long)AVGUSECLATENCY);
    }

    public static float nmaxUsecLatency(long struct) {
        return UNSAFE.getFloat(null, struct + (long)MAXUSECLATENCY);
    }

    static {
        Struct.Layout layout = CGEventTapInformation.__struct(CGEventTapInformation.__member(4), CGEventTapInformation.__member(4), CGEventTapInformation.__member(4), CGEventTapInformation.__member(8), CGEventTapInformation.__member(POINTER_SIZE), CGEventTapInformation.__member(POINTER_SIZE), CGEventTapInformation.__member(1), CGEventTapInformation.__member(4), CGEventTapInformation.__member(4), CGEventTapInformation.__member(4));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        EVENTTAPID = layout.offsetof(0);
        TAPPOINT = layout.offsetof(1);
        OPTIONS = layout.offsetof(2);
        EVENTSOFINTEREST = layout.offsetof(3);
        TAPPINGPROCESS = layout.offsetof(4);
        PROCESSBEINGTAPPED = layout.offsetof(5);
        ENABLED = layout.offsetof(6);
        MINUSECLATENCY = layout.offsetof(7);
        AVGUSECLATENCY = layout.offsetof(8);
        MAXUSECLATENCY = layout.offsetof(9);
    }

    public static class Buffer
    extends StructBuffer<CGEventTapInformation, Buffer>
    implements NativeResource {
        private static final CGEventTapInformation ELEMENT_FACTORY = CGEventTapInformation.create(-1L);

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
        protected CGEventTapInformation getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="uint32_t")
        public int eventTapID() {
            return CGEventTapInformation.neventTapID(this.address());
        }

        @NativeType(value="CGEventTapLocation")
        public int tapPoint() {
            return CGEventTapInformation.ntapPoint(this.address());
        }

        @NativeType(value="CGEventTapOptions")
        public int options() {
            return CGEventTapInformation.noptions(this.address());
        }

        @NativeType(value="CGEventMask")
        public long eventsOfInterest() {
            return CGEventTapInformation.neventsOfInterest(this.address());
        }

        @NativeType(value="pid_t")
        public long tappingProcess() {
            return CGEventTapInformation.ntappingProcess(this.address());
        }

        @NativeType(value="pid_t")
        public long processBeingTapped() {
            return CGEventTapInformation.nprocessBeingTapped(this.address());
        }

        @NativeType(value="bool")
        public boolean enabled() {
            return CGEventTapInformation.nenabled(this.address());
        }

        public float minUsecLatency() {
            return CGEventTapInformation.nminUsecLatency(this.address());
        }

        public float avgUsecLatency() {
            return CGEventTapInformation.navgUsecLatency(this.address());
        }

        public float maxUsecLatency() {
            return CGEventTapInformation.nmaxUsecLatency(this.address());
        }
    }
}
