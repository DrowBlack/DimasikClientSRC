package dimasik.itemics.cache;

import dimasik.itemics.api.utils.BlockUtils;
import dimasik.itemics.cache.CachedChunk;
import dimasik.itemics.pathing.movement.MovementHelper;
import dimasik.itemics.utils.BlockStateInterface;
import dimasik.itemics.utils.pathing.PathingBlockType;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.TallGrassBlock;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public final class ChunkPacker {
    private ChunkPacker() {
    }

    public static CachedChunk pack(Chunk chunk) {
        HashMap<String, List<BlockPos>> specialBlocks = new HashMap<String, List<BlockPos>>();
        BitSet bitSet = new BitSet(131072);
        try {
            ChunkSection[] chunkInternalStorageArray = chunk.getSections();
            for (int y0 = 0; y0 < 16; ++y0) {
                ChunkSection extendedblockstorage = chunkInternalStorageArray[y0];
                if (extendedblockstorage == null) continue;
                PalettedContainer<BlockState> bsc = extendedblockstorage.getData();
                int yReal = y0 << 4;
                for (int y1 = 0; y1 < 16; ++y1) {
                    int y = y1 | yReal;
                    for (int z = 0; z < 16; ++z) {
                        for (int x = 0; x < 16; ++x) {
                            int index = CachedChunk.getPositionIndex(x, y, z);
                            BlockState state = bsc.get(x, y1, z);
                            boolean[] bits = ChunkPacker.getPathingBlockType(state, chunk, x, y, z).getBits();
                            bitSet.set(index, bits[0]);
                            bitSet.set(index + 1, bits[1]);
                            Block block = state.getBlock();
                            if (!CachedChunk.BLOCKS_TO_KEEP_TRACK_OF.contains(block)) continue;
                            String name = BlockUtils.blockToString(block);
                            specialBlocks.computeIfAbsent(name, b -> new ArrayList()).add(new BlockPos(x, y, z));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        BlockState[] blocks = new BlockState[256];
        for (int z = 0; z < 16; ++z) {
            block7: for (int x = 0; x < 16; ++x) {
                for (int y = 255; y >= 0; --y) {
                    int index = CachedChunk.getPositionIndex(x, y, z);
                    if (!bitSet.get(index) && !bitSet.get(index + 1)) continue;
                    blocks[z << 4 | x] = BlockStateInterface.getFromChunk(chunk, x, y, z);
                    continue block7;
                }
                blocks[z << 4 | x] = Blocks.AIR.getDefaultState();
            }
        }
        return new CachedChunk(chunk.getPos().x, chunk.getPos().z, bitSet, blocks, specialBlocks, System.currentTimeMillis());
    }

    private static PathingBlockType getPathingBlockType(BlockState state, Chunk chunk, int x, int y, int z) {
        Block block = state.getBlock();
        if (MovementHelper.isWater(state)) {
            if (MovementHelper.possiblyFlowing(state)) {
                return PathingBlockType.AVOID;
            }
            if (x != 15 && MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(chunk, x + 1, y, z)) || x != 0 && MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(chunk, x - 1, y, z)) || z != 15 && MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(chunk, x, y, z + 1)) || z != 0 && MovementHelper.possiblyFlowing(BlockStateInterface.getFromChunk(chunk, x, y, z - 1))) {
                return PathingBlockType.AVOID;
            }
            if (x == 0 || x == 15 || z == 0 || z == 15) {
                Vector3d flow = state.getFluidState().getFlow(chunk.getWorld(), new BlockPos(x + (chunk.getPos().x << 4), y, z + (chunk.getPos().z << 4)));
                if (flow.x != 0.0 || flow.z != 0.0) {
                    return PathingBlockType.WATER;
                }
                return PathingBlockType.AVOID;
            }
            return PathingBlockType.WATER;
        }
        if (MovementHelper.avoidWalkingInto(state) || MovementHelper.isBottomSlab(state)) {
            return PathingBlockType.AVOID;
        }
        if (block instanceof AirBlock || block instanceof TallGrassBlock || block instanceof DoublePlantBlock || block instanceof FlowerBlock) {
            return PathingBlockType.AIR;
        }
        return PathingBlockType.SOLID;
    }

    public static BlockState pathingTypeToBlock(PathingBlockType type, RegistryKey<World> dimension) {
        switch (type) {
            case AIR: {
                return Blocks.AIR.getDefaultState();
            }
            case WATER: {
                return Blocks.WATER.getDefaultState();
            }
            case AVOID: {
                return Blocks.LAVA.getDefaultState();
            }
            case SOLID: {
                if (dimension == World.OVERWORLD) {
                    return Blocks.STONE.getDefaultState();
                }
                if (dimension == World.THE_NETHER) {
                    return Blocks.NETHERRACK.getDefaultState();
                }
                if (dimension != World.THE_END) break;
                return Blocks.END_STONE.getDefaultState();
            }
        }
        return null;
    }
}
