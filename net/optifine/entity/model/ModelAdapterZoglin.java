package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZoglinRenderer;
import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterHoglin;

public class ModelAdapterZoglin
extends ModelAdapterHoglin {
    public ModelAdapterZoglin() {
        super(EntityType.ZOGLIN, "zoglin", 0.7f);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ZoglinRenderer zoglinrenderer = new ZoglinRenderer(entityrenderermanager);
        zoglinrenderer.entityModel = (BoarModel)modelBase;
        zoglinrenderer.shadowSize = shadowSize;
        return zoglinrenderer;
    }
}
