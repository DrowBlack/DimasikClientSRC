package net.minecraft.world.lighting;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.LightEngine;
import net.minecraft.world.lighting.SkyLightStorage;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SkyLightEngine
extends LightEngine<SkyLightStorage.StorageMap, SkyLightStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] CARDINALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public SkyLightEngine(IChunkLightProvider p_i51289_1_) {
        super(p_i51289_1_, LightType.SKY, new SkyLightStorage(p_i51289_1_));
    }

    @Override
    protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
        if (endPos == Long.MAX_VALUE) {
            return 15;
        }
        if (startPos == Long.MAX_VALUE) {
            if (!((SkyLightStorage)this.storage).func_215551_l(endPos)) {
                return 15;
            }
            startLevel = 0;
        }
        if (startLevel >= 15) {
            return startLevel;
        }
        MutableInt mutableint = new MutableInt();
        BlockState blockstate = this.getBlockAndOpacity(endPos, mutableint);
        if (mutableint.getValue() >= 15) {
            return 15;
        }
        int i = BlockPos.unpackX(startPos);
        int j = BlockPos.unpackY(startPos);
        int k = BlockPos.unpackZ(startPos);
        int l = BlockPos.unpackX(endPos);
        int i1 = BlockPos.unpackY(endPos);
        int j1 = BlockPos.unpackZ(endPos);
        boolean flag = i == l && k == j1;
        int k1 = Integer.signum(l - i);
        int l1 = Integer.signum(i1 - j);
        int i2 = Integer.signum(j1 - k);
        Direction direction = startPos == Long.MAX_VALUE ? Direction.DOWN : Direction.byLong(k1, l1, i2);
        BlockState blockstate1 = this.getBlockAndOpacity(startPos, null);
        if (direction != null) {
            VoxelShape voxelshape1;
            VoxelShape voxelshape = this.getVoxelShape(blockstate1, startPos, direction);
            if (VoxelShapes.faceShapeCovers(voxelshape, voxelshape1 = this.getVoxelShape(blockstate, endPos, direction.getOpposite()))) {
                return 15;
            }
        } else {
            VoxelShape voxelshape3 = this.getVoxelShape(blockstate1, startPos, Direction.DOWN);
            if (VoxelShapes.faceShapeCovers(voxelshape3, VoxelShapes.empty())) {
                return 15;
            }
            int j2 = flag ? -1 : 0;
            Direction direction1 = Direction.byLong(k1, j2, i2);
            if (direction1 == null) {
                return 15;
            }
            VoxelShape voxelshape2 = this.getVoxelShape(blockstate, endPos, direction1.getOpposite());
            if (VoxelShapes.faceShapeCovers(VoxelShapes.empty(), voxelshape2)) {
                return 15;
            }
        }
        boolean flag1 = startPos == Long.MAX_VALUE || flag && j > i1;
        return flag1 && startLevel == 0 && mutableint.getValue() == 0 ? 0 : startLevel + Math.max(1, mutableint.getValue());
    }

    @Override
    protected void notifyNeighbors(long pos, int level, boolean isDecreasing) {
        long l1;
        long i2;
        int i1;
        long i = SectionPos.worldToSection(pos);
        int j = BlockPos.unpackY(pos);
        int k = SectionPos.mask(j);
        int l = SectionPos.toChunk(j);
        if (k != 0) {
            i1 = 0;
        } else {
            int j1 = 0;
            while (!((SkyLightStorage)this.storage).hasSection(SectionPos.withOffset(i, 0, -j1 - 1, 0)) && ((SkyLightStorage)this.storage).isAboveBottom(l - j1 - 1)) {
                ++j1;
            }
            i1 = j1;
        }
        long i3 = BlockPos.offset(pos, 0, -1 - i1 * 16, 0);
        long k1 = SectionPos.worldToSection(i3);
        if (i == k1 || ((SkyLightStorage)this.storage).hasSection(k1)) {
            this.propagateLevel(pos, i3, level, isDecreasing);
        }
        if (i == (i2 = SectionPos.worldToSection(l1 = BlockPos.offset(pos, Direction.UP))) || ((SkyLightStorage)this.storage).hasSection(i2)) {
            this.propagateLevel(pos, l1, level, isDecreasing);
        }
        block1: for (Direction direction : CARDINALS) {
            int j2 = 0;
            do {
                long k2;
                long l2;
                if (i == (l2 = SectionPos.worldToSection(k2 = BlockPos.offset(pos, direction.getXOffset(), -j2, direction.getZOffset())))) {
                    this.propagateLevel(pos, k2, level, isDecreasing);
                    continue block1;
                }
                if (!((SkyLightStorage)this.storage).hasSection(l2)) continue;
                this.propagateLevel(pos, k2, level, isDecreasing);
            } while (++j2 <= i1 * 16);
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
        NibbleArray nibblearray = ((SkyLightStorage)this.storage).getArray(j1, true);
        for (Direction direction : DIRECTIONS) {
            int i1;
            long k = BlockPos.offset(pos, direction);
            long l = SectionPos.worldToSection(k);
            NibbleArray nibblearray1 = j1 == l ? nibblearray : ((SkyLightStorage)this.storage).getArray(l, true);
            if (nibblearray1 != null) {
                if (k == excludedSourcePos) continue;
                int k1 = this.getEdgeLevel(k, pos, this.getLevelFromArray(nibblearray1, k));
                if (i > k1) {
                    i = k1;
                }
                if (i != 0) continue;
                return i;
            }
            if (direction == Direction.DOWN) continue;
            k = BlockPos.atSectionBottomY(k);
            while (!((SkyLightStorage)this.storage).hasSection(l) && !((SkyLightStorage)this.storage).isAboveWorld(l)) {
                l = SectionPos.withOffset(l, Direction.UP);
                k = BlockPos.offset(k, 0, 16, 0);
            }
            NibbleArray nibblearray2 = ((SkyLightStorage)this.storage).getArray(l, true);
            if (k == excludedSourcePos) continue;
            if (nibblearray2 != null) {
                i1 = this.getEdgeLevel(k, pos, this.getLevelFromArray(nibblearray2, k));
            } else {
                int n = i1 = ((SkyLightStorage)this.storage).isSectionEnabled(l) ? 0 : 15;
            }
            if (i > i1) {
                i = i1;
            }
            if (i != 0) continue;
            return i;
        }
        return i;
    }

    @Override
    protected void scheduleUpdate(long worldPos) {
        ((SkyLightStorage)this.storage).processAllLevelUpdates();
        long i = SectionPos.worldToSection(worldPos);
        if (((SkyLightStorage)this.storage).hasSection(i)) {
            super.scheduleUpdate(worldPos);
        } else {
            worldPos = BlockPos.atSectionBottomY(worldPos);
            while (!((SkyLightStorage)this.storage).hasSection(i) && !((SkyLightStorage)this.storage).isAboveWorld(i)) {
                i = SectionPos.withOffset(i, Direction.UP);
                worldPos = BlockPos.offset(worldPos, 0, 16, 0);
            }
            if (((SkyLightStorage)this.storage).hasSection(i)) {
                super.scheduleUpdate(worldPos);
            }
        }
    }

    @Override
    public String getDebugString(long sectionPosIn) {
        return super.getDebugString(sectionPosIn) + (((SkyLightStorage)this.storage).isAboveWorld(sectionPosIn) ? "*" : "");
    }
}
