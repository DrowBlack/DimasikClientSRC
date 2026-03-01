package net.minecraft.world.storage;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class CommandStorage {
    private final Map<String, Container> loadedContainers = Maps.newHashMap();
    private final DimensionSavedDataManager manager;

    public CommandStorage(DimensionSavedDataManager manager) {
        this.manager = manager;
    }

    private Container createContainer(String namespace, String name) {
        Container commandstorage$container = new Container(name);
        this.loadedContainers.put(namespace, commandstorage$container);
        return commandstorage$container;
    }

    public CompoundNBT getData(ResourceLocation id) {
        String s1;
        String s = id.getNamespace();
        Container commandstorage$container = this.manager.get(() -> this.lambda$getData$0(s, s1 = CommandStorage.prefixStorageNamespace(s)), s1);
        return commandstorage$container != null ? commandstorage$container.getData(id.getPath()) : new CompoundNBT();
    }

    public void setData(ResourceLocation id, CompoundNBT nbt) {
        String s = id.getNamespace();
        String s1 = CommandStorage.prefixStorageNamespace(s);
        this.manager.getOrCreate(() -> this.createContainer(s, s1), s1).setData(id.getPath(), nbt);
    }

    public Stream<ResourceLocation> getSavedDataKeys() {
        return this.loadedContainers.entrySet().stream().flatMap(entry -> ((Container)entry.getValue()).getSavedKeys((String)entry.getKey()));
    }

    private static String prefixStorageNamespace(String namespace) {
        return "command_storage_" + namespace;
    }

    private /* synthetic */ Container lambda$getData$0(String s, String s1) {
        return this.createContainer(s, s1);
    }

    static class Container
    extends WorldSavedData {
        private final Map<String, CompoundNBT> contents = Maps.newHashMap();

        public Container(String name) {
            super(name);
        }

        @Override
        public void read(CompoundNBT nbt) {
            CompoundNBT compoundnbt = nbt.getCompound("contents");
            for (String s : compoundnbt.keySet()) {
                this.contents.put(s, compoundnbt.getCompound(s));
            }
        }

        @Override
        public CompoundNBT write(CompoundNBT compound) {
            CompoundNBT compoundnbt = new CompoundNBT();
            this.contents.forEach((id, nbt) -> compoundnbt.put((String)id, nbt.copy()));
            compound.put("contents", compoundnbt);
            return compound;
        }

        public CompoundNBT getData(String id) {
            CompoundNBT compoundnbt = this.contents.get(id);
            return compoundnbt != null ? compoundnbt : new CompoundNBT();
        }

        public void setData(String id, CompoundNBT nbt) {
            if (nbt.isEmpty()) {
                this.contents.remove(id);
            } else {
                this.contents.put(id, nbt);
            }
            this.markDirty();
        }

        public Stream<ResourceLocation> getSavedKeys(String namespace) {
            return this.contents.keySet().stream().map(id -> new ResourceLocation(namespace, (String)id));
        }
    }
}
