package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.CubeCoordinateIterator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;

public class SectionPos
extends Vector3i {
    private SectionPos(int p_i50794_1_, int p_i50794_2_, int p_i50794_3_) {
        super(p_i50794_1_, p_i50794_2_, p_i50794_3_);
    }

    public static SectionPos of(int chunkX, int chunkY, int chunkZ) {
        return new SectionPos(chunkX, chunkY, chunkZ);
    }

    public static SectionPos from(BlockPos worldPos) {
        return new SectionPos(SectionPos.toChunk(worldPos.getX()), SectionPos.toChunk(worldPos.getY()), SectionPos.toChunk(worldPos.getZ()));
    }

    public static SectionPos from(ChunkPos xz, int y) {
        return new SectionPos(xz.x, y, xz.z);
    }

    public static SectionPos from(Entity p_218157_0_) {
        return new SectionPos(SectionPos.toChunk(MathHelper.floor(p_218157_0_.getPosX())), SectionPos.toChunk(MathHelper.floor(p_218157_0_.getPosY())), SectionPos.toChunk(MathHelper.floor(p_218157_0_.getPosZ())));
    }

    public static SectionPos from(long p_218170_0_) {
        return new SectionPos(SectionPos.extractX(p_218170_0_), SectionPos.extractY(p_218170_0_), SectionPos.extractZ(p_218170_0_));
    }

    public static long withOffset(long p_218172_0_, Direction p_218172_2_) {
        return SectionPos.withOffset(p_218172_0_, p_218172_2_.getXOffset(), p_218172_2_.getYOffset(), p_218172_2_.getZOffset());
    }

    public static long withOffset(long p_218174_0_, int dx, int dy, int dz) {
        return SectionPos.asLong(SectionPos.extractX(p_218174_0_) + dx, SectionPos.extractY(p_218174_0_) + dy, SectionPos.extractZ(p_218174_0_) + dz);
    }

    public static int toChunk(int worldCoord) {
        return worldCoord >> 4;
    }

    public static int mask(int p_218171_0_) {
        return p_218171_0_ & 0xF;
    }

    public static short toRelativeOffset(BlockPos p_218150_0_) {
        int i = SectionPos.mask(p_218150_0_.getX());
        int j = SectionPos.mask(p_218150_0_.getY());
        int k = SectionPos.mask(p_218150_0_.getZ());
        return (short)(i << 8 | k << 4 | j << 0);
    }

    public static int func_243641_a(short p_243641_0_) {
        return p_243641_0_ >>> 8 & 0xF;
    }

    public static int func_243642_b(short p_243642_0_) {
        return p_243642_0_ >>> 0 & 0xF;
    }

    public static int func_243643_c(short p_243643_0_) {
        return p_243643_0_ >>> 4 & 0xF;
    }

    public int func_243644_d(short p_243644_1_) {
        return this.getWorldStartX() + SectionPos.func_243641_a(p_243644_1_);
    }

    public int func_243645_e(short p_243645_1_) {
        return this.getWorldStartY() + SectionPos.func_243642_b(p_243645_1_);
    }

    public int func_243646_f(short p_243646_1_) {
        return this.getWorldStartZ() + SectionPos.func_243643_c(p_243646_1_);
    }

    public BlockPos func_243647_g(short p_243647_1_) {
        return new BlockPos(this.func_243644_d(p_243647_1_), this.func_243645_e(p_243647_1_), this.func_243646_f(p_243647_1_));
    }

    public static int toWorld(int chunkCoord) {
        return chunkCoord << 4;
    }

    public static int extractX(long packed) {
        return (int)(packed << 0 >> 42);
    }

    public static int extractY(long packed) {
        return (int)(packed << 44 >> 44);
    }

    public static int extractZ(long packed) {
        return (int)(packed << 22 >> 42);
    }

    public int getSectionX() {
        return this.getX();
    }

    public int getSectionY() {
        return this.getY();
    }

    public int getSectionZ() {
        return this.getZ();
    }

    public int getWorldStartX() {
        return this.getSectionX() << 4;
    }

    public int getWorldStartY() {
        return this.getSectionY() << 4;
    }

    public int getWorldStartZ() {
        return this.getSectionZ() << 4;
    }

    public int getWorldEndX() {
        return (this.getSectionX() << 4) + 15;
    }

    public int getWorldEndY() {
        return (this.getSectionY() << 4) + 15;
    }

    public int getWorldEndZ() {
        return (this.getSectionZ() << 4) + 15;
    }

    public static long worldToSection(long worldPos) {
        return SectionPos.asLong(SectionPos.toChunk(BlockPos.unpackX(worldPos)), SectionPos.toChunk(BlockPos.unpackY(worldPos)), SectionPos.toChunk(BlockPos.unpackZ(worldPos)));
    }

    public static long toSectionColumnPos(long p_218169_0_) {
        return p_218169_0_ & 0xFFFFFFFFFFF00000L;
    }

    public BlockPos asBlockPos() {
        return new BlockPos(SectionPos.toWorld(this.getSectionX()), SectionPos.toWorld(this.getSectionY()), SectionPos.toWorld(this.getSectionZ()));
    }

    public BlockPos getCenter() {
        int i = 8;
        return this.asBlockPos().add(8, 8, 8);
    }

    public ChunkPos asChunkPos() {
        return new ChunkPos(this.getSectionX(), this.getSectionZ());
    }

    public static long asLong(int p_218166_0_, int p_218166_1_, int p_218166_2_) {
        long i = 0L;
        i |= ((long)p_218166_0_ & 0x3FFFFFL) << 42;
        return (i |= ((long)p_218166_1_ & 0xFFFFFL) << 0) | ((long)p_218166_2_ & 0x3FFFFFL) << 20;
    }

    public long asLong() {
        return SectionPos.asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
    }

    public Stream<BlockPos> allBlocksWithin() {
        return BlockPos.getAllInBox(this.getWorldStartX(), this.getWorldStartY(), this.getWorldStartZ(), this.getWorldEndX(), this.getWorldEndY(), this.getWorldEndZ());
    }

    public static Stream<SectionPos> getAllInBox(SectionPos center, int radius) {
        int i = center.getSectionX();
        int j = center.getSectionY();
        int k = center.getSectionZ();
        return SectionPos.getAllInBox(i - radius, j - radius, k - radius, i + radius, j + radius, k + radius);
    }

    public static Stream<SectionPos> func_229421_b_(ChunkPos p_229421_0_, int p_229421_1_) {
        int i = p_229421_0_.x;
        int j = p_229421_0_.z;
        return SectionPos.getAllInBox(i - p_229421_1_, 0, j - p_229421_1_, i + p_229421_1_, 15, j + p_229421_1_);
    }

    public static Stream<SectionPos> getAllInBox(final int p_218168_0_, final int p_218168_1_, final int p_218168_2_, final int p_218168_3_, final int p_218168_4_, final int p_218168_5_) {
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<SectionPos>((long)((p_218168_3_ - p_218168_0_ + 1) * (p_218168_4_ - p_218168_1_ + 1) * (p_218168_5_ - p_218168_2_ + 1)), 64){
            final CubeCoordinateIterator field_218394_a;
            {
                super(est, additionalCharacteristics);
                this.field_218394_a = new CubeCoordinateIterator(p_218168_0_, p_218168_1_, p_218168_2_, p_218168_3_, p_218168_4_, p_218168_5_);
            }

            @Override
            public boolean tryAdvance(Consumer<? super SectionPos> p_tryAdvance_1_) {
                if (this.field_218394_a.hasNext()) {
                    p_tryAdvance_1_.accept(new SectionPos(this.field_218394_a.getX(), this.field_218394_a.getY(), this.field_218394_a.getZ()));
                    return true;
                }
                return false;
            }
        }, false);
    }
}
