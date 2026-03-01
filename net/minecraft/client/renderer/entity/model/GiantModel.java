package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.AbstractZombieModel;
import net.minecraft.entity.monster.GiantEntity;

public class GiantModel
extends AbstractZombieModel<GiantEntity> {
    public GiantModel() {
        this(0.0f, false);
    }

    public GiantModel(float modelSize, boolean p_i51066_2_) {
        super(modelSize, 0.0f, 64, p_i51066_2_ ? 32 : 64);
    }

    @Override
    public boolean isAggressive(GiantEntity entityIn) {
        return false;
    }
}
