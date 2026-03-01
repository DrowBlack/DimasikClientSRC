package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.FT_Face;

public class FT_Prop_GlyphToScriptMap
extends Struct<FT_Prop_GlyphToScriptMap> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int FACE;
    public static final int MAP;

    protected FT_Prop_GlyphToScriptMap(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected FT_Prop_GlyphToScriptMap create(long address, @Nullable ByteBuffer container) {
        return new FT_Prop_GlyphToScriptMap(address, container);
    }

    public FT_Prop_GlyphToScriptMap(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), FT_Prop_GlyphToScriptMap.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    public FT_Face face() {
        return FT_Prop_GlyphToScriptMap.nface(this.address());
    }

    @Nullable
    @NativeType(value="FT_UShort *")
    public ShortBuffer map(int capacity) {
        return FT_Prop_GlyphToScriptMap.nmap(this.address(), capacity);
    }

    public static FT_Prop_GlyphToScriptMap create(long address) {
        return new FT_Prop_GlyphToScriptMap(address, null);
    }

    @Nullable
    public static FT_Prop_GlyphToScriptMap createSafe(long address) {
        return address == 0L ? null : new FT_Prop_GlyphToScriptMap(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static FT_Face nface(long struct) {
        return FT_Face.create(MemoryUtil.memGetAddress(struct + (long)FACE));
    }

    @Nullable
    public static ShortBuffer nmap(long struct, int capacity) {
        return MemoryUtil.memShortBufferSafe(MemoryUtil.memGetAddress(struct + (long)MAP), capacity);
    }

    static {
        Struct.Layout layout = FT_Prop_GlyphToScriptMap.__struct(FT_Prop_GlyphToScriptMap.__member(POINTER_SIZE), FT_Prop_GlyphToScriptMap.__member(POINTER_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        FACE = layout.offsetof(0);
        MAP = layout.offsetof(1);
    }

    public static class Buffer
    extends StructBuffer<FT_Prop_GlyphToScriptMap, Buffer> {
        private static final FT_Prop_GlyphToScriptMap ELEMENT_FACTORY = FT_Prop_GlyphToScriptMap.create(-1L);

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
        protected FT_Prop_GlyphToScriptMap getElementFactory() {
            return ELEMENT_FACTORY;
        }

        public FT_Face face() {
            return FT_Prop_GlyphToScriptMap.nface(this.address());
        }

        @Nullable
        @NativeType(value="FT_UShort *")
        public ShortBuffer map(int capacity) {
            return FT_Prop_GlyphToScriptMap.nmap(this.address(), capacity);
        }
    }
}
