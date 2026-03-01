package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.BlockLightStorage;
import net.minecraft.world.lighting.LightEngine;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BlockLightEngine
extends LightEngine<BlockLightStorage.StorageMap, BlockLightStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockPos.Mutable scratchPos = new BlockPos.Mutable();

    public BlockLightEngine(IChunkLightProvider p_i51301_1_) {
        super(p_i51301_1_, LightType.BLOCK, new BlockLightStorage(p_i51301_1_));
    }

    private int getLightValue(long worldPos) {
        int i = BlockPos.unpackX(worldPos);
        int j = BlockPos.unpackY(worldPos);
        int k = BlockPos.unpackZ(worldPos);
        IBlockReader iblockreader = this.chunkProvider.getChunkForLight(i >> 4, k >> 4);
        return iblockreader != null ? iblockreader.getLightValue(this.scratchPos.setPos(i, j, k)) : 0;
    }

    @Override
    protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
        VoxelShape voxelshape1;
        int k;
        int j;
        if (endPos == Long.MAX_VALUE) {
            return 15;
        }
        if (startPos == Long.MAX_VALUE) {
            return startLevel + 15 - this.getLightValue(endPos);
        }
        if (startLevel >= 15) {
            return startLevel;
        }
        int i = Integer.signum(BlockPos.unpackX(endPos) - BlockPos.unpackX(startPos));
        Direction direction = Direction.byLong(i, j = Integer.signum(BlockPos.unpackY(endPos) - BlockPos.unpackY(startPos)), k = Integer.signum(BlockPos.unpackZ(endPos) - BlockPos.unpackZ(startPos)));
        if (direction == null) {
            return 15;
        }
        MutableInt mutableint = new MutableInt();
        BlockState blockstate = this.getBlockAndOpacity(endPos, mutableint);
        if (mutableint.getValue() >= 15) {
            return 15;
        }
        BlockState blockstate1 = this.getBlockAndOpacity(startPos, null);
        VoxelShape voxelshape = this.getVoxelShape(blockstate1, startPos, direction);
        return VoxelShapes.faceShapeCovers(voxelshape, voxelshape1 = this.getVoxelShape(blockstate, endPos, direction.getOpposite())) ? 15 : startLevel + Math.max(1, mutableint.getValue());
    }

    @Override
    protected void notifyNeighbors(long pos, int level, boolean isDecreasing) {
        long i = SectionPos.worldToSection(pos);
        for (Direction direction : DIRECTIONS) {
            long j = BlockPos.offset(pos, direction);
            long k = SectionPos.worldToSection(j);
            if (i != k && !((BlockLightStorage)this.storage).hasSection(k)) continue;
            this.propagateLevel(pos, j, level, isDecreasing);
        }
    }

    @Override
    protected int computeLevel(long pos, long excludedSourcePos, int level) {
        int i = level;
        if (Long.MAX_VALUE != excludedSourcePos) {
            int j = this.getEdgeLevel(Long.MAX_VALUE, pos, 0);
            if (level > j) {
                i = j;
            }
            if (i == 0) {
                return i;
            }
        }
        long j1 = SectionPos.worldToSection(pos);
        NibbleArray nibblearray = ((BlockLightStorage)this.storage).getArray(j1, true);
        for (Direction direction : DIRECTIONS) {
            long l;
            NibbleArray nibblearray1;
            long k = BlockPos.offset(pos, direction);
            if (k == excludedSourcePos || (nibblearray1 = j1 == (l = SectionPos.worldToSection(k)) ? nibblearray : ((BlockLightStorage)this.storage).getArray(l, true)) == null) continue;
            int i1 = this.getEdgeLevel(k, pos, this.getLevelFromArray(nibblearray1, k));
            if (i > i1) {
                i = i1;
            }
            if (i != 0) continue;
            return i;
        }
        return i;
    }

    @Override
    public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
        ((BlockLightStorage)this.storage).processAllLevelUpdates();
        this.scheduleUpdate(Long.MAX_VALUE, p_215623_1_.toLong(), 15 - p_215623_2_, true);
    }
}
