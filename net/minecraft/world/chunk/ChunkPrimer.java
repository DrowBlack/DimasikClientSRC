package net.minecraft.world.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPrimer
implements IChunk {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ChunkPos pos;
    private volatile boolean modified;
    @Nullable
    private BiomeContainer biomes;
    @Nullable
    private volatile WorldLightManager lightManager;
    private final Map<Heightmap.Type, Heightmap> heightmaps = Maps.newEnumMap(Heightmap.Type.class);
    private volatile ChunkStatus status = ChunkStatus.EMPTY;
    private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
    private final Map<BlockPos, CompoundNBT> deferredTileEntities = Maps.newHashMap();
    private final ChunkSection[] sections = new ChunkSection[16];
    private final List<CompoundNBT> entities = Lists.newArrayList();
    private final List<BlockPos> lightPositions = Lists.newArrayList();
    private final ShortList[] packedPositions = new ShortList[16];
    private final Map<Structure<?>, StructureStart<?>> structureStartMap = Maps.newHashMap();
    private final Map<Structure<?>, LongSet> structureReferenceMap = Maps.newHashMap();
    private final UpgradeData upgradeData;
    private final ChunkPrimerTickList<Block> pendingBlockTicks;
    private final ChunkPrimerTickList<Fluid> pendingFluidTicks;
    private long inhabitedTime;
    private final Map<GenerationStage.Carving, BitSet> carvingMasks = new Object2ObjectArrayMap<GenerationStage.Carving, BitSet>();
    private volatile boolean hasLight;

    public ChunkPrimer(ChunkPos pos, UpgradeData data) {
        this(pos, data, null, new ChunkPrimerTickList<Block>(block -> block == null || block.getDefaultState().isAir(), pos), new ChunkPrimerTickList<Fluid>(fluid -> fluid == null || fluid == Fluids.EMPTY, pos));
    }

    public ChunkPrimer(ChunkPos pos, UpgradeData upgradeData, @Nullable ChunkSection[] sections, ChunkPrimerTickList<Block> pendingBlockTicks, ChunkPrimerTickList<Fluid> pendingFluidTicks) {
        this.pos = pos;
        this.upgradeData = upgradeData;
        this.pendingBlockTicks = pendingBlockTicks;
        this.pendingFluidTicks = pendingFluidTicks;
        if (sections != null) {
            if (this.sections.length == sections.length) {
                System.arraycopy(sections, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)sections.length, (Object)this.sections.length);
            }
        }
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getY();
        if (World.isYOutOfBounds(i)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        ChunkSection chunksection = this.getSections()[i >> 4];
        return ChunkSection.isEmpty(chunksection) ? Blocks.AIR.getDefaultState() : chunksection.getBlockState(pos.getX() & 0xF, i & 0xF, pos.getZ() & 0xF);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        int i = pos.getY();
        if (World.isYOutOfBounds(i)) {
            return Fluids.EMPTY.getDefaultState();
        }
        ChunkSection chunksection = this.getSections()[i >> 4];
        return ChunkSection.isEmpty(chunksection) ? Fluids.EMPTY.getDefaultState() : chunksection.getFluidState(pos.getX() & 0xF, i & 0xF, pos.getZ() & 0xF);
    }

    @Override
    public Stream<BlockPos> getLightSources() {
        return this.lightPositions.stream();
    }

    public ShortList[] getPackedLightPositions() {
        ShortList[] ashortlist = new ShortList[16];
        for (BlockPos blockpos : this.lightPositions) {
            IChunk.getList(ashortlist, blockpos.getY() >> 4).add(ChunkPrimer.packToLocal(blockpos));
        }
        return ashortlist;
    }

    public void addLightValue(short packedPosition, int lightValue) {
        this.addLightPosition(ChunkPrimer.unpackToWorld(packedPosition, lightValue, this.pos));
    }

    public void addLightPosition(BlockPos lightPos) {
        this.lightPositions.add(lightPos.toImmutable());
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (j >= 0 && j < 256) {
            if (this.sections[j >> 4] == Chunk.EMPTY_SECTION && state.isIn(Blocks.AIR)) {
                return state;
            }
            if (state.getLightValue() > 0) {
                this.lightPositions.add(new BlockPos((i & 0xF) + this.getPos().getXStart(), j, (k & 0xF) + this.getPos().getZStart()));
            }
            ChunkSection chunksection = this.getSection(j >> 4);
            BlockState blockstate = chunksection.setBlockState(i & 0xF, j & 0xF, k & 0xF, state);
            if (this.status.isAtLeast(ChunkStatus.FEATURES) && state != blockstate && (state.getOpacity(this, pos) != blockstate.getOpacity(this, pos) || state.getLightValue() != blockstate.getLightValue() || state.isTransparent() || blockstate.isTransparent())) {
                WorldLightManager worldlightmanager = this.getWorldLightManager();
                worldlightmanager.checkBlock(pos);
            }
            EnumSet<Heightmap.Type> enumset1 = this.getStatus().getHeightMaps();
            EnumSet<Heightmap.Type> enumset = null;
            for (Heightmap.Type heightmap$type : enumset1) {
                Heightmap heightmap = this.heightmaps.get(heightmap$type);
                if (heightmap != null) continue;
                if (enumset == null) {
                    enumset = EnumSet.noneOf(Heightmap.Type.class);
                }
                enumset.add(heightmap$type);
            }
            if (enumset != null) {
                Heightmap.updateChunkHeightmaps(this, enumset);
            }
            for (Heightmap.Type heightmap$type1 : enumset1) {
                this.heightmaps.get(heightmap$type1).update(i & 0xF, j, k & 0xF, state);
            }
            return blockstate;
        }
        return Blocks.VOID_AIR.getDefaultState();
    }

    public ChunkSection getSection(int sectionId) {
        if (this.sections[sectionId] == Chunk.EMPTY_SECTION) {
            this.sections[sectionId] = new ChunkSection(sectionId << 4);
        }
        return this.sections[sectionId];
    }

    @Override
    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        tileEntityIn.setPos(pos);
        this.tileEntities.put(pos, tileEntityIn);
    }

    @Override
    public Set<BlockPos> getTileEntitiesPos() {
        HashSet<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
        set.addAll(this.tileEntities.keySet());
        return set;
    }

    @Override
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return this.tileEntities.get(pos);
    }

    public Map<BlockPos, TileEntity> getTileEntities() {
        return this.tileEntities;
    }

    public void addEntity(CompoundNBT entityCompound) {
        this.entities.add(entityCompound);
    }

    @Override
    public void addEntity(Entity entityIn) {
        if (!entityIn.isPassenger()) {
            CompoundNBT compoundnbt = new CompoundNBT();
            entityIn.writeUnlessPassenger(compoundnbt);
            this.addEntity(compoundnbt);
        }
    }

    public List<CompoundNBT> getEntities() {
        return this.entities;
    }

    public void setBiomes(BiomeContainer biomes) {
        this.biomes = biomes;
    }

    @Override
    @Nullable
    public BiomeContainer getBiomes() {
        return this.biomes;
    }

    @Override
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    @Override
    public boolean isModified() {
        return this.modified;
    }

    @Override
    public ChunkStatus getStatus() {
        return this.status;
    }

    public void setStatus(ChunkStatus status) {
        this.status = status;
        this.setModified(true);
    }

    @Override
    public ChunkSection[] getSections() {
        return this.sections;
    }

    @Nullable
    public WorldLightManager getWorldLightManager() {
        return this.lightManager;
    }

    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightmaps.entrySet());
    }

    @Override
    public void setHeightmap(Heightmap.Type type, long[] data) {
        this.getHeightmap(type).setDataArray(data);
    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type typeIn) {
        return this.heightmaps.computeIfAbsent(typeIn, type -> new Heightmap(this, (Heightmap.Type)type));
    }

    @Override
    public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
        Heightmap heightmap = this.heightmaps.get(heightmapType);
        if (heightmap == null) {
            Heightmap.updateChunkHeightmaps(this, EnumSet.of(heightmapType));
            heightmap = this.heightmaps.get(heightmapType);
        }
        return heightmap.getHeight(x & 0xF, z & 0xF) - 1;
    }

    @Override
    public ChunkPos getPos() {
        return this.pos;
    }

    @Override
    public void setLastSaveTime(long saveTime) {
    }

    @Override
    @Nullable
    public StructureStart<?> func_230342_a_(Structure<?> p_230342_1_) {
        return this.structureStartMap.get(p_230342_1_);
    }

    @Override
    public void func_230344_a_(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_) {
        this.structureStartMap.put(p_230344_1_, p_230344_2_);
        this.modified = true;
    }

    @Override
    public Map<Structure<?>, StructureStart<?>> getStructureStarts() {
        return Collections.unmodifiableMap(this.structureStartMap);
    }

    @Override
    public void setStructureStarts(Map<Structure<?>, StructureStart<?>> structureStartsIn) {
        this.structureStartMap.clear();
        this.structureStartMap.putAll(structureStartsIn);
        this.modified = true;
    }

    @Override
    public LongSet func_230346_b_(Structure<?> p_230346_1_) {
        return this.structureReferenceMap.computeIfAbsent(p_230346_1_, structureIn -> new LongOpenHashSet());
    }

    @Override
    public void func_230343_a_(Structure<?> p_230343_1_, long p_230343_2_) {
        this.structureReferenceMap.computeIfAbsent(p_230343_1_, structureIn -> new LongOpenHashSet()).add(p_230343_2_);
        this.modified = true;
    }

    @Override
    public Map<Structure<?>, LongSet> getStructureReferences() {
        return Collections.unmodifiableMap(this.structureReferenceMap);
    }

    @Override
    public void setStructureReferences(Map<Structure<?>, LongSet> structureReferences) {
        this.structureReferenceMap.clear();
        this.structureReferenceMap.putAll(structureReferences);
        this.modified = true;
    }

    public static short packToLocal(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        int l = i & 0xF;
        int i1 = j & 0xF;
        int j1 = k & 0xF;
        return (short)(l | i1 << 4 | j1 << 8);
    }

    public static BlockPos unpackToWorld(short packedPos, int yOffset, ChunkPos chunkPosIn) {
        int i = (packedPos & 0xF) + (chunkPosIn.x << 4);
        int j = (packedPos >>> 4 & 0xF) + (yOffset << 4);
        int k = (packedPos >>> 8 & 0xF) + (chunkPosIn.z << 4);
        return new BlockPos(i, j, k);
    }

    @Override
    public void markBlockForPostprocessing(BlockPos pos) {
        if (!World.isOutsideBuildHeight(pos)) {
            IChunk.getList(this.packedPositions, pos.getY() >> 4).add(ChunkPrimer.packToLocal(pos));
        }
    }

    @Override
    public ShortList[] getPackedPositions() {
        return this.packedPositions;
    }

    @Override
    public void addPackedPosition(short packedPosition, int index) {
        IChunk.getList(this.packedPositions, index).add(packedPosition);
    }

    public ChunkPrimerTickList<Block> getBlocksToBeTicked() {
        return this.pendingBlockTicks;
    }

    public ChunkPrimerTickList<Fluid> getFluidsToBeTicked() {
        return this.pendingFluidTicks;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    @Override
    public void setInhabitedTime(long newInhabitedTime) {
        this.inhabitedTime = newInhabitedTime;
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void addTileEntity(CompoundNBT nbt) {
        this.deferredTileEntities.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
    }

    public Map<BlockPos, CompoundNBT> getDeferredTileEntities() {
        return Collections.unmodifiableMap(this.deferredTileEntities);
    }

    @Override
    public CompoundNBT getDeferredTileEntity(BlockPos pos) {
        return this.deferredTileEntities.get(pos);
    }

    @Override
    @Nullable
    public CompoundNBT getTileEntityNBT(BlockPos pos) {
        TileEntity tileentity = this.getTileEntity(pos);
        return tileentity != null ? tileentity.write(new CompoundNBT()) : this.deferredTileEntities.get(pos);
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        this.tileEntities.remove(pos);
        this.deferredTileEntities.remove(pos);
    }

    @Nullable
    public BitSet getCarvingMask(GenerationStage.Carving type) {
        return this.carvingMasks.get(type);
    }

    public BitSet getOrAddCarvingMask(GenerationStage.Carving type) {
        return this.carvingMasks.computeIfAbsent(type, typeIn -> new BitSet(65536));
    }

    public void setCarvingMask(GenerationStage.Carving type, BitSet mask) {
        this.carvingMasks.put(type, mask);
    }

    public void setLightManager(WorldLightManager lightManager) {
        this.lightManager = lightManager;
    }

    @Override
    public boolean hasLight() {
        return this.hasLight;
    }

    @Override
    public void setLight(boolean lightCorrectIn) {
        this.hasLight = lightCorrectIn;
        this.setModified(true);
    }
}
