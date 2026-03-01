package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterQuadruped;

public class ModelAdapterPig
extends ModelAdapterQuadruped {
    public ModelAdapterPig() {
        super(EntityType.PIG, "pig", 0.7f);
    }

    @Override
    public Model makeModel() {
        return new PigModel();
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PigRenderer pigrenderer = new PigRenderer(entityrenderermanager);
        pigrenderer.entityModel = (PigModel)modelBase;
        pigrenderer.shadowSize = shadowSize;
        return pigrenderer;
    }
}
