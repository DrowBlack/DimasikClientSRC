package net.minecraft.resources;

import java.util.function.Consumer;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.VanillaPack;

public class ServerPackFinder
implements IPackFinder {
    private final VanillaPack field_195738_a = new VanillaPack("minecraft");

    @Override
    public void findPacks(Consumer<ResourcePackInfo> infoConsumer, ResourcePackInfo.IFactory infoFactory) {
        ResourcePackInfo resourcepackinfo = ResourcePackInfo.createResourcePack("vanilla", false, () -> this.field_195738_a, infoFactory, ResourcePackInfo.Priority.BOTTOM, IPackNameDecorator.BUILTIN);
        if (resourcepackinfo != null) {
            infoConsumer.accept(resourcepackinfo);
        }
    }
}
