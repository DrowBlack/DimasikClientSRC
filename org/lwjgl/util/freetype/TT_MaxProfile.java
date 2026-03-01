package org.lwjgl.util.freetype;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;

public class TT_MaxProfile
extends Struct<TT_MaxProfile> {
    public static final int SIZEOF;
    public static final int ALIGNOF;
    public static final int VERSION;
    public static final int NUMGLYPHS;
    public static final int MAXPOINTS;
    public static final int MAXCONTOURS;
    public static final int MAXCOMPOSITEPOINTS;
    public static final int MAXCOMPOSITECONTOURS;
    public static final int MAXZONES;
    public static final int MAXTWILIGHTPOINTS;
    public static final int MAXSTORAGE;
    public static final int MAXFUNCTIONDEFS;
    public static final int MAXINSTRUCTIONDEFS;
    public static final int MAXSTACKELEMENTS;
    public static final int MAXSIZEOFINSTRUCTIONS;
    public static final int MAXCOMPONENTELEMENTS;
    public static final int MAXCOMPONENTDEPTH;

    protected TT_MaxProfile(long address, @Nullable ByteBuffer container) {
        super(address, container);
    }

    @Override
    protected TT_MaxProfile create(long address, @Nullable ByteBuffer container) {
        return new TT_MaxProfile(address, container);
    }

    public TT_MaxProfile(ByteBuffer container) {
        super(MemoryUtil.memAddress(container), TT_MaxProfile.__checkContainer(container, SIZEOF));
    }

    @Override
    public int sizeof() {
        return SIZEOF;
    }

    @NativeType(value="FT_Fixed")
    public long version() {
        return TT_MaxProfile.nversion(this.address());
    }

    @NativeType(value="FT_UShort")
    public short numGlyphs() {
        return TT_MaxProfile.nnumGlyphs(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxPoints() {
        return TT_MaxProfile.nmaxPoints(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxContours() {
        return TT_MaxProfile.nmaxContours(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxCompositePoints() {
        return TT_MaxProfile.nmaxCompositePoints(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxCompositeContours() {
        return TT_MaxProfile.nmaxCompositeContours(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxZones() {
        return TT_MaxProfile.nmaxZones(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxTwilightPoints() {
        return TT_MaxProfile.nmaxTwilightPoints(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxStorage() {
        return TT_MaxProfile.nmaxStorage(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxFunctionDefs() {
        return TT_MaxProfile.nmaxFunctionDefs(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxInstructionDefs() {
        return TT_MaxProfile.nmaxInstructionDefs(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxStackElements() {
        return TT_MaxProfile.nmaxStackElements(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxSizeOfInstructions() {
        return TT_MaxProfile.nmaxSizeOfInstructions(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxComponentElements() {
        return TT_MaxProfile.nmaxComponentElements(this.address());
    }

    @NativeType(value="FT_UShort")
    public short maxComponentDepth() {
        return TT_MaxProfile.nmaxComponentDepth(this.address());
    }

    public static TT_MaxProfile create(long address) {
        return new TT_MaxProfile(address, null);
    }

    @Nullable
    public static TT_MaxProfile createSafe(long address) {
        return address == 0L ? null : new TT_MaxProfile(address, null);
    }

    public static Buffer create(long address, int capacity) {
        return new Buffer(address, capacity);
    }

    @Nullable
    public static Buffer createSafe(long address, int capacity) {
        return address == 0L ? null : new Buffer(address, capacity);
    }

    public static long nversion(long struct) {
        return MemoryUtil.memGetCLong(struct + (long)VERSION);
    }

    public static short nnumGlyphs(long struct) {
        return UNSAFE.getShort(null, struct + (long)NUMGLYPHS);
    }

    public static short nmaxPoints(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXPOINTS);
    }

    public static short nmaxContours(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXCONTOURS);
    }

    public static short nmaxCompositePoints(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXCOMPOSITEPOINTS);
    }

    public static short nmaxCompositeContours(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXCOMPOSITECONTOURS);
    }

    public static short nmaxZones(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXZONES);
    }

    public static short nmaxTwilightPoints(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXTWILIGHTPOINTS);
    }

    public static short nmaxStorage(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXSTORAGE);
    }

    public static short nmaxFunctionDefs(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXFUNCTIONDEFS);
    }

    public static short nmaxInstructionDefs(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXINSTRUCTIONDEFS);
    }

    public static short nmaxStackElements(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXSTACKELEMENTS);
    }

    public static short nmaxSizeOfInstructions(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXSIZEOFINSTRUCTIONS);
    }

    public static short nmaxComponentElements(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXCOMPONENTELEMENTS);
    }

    public static short nmaxComponentDepth(long struct) {
        return UNSAFE.getShort(null, struct + (long)MAXCOMPONENTDEPTH);
    }

    static {
        Struct.Layout layout = TT_MaxProfile.__struct(TT_MaxProfile.__member(CLONG_SIZE), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2), TT_MaxProfile.__member(2));
        SIZEOF = layout.getSize();
        ALIGNOF = layout.getAlignment();
        VERSION = layout.offsetof(0);
        NUMGLYPHS = layout.offsetof(1);
        MAXPOINTS = layout.offsetof(2);
        MAXCONTOURS = layout.offsetof(3);
        MAXCOMPOSITEPOINTS = layout.offsetof(4);
        MAXCOMPOSITECONTOURS = layout.offsetof(5);
        MAXZONES = layout.offsetof(6);
        MAXTWILIGHTPOINTS = layout.offsetof(7);
        MAXSTORAGE = layout.offsetof(8);
        MAXFUNCTIONDEFS = layout.offsetof(9);
        MAXINSTRUCTIONDEFS = layout.offsetof(10);
        MAXSTACKELEMENTS = layout.offsetof(11);
        MAXSIZEOFINSTRUCTIONS = layout.offsetof(12);
        MAXCOMPONENTELEMENTS = layout.offsetof(13);
        MAXCOMPONENTDEPTH = layout.offsetof(14);
    }

    public static class Buffer
    extends StructBuffer<TT_MaxProfile, Buffer> {
        private static final TT_MaxProfile ELEMENT_FACTORY = TT_MaxProfile.create(-1L);

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
        protected TT_MaxProfile getElementFactory() {
            return ELEMENT_FACTORY;
        }

        @NativeType(value="FT_Fixed")
        public long version() {
            return TT_MaxProfile.nversion(this.address());
        }

        @NativeType(value="FT_UShort")
        public short numGlyphs() {
            return TT_MaxProfile.nnumGlyphs(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxPoints() {
            return TT_MaxProfile.nmaxPoints(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxContours() {
            return TT_MaxProfile.nmaxContours(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxCompositePoints() {
            return TT_MaxProfile.nmaxCompositePoints(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxCompositeContours() {
            return TT_MaxProfile.nmaxCompositeContours(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxZones() {
            return TT_MaxProfile.nmaxZones(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxTwilightPoints() {
            return TT_MaxProfile.nmaxTwilightPoints(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxStorage() {
            return TT_MaxProfile.nmaxStorage(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxFunctionDefs() {
            return TT_MaxProfile.nmaxFunctionDefs(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxInstructionDefs() {
            return TT_MaxProfile.nmaxInstructionDefs(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxStackElements() {
            return TT_MaxProfile.nmaxStackElements(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxSizeOfInstructions() {
            return TT_MaxProfile.nmaxSizeOfInstructions(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxComponentElements() {
            return TT_MaxProfile.nmaxComponentElements(this.address());
        }

        @NativeType(value="FT_UShort")
        public short maxComponentDepth() {
            return TT_MaxProfile.nmaxComponentDepth(this.address());
        }
    }
}
