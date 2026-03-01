package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterBiped;

public class ModelAdapterSkeleton
extends ModelAdapterBiped {
    public ModelAdapterSkeleton() {
        super(EntityType.SKELETON, "skeleton", 0.7f);
    }

    @Override
    public Model makeModel() {
        return new SkeletonModel();
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SkeletonRenderer skeletonrenderer = new SkeletonRenderer(entityrenderermanager);
        skeletonrenderer.entityModel = (SkeletonModel)modelBase;
        skeletonrenderer.shadowSize = shadowSize;
        return skeletonrenderer;
    }
}
