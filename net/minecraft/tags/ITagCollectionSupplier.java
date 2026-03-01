package net.minecraft.tags;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.TagRegistryManager;
import net.minecraft.util.registry.Registry;

public interface ITagCollectionSupplier {
    public static final ITagCollectionSupplier TAG_COLLECTION_SUPPLIER = ITagCollectionSupplier.getTagCollectionSupplier(ITagCollection.getEmptyTagCollection(), ITagCollection.getEmptyTagCollection(), ITagCollection.getEmptyTagCollection(), ITagCollection.getEmptyTagCollection());

    public ITagCollection<Block> getBlockTags();

    public ITagCollection<Item> getItemTags();

    public ITagCollection<Fluid> getFluidTags();

    public ITagCollection<EntityType<?>> getEntityTypeTags();

    default public void updateTags() {
        TagRegistryManager.fetchTags(this);
        Blocks.cacheBlockStates();
    }

    default public void writeTagCollectionSupplierToBuffer(PacketBuffer buffer) {
        this.getBlockTags().writeTagCollectionToBuffer(buffer, Registry.BLOCK);
        this.getItemTags().writeTagCollectionToBuffer(buffer, Registry.ITEM);
        this.getFluidTags().writeTagCollectionToBuffer(buffer, Registry.FLUID);
        this.getEntityTypeTags().writeTagCollectionToBuffer(buffer, Registry.ENTITY_TYPE);
    }

    public static ITagCollectionSupplier readTagCollectionSupplierFromBuffer(PacketBuffer buffer) {
        ITagCollection<Block> itagcollection = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.BLOCK);
        ITagCollection<Item> itagcollection1 = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.ITEM);
        ITagCollection<Fluid> itagcollection2 = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.FLUID);
        ITagCollection<EntityType<?>> itagcollection3 = ITagCollection.readTagCollectionFromBuffer(buffer, Registry.ENTITY_TYPE);
        return ITagCollectionSupplier.getTagCollectionSupplier(itagcollection, itagcollection1, itagcollection2, itagcollection3);
    }

    public static ITagCollectionSupplier getTagCollectionSupplier(final ITagCollection<Block> blockTags, final ITagCollection<Item> itemTags, final ITagCollection<Fluid> fluidTags, final ITagCollection<EntityType<?>> entityTypeTags) {
        return new ITagCollectionSupplier(){

            @Override
            public ITagCollection<Block> getBlockTags() {
                return blockTags;
            }

            @Override
            public ITagCollection<Item> getItemTags() {
                return itemTags;
            }

            @Override
            public ITagCollection<Fluid> getFluidTags() {
                return fluidTags;
            }

            @Override
            public ITagCollection<EntityType<?>> getEntityTypeTags() {
                return entityTypeTags;
            }
        };
    }
}
