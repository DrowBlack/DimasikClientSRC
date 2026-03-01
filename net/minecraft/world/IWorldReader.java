package net.minecraft.world;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ICollisionReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;

public interface IWorldReader
extends IBlockDisplayReader,
ICollisionReader,
BiomeManager.IBiomeReader {
    @Nullable
    public IChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    @Deprecated
    public boolean chunkExists(int var1, int var2);

    public int getHeight(Heightmap.Type var1, int var2, int var3);

    public int getSkylightSubtracted();

    public BiomeManager getBiomeManager();

    default public Biome getBiome(BlockPos pos) {
        return this.getBiomeManager().getBiome(pos);
    }

    default public Stream<BlockState> getStatesInArea(AxisAlignedBB aabb) {
        int j1;
        int i = MathHelper.floor(aabb.minX);
        int j = MathHelper.floor(aabb.maxX);
        int k = MathHelper.floor(aabb.minY);
        int l = MathHelper.floor(aabb.maxY);
        int i1 = MathHelper.floor(aabb.minZ);
        return this.isAreaLoaded(i, k, i1, j, l, j1 = MathHelper.floor(aabb.maxZ)) ? this.func_234853_a_(aabb) : Stream.empty();
    }

    @Override
    default public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        return colorResolverIn.getColor(this.getBiome(blockPosIn), blockPosIn.getX(), blockPosIn.getZ());
    }

    @Override
    default public Biome getNoiseBiome(int x, int y, int z) {
        IChunk ichunk = this.getChunk(x >> 2, z >> 2, ChunkStatus.BIOMES, false);
        return ichunk != null && ichunk.getBiomes() != null ? ichunk.getBiomes().getNoiseBiome(x, y, z) : this.getNoiseBiomeRaw(x, y, z);
    }

    public Biome getNoiseBiomeRaw(int var1, int var2, int var3);

    public boolean isRemote();

    @Deprecated
    public int getSeaLevel();

    public DimensionType getDimensionType();

    default public BlockPos getHeight(Heightmap.Type heightmapType, BlockPos pos) {
        return new BlockPos(pos.getX(), this.getHeight(heightmapType, pos.getX(), pos.getZ()), pos.getZ());
    }

    default public boolean isAirBlock(BlockPos pos) {
        return this.getBlockState(pos).isAir();
    }

    default public boolean canBlockSeeSky(BlockPos pos) {
        if (pos.getY() >= this.getSeaLevel()) {
            return this.canSeeSky(pos);
        }
        BlockPos blockpos = new BlockPos(pos.getX(), this.getSeaLevel(), pos.getZ());
        if (!this.canSeeSky(blockpos)) {
            return false;
        }
        BlockPos blockpos1 = blockpos.down();
        while (blockpos1.getY() > pos.getY()) {
            BlockState blockstate = this.getBlockState(blockpos1);
            if (blockstate.getOpacity(this, blockpos1) > 0 && !blockstate.getMaterial().isLiquid()) {
                return false;
            }
            blockpos1 = blockpos1.down();
        }
        return true;
    }

    @Deprecated
    default public float getBrightness(BlockPos pos) {
        return this.getDimensionType().getAmbientLight(this.getLight(pos));
    }

    default public int getStrongPower(BlockPos pos, Direction direction) {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }

    default public IChunk getChunk(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    default public IChunk getChunk(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
    }

    default public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus) {
        return this.getChunk(chunkX, chunkZ, requiredStatus, true);
    }

    @Override
    @Nullable
    default public IBlockReader getBlockReader(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
    }

    default public boolean hasWater(BlockPos pos) {
        return this.getFluidState(pos).isTagged(FluidTags.WATER);
    }

    default public boolean containsAnyLiquid(AxisAlignedBB bb) {
        int i = MathHelper.floor(bb.minX);
        int j = MathHelper.ceil(bb.maxX);
        int k = MathHelper.floor(bb.minY);
        int l = MathHelper.ceil(bb.maxY);
        int i1 = MathHelper.floor(bb.minZ);
        int j1 = MathHelper.ceil(bb.maxZ);
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    BlockState blockstate = this.getBlockState(blockpos$mutable.setPos(k1, l1, i2));
                    if (blockstate.getFluidState().isEmpty()) continue;
                    return true;
                }
            }
        }
        return false;
    }

    default public int getLight(BlockPos pos) {
        return this.getNeighborAwareLightSubtracted(pos, this.getSkylightSubtracted());
    }

    default public int getNeighborAwareLightSubtracted(BlockPos pos, int amount) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000 ? this.getLightSubtracted(pos, amount) : 15;
    }

    @Deprecated
    default public boolean isBlockLoaded(BlockPos pos) {
        return this.chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Deprecated
    default public boolean isAreaLoaded(BlockPos from, BlockPos to) {
        return this.isAreaLoaded(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }

    @Deprecated
    default public boolean isAreaLoaded(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        if (toY >= 0 && fromY < 256) {
            fromZ >>= 4;
            toX >>= 4;
            toZ >>= 4;
            for (int i = fromX >>= 4; i <= toX; ++i) {
                for (int j = fromZ; j <= toZ; ++j) {
                    if (this.chunkExists(i, j)) continue;
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
