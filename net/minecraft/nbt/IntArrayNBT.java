package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class IntArrayNBT
extends CollectionNBT<IntNBT> {
    public static final INBTType<IntArrayNBT> TYPE = new INBTType<IntArrayNBT>(){

        @Override
        public IntArrayNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
            accounter.read(192L);
            int i = input.readInt();
            accounter.read(32L * (long)i);
            int[] aint = new int[i];
            for (int j = 0; j < i; ++j) {
                aint[j] = input.readInt();
            }
            return new IntArrayNBT(aint);
        }

        @Override
        public String getName() {
            return "INT[]";
        }

        @Override
        public String getTagName() {
            return "TAG_Int_Array";
        }
    };
    private int[] intArray;

    public IntArrayNBT(int[] intArray) {
        this.intArray = intArray;
    }

    public IntArrayNBT(List<Integer> integers) {
        this(IntArrayNBT.toArray(integers));
    }

    private static int[] toArray(List<Integer> integers) {
        int[] aint = new int[integers.size()];
        for (int i = 0; i < integers.size(); ++i) {
            Integer integer = integers.get(i);
            aint[i] = integer == null ? 0 : integer;
        }
        return aint;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.intArray.length);
        for (int i : this.intArray) {
            output.writeInt(i);
        }
    }

    @Override
    public byte getId() {
        return 11;
    }

    public INBTType<IntArrayNBT> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[I;");
        for (int i = 0; i < this.intArray.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }
            stringbuilder.append(this.intArray[i]);
        }
        return stringbuilder.append(']').toString();
    }

    @Override
    public IntArrayNBT copy() {
        int[] aint = new int[this.intArray.length];
        System.arraycopy(this.intArray, 0, aint, 0, this.intArray.length);
        return new IntArrayNBT(aint);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        return p_equals_1_ instanceof IntArrayNBT && Arrays.equals(this.intArray, ((IntArrayNBT)p_equals_1_).intArray);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.intArray);
    }

    public int[] getIntArray() {
        return this.intArray;
    }

    @Override
    public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
        IFormattableTextComponent itextcomponent = new StringTextComponent("I").mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("[").append(itextcomponent).appendString(";");
        for (int i = 0; i < this.intArray.length; ++i) {
            iformattabletextcomponent.appendString(" ").append(new StringTextComponent(String.valueOf(this.intArray[i])).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER));
            if (i == this.intArray.length - 1) continue;
            iformattabletextcomponent.appendString(",");
        }
        iformattabletextcomponent.appendString("]");
        return iformattabletextcomponent;
    }

    @Override
    public int size() {
        return this.intArray.length;
    }

    @Override
    public IntNBT get(int p_get_1_) {
        return IntNBT.valueOf(this.intArray[p_get_1_]);
    }

    @Override
    public IntNBT set(int p_set_1_, IntNBT p_set_2_) {
        int i = this.intArray[p_set_1_];
        this.intArray[p_set_1_] = p_set_2_.getInt();
        return IntNBT.valueOf(i);
    }

    @Override
    public void add(int p_add_1_, IntNBT p_add_2_) {
        this.intArray = ArrayUtils.add(this.intArray, p_add_1_, p_add_2_.getInt());
    }

    @Override
    public boolean setNBTByIndex(int index, INBT nbt) {
        if (nbt instanceof NumberNBT) {
            this.intArray[index] = ((NumberNBT)nbt).getInt();
            return true;
        }
        return false;
    }

    @Override
    public boolean addNBTByIndex(int index, INBT nbt) {
        if (nbt instanceof NumberNBT) {
            this.intArray = ArrayUtils.add(this.intArray, index, ((NumberNBT)nbt).getInt());
            return true;
        }
        return false;
    }

    @Override
    public IntNBT remove(int p_remove_1_) {
        int i = this.intArray[p_remove_1_];
        this.intArray = ArrayUtils.remove(this.intArray, p_remove_1_);
        return IntNBT.valueOf(i);
    }

    @Override
    public byte getTagType() {
        return 3;
    }

    @Override
    public void clear() {
        this.intArray = new int[0];
    }
}
