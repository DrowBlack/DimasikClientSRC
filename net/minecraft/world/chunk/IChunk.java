package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IStructureReader;
import net.minecraft.world.ITickList;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import org.apache.logging.log4j.LogManager;

public interface IChunk
extends IBlockReader,
IStructureReader {
    @Nullable
    public BlockState setBlockState(BlockPos var1, BlockState var2, boolean var3);

    public void addTileEntity(BlockPos var1, TileEntity var2);

    public void addEntity(Entity var1);

    @Nullable
    default public ChunkSection getLastExtendedBlockStorage() {
        ChunkSection[] achunksection = this.getSections();
        for (int i = achunksection.length - 1; i >= 0; --i) {
            ChunkSection chunksection = achunksection[i];
            if (ChunkSection.isEmpty(chunksection)) continue;
            return chunksection;
        }
        return null;
    }

    default public int getTopFilledSegment() {
        ChunkSection chunksection = this.getLastExtendedBlockStorage();
        return chunksection == null ? 0 : chunksection.getYLocation();
    }

    public Set<BlockPos> getTileEntitiesPos();

    public ChunkSection[] getSections();

    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps();

    public void setHeightmap(Heightmap.Type var1, long[] var2);

    public Heightmap getHeightmap(Heightmap.Type var1);

    public int getTopBlockY(Heightmap.Type var1, int var2, int var3);

    public ChunkPos getPos();

    public void setLastSaveTime(long var1);

    public Map<Structure<?>, StructureStart<?>> getStructureStarts();

    public void setStructureStarts(Map<Structure<?>, StructureStart<?>> var1);

    default public boolean isEmptyBetween(int startY, int endY) {
        if (startY < 0) {
            startY = 0;
        }
        if (endY >= 256) {
            endY = 255;
        }
        for (int i = startY; i <= endY; i += 16) {
            if (ChunkSection.isEmpty(this.getSections()[i >> 4])) continue;
            return false;
        }
        return true;
    }

    @Nullable
    public BiomeContainer getBiomes();

    public void setModified(boolean var1);

    public boolean isModified();

    public ChunkStatus getStatus();

    public void removeTileEntity(BlockPos var1);

    default public void markBlockForPostprocessing(BlockPos pos) {
        LogManager.getLogger().warn("Trying to mark a block for PostProcessing @ {}, but this operation is not supported.", (Object)pos);
    }

    public ShortList[] getPackedPositions();

    default public void addPackedPosition(short packedPosition, int index) {
        IChunk.getList(this.getPackedPositions(), index).add(packedPosition);
    }

    default public void addTileEntity(CompoundNBT nbt) {
        LogManager.getLogger().warn("Trying to set a BlockEntity, but this operation is not supported.");
    }

    @Nullable
    public CompoundNBT getDeferredTileEntity(BlockPos var1);

    @Nullable
    public CompoundNBT getTileEntityNBT(BlockPos var1);

    public Stream<BlockPos> getLightSources();

    public ITickList<Block> getBlocksToBeTicked();

    public ITickList<Fluid> getFluidsToBeTicked();

    public UpgradeData getUpgradeData();

    public void setInhabitedTime(long var1);

    public long getInhabitedTime();

    public static ShortList getList(ShortList[] packedPositions, int index) {
        if (packedPositions[index] == null) {
            packedPositions[index] = new ShortArrayList();
        }
        return packedPositions[index];
    }

    public boolean hasLight();

    public void setLight(boolean var1);
}
