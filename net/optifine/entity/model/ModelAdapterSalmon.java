package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.client.renderer.entity.model.SalmonModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterSalmon
extends ModelAdapter {
    public ModelAdapterSalmon() {
        super(EntityType.SALMON, "salmon", 0.3f);
    }

    @Override
    public Model makeModel() {
        return new SalmonModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        ModelRenderer modelrenderer2;
        ModelRenderer modelrenderer1;
        ModelRenderer modelrenderer;
        if (!(model instanceof SalmonModel)) {
            return null;
        }
        SalmonModel salmonmodel = (SalmonModel)model;
        if (modelPart.equals("body_front")) {
            return (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 0);
        }
        if (modelPart.equals("body_back")) {
            return (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 1);
        }
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 2);
        }
        if (modelPart.equals("fin_back_1") && (modelrenderer = (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 0)) != null) {
            return modelrenderer.getChild(0);
        }
        if (modelPart.equals("fin_back_2") && (modelrenderer1 = (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 1)) != null) {
            return modelrenderer1.getChild(1);
        }
        if (modelPart.equals("tail") && (modelrenderer2 = (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 1)) != null) {
            return modelrenderer2.getChild(0);
        }
        if (modelPart.equals("fin_right")) {
            return (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 3);
        }
        return modelPart.equals("fin_left") ? (ModelRenderer)Reflector.ModelSalmon_ModelRenderers.getValue(salmonmodel, 4) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body_front", "body_back", "head", "fin_back_1", "fin_back_2", "tail", "fin_right", "fin_left"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SalmonRenderer salmonrenderer = new SalmonRenderer(entityrenderermanager);
        salmonrenderer.entityModel = (SalmonModel)modelBase;
        salmonrenderer.shadowSize = shadowSize;
        return salmonrenderer;
    }
}
