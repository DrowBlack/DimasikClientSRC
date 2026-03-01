package net.minecraft.world.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.BitSet;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;

public class ChunkPrimerWrapper
extends ChunkPrimer {
    private final Chunk chunk;

    public ChunkPrimerWrapper(Chunk chunk) {
        super(chunk.getPos(), UpgradeData.EMPTY);
        this.chunk = chunk;
    }

    @Override
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return this.chunk.getTileEntity(pos);
    }

    @Override
    @Nullable
    public BlockState getBlockState(BlockPos pos) {
        return this.chunk.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.chunk.getFluidState(pos);
    }

    @Override
    public int getMaxLightLevel() {
        return this.chunk.getMaxLightLevel();
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
        return null;
    }

    @Override
    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
    }

    @Override
    public void addEntity(Entity entityIn) {
    }

    @Override
    public void setStatus(ChunkStatus status) {
    }

    @Override
    public ChunkSection[] getSections() {
        return this.chunk.getSections();
    }

    @Override
    @Nullable
    public WorldLightManager getWorldLightManager() {
        return this.chunk.getWorldLightManager();
    }

    @Override
    public void setHeightmap(Heightmap.Type type, long[] data) {
    }

    private Heightmap.Type func_209532_c(Heightmap.Type type) {
        if (type == Heightmap.Type.WORLD_SURFACE_WG) {
            return Heightmap.Type.WORLD_SURFACE;
        }
        return type == Heightmap.Type.OCEAN_FLOOR_WG ? Heightmap.Type.OCEAN_FLOOR : type;
    }

    @Override
    public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
        return this.chunk.getTopBlockY(this.func_209532_c(heightmapType), x, z);
    }

    @Override
    public ChunkPos getPos() {
        return this.chunk.getPos();
    }

    @Override
    public void setLastSaveTime(long saveTime) {
    }

    @Override
    @Nullable
    public StructureStart<?> func_230342_a_(Structure<?> p_230342_1_) {
        return this.chunk.func_230342_a_(p_230342_1_);
    }

    @Override
    public void func_230344_a_(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_) {
    }

    @Override
    public Map<Structure<?>, StructureStart<?>> getStructureStarts() {
        return this.chunk.getStructureStarts();
    }

    @Override
    public void setStructureStarts(Map<Structure<?>, StructureStart<?>> structureStartsIn) {
    }

    @Override
    public LongSet func_230346_b_(Structure<?> p_230346_1_) {
        return this.chunk.func_230346_b_(p_230346_1_);
    }

    @Override
    public void func_230343_a_(Structure<?> p_230343_1_, long p_230343_2_) {
    }

    @Override
    public Map<Structure<?>, LongSet> getStructureReferences() {
        return this.chunk.getStructureReferences();
    }

    @Override
    public void setStructureReferences(Map<Structure<?>, LongSet> structureReferences) {
    }

    @Override
    public BiomeContainer getBiomes() {
        return this.chunk.getBiomes();
    }

    @Override
    public void setModified(boolean modified) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.chunk.getStatus();
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
    }

    @Override
    public void markBlockForPostprocessing(BlockPos pos) {
    }

    @Override
    public void addTileEntity(CompoundNBT nbt) {
    }

    @Override
    @Nullable
    public CompoundNBT getDeferredTileEntity(BlockPos pos) {
        return this.chunk.getDeferredTileEntity(pos);
    }

    @Override
    @Nullable
    public CompoundNBT getTileEntityNBT(BlockPos pos) {
        return this.chunk.getTileEntityNBT(pos);
    }

    @Override
    public void setBiomes(BiomeContainer biomes) {
    }

    @Override
    public Stream<BlockPos> getLightSources() {
        return this.chunk.getLightSources();
    }

    @Override
    public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
        return new ChunkPrimerTickList<Block>(block -> block.getDefaultState().isAir(), this.getPos());
    }

    @Override
    public ChunkPrimerTickList<Fluid> getFluidsToBeTicked() {
        return new ChunkPrimerTickList<Fluid>(fluid -> fluid == Fluids.EMPTY, this.getPos());
    }

    @Override
    public BitSet getCarvingMask(GenerationStage.Carving type) {
        throw Util.pauseDevMode(new UnsupportedOperationException("Meaningless in this context"));
    }

    @Override
    public BitSet getOrAddCarvingMask(GenerationStage.Carving type) {
        throw Util.pauseDevMode(new UnsupportedOperationException("Meaningless in this context"));
    }

    public Chunk getChunk() {
        return this.chunk;
    }

    @Override
    public boolean hasLight() {
        return this.chunk.hasLight();
    }

    @Override
    public void setLight(boolean lightCorrectIn) {
        this.chunk.setLight(lightCorrectIn);
    }
}
