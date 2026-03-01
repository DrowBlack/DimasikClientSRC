package net.optifine.entity.model;

import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.Either;

public interface IEntityRenderer {
    public Either<EntityType, TileEntityType> getType();

    public void setType(Either<EntityType, TileEntityType> var1);

    public ResourceLocation getLocationTextureCustom();

    public void setLocationTextureCustom(ResourceLocation var1);
}
