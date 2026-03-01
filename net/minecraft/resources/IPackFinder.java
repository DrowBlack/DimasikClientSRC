package net.minecraft.resources;

import java.util.function.Consumer;
import net.minecraft.resources.ResourcePackInfo;

public interface IPackFinder {
    public void findPacks(Consumer<ResourcePackInfo> var1, ResourcePackInfo.IFactory var2);
}
