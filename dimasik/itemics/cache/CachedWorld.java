package dimasik.itemics.cache;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.cache.ICachedWorld;
import dimasik.itemics.api.cache.IWorldData;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.cache.CachedChunk;
import dimasik.itemics.cache.CachedRegion;
import dimasik.itemics.cache.ChunkPacker;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public final class CachedWorld
implements ICachedWorld,
Helper {
    private static final int REGION_MAX = 58594;
    private Long2ObjectMap<CachedRegion> cachedRegions = new Long2ObjectOpenHashMap<CachedRegion>();
    private final String directory;
    private final LinkedBlockingQueue<ChunkPos> toPackQueue = new LinkedBlockingQueue();
    private final Map<ChunkPos, Chunk> toPackMap = new ConcurrentHashMap<ChunkPos, Chunk>();
    private final RegistryKey<World> dimension;

    CachedWorld(Path directory, RegistryKey<World> dimension) {
        if (!Files.exists(directory, new LinkOption[0])) {
            try {
                Files.createDirectories(directory, new FileAttribute[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        this.directory = directory.toString();
        this.dimension = dimension;
        System.out.println("Cached world directory: " + String.valueOf(directory));
        Itemics.getExecutor().execute(new PackerThread());
        Itemics.getExecutor().execute(() -> {
            try {
                Thread.sleep(30000L);
                while (true) {
                    this.save();
                    Thread.sleep(600000L);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        });
    }

    @Override
    public final void queueForPacking(Chunk chunk) {
        if (this.toPackMap.put(chunk.getPos(), chunk) == null) {
            this.toPackQueue.add(chunk.getPos());
        }
    }

    @Override
    public final boolean isCached(int blockX, int blockZ) {
        CachedRegion region = this.getRegion(blockX >> 9, blockZ >> 9);
        if (region == null) {
            return false;
        }
        return region.isCached(blockX & 0x1FF, blockZ & 0x1FF);
    }

    public final boolean regionLoaded(int blockX, int blockZ) {
        return this.getRegion(blockX >> 9, blockZ >> 9) != null;
    }

    @Override
    public final ArrayList<BlockPos> getLocationsOf(String block, int maximum, int centerX, int centerZ, int maxRegionDistanceSq) {
        ArrayList<BlockPos> res = new ArrayList<BlockPos>();
        int centerRegionX = centerX >> 9;
        int centerRegionZ = centerZ >> 9;
        for (int searchRadius = 0; searchRadius <= maxRegionDistanceSq; ++searchRadius) {
            for (int xoff = -searchRadius; xoff <= searchRadius; ++xoff) {
                for (int zoff = -searchRadius; zoff <= searchRadius; ++zoff) {
                    int regionZ;
                    int regionX;
                    CachedRegion region;
                    int distance = xoff * xoff + zoff * zoff;
                    if (distance != searchRadius || (region = this.getOrCreateRegion(regionX = xoff + centerRegionX, regionZ = zoff + centerRegionZ)) == null) continue;
                    res.addAll(region.getLocationsOf(block));
                }
            }
            if (res.size() < maximum) continue;
            return res;
        }
        return res;
    }

    private void updateCachedChunk(CachedChunk chunk) {
        CachedRegion region = this.getOrCreateRegion(chunk.x >> 5, chunk.z >> 5);
        region.updateCachedChunk(chunk.x & 0x1F, chunk.z & 0x1F, chunk);
    }

    @Override
    public final void save() {
        if (!((Boolean)Itemics.settings().chunkCaching.value).booleanValue()) {
            System.out.println("Not saving to disk; chunk caching is disabled.");
            this.allRegions().forEach(region -> {
                if (region != null) {
                    region.removeExpired();
                }
            });
            this.prune();
            return;
        }
        long start = System.nanoTime() / 1000000L;
        this.allRegions().parallelStream().forEach(region -> {
            if (region != null) {
                region.save(this.directory);
            }
        });
        long now = System.nanoTime() / 1000000L;
        System.out.println("World save took " + (now - start) + "ms");
        this.prune();
    }

    private synchronized void prune() {
        if (!((Boolean)Itemics.settings().pruneRegionsFromRAM.value).booleanValue()) {
            return;
        }
        BlockPos pruneCenter = this.guessPosition();
        for (CachedRegion region : this.allRegions()) {
            int distZ;
            int distX;
            double dist;
            if (region == null || !((dist = Math.sqrt((distX = (region.getX() << 9) + 256 - pruneCenter.getX()) * distX + (distZ = (region.getZ() << 9) + 256 - pruneCenter.getZ()) * distZ)) > 1024.0)) continue;
            if (!((Boolean)Itemics.settings().censorCoordinates.value).booleanValue()) {
                this.logDebug("Deleting cached region " + region.getX() + "," + region.getZ() + " from ram");
            }
            this.cachedRegions.remove(this.getRegionID(region.getX(), region.getZ()));
        }
    }

    private BlockPos guessPosition() {
        for (IItemics iitemics : ItemicsAPI.getProvider().getAllItemics()) {
            IWorldData data = iitemics.getWorldProvider().getCurrentWorld();
            if (data == null || data.getCachedWorld() != this || iitemics.getPlayerContext().player() == null) continue;
            return iitemics.getPlayerContext().playerFeet();
        }
        CachedChunk mostRecentlyModified = null;
        for (CachedRegion region : this.allRegions()) {
            CachedChunk ch;
            if (region == null || (ch = region.mostRecentlyModified()) == null || mostRecentlyModified != null && mostRecentlyModified.cacheTimestamp >= ch.cacheTimestamp) continue;
            mostRecentlyModified = ch;
        }
        if (mostRecentlyModified == null) {
            return new BlockPos(0, 0, 0);
        }
        return new BlockPos((mostRecentlyModified.x << 4) + 8, 0, (mostRecentlyModified.z << 4) + 8);
    }

    private synchronized List<CachedRegion> allRegions() {
        return new ArrayList<CachedRegion>(this.cachedRegions.values());
    }

    @Override
    public final void reloadAllFromDisk() {
        long start = System.nanoTime() / 1000000L;
        this.allRegions().forEach(region -> {
            if (region != null) {
                region.load(this.directory);
            }
        });
        long now = System.nanoTime() / 1000000L;
        System.out.println("World load took " + (now - start) + "ms");
    }

    @Override
    public final synchronized CachedRegion getRegion(int regionX, int regionZ) {
        return (CachedRegion)this.cachedRegions.get(this.getRegionID(regionX, regionZ));
    }

    private synchronized CachedRegion getOrCreateRegion(int regionX, int regionZ) {
        return this.cachedRegions.computeIfAbsent(this.getRegionID(regionX, regionZ), id -> {
            CachedRegion newRegion = new CachedRegion(regionX, regionZ, this.dimension);
            newRegion.load(this.directory);
            return newRegion;
        });
    }

    public void tryLoadFromDisk(int regionX, int regionZ) {
        this.getOrCreateRegion(regionX, regionZ);
    }

    private long getRegionID(int regionX, int regionZ) {
        if (!this.isRegionInWorld(regionX, regionZ)) {
            return 0L;
        }
        return (long)regionX & 0xFFFFFFFFL | ((long)regionZ & 0xFFFFFFFFL) << 32;
    }

    private boolean isRegionInWorld(int regionX, int regionZ) {
        return regionX <= 58594 && regionX >= -58594 && regionZ <= 58594 && regionZ >= -58594;
    }

    private class PackerThread
    implements Runnable {
        private PackerThread() {
        }

        @Override
        public void run() {
            while (true) {
                try {
                    while (true) {
                        ChunkPos pos = CachedWorld.this.toPackQueue.take();
                        Chunk chunk = CachedWorld.this.toPackMap.remove(pos);
                        CachedChunk cached = ChunkPacker.pack(chunk);
                        CachedWorld.this.updateCachedChunk(cached);
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                catch (Throwable th) {
                    th.printStackTrace();
                    continue;
                }
                break;
            }
        }
    }
}
