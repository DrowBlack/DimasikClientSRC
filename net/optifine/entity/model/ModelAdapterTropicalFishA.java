package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TropicalFishRenderer;
import net.minecraft.client.renderer.entity.model.TropicalFishAModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterTropicalFishA
extends ModelAdapter {
    public ModelAdapterTropicalFishA() {
        super(EntityType.TROPICAL_FISH, "tropical_fish_a", 0.2f);
    }

    @Override
    public Model makeModel() {
        return new TropicalFishAModel(0.0f);
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof TropicalFishAModel)) {
            return null;
        }
        TropicalFishAModel tropicalfishamodel = (TropicalFishAModel)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 0);
        }
        if (modelPart.equals("tail")) {
            return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 1);
        }
        if (modelPart.equals("fin_right")) {
            return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 2);
        }
        if (modelPart.equals("fin_left")) {
            return (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 3);
        }
        return modelPart.equals("fin_top") ? (ModelRenderer)Reflector.ModelTropicalFishA_ModelRenderers.getValue(tropicalfishamodel, 4) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "tail", "fin_right", "fin_left", "fin_top"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EntityRenderer entityrenderer = entityrenderermanager.getEntityRenderMap().get(EntityType.TROPICAL_FISH);
        if (!(entityrenderer instanceof TropicalFishRenderer)) {
            Config.warn("Not a TropicalFishRenderer: " + String.valueOf(entityrenderer));
            return null;
        }
        if (entityrenderer.getType() == null) {
            TropicalFishRenderer tropicalfishrenderer = new TropicalFishRenderer(entityrenderermanager);
            tropicalfishrenderer.shadowSize = shadowSize;
            entityrenderer = tropicalfishrenderer;
        }
        TropicalFishRenderer tropicalfishrenderer1 = (TropicalFishRenderer)entityrenderer;
        if (!Reflector.RenderTropicalFish_modelA.exists()) {
            Config.warn("Model field not found: RenderTropicalFish.modelA");
            return null;
        }
        Reflector.RenderTropicalFish_modelA.setValue(tropicalfishrenderer1, modelBase);
        return tropicalfishrenderer1;
    }
}
