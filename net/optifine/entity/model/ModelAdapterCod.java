package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.CodRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.CodModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterCod
extends ModelAdapter {
    public ModelAdapterCod() {
        super(EntityType.COD, "cod", 0.3f);
    }

    @Override
    public Model makeModel() {
        return new CodModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof CodModel)) {
            return null;
        }
        CodModel codmodel = (CodModel)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 0);
        }
        if (modelPart.equals("fin_back")) {
            return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 1);
        }
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 2);
        }
        if (modelPart.equals("nose")) {
            return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 3);
        }
        if (modelPart.equals("fin_right")) {
            return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 4);
        }
        if (modelPart.equals("fin_left")) {
            return (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 5);
        }
        return modelPart.equals("tail") ? (ModelRenderer)Reflector.ModelCod_ModelRenderers.getValue(codmodel, 6) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "fin_back", "head", "nose", "fin_right", "fin_left", "tail"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        CodRenderer codrenderer = new CodRenderer(entityrenderermanager);
        codrenderer.entityModel = (CodModel)modelBase;
        codrenderer.shadowSize = shadowSize;
        return codrenderer;
    }
}
