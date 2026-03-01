package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class TT_OS2
extends Struct<TT_OS2> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int VERSION;
    public static final int XAVGCHARWIDTH;
    public static final int USWEIGHTCLASS;
    public static final int USWIDTHCLASS;
    public static final int FSTYPE;
    public static final int YSUBSCRIPTXSIZE;
    public static final int YSUBSCRIPTYSIZE;
    public static final int YSUBSCRIPTXOFFSET;
    public static final int YSUBSCRIPTYOFFSET;
    public static final int YSUPERSCRIPTXSIZE;
    public static final int YSUPERSCRIPTYSIZE;
    public static final int YSUPERSCRIPTXOFFSET;
    public static final int YSUPERSCRIPTYOFFSET;
    public static final int YSTRIKEOUTSIZE;
    public static final int YSTRIKEOUTPOSITION;
    public static final int SFAMILYCLASS;
    public static final int PANOSE;
    public static final int ULUNICODERANGE1;
    public static final int ULUNICODERANGE2;
    public static final int ULUNICODERANGE3;
    public static final int ULUNICODERANGE4;
    public static final int ACHVENDID;
    public static final int FSSELECTION;
    public static final int USFIRSTCHARINDEX;
    public static final int USLASTCHARINDEX;
    public static final int STYPOASCENDER;
    public static final int STYPODESCENDER;
    public static final int STYPOLINEGAP;
    public static final int USWINASCENT;
    public static final int USWINDESCENT;
    public static final int ULCODEPAGERANGE1;
    public static final int ULCODEPAGERANGE2;
    public static final int SXHEIGHT;
    public static final int SCAPHEIGHT;
    public static final int USDEFAULTCHAR;
    public static final int USBREAKCHAR;
    public static final int USMAXCONTEXT;
    public static final int USLOWEROPTICALPOINTSIZE;
    public static final int USUPPEROPTICALPOINTSIZE;

    protected TT_OS2(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected TT_OS2 create(long address, @Nullable ByteBuffer container) {
        return new TT_OS2(address, container);
    }

    public TT_OS2(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), TT_OS2.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_UShort")
    public short version() {
        return TT_OS2.nversion(this.address());
    }

    @NativeType(value="FT_Short")
    public short xAvgCharWidth() {
        return TT_OS2.nxAvgCharWidth(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usWeightClass() {
        return TT_OS2.nusWeightClass(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usWidthClass() {
        return TT_OS2.nusWidthClass(this.address());
    }

    @NativeType(value="FT_UShort")
    public short fsType() {
        return TT_OS2.nfsType(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySubscriptXSize() {
        return TT_OS2.nySubscriptXSize(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySubscriptYSize() {
        return TT_OS2.nySubscriptYSize(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySubscriptXOffset() {
        return TT_OS2.nySubscriptXOffset(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySubscriptYOffset() {
        return TT_OS2.nySubscriptYOffset(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySuperscriptXSize() {
        return TT_OS2.nySuperscriptXSize(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySuperscriptYSize() {
        return TT_OS2.nySuperscriptYSize(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySuperscriptXOffset() {
        return TT_OS2.nySuperscriptXOffset(this.address());
    }

    @NativeType(value="FT_Short")
    public short ySuperscriptYOffset() {
        return TT_OS2.nySuperscriptYOffset(this.address());
    }

    @NativeType(value="FT_Short")
    public short yStrikeoutSize() {
        return TT_OS2.nyStrikeoutSize(this.address());
    }

    @NativeType(value="FT_Short")
    public short yStrikeoutPosition() {
        return TT_OS2.nyStrikeoutPosition(this.address());
    }

    @NativeType(value="FT_Short")
    public short sFamilyClass() {
        return TT_OS2.nsFamilyClass(this.address());
    }

    @NativeType(value="FT_Byte[10]")
    public ByteBuffer panose() {
        return TT_OS2.npanose(this.address());
    }

    @NativeType(value="FT_Byte")
    public byte panose(int index) {
        return TT_OS2.npanose(this.address(), index);
    }

    @NativeType(value="FT_ULong")
    public long ulUnicodeRange1() {
        return TT_OS2.nulUnicodeRange1(this.address());
    }

    @NativeType(value="FT_ULong")
    public long ulUnicodeRange2() {
        return TT_OS2.nulUnicodeRange2(this.address());
    }

    @NativeType(value="FT_ULong")
    public long ulUnicodeRange3() {
        return TT_OS2.nulUnicodeRange3(this.address());
    }

    @NativeType(value="FT_ULong")
    public long ulUnicodeRange4() {
        return TT_OS2.nulUnicodeRange4(this.address());
    }

    @NativeType(value="FT_Char[4]")
    public ByteBuffer achVendID() {
        return TT_OS2.nachVendID(this.address());
    }

    @NativeType(value="FT_Char")
    public byte achVendID(int index) {
        return TT_OS2.nachVendID(this.address(), index);
    }

    @NativeType(value="FT_UShort")
    public short fsSelection() {
        return TT_OS2.nfsSelection(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usFirstCharIndex() {
        return TT_OS2.nusFirstCharIndex(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usLastCharIndex() {
        return TT_OS2.nusLastCharIndex(this.address());
    }

    @NativeType(value="FT_Short")
    public short sTypoAscender() {
        return TT_OS2.nsTypoAscender(this.address());
    }

    @NativeType(value="FT_Short")
    public short sTypoDescender() {
        return TT_OS2.nsTypoDescender(this.address());
    }

    @NativeType(value="FT_Short")
    public short sTypoLineGap() {
        return TT_OS2.nsTypoLineGap(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usWinAscent() {
        return TT_OS2.nusWinAscent(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usWinDescent() {
        return TT_OS2.nusWinDescent(this.address());
    }

    @NativeType(value="FT_ULong")
    public long ulCodePageRange1() {
        return TT_OS2.nulCodePageRange1(this.address());
    }

    @NativeType(value="FT_ULong")
    public long ulCodePageRange2() {
        return TT_OS2.nulCodePageRange2(this.address());
    }

    @NativeType(value="FT_Short")
    public short sxHeight() {
        return TT_OS2.nsxHeight(this.address());
    }

    @NativeType(value="FT_Short")
    public short sCapHeight() {
        return TT_OS2.nsCapHeight(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usDefaultChar() {
        return TT_OS2.nusDefaultChar(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usBreakChar() {
        return TT_OS2.nusBreakChar(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usMaxContext() {
        return TT_OS2.nusMaxContext(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usLowerOpticalPointSize() {
        return TT_OS2.nusLowerOpticalPointSize(this.address());
    }

    @NativeType(value="FT_UShort")
    public short usUpperOpticalPointSize() {
        return TT_OS2.nusUpperOpticalPointSize(this.address());
    }

    public static TT_OS2 create(long address) {
        return new TT_OS2(address, null);
    }

    @Nullable
    public static TT_OS2 createSafe(long address) {
        return address == 0L ? null : new TT_OS2(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static short nversion(long struct) {
        return UNSAFE.getShort(null, struct + (long)VERSION);
    }

    public static short nxAvgCharWidth(long struct) {
        return UNSAFE.getShort(null, struct + (long)XAVGCHARWIDTH);
    }

    public static short nusWeightClass(long struct) {
        return UNSAFE.getShort(null, struct + (long)USWEIGHTCLASS);
    }

    public static short nusWidthClass(long struct) {
        return UNSAFE.getShort(null, struct + (long)USWIDTHCLASS);
    }

    public static short nfsType(long struct) {
        return UNSAFE.getShort(null, struct + (long)FSTYPE);
    }

    public static short nySubscriptXSize(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUBSCRIPTXSIZE);
    }

    public static short nySubscriptYSize(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUBSCRIPTYSIZE);
    }

    public static short nySubscriptXOffset(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUBSCRIPTXOFFSET);
    }

    public static short nySubscriptYOffset(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUBSCRIPTYOFFSET);
    }

    public static short nySuperscriptXSize(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUPERSCRIPTXSIZE);
    }

    public static short nySuperscriptYSize(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUPERSCRIPTYSIZE);
    }

    public static short nySuperscriptXOffset(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUPERSCRIPTXOFFSET);
    }

    public static short nySuperscriptYOffset(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSUPERSCRIPTYOFFSET);
    }

    public static short nyStrikeoutSize(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSTRIKEOUTSIZE);
    }

    public static short nyStrikeoutPosition(long struct) {
        return UNSAFE.getShort(null, struct + (long)YSTRIKEOUTPOSITION);
    }

    public static short nsFamilyClass(long struct) {
        return UNSAFE.getShort(null, struct + (long)SFAMILYCLASS);
    }

    public static ByteBuffer npanose(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)PANOSE, 10);
    }

    public static byte npanose(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)PANOSE + Checks.check(index, 10) * 1L);
    }

    public static long nulUnicodeRange1(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ULUNICODERANGE1);
    }

    public static long nulUnicodeRange2(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ULUNICODERANGE2);
    }

    public static long nulUnicodeRange3(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ULUNICODERANGE3);
    }

    public static long nulUnicodeRange4(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ULUNICODERANGE4);
    }

    public static ByteBuffer nachVendID(long struct) {
        return MemoryUtil.memByteBuffer(struct + (long)ACHVENDID, 4);
    }

    public static byte nachVendID(long struct, int index) {
        return UNSAFE.getByte(null, struct + (long)ACHVENDID + Checks.check(index, 4) * 1L);
    }

    public static short nfsSelection(long struct) {
        return UNSAFE.getShort(null, struct + (long)FSSELECTION);
    }

    public static short nusFirstCharIndex(long struct) {
        return UNSAFE.getShort(null, struct + (long)USFIRSTCHARINDEX);
    }

    public static short nusLastCharIndex(long struct) {
        return UNSAFE.getShort(null, struct + (long)USLASTCHARINDEX);
    }

    public static short nsTypoAscender(long struct) {
        return UNSAFE.getShort(null, struct + (long)STYPOASCENDER);
    }

    public static short nsTypoDescender(long struct) {
        return UNSAFE.getShort(null, struct + (long)STYPODESCENDER);
    }

    public static short nsTypoLineGap(long struct) {
        return UNSAFE.getShort(null, struct + (long)STYPOLINEGAP);
    }

    public static short nusWinAscent(long struct) {
        return UNSAFE.getShort(null, struct + (long)USWINASCENT);
    }

    public static short nusWinDescent(long struct) {
        return UNSAFE.getShort(null, struct + (long)USWINDESCENT);
    }

    public static long nulCodePageRange1(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ULCODEPAGERANGE1);
    }

    public static long nulCodePageRange2(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)ULCODEPAGERANGE2);
    }

    public static short nsxHeight(long struct) {
        return UNSAFE.getShort(null, struct + (long)SXHEIGHT);
    }

    public static short nsCapHeight(long struct) {
        return UNSAFE.getShort(null, struct + (long)SCAPHEIGHT);
    }

    public static short nusDefaultChar(long struct) {
        return UNSAFE.getShort(null, struct + (long)USDEFAULTCHAR);
    }

    public static short nusBreakChar(long struct) {
        return UNSAFE.getShort(null, struct + (long)USBREAKCHAR);
    }

    public static short nusMaxContext(long struct) {
        return UNSAFE.getShort(null, struct + (long)USMAXCONTEXT);
    }

    public static short nusLowerOpticalPointSize(long struct) {
        return UNSAFE.getShort(null, struct + (long)USLOWEROPTICALPOINTSIZE);
    }

    public static short nusUpperOpticalPointSize(long struct) {
        return UNSAFE.getShort(null, struct + (long)USUPPEROPTICALPOINTSIZE);
    }

    static {
        Struct.Layout layout = TT_OS2.__struct(TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__array(1, 10), TT_OS2.__member(CLONG_SIZE), TT_OS2.__member(CLONG_SIZE), TT_OS2.__member(CLONG_SIZE), TT_OS2.__member(CLONG_SIZE), TT_OS2.__array(1, 4), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(CLONG_SIZE), TT_OS2.__member(CLONG_SIZE), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2), TT_OS2.__member(2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        VERSION = layout.offsetof(0);
        XAVGCHARWIDTH = layout.offsetof(1);
        USWEIGHTCLASS = layout.offsetof(2);
        USWIDTHCLASS = layout.offsetof(3);
        FSTYPE = layout.offsetof(4);
        YSUBSCRIPTXSIZE = layout.offsetof(5);
        YSUBSCRIPTYSIZE = layout.offsetof(6);
        YSUBSCRIPTXOFFSET = layout.offsetof(7);
        YSUBSCRIPTYOFFSET = layout.offsetof(8);
        YSUPERSCRIPTXSIZE = layout.offsetof(9);
        YSUPERSCRIPTYSIZE = layout.offsetof(10);
        YSUPERSCRIPTXOFFSET = layout.offsetof(11);
        YSUPERSCRIPTYOFFSET = layout.offsetof(12);
        YSTRIKEOUTSIZE = layout.offsetof(13);
        YSTRIKEOUTPOSITION = layout.offsetof(14);
        SFAMILYCLASS = layout.offsetof(15);
        PANOSE = layout.offsetof(16);
        ULUNICODERANGE1 = layout.offsetof(17);
        ULUNICODERANGE2 = layout.offsetof(18);
        ULUNICODERANGE3 = layout.offsetof(19);
        ULUNICODERANGE4 = layout.offsetof(20);
        ACHVENDID = layout.offsetof(21);
        FSSELECTION = layout.offsetof(22);
        USFIRSTCHARINDEX = layout.offsetof(23);
        USLASTCHARINDEX = layout.offsetof(24);
        STYPOASCENDER = layout.offsetof(25);
        STYPODESCENDER = layout.offsetof(26);
        STYPOLINEGAP = layout.offsetof(27);
        USWINASCENT = layout.offsetof(28);
        USWINDESCENT = layout.offsetof(29);
        ULCODEPAGERANGE1 = layout.offsetof(30);
        ULCODEPAGERANGE2 = layout.offsetof(31);
        SXHEIGHT = layout.offsetof(32);
        SCAPHEIGHT = layout.offsetof(33);
        USDEFAULTCHAR = layout.offsetof(34);
        USBREAKCHAR = layout.offsetof(35);
        USMAXCONTEXT = layout.offsetof(36);
        USLOWEROPTICALPOINTSIZE = layout.offsetof(37);
        USUPPEROPTICALPOINTSIZE = layout.offsetof(38);
    }

    public static class Buffer
    extends StructBuffer<TT_OS2, Buffer> {
        private static final TT_OS2 ELEMENT_FACTORY = TT_OS2.create(-1L);

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
        protected TT_OS2 getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_UShort")
        public short version() {
            return TT_OS2.nversion(this.address());
        }

        @NativeType(value="FT_Short")
        public short xAvgCharWidth() {
            return TT_OS2.nxAvgCharWidth(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usWeightClass() {
            return TT_OS2.nusWeightClass(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usWidthClass() {
            return TT_OS2.nusWidthClass(this.address());
        }

        @NativeType(value="FT_UShort")
        public short fsType() {
            return TT_OS2.nfsType(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySubscriptXSize() {
            return TT_OS2.nySubscriptXSize(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySubscriptYSize() {
            return TT_OS2.nySubscriptYSize(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySubscriptXOffset() {
            return TT_OS2.nySubscriptXOffset(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySubscriptYOffset() {
            return TT_OS2.nySubscriptYOffset(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySuperscriptXSize() {
            return TT_OS2.nySuperscriptXSize(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySuperscriptYSize() {
            return TT_OS2.nySuperscriptYSize(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySuperscriptXOffset() {
            return TT_OS2.nySuperscriptXOffset(this.address());
        }

        @NativeType(value="FT_Short")
        public short ySuperscriptYOffset() {
            return TT_OS2.nySuperscriptYOffset(this.address());
        }

        @NativeType(value="FT_Short")
        public short yStrikeoutSize() {
            return TT_OS2.nyStrikeoutSize(this.address());
        }

        @NativeType(value="FT_Short")
        public short yStrikeoutPosition() {
            return TT_OS2.nyStrikeoutPosition(this.address());
        }

        @NativeType(value="FT_Short")
        public short sFamilyClass() {
            return TT_OS2.nsFamilyClass(this.address());
        }

        @NativeType(value="FT_Byte[10]")
        public ByteBuffer panose() {
            return TT_OS2.npanose(this.address());
        }

        @NativeType(value="FT_Byte")
        public byte panose(int index) {
            return TT_OS2.npanose(this.address(), index);
        }

        @NativeType(value="FT_ULong")
        public long ulUnicodeRange1() {
            return TT_OS2.nulUnicodeRange1(this.address());
        }

        @NativeType(value="FT_ULong")
        public long ulUnicodeRange2() {
            return TT_OS2.nulUnicodeRange2(this.address());
        }

        @NativeType(value="FT_ULong")
        public long ulUnicodeRange3() {
            return TT_OS2.nulUnicodeRange3(this.address());
        }

        @NativeType(value="FT_ULong")
        public long ulUnicodeRange4() {
            return TT_OS2.nulUnicodeRange4(this.address());
        }

        @NativeType(value="FT_Char[4]")
        public ByteBuffer achVendID() {
            return TT_OS2.nachVendID(this.address());
        }

        @NativeType(value="FT_Char")
        public byte achVendID(int index) {
            return TT_OS2.nachVendID(this.address(), index);
        }

        @NativeType(value="FT_UShort")
        public short fsSelection() {
            return TT_OS2.nfsSelection(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usFirstCharIndex() {
            return TT_OS2.nusFirstCharIndex(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usLastCharIndex() {
            return TT_OS2.nusLastCharIndex(this.address());
        }

        @NativeType(value="FT_Short")
        public short sTypoAscender() {
            return TT_OS2.nsTypoAscender(this.address());
        }

        @NativeType(value="FT_Short")
        public short sTypoDescender() {
            return TT_OS2.nsTypoDescender(this.address());
        }

        @NativeType(value="FT_Short")
        public short sTypoLineGap() {
            return TT_OS2.nsTypoLineGap(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usWinAscent() {
            return TT_OS2.nusWinAscent(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usWinDescent() {
            return TT_OS2.nusWinDescent(this.address());
        }

        @NativeType(value="FT_ULong")
        public long ulCodePageRange1() {
            return TT_OS2.nulCodePageRange1(this.address());
        }

        @NativeType(value="FT_ULong")
        public long ulCodePageRange2() {
            return TT_OS2.nulCodePageRange2(this.address());
        }

        @NativeType(value="FT_Short")
        public short sxHeight() {
            return TT_OS2.nsxHeight(this.address());
        }

        @NativeType(value="FT_Short")
        public short sCapHeight() {
            return TT_OS2.nsCapHeight(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usDefaultChar() {
            return TT_OS2.nusDefaultChar(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usBreakChar() {
            return TT_OS2.nusBreakChar(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usMaxContext() {
            return TT_OS2.nusMaxContext(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usLowerOpticalPointSize() {
            return TT_OS2.nusLowerOpticalPointSize(this.address());
        }

        @NativeType(value="FT_UShort")
        public short usUpperOpticalPointSize() {
            return TT_OS2.nusUpperOpticalPointSize(this.address());
        }
    }
}
