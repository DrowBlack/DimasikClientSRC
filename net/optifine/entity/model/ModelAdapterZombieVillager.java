package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ZombieVillagerRenderer;
import net.minecraft.client.renderer.entity.model.ZombieVillagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.minecraft.resources.IReloadableResourceManager;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterBiped;

public class ModelAdapterZombieVillager
extends ModelAdapterBiped {
    public ModelAdapterZombieVillager() {
        super(EntityType.ZOMBIE_VILLAGER, "zombie_villager", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new ZombieVillagerModel(0.0f, false);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager)Minecraft.getInstance().getResourceManager();
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ZombieVillagerRenderer zombievillagerrenderer = new ZombieVillagerRenderer(entityrenderermanager, ireloadableresourcemanager);
        zombievillagerrenderer.entityModel = (ZombieVillagerModel)modelBase;
        zombievillagerrenderer.shadowSize = shadowSize;
        return zombievillagerrenderer;
    }
}
