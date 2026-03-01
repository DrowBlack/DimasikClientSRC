package dimasik.itemics.utils.schematic.format.defaults;

import dimasik.itemics.utils.schematic.StaticSchematic;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.Validate;

public final class LitematicaSchematic
extends StaticSchematic {
    private final Vector3i offsetMinCorner;
    private final CompoundNBT nbt;

    public LitematicaSchematic(CompoundNBT nbtTagCompound, boolean rotated) {
        this.nbt = nbtTagCompound;
        this.offsetMinCorner = new Vector3i(this.getMinOfSchematic("x"), this.getMinOfSchematic("y"), this.getMinOfSchematic("z"));
        this.y = Math.abs(this.nbt.getCompound("Metadata").getCompound("EnclosingSize").getInt("y"));
        if (rotated) {
            this.x = Math.abs(this.nbt.getCompound("Metadata").getCompound("EnclosingSize").getInt("z"));
            this.z = Math.abs(this.nbt.getCompound("Metadata").getCompound("EnclosingSize").getInt("x"));
        } else {
            this.x = Math.abs(this.nbt.getCompound("Metadata").getCompound("EnclosingSize").getInt("x"));
            this.z = Math.abs(this.nbt.getCompound("Metadata").getCompound("EnclosingSize").getInt("z"));
        }
        this.states = new BlockState[this.x][this.z][this.y];
        this.fillInSchematic();
    }

    private static String[] getRegions(CompoundNBT nbt) {
        return nbt.getCompound("Regions").keySet().toArray(new String[0]);
    }

    private static int getMinOfSubregion(CompoundNBT nbt, String subReg, String s) {
        int a = nbt.getCompound("Regions").getCompound(subReg).getCompound("Position").getInt(s);
        int b = nbt.getCompound("Regions").getCompound(subReg).getCompound("Size").getInt(s);
        if (b < 0) {
            ++b;
        }
        return Math.min(a, a + b);
    }

    private static BlockState[] getBlockList(ListNBT blockStatePalette) {
        BlockState[] blockList = new BlockState[blockStatePalette.size()];
        for (int i = 0; i < blockStatePalette.size(); ++i) {
            Block block = Registry.BLOCK.getOrDefault(new ResourceLocation(((CompoundNBT)blockStatePalette.get(i)).getString("Name")));
            CompoundNBT properties = ((CompoundNBT)blockStatePalette.get(i)).getCompound("Properties");
            blockList[i] = LitematicaSchematic.getBlockState(block, properties);
        }
        return blockList;
    }

    private static BlockState getBlockState(Block block, CompoundNBT properties) {
        BlockState blockState = block.getDefaultState();
        for (Object key : properties.keySet().toArray()) {
            Property<?> property = block.getStateContainer().getProperty((String)key);
            String propertyValue = properties.getString((String)key);
            if (property == null) continue;
            blockState = LitematicaSchematic.setPropertyValue(blockState, property, propertyValue);
        }
        return blockState;
    }

    private static <T extends Comparable<T>> BlockState setPropertyValue(BlockState state, Property<T> property, String value) {
        Optional<T> parsed = property.parseValue(value);
        if (parsed.isPresent()) {
            return (BlockState)state.with(property, (Comparable)parsed.get());
        }
        throw new IllegalArgumentException("Invalid value for property " + String.valueOf(property));
    }

    private static int getBitsPerBlock(int amountOfBlockTypes) {
        return (int)Math.max(2.0, Math.ceil(Math.log(amountOfBlockTypes) / Math.log(2.0)));
    }

    private static long getVolume(CompoundNBT nbt, String subReg) {
        return Math.abs(nbt.getCompound("Regions").getCompound(subReg).getCompound("Size").getInt("x") * nbt.getCompound("Regions").getCompound(subReg).getCompound("Size").getInt("y") * nbt.getCompound("Regions").getCompound(subReg).getCompound("Size").getInt("z"));
    }

    private static long[] getBlockStates(CompoundNBT nbt, String subReg) {
        return nbt.getCompound("Regions").getCompound(subReg).getLongArray("BlockStates");
    }

    private static boolean inSubregion(CompoundNBT nbt, String subReg, int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < Math.abs(nbt.getCompound("Regions").getCompound(subReg).getCompound("Size").getInt("x")) && y < Math.abs(nbt.getCompound("Regions").getCompound(subReg).getCompound("Size").getInt("y")) && z < Math.abs(nbt.getCompound("Regions").getCompound(subReg).getCompound("Size").getInt("z"));
    }

    private int getMinOfSchematic(String s) {
        int n = Integer.MAX_VALUE;
        for (String subReg : LitematicaSchematic.getRegions(this.nbt)) {
            n = Math.min(n, LitematicaSchematic.getMinOfSubregion(this.nbt, subReg, s));
        }
        return n;
    }

    private void fillInSchematic() {
        for (String subReg : LitematicaSchematic.getRegions(this.nbt)) {
            ListNBT usedBlockTypes = this.nbt.getCompound("Regions").getCompound(subReg).getList("BlockStatePalette", 10);
            BlockState[] blockList = LitematicaSchematic.getBlockList(usedBlockTypes);
            int bitsPerBlock = LitematicaSchematic.getBitsPerBlock(usedBlockTypes.size());
            long regionVolume = LitematicaSchematic.getVolume(this.nbt, subReg);
            long[] blockStateArray = LitematicaSchematic.getBlockStates(this.nbt, subReg);
            LitematicaBitArray bitArray = new LitematicaBitArray(bitsPerBlock, regionVolume, blockStateArray);
            this.writeSubregionIntoSchematic(this.nbt, subReg, blockList, bitArray);
        }
    }

    private void writeSubregionIntoSchematic(CompoundNBT nbt, String subReg, BlockState[] blockList, LitematicaBitArray bitArray) {
        Vector3i offsetSubregion = new Vector3i(LitematicaSchematic.getMinOfSubregion(nbt, subReg, "x"), LitematicaSchematic.getMinOfSubregion(nbt, subReg, "y"), LitematicaSchematic.getMinOfSubregion(nbt, subReg, "z"));
        int index = 0;
        for (int y = 0; y < this.y; ++y) {
            for (int z = 0; z < this.z; ++z) {
                for (int x = 0; x < this.x; ++x) {
                    if (!LitematicaSchematic.inSubregion(nbt, subReg, x, y, z)) continue;
                    this.states[x - (this.offsetMinCorner.getX() - offsetSubregion.getX())][z - (this.offsetMinCorner.getZ() - offsetSubregion.getZ())][y - (this.offsetMinCorner.getY() - offsetSubregion.getY())] = blockList[bitArray.getAt(index)];
                    ++index;
                }
            }
        }
    }

    public Vector3i getOffsetMinCorner() {
        return this.offsetMinCorner;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public void setDirect(int x, int y, int z, BlockState blockState) {
        this.states[x][z][y] = blockState;
    }

    public LitematicaSchematic getCopy(boolean rotated) {
        return new LitematicaSchematic(this.nbt, rotated);
    }

    private static class LitematicaBitArray {
        private final long[] longArray;
        private final int bitsPerEntry;
        private final long maxEntryValue;
        private final long arraySize;

        public LitematicaBitArray(int bitsPerEntryIn, long arraySizeIn, @Nullable long[] longArrayIn) {
            Validate.inclusiveBetween(1L, 32L, bitsPerEntryIn);
            this.arraySize = arraySizeIn;
            this.bitsPerEntry = bitsPerEntryIn;
            this.maxEntryValue = (1L << bitsPerEntryIn) - 1L;
            this.longArray = longArrayIn != null ? longArrayIn : new long[(int)(LitematicaBitArray.roundUp(arraySizeIn * (long)bitsPerEntryIn, 64L) / 64L)];
        }

        public static long roundUp(long number, long interval) {
            long i;
            int sign = 1;
            if (interval == 0L) {
                return 0L;
            }
            if (number == 0L) {
                return interval;
            }
            if (number < 0L) {
                sign = -1;
            }
            return (i = number % (interval * (long)sign)) == 0L ? number : number + interval * (long)sign - i;
        }

        public int getAt(long index) {
            Validate.inclusiveBetween(0L, this.arraySize - 1L, index);
            long startOffset = index * (long)this.bitsPerEntry;
            int startArrIndex = (int)(startOffset >> 6);
            int endArrIndex = (int)((index + 1L) * (long)this.bitsPerEntry - 1L >> 6);
            int startBitOffset = (int)(startOffset & 0x3FL);
            if (startArrIndex == endArrIndex) {
                return (int)(this.longArray[startArrIndex] >>> startBitOffset & this.maxEntryValue);
            }
            int endOffset = 64 - startBitOffset;
            return (int)((this.longArray[startArrIndex] >>> startBitOffset | this.longArray[endArrIndex] << endOffset) & this.maxEntryValue);
        }

        public long size() {
            return this.arraySize;
        }
    }
}
