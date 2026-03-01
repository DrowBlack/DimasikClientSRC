package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.INBTType;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NumberNBT;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.ArrayUtils;

public class ByteArrayNBT
extends CollectionNBT<ByteNBT> {
    public static final INBTType<ByteArrayNBT> TYPE = new INBTType<ByteArrayNBT>(){

        @Override
        public ByteArrayNBT readNBT(DataInput input, int depth, NBTSizeTracker accounter) throws IOException {
            accounter.read(192L);
            int i = input.readInt();
            accounter.read(8L * (long)i);
            byte[] abyte = new byte[i];
            input.readFully(abyte);
            return new ByteArrayNBT(abyte);
        }

        @Override
        public String getName() {
            return "BYTE[]";
        }

        @Override
        public String getTagName() {
            return "TAG_Byte_Array";
        }
    };
    private byte[] data;

    public ByteArrayNBT(byte[] data) {
        this.data = data;
    }

    public ByteArrayNBT(List<Byte> bytes) {
        this(ByteArrayNBT.toArray(bytes));
    }

    private static byte[] toArray(List<Byte> bytes) {
        byte[] abyte = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); ++i) {
            Byte obyte = bytes.get(i);
            abyte[i] = obyte == null ? (byte)0 : obyte;
        }
        return abyte;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        output.write(this.data);
    }

    @Override
    public byte getId() {
        return 7;
    }

    public INBTType<ByteArrayNBT> getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[B;");
        for (int i = 0; i < this.data.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }
            stringbuilder.append(this.data[i]).append('B');
        }
        return stringbuilder.append(']').toString();
    }

    @Override
    public INBT copy() {
        byte[] abyte = new byte[this.data.length];
        System.arraycopy(this.data, 0, abyte, 0, this.data.length);
        return new ByteArrayNBT(abyte);
    }

    @Override
    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        return p_equals_1_ instanceof ByteArrayNBT && Arrays.equals(this.data, ((ByteArrayNBT)p_equals_1_).data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public ITextComponent toFormattedComponent(String indentation, int indentDepth) {
        IFormattableTextComponent itextcomponent = new StringTextComponent("B").mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER_TYPE);
        IFormattableTextComponent iformattabletextcomponent = new StringTextComponent("[").append(itextcomponent).appendString(";");
        for (int i = 0; i < this.data.length; ++i) {
            IFormattableTextComponent iformattabletextcomponent1 = new StringTextComponent(String.valueOf(this.data[i])).mergeStyle(SYNTAX_HIGHLIGHTING_NUMBER);
            iformattabletextcomponent.appendString(" ").append(iformattabletextcomponent1).append(itextcomponent);
            if (i == this.data.length - 1) continue;
            iformattabletextcomponent.appendString(",");
        }
        iformattabletextcomponent.appendString("]");
        return iformattabletextcomponent;
    }

    public byte[] getByteArray() {
        return this.data;
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public ByteNBT get(int p_get_1_) {
        return ByteNBT.valueOf(this.data[p_get_1_]);
    }

    @Override
    public ByteNBT set(int p_set_1_, ByteNBT p_set_2_) {
        byte b0 = this.data[p_set_1_];
        this.data[p_set_1_] = p_set_2_.getByte();
        return ByteNBT.valueOf(b0);
    }

    @Override
    public void add(int p_add_1_, ByteNBT p_add_2_) {
        this.data = ArrayUtils.add(this.data, p_add_1_, p_add_2_.getByte());
    }

    @Override
    public boolean setNBTByIndex(int index, INBT nbt) {
        if (nbt instanceof NumberNBT) {
            this.data[index] = ((NumberNBT)nbt).getByte();
            return true;
        }
        return false;
    }

    @Override
    public boolean addNBTByIndex(int index, INBT nbt) {
        if (nbt instanceof NumberNBT) {
            this.data = ArrayUtils.add(this.data, index, ((NumberNBT)nbt).getByte());
            return true;
        }
        return false;
    }

    @Override
    public ByteNBT remove(int p_remove_1_) {
        byte b0 = this.data[p_remove_1_];
        this.data = ArrayUtils.remove(this.data, p_remove_1_);
        return ByteNBT.valueOf(b0);
    }

    @Override
    public byte getTagType() {
        return 1;
    }

    @Override
    public void clear() {
        this.data = new byte[0];
    }
}
