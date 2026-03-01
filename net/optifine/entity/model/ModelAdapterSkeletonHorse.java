package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterHorse;

public class ModelAdapterSkeletonHorse
extends ModelAdapterHorse {
    public ModelAdapterSkeletonHorse() {
        super(EntityType.SKELETON_HORSE, "skeleton_horse", 0.75f);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        UndeadHorseRenderer undeadhorserenderer = new UndeadHorseRenderer(entityrenderermanager);
        undeadhorserenderer.entityModel = (HorseModel)modelBase;
        undeadhorserenderer.shadowSize = shadowSize;
        return undeadhorserenderer;
    }
}
