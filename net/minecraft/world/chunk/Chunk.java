package net.minecraft.world.chunk;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.palette.UpgradeData;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.EmptyTickList;
import net.minecraft.world.ITickList;
import net.minecraft.world.SerializableTickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkPrimerTickList;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.DebugChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerTickList;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk
implements IChunk {
    private static final Logger LOGGER = LogManager.getLogger();
    @Nullable
    public static final ChunkSection EMPTY_SECTION = null;
    private final ChunkSection[] sections = new ChunkSection[16];
    private BiomeContainer blockBiomeArray;
    private final Map<BlockPos, CompoundNBT> deferredTileEntities = Maps.newHashMap();
    private boolean loaded;
    private final World world;
    private final Map<Heightmap.Type, Heightmap> heightMap = Maps.newEnumMap(Heightmap.Type.class);
    private final UpgradeData upgradeData;
    private final Map<BlockPos, TileEntity> tileEntities = Maps.newHashMap();
    private final ClassInheritanceMultiMap<Entity>[] entityLists;
    private final Map<Structure<?>, StructureStart<?>> structureStarts = Maps.newHashMap();
    private final Map<Structure<?>, LongSet> structureReferences = Maps.newHashMap();
    private final ShortList[] packedBlockPositions = new ShortList[16];
    private ITickList<Block> blocksToBeTicked;
    private ITickList<Fluid> fluidsToBeTicked;
    private boolean hasEntities;
    private long lastSaveTime;
    private volatile boolean dirty;
    private long inhabitedTime;
    @Nullable
    private Supplier<ChunkHolder.LocationType> locationType;
    @Nullable
    private Consumer<Chunk> postLoadConsumer;
    private final ChunkPos pos;
    private volatile boolean lightCorrect;

    public Chunk(World worldIn, ChunkPos chunkPosIn, BiomeContainer biomeContainerIn) {
        this(worldIn, chunkPosIn, biomeContainerIn, UpgradeData.EMPTY, EmptyTickList.get(), EmptyTickList.get(), 0L, null, null);
    }

    public Chunk(World worldIn, ChunkPos chunkPosIn, BiomeContainer biomeContainerIn, UpgradeData upgradeDataIn, ITickList<Block> tickBlocksIn, ITickList<Fluid> tickFluidsIn, long inhabitedTimeIn, @Nullable ChunkSection[] sectionsIn, @Nullable Consumer<Chunk> postLoadConsumerIn) {
        this.entityLists = new ClassInheritanceMultiMap[16];
        this.world = worldIn;
        this.pos = chunkPosIn;
        this.upgradeData = upgradeDataIn;
        for (Heightmap.Type heightmap$type : Heightmap.Type.values()) {
            if (!ChunkStatus.FULL.getHeightMaps().contains(heightmap$type)) continue;
            this.heightMap.put(heightmap$type, new Heightmap(this, heightmap$type));
        }
        for (int i = 0; i < this.entityLists.length; ++i) {
            this.entityLists[i] = new ClassInheritanceMultiMap<Entity>(Entity.class);
        }
        this.blockBiomeArray = biomeContainerIn;
        this.blocksToBeTicked = tickBlocksIn;
        this.fluidsToBeTicked = tickFluidsIn;
        this.inhabitedTime = inhabitedTimeIn;
        this.postLoadConsumer = postLoadConsumerIn;
        if (sectionsIn != null) {
            if (this.sections.length == sectionsIn.length) {
                System.arraycopy(sectionsIn, 0, this.sections, 0, this.sections.length);
            } else {
                LOGGER.warn("Could not set level chunk sections, array length is {} instead of {}", (Object)sectionsIn.length, (Object)this.sections.length);
            }
        }
    }

    public Chunk(World worldIn, ChunkPrimer primer) {
        this(worldIn, primer.getPos(), primer.getBiomes(), primer.getUpgradeData(), primer.getBlocksToBeTicked(), primer.getFluidsToBeTicked(), primer.getInhabitedTime(), primer.getSections(), null);
        for (CompoundNBT compoundnbt : primer.getEntities()) {
            EntityType.loadEntityAndExecute(compoundnbt, worldIn, entity -> {
                this.addEntity((Entity)entity);
                return entity;
            });
        }
        for (TileEntity tileentity : primer.getTileEntities().values()) {
            this.addTileEntity(tileentity);
        }
        this.deferredTileEntities.putAll(primer.getDeferredTileEntities());
        for (int i = 0; i < primer.getPackedPositions().length; ++i) {
            this.packedBlockPositions[i] = primer.getPackedPositions()[i];
        }
        this.setStructureStarts(primer.getStructureStarts());
        this.setStructureReferences(primer.getStructureReferences());
        for (Map.Entry<Heightmap.Type, Heightmap> entry : primer.getHeightmaps()) {
            if (!ChunkStatus.FULL.getHeightMaps().contains(entry.getKey())) continue;
            this.getHeightmap(entry.getKey()).setDataArray(entry.getValue().getDataArray());
        }
        this.setLight(primer.hasLight());
        this.dirty = true;
    }

    @Override
    public Heightmap getHeightmap(Heightmap.Type typeIn) {
        return this.heightMap.computeIfAbsent(typeIn, type -> new Heightmap(this, (Heightmap.Type)type));
    }

    @Override
    public Set<BlockPos> getTileEntitiesPos() {
        HashSet<BlockPos> set = Sets.newHashSet(this.deferredTileEntities.keySet());
        set.addAll(this.tileEntities.keySet());
        return set;
    }

    @Override
    public ChunkSection[] getSections() {
        return this.sections;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        if (this.world.isDebug()) {
            BlockState blockstate = null;
            if (j == 60) {
                blockstate = Blocks.BARRIER.getDefaultState();
            }
            if (j == 70) {
                blockstate = DebugChunkGenerator.getBlockStateFor(i, k);
            }
            return blockstate == null ? Blocks.AIR.getDefaultState() : blockstate;
        }
        try {
            ChunkSection chunksection;
            if (j >= 0 && j >> 4 < this.sections.length && !ChunkSection.isEmpty(chunksection = this.sections[j >> 4])) {
                return chunksection.getBlockState(i & 0xF, j & 0xF, k & 0xF);
            }
            return Blocks.AIR.getDefaultState();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting block state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.addDetail("Location", () -> CrashReportCategory.getCoordinateInfo(i, j, k));
            throw new ReportedException(crashreport);
        }
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getFluidState(pos.getX(), pos.getY(), pos.getZ());
    }

    public FluidState getFluidState(int bx, int by, int bz) {
        try {
            ChunkSection chunksection;
            if (by >= 0 && by >> 4 < this.sections.length && !ChunkSection.isEmpty(chunksection = this.sections[by >> 4])) {
                return chunksection.getFluidState(bx & 0xF, by & 0xF, bz & 0xF);
            }
            return Fluids.EMPTY.getDefaultState();
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Getting fluid state");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being got");
            crashreportcategory.addDetail("Location", () -> CrashReportCategory.getCoordinateInfo(bx, by, bz));
            throw new ReportedException(crashreport);
        }
    }

    @Override
    @Nullable
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean isMoving) {
        TileEntity tileentity;
        int i = pos.getX() & 0xF;
        int j = pos.getY();
        int k = pos.getZ() & 0xF;
        ChunkSection chunksection = this.sections[j >> 4];
        if (chunksection == EMPTY_SECTION) {
            if (state.isAir()) {
                return null;
            }
            this.sections[j >> 4] = chunksection = new ChunkSection(j >> 4 << 4);
        }
        boolean flag = chunksection.isEmpty();
        BlockState blockstate = chunksection.setBlockState(i, j & 0xF, k, state);
        if (blockstate == state) {
            return null;
        }
        Block block = state.getBlock();
        Block block1 = blockstate.getBlock();
        this.heightMap.get(Heightmap.Type.MOTION_BLOCKING).update(i, j, k, state);
        this.heightMap.get(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES).update(i, j, k, state);
        this.heightMap.get(Heightmap.Type.OCEAN_FLOOR).update(i, j, k, state);
        this.heightMap.get(Heightmap.Type.WORLD_SURFACE).update(i, j, k, state);
        boolean flag1 = chunksection.isEmpty();
        if (flag != flag1) {
            this.world.getChunkProvider().getLightManager().func_215567_a(pos, flag1);
        }
        if (!this.world.isRemote) {
            blockstate.onReplaced(this.world, pos, state, isMoving);
        } else if (block1 != block && block1 instanceof ITileEntityProvider) {
            this.world.removeTileEntity(pos);
        }
        if (!chunksection.getBlockState(i, j & 0xF, k).isIn(block)) {
            return null;
        }
        if (block1 instanceof ITileEntityProvider && (tileentity = this.getTileEntity(pos, CreateEntityType.CHECK)) != null) {
            tileentity.updateContainingBlockInfo();
        }
        if (!this.world.isRemote) {
            state.onBlockAdded(this.world, pos, blockstate, isMoving);
        }
        if (block instanceof ITileEntityProvider) {
            TileEntity tileentity1 = this.getTileEntity(pos, CreateEntityType.CHECK);
            if (tileentity1 == null) {
                tileentity1 = ((ITileEntityProvider)((Object)block)).createNewTileEntity(this.world);
                this.world.setTileEntity(pos, tileentity1);
            } else {
                tileentity1.updateContainingBlockInfo();
            }
        }
        this.dirty = true;
        return blockstate;
    }

    @Nullable
    public WorldLightManager getWorldLightManager() {
        return this.world.getChunkProvider().getLightManager();
    }

    @Override
    public void addEntity(Entity entityIn) {
        int k;
        this.hasEntities = true;
        int i = MathHelper.floor(entityIn.getPosX() / 16.0);
        int j = MathHelper.floor(entityIn.getPosZ() / 16.0);
        if (i != this.pos.x || j != this.pos.z) {
            LOGGER.warn("Wrong location! ({}, {}) should be ({}, {}), {}", (Object)i, (Object)j, (Object)this.pos.x, (Object)this.pos.z, (Object)entityIn);
            entityIn.removed = true;
        }
        if ((k = MathHelper.floor(entityIn.getPosY() / 16.0)) < 0) {
            k = 0;
        }
        if (k >= this.entityLists.length) {
            k = this.entityLists.length - 1;
        }
        entityIn.addedToChunk = true;
        entityIn.chunkCoordX = this.pos.x;
        entityIn.chunkCoordY = k;
        entityIn.chunkCoordZ = this.pos.z;
        this.entityLists[k].add(entityIn);
    }

    @Override
    public void setHeightmap(Heightmap.Type type, long[] data) {
        this.heightMap.get(type).setDataArray(data);
    }

    public void removeEntity(Entity entityIn) {
        this.removeEntityAtIndex(entityIn, entityIn.chunkCoordY);
    }

    public void removeEntityAtIndex(Entity entityIn, int index) {
        if (index < 0) {
            index = 0;
        }
        if (index >= this.entityLists.length) {
            index = this.entityLists.length - 1;
        }
        this.entityLists[index].remove(entityIn);
    }

    @Override
    public int getTopBlockY(Heightmap.Type heightmapType, int x, int z) {
        return this.heightMap.get(heightmapType).getHeight(x & 0xF, z & 0xF) - 1;
    }

    @Nullable
    private TileEntity createNewTileEntity(BlockPos pos) {
        BlockState blockstate = this.getBlockState(pos);
        Block block = blockstate.getBlock();
        return !block.isTileEntityProvider() ? null : ((ITileEntityProvider)((Object)block)).createNewTileEntity(this.world);
    }

    @Override
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return this.getTileEntity(pos, CreateEntityType.CHECK);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, CreateEntityType creationMode) {
        TileEntity tileentity1;
        CompoundNBT compoundnbt;
        TileEntity tileentity = this.tileEntities.get(pos);
        if (tileentity == null && (compoundnbt = this.deferredTileEntities.remove(pos)) != null && (tileentity1 = this.setDeferredTileEntity(pos, compoundnbt)) != null) {
            return tileentity1;
        }
        if (tileentity == null) {
            if (creationMode == CreateEntityType.IMMEDIATE) {
                tileentity = this.createNewTileEntity(pos);
                this.world.setTileEntity(pos, tileentity);
            }
        } else if (tileentity.isRemoved()) {
            this.tileEntities.remove(pos);
            return null;
        }
        return tileentity;
    }

    public void addTileEntity(TileEntity tileEntityIn) {
        this.addTileEntity(tileEntityIn.getPos(), tileEntityIn);
        if (this.loaded || this.world.isRemote()) {
            this.world.setTileEntity(tileEntityIn.getPos(), tileEntityIn);
        }
    }

    @Override
    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
        if (this.getBlockState(pos).getBlock() instanceof ITileEntityProvider) {
            tileEntityIn.setWorldAndPos(this.world, pos);
            tileEntityIn.validate();
            TileEntity tileentity = this.tileEntities.put(pos.toImmutable(), tileEntityIn);
            if (tileentity != null && tileentity != tileEntityIn) {
                tileentity.remove();
            }
        }
    }

    @Override
    public void addTileEntity(CompoundNBT nbt) {
        this.deferredTileEntities.put(new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z")), nbt);
    }

    @Override
    @Nullable
    public CompoundNBT getTileEntityNBT(BlockPos pos) {
        TileEntity tileentity = this.getTileEntity(pos);
        if (tileentity != null && !tileentity.isRemoved()) {
            CompoundNBT compoundnbt1 = tileentity.write(new CompoundNBT());
            compoundnbt1.putBoolean("keepPacked", false);
            return compoundnbt1;
        }
        CompoundNBT compoundnbt = this.deferredTileEntities.get(pos);
        if (compoundnbt != null) {
            compoundnbt = compoundnbt.copy();
            compoundnbt.putBoolean("keepPacked", true);
        }
        return compoundnbt;
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity;
        if ((this.loaded || this.world.isRemote()) && (tileentity = this.tileEntities.remove(pos)) != null) {
            tileentity.remove();
        }
    }

    public void postLoad() {
        if (this.postLoadConsumer != null) {
            this.postLoadConsumer.accept(this);
            this.postLoadConsumer = null;
        }
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, @Nullable Predicate<? super Entity> filter) {
        int i = MathHelper.floor((aabb.minY - 2.0) / 16.0);
        int j = MathHelper.floor((aabb.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);
        for (int k = i; k <= j; ++k) {
            ClassInheritanceMultiMap<Entity> classinheritancemultimap = this.entityLists[k];
            List<Entity> list = classinheritancemultimap.func_241289_a_();
            int l = list.size();
            for (int i1 = 0; i1 < l; ++i1) {
                Entity entity = list.get(i1);
                if (!entity.getBoundingBox().intersects(aabb) || entity == entityIn) continue;
                if (filter == null || filter.test(entity)) {
                    listToFill.add(entity);
                }
                if (!(entity instanceof EnderDragonEntity)) continue;
                for (EnderDragonPartEntity enderdragonpartentity : ((EnderDragonEntity)entity).getDragonParts()) {
                    if (enderdragonpartentity == entityIn || !enderdragonpartentity.getBoundingBox().intersects(aabb) || filter != null && !filter.test(enderdragonpartentity)) continue;
                    listToFill.add(enderdragonpartentity);
                }
            }
        }
    }

    public <T extends Entity> void getEntitiesWithinAABBForList(@Nullable EntityType<?> entitytypeIn, AxisAlignedBB aabb, List<? super T> list, Predicate<? super T> filter) {
        int i = MathHelper.floor((aabb.minY - 2.0) / 16.0);
        int j = MathHelper.floor((aabb.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);
        for (int k = i; k <= j; ++k) {
            for (Entity entity : this.entityLists[k].getByClass(Entity.class)) {
                if (entitytypeIn != null && entity.getType() != entitytypeIn || !entity.getBoundingBox().intersects(aabb) || !filter.test(entity)) continue;
                list.add(entity);
            }
        }
    }

    public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, @Nullable Predicate<? super T> filter) {
        int i = MathHelper.floor((aabb.minY - 2.0) / 16.0);
        int j = MathHelper.floor((aabb.maxY + 2.0) / 16.0);
        i = MathHelper.clamp(i, 0, this.entityLists.length - 1);
        j = MathHelper.clamp(j, 0, this.entityLists.length - 1);
        for (int k = i; k <= j; ++k) {
            for (Entity t : this.entityLists[k].getByClass(entityClass)) {
                if (!t.getBoundingBox().intersects(aabb) || filter != null && !filter.test(t)) continue;
                listToFill.add(t);
            }
        }
    }

    public boolean isEmpty() {
        return false;
    }

    @Override
    public ChunkPos getPos() {
        return this.pos;
    }

    public void read(@Nullable BiomeContainer biomeContainerIn, PacketBuffer packetBufferIn, CompoundNBT nbtIn, int availableSections) {
        boolean flag = biomeContainerIn != null;
        Predicate<BlockPos> predicate = flag ? pos -> true : pos -> (availableSections & 1 << (pos.getY() >> 4)) != 0;
        Sets.newHashSet(this.tileEntities.keySet()).stream().filter(predicate).forEach(this.world::removeTileEntity);
        for (int i = 0; i < this.sections.length; ++i) {
            ChunkSection chunksection = this.sections[i];
            if ((availableSections & 1 << i) == 0) {
                if (!flag || chunksection == EMPTY_SECTION) continue;
                this.sections[i] = EMPTY_SECTION;
                continue;
            }
            if (chunksection == EMPTY_SECTION) {
                this.sections[i] = chunksection = new ChunkSection(i << 4);
            }
            chunksection.read(packetBufferIn);
        }
        if (biomeContainerIn != null) {
            this.blockBiomeArray = biomeContainerIn;
        }
        for (Heightmap.Type heightmap$type : Heightmap.Type.values()) {
            String s = heightmap$type.getId();
            if (!nbtIn.contains(s, 12)) continue;
            this.setHeightmap(heightmap$type, nbtIn.getLongArray(s));
        }
        for (TileEntity tileentity : this.tileEntities.values()) {
            tileentity.updateContainingBlockInfo();
        }
    }

    @Override
    public BiomeContainer getBiomes() {
        return this.blockBiomeArray;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    public Collection<Map.Entry<Heightmap.Type, Heightmap>> getHeightmaps() {
        return Collections.unmodifiableSet(this.heightMap.entrySet());
    }

    public Map<BlockPos, TileEntity> getTileEntityMap() {
        return this.tileEntities;
    }

    public ClassInheritanceMultiMap<Entity>[] getEntityLists() {
        return this.entityLists;
    }

    @Override
    public CompoundNBT getDeferredTileEntity(BlockPos pos) {
        return this.deferredTileEntities.get(pos);
    }

    @Override
    public Stream<BlockPos> getLightSources() {
        return StreamSupport.stream(BlockPos.getAllInBoxMutable(this.pos.getXStart(), 0, this.pos.getZStart(), this.pos.getXEnd(), 255, this.pos.getZEnd()).spliterator(), false).filter(pos -> this.getBlockState((BlockPos)pos).getLightValue() != 0);
    }

    @Override
    public ITickList<Block> getBlocksToBeTicked() {
        return this.blocksToBeTicked;
    }

    @Override
    public ITickList<Fluid> getFluidsToBeTicked() {
        return this.fluidsToBeTicked;
    }

    @Override
    public void setModified(boolean modified) {
        this.dirty = modified;
    }

    @Override
    public boolean isModified() {
        return this.dirty || this.hasEntities && this.world.getGameTime() != this.lastSaveTime;
    }

    public void setHasEntities(boolean hasEntitiesIn) {
        this.hasEntities = hasEntitiesIn;
    }

    @Override
    public void setLastSaveTime(long saveTime) {
        this.lastSaveTime = saveTime;
    }

    @Override
    @Nullable
    public StructureStart<?> func_230342_a_(Structure<?> p_230342_1_) {
        return this.structureStarts.get(p_230342_1_);
    }

    @Override
    public void func_230344_a_(Structure<?> p_230344_1_, StructureStart<?> p_230344_2_) {
        this.structureStarts.put(p_230344_1_, p_230344_2_);
    }

    @Override
    public Map<Structure<?>, StructureStart<?>> getStructureStarts() {
        return this.structureStarts;
    }

    @Override
    public void setStructureStarts(Map<Structure<?>, StructureStart<?>> structureStartsIn) {
        this.structureStarts.clear();
        this.structureStarts.putAll(structureStartsIn);
    }

    @Override
    public LongSet func_230346_b_(Structure<?> p_230346_1_) {
        return this.structureReferences.computeIfAbsent(p_230346_1_, structureIn -> new LongOpenHashSet());
    }

    @Override
    public void func_230343_a_(Structure<?> p_230343_1_, long p_230343_2_) {
        this.structureReferences.computeIfAbsent(p_230343_1_, structureIn -> new LongOpenHashSet()).add(p_230343_2_);
    }

    @Override
    public Map<Structure<?>, LongSet> getStructureReferences() {
        return this.structureReferences;
    }

    @Override
    public void setStructureReferences(Map<Structure<?>, LongSet> structureReferences) {
        this.structureReferences.clear();
        this.structureReferences.putAll(structureReferences);
    }

    @Override
    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    @Override
    public void setInhabitedTime(long newInhabitedTime) {
        this.inhabitedTime = newInhabitedTime;
    }

    public void postProcess() {
        ChunkPos chunkpos = this.getPos();
        for (int i = 0; i < this.packedBlockPositions.length; ++i) {
            if (this.packedBlockPositions[i] == null) continue;
            for (Short oshort : this.packedBlockPositions[i]) {
                BlockPos blockpos = ChunkPrimer.unpackToWorld(oshort, i, chunkpos);
                BlockState blockstate = this.getBlockState(blockpos);
                BlockState blockstate1 = Block.getValidBlockForPosition(blockstate, this.world, blockpos);
                this.world.setBlockState(blockpos, blockstate1, 20);
            }
            this.packedBlockPositions[i].clear();
        }
        this.rescheduleTicks();
        for (BlockPos blockpos1 : Sets.newHashSet(this.deferredTileEntities.keySet())) {
            this.getTileEntity(blockpos1);
        }
        this.deferredTileEntities.clear();
        this.upgradeData.postProcessChunk(this);
    }

    @Nullable
    private TileEntity setDeferredTileEntity(BlockPos pos, CompoundNBT compound) {
        TileEntity tileentity;
        BlockState blockstate = this.getBlockState(pos);
        if ("DUMMY".equals(compound.getString("id"))) {
            Block block = blockstate.getBlock();
            if (block instanceof ITileEntityProvider) {
                tileentity = ((ITileEntityProvider)((Object)block)).createNewTileEntity(this.world);
            } else {
                tileentity = null;
                LOGGER.warn("Tried to load a DUMMY block entity @ {} but found not block entity block {} at location", (Object)pos, (Object)blockstate);
            }
        } else {
            tileentity = TileEntity.readTileEntity(blockstate, compound);
        }
        if (tileentity != null) {
            tileentity.setWorldAndPos(this.world, pos);
            this.addTileEntity(tileentity);
        } else {
            LOGGER.warn("Tried to load a block entity for block {} but failed at location {}", (Object)blockstate, (Object)pos);
        }
        return tileentity;
    }

    @Override
    public UpgradeData getUpgradeData() {
        return this.upgradeData;
    }

    @Override
    public ShortList[] getPackedPositions() {
        return this.packedBlockPositions;
    }

    public void rescheduleTicks() {
        if (this.blocksToBeTicked instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList)this.blocksToBeTicked).postProcess(this.world.getPendingBlockTicks(), pos -> this.getBlockState((BlockPos)pos).getBlock());
            this.blocksToBeTicked = EmptyTickList.get();
        } else if (this.blocksToBeTicked instanceof SerializableTickList) {
            ((SerializableTickList)this.blocksToBeTicked).func_234855_a_(this.world.getPendingBlockTicks());
            this.blocksToBeTicked = EmptyTickList.get();
        }
        if (this.fluidsToBeTicked instanceof ChunkPrimerTickList) {
            ((ChunkPrimerTickList)this.fluidsToBeTicked).postProcess(this.world.getPendingFluidTicks(), pos -> this.getFluidState((BlockPos)pos).getFluid());
            this.fluidsToBeTicked = EmptyTickList.get();
        } else if (this.fluidsToBeTicked instanceof SerializableTickList) {
            ((SerializableTickList)this.fluidsToBeTicked).func_234855_a_(this.world.getPendingFluidTicks());
            this.fluidsToBeTicked = EmptyTickList.get();
        }
    }

    public void saveScheduledTicks(ServerWorld serverWorldIn) {
        if (this.blocksToBeTicked == EmptyTickList.get()) {
            this.blocksToBeTicked = new SerializableTickList<Block>(Registry.BLOCK::getKey, ((ServerTickList)serverWorldIn.getPendingBlockTicks()).getPending(this.pos, true, false), serverWorldIn.getGameTime());
            this.setModified(true);
        }
        if (this.fluidsToBeTicked == EmptyTickList.get()) {
            this.fluidsToBeTicked = new SerializableTickList<Fluid>(Registry.FLUID::getKey, ((ServerTickList)serverWorldIn.getPendingFluidTicks()).getPending(this.pos, true, false), serverWorldIn.getGameTime());
            this.setModified(true);
        }
    }

    @Override
    public ChunkStatus getStatus() {
        return ChunkStatus.FULL;
    }

    public ChunkHolder.LocationType getLocationType() {
        return this.locationType == null ? ChunkHolder.LocationType.BORDER : this.locationType.get();
    }

    public void setLocationType(Supplier<ChunkHolder.LocationType> locationTypeIn) {
        this.locationType = locationTypeIn;
    }

    @Override
    public boolean hasLight() {
        return this.lightCorrect;
    }

    @Override
    public void setLight(boolean lightCorrectIn) {
        this.lightCorrect = lightCorrectIn;
        this.setModified(true);
    }

    public static enum CreateEntityType {
        IMMEDIATE,
        QUEUED,
        CHECK;

    }
}
