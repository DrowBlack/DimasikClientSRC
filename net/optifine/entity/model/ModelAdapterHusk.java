package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterBiped;

public class ModelAdapterHusk
extends ModelAdapterBiped {
    public ModelAdapterHusk() {
        super(EntityType.HUSK, "husk", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new ZombieModel(0.0f, false);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        HuskRenderer huskrenderer = new HuskRenderer(entityrenderermanager);
        huskrenderer.entityModel = (ZombieModel)modelBase;
        huskrenderer.shadowSize = shadowSize;
        return huskrenderer;
    }
}
