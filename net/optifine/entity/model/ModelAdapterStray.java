package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterBiped;

public class ModelAdapterStray
extends ModelAdapterBiped {
    public ModelAdapterStray() {
        super(EntityType.STRAY, "stray", 0.7f);
    }

    @Override
    public Model makeModel() {
        return new SkeletonModel();
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        StrayRenderer strayrenderer = new StrayRenderer(entityrenderermanager);
        strayrenderer.entityModel = (SkeletonModel)modelBase;
        strayrenderer.shadowSize = shadowSize;
        return strayrenderer;
    }
}
