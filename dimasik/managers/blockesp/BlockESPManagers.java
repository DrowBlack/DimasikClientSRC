package dimasik.managers.blockesp;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockESPManagers {
    private static final BlockESPManagers INSTANCE = new BlockESPManagers();
    public final Map<Block, Integer> blockMap = new ConcurrentHashMap<Block, Integer>();
    private final Set<BlockPos> cachedPositions = new HashSet<BlockPos>();
    private final Object lock = new Object();
    private static final int UPDATE_INTERVAL = 10;
    private volatile boolean updating = false;
    private BlockPos lastPlayerPos = BlockPos.ZERO;
    private int tickCounter = 0;

    public static BlockESPManagers getInstance() {
        return INSTANCE;
    }

    public boolean addBlock(Block block, int color) {
        return this.blockMap.putIfAbsent(block, color) == null;
    }

    public boolean removeBlock(Block block) {
        return this.blockMap.remove(block) != null;
    }

    public void clear() {
        this.blockMap.clear();
    }

    public Map<Block, Integer> getBlocks() {
        return this.blockMap;
    }

    public int getColorFor(Block block) {
        return this.blockMap.getOrDefault(block, -1);
    }

    public void updateCacheAsync(World world, BlockPos playerPos) {
        if (!(this.updating || this.tickCounter++ < 10 && playerPos.equals(this.lastPlayerPos))) {
            this.updating = true;
            this.lastPlayerPos = playerPos;
            this.tickCounter = 0;
            new Thread(() -> {
                HashSet newCache = new HashSet();
                int radius = 60;
                BlockPos min = playerPos.add(-radius, -100, -radius);
                BlockPos max = playerPos.add(radius, 100, radius);
                BlockPos.getAllInBox(min, max).forEach(pos -> {
                    Block block;
                    if (world.isBlockLoaded((BlockPos)pos) && this.blockMap.containsKey(block = world.getBlockState((BlockPos)pos).getBlock())) {
                        newCache.add(pos.toImmutable());
                    }
                });
                Object object = this.lock;
                synchronized (object) {
                    this.cachedPositions.clear();
                    this.cachedPositions.addAll(newCache);
                }
                this.updating = false;
            }, "BlockESP Cache Updater").start();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Set<BlockPos> getCachedPositions() {
        Object object = this.lock;
        synchronized (object) {
            return new HashSet<BlockPos>(this.cachedPositions);
        }
    }

    @Generated
    public Map<Block, Integer> getBlockMap() {
        return this.blockMap;
    }

    @Generated
    public Object getLock() {
        return this.lock;
    }

    @Generated
    public boolean isUpdating() {
        return this.updating;
    }

    @Generated
    public BlockPos getLastPlayerPos() {
        return this.lastPlayerPos;
    }

    @Generated
    public int getTickCounter() {
        return this.tickCounter;
    }
}
