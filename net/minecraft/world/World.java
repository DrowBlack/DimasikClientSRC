package net.minecraft.world;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.IParticleData;
import net.minecraft.profiler.IProfiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class World
implements IWorld,
AutoCloseable {
    protected static final Logger LOGGER = LogManager.getLogger();
    public static final Codec<RegistryKey<World>> CODEC = ResourceLocation.CODEC.xmap(RegistryKey.getKeyCreator(Registry.WORLD_KEY), RegistryKey::getLocation);
    public static final RegistryKey<World> OVERWORLD = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("overworld"));
    public static final RegistryKey<World> THE_NETHER = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("the_nether"));
    public static final RegistryKey<World> THE_END = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("the_end"));
    private static final Direction[] FACING_VALUES = Direction.values();
    public final List<TileEntity> loadedTileEntityList = Lists.newArrayList();
    public final List<TileEntity> tickableTileEntities = Lists.newArrayList();
    protected final List<TileEntity> addedTileEntityList = Lists.newArrayList();
    protected final List<TileEntity> tileEntitiesToBeRemoved = Lists.newArrayList();
    private final Thread mainThread;
    private final boolean isDebug;
    private int skylightSubtracted;
    protected int updateLCG = new Random().nextInt();
    protected final int DIST_HASH_MAGIC = 1013904223;
    protected float prevRainingStrength;
    protected float rainingStrength;
    protected float prevThunderingStrength;
    protected float thunderingStrength;
    public final Random rand = new Random();
    private final DimensionType dimensionType;
    protected final ISpawnWorldInfo worldInfo;
    private final Supplier<IProfiler> profiler;
    public final boolean isRemote;
    protected boolean processingLoadedTiles;
    private final WorldBorder worldBorder;
    private final BiomeManager biomeManager;
    private final RegistryKey<World> dimension;

    protected World(ISpawnWorldInfo worldInfo, RegistryKey<World> dimension, final DimensionType dimensionType, Supplier<IProfiler> profiler, boolean isRemote, boolean isDebug, long seed) {
        this.profiler = profiler;
        this.worldInfo = worldInfo;
        this.dimensionType = dimensionType;
        this.dimension = dimension;
        this.isRemote = isRemote;
        this.worldBorder = dimensionType.getCoordinateScale() != 1.0 ? new WorldBorder(){

            @Override
            public double getCenterX() {
                return super.getCenterX() / dimensionType.getCoordinateScale();
            }

            @Override
            public double getCenterZ() {
                return super.getCenterZ() / dimensionType.getCoordinateScale();
            }
        } : new WorldBorder();
        this.mainThread = Thread.currentThread();
        this.biomeManager = new BiomeManager(this, seed, dimensionType.getMagnifier());
        this.isDebug = isDebug;
    }

    @Override
    public boolean isRemote() {
        return this.isRemote;
    }

    @Nullable
    public MinecraftServer getServer() {
        return null;
    }

    public static boolean isValid(BlockPos pos) {
        return !World.isOutsideBuildHeight(pos) && World.isValidXZPosition(pos);
    }

    public static boolean isInvalidPosition(BlockPos pos) {
        return !World.isInvalidYPosition(pos.getY()) && World.isValidXZPosition(pos);
    }

    private static boolean isValidXZPosition(BlockPos pos) {
        return pos.getX() >= -30000000 && pos.getZ() >= -30000000 && pos.getX() < 30000000 && pos.getZ() < 30000000;
    }

    private static boolean isInvalidYPosition(int y) {
        return y < -20000000 || y >= 20000000;
    }

    public static boolean isOutsideBuildHeight(BlockPos pos) {
        return World.isYOutOfBounds(pos.getY());
    }

    public static boolean isYOutOfBounds(int y) {
        return y < 0 || y >= 256;
    }

    public Chunk getChunkAt(BlockPos pos) {
        return this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public Chunk getChunk(int chunkX, int chunkZ) {
        return (Chunk)this.getChunk(chunkX, chunkZ, ChunkStatus.FULL);
    }

    @Override
    public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
        IChunk ichunk = this.getChunkProvider().getChunk(x, z, requiredStatus, nonnull);
        if (ichunk == null && nonnull) {
            throw new IllegalStateException("Should always be able to create a chunk!");
        }
        return ichunk;
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
        return this.setBlockState(pos, newState, flags, 512);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        if (World.isOutsideBuildHeight(pos)) {
            return false;
        }
        if (!this.isRemote && this.isDebug()) {
            return false;
        }
        Chunk chunk = this.getChunkAt(pos);
        Block block = state.getBlock();
        BlockState blockstate = chunk.setBlockState(pos, state, (flags & 0x40) != 0);
        if (blockstate == null) {
            return false;
        }
        BlockState blockstate1 = this.getBlockState(pos);
        if ((flags & 0x80) == 0 && blockstate1 != blockstate && (blockstate1.getOpacity(this, pos) != blockstate.getOpacity(this, pos) || blockstate1.getLightValue() != blockstate.getLightValue() || blockstate1.isTransparent() || blockstate.isTransparent())) {
            this.getProfiler().startSection("queueCheckLight");
            this.getChunkProvider().getLightManager().checkBlock(pos);
            this.getProfiler().endSection();
        }
        if (blockstate1 == state) {
            if (blockstate != blockstate1) {
                this.markBlockRangeForRenderUpdate(pos, blockstate, blockstate1);
            }
            if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && (this.isRemote || chunk.getLocationType() != null && chunk.getLocationType().isAtLeast(ChunkHolder.LocationType.TICKING))) {
                this.notifyBlockUpdate(pos, blockstate, state, flags);
            }
            if ((flags & 1) != 0) {
                this.func_230547_a_(pos, blockstate.getBlock());
                if (!this.isRemote && state.hasComparatorInputOverride()) {
                    this.updateComparatorOutputLevel(pos, block);
                }
            }
            if ((flags & 0x10) == 0 && recursionLeft > 0) {
                int i = flags & 0xFFFFFFDE;
                blockstate.updateDiagonalNeighbors(this, pos, i, recursionLeft - 1);
                state.updateNeighbours(this, pos, i, recursionLeft - 1);
                state.updateDiagonalNeighbors(this, pos, i, recursionLeft - 1);
            }
            this.onBlockStateChange(pos, blockstate, blockstate1);
        }
        return true;
    }

    public void onBlockStateChange(BlockPos pos, BlockState blockStateIn, BlockState newState) {
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean isMoving) {
        FluidState fluidstate = this.getFluidState(pos);
        return this.setBlockState(pos, fluidstate.getBlockState(), 3 | (isMoving ? 64 : 0));
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft) {
        BlockState blockstate = this.getBlockState(pos);
        if (blockstate.isAir()) {
            return false;
        }
        FluidState fluidstate = this.getFluidState(pos);
        if (!(blockstate.getBlock() instanceof AbstractFireBlock)) {
            this.playEvent(2001, pos, Block.getStateId(blockstate));
        }
        if (dropBlock) {
            TileEntity tileentity = blockstate.getBlock().isTileEntityProvider() ? this.getTileEntity(pos) : null;
            Block.spawnDrops(blockstate, this, pos, tileentity, entity, ItemStack.EMPTY);
        }
        return this.setBlockState(pos, fluidstate.getBlockState(), 3, recursionLeft);
    }

    public boolean setBlockState(BlockPos pos, BlockState state) {
        return this.setBlockState(pos, state, 3);
    }

    public abstract void notifyBlockUpdate(BlockPos var1, BlockState var2, BlockState var3, int var4);

    public void markBlockRangeForRenderUpdate(BlockPos blockPosIn, BlockState oldState, BlockState newState) {
    }

    public void notifyNeighborsOfStateChange(BlockPos pos, Block blockIn) {
        this.neighborChanged(pos.west(), blockIn, pos);
        this.neighborChanged(pos.east(), blockIn, pos);
        this.neighborChanged(pos.down(), blockIn, pos);
        this.neighborChanged(pos.up(), blockIn, pos);
        this.neighborChanged(pos.north(), blockIn, pos);
        this.neighborChanged(pos.south(), blockIn, pos);
    }

    public void notifyNeighborsOfStateExcept(BlockPos pos, Block blockType, Direction skipSide) {
        if (skipSide != Direction.WEST) {
            this.neighborChanged(pos.west(), blockType, pos);
        }
        if (skipSide != Direction.EAST) {
            this.neighborChanged(pos.east(), blockType, pos);
        }
        if (skipSide != Direction.DOWN) {
            this.neighborChanged(pos.down(), blockType, pos);
        }
        if (skipSide != Direction.UP) {
            this.neighborChanged(pos.up(), blockType, pos);
        }
        if (skipSide != Direction.NORTH) {
            this.neighborChanged(pos.north(), blockType, pos);
        }
        if (skipSide != Direction.SOUTH) {
            this.neighborChanged(pos.south(), blockType, pos);
        }
    }

    public void neighborChanged(BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!this.isRemote) {
            BlockState blockstate = this.getBlockState(pos);
            try {
                blockstate.neighborChanged(this, pos, blockIn, fromPos, false);
            }
            catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while updating neighbours");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");
                crashreportcategory.addDetail("Source block type", () -> {
                    try {
                        return String.format("ID #%s (%s // %s)", Registry.BLOCK.getKey(blockIn), blockIn.getTranslationKey(), blockIn.getClass().getCanonicalName());
                    }
                    catch (Throwable throwable1) {
                        return "ID #" + String.valueOf(Registry.BLOCK.getKey(blockIn));
                    }
                });
                CrashReportCategory.addBlockInfo(crashreportcategory, pos, blockstate);
                throw new ReportedException(crashreport);
            }
        }
    }

    @Override
    public int getHeight(Heightmap.Type heightmapType, int x, int z) {
        int i = x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 ? (this.chunkExists(x >> 4, z >> 4) ? this.getChunk(x >> 4, z >> 4).getTopBlockY(heightmapType, x & 0xF, z & 0xF) + 1 : 0) : this.getSeaLevel() + 1;
        return i;
    }

    @Override
    public WorldLightManager getLightManager() {
        return this.getChunkProvider().getLightManager();
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (World.isOutsideBuildHeight(pos)) {
            return Blocks.VOID_AIR.getDefaultState();
        }
        Chunk chunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        return chunk.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        if (World.isOutsideBuildHeight(pos)) {
            return Fluids.EMPTY.getDefaultState();
        }
        Chunk chunk = this.getChunkAt(pos);
        return chunk.getFluidState(pos);
    }

    public boolean isDaytime() {
        return !this.getDimensionType().doesFixedTimeExist() && this.skylightSubtracted < 4;
    }

    public boolean isNightTime() {
        return !this.getDimensionType().doesFixedTimeExist() && !this.isDaytime();
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        this.playSound(player, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, soundIn, category, volume, pitch);
    }

    public abstract void playSound(@Nullable PlayerEntity var1, double var2, double var4, double var6, SoundEvent var8, SoundCategory var9, float var10, float var11);

    public abstract void playMovingSound(@Nullable PlayerEntity var1, Entity var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
    }

    @Override
    public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    public void addParticle(IParticleData particleData, boolean forceAlwaysRender, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    public void addOptionalParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    public void addOptionalParticle(IParticleData particleData, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
    }

    public float getCelestialAngleRadians(float partialTicks) {
        float f = this.func_242415_f(partialTicks);
        return f * ((float)Math.PI * 2);
    }

    public boolean addTileEntity(TileEntity tile) {
        boolean flag;
        if (this.processingLoadedTiles) {
            org.apache.logging.log4j.util.Supplier[] supplierArray = new org.apache.logging.log4j.util.Supplier[2];
            supplierArray[0] = () -> Registry.BLOCK_ENTITY_TYPE.getKey(tile.getType());
            supplierArray[1] = tile::getPos;
            LOGGER.error("Adding block entity while ticking: {} @ {}", supplierArray);
        }
        if ((flag = this.loadedTileEntityList.add(tile)) && tile instanceof ITickableTileEntity) {
            this.tickableTileEntities.add(tile);
        }
        if (this.isRemote) {
            BlockPos blockpos = tile.getPos();
            BlockState blockstate = this.getBlockState(blockpos);
            this.notifyBlockUpdate(blockpos, blockstate, blockstate, 2);
        }
        return flag;
    }

    public void addTileEntities(Collection<TileEntity> tileEntityCollection) {
        if (this.processingLoadedTiles) {
            this.addedTileEntityList.addAll(tileEntityCollection);
        } else {
            for (TileEntity tileentity : tileEntityCollection) {
                this.addTileEntity(tileentity);
            }
        }
    }

    public void tickBlockEntities() {
        IProfiler iprofiler = this.getProfiler();
        iprofiler.startSection("blockEntities");
        if (!this.tileEntitiesToBeRemoved.isEmpty()) {
            this.tickableTileEntities.removeAll(this.tileEntitiesToBeRemoved);
            this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
            this.tileEntitiesToBeRemoved.clear();
        }
        this.processingLoadedTiles = true;
        Iterator<TileEntity> iterator = this.tickableTileEntities.iterator();
        while (iterator.hasNext()) {
            TileEntity tileentity = iterator.next();
            if (!tileentity.isRemoved() && tileentity.hasWorld()) {
                BlockPos blockpos = tileentity.getPos();
                if (this.getChunkProvider().canTick(blockpos) && this.getWorldBorder().contains(blockpos)) {
                    try {
                        iprofiler.startSection(() -> String.valueOf(TileEntityType.getId(tileentity.getType())));
                        if (tileentity.getType().isValidBlock(this.getBlockState(blockpos).getBlock())) {
                            ((ITickableTileEntity)((Object)tileentity)).tick();
                        } else {
                            tileentity.warnInvalidBlock();
                        }
                        iprofiler.endSection();
                    }
                    catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Block entity being ticked");
                        tileentity.addInfoToCrashReport(crashreportcategory);
                        throw new ReportedException(crashreport);
                    }
                }
            }
            if (!tileentity.isRemoved()) continue;
            iterator.remove();
            this.loadedTileEntityList.remove(tileentity);
            if (!this.isBlockLoaded(tileentity.getPos())) continue;
            this.getChunkAt(tileentity.getPos()).removeTileEntity(tileentity.getPos());
        }
        this.processingLoadedTiles = false;
        iprofiler.endStartSection("pendingBlockEntities");
        if (!this.addedTileEntityList.isEmpty()) {
            for (int i = 0; i < this.addedTileEntityList.size(); ++i) {
                TileEntity tileentity1 = this.addedTileEntityList.get(i);
                if (tileentity1.isRemoved()) continue;
                if (!this.loadedTileEntityList.contains(tileentity1)) {
                    this.addTileEntity(tileentity1);
                }
                if (!this.isBlockLoaded(tileentity1.getPos())) continue;
                Chunk chunk = this.getChunkAt(tileentity1.getPos());
                BlockState blockstate = chunk.getBlockState(tileentity1.getPos());
                chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                this.notifyBlockUpdate(tileentity1.getPos(), blockstate, blockstate, 3);
            }
            this.addedTileEntityList.clear();
        }
        iprofiler.endSection();
    }

    public void guardEntityTick(Consumer<Entity> consumerEntity, Entity entityIn) {
        try {
            consumerEntity.accept(entityIn);
        }
        catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking entity");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being ticked");
            entityIn.fillCrashReport(crashreportcategory);
            throw new ReportedException(crashreport);
        }
    }

    public Explosion createExplosion(@Nullable Entity entityIn, double xIn, double yIn, double zIn, float explosionRadius, Explosion.Mode modeIn) {
        return this.createExplosion(entityIn, null, null, xIn, yIn, zIn, explosionRadius, false, modeIn);
    }

    public Explosion createExplosion(@Nullable Entity entityIn, double xIn, double yIn, double zIn, float explosionRadius, boolean causesFire, Explosion.Mode modeIn) {
        return this.createExplosion(entityIn, null, null, xIn, yIn, zIn, explosionRadius, causesFire, modeIn);
    }

    public Explosion createExplosion(@Nullable Entity exploder, @Nullable DamageSource damageSource, @Nullable ExplosionContext context, double x, double y, double z, float size, boolean causesFire, Explosion.Mode mode) {
        Explosion explosion = new Explosion(this, exploder, damageSource, context, x, y, z, size, causesFire, mode);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    public String getProviderName() {
        return this.getChunkProvider().makeString();
    }

    @Override
    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        if (World.isOutsideBuildHeight(pos)) {
            return null;
        }
        if (!this.isRemote && Thread.currentThread() != this.mainThread) {
            return null;
        }
        TileEntity tileentity = null;
        if (this.processingLoadedTiles) {
            tileentity = this.getPendingTileEntityAt(pos);
        }
        if (tileentity == null) {
            tileentity = this.getChunkAt(pos).getTileEntity(pos, Chunk.CreateEntityType.IMMEDIATE);
        }
        if (tileentity == null) {
            tileentity = this.getPendingTileEntityAt(pos);
        }
        return tileentity;
    }

    @Nullable
    private TileEntity getPendingTileEntityAt(BlockPos pos) {
        for (int i = 0; i < this.addedTileEntityList.size(); ++i) {
            TileEntity tileentity = this.addedTileEntityList.get(i);
            if (tileentity.isRemoved() || !tileentity.getPos().equals(pos)) continue;
            return tileentity;
        }
        return null;
    }

    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        if (!World.isOutsideBuildHeight(pos) && tileEntityIn != null && !tileEntityIn.isRemoved()) {
            if (this.processingLoadedTiles) {
                tileEntityIn.setWorldAndPos(this, pos);
                Iterator<TileEntity> iterator = this.addedTileEntityList.iterator();
                while (iterator.hasNext()) {
                    TileEntity tileentity = iterator.next();
                    if (!tileentity.getPos().equals(pos)) continue;
                    tileentity.remove();
                    iterator.remove();
                }
                this.addedTileEntityList.add(tileEntityIn);
            } else {
                this.getChunkAt(pos).addTileEntity(pos, tileEntityIn);
                this.addTileEntity(tileEntityIn);
            }
        }
    }

    public void removeTileEntity(BlockPos pos) {
        TileEntity tileentity = this.getTileEntity(pos);
        if (tileentity != null && this.processingLoadedTiles) {
            tileentity.remove();
            this.addedTileEntityList.remove(tileentity);
        } else {
            if (tileentity != null) {
                this.addedTileEntityList.remove(tileentity);
                this.loadedTileEntityList.remove(tileentity);
                this.tickableTileEntities.remove(tileentity);
            }
            this.getChunkAt(pos).removeTileEntity(pos);
        }
    }

    public boolean isBlockPresent(BlockPos pos) {
        return World.isOutsideBuildHeight(pos) ? false : this.getChunkProvider().chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public boolean isDirectionSolid(BlockPos pos, Entity entity, Direction direction) {
        if (World.isOutsideBuildHeight(pos)) {
            return false;
        }
        IChunk ichunk = this.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.FULL, false);
        return ichunk == null ? false : ichunk.getBlockState(pos).isTopSolid(this, pos, entity, direction);
    }

    public boolean isTopSolid(BlockPos pos, Entity entityIn) {
        return this.isDirectionSolid(pos, entityIn, Direction.UP);
    }

    public void calculateInitialSkylight() {
        double d0 = 1.0 - (double)(this.getRainStrength(1.0f) * 5.0f) / 16.0;
        double d1 = 1.0 - (double)(this.getThunderStrength(1.0f) * 5.0f) / 16.0;
        double d2 = 0.5 + 2.0 * MathHelper.clamp((double)MathHelper.cos(this.func_242415_f(1.0f) * ((float)Math.PI * 2)), -0.25, 0.25);
        this.skylightSubtracted = (int)((1.0 - d2 * d0 * d1) * 11.0);
    }

    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
        this.getChunkProvider().setAllowedSpawnTypes(hostile, peaceful);
    }

    protected void calculateInitialWeather() {
        if (this.worldInfo.isRaining()) {
            this.rainingStrength = 1.0f;
            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = 1.0f;
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.getChunkProvider().close();
    }

    @Override
    @Nullable
    public IBlockReader getBlockReader(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false);
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
        this.getProfiler().func_230035_c_("getEntities");
        ArrayList<Entity> list = Lists.newArrayList();
        int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
        int j = MathHelper.floor((boundingBox.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
        int l = MathHelper.floor((boundingBox.maxZ + 2.0) / 16.0);
        AbstractChunkProvider abstractchunkprovider = this.getChunkProvider();
        for (int i1 = i; i1 <= j; ++i1) {
            for (int j1 = k; j1 <= l; ++j1) {
                Chunk chunk = abstractchunkprovider.getChunk(i1, j1, false);
                if (chunk == null) continue;
                chunk.getEntitiesWithinAABBForEntity(entityIn, boundingBox, list, predicate);
            }
        }
        return list;
    }

    public <T extends Entity> List<T> getEntitiesWithinAABB(@Nullable EntityType<T> type, AxisAlignedBB boundingBox, Predicate<? super T> predicate) {
        this.getProfiler().func_230035_c_("getEntities");
        int i = MathHelper.floor((boundingBox.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((boundingBox.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((boundingBox.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((boundingBox.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        for (int i1 = i; i1 < j; ++i1) {
            for (int j1 = k; j1 < l; ++j1) {
                Chunk chunk = this.getChunkProvider().getChunk(i1, j1, false);
                if (chunk == null) continue;
                chunk.getEntitiesWithinAABBForList(type, boundingBox, list, predicate);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
        this.getProfiler().func_230035_c_("getEntities");
        int i = MathHelper.floor((aabb.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((aabb.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((aabb.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((aabb.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        AbstractChunkProvider abstractchunkprovider = this.getChunkProvider();
        for (int i1 = i; i1 < j; ++i1) {
            for (int j1 = k; j1 < l; ++j1) {
                Chunk chunk = abstractchunkprovider.getChunk(i1, j1, false);
                if (chunk == null) continue;
                chunk.getEntitiesOfTypeWithinAABB(clazz, aabb, list, filter);
            }
        }
        return list;
    }

    @Override
    public <T extends Entity> List<T> getLoadedEntitiesWithinAABB(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, @Nullable Predicate<? super T> p_225316_3_) {
        this.getProfiler().func_230035_c_("getLoadedEntities");
        int i = MathHelper.floor((p_225316_2_.minX - 2.0) / 16.0);
        int j = MathHelper.ceil((p_225316_2_.maxX + 2.0) / 16.0);
        int k = MathHelper.floor((p_225316_2_.minZ - 2.0) / 16.0);
        int l = MathHelper.ceil((p_225316_2_.maxZ + 2.0) / 16.0);
        ArrayList list = Lists.newArrayList();
        AbstractChunkProvider abstractchunkprovider = this.getChunkProvider();
        for (int i1 = i; i1 < j; ++i1) {
            for (int j1 = k; j1 < l; ++j1) {
                Chunk chunk = abstractchunkprovider.getChunkNow(i1, j1);
                if (chunk == null) continue;
                chunk.getEntitiesOfTypeWithinAABB(p_225316_1_, p_225316_2_, list, p_225316_3_);
            }
        }
        return list;
    }

    @Nullable
    public abstract Entity getEntityByID(int var1);

    public void markChunkDirty(BlockPos pos, TileEntity unusedTileEntity) {
        if (this.isBlockLoaded(pos)) {
            this.getChunkAt(pos).markDirty();
        }
    }

    @Override
    public int getSeaLevel() {
        return 63;
    }

    public int getStrongPower(BlockPos pos) {
        int i = 0;
        if ((i = Math.max(i, this.getStrongPower(pos.down(), Direction.DOWN))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.up(), Direction.UP))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.north(), Direction.NORTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.south(), Direction.SOUTH))) >= 15) {
            return i;
        }
        if ((i = Math.max(i, this.getStrongPower(pos.west(), Direction.WEST))) >= 15) {
            return i;
        }
        return (i = Math.max(i, this.getStrongPower(pos.east(), Direction.EAST))) >= 15 ? i : i;
    }

    public boolean isSidePowered(BlockPos pos, Direction side) {
        return this.getRedstonePower(pos, side) > 0;
    }

    public int getRedstonePower(BlockPos pos, Direction facing) {
        BlockState blockstate = this.getBlockState(pos);
        int i = blockstate.getWeakPower(this, pos, facing);
        return blockstate.isNormalCube(this, pos) ? Math.max(i, this.getStrongPower(pos)) : i;
    }

    public boolean isBlockPowered(BlockPos pos) {
        if (this.getRedstonePower(pos.down(), Direction.DOWN) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.up(), Direction.UP) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.north(), Direction.NORTH) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.south(), Direction.SOUTH) > 0) {
            return true;
        }
        if (this.getRedstonePower(pos.west(), Direction.WEST) > 0) {
            return true;
        }
        return this.getRedstonePower(pos.east(), Direction.EAST) > 0;
    }

    public int getRedstonePowerFromNeighbors(BlockPos pos) {
        int i = 0;
        for (Direction direction : FACING_VALUES) {
            int j = this.getRedstonePower(pos.offset(direction), direction);
            if (j >= 15) {
                return 15;
            }
            if (j <= i) continue;
            i = j;
        }
        return i;
    }

    public void sendQuittingDisconnectingPacket() {
    }

    public long getGameTime() {
        return this.worldInfo.getGameTime();
    }

    public long getDayTime() {
        return this.worldInfo.getDayTime();
    }

    public boolean isBlockModifiable(PlayerEntity player, BlockPos pos) {
        return true;
    }

    public void setEntityState(Entity entityIn, byte state) {
    }

    public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
        this.getBlockState(pos).receiveBlockEvent(this, pos, eventID, eventParam);
    }

    @Override
    public IWorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    public GameRules getGameRules() {
        return this.worldInfo.getGameRulesInstance();
    }

    public float getThunderStrength(float delta) {
        return MathHelper.lerp(delta, this.prevThunderingStrength, this.thunderingStrength) * this.getRainStrength(delta);
    }

    public void setThunderStrength(float strength) {
        this.prevThunderingStrength = strength;
        this.thunderingStrength = strength;
    }

    public float getRainStrength(float delta) {
        return MathHelper.lerp(delta, this.prevRainingStrength, this.rainingStrength);
    }

    public void setRainStrength(float strength) {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    public boolean isThundering() {
        if (this.getDimensionType().hasSkyLight() && !this.getDimensionType().getHasCeiling()) {
            return (double)this.getThunderStrength(1.0f) > 0.9;
        }
        return false;
    }

    public boolean isRaining() {
        return (double)this.getRainStrength(1.0f) > 0.2;
    }

    public boolean isRainingAt(BlockPos position) {
        if (!this.isRaining()) {
            return false;
        }
        if (!this.canSeeSky(position)) {
            return false;
        }
        if (this.getHeight(Heightmap.Type.MOTION_BLOCKING, position).getY() > position.getY()) {
            return false;
        }
        Biome biome = this.getBiome(position);
        return biome.getPrecipitation() == Biome.RainType.RAIN && biome.getTemperature(position) >= 0.15f;
    }

    public boolean isBlockinHighHumidity(BlockPos pos) {
        Biome biome = this.getBiome(pos);
        return biome.isHighHumidity();
    }

    @Nullable
    public abstract MapData getMapData(String var1);

    public abstract void registerMapData(MapData var1);

    public abstract int getNextMapId();

    public void playBroadcastSound(int id, BlockPos pos, int data) {
    }

    public CrashReportCategory fillCrashReport(CrashReport report) {
        CrashReportCategory crashreportcategory = report.makeCategoryDepth("Affected level", 1);
        crashreportcategory.addDetail("All players", () -> this.getPlayers().size() + " total; " + String.valueOf(this.getPlayers()));
        crashreportcategory.addDetail("Chunk stats", this.getChunkProvider()::makeString);
        crashreportcategory.addDetail("Level dimension", () -> this.getDimensionKey().getLocation().toString());
        try {
            this.worldInfo.addToCrashReport(crashreportcategory);
        }
        catch (Throwable throwable) {
            crashreportcategory.addCrashSectionThrowable("Level Data Unobtainable", throwable);
        }
        return crashreportcategory;
    }

    public abstract void sendBlockBreakProgress(int var1, BlockPos var2, int var3);

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, @Nullable CompoundNBT compound) {
    }

    public abstract Scoreboard getScoreboard();

    public void updateComparatorOutputLevel(BlockPos pos, Block blockIn) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos blockpos = pos.offset(direction);
            if (!this.isBlockLoaded(blockpos)) continue;
            BlockState blockstate = this.getBlockState(blockpos);
            if (blockstate.isIn(Blocks.COMPARATOR)) {
                blockstate.neighborChanged(this, blockpos, blockIn, pos, false);
                continue;
            }
            if (!blockstate.isNormalCube(this, blockpos) || !(blockstate = this.getBlockState(blockpos = blockpos.offset(direction))).isIn(Blocks.COMPARATOR)) continue;
            blockstate.neighborChanged(this, blockpos, blockIn, pos, false);
        }
    }

    @Override
    public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
        long i = 0L;
        float f = 0.0f;
        if (this.isBlockLoaded(pos)) {
            f = this.getMoonFactor();
            i = this.getChunkAt(pos).getInhabitedTime();
        }
        return new DifficultyInstance(this.getDifficulty(), this.getDayTime(), i, f);
    }

    @Override
    public int getSkylightSubtracted() {
        return this.skylightSubtracted;
    }

    public void setTimeLightningFlash(int timeFlashIn) {
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.worldBorder;
    }

    public void sendPacketToServer(IPacket<?> packetIn) {
        throw new UnsupportedOperationException("Can't send packets to server unless you're on the client.");
    }

    @Override
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    public RegistryKey<World> getDimensionKey() {
        return this.dimension;
    }

    @Override
    public Random getRandom() {
        return this.rand;
    }

    @Override
    public boolean hasBlockState(BlockPos pos, Predicate<BlockState> state) {
        return state.test(this.getBlockState(pos));
    }

    public abstract RecipeManager getRecipeManager();

    public abstract ITagCollectionSupplier getTags();

    public BlockPos getBlockRandomPos(int x, int y, int z, int yMask) {
        this.updateLCG = this.updateLCG * 3 + 1013904223;
        int i = this.updateLCG >> 2;
        return new BlockPos(x + (i & 0xF), y + (i >> 16 & yMask), z + (i >> 8 & 0xF));
    }

    public boolean isSaveDisabled() {
        return false;
    }

    public IProfiler getProfiler() {
        return this.profiler.get();
    }

    public Supplier<IProfiler> getWorldProfiler() {
        return this.profiler;
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    public final boolean isDebug() {
        return this.isDebug;
    }
}
