package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public abstract class ModelAdapterQuadruped
extends ModelAdapter {
    public ModelAdapterQuadruped(EntityType type, String name, float shadowSize) {
        super(type, name, shadowSize);
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof QuadrupedModel)) {
            return null;
        }
        QuadrupedModel quadrupedmodel = (QuadrupedModel)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelQuadruped_ModelRenderers.getValue(quadrupedmodel, 0);
        }
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelQuadruped_ModelRenderers.getValue(quadrupedmodel, 1);
        }
        if (modelPart.equals("leg1")) {
            return (ModelRenderer)Reflector.ModelQuadruped_ModelRenderers.getValue(quadrupedmodel, 2);
        }
        if (modelPart.equals("leg2")) {
            return (ModelRenderer)Reflector.ModelQuadruped_ModelRenderers.getValue(quadrupedmodel, 3);
        }
        if (modelPart.equals("leg3")) {
            return (ModelRenderer)Reflector.ModelQuadruped_ModelRenderers.getValue(quadrupedmodel, 4);
        }
        return modelPart.equals("leg4") ? (ModelRenderer)Reflector.ModelQuadruped_ModelRenderers.getValue(quadrupedmodel, 5) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "body", "leg1", "leg2", "leg3", "leg4"};
    }
}
