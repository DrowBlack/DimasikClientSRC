package net.minecraft.nbt;

import java.io.DataInput;
import java.io.IOException;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTSizeTracker;

public interface INBTType<T extends INBT> {
    public T readNBT(DataInput var1, int var2, NBTSizeTracker var3) throws IOException;

    default public boolean isPrimitive() {
        return false;
    }

    public String getName();

    public String getTagName();

    public static INBTType<EndNBT> getEndNBT(final int id) {
        return new INBTType<EndNBT>(){

            @Override
            public EndNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
                throw new IllegalArgumentException("Invalid tag id: " + id);
            }

            @Override
            public String getName() {
                return "INVALID[" + id + "]";
            }

            @Override
            public String getTagName() {
                return "UNKNOWN_" + id;
            }
        };
    }
}
