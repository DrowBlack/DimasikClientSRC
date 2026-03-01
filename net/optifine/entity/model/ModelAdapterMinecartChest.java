package net.optifine.entity.model;

import net.minecraft.entity.EntityType;
import net.optifine.entity.model.ModelAdapterMinecart;

public class ModelAdapterMinecartChest
extends ModelAdapterMinecart {
    public ModelAdapterMinecartChest() {
        super(EntityType.CHEST_MINECART, "chest_minecart", 0.5f);
    }
}
