package net.minecraft.world.chunk;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.palette.IPalette;
import net.minecraft.util.palette.IdentityPalette;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraft.world.chunk.Chunk;
import net.optifine.ChunkDataOF;
import net.optifine.ChunkSectionDataOF;

public class ChunkSection {
    private static final IPalette<BlockState> REGISTRY_PALETTE = new IdentityPalette<BlockState>(Block.BLOCK_STATE_IDS, Blocks.AIR.getDefaultState());
    private final int yBase;
    private short blockRefCount;
    private short blockTickRefCount;
    private short fluidRefCount;
    private final PalettedContainer<BlockState> data;
    public static final ThreadLocal<ChunkDataOF> THREAD_CHUNK_DATA_OF = new ThreadLocal();

    public ChunkSection(int yBaseIn) {
        this(yBaseIn, 0, 0, 0);
    }

    public ChunkSection(int yBaseIn, short blockRefCountIn, short blockTickRefCountIn, short fluidRefCountIn) {
        this.yBase = yBaseIn;
        this.blockRefCount = blockRefCountIn;
        this.blockTickRefCount = blockTickRefCountIn;
        this.fluidRefCount = fluidRefCountIn;
        this.data = new PalettedContainer<BlockState>(REGISTRY_PALETTE, Block.BLOCK_STATE_IDS, NBTUtil::readBlockState, NBTUtil::writeBlockState, Blocks.AIR.getDefaultState());
    }

    public BlockState getBlockState(int x, int y, int z) {
        return this.data.get(x, y, z);
    }

    public FluidState getFluidState(int x, int y, int z) {
        return this.data.get(x, y, z).getFluidState();
    }

    public void lock() {
        this.data.lock();
    }

    public void unlock() {
        this.data.unlock();
    }

    public BlockState setBlockState(int x, int y, int z, BlockState blockStateIn) {
        return this.setBlockState(x, y, z, blockStateIn, true);
    }

    public BlockState setBlockState(int x, int y, int z, BlockState state, boolean useLocks) {
        BlockState blockstate = useLocks ? this.data.lockedSwap(x, y, z, state) : this.data.swap(x, y, z, state);
        FluidState fluidstate = blockstate.getFluidState();
        FluidState fluidstate1 = state.getFluidState();
        if (!blockstate.isAir()) {
            this.blockRefCount = (short)(this.blockRefCount - 1);
            if (blockstate.ticksRandomly()) {
                this.blockTickRefCount = (short)(this.blockTickRefCount - 1);
            }
        }
        if (!fluidstate.isEmpty()) {
            this.fluidRefCount = (short)(this.fluidRefCount - 1);
        }
        if (!state.isAir()) {
            this.blockRefCount = (short)(this.blockRefCount + 1);
            if (state.ticksRandomly()) {
                this.blockTickRefCount = (short)(this.blockTickRefCount + 1);
            }
        }
        if (!fluidstate1.isEmpty()) {
            this.fluidRefCount = (short)(this.fluidRefCount + 1);
        }
        return blockstate;
    }

    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }

    public static boolean isEmpty(@Nullable ChunkSection section) {
        return section == Chunk.EMPTY_SECTION || section.isEmpty();
    }

    public boolean needsRandomTickAny() {
        return this.needsRandomTick() || this.needsRandomTickFluid();
    }

    public boolean needsRandomTick() {
        return this.blockTickRefCount > 0;
    }

    public boolean needsRandomTickFluid() {
        return this.fluidRefCount > 0;
    }

    public int getYLocation() {
        return this.yBase;
    }

    public void recalculateRefCounts() {
        ChunkSectionDataOF chunksectiondataof;
        int i;
        ChunkSectionDataOF[] achunksectiondataof;
        ChunkDataOF chunkdataof = THREAD_CHUNK_DATA_OF.get();
        if (chunkdataof != null && (achunksectiondataof = chunkdataof.getChunkSectionDatas()) != null && (i = this.yBase >> 4) >= 0 && i < achunksectiondataof.length && (chunksectiondataof = achunksectiondataof[i]) != null) {
            this.blockRefCount = chunksectiondataof.getBlockRefCount();
            this.blockTickRefCount = chunksectiondataof.getTickRefCount();
            this.fluidRefCount = chunksectiondataof.getFluidRefCount();
            achunksectiondataof[i] = null;
            return;
        }
        this.blockRefCount = 0;
        this.blockTickRefCount = 0;
        this.fluidRefCount = 0;
        this.data.count((p_lambda$recalculateRefCounts$0_1_, p_lambda$recalculateRefCounts$0_2_) -> {
            FluidState fluidstate = p_lambda$recalculateRefCounts$0_1_.getFluidState();
            if (!p_lambda$recalculateRefCounts$0_1_.isAir()) {
                this.blockRefCount = (short)(this.blockRefCount + p_lambda$recalculateRefCounts$0_2_);
                if (p_lambda$recalculateRefCounts$0_1_.ticksRandomly()) {
                    this.blockTickRefCount = (short)(this.blockTickRefCount + p_lambda$recalculateRefCounts$0_2_);
                }
            }
            if (!fluidstate.isEmpty()) {
                this.blockRefCount = (short)(this.blockRefCount + p_lambda$recalculateRefCounts$0_2_);
                if (fluidstate.ticksRandomly()) {
                    this.fluidRefCount = (short)(this.fluidRefCount + p_lambda$recalculateRefCounts$0_2_);
                }
            }
        });
    }

    public PalettedContainer<BlockState> getData() {
        return this.data;
    }

    public void read(PacketBuffer packetBufferIn) {
        this.blockRefCount = packetBufferIn.readShort();
        this.data.read(packetBufferIn);
    }

    public void write(PacketBuffer packetBufferIn) {
        packetBufferIn.writeShort(this.blockRefCount);
        this.data.write(packetBufferIn);
    }

    public int getSize() {
        return 2 + this.data.getSerializedSize();
    }

    public boolean isValidPOIState(Predicate<BlockState> predicate) {
        return this.data.func_235963_a_(predicate);
    }

    public short getBlockRefCount() {
        return this.blockRefCount;
    }

    public short getTickRefCount() {
        return this.blockTickRefCount;
    }

    public short getFluidRefCount() {
        return this.fluidRefCount;
    }
}
