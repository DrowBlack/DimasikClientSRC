package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GiantZombieRenderer;
import net.minecraft.client.renderer.entity.model.GiantModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterZombie;

public class ModelAdapterGiant
extends ModelAdapterZombie {
    public ModelAdapterGiant() {
        super(EntityType.GIANT, "giant", 3.0f);
    }

    @Override
    public Model makeModel() {
        return new GiantModel();
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        GiantZombieRenderer giantzombierenderer = new GiantZombieRenderer(entityrenderermanager, 6.0f);
        giantzombierenderer.entityModel = (GiantModel)modelBase;
        giantzombierenderer.shadowSize = shadowSize;
        return giantzombierenderer;
    }
}
