package dimasik.itemics.utils.schematic.format.defaults;

import dimasik.itemics.utils.schematic.StaticSchematic;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.ItemIntIDToString;
import net.minecraft.util.registry.Registry;

public final class MCEditSchematic
extends StaticSchematic {
    public MCEditSchematic(CompoundNBT schematic) {
        String type = schematic.getString("Materials");
        if (!type.equals("Alpha")) {
            throw new IllegalStateException("bad schematic " + type);
        }
        this.x = schematic.getInt("Width");
        this.y = schematic.getInt("Height");
        this.z = schematic.getInt("Length");
        byte[] blocks = schematic.getByteArray("Blocks");
        byte[] additional = null;
        if (schematic.contains("AddBlocks")) {
            byte[] addBlocks = schematic.getByteArray("AddBlocks");
            additional = new byte[addBlocks.length * 2];
            for (int i = 0; i < addBlocks.length; ++i) {
                additional[i * 2 + 0] = (byte)(addBlocks[i] >> 4 & 0xF);
                additional[i * 2 + 1] = (byte)(addBlocks[i] >> 0 & 0xF);
            }
        }
        this.states = new BlockState[this.x][this.z][this.y];
        for (int y = 0; y < this.y; ++y) {
            for (int z = 0; z < this.z; ++z) {
                for (int x = 0; x < this.x; ++x) {
                    int blockInd = (y * this.z + z) * this.x + x;
                    int blockID = blocks[blockInd] & 0xFF;
                    if (additional != null) {
                        blockID |= additional[blockInd] << 8;
                    }
                    Block block = Registry.BLOCK.getOrDefault(ResourceLocation.tryCreate(ItemIntIDToString.getItem(blockID)));
                    this.states[x][z][y] = block.getDefaultState();
                }
            }
        }
    }
}
