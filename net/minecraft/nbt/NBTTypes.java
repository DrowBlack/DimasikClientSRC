package net.minecraft.nbt;

import net.minecraft.nbt.ByteArrayNBT;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.EndNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.ShortNBT;
import net.minecraft.nbt.StringNBT;

public class NBTTypes {
    private static final INBTType<?>[] TYPES = new INBTType[]{EndNBT.TYPE, ByteNBT.TYPE, ShortNBT.TYPE, IntNBT.TYPE, LongNBT.TYPE, FloatNBT.TYPE, DoubleNBT.TYPE, ByteArrayNBT.TYPE, StringNBT.TYPE, ListNBT.TYPE, CompoundNBT.TYPE, IntArrayNBT.TYPE, LongArrayNBT.TYPE};

    public static INBTType<?> getGetTypeByID(int id) {
        return id >= 0 && id < TYPES.length ? TYPES[id] : INBTType.getEndNBT(id);
    }
}
