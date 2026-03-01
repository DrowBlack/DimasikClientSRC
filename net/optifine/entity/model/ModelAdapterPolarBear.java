package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PolarBearRenderer;
import net.minecraft.client.renderer.entity.model.PolarBearModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterQuadruped;

public class ModelAdapterPolarBear
extends ModelAdapterQuadruped {
    public ModelAdapterPolarBear() {
        super(EntityType.POLAR_BEAR, "polar_bear", 0.7f);
    }

    @Override
    public Model makeModel() {
        return new PolarBearModel();
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PolarBearRenderer polarbearrenderer = new PolarBearRenderer(entityrenderermanager);
        polarbearrenderer.entityModel = (PolarBearModel)modelBase;
        polarbearrenderer.shadowSize = shadowSize;
        return polarbearrenderer;
    }
}
