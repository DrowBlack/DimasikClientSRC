package net.optifine.entity.model;

import net.minecraft.entity.EntityType;
import net.optifine.entity.model.ModelAdapterMinecart;

public class ModelAdapterMinecartHopper
extends ModelAdapterMinecart {
    public ModelAdapterMinecartHopper() {
        super(EntityType.HOPPER_MINECART, "hopper_minecart", 0.5f);
    }
}
