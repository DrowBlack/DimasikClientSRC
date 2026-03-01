package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EvokerFangsRenderer;
import net.minecraft.client.renderer.entity.model.EvokerFangsModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterEvokerFangs
extends ModelAdapter {
    public ModelAdapterEvokerFangs() {
        super(EntityType.EVOKER_FANGS, "evoker_fangs", 0.0f, new String[]{"evocation_fangs"});
    }

    @Override
    public Model makeModel() {
        return new EvokerFangsModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof EvokerFangsModel)) {
            return null;
        }
        EvokerFangsModel evokerfangsmodel = (EvokerFangsModel)model;
        if (modelPart.equals("base")) {
            return (ModelRenderer)Reflector.getFieldValue(evokerfangsmodel, Reflector.ModelEvokerFangs_ModelRenderers, 0);
        }
        if (modelPart.equals("upper_jaw")) {
            return (ModelRenderer)Reflector.getFieldValue(evokerfangsmodel, Reflector.ModelEvokerFangs_ModelRenderers, 1);
        }
        return modelPart.equals("lower_jaw") ? (ModelRenderer)Reflector.getFieldValue(evokerfangsmodel, Reflector.ModelEvokerFangs_ModelRenderers, 2) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"base", "upper_jaw", "lower_jaw"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        EvokerFangsRenderer evokerfangsrenderer = new EvokerFangsRenderer(entityrenderermanager);
        if (!Reflector.RenderEvokerFangs_model.exists()) {
            Config.warn("Field not found: RenderEvokerFangs.model");
            return null;
        }
        Reflector.setFieldValue(evokerfangsrenderer, Reflector.RenderEvokerFangs_model, modelBase);
        evokerfangsrenderer.shadowSize = shadowSize;
        return evokerfangsrenderer;
    }
}
