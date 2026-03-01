package org.lwjgl.util.freetype;

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
import org.lwjgl.util.freetype.SVG_Lib_Free_Func;
import org.lwjgl.util.freetype.SVG_Lib_Free_FuncI;
import org.lwjgl.util.freetype.SVG_Lib_Init_Func;
import org.lwjgl.util.freetype.SVG_Lib_Init_FuncI;
import org.lwjgl.util.freetype.SVG_Lib_Preset_Slot_Func;
import org.lwjgl.util.freetype.SVG_Lib_Preset_Slot_FuncI;
import org.lwjgl.util.freetype.SVG_Lib_Render_Func;
import org.lwjgl.util.freetype.SVG_Lib_Render_FuncI;

public class SVG_RendererHooks
extends Struct<SVG_RendererHooks>
implements NativeResource {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int INIT_SVG;
    public static final int FREE_SVG;
    public static final int RENDER_SVG;
    public static final int PRESET_SLOT;

    protected SVG_RendererHooks(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected SVG_RendererHooks create(long address, @Nullable ByteBuffer container) {
        return new SVG_RendererHooks(address, container);
    }

    public SVG_RendererHooks(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), SVG_RendererHooks.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public SVG_Lib_Init_Func init_svg() {
        return SVG_RendererHooks.ninit_svg(this.address());
    }

    public SVG_Lib_Free_Func free_svg() {
        return SVG_RendererHooks.nfree_svg(this.address());
    }

    public SVG_Lib_Render_Func render_svg() {
        return SVG_RendererHooks.nrender_svg(this.address());
    }

    public SVG_Lib_Preset_Slot_Func preset_slot() {
        return SVG_RendererHooks.npreset_slot(this.address());
    }

    public SVG_RendererHooks init_svg(@NativeType(value="SVG_Lib_Init_Func") SVG_Lib_Init_FuncI value) {
        SVG_RendererHooks.ninit_svg(this.address(), value);
        return this;
    }

    public SVG_RendererHooks free_svg(@NativeType(value="SVG_Lib_Free_Func") SVG_Lib_Free_FuncI value) {
        SVG_RendererHooks.nfree_svg(this.address(), value);
        return this;
    }

    public SVG_RendererHooks render_svg(@NativeType(value="SVG_Lib_Render_Func") SVG_Lib_Render_FuncI value) {
        SVG_RendererHooks.nrender_svg(this.address(), value);
        return this;
    }

    public SVG_RendererHooks preset_slot(@NativeType(value="SVG_Lib_Preset_Slot_Func") SVG_Lib_Preset_Slot_FuncI value) {
        SVG_RendererHooks.npreset_slot(this.address(), value);
        return this;
    }

    public SVG_RendererHooks set(SVG_Lib_Init_FuncI init_svg, SVG_Lib_Free_FuncI free_svg, SVG_Lib_Render_FuncI render_svg, SVG_Lib_Preset_Slot_FuncI preset_slot) {
        this.init_svg(init_svg);
        this.free_svg(free_svg);
        this.render_svg(render_svg);
        this.preset_slot(preset_slot);
        return this;
    }

    public SVG_RendererHooks set(SVG_RendererHooks src) {
        MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
        return this;
    }

    public static SVG_RendererHooks malloc() {
        return new SVG_RendererHooks(MemoryUtil.nmemAllocChecked(SIZEOF), null);
    }

    public static SVG_RendererHooks calloc() {
        return new SVG_RendererHooks(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
    }

    public static SVG_RendererHooks create() {
        ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
        return new SVG_RendererHooks(MemoryUtil.memAddress(container), container);
    }

    public static SVG_RendererHooks create(long address) {
        return new SVG_RendererHooks(address, null);
    }

    @Nullable
    public static SVG_RendererHooks createSafe(long address) {
        return address == 0L ? null : new SVG_RendererHooks(address, null);
    }

    public static Buffer malloc(int capacity) {
        return new Buffer(MemoryUtil.nmemAllocChecked(SVG_RendererHooks.__checkMalloc(capacity, SIZEOF)), capacity);
    }

    public static Buffer calloc(int capacity) {
        return new Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
    }

    public static Buffer create(int capacity) {
        ByteBuffer container = SVG_RendererHooks.__create(capacity, SIZEOF);
        return new Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static SVG_RendererHooks malloc(MemoryStack stack) {
        return new SVG_RendererHooks(stack.nmalloc(ALIGNOF, SIZEOF), null);
    }

    public static SVG_RendererHooks calloc(MemoryStack stack) {
        return new SVG_RendererHooks(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
    }

    public static Buffer malloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
    }

    public static Buffer calloc(int capacity, MemoryStack stack) {
        return new Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
    }

    public static SVG_Lib_Init_Func ninit_svg(long struct) {
        return SVG_Lib_Init_Func.create(MemoryUtil.memGetAddress(struct + (long)INIT_SVG));
    }

    public static SVG_Lib_Free_Func nfree_svg(long struct) {
        return SVG_Lib_Free_Func.create(MemoryUtil.memGetAddress(struct + (long)FREE_SVG));
    }

    public static SVG_Lib_Render_Func nrender_svg(long struct) {
        return SVG_Lib_Render_Func.create(MemoryUtil.memGetAddress(struct + (long)RENDER_SVG));
    }

    public static SVG_Lib_Preset_Slot_Func npreset_slot(long struct) {
        return SVG_Lib_Preset_Slot_Func.create(MemoryUtil.memGetAddress(struct + (long)PRESET_SLOT));
    }

    public static void ninit_svg(long struct, SVG_Lib_Init_FuncI value) {
        MemoryUtil.memPutAddress(struct + (long)INIT_SVG, value.address());
    }

    public static void nfree_svg(long struct, SVG_Lib_Free_FuncI value) {
        MemoryUtil.memPutAddress(struct + (long)FREE_SVG, value.address());
    }

    public static void nrender_svg(long struct, SVG_Lib_Render_FuncI value) {
        MemoryUtil.memPutAddress(struct + (long)RENDER_SVG, value.address());
    }

    public static void npreset_slot(long struct, SVG_Lib_Preset_Slot_FuncI value) {
        MemoryUtil.memPutAddress(struct + (long)PRESET_SLOT, value.address());
    }

    public static void validate(long struct) {
        Checks.check(MemoryUtil.memGetAddress(struct + (long)INIT_SVG));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)FREE_SVG));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)RENDER_SVG));
        Checks.check(MemoryUtil.memGetAddress(struct + (long)PRESET_SLOT));
    }

    static {
        Struct.Layout layout = SVG_RendererHooks.__struct(SVG_RendererHooks.__member(POINTER_SIZE), SVG_RendererHooks.__member(POINTER_SIZE), SVG_RendererHooks.__member(POINTER_SIZE), SVG_RendererHooks.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        INIT_SVG = layout.offsetof(0);
        FREE_SVG = layout.offsetof(1);
        RENDER_SVG = layout.offsetof(2);
        PRESET_SLOT = layout.offsetof(3);
    }

    public static class Buffer
    extends StructBuffer<SVG_RendererHooks, Buffer>
    implements NativeResource {
        private static final SVG_RendererHooks ELEMENT_FACTORY = SVG_RendererHooks.create(-1L);

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
        protected SVG_RendererHooks getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public SVG_Lib_Init_Func init_svg() {
            return SVG_RendererHooks.ninit_svg(this.address());
        }

        public SVG_Lib_Free_Func free_svg() {
            return SVG_RendererHooks.nfree_svg(this.address());
        }

        public SVG_Lib_Render_Func render_svg() {
            return SVG_RendererHooks.nrender_svg(this.address());
        }

        public SVG_Lib_Preset_Slot_Func preset_slot() {
            return SVG_RendererHooks.npreset_slot(this.address());
        }

        public Buffer init_svg(@NativeType(value="SVG_Lib_Init_Func") SVG_Lib_Init_FuncI value) {
            SVG_RendererHooks.ninit_svg(this.address(), value);
            return this;
        }

        public Buffer free_svg(@NativeType(value="SVG_Lib_Free_Func") SVG_Lib_Free_FuncI value) {
            SVG_RendererHooks.nfree_svg(this.address(), value);
            return this;
        }

        public Buffer render_svg(@NativeType(value="SVG_Lib_Render_Func") SVG_Lib_Render_FuncI value) {
            SVG_RendererHooks.nrender_svg(this.address(), value);
            return this;
        }

        public Buffer preset_slot(@NativeType(value="SVG_Lib_Preset_Slot_Func") SVG_Lib_Preset_Slot_FuncI value) {
            SVG_RendererHooks.npreset_slot(this.address(), value);
            return this;
        }
    }
}
