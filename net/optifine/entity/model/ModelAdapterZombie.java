package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.entity.model.ZombieModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterBiped;

public class ModelAdapterZombie
extends ModelAdapterBiped {
    public ModelAdapterZombie() {
        super(EntityType.ZOMBIE, "zombie", 0.5f);
    }

    protected ModelAdapterZombie(EntityType type, String name, float shadowSize) {
        super(type, name, shadowSize);
    }

    @Override
    public Model makeModel() {
        return new ZombieModel(0.0f, false);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ZombieRenderer zombierenderer = new ZombieRenderer(entityrenderermanager);
        zombierenderer.entityModel = (ZombieModel)modelBase;
        zombierenderer.shadowSize = shadowSize;
        return zombierenderer;
    }
}
