package net.minecraft.util;

import net.minecraft.util.IIntArray;

public class IntArray
implements IIntArray {
    private final int[] array;

    public IntArray(int size) {
        this.array = new int[size];
    }

    @Override
    public int get(int index) {
        return this.array[index];
    }

    @Override
    public void set(int index, int value) {
        this.array[index] = value;
    }

    @Override
    public int size() {
        return this.array.length;
    }
}
