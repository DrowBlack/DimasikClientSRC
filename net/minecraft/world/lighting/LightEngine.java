package net.minecraft.world.lighting;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.lighting.IWorldLightListener;
import net.minecraft.world.lighting.LevelBasedGraph;
import net.minecraft.world.lighting.LightDataMap;
import net.minecraft.world.lighting.SectionLightStorage;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class LightEngine<M extends LightDataMap<M>, S extends SectionLightStorage<M>>
extends LevelBasedGraph
implements IWorldLightListener {
    private static final Direction[] DIRECTIONS = Direction.values();
    protected final IChunkLightProvider chunkProvider;
    protected final LightType type;
    protected final S storage;
    private boolean field_215629_e;
    protected final BlockPos.Mutable scratchPos = new BlockPos.Mutable();
    private final long[] recentPositions = new long[2];
    private final IBlockReader[] recentChunks = new IBlockReader[2];

    public LightEngine(IChunkLightProvider chunkLightProvider, LightType lightTypeIn, S storageIn) {
        super(16, 256, 8192);
        this.chunkProvider = chunkLightProvider;
        this.type = lightTypeIn;
        this.storage = storageIn;
        this.invalidateCaches();
    }

    @Override
    protected void scheduleUpdate(long worldPos) {
        ((SectionLightStorage)this.storage).processAllLevelUpdates();
        if (((SectionLightStorage)this.storage).hasSection(SectionPos.worldToSection(worldPos))) {
            super.scheduleUpdate(worldPos);
        }
    }

    @Nullable
    private IBlockReader getChunkReader(int chunkX, int chunkZ) {
        long i = ChunkPos.asLong(chunkX, chunkZ);
        for (int j = 0; j < 2; ++j) {
            if (i != this.recentPositions[j]) continue;
            return this.recentChunks[j];
        }
        IBlockReader iblockreader = this.chunkProvider.getChunkForLight(chunkX, chunkZ);
        for (int k = 1; k > 0; --k) {
            this.recentPositions[k] = this.recentPositions[k - 1];
            this.recentChunks[k] = this.recentChunks[k - 1];
        }
        this.recentPositions[0] = i;
        this.recentChunks[0] = iblockreader;
        return iblockreader;
    }

    private void invalidateCaches() {
        Arrays.fill(this.recentPositions, ChunkPos.SENTINEL);
        Arrays.fill(this.recentChunks, null);
    }

    protected BlockState getBlockAndOpacity(long pos, @Nullable MutableInt opacityOut) {
        boolean flag;
        int j;
        if (pos == Long.MAX_VALUE) {
            if (opacityOut != null) {
                opacityOut.setValue(0);
            }
            return Blocks.AIR.getDefaultState();
        }
        int i = SectionPos.toChunk(BlockPos.unpackX(pos));
        IBlockReader iblockreader = this.getChunkReader(i, j = SectionPos.toChunk(BlockPos.unpackZ(pos)));
        if (iblockreader == null) {
            if (opacityOut != null) {
                opacityOut.setValue(16);
            }
            return Blocks.BEDROCK.getDefaultState();
        }
        this.scratchPos.setPos(pos);
        BlockState blockstate = iblockreader.getBlockState(this.scratchPos);
        boolean bl = flag = blockstate.isSolid() && blockstate.isTransparent();
        if (opacityOut != null) {
            opacityOut.setValue(blockstate.getOpacity(this.chunkProvider.getWorld(), this.scratchPos));
        }
        return flag ? blockstate : Blocks.AIR.getDefaultState();
    }

    protected VoxelShape getVoxelShape(BlockState blockStateIn, long worldPos, Direction directionIn) {
        return blockStateIn.isSolid() ? blockStateIn.getFaceOcclusionShape(this.chunkProvider.getWorld(), this.scratchPos.setPos(worldPos), directionIn) : VoxelShapes.empty();
    }

    public static int func_215613_a(IBlockReader p_215613_0_, BlockState p_215613_1_, BlockPos p_215613_2_, BlockState p_215613_3_, BlockPos p_215613_4_, Direction p_215613_5_, int p_215613_6_) {
        boolean flag1;
        boolean flag = p_215613_1_.isSolid() && p_215613_1_.isTransparent();
        boolean bl = flag1 = p_215613_3_.isSolid() && p_215613_3_.isTransparent();
        if (!flag && !flag1) {
            return p_215613_6_;
        }
        VoxelShape voxelshape = flag ? p_215613_1_.getRenderShapeTrue(p_215613_0_, p_215613_2_) : VoxelShapes.empty();
        VoxelShape voxelshape1 = flag1 ? p_215613_3_.getRenderShapeTrue(p_215613_0_, p_215613_4_) : VoxelShapes.empty();
        return VoxelShapes.doAdjacentCubeSidesFillSquare(voxelshape, voxelshape1, p_215613_5_) ? 16 : p_215613_6_;
    }

    @Override
    protected boolean isRoot(long pos) {
        return pos == Long.MAX_VALUE;
    }

    @Override
    protected int computeLevel(long pos, long excludedSourcePos, int level) {
        return 0;
    }

    @Override
    protected int getLevel(long sectionPosIn) {
        return sectionPosIn == Long.MAX_VALUE ? 0 : 15 - ((SectionLightStorage)this.storage).getLight(sectionPosIn);
    }

    protected int getLevelFromArray(NibbleArray array, long worldPos) {
        return 15 - array.get(SectionPos.mask(BlockPos.unpackX(worldPos)), SectionPos.mask(BlockPos.unpackY(worldPos)), SectionPos.mask(BlockPos.unpackZ(worldPos)));
    }

    @Override
    protected void setLevel(long sectionPosIn, int level) {
        ((SectionLightStorage)this.storage).setLight(sectionPosIn, Math.min(15, 15 - level));
    }

    @Override
    protected int getEdgeLevel(long startPos, long endPos, int startLevel) {
        return 0;
    }

    public boolean func_215619_a() {
        return this.needsUpdate() || ((LevelBasedGraph)this.storage).needsUpdate() || ((SectionLightStorage)this.storage).hasSectionsToUpdate();
    }

    public int tick(int toUpdateCount, boolean updateSkyLight, boolean updateBlockLight) {
        if (!this.field_215629_e) {
            if (((LevelBasedGraph)this.storage).needsUpdate() && (toUpdateCount = ((LevelBasedGraph)this.storage).processUpdates(toUpdateCount)) == 0) {
                return toUpdateCount;
            }
            ((SectionLightStorage)this.storage).updateSections(this, updateSkyLight, updateBlockLight);
        }
        this.field_215629_e = true;
        if (this.needsUpdate()) {
            toUpdateCount = this.processUpdates(toUpdateCount);
            this.invalidateCaches();
            if (toUpdateCount == 0) {
                return toUpdateCount;
            }
        }
        this.field_215629_e = false;
        ((SectionLightStorage)this.storage).updateAndNotify();
        return toUpdateCount;
    }

    protected void setData(long sectionPosIn, @Nullable NibbleArray array, boolean p_215621_4_) {
        ((SectionLightStorage)this.storage).setData(sectionPosIn, array, p_215621_4_);
    }

    @Override
    @Nullable
    public NibbleArray getData(SectionPos p_215612_1_) {
        return ((SectionLightStorage)this.storage).getArray(p_215612_1_.asLong());
    }

    @Override
    public int getLightFor(BlockPos worldPos) {
        return ((SectionLightStorage)this.storage).getLightOrDefault(worldPos.toLong());
    }

    public String getDebugString(long sectionPosIn) {
        return "" + ((SectionLightStorage)this.storage).getLevel(sectionPosIn);
    }

    public void checkLight(BlockPos worldPos) {
        long i = worldPos.toLong();
        this.scheduleUpdate(i);
        for (Direction direction : DIRECTIONS) {
            this.scheduleUpdate(BlockPos.offset(i, direction));
        }
    }

    public void func_215623_a(BlockPos p_215623_1_, int p_215623_2_) {
    }

    @Override
    public void updateSectionStatus(SectionPos pos, boolean isEmpty) {
        ((SectionLightStorage)this.storage).updateSectionStatus(pos.asLong(), isEmpty);
    }

    public void func_215620_a(ChunkPos chunkPos, boolean p_215620_2_) {
        long i = SectionPos.toSectionColumnPos(SectionPos.asLong(chunkPos.x, 0, chunkPos.z));
        ((SectionLightStorage)this.storage).setColumnEnabled(i, p_215620_2_);
    }

    public void retainChunkData(ChunkPos pos, boolean retain) {
        long i = SectionPos.toSectionColumnPos(SectionPos.asLong(pos.x, 0, pos.z));
        ((SectionLightStorage)this.storage).retainChunkData(i, retain);
    }
}
