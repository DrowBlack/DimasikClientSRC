package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ShortNBT
extends NumberNBT {
    public static final INBTType<ShortNBT> TYPE = new INBTType<ShortNBT>(){

        @Override
        public ShortNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
            accounter.read(80L);
            return ShortNBT.valueOf(input.readShort());
        }

        @Override
        public String getName() {
            return "SHORT";
        }

        @Override
        public String getTagName() {
            return "TAG_Short";
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }
    };
    private final short data;

    private ShortNBT(short data) {
        this.data = data;
    }

    public static ShortNBT valueOf(short value) {
        return value >= -128 && value <= 1024 ? Cache.CACHE[value + 128] : new ShortNBT(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeShort(this.data);
    }

    @Override
    public byte getId() {
        return 2;
    }

    public INBTType<ShortNBT> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.data + "s";
    }

    @Override
    public ShortNBT copy() {
        return this;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        return p_equals_1_ instanceof ShortNBT && this.data == ((ShortNBT)p_equals_1_).data;
    }

    public int hashCode() {
        return this.data;
    }

    @Override
    public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
        IFormattableTextComponent itextcomponent = new StringTextComponent("s").mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new StringTextComponent(String.valueOf(this.data)).append(itextcomponent).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
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
        return this.data;
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
        static final ShortNBT[] CACHE = new ShortNBT[1153];

        Cache() {
        }

        static {
            for (int i = 0; i < CACHE.length; ++i) {
                Cache.CACHE[i] = new ShortNBT((short)(-128 + i));
            }
        }
    }
}
