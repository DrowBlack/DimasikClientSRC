package net.minecraft.tags;

import java.util.stream.Collectors;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.ItemTags;

public class TagCollectionManager {
    private static volatile ITagCollectionSupplier manager = ITagCollectionSupplier.getTagCollectionSupplier(ITagCollection.getTagCollectionFromMap(BlockTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, blockTag -> blockTag))), ITagCollection.getTagCollectionFromMap(ItemTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, itemTag -> itemTag))), ITagCollection.getTagCollectionFromMap(FluidTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, fluidTag -> fluidTag))), ITagCollection.getTagCollectionFromMap(EntityTypeTags.getAllTags().stream().collect(Collectors.toMap(ITag.INamedTag::getName, entityTypeTag -> entityTypeTag))));

    public static ITagCollectionSupplier getManager() {
        return manager;
    }

    public static void setManager(ITagCollectionSupplier managerIn) {
        manager = managerIn;
    }
}
