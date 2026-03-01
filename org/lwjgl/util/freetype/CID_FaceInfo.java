package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.CLongBuffer;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.util.freetype.CID_FaceDict;
import org.lwjgl.util.freetype.FT_BBox;
import org.lwjgl.util.freetype.PS_FontInfo;

@NativeType(value="struct CID_FaceInfoRec")
public class CID_FaceInfo
extends Struct<CID_FaceInfo> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int CID_FONT_NAME;
    public static final int CID_VERSION;
    public static final int CID_FONT_TYPE;
    public static final int REGISTRY;
    public static final int ORDERING;
    public static final int SUPPLEMENT;
    public static final int FONT_INFO;
    public static final int FONT_BBOX;
    public static final int UID_BASE;
    public static final int NUM_XUID;
    public static final int XUID;
    public static final int CIDMAP_OFFSET;
    public static final int FD_BYTES;
    public static final int GD_BYTES;
    public static final int CID_COUNT;
    public static final int NUM_DICTS;
    public static final int FONT_DICTS;
    public static final int DATA_OFFSET;

    protected CID_FaceInfo(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected CID_FaceInfo create(long address, @Nullable ByteBuffer container) {
        return new CID_FaceInfo(address, container);
    }

    public CID_FaceInfo(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), CID_FaceInfo.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_String *")
    public ByteBuffer cid_font_name() {
        return CID_FaceInfo.ncid_font_name(this.address());
    }

    @NativeType(value="FT_String *")
    public String cid_font_nameString() {
        return CID_FaceInfo.ncid_font_nameString(this.address());
    }

    @NativeType(value="FT_Fixed")
    public long cid_version() {
        return CID_FaceInfo.ncid_version(this.address());
    }

    @NativeType(value="FT_Int")
    public int cid_font_type() {
        return CID_FaceInfo.ncid_font_type(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer registry() {
        return CID_FaceInfo.nregistry(this.address());
    }

    @NativeType(value="FT_String *")
    public String registryString() {
        return CID_FaceInfo.nregistryString(this.address());
    }

    @NativeType(value="FT_String *")
    public ByteBuffer ordering() {
        return CID_FaceInfo.nordering(this.address());
    }

    @NativeType(value="FT_String *")
    public String orderingString() {
        return CID_FaceInfo.norderingString(this.address());
    }

    @NativeType(value="FT_Int")
    public int supplement() {
        return CID_FaceInfo.nsupplement(this.address());
    }

    @NativeType(value="PS_FontInfoRec")
    public PS_FontInfo font_info() {
        return CID_FaceInfo.nfont_info(this.address());
    }

    public FT_BBox font_bbox() {
        return CID_FaceInfo.nfont_bbox(this.address());
    }

    @NativeType(value="FT_ULong")
    public long uid_base() {
        return CID_FaceInfo.nuid_base(this.address());
    }

    @NativeType(value="FT_Int")
    public int num_xuid() {
        return CID_FaceInfo.nnum_xuid(this.address());
    }

    @NativeType(value="FT_ULong[16]")
    public CLongBuffer xuid() {
        return CID_FaceInfo.nxuid(this.address());
    }

    @NativeType(value="FT_ULong")
    public long xuid(int index) {
        return CID_FaceInfo.nxuid(this.address(), index);
    }

    @NativeType(value="FT_ULong")
    public long cidmap_offset() {
        return CID_FaceInfo.ncidmap_offset(this.address());
    }

    @NativeType(value="FT_UInt")
    public int fd_bytes() {
        return CID_FaceInfo.nfd_bytes(this.address());
    }

    @NativeType(value="FT_UInt")
    public int gd_bytes() {
        return CID_FaceInfo.ngd_bytes(this.address());
    }

    @NativeType(value="FT_ULong")
    public long cid_count() {
        return CID_FaceInfo.ncid_count(this.address());
    }

    @NativeType(value="FT_UInt")
    public int num_dicts() {
        return CID_FaceInfo.nnum_dicts(this.address());
    }

    public CID_FaceDict font_dicts() {
        return CID_FaceInfo.nfont_dicts(this.address());
    }

    @NativeType(value="FT_ULong")
    public long data_offset() {
        return CID_FaceInfo.ndata_offset(this.address());
    }

    public static CID_FaceInfo create(long address) {
        return new CID_FaceInfo(address, null);
    }

    @Nullable
    public static CID_FaceInfo createSafe(long address) {
        return address == 0L ? null : new CID_FaceInfo(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static ByteBuffer ncid_font_name(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)CID_FONT_NAME));
    }

    public static String ncid_font_nameString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)CID_FONT_NAME));
    }

    public static long ncid_version(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CID_VERSION);
    }

    public static int ncid_font_type(long struct) {
        return UNSAFE.getInt(null, struct + (long)CID_FONT_TYPE);
    }

    public static ByteBuffer nregistry(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)REGISTRY));
    }

    public static String nregistryString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)REGISTRY));
    }

    public static ByteBuffer nordering(long struct) {
        return MemoryUtil.memByteBufferNT1(MemoryUtil.memGetAddress(struct + (long)ORDERING));
    }

    public static String norderingString(long struct) {
        return MemoryUtil.memUTF8(MemoryUtil.memGetAddress(struct + (long)ORDERING));
    }

    public static int nsupplement(long struct) {
        return UNSAFE.getInt(null, struct + (long)SUPPLEMENT);
    }

    public static PS_FontInfo nfont_info(long struct) {
        return PS_FontInfo.create(struct + (long)FONT_INFO);
    }

    public static FT_BBox nfont_bbox(long struct) {
        return FT_BBox.create(struct + (long)FONT_BBOX);
    }

    public static long nuid_base(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)UID_BASE);
    }

    public static int nnum_xuid(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_XUID);
    }

    public static CLongBuffer nxuid(long struct) {
        return MemoryUtil.memCLongBuffer(struct + (long)XUID, 16);
    }

    public static long nxuid(long struct, int index) {
        return MemoryUtil.memGetCLong(struct + (long)XUID + Checks.check(index, 16) * (long)CLONG_SIZE);
    }

    public static long ncidmap_offset(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CIDMAP_OFFSET);
    }

    public static int nfd_bytes(long struct) {
        return UNSAFE.getInt(null, struct + (long)FD_BYTES);
    }

    public static int ngd_bytes(long struct) {
        return UNSAFE.getInt(null, struct + (long)GD_BYTES);
    }

    public static long ncid_count(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)CID_COUNT);
    }

    public static int nnum_dicts(long struct) {
        return UNSAFE.getInt(null, struct + (long)NUM_DICTS);
    }

    public static CID_FaceDict nfont_dicts(long struct) {
        return CID_FaceDict.create(MemoryUtil.memGetAddress(struct + (long)FONT_DICTS));
    }

    public static long ndata_offset(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)DATA_OFFSET);
    }

    static {
        Struct.Layout layout = CID_FaceInfo.__struct(CID_FaceInfo.__member(POINTER_SIZE), CID_FaceInfo.__member(CLONG_SIZE), CID_FaceInfo.__member(4), CID_FaceInfo.__member(POINTER_SIZE), CID_FaceInfo.__member(POINTER_SIZE), CID_FaceInfo.__member(4), CID_FaceInfo.__member(PS_FontInfo.SIZEOF, PS_FontInfo.ALIGNOF), CID_FaceInfo.__member(FT_BBox.SIZEOF, FT_BBox.ALIGNOF), CID_FaceInfo.__member(CLONG_SIZE), CID_FaceInfo.__member(4), CID_FaceInfo.__array(CLONG_SIZE, 16), CID_FaceInfo.__member(CLONG_SIZE), CID_FaceInfo.__member(4), CID_FaceInfo.__member(4), CID_FaceInfo.__member(CLONG_SIZE), CID_FaceInfo.__member(4), CID_FaceInfo.__member(POINTER_SIZE), CID_FaceInfo.__member(CLONG_SIZE));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        CID_FONT_NAME = layout.offsetof(0);
        CID_VERSION = layout.offsetof(1);
        CID_FONT_TYPE = layout.offsetof(2);
        REGISTRY = layout.offsetof(3);
        ORDERING = layout.offsetof(4);
        SUPPLEMENT = layout.offsetof(5);
        FONT_INFO = layout.offsetof(6);
        FONT_BBOX = layout.offsetof(7);
        UID_BASE = layout.offsetof(8);
        NUM_XUID = layout.offsetof(9);
        XUID = layout.offsetof(10);
        CIDMAP_OFFSET = layout.offsetof(11);
        FD_BYTES = layout.offsetof(12);
        GD_BYTES = layout.offsetof(13);
        CID_COUNT = layout.offsetof(14);
        NUM_DICTS = layout.offsetof(15);
        FONT_DICTS = layout.offsetof(16);
        DATA_OFFSET = layout.offsetof(17);
    }

    public static class Buffer
    extends StructBuffer<CID_FaceInfo, Buffer> {
        private static final CID_FaceInfo ELEMENT_FACTORY = CID_FaceInfo.create(-1L);

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
        protected CID_FaceInfo getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_String *")
        public ByteBuffer cid_font_name() {
            return CID_FaceInfo.ncid_font_name(this.address());
        }

        @NativeType(value="FT_String *")
        public String cid_font_nameString() {
            return CID_FaceInfo.ncid_font_nameString(this.address());
        }

        @NativeType(value="FT_Fixed")
        public long cid_version() {
            return CID_FaceInfo.ncid_version(this.address());
        }

        @NativeType(value="FT_Int")
        public int cid_font_type() {
            return CID_FaceInfo.ncid_font_type(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer registry() {
            return CID_FaceInfo.nregistry(this.address());
        }

        @NativeType(value="FT_String *")
        public String registryString() {
            return CID_FaceInfo.nregistryString(this.address());
        }

        @NativeType(value="FT_String *")
        public ByteBuffer ordering() {
            return CID_FaceInfo.nordering(this.address());
        }

        @NativeType(value="FT_String *")
        public String orderingString() {
            return CID_FaceInfo.norderingString(this.address());
        }

        @NativeType(value="FT_Int")
        public int supplement() {
            return CID_FaceInfo.nsupplement(this.address());
        }

        @NativeType(value="PS_FontInfoRec")
        public PS_FontInfo font_info() {
            return CID_FaceInfo.nfont_info(this.address());
        }

        public FT_BBox font_bbox() {
            return CID_FaceInfo.nfont_bbox(this.address());
        }

        @NativeType(value="FT_ULong")
        public long uid_base() {
            return CID_FaceInfo.nuid_base(this.address());
        }

        @NativeType(value="FT_Int")
        public int num_xuid() {
            return CID_FaceInfo.nnum_xuid(this.address());
        }

        @NativeType(value="FT_ULong[16]")
        public CLongBuffer xuid() {
            return CID_FaceInfo.nxuid(this.address());
        }

        @NativeType(value="FT_ULong")
        public long xuid(int index) {
            return CID_FaceInfo.nxuid(this.address(), index);
        }

        @NativeType(value="FT_ULong")
        public long cidmap_offset() {
            return CID_FaceInfo.ncidmap_offset(this.address());
        }

        @NativeType(value="FT_UInt")
        public int fd_bytes() {
            return CID_FaceInfo.nfd_bytes(this.address());
        }

        @NativeType(value="FT_UInt")
        public int gd_bytes() {
            return CID_FaceInfo.ngd_bytes(this.address());
        }

        @NativeType(value="FT_ULong")
        public long cid_count() {
            return CID_FaceInfo.ncid_count(this.address());
        }

        @NativeType(value="FT_UInt")
        public int num_dicts() {
            return CID_FaceInfo.nnum_dicts(this.address());
        }

        public CID_FaceDict font_dicts() {
            return CID_FaceInfo.nfont_dicts(this.address());
        }

        @NativeType(value="FT_ULong")
        public long data_offset() {
            return CID_FaceInfo.ndata_offset(this.address());
        }
    }
}
