package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class TT_PCLT
extends Struct<TT_PCLT> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int VERSION;
    public static final int FONTNUMBER;
    public static final int PITCH;
    public static final int XHEIGHT;
    public static final int STYLE;
    public static final int TYPEFAMILY;
    public static final int CAPHEIGHT;
    public static final int SYMBOLSET;
    public static final int TYPEFACE;
    public static final int CHARACTERCOMPLEMENT;
    public static final int FILENAME;
    public static final int STROKEWEIGHT;
    public static final int WIDTHTYPE;
    public static final int SERIFSTYLE;
    public static final int RESERVED;

    protected TT_PCLT(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected TT_PCLT create(long address, @Nullable ByteBuffer container) {
        return new TT_PCLT(address, container);
    }

    public TT_PCLT(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), TT_PCLT.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long Version() {
        return TT_PCLT.nVersion(this.address());
    }

    @NativeType(value="FT_ULong")
    public long FontNumber() {
        return TT_PCLT.nFontNumber(this.address());
    }

    @NativeType(value="FT_UShort")
    public short Pitch() {
        return TT_PCLT.nPitch(this.address());
    }

    @NativeType(value="FT_UShort")
    public short xHeight() {
        return TT_PCLT.nxHeight(this.address());
    }

    @NativeType(value="FT_UShort")
    public short Style() {
        return TT_PCLT.nStyle(this.address());
    }

    @NativeType(value="FT_UShort")
    public short TypeFamily() {
        return TT_PCLT.nTypeFamily(this.address());
    }

    @NativeType(value="FT_UShort")
    public short CapHeight() {
        return TT_PCLT.nCapHeight(this.address());
    }

    @NativeType(value="FT_UShort")
    public short SymbolSet() {
        return TT_PCLT.nSymbolSet(this.address());
    }

    @NativeType(value="FT_Char[16]")
    public ByteBuffer TypeFace() {
        return TT_PCLT.nTypeFace(this.address());
    }

    @NativeType(value="FT_Char")
    public byte TypeFace(int index) {
        return TT_PCLT.nTypeFace(this.address(), index);
    }

    @NativeType(value="FT_Char[8]")
    public ByteBuffer CharacterComplement() {
        return TT_PCLT.nCharacterComplement(this.address());
    }

    @NativeType(value="FT_Char")
    public byte CharacterComplement(int index) {
        return TT_PCLT.nCharacterComplement(this.address(), index);
    }

    @NativeType(value="FT_Char[6]")
    public ByteBuffer FileName() {
        return TT_PCLT.nFileName(this.address());
    }

    @NativeType(value="FT_Char")
    public byte FileName(int index) {
        return TT_PCLT.nFileName(this.address(), index);
    }

    @NativeType(value="FT_Char")
    public byte StrokeWeight() {
        return TT_PCLT.nStrokeWeight(this.address());
    }

    @NativeType(value="FT_Char")
    public byte WidthType() {
        return TT_PCLT.nWidthType(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte SerifStyle() {
        return TT_PCLT.nSerifStyle(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte Reserved() {
        return TT_PCLT.nReserved(this.address());
    }

    public static TT_PCLT create(long address) {
        return new TT_PCLT(address, null);
    }

    @Nullable
    public static TT_PCLT createSafe(long address) {
        return address == 0L ? null : new TT_PCLT(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nVersion(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)VERSION);
    }

    public static long nFontNumber(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)FONTNUMBER);
    }

    public static short nPitch(long struct) {
        return UNSAFE.getShort(null, struct + (long)PITCH);
    }

    public static short nxHeight(long struct) {
        return UNSAFE.getShort(null, struct + (long)XHEIGHT);
    }

    public static short nStyle(long struct) {
        return UNSAFE.getShort(null, struct + (long)STYLE);
    }

    public static short nTypeFamily(long struct) {
        return UNSAFE.getShort(null, struct + (long)TYPEFAMILY);
    }

    public static short nCapHeight(long struct) {
        return UNSAFE.getShort(null, struct + (long)CAPHEIGHT);
    }

    public static short nSymbolSet(long struct) {
        return UNSAFE.getShort(null, struct + (long)SYMBOLSET);
    }

    public static ByteBuffer nTypeFace(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)TYPEFACE, 16);
    }

    public static byte nTypeFace(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)TYPEFACE + Checks.check(index, 16) * 1L);
    }

    public static ByteBuffer nCharacterComplement(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)CHARACTERCOMPLEMENT, 8);
    }

    public static byte nCharacterComplement(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)CHARACTERCOMPLEMENT + Checks.check(index, 8) * 1L);
    }

    public static ByteBuffer nFileName(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)FILENAME, 6);
    }

    public static byte nFileName(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)FILENAME + Checks.check(index, 6) * 1L);
    }

    public static byte nStrokeWeight(long struct) {
        return UNSAFE.getByte(null, struct + (long)STROKEWEIGHT);
    }

    public static byte nWidthType(long struct) {
        return UNSAFE.getByte(null, struct + (long)WIDTHTYPE);
    }

    public static byte nSerifStyle(long struct) {
        return UNSAFE.getByte(null, struct + (long)SERIFSTYLE);
    }

    public static byte nReserved(long struct) {
        return UNSAFE.getByte(null, struct + (long)RESERVED);
    }

    static {
        Struct.Layout layout = TT_PCLT.__struct(TT_PCLT.__member(CLONG_SIZE), TT_PCLT.__member(CLONG_SIZE), TT_PCLT.__member(2), TT_PCLT.__member(2), TT_PCLT.__member(2), TT_PCLT.__member(2), TT_PCLT.__member(2), TT_PCLT.__member(2), TT_PCLT.__array(1, 16), TT_PCLT.__array(1, 8), TT_PCLT.__array(1, 6), TT_PCLT.__member(1), TT_PCLT.__member(1), TT_PCLT.__member(1), TT_PCLT.__member(1));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        VERSION = layout.offsetof(0);
        FONTNUMBER = layout.offsetof(1);
        PITCH = layout.offsetof(2);
        XHEIGHT = layout.offsetof(3);
        STYLE = layout.offsetof(4);
        TYPEFAMILY = layout.offsetof(5);
        CAPHEIGHT = layout.offsetof(6);
        SYMBOLSET = layout.offsetof(7);
        TYPEFACE = layout.offsetof(8);
        CHARACTERCOMPLEMENT = layout.offsetof(9);
        FILENAME = layout.offsetof(10);
        STROKEWEIGHT = layout.offsetof(11);
        WIDTHTYPE = layout.offsetof(12);
        SERIFSTYLE = layout.offsetof(13);
        RESERVED = layout.offsetof(14);
    }

    public static class Buffer
    extends StructBuffer<TT_PCLT, Buffer> {
        private static final TT_PCLT ELEMENT_FACTORY = TT_PCLT.create(-1L);

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
        protected TT_PCLT getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long Version() {
            return TT_PCLT.nVersion(this.address());
        }

        @NativeType(value="FT_ULong")
        public long FontNumber() {
            return TT_PCLT.nFontNumber(this.address());
        }

        @NativeType(value="FT_UShort")
        public short Pitch() {
            return TT_PCLT.nPitch(this.address());
        }

        @NativeType(value="FT_UShort")
        public short xHeight() {
            return TT_PCLT.nxHeight(this.address());
        }

        @NativeType(value="FT_UShort")
        public short Style() {
            return TT_PCLT.nStyle(this.address());
        }

        @NativeType(value="FT_UShort")
        public short TypeFamily() {
            return TT_PCLT.nTypeFamily(this.address());
        }

        @NativeType(value="FT_UShort")
        public short CapHeight() {
            return TT_PCLT.nCapHeight(this.address());
        }

        @NativeType(value="FT_UShort")
        public short SymbolSet() {
            return TT_PCLT.nSymbolSet(this.address());
        }

        @NativeType(value="FT_Char[16]")
        public ByteBuffer TypeFace() {
            return TT_PCLT.nTypeFace(this.address());
        }

        @NativeType(value="FT_Char")
        public byte TypeFace(int index) {
            return TT_PCLT.nTypeFace(this.address(), index);
        }

        @NativeType(value="FT_Char[8]")
        public ByteBuffer CharacterComplement() {
            return TT_PCLT.nCharacterComplement(this.address());
        }

        @NativeType(value="FT_Char")
        public byte CharacterComplement(int index) {
            return TT_PCLT.nCharacterComplement(this.address(), index);
        }

        @NativeType(value="FT_Char[6]")
        public ByteBuffer FileName() {
            return TT_PCLT.nFileName(this.address());
        }

        @NativeType(value="FT_Char")
        public byte FileName(int index) {
            return TT_PCLT.nFileName(this.address(), index);
        }

        @NativeType(value="FT_Char")
        public byte StrokeWeight() {
            return TT_PCLT.nStrokeWeight(this.address());
        }

        @NativeType(value="FT_Char")
        public byte WidthType() {
            return TT_PCLT.nWidthType(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte SerifStyle() {
            return TT_PCLT.nSerifStyle(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte Reserved() {
            return TT_PCLT.nReserved(this.address());
        }
    }
}
