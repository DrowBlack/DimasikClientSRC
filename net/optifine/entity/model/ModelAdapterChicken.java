package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.ChickenModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterChicken
extends ModelAdapter {
    public ModelAdapterChicken() {
        super(EntityType.CHICKEN, "chicken", 0.3f);
    }

    @Override
    public Model makeModel() {
        return new ChickenModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof ChickenModel)) {
            return null;
        }
        ChickenModel chickenmodel = (ChickenModel)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 0);
        }
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 1);
        }
        if (modelPart.equals("right_leg")) {
            return (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 2);
        }
        if (modelPart.equals("left_leg")) {
            return (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 3);
        }
        if (modelPart.equals("right_wing")) {
            return (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 4);
        }
        if (modelPart.equals("left_wing")) {
            return (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 5);
        }
        if (modelPart.equals("bill")) {
            return (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 6);
        }
        return modelPart.equals("chin") ? (ModelRenderer)Reflector.ModelChicken_ModelRenderers.getValue(chickenmodel, 7) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "body", "right_leg", "left_leg", "right_wing", "left_wing", "bill", "chin"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ChickenRenderer chickenrenderer = new ChickenRenderer(entityrenderermanager);
        chickenrenderer.entityModel = (ChickenModel)modelBase;
        chickenrenderer.shadowSize = shadowSize;
        return chickenrenderer;
    }
}
