package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MooshroomRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterQuadruped;

public class ModelAdapterMooshroom
extends ModelAdapterQuadruped {
    public ModelAdapterMooshroom() {
        super(EntityType.MOOSHROOM, "mooshroom", 0.7f);
    }

    @Override
    public Model makeModel() {
        return new CowModel();
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        MooshroomRenderer mooshroomrenderer = new MooshroomRenderer(entityrenderermanager);
        mooshroomrenderer.entityModel = (CowModel)modelBase;
        mooshroomrenderer.shadowSize = shadowSize;
        return mooshroomrenderer;
    }
}
