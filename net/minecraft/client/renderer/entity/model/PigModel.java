package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.entity.Entity;

public class PigModel<T extends Entity>
extends QuadrupedModel<T> {
    public PigModel() {
        this(0.0f);
    }

    public PigModel(float scale) {
        super(6, scale, false, 4.0f, 4.0f, 2.0f, 2.0f, 24);
        this.headModel.setTextureOffset(16, 16).addBox(-2.0f, 0.0f, -9.0f, 4.0f, 3.0f, 1.0f, scale);
    }
}
