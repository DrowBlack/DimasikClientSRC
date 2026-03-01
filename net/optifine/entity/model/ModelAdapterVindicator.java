package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VindicatorRenderer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterIllager;

public class ModelAdapterVindicator
extends ModelAdapterIllager {
    public ModelAdapterVindicator() {
        super(EntityType.VINDICATOR, "vindicator", 0.5f, new String[]{"vindication_illager"});
    }

    @Override
    public Model makeModel() {
        return new IllagerModel(0.0f, 0.0f, 64, 64);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        VindicatorRenderer vindicatorrenderer = new VindicatorRenderer(entityrenderermanager);
        vindicatorrenderer.entityModel = (IllagerModel)modelBase;
        vindicatorrenderer.shadowSize = shadowSize;
        return vindicatorrenderer;
    }
}
