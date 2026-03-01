package net.minecraft.world.chunk;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.lighting.WorldLightManager;

public abstract class AbstractChunkProvider
implements IChunkLightProvider,
AutoCloseable {
    @Nullable
    public Chunk getChunk(int chunkX, int chunkZ, boolean load) {
        return (Chunk)this.getChunk(chunkX, chunkZ, ChunkStatus.FULL, load);
    }

    @Nullable
    public Chunk getChunkNow(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, false);
    }

    @Override
    @Nullable
    public IBlockReader getChunkForLight(int chunkX, int chunkZ) {
        return this.getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, false);
    }

    public boolean chunkExists(int x, int z) {
        return this.getChunk(x, z, ChunkStatus.FULL, false) != null;
    }

    @Nullable
    public abstract IChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

    public abstract String makeString();

    @Override
    public void close() throws IOException {
    }

    public abstract WorldLightManager getLightManager();

    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
    }

    public void forceChunk(ChunkPos pos, boolean add) {
    }

    public boolean isChunkLoaded(Entity entityIn) {
        return true;
    }

    public boolean isChunkLoaded(ChunkPos pos) {
        return true;
    }

    public boolean canTick(BlockPos pos) {
        return true;
    }
}
