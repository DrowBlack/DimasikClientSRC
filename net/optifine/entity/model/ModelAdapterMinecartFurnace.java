package net.optifine.entity.model;

import net.minecraft.entity.EntityType;
import net.optifine.entity.model.ModelAdapterMinecart;

public class ModelAdapterMinecartFurnace
extends ModelAdapterMinecart {
    public ModelAdapterMinecartFurnace() {
        super(EntityType.FURNACE_MINECART, "furnace_minecart", 0.5f);
    }
}
