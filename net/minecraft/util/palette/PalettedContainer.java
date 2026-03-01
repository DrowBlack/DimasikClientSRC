package net.minecraft.util.palette;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.palette.ArrayPalette;
import net.minecraft.util.palette.HashMapPalette;
import net.minecraft.util.palette.IPalette;
import net.minecraft.util.palette.IResizeCallback;

public class PalettedContainer<T>
implements IResizeCallback<T> {
    private final IPalette<T> registryPalette;
    private final IResizeCallback<T> dummyPaletteResize = (p_205517_0_, p_205517_1_) -> 0;
    private final ObjectIntIdentityMap<T> registry;
    private final Function<CompoundNBT, T> deserializer;
    private final Function<T, CompoundNBT> serializer;
    private final T defaultState;
    protected BitArray storage;
    private IPalette<T> palette;
    private int bits;
    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        if (this.lock.isLocked() && !this.lock.isHeldByCurrentThread()) {
            String s = Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map(p_210458_0_ -> p_210458_0_.getName() + ": \n\tat " + Arrays.stream(p_210458_0_.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "))).collect(Collectors.joining("\n"));
            CrashReport crashreport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Thread dumps");
            crashreportcategory.addDetail("Thread dumps", s);
            throw new ReportedException(crashreport);
        }
        this.lock.lock();
    }

    public void unlock() {
        this.lock.unlock();
    }

    public PalettedContainer(IPalette<T> globalPaletteIn, ObjectIntIdentityMap<T> registryIn, Function<CompoundNBT, T> deserializerIn, Function<T, CompoundNBT> serializerIn, T defaultStateIn) {
        this.registryPalette = globalPaletteIn;
        this.registry = registryIn;
        this.deserializer = deserializerIn;
        this.serializer = serializerIn;
        this.defaultState = defaultStateIn;
        this.setBits(4);
    }

    private static int getIndex(int x, int y, int z) {
        return y << 8 | z << 4 | x;
    }

    private void setBits(int bitsIn) {
        if (bitsIn != this.bits) {
            this.bits = bitsIn;
            if (this.bits <= 4) {
                this.bits = 4;
                this.palette = new ArrayPalette<T>(this.registry, this.bits, this, this.deserializer);
            } else if (this.bits < 9) {
                this.palette = new HashMapPalette<T>(this.registry, this.bits, this, this.deserializer, this.serializer);
            } else {
                this.palette = this.registryPalette;
                this.bits = MathHelper.log2DeBruijn(this.registry.size());
            }
            this.palette.idFor(this.defaultState);
            this.storage = new BitArray(this.bits, 4096);
        }
    }

    @Override
    public int onResize(int p_onResize_1_, T p_onResize_2_) {
        this.lock();
        BitArray bitarray = this.storage;
        IPalette<T> ipalette = this.palette;
        this.setBits(p_onResize_1_);
        for (int i = 0; i < bitarray.size(); ++i) {
            T t = ipalette.get(bitarray.getAt(i));
            if (t == null) continue;
            this.set(i, t);
        }
        int j = this.palette.idFor(p_onResize_2_);
        this.unlock();
        return j;
    }

    public T lockedSwap(int x, int y, int z, T state) {
        this.lock();
        T t = this.doSwap(PalettedContainer.getIndex(x, y, z), state);
        this.unlock();
        return t;
    }

    public T swap(int x, int y, int z, T state) {
        return this.doSwap(PalettedContainer.getIndex(x, y, z), state);
    }

    protected T doSwap(int index, T state) {
        int i = this.palette.idFor(state);
        int j = this.storage.swapAt(index, i);
        T t = this.palette.get(j);
        return t == null ? this.defaultState : t;
    }

    protected void set(int index, T state) {
        int i = this.palette.idFor(state);
        this.storage.setAt(index, i);
    }

    public T get(int x, int y, int z) {
        return this.get(PalettedContainer.getIndex(x, y, z));
    }

    protected T get(int index) {
        T t = this.palette.get(this.storage.getAt(index));
        return t == null ? this.defaultState : t;
    }

    public void read(PacketBuffer buf) {
        this.lock();
        byte i = buf.readByte();
        if (this.bits != i) {
            this.setBits(i);
        }
        this.palette.read(buf);
        buf.readLongArray(this.storage.getBackingLongArray());
        this.unlock();
    }

    public void write(PacketBuffer buf) {
        this.lock();
        buf.writeByte(this.bits);
        this.palette.write(buf);
        buf.writeLongArray(this.storage.getBackingLongArray());
        this.unlock();
    }

    public void readChunkPalette(ListNBT paletteNbt, long[] data) {
        this.lock();
        int i = Math.max(4, MathHelper.log2DeBruijn(paletteNbt.size()));
        if (i != this.bits) {
            this.setBits(i);
        }
        this.palette.read(paletteNbt);
        int j = data.length * 64 / 4096;
        if (this.palette == this.registryPalette) {
            HashMapPalette<T> ipalette = new HashMapPalette<T>(this.registry, i, this.dummyPaletteResize, this.deserializer, this.serializer);
            ipalette.read(paletteNbt);
            BitArray bitarray = new BitArray(i, 4096, data);
            for (int k = 0; k < 4096; ++k) {
                this.storage.setAt(k, this.registryPalette.idFor(ipalette.get(bitarray.getAt(k))));
            }
        } else if (j == this.bits) {
            System.arraycopy(data, 0, this.storage.getBackingLongArray(), 0, data.length);
        } else {
            BitArray bitarray1 = new BitArray(j, 4096, data);
            for (int l = 0; l < 4096; ++l) {
                this.storage.setAt(l, bitarray1.getAt(l));
            }
        }
        this.unlock();
    }

    public void writeChunkPalette(CompoundNBT compound, String paletteName, String paletteDataName) {
        this.lock();
        HashMapPalette<T> hashmappalette = new HashMapPalette<T>(this.registry, this.bits, this.dummyPaletteResize, this.deserializer, this.serializer);
        T t = this.defaultState;
        int i = hashmappalette.idFor(this.defaultState);
        int[] aint = new int[4096];
        for (int j = 0; j < 4096; ++j) {
            T t1 = this.get(j);
            if (t1 != t) {
                t = t1;
                i = hashmappalette.idFor(t1);
            }
            aint[j] = i;
        }
        ListNBT listnbt = new ListNBT();
        hashmappalette.writePaletteToList(listnbt);
        compound.put(paletteName, listnbt);
        int l = Math.max(4, MathHelper.log2DeBruijn(listnbt.size()));
        BitArray bitarray = new BitArray(l, 4096);
        for (int k = 0; k < aint.length; ++k) {
            bitarray.setAt(k, aint[k]);
        }
        compound.putLongArray(paletteDataName, bitarray.getBackingLongArray());
        this.unlock();
    }

    public int getSerializedSize() {
        return 1 + this.palette.getSerializedSize() + PacketBuffer.getVarIntSize(this.storage.size()) + this.storage.getBackingLongArray().length * 8;
    }

    public boolean func_235963_a_(Predicate<T> p_235963_1_) {
        return this.palette.func_230341_a_(p_235963_1_);
    }

    public void count(ICountConsumer<T> countConsumerIn) {
        Int2IntOpenHashMap int2intmap = new Int2IntOpenHashMap();
        this.storage.getAll(p_225498_1_ -> int2intmap.put(p_225498_1_, int2intmap.get(p_225498_1_) + 1));
        int2intmap.int2IntEntrySet().forEach(p_225499_2_ -> countConsumerIn.accept(this.palette.get(p_225499_2_.getIntKey()), p_225499_2_.getIntValue()));
    }

    @FunctionalInterface
    public static interface ICountConsumer<T> {
        public void accept(T var1, int var2);
    }
}
