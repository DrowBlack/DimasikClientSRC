package net.minecraft.world.storage;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

public class MapIdTracker
extends WorldSavedData {
    private final Object2IntMap<String> usedIds = new Object2IntOpenHashMap<String>();

    public MapIdTracker() {
        super("idcounts");
        this.usedIds.defaultReturnValue(-1);
    }

    @Override
    public void read(CompoundNBT nbt) {
        this.usedIds.clear();
        for (String s : nbt.keySet()) {
            if (!nbt.contains(s, 99)) continue;
            this.usedIds.put(s, nbt.getInt(s));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        for (Object2IntMap.Entry entry : this.usedIds.object2IntEntrySet()) {
            compound.putInt((String)entry.getKey(), entry.getIntValue());
        }
        return compound;
    }

    public int getNextId() {
        int i = this.usedIds.getInt("map") + 1;
        this.usedIds.put("map", i);
        this.markDirty();
        return i;
    }
}
