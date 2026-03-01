package net.minecraft.util;

import net.minecraft.util.IIntArray;

public abstract class IntReferenceHolder {
    private int lastKnownValue;

    public static IntReferenceHolder create(final IIntArray data, final int idx) {
        return new IntReferenceHolder(){

            @Override
            public int get() {
                return data.get(idx);
            }

            @Override
            public void set(int value) {
                data.set(idx, value);
            }
        };
    }

    public static IntReferenceHolder create(final int[] data, final int idx) {
        return new IntReferenceHolder(){

            @Override
            public int get() {
                return data[idx];
            }

            @Override
            public void set(int value) {
                data[idx] = value;
            }
        };
    }

    public static IntReferenceHolder single() {
        return new IntReferenceHolder(){
            private int value;

            @Override
            public int get() {
                return this.value;
            }

            @Override
            public void set(int value) {
                this.value = value;
            }
        };
    }

    public abstract int get();

    public abstract void set(int var1);

    public boolean isDirty() {
        int i = this.get();
        boolean flag = i != this.lastKnownValue;
        this.lastKnownValue = i;
        return flag;
    }
}
