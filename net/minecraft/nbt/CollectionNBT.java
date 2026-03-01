package net.minecraft.nbt;

import java.util.AbstractList;
import net.minecraft.nbt.INBT;

public abstract class CollectionNBT<T extends INBT>
extends AbstractList<T>
implements INBT {
    @Override
    public abstract T set(int var1, T var2);

    @Override
    public abstract void add(int var1, T var2);

    @Override
    public abstract T remove(int var1);

    public abstract boolean setNBTByIndex(int var1, INBT var2);

    public abstract boolean addNBTByIndex(int var1, INBT var2);

    public abstract byte getTagType();
}
