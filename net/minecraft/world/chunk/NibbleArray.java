package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class NibbleArray {
    @Nullable
    protected byte[] data;

    public NibbleArray() {
    }

    public NibbleArray(byte[] storageArray) {
        this.data = storageArray;
        if (storageArray.length != 2048) {
            throw Util.pauseDevMode(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + storageArray.length));
        }
    }

    protected NibbleArray(int size) {
        this.data = new byte[size];
    }

    public int get(int x, int y, int z) {
        return this.getFromIndex(this.getCoordinateIndex(x, y, z));
    }

    public void set(int x, int y, int z, int value) {
        this.setIndex(this.getCoordinateIndex(x, y, z), value);
    }

    protected int getCoordinateIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    private int getFromIndex(int index) {
        if (this.data == null) {
            return 0;
        }
        int i = this.getNibbleIndex(index);
        return this.isLowerNibble(index) ? this.data[i] & 0xF : this.data[i] >> 4 & 0xF;
    }

    private void setIndex(int index, int value) {
        if (this.data == null) {
            this.data = new byte[2048];
        }
        int i = this.getNibbleIndex(index);
        this.data[i] = this.isLowerNibble(index) ? (byte)(this.data[i] & 0xF0 | value & 0xF) : (byte)(this.data[i] & 0xF | (value & 0xF) << 4);
    }

    private boolean isLowerNibble(int index) {
        return (index & 1) == 0;
    }

    private int getNibbleIndex(int index) {
        return index >> 1;
    }

    public byte[] getData() {
        if (this.data == null) {
            this.data = new byte[2048];
        }
        return this.data;
    }

    public NibbleArray copy() {
        return this.data == null ? new NibbleArray() : new NibbleArray((byte[])this.data.clone());
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        for (int i = 0; i < 4096; ++i) {
            stringbuilder.append(Integer.toHexString(this.getFromIndex(i)));
            if ((i & 0xF) == 15) {
                stringbuilder.append("\n");
            }
            if ((i & 0xFF) != 255) continue;
            stringbuilder.append("\n");
        }
        return stringbuilder.toString();
    }

    public boolean isEmpty() {
        return this.data == null;
    }
}
