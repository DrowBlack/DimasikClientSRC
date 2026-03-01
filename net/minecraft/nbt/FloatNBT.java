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

public class FloatNBT
extends NumberNBT {
    public static final FloatNBT ZERO = new FloatNBT(0.0f);
    public static final INBTType<FloatNBT> TYPE = new INBTType<FloatNBT>(){

        @Override
        public FloatNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
            accounter.read(96L);
            return FloatNBT.valueOf(input.readFloat());
        }

        @Override
        public String getName() {
            return "FLOAT";
        }

        @Override
        public String getTagName() {
            return "TAG_Float";
        }

        @Override
        public boolean isPrimitive() {
            return true;
        }
    };
    private final float data;

    private FloatNBT(float data) {
        this.data = data;
    }

    public static FloatNBT valueOf(float value) {
        return value == 0.0f ? ZERO : new FloatNBT(value);
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.data);
    }

    @Override
    public byte getId() {
        return 5;
    }

    public INBTType<FloatNBT> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return this.data + "f";
    }

    @Override
    public FloatNBT copy() {
        return this;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        return p_equals_1_ instanceof FloatNBT && this.data == ((FloatNBT)p_equals_1_).data;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.data);
    }

    @Override
    public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
        IFormattableTextComponent itextcomponent = new StringTextComponent("f").mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        return new StringTextComponent(String.valueOf(this.data)).append(itextcomponent).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
    }

    @Override
    public long getLong() {
        return (long)this.data;
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
        return this.data;
    }

    @Override
    public Number getAsNumber() {
        return Float.valueOf(this.data);
    }
}
