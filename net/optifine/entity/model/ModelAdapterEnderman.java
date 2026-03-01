package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EndermanModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterBiped;

public class ModelAdapterEnderman
extends ModelAdapterBiped {
    public ModelAdapterEnderman() {
        super(EntityType.ENDERMAN, "enderman", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new EndermanModel(0.0f);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EndermanRenderer endermanrenderer = new EndermanRenderer(entityrenderermanager);
        endermanrenderer.entityModel = (EndermanModel)modelBase;
        endermanrenderer.shadowSize = shadowSize;
        return endermanrenderer;
    }
}
