package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class DoubleNBT
extends NumberNBT {
    public static final DoubleNBT ZERO = new DoubleNBT(0.0);
    public static final INBTType<DoubleNBT> TYPE = new INBTType<DoubleNBT>(){

        @Override
        public DoubleNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
            accounter.read(128L);
            return DoubleNBT.valueOf(input.readDouble());
        }

        @Override
        public String getName() {
            return "DOUBLE";
        }

        @Override
        public String getTagName() {
            return "TAG_Double";
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }
    };
    private final double data;

    private DoubleNBT(double data) {
        this.data = data;
    }

    public static DoubleNBT valueOf(double value) {
        return value == 0.0 ? ZERO : new DoubleNBT(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.data);
    }

    @Override
    public byte getId() {
        return 6;
    }

    public INBTType<DoubleNBT> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.data + "d";
    }

    @Override
    public DoubleNBT copy() {
        return this;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        return p_equals_1_ instanceof DoubleNBT && this.data == ((DoubleNBT)p_equals_1_).data;
    }

    public int hashCode() {
        long i = Double.doubleToLongBits(this.data);
        return (int)(i ^ i >>> 32);
    }

    @Override
    public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
        IFormattableTextComponent itextcomponent = new StringTextComponent("d").mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new StringTextComponent(String.valueOf(this.data)).append(itextcomponent).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public long getLong() {
        return (long)Math.floor(this.data);
    }

    @Override
    public int getInt() {
        return MathHelper.floor(this.data);
    }

    @Override
    public short getShort() {
        return (short)(MathHelper.floor(this.data) & 0xFFFF);
    }

    @Override
    public byte getByte() {
        return (byte)(MathHelper.floor(this.data) & 0xFF);
    }

    @Override
    public double getDouble() {
        return this.data;
    }

    @Override
    public float getFloat() {
        return (float)this.data;
    }

    @Override
    public Number getAsNumber() {
        return this.data;
    }
}
