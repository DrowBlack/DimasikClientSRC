package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.resources.IReloadableResourceManager;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterVillager
extends ModelAdapter {
    public ModelAdapterVillager() {
        super(EntityType.VILLAGER, "villager", 0.5f);
    }

    protected ModelAdapterVillager(EntityType type, String name, float shadowSize) {
        super(type, name, shadowSize);
    }

    @Override
    public Model makeModel() {
        return new VillagerModel(0.0f);
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof VillagerModel)) {
            return null;
        }
        VillagerModel villagermodel = (VillagerModel)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 0);
        }
        if (modelPart.equals("headwear")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 1);
        }
        if (modelPart.equals("headwear2")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 2);
        }
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 3);
        }
        if (modelPart.equals("bodywear")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 4);
        }
        if (modelPart.equals("arms")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 5);
        }
        if (modelPart.equals("right_leg")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 6);
        }
        if (modelPart.equals("left_leg")) {
            return (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 7);
        }
        return modelPart.equals("nose") ? (ModelRenderer)Reflector.ModelVillager_ModelRenderers.getValue(villagermodel, 8) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "headwear", "headwear2", "body", "bodywear", "arms", "right_leg", "left_leg", "nose"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        IReloadableResourceManager ireloadableresourcemanager = (IReloadableResourceManager)Minecraft.getInstance().getResourceManager();
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        VillagerRenderer villagerrenderer = new VillagerRenderer(entityrenderermanager, ireloadableresourcemanager);
        villagerrenderer.entityModel = (VillagerModel)modelBase;
        villagerrenderer.shadowSize = shadowSize;
        return villagerrenderer;
    }
}
