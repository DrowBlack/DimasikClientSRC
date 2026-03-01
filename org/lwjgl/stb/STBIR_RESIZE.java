package org.lwjgl.stb;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class STBIR_RESIZE
extends Struct<STBIR_RESIZE>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int USER_DATA;
    public static final int INPUT_PIXELS;
    public static final int INPUT_W;
    public static final int INPUT_H;
    public static final int INPUT_S0;
    public static final int INPUT_T0;
    public static final int INPUT_S1;
    public static final int INPUT_T1;
    public static final int INPUT_CB;
    public static final int OUTPUT_PIXELS;
    public static final int OUTPUT_W;
    public static final int OUTPUT_H;
    public static final int OUTPUT_SUBX;
    public static final int OUTPUT_SUBY;
    public static final int OUTPUT_SUBW;
    public static final int OUTPUT_SUBH;
    public static final int OUTPUT_CB;
    public static final int INPUT_STRIDE_IN_BYTES;
    public static final int OUTPUT_STRIDE_IN_BYTES;
    public static final int SPLITS;
    public static final int FAST_ALPHA;
    public static final int NEEDS_REBUILD;
    public static final int CALLED_ALLOC;
    public static final int INPUT_PIXEL_LAYOUT_PUBLIC;
    public static final int OUTPUT_PIXEL_LAYOUT_PUBLIC;
    public static final int INPUT_DATA_TYPE;
    public static final int OUTPUT_DATA_TYPE;
    public static final int HORIZONTAL_FILTER;
    public static final int VERTICAL_FILTER;
    public static final int HORIZONTAL_EDGE;
    public static final int VERTICAL_EDGE;
    public static final int HORIZONTAL_FILTER_KERNEL;
    public static final int HORIZONTAL_FILTER_SUPPORT;
    public static final int VERTICAL_FILTER_KERNEL;
    public static final int VERTICAL_FILTER_SUPPORT;
    public static final int SAMPLERS;

    protected STBIR_RESIZE(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected STBIR_RESIZE create(long address, @Nullable ByteBuffer container) {
        return new STBIR_RESIZE(address, container);
    }

    public STBIR_RESIZE(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), STBIR_RESIZE.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="void *")
    public long user_data() {
        return STBIR_RESIZE.nuser_data(this.address());
    }

    @NativeType(value="void const *")
    public ByteBuffer input_pixels(int capacity) {
        return STBIR_RESIZE.ninput_pixels(this.address(), capacity);
    }

    public int input_w() {
        return STBIR_RESIZE.ninput_w(this.address());
    }

    public int input_h() {
        return STBIR_RESIZE.ninput_h(this.address());
    }

    public double input_s0() {
        return STBIR_RESIZE.ninput_s0(this.address());
    }

    public double input_t0() {
        return STBIR_RESIZE.ninput_t0(this.address());
    }

    public double input_s1() {
        return STBIR_RESIZE.ninput_s1(this.address());
    }

    public double input_t1() {
        return STBIR_RESIZE.ninput_t1(this.address());
    }

    @Nullable
    @NativeType(value="stbir_input_callback **")
    public PointerBuffer input_cb(int capacity) {
        return STBIR_RESIZE.ninput_cb(this.address(), capacity);
    }

    @Nullable
    @NativeType(value="void *")
    public ByteBuffer output_pixels(int capacity) {
        return STBIR_RESIZE.noutput_pixels(this.address(), capacity);
    }

    public int output_w() {
        return STBIR_RESIZE.noutput_w(this.address());
    }

    public int output_h() {
        return STBIR_RESIZE.noutput_h(this.address());
    }

    public int output_subx() {
        return STBIR_RESIZE.noutput_subx(this.address());
    }

    public int output_suby() {
        return STBIR_RESIZE.noutput_suby(this.address());
    }

    public int output_subw() {
        return STBIR_RESIZE.noutput_subw(this.address());
    }

    public int output_subh() {
        return STBIR_RESIZE.noutput_subh(this.address());
    }

    @Nullable
    @NativeType(value="stbir_output_callback **")
    public PointerBuffer output_cb(int capacity) {
        return STBIR_RESIZE.noutput_cb(this.address(), capacity);
    }

    public int input_stride_in_bytes() {
        return STBIR_RESIZE.ninput_stride_in_bytes(this.address());
    }

    public int output_stride_in_bytes() {
        return STBIR_RESIZE.noutput_stride_in_bytes(this.address());
    }

    public int splits() {
        return STBIR_RESIZE.nsplits(this.address());
    }

    @NativeType(value="int")
    public boolean fast_alpha() {
        return STBIR_RESIZE.nfast_alpha(this.address()) != 0;
    }

    @NativeType(value="int")
    public boolean needs_rebuild() {
        return STBIR_RESIZE.nneeds_rebuild(this.address()) != 0;
    }

    @NativeType(value="int")
    public boolean called_alloc() {
        return STBIR_RESIZE.ncalled_alloc(this.address()) != 0;
    }

    @NativeType(value="stbir_pixel_layout")
    public int input_pixel_layout_public() {
        return STBIR_RESIZE.ninput_pixel_layout_public(this.address());
    }

    @NativeType(value="stbir_pixel_layout")
    public int output_pixel_layout_public() {
        return STBIR_RESIZE.noutput_pixel_layout_public(this.address());
    }

    @NativeType(value="stbir_datatype")
    public int input_data_type() {
        return STBIR_RESIZE.ninput_data_type(this.address());
    }

    @NativeType(value="stbir_datatype")
    public int output_data_type() {
        return STBIR_RESIZE.noutput_data_type(this.address());
    }

    @NativeType(value="stbir_filter")
    public int horizontal_filter() {
        return STBIR_RESIZE.nhorizontal_filter(this.address());
    }

    @NativeType(value="stbir_filter")
    public int vertical_filter() {
        return STBIR_RESIZE.nvertical_filter(this.address());
    }

    @NativeType(value="stbir_edge")
    public int horizontal_edge() {
        return STBIR_RESIZE.nhorizontal_edge(this.address());
    }

    @NativeType(value="stbir_edge")
    public int vertical_edge() {
        return STBIR_RESIZE.nvertical_edge(this.address());
    }

    @Nullable
    @NativeType(value="stbir__kernel_callback **")
    public PointerBuffer horizontal_filter_kernel(int capacity) {
        return STBIR_RESIZE.nhorizontal_filter_kernel(this.address(), capacity);
    }

    @Nullable
    @NativeType(value="stbir__support_callback **")
    public PointerBuffer horizontal_filter_support(int capacity) {
        return STBIR_RESIZE.nhorizontal_filter_support(this.address(), capacity);
    }

    @Nullable
    @NativeType(value="stbir__kernel_callback **")
    public PointerBuffer vertical_filter_kernel(int capacity) {
        return STBIR_RESIZE.nvertical_filter_kernel(this.address(), capacity);
    }

    @Nullable
    @NativeType(value="stbir__support_callback **")
    public PointerBuffer vertical_filter_support(int capacity) {
        return STBIR_RESIZE.nvertical_filter_support(this.address(), capacity);
    }

    @NativeType(value="stbir__info *")
    public long samplers() {
        return STBIR_RESIZE.nsamplers(this.address());
    }

    public STBIR_RESIZE user_data(@NativeType(value="void *") long value) {
        STBIR_RESIZE.nuser_data(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_pixels(@NativeType(value="void const *") ByteBuffer value) {
        STBIR_RESIZE.ninput_pixels(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_w(int value) {
        STBIR_RESIZE.ninput_w(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_h(int value) {
        STBIR_RESIZE.ninput_h(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_s0(double value) {
        STBIR_RESIZE.ninput_s0(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_t0(double value) {
        STBIR_RESIZE.ninput_t0(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_s1(double value) {
        STBIR_RESIZE.ninput_s1(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_t1(double value) {
        STBIR_RESIZE.ninput_t1(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_cb(@Nullable @NativeType(value="stbir_input_callback **") PointerBuffer value) {
        STBIR_RESIZE.ninput_cb(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_pixels(@Nullable @NativeType(value="void *") ByteBuffer value) {
        STBIR_RESIZE.noutput_pixels(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_w(int value) {
        STBIR_RESIZE.noutput_w(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_h(int value) {
        STBIR_RESIZE.noutput_h(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_subx(int value) {
        STBIR_RESIZE.noutput_subx(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_suby(int value) {
        STBIR_RESIZE.noutput_suby(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_subw(int value) {
        STBIR_RESIZE.noutput_subw(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_subh(int value) {
        STBIR_RESIZE.noutput_subh(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_cb(@Nullable @NativeType(value="stbir_output_callback **") PointerBuffer value) {
        STBIR_RESIZE.noutput_cb(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_stride_in_bytes(int value) {
        STBIR_RESIZE.ninput_stride_in_bytes(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_stride_in_bytes(int value) {
        STBIR_RESIZE.noutput_stride_in_bytes(this.address(), value);
        return this;
    }

    public STBIR_RESIZE splits(int value) {
        STBIR_RESIZE.nsplits(this.address(), value);
        return this;
    }

    public STBIR_RESIZE fast_alpha(@NativeType(value="int") boolean value) {
        STBIR_RESIZE.nfast_alpha(this.address(), value ? 1 : 0);
        return this;
    }

    public STBIR_RESIZE needs_rebuild(@NativeType(value="int") boolean value) {
        STBIR_RESIZE.nneeds_rebuild(this.address(), value ? 1 : 0);
        return this;
    }

    public STBIR_RESIZE called_alloc(@NativeType(value="int") boolean value) {
        STBIR_RESIZE.ncalled_alloc(this.address(), value ? 1 : 0);
        return this;
    }

    public STBIR_RESIZE input_pixel_layout_public(@NativeType(value="stbir_pixel_layout") int value) {
        STBIR_RESIZE.ninput_pixel_layout_public(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_pixel_layout_public(@NativeType(value="stbir_pixel_layout") int value) {
        STBIR_RESIZE.noutput_pixel_layout_public(this.address(), value);
        return this;
    }

    public STBIR_RESIZE input_data_type(@NativeType(value="stbir_datatype") int value) {
        STBIR_RESIZE.ninput_data_type(this.address(), value);
        return this;
    }

    public STBIR_RESIZE output_data_type(@NativeType(value="stbir_datatype") int value) {
        STBIR_RESIZE.noutput_data_type(this.address(), value);
        return this;
    }

    public STBIR_RESIZE horizontal_filter(@NativeType(value="stbir_filter") int value) {
        STBIR_RESIZE.nhorizontal_filter(this.address(), value);
        return this;
    }

    public STBIR_RESIZE vertical_filter(@NativeType(value="stbir_filter") int value) {
        STBIR_RESIZE.nvertical_filter(this.address(), value);
        return this;
    }

    public STBIR_RESIZE horizontal_edge(@NativeType(value="stbir_edge") int value) {
        STBIR_RESIZE.nhorizontal_edge(this.address(), value);
        return this;
    }

    public STBIR_RESIZE vertical_edge(@NativeType(value="stbir_edge") int value) {
        STBIR_RESIZE.nvertical_edge(this.address(), value);
        return this;
    }

    public STBIR_RESIZE horizontal_filter_kernel(@Nullable @NativeType(value="stbir__kernel_callback **") PointerBuffer value) {
        STBIR_RESIZE.nhorizontal_filter_kernel(this.address(), value);
        return this;
    }

    public STBIR_RESIZE horizontal_filter_support(@Nullable @NativeType(value="stbir__support_callback **") PointerBuffer value) {
        STBIR_RESIZE.nhorizontal_filter_support(this.address(), value);
        return this;
    }

    public STBIR_RESIZE vertical_filter_kernel(@Nullable @NativeType(value="stbir__kernel_callback **") PointerBuffer value) {
        STBIR_RESIZE.nvertical_filter_kernel(this.address(), value);
        return this;
    }

    public STBIR_RESIZE vertical_filter_support(@Nullable @NativeType(value="stbir__support_callback **") PointerBuffer value) {
        STBIR_RESIZE.nvertical_filter_support(this.address(), value);
        return this;
    }

    public STBIR_RESIZE samplers(@NativeType(value="stbir__info *") long value) {
        STBIR_RESIZE.nsamplers(this.address(), value);
        return this;
    }

    public STBIR_RESIZE set(long user_data, ByteBuffer input_pixels, int input_w, int input_h, double input_s0, double input_t0, double input_s1, double input_t1, @Nullable PointerBuffer input_cb, @Nullable ByteBuffer output_pixels, int output_w, int output_h, int output_subx, int output_suby, int output_subw, int output_subh, @Nullable PointerBuffer output_cb, int input_stride_in_bytes, int output_stride_in_bytes, int splits, boolean fast_alpha, boolean needs_rebuild, boolean called_alloc, int input_pixel_layout_public, int output_pixel_layout_public, int input_data_type, int output_data_type, int horizontal_filter, int vertical_filter, int horizontal_edge, int vertical_edge, @Nullable PointerBuffer horizontal_filter_kernel, @Nullable PointerBuffer horizontal_filter_support, @Nullable PointerBuffer vertical_filter_kernel, @Nullable PointerBuffer vertical_filter_support, long samplers) {
        this.user_data(user_data);
        this.input_pixels(input_pixels);
        this.input_w(input_w);
        this.input_h(input_h);
        this.input_s0(input_s0);
        this.input_t0(input_t0);
        this.input_s1(input_s1);
        this.input_t1(input_t1);
        this.input_cb(input_cb);
        this.output_pixels(output_pixels);
        this.output_w(output_w);
        this.output_h(output_h);
        this.output_subx(output_subx);
        this.output_suby(output_suby);
        this.output_subw(output_subw);
        this.output_subh(output_subh);
        this.output_cb(output_cb);
        this.input_stride_in_bytes(input_stride_in_bytes);
        this.output_stride_in_bytes(output_stride_in_bytes);
        this.splits(splits);
        this.fast_alpha(fast_alpha);
        this.needs_rebuild(needs_rebuild);
        this.called_alloc(called_alloc);
        this.input_pixel_layout_public(input_pixel_layout_public);
        this.output_pixel_layout_public(output_pixel_layout_public);
        this.input_data_type(input_data_type);
        this.output_data_type(output_data_type);
        this.horizontal_filter(horizontal_filter);
        this.vertical_filter(vertical_filter);
        this.horizontal_edge(horizontal_edge);
        this.vertical_edge(vertical_edge);
        this.horizontal_filter_kernel(horizontal_filter_kernel);
        this.horizontal_filter_support(horizontal_filter_support);
        this.vertical_filter_kernel(vertical_filter_kernel);
        this.vertical_filter_support(vertical_filter_support);
        this.samplers(samplers);
        return this;
    }

    public STBIR_RESIZE set(STBIR_RESIZE src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static STBIR_RESIZE malloc() {
        return new STBIR_RESIZE(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static STBIR_RESIZE calloc() {
        return new STBIR_RESIZE(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static STBIR_RESIZE create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new STBIR_RESIZE(MemoryUtil.memAddress(container), container);
    }

    public static STBIR_RESIZE create(long address) {
        return new STBIR_RESIZE(address, null);
    }

    @Nullable
    public static STBIR_RESIZE createSafe(long address) {
        return address == 0L ? null : new STBIR_RESIZE(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(STBIR_RESIZE.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = STBIR_RESIZE.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static STBIR_RESIZE malloc(MemoryStack stack) {
        return new STBIR_RESIZE(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static STBIR_RESIZE calloc(MemoryStack stack) {
        return new STBIR_RESIZE(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static long nuser_data(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)USER_DATA);
    }

    public static ByteBuffer ninput_pixels(long struct, int capacity) {
        return MemoryUtil.memByteBuffer(MemoryUtil.memGetAddress(struct + (long)INPUT_PIXELS), capacity);
    }

    public static int ninput_w(long struct) {
        return UNSAFE.getInt(null, struct + (long)INPUT_W);
    }

    public static int ninput_h(long struct) {
        return UNSAFE.getInt(null, struct + (long)INPUT_H);
    }

    public static double ninput_s0(long struct) {
        return UNSAFE.getDouble(null, struct + (long)INPUT_S0);
    }

    public static double ninput_t0(long struct) {
        return UNSAFE.getDouble(null, struct + (long)INPUT_T0);
    }

    public static double ninput_s1(long struct) {
        return UNSAFE.getDouble(null, struct + (long)INPUT_S1);
    }

    public static double ninput_t1(long struct) {
        return UNSAFE.getDouble(null, struct + (long)INPUT_T1);
    }

    @Nullable
    public static PointerBuffer ninput_cb(long struct, int capacity) {
        return MemoryUtil.memPointerBufferSafe(MemoryUtil.memGetAddress(struct + (long)INPUT_CB), capacity);
    }

    @Nullable
    public static ByteBuffer noutput_pixels(long struct, int capacity) {
        return MemoryUtil.memByteBufferSafe(MemoryUtil.memGetAddress(struct + (long)OUTPUT_PIXELS), capacity);
    }

    public static int noutput_w(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_W);
    }

    public static int noutput_h(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_H);
    }

    public static int noutput_subx(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_SUBX);
    }

    public static int noutput_suby(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_SUBY);
    }

    public static int noutput_subw(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_SUBW);
    }

    public static int noutput_subh(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_SUBH);
    }

    @Nullable
    public static PointerBuffer noutput_cb(long struct, int capacity) {
        return MemoryUtil.memPointerBufferSafe(MemoryUtil.memGetAddress(struct + (long)OUTPUT_CB), capacity);
    }

    public static int ninput_stride_in_bytes(long struct) {
        return UNSAFE.getInt(null, struct + (long)INPUT_STRIDE_IN_BYTES);
    }

    public static int noutput_stride_in_bytes(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_STRIDE_IN_BYTES);
    }

    public static int nsplits(long struct) {
        return UNSAFE.getInt(null, struct + (long)SPLITS);
    }

    public static int nfast_alpha(long struct) {
        return UNSAFE.getInt(null, struct + (long)FAST_ALPHA);
    }

    public static int nneeds_rebuild(long struct) {
        return UNSAFE.getInt(null, struct + (long)NEEDS_REBUILD);
    }

    public static int ncalled_alloc(long struct) {
        return UNSAFE.getInt(null, struct + (long)CALLED_ALLOC);
    }

    public static int ninput_pixel_layout_public(long struct) {
        return UNSAFE.getInt(null, struct + (long)INPUT_PIXEL_LAYOUT_PUBLIC);
    }

    public static int noutput_pixel_layout_public(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_PIXEL_LAYOUT_PUBLIC);
    }

    public static int ninput_data_type(long struct) {
        return UNSAFE.getInt(null, struct + (long)INPUT_DATA_TYPE);
    }

    public static int noutput_data_type(long struct) {
        return UNSAFE.getInt(null, struct + (long)OUTPUT_DATA_TYPE);
    }

    public static int nhorizontal_filter(long struct) {
        return UNSAFE.getInt(null, struct + (long)HORIZONTAL_FILTER);
    }

    public static int nvertical_filter(long struct) {
        return UNSAFE.getInt(null, struct + (long)VERTICAL_FILTER);
    }

    public static int nhorizontal_edge(long struct) {
        return UNSAFE.getInt(null, struct + (long)HORIZONTAL_EDGE);
    }

    public static int nvertical_edge(long struct) {
        return UNSAFE.getInt(null, struct + (long)VERTICAL_EDGE);
    }

    @Nullable
    public static PointerBuffer nhorizontal_filter_kernel(long struct, int capacity) {
        return MemoryUtil.memPointerBufferSafe(MemoryUtil.memGetAddress(struct + (long)HORIZONTAL_FILTER_KERNEL), capacity);
    }

    @Nullable
    public static PointerBuffer nhorizontal_filter_support(long struct, int capacity) {
        return MemoryUtil.memPointerBufferSafe(MemoryUtil.memGetAddress(struct + (long)HORIZONTAL_FILTER_SUPPORT), capacity);
    }

    @Nullable
    public static PointerBuffer nvertical_filter_kernel(long struct, int capacity) {
        return MemoryUtil.memPointerBufferSafe(MemoryUtil.memGetAddress(struct + (long)VERTICAL_FILTER_KERNEL), capacity);
    }

    @Nullable
    public static PointerBuffer nvertical_filter_support(long struct, int capacity) {
        return MemoryUtil.memPointerBufferSafe(MemoryUtil.memGetAddress(struct + (long)VERTICAL_FILTER_SUPPORT), capacity);
    }

    public static long nsamplers(long struct) {
        return MemoryUtil.memGetAddress(struct + (long)SAMPLERS);
    }

    public static void nuser_data(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)USER_DATA, value);
    }

    public static void ninput_pixels(long struct, ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)INPUT_PIXELS, MemoryUtil.memAddress(value));
    }

    public static void ninput_w(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)INPUT_W, value);
    }

    public static void ninput_h(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)INPUT_H, value);
    }

    public static void ninput_s0(long struct, double value) {
        UNSAFE.putDouble(null, struct + (long)INPUT_S0, value);
    }

    public static void ninput_t0(long struct, double value) {
        UNSAFE.putDouble(null, struct + (long)INPUT_T0, value);
    }

    public static void ninput_s1(long struct, double value) {
        UNSAFE.putDouble(null, struct + (long)INPUT_S1, value);
    }

    public static void ninput_t1(long struct, double value) {
        UNSAFE.putDouble(null, struct + (long)INPUT_T1, value);
    }

    public static void ninput_cb(long struct, @Nullable PointerBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)INPUT_CB, MemoryUtil.memAddressSafe(value));
    }

    public static void noutput_pixels(long struct, @Nullable ByteBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)OUTPUT_PIXELS, MemoryUtil.memAddressSafe(value));
    }

    public static void noutput_w(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_W, value);
    }

    public static void noutput_h(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_H, value);
    }

    public static void noutput_subx(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_SUBX, value);
    }

    public static void noutput_suby(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_SUBY, value);
    }

    public static void noutput_subw(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_SUBW, value);
    }

    public static void noutput_subh(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_SUBH, value);
    }

    public static void noutput_cb(long struct, @Nullable PointerBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)OUTPUT_CB, MemoryUtil.memAddressSafe(value));
    }

    public static void ninput_stride_in_bytes(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)INPUT_STRIDE_IN_BYTES, value);
    }

    public static void noutput_stride_in_bytes(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_STRIDE_IN_BYTES, value);
    }

    public static void nsplits(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)SPLITS, value);
    }

    public static void nfast_alpha(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)FAST_ALPHA, value);
    }

    public static void nneeds_rebuild(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)NEEDS_REBUILD, value);
    }

    public static void ncalled_alloc(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)CALLED_ALLOC, value);
    }

    public static void ninput_pixel_layout_public(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)INPUT_PIXEL_LAYOUT_PUBLIC, value);
    }

    public static void noutput_pixel_layout_public(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_PIXEL_LAYOUT_PUBLIC, value);
    }

    public static void ninput_data_type(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)INPUT_DATA_TYPE, value);
    }

    public static void noutput_data_type(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)OUTPUT_DATA_TYPE, value);
    }

    public static void nhorizontal_filter(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)HORIZONTAL_FILTER, value);
    }

    public static void nvertical_filter(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)VERTICAL_FILTER, value);
    }

    public static void nhorizontal_edge(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)HORIZONTAL_EDGE, value);
    }

    public static void nvertical_edge(long struct, int value) {
        UNSAFE.putInt(null, struct + (long)VERTICAL_EDGE, value);
    }

    public static void nhorizontal_filter_kernel(long struct, @Nullable PointerBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)HORIZONTAL_FILTER_KERNEL, MemoryUtil.memAddressSafe(value));
    }

    public static void nhorizontal_filter_support(long struct, @Nullable PointerBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)HORIZONTAL_FILTER_SUPPORT, MemoryUtil.memAddressSafe(value));
    }

    public static void nvertical_filter_kernel(long struct, @Nullable PointerBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)VERTICAL_FILTER_KERNEL, MemoryUtil.memAddressSafe(value));
    }

    public static void nvertical_filter_support(long struct, @Nullable PointerBuffer value) {
        MemoryUtil.memPutAddress(struct + (long)VERTICAL_FILTER_SUPPORT, MemoryUtil.memAddressSafe(value));
    }

    public static void nsamplers(long struct, long value) {
        MemoryUtil.memPutAddress(struct + (long)SAMPLERS, Checks.check(value));
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)INPUT_PIXELS));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)SAMPLERS));
    }

    static {
        Struct.Layout layout = STBIR_RESIZE.__struct(STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(8), STBIR_RESIZE.__member(8), STBIR_RESIZE.__member(8), STBIR_RESIZE.__member(8), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(4), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(POINTER_SIZE), STBIR_RESIZE.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        USER_DATA = layout.offsetof(0);
        INPUT_PIXELS = layout.offsetof(1);
        INPUT_W = layout.offsetof(2);
        INPUT_H = layout.offsetof(3);
        INPUT_S0 = layout.offsetof(4);
        INPUT_T0 = layout.offsetof(5);
        INPUT_S1 = layout.offsetof(6);
        INPUT_T1 = layout.offsetof(7);
        INPUT_CB = layout.offsetof(8);
        OUTPUT_PIXELS = layout.offsetof(9);
        OUTPUT_W = layout.offsetof(10);
        OUTPUT_H = layout.offsetof(11);
        OUTPUT_SUBX = layout.offsetof(12);
        OUTPUT_SUBY = layout.offsetof(13);
        OUTPUT_SUBW = layout.offsetof(14);
        OUTPUT_SUBH = layout.offsetof(15);
        OUTPUT_CB = layout.offsetof(16);
        INPUT_STRIDE_IN_BYTES = layout.offsetof(17);
        OUTPUT_STRIDE_IN_BYTES = layout.offsetof(18);
        SPLITS = layout.offsetof(19);
        FAST_ALPHA = layout.offsetof(20);
        NEEDS_REBUILD = layout.offsetof(21);
        CALLED_ALLOC = layout.offsetof(22);
        INPUT_PIXEL_LAYOUT_PUBLIC = layout.offsetof(23);
        OUTPUT_PIXEL_LAYOUT_PUBLIC = layout.offsetof(24);
        INPUT_DATA_TYPE = layout.offsetof(25);
        OUTPUT_DATA_TYPE = layout.offsetof(26);
        HORIZONTAL_FILTER = layout.offsetof(27);
        VERTICAL_FILTER = layout.offsetof(28);
        HORIZONTAL_EDGE = layout.offsetof(29);
        VERTICAL_EDGE = layout.offsetof(30);
        HORIZONTAL_FILTER_KERNEL = layout.offsetof(31);
        HORIZONTAL_FILTER_SUPPORT = layout.offsetof(32);
        VERTICAL_FILTER_KERNEL = layout.offsetof(33);
        VERTICAL_FILTER_SUPPORT = layout.offsetof(34);
        SAMPLERS = layout.offsetof(35);
    }

    public static class Buffer
    extends StructBuffer<STBIR_RESIZE, Buffer>
    implements NativeResource {
        private static final STBIR_RESIZE ELEMENT_FACTORY = STBIR_RESIZE.create(-1L);

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
        protected STBIR_RESIZE getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="void *")
        public long user_data() {
            return STBIR_RESIZE.nuser_data(this.address());
        }

        @NativeType(value="void const *")
        public ByteBuffer input_pixels(int capacity) {
            return STBIR_RESIZE.ninput_pixels(this.address(), capacity);
        }

        public int input_w() {
            return STBIR_RESIZE.ninput_w(this.address());
        }

        public int input_h() {
            return STBIR_RESIZE.ninput_h(this.address());
        }

        public double input_s0() {
            return STBIR_RESIZE.ninput_s0(this.address());
        }

        public double input_t0() {
            return STBIR_RESIZE.ninput_t0(this.address());
        }

        public double input_s1() {
            return STBIR_RESIZE.ninput_s1(this.address());
        }

        public double input_t1() {
            return STBIR_RESIZE.ninput_t1(this.address());
        }

        @Nullable
        @NativeType(value="stbir_input_callback **")
        public PointerBuffer input_cb(int capacity) {
            return STBIR_RESIZE.ninput_cb(this.address(), capacity);
        }

        @Nullable
        @NativeType(value="void *")
        public ByteBuffer output_pixels(int capacity) {
            return STBIR_RESIZE.noutput_pixels(this.address(), capacity);
        }

        public int output_w() {
            return STBIR_RESIZE.noutput_w(this.address());
        }

        public int output_h() {
            return STBIR_RESIZE.noutput_h(this.address());
        }

        public int output_subx() {
            return STBIR_RESIZE.noutput_subx(this.address());
        }

        public int output_suby() {
            return STBIR_RESIZE.noutput_suby(this.address());
        }

        public int output_subw() {
            return STBIR_RESIZE.noutput_subw(this.address());
        }

        public int output_subh() {
            return STBIR_RESIZE.noutput_subh(this.address());
        }

        @Nullable
        @NativeType(value="stbir_output_callback **")
        public PointerBuffer output_cb(int capacity) {
            return STBIR_RESIZE.noutput_cb(this.address(), capacity);
        }

        public int input_stride_in_bytes() {
            return STBIR_RESIZE.ninput_stride_in_bytes(this.address());
        }

        public int output_stride_in_bytes() {
            return STBIR_RESIZE.noutput_stride_in_bytes(this.address());
        }

        public int splits() {
            return STBIR_RESIZE.nsplits(this.address());
        }

        @NativeType(value="int")
        public boolean fast_alpha() {
            return STBIR_RESIZE.nfast_alpha(this.address()) != 0;
        }

        @NativeType(value="int")
        public boolean needs_rebuild() {
            return STBIR_RESIZE.nneeds_rebuild(this.address()) != 0;
        }

        @NativeType(value="int")
        public boolean called_alloc() {
            return STBIR_RESIZE.ncalled_alloc(this.address()) != 0;
        }

        @NativeType(value="stbir_pixel_layout")
        public int input_pixel_layout_public() {
            return STBIR_RESIZE.ninput_pixel_layout_public(this.address());
        }

        @NativeType(value="stbir_pixel_layout")
        public int output_pixel_layout_public() {
            return STBIR_RESIZE.noutput_pixel_layout_public(this.address());
        }

        @NativeType(value="stbir_datatype")
        public int input_data_type() {
            return STBIR_RESIZE.ninput_data_type(this.address());
        }

        @NativeType(value="stbir_datatype")
        public int output_data_type() {
            return STBIR_RESIZE.noutput_data_type(this.address());
        }

        @NativeType(value="stbir_filter")
        public int horizontal_filter() {
            return STBIR_RESIZE.nhorizontal_filter(this.address());
        }

        @NativeType(value="stbir_filter")
        public int vertical_filter() {
            return STBIR_RESIZE.nvertical_filter(this.address());
        }

        @NativeType(value="stbir_edge")
        public int horizontal_edge() {
            return STBIR_RESIZE.nhorizontal_edge(this.address());
        }

        @NativeType(value="stbir_edge")
        public int vertical_edge() {
            return STBIR_RESIZE.nvertical_edge(this.address());
        }

        @Nullable
        @NativeType(value="stbir__kernel_callback **")
        public PointerBuffer horizontal_filter_kernel(int capacity) {
            return STBIR_RESIZE.nhorizontal_filter_kernel(this.address(), capacity);
        }

        @Nullable
        @NativeType(value="stbir__support_callback **")
        public PointerBuffer horizontal_filter_support(int capacity) {
            return STBIR_RESIZE.nhorizontal_filter_support(this.address(), capacity);
        }

        @Nullable
        @NativeType(value="stbir__kernel_callback **")
        public PointerBuffer vertical_filter_kernel(int capacity) {
            return STBIR_RESIZE.nvertical_filter_kernel(this.address(), capacity);
        }

        @Nullable
        @NativeType(value="stbir__support_callback **")
        public PointerBuffer vertical_filter_support(int capacity) {
            return STBIR_RESIZE.nvertical_filter_support(this.address(), capacity);
        }

        @NativeType(value="stbir__info *")
        public long samplers() {
            return STBIR_RESIZE.nsamplers(this.address());
        }

        public Buffer user_data(@NativeType(value="void *") long value) {
            STBIR_RESIZE.nuser_data(this.address(), value);
            return this;
        }

        public Buffer input_pixels(@NativeType(value="void const *") ByteBuffer value) {
            STBIR_RESIZE.ninput_pixels(this.address(), value);
            return this;
        }

        public Buffer input_w(int value) {
            STBIR_RESIZE.ninput_w(this.address(), value);
            return this;
        }

        public Buffer input_h(int value) {
            STBIR_RESIZE.ninput_h(this.address(), value);
            return this;
        }

        public Buffer input_s0(double value) {
            STBIR_RESIZE.ninput_s0(this.address(), value);
            return this;
        }

        public Buffer input_t0(double value) {
            STBIR_RESIZE.ninput_t0(this.address(), value);
            return this;
        }

        public Buffer input_s1(double value) {
            STBIR_RESIZE.ninput_s1(this.address(), value);
            return this;
        }

        public Buffer input_t1(double value) {
            STBIR_RESIZE.ninput_t1(this.address(), value);
            return this;
        }

        public Buffer input_cb(@Nullable @NativeType(value="stbir_input_callback **") PointerBuffer value) {
            STBIR_RESIZE.ninput_cb(this.address(), value);
            return this;
        }

        public Buffer output_pixels(@Nullable @NativeType(value="void *") ByteBuffer value) {
            STBIR_RESIZE.noutput_pixels(this.address(), value);
            return this;
        }

        public Buffer output_w(int value) {
            STBIR_RESIZE.noutput_w(this.address(), value);
            return this;
        }

        public Buffer output_h(int value) {
            STBIR_RESIZE.noutput_h(this.address(), value);
            return this;
        }

        public Buffer output_subx(int value) {
            STBIR_RESIZE.noutput_subx(this.address(), value);
            return this;
        }

        public Buffer output_suby(int value) {
            STBIR_RESIZE.noutput_suby(this.address(), value);
            return this;
        }

        public Buffer output_subw(int value) {
            STBIR_RESIZE.noutput_subw(this.address(), value);
            return this;
        }

        public Buffer output_subh(int value) {
            STBIR_RESIZE.noutput_subh(this.address(), value);
            return this;
        }

        public Buffer output_cb(@Nullable @NativeType(value="stbir_output_callback **") PointerBuffer value) {
            STBIR_RESIZE.noutput_cb(this.address(), value);
            return this;
        }

        public Buffer input_stride_in_bytes(int value) {
            STBIR_RESIZE.ninput_stride_in_bytes(this.address(), value);
            return this;
        }

        public Buffer output_stride_in_bytes(int value) {
            STBIR_RESIZE.noutput_stride_in_bytes(this.address(), value);
            return this;
        }

        public Buffer splits(int value) {
            STBIR_RESIZE.nsplits(this.address(), value);
            return this;
        }

        public Buffer fast_alpha(@NativeType(value="int") boolean value) {
            STBIR_RESIZE.nfast_alpha(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer needs_rebuild(@NativeType(value="int") boolean value) {
            STBIR_RESIZE.nneeds_rebuild(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer called_alloc(@NativeType(value="int") boolean value) {
            STBIR_RESIZE.ncalled_alloc(this.address(), value ? 1 : 0);
            return this;
        }

        public Buffer input_pixel_layout_public(@NativeType(value="stbir_pixel_layout") int value) {
            STBIR_RESIZE.ninput_pixel_layout_public(this.address(), value);
            return this;
        }

        public Buffer output_pixel_layout_public(@NativeType(value="stbir_pixel_layout") int value) {
            STBIR_RESIZE.noutput_pixel_layout_public(this.address(), value);
            return this;
        }

        public Buffer input_data_type(@NativeType(value="stbir_datatype") int value) {
            STBIR_RESIZE.ninput_data_type(this.address(), value);
            return this;
        }

        public Buffer output_data_type(@NativeType(value="stbir_datatype") int value) {
            STBIR_RESIZE.noutput_data_type(this.address(), value);
            return this;
        }

        public Buffer horizontal_filter(@NativeType(value="stbir_filter") int value) {
            STBIR_RESIZE.nhorizontal_filter(this.address(), value);
            return this;
        }

        public Buffer vertical_filter(@NativeType(value="stbir_filter") int value) {
            STBIR_RESIZE.nvertical_filter(this.address(), value);
            return this;
        }

        public Buffer horizontal_edge(@NativeType(value="stbir_edge") int value) {
            STBIR_RESIZE.nhorizontal_edge(this.address(), value);
            return this;
        }

        public Buffer vertical_edge(@NativeType(value="stbir_edge") int value) {
            STBIR_RESIZE.nvertical_edge(this.address(), value);
            return this;
        }

        public Buffer horizontal_filter_kernel(@Nullable @NativeType(value="stbir__kernel_callback **") PointerBuffer value) {
            STBIR_RESIZE.nhorizontal_filter_kernel(this.address(), value);
            return this;
        }

        public Buffer horizontal_filter_support(@Nullable @NativeType(value="stbir__support_callback **") PointerBuffer value) {
            STBIR_RESIZE.nhorizontal_filter_support(this.address(), value);
            return this;
        }

        public Buffer vertical_filter_kernel(@Nullable @NativeType(value="stbir__kernel_callback **") PointerBuffer value) {
            STBIR_RESIZE.nvertical_filter_kernel(this.address(), value);
            return this;
        }

        public Buffer vertical_filter_support(@Nullable @NativeType(value="stbir__support_callback **") PointerBuffer value) {
            STBIR_RESIZE.nvertical_filter_support(this.address(), value);
            return this;
        }

        public Buffer samplers(@NativeType(value="stbir__info *") long value) {
            STBIR_RESIZE.nsamplers(this.address(), value);
            return this;
        }
    }
}
