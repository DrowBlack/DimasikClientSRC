package net.minecraft.client.multiplayer;

import dimasik.itemics.utils.accessor.IChunkArray;
import dimasik.itemics.utils.accessor.IClientChunkProvider;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.optifine.ChunkDataOF;
import net.optifine.ChunkOF;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientChunkProvider
extends AbstractChunkProvider
implements IClientChunkProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Chunk empty;
    private final WorldLightManager lightManager;
    private volatile ChunkArray array;
    private final ClientWorld world;

    public ClientChunkProvider(ClientWorld clientWorldIn, int viewDistance) {
        this.world = clientWorldIn;
        this.empty = new EmptyChunk((World)clientWorldIn, new ChunkPos(0, 0));
        this.lightManager = new WorldLightManager(this, true, clientWorldIn.getDimensionType().hasSkyLight());
        this.array = new ChunkArray(ClientChunkProvider.adjustViewDistance(viewDistance));
    }

    @Override
    public WorldLightManager getLightManager() {
        return this.lightManager;
    }

    private static boolean isValid(@Nullable Chunk chunkIn, int x, int z) {
        if (chunkIn == null) {
            return false;
        }
        ChunkPos chunkpos = chunkIn.getPos();
        return chunkpos.x == x && chunkpos.z == z;
    }

    public void unloadChunk(int x, int z) {
        int i;
        Chunk chunk;
        if (this.array.inView(x, z) && ClientChunkProvider.isValid(chunk = this.array.get(i = this.array.getIndex(x, z)), x, z)) {
            if (Reflector.ChunkEvent_Unload_Constructor.exists()) {
                Reflector.postForgeBusEvent(Reflector.ChunkEvent_Unload_Constructor, chunk);
            }
            chunk.setLoaded(false);
            this.array.unload(i, chunk, null);
        }
    }

    @Override
    @Nullable
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load) {
        Chunk chunk;
        if (this.array.inView(chunkX, chunkZ) && ClientChunkProvider.isValid(chunk = this.array.get(this.array.getIndex(chunkX, chunkZ)), chunkX, chunkZ)) {
            return chunk;
        }
        return load ? this.empty : null;
    }

    @Override
    public IBlockReader getWorld() {
        return this.world;
    }

    @Nullable
    public Chunk loadChunk(int chunkX, int chunkZ, @Nullable BiomeContainer biomeContainerIn, PacketBuffer packetIn, CompoundNBT nbtTagIn, int sizeIn, boolean p_228313_7_) {
        if (!this.array.inView(chunkX, chunkZ)) {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", (Object)chunkX, (Object)chunkZ);
            return null;
        }
        int i = this.array.getIndex(chunkX, chunkZ);
        Chunk chunk = this.array.chunks.get(i);
        if (!p_228313_7_ && ClientChunkProvider.isValid(chunk, chunkX, chunkZ)) {
            boolean flag = false;
            if (chunk instanceof ChunkOF) {
                ChunkOF chunkof = (ChunkOF)chunk;
                Object object = packetIn.getCustomData("ChunkDataOF");
                if (object instanceof ChunkDataOF) {
                    ChunkDataOF chunkdataof = (ChunkDataOF)object;
                    chunkof.setChunkDataOF(chunkdataof);
                    ChunkSection.THREAD_CHUNK_DATA_OF.set(chunkdataof);
                    flag = true;
                }
            }
            chunk.read(biomeContainerIn, packetIn, nbtTagIn, sizeIn);
            if (flag) {
                ChunkSection.THREAD_CHUNK_DATA_OF.set(null);
            }
        } else {
            if (biomeContainerIn == null) {
                LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", (Object)chunkX, (Object)chunkZ);
                return null;
            }
            if (chunk != null) {
                chunk.setLoaded(false);
            }
            chunk = new ChunkOF(this.world, new ChunkPos(chunkX, chunkZ), biomeContainerIn);
            chunk.read(biomeContainerIn, packetIn, nbtTagIn, sizeIn);
            this.array.replace(i, chunk);
        }
        ChunkSection[] achunksection = chunk.getSections();
        WorldLightManager worldlightmanager = this.getLightManager();
        worldlightmanager.enableLightSources(new ChunkPos(chunkX, chunkZ), true);
        for (int j = 0; j < achunksection.length; ++j) {
            ChunkSection chunksection = achunksection[j];
            worldlightmanager.updateSectionStatus(SectionPos.of(chunkX, j, chunkZ), ChunkSection.isEmpty(chunksection));
        }
        this.world.onChunkLoaded(chunkX, chunkZ);
        if (Reflector.ChunkEvent_Load_Constructor.exists()) {
            Reflector.postForgeBusEvent(Reflector.ChunkEvent_Load_Constructor, chunk);
        }
        chunk.setLoaded(true);
        return chunk;
    }

    public void tick(BooleanSupplier hasTimeLeft) {
    }

    public void setCenter(int x, int z) {
        this.array.centerX = x;
        this.array.centerZ = z;
    }

    public void setViewDistance(int viewDistance) {
        int i = this.array.viewDistance;
        int j = ClientChunkProvider.adjustViewDistance(viewDistance);
        if (i != j) {
            ChunkArray clientchunkprovider$chunkarray = new ChunkArray(j);
            clientchunkprovider$chunkarray.centerX = this.array.centerX;
            clientchunkprovider$chunkarray.centerZ = this.array.centerZ;
            for (int k = 0; k < this.array.chunks.length(); ++k) {
                Chunk chunk = this.array.chunks.get(k);
                if (chunk == null) continue;
                ChunkPos chunkpos = chunk.getPos();
                if (!clientchunkprovider$chunkarray.inView(chunkpos.x, chunkpos.z)) continue;
                clientchunkprovider$chunkarray.replace(clientchunkprovider$chunkarray.getIndex(chunkpos.x, chunkpos.z), chunk);
            }
            this.array = clientchunkprovider$chunkarray;
        }
    }

    private static int adjustViewDistance(int p_217254_0_) {
        return Math.max(2, p_217254_0_) + 3;
    }

    @Override
    public String makeString() {
        return "Client Chunk Cache: " + this.array.chunks.length() + ", " + this.getLoadedChunksCount();
    }

    public int getLoadedChunksCount() {
        return this.array.loaded;
    }

    @Override
    public void markLightChanged(LightType type, SectionPos pos) {
        Minecraft.getInstance().worldRenderer.markForRerender(pos.getSectionX(), pos.getSectionY(), pos.getSectionZ());
    }

    @Override
    public boolean canTick(BlockPos pos) {
        return this.chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
    }

    @Override
    public boolean isChunkLoaded(ChunkPos pos) {
        return this.chunkExists(pos.x, pos.z);
    }

    @Override
    public boolean isChunkLoaded(Entity entityIn) {
        return this.chunkExists(MathHelper.floor(entityIn.getPosX()) >> 4, MathHelper.floor(entityIn.getPosZ()) >> 4);
    }

    @Override
    public ClientChunkProvider createThreadSafeCopy() {
        IChunkArray arr = this.extractReferenceArray();
        ClientChunkProvider result = new ClientChunkProvider(this.world, arr.viewDistance() - 3);
        IChunkArray copyArr = result.extractReferenceArray();
        copyArr.copyFrom(arr);
        if (copyArr.viewDistance() != arr.viewDistance()) {
            throw new IllegalStateException(copyArr.viewDistance() + " " + arr.viewDistance());
        }
        return result;
    }

    @Override
    public IChunkArray extractReferenceArray() {
        return this.array;
    }

    final class ChunkArray
    implements IChunkArray {
        private final AtomicReferenceArray<Chunk> chunks;
        private final int viewDistance;
        private final int sideLength;
        private volatile int centerX;
        private volatile int centerZ;
        private int loaded;

        private ChunkArray(int viewDistanceIn) {
            this.viewDistance = viewDistanceIn;
            this.sideLength = viewDistanceIn * 2 + 1;
            this.chunks = new AtomicReferenceArray(this.sideLength * this.sideLength);
        }

        private int getIndex(int x, int z) {
            return Math.floorMod(z, this.sideLength) * this.sideLength + Math.floorMod(x, this.sideLength);
        }

        protected void replace(int chunkIndex, @Nullable Chunk chunkIn) {
            Chunk chunk = this.chunks.getAndSet(chunkIndex, chunkIn);
            if (chunk != null) {
                --this.loaded;
                ClientChunkProvider.this.world.onChunkUnloaded(chunk);
            }
            if (chunkIn != null) {
                ++this.loaded;
            }
        }

        protected Chunk unload(int chunkIndex, Chunk chunkIn, @Nullable Chunk replaceWith) {
            if (this.chunks.compareAndSet(chunkIndex, chunkIn, replaceWith) && replaceWith == null) {
                --this.loaded;
            }
            ClientChunkProvider.this.world.onChunkUnloaded(chunkIn);
            return chunkIn;
        }

        private boolean inView(int x, int z) {
            return Math.abs(x - this.centerX) <= this.viewDistance && Math.abs(z - this.centerZ) <= this.viewDistance;
        }

        @Nullable
        protected Chunk get(int chunkIndex) {
            return this.chunks.get(chunkIndex);
        }

        @Override
        public void copyFrom(IChunkArray other) {
            this.centerX = other.centerX();
            this.centerZ = other.centerZ();
            AtomicReferenceArray<Chunk> copyingFrom = other.getChunks();
            for (int k = 0; k < copyingFrom.length(); ++k) {
                Chunk chunk = copyingFrom.get(k);
                if (chunk == null) continue;
                ChunkPos chunkpos = chunk.getPos();
                if (!this.inView(chunkpos.x, chunkpos.z)) continue;
                int index = this.getIndex(chunkpos.x, chunkpos.z);
                if (this.chunks.get(index) != null) {
                    throw new IllegalStateException("Doing this would mutate the client's REAL loaded chunks?!");
                }
                this.replace(index, chunk);
            }
        }

        @Override
        public int centerX() {
            return this.centerX;
        }

        @Override
        public int centerZ() {
            return this.centerZ;
        }

        @Override
        public int viewDistance() {
            return this.viewDistance;
        }

        @Override
        public AtomicReferenceArray<Chunk> getChunks() {
            return this.chunks;
        }
    }
}
