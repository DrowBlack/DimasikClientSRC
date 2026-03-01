package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterDragon
extends ModelAdapter {
    public ModelAdapterDragon() {
        super(EntityType.ENDER_DRAGON, "dragon", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new EnderDragonRenderer.EnderDragonModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof EnderDragonRenderer.EnderDragonModel)) {
            return null;
        }
        EnderDragonRenderer.EnderDragonModel enderdragonrenderer$enderdragonmodel = (EnderDragonRenderer.EnderDragonModel)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 0);
        }
        if (modelPart.equals("spine")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 1);
        }
        if (modelPart.equals("jaw")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 2);
        }
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 3);
        }
        if (modelPart.equals("left_wing")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 4);
        }
        if (modelPart.equals("left_wing_tip")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 5);
        }
        if (modelPart.equals("front_left_leg")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 6);
        }
        if (modelPart.equals("front_left_shin")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 7);
        }
        if (modelPart.equals("front_left_foot")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 8);
        }
        if (modelPart.equals("back_left_leg")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 9);
        }
        if (modelPart.equals("back_left_shin")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 10);
        }
        if (modelPart.equals("back_left_foot")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 11);
        }
        if (modelPart.equals("right_wing")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 12);
        }
        if (modelPart.equals("right_wing_tip")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 13);
        }
        if (modelPart.equals("front_right_leg")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 14);
        }
        if (modelPart.equals("front_right_shin")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 15);
        }
        if (modelPart.equals("front_right_foot")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 16);
        }
        if (modelPart.equals("back_right_leg")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 17);
        }
        if (modelPart.equals("back_right_shin")) {
            return (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 18);
        }
        return modelPart.equals("back_right_foot") ? (ModelRenderer)Reflector.getFieldValue(enderdragonrenderer$enderdragonmodel, Reflector.ModelDragon_ModelRenderers, 19) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "spine", "jaw", "body", "left_wing", "left_wing_tip", "front_left_leg", "front_left_shin", "front_left_foot", "back_left_leg", "back_left_shin", "back_left_foot", "right_wing", "right_wing_tip", "front_right_leg", "front_right_shin", "front_right_foot", "back_right_leg", "back_right_shin", "back_right_foot"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EnderDragonRenderer enderdragonrenderer = new EnderDragonRenderer(entityrenderermanager);
        if (!Reflector.EnderDragonRenderer_model.exists()) {
            Config.warn("Field not found: EnderDragonRenderer.model");
            return null;
        }
        Reflector.setFieldValue(enderdragonrenderer, Reflector.EnderDragonRenderer_model, modelBase);
        enderdragonrenderer.shadowSize = shadowSize;
        return enderdragonrenderer;
    }
}
