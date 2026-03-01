package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IntNBT
extends NumberNBT {
    public static final INBTType<IntNBT> TYPE = new INBTType<IntNBT>(){

        @Override
        public IntNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
            accounter.read(96L);
            return IntNBT.valueOf(input.readInt());
        }

        @Override
        public String getName() {
            return "INT";
        }

        @Override
        public String getTagName() {
            return "TAG_Int";
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }
    };
    private final int data;

    private IntNBT(int data) {
        this.data = data;
    }

    public static IntNBT valueOf(int dataIn) {
        return dataIn >= -128 && dataIn <= 1024 ? Cache.CACHE[dataIn + 128] : new IntNBT(dataIn);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.data);
    }

    @Override
    public byte getId() {
        return 3;
    }

    public INBTType<IntNBT> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return String.valueOf(this.data);
    }

    @Override
    public IntNBT copy() {
        return this;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        return p_equals_1_ instanceof IntNBT && this.data == ((IntNBT)p_equals_1_).data;
    }

    public int hashCode() {
        return this.data;
    }

    @Override
    public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
        return new StringTextComponent(String.valueOf(this.data)).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public long getLong() {
        return this.data;
    }

    @Override
    public int getInt() {
        return this.data;
    }

    @Override
    public short getShort() {
        return (short)(this.data & 0xFFFF);
    }

    @Override
    public byte getByte() {
        return (byte)(this.data & 0xFF);
    }

    @Override
    public double getDouble() {
        return this.data;
    }

    @Override
    public float getFloat() {
        return this.data;
    }

    @Override
    public Number getAsNumber() {
        return this.data;
    }

    static class Cache {
        static final IntNBT[] CACHE = new IntNBT[1153];

        Cache() {
        }

        static {
            for (int i = 0; i < CACHE.length; ++i) {
                Cache.CACHE[i] = new IntNBT(-128 + i);
            }
        }
    }
}
