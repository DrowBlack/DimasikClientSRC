package net.minecraft.util.math;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;

public class ChunkPos {
    public static final long SENTINEL = ChunkPos.asLong(1875016, 1875016);
    public final int x;
    public final int z;
    private int cachedHashCode = 0;

    public ChunkPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public ChunkPos(BlockPos pos) {
        this.x = pos.getX() >> 4;
        this.z = pos.getZ() >> 4;
    }

    public ChunkPos(long longIn) {
        this.x = (int)longIn;
        this.z = (int)(longIn >> 32);
    }

    public long asLong() {
        return ChunkPos.asLong(this.x, this.z);
    }

    public static long asLong(int x, int z) {
        return (long)x & 0xFFFFFFFFL | ((long)z & 0xFFFFFFFFL) << 32;
    }

    public static int getX(long chunkAsLong) {
        return (int)(chunkAsLong & 0xFFFFFFFFL);
    }

    public static int getZ(long chunkAsLong) {
        return (int)(chunkAsLong >>> 32 & 0xFFFFFFFFL);
    }

    public int hashCode() {
        if (this.cachedHashCode != 0) {
            return this.cachedHashCode;
        }
        int i = 1664525 * this.x + 1013904223;
        int j = 1664525 * (this.z ^ 0xDEADBEEF) + 1013904223;
        this.cachedHashCode = i ^ j;
        return this.cachedHashCode;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof ChunkPos)) {
            return false;
        }
        ChunkPos chunkpos = (ChunkPos)p_equals_1_;
        return this.x == chunkpos.x && this.z == chunkpos.z;
    }

    public int getXStart() {
        return this.x << 4;
    }

    public int getZStart() {
        return this.z << 4;
    }

    public int getXEnd() {
        return (this.x << 4) + 15;
    }

    public int getZEnd() {
        return (this.z << 4) + 15;
    }

    public int getRegionCoordX() {
        return this.x >> 5;
    }

    public int getRegionCoordZ() {
        return this.z >> 5;
    }

    public int getRegionPositionX() {
        return this.x & 0x1F;
    }

    public int getRegionPositionZ() {
        return this.z & 0x1F;
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPos asBlockPos() {
        return new BlockPos(this.getXStart(), 0, this.getZStart());
    }

    public int getChessboardDistance(ChunkPos chunkPosIn) {
        return Math.max(Math.abs(this.x - chunkPosIn.x), Math.abs(this.z - chunkPosIn.z));
    }

    public static Stream<ChunkPos> getAllInBox(ChunkPos center, int radius) {
        return ChunkPos.getAllInBox(new ChunkPos(center.x - radius, center.z - radius), new ChunkPos(center.x + radius, center.z + radius));
    }

    public static Stream<ChunkPos> getAllInBox(final ChunkPos start, final ChunkPos end) {
        int i = Math.abs(start.x - end.x) + 1;
        int j = Math.abs(start.z - end.z) + 1;
        final int k = start.x < end.x ? 1 : -1;
        final int l = start.z < end.z ? 1 : -1;
        return StreamSupport.stream(new Spliterators.AbstractSpliterator<ChunkPos>((long)(i * j), 64){
            @Nullable
            private ChunkPos current;

            @Override
            public boolean tryAdvance(Consumer<? super ChunkPos> p_tryAdvance_1_) {
                if (this.current == null) {
                    this.current = start;
                } else {
                    int i1 = this.current.x;
                    int j1 = this.current.z;
                    if (i1 == end.x) {
                        if (j1 == end.z) {
                            return false;
                        }
                        this.current = new ChunkPos(start.x, j1 + l);
                    } else {
                        this.current = new ChunkPos(i1 + k, j1);
                    }
                }
                p_tryAdvance_1_.accept(this.current);
                return true;
            }
        }, false);
    }
}
