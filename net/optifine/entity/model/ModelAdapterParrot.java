package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.model.ParrotModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterParrot
extends ModelAdapter {
    public ModelAdapterParrot() {
        super(EntityType.PARROT, "parrot", 0.3f);
    }

    @Override
    public Model makeModel() {
        return new ParrotModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof ParrotModel)) {
            return null;
        }
        ParrotModel parrotmodel = (ParrotModel)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 0);
        }
        if (modelPart.equals("tail")) {
            return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 1);
        }
        if (modelPart.equals("left_wing")) {
            return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 2);
        }
        if (modelPart.equals("right_wing")) {
            return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 3);
        }
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 4);
        }
        if (modelPart.equals("left_leg")) {
            return (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 9);
        }
        return modelPart.equals("right_leg") ? (ModelRenderer)Reflector.getFieldValue(parrotmodel, Reflector.ModelParrot_ModelRenderers, 10) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "tail", "left_wing", "right_wing", "head", "left_leg", "right_leg"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ParrotRenderer parrotrenderer = new ParrotRenderer(entityrenderermanager);
        parrotrenderer.entityModel = (ParrotModel)modelBase;
        parrotrenderer.shadowSize = shadowSize;
        return parrotrenderer;
    }
}
