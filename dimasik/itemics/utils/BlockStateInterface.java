package dimasik.itemics.utils;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.cache.CachedRegion;
import dimasik.itemics.cache.WorldData;
import dimasik.itemics.utils.BlockStateInterfaceAccessWrapper;
import dimasik.itemics.utils.accessor.IClientChunkProvider;
import dimasik.itemics.utils.pathing.BetterWorldBorder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

public class BlockStateInterface {
    private final ClientChunkProvider provider;
    private final WorldData worldData;
    protected final IBlockReader world;
    public final BlockPos.Mutable isPassableBlockPos;
    public final IBlockReader access;
    public final BetterWorldBorder worldBorder;
    private Chunk prev = null;
    private CachedRegion prevCached = null;
    private final boolean useTheRealWorld;
    private static final BlockState AIR = Blocks.AIR.getDefaultState();

    public BlockStateInterface(IPlayerContext ctx) {
        this(ctx, false);
    }

    public BlockStateInterface(IPlayerContext ctx, boolean copyLoadedChunks) {
        this(ctx.world(), (WorldData)ctx.worldData(), copyLoadedChunks);
    }

    public BlockStateInterface(World world, WorldData worldData, boolean copyLoadedChunks) {
        this.world = world;
        this.worldBorder = new BetterWorldBorder(world.getWorldBorder());
        this.worldData = worldData;
        this.provider = copyLoadedChunks ? ((IClientChunkProvider)((Object)world.getChunkProvider())).createThreadSafeCopy() : (ClientChunkProvider)world.getChunkProvider();
        boolean bl = this.useTheRealWorld = (Boolean)Itemics.settings().pathThroughCachedOnly.value == false;
        if (!Minecraft.getInstance().isOnExecutionThread()) {
            throw new IllegalStateException();
        }
        this.isPassableBlockPos = new BlockPos.Mutable();
        this.access = new BlockStateInterfaceAccessWrapper(this);
    }

    public boolean worldContainsLoadedChunk(int blockX, int blockZ) {
        return this.provider.chunkExists(blockX >> 4, blockZ >> 4);
    }

    public static Block getBlock(IPlayerContext ctx, BlockPos pos) {
        return BlockStateInterface.get(ctx, pos).getBlock();
    }

    public static BlockState get(IPlayerContext ctx, BlockPos pos) {
        return new BlockStateInterface(ctx).get0(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState get0(BlockPos pos) {
        return this.get0(pos.getX(), pos.getY(), pos.getZ());
    }

    public BlockState get0(int x, int y, int z) {
        BlockState type;
        Object cached;
        if (y < 0 || y >= 256) {
            return AIR;
        }
        if (this.useTheRealWorld) {
            cached = this.prev;
            if (cached != null && ((Chunk)cached).getPos().x == x >> 4 && ((Chunk)cached).getPos().z == z >> 4) {
                return BlockStateInterface.getFromChunk((Chunk)cached, x, y, z);
            }
            Chunk chunk = this.provider.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false);
            if (chunk != null && !chunk.isEmpty()) {
                this.prev = chunk;
                return BlockStateInterface.getFromChunk(chunk, x, y, z);
            }
        }
        if ((cached = this.prevCached) == null || ((CachedRegion)cached).getX() != x >> 9 || ((CachedRegion)cached).getZ() != z >> 9) {
            if (this.worldData == null) {
                return AIR;
            }
            CachedRegion region = this.worldData.cache.getRegion(x >> 9, z >> 9);
            if (region == null) {
                return AIR;
            }
            this.prevCached = region;
            cached = region;
        }
        if ((type = ((CachedRegion)cached).getBlock(x & 0x1FF, y, z & 0x1FF)) == null) {
            return AIR;
        }
        return type;
    }

    public boolean isLoaded(int x, int z) {
        Chunk prevChunk = this.prev;
        if (prevChunk != null && prevChunk.getPos().x == x >> 4 && prevChunk.getPos().z == z >> 4) {
            return true;
        }
        prevChunk = this.provider.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, false);
        if (prevChunk != null && !prevChunk.isEmpty()) {
            this.prev = prevChunk;
            return true;
        }
        CachedRegion prevRegion = this.prevCached;
        if (prevRegion != null && prevRegion.getX() == x >> 9 && prevRegion.getZ() == z >> 9) {
            return prevRegion.isCached(x & 0x1FF, z & 0x1FF);
        }
        if (this.worldData == null) {
            return false;
        }
        prevRegion = this.worldData.cache.getRegion(x >> 9, z >> 9);
        if (prevRegion == null) {
            return false;
        }
        this.prevCached = prevRegion;
        return prevRegion.isCached(x & 0x1FF, z & 0x1FF);
    }

    public static BlockState getFromChunk(Chunk chunk, int x, int y, int z) {
        ChunkSection section = chunk.getSections()[y >> 4];
        if (ChunkSection.isEmpty(section)) {
            return AIR;
        }
        return section.getBlockState(x & 0xF, y & 0xF, z & 0xF);
    }
}
