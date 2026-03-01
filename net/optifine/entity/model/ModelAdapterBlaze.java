package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BlazeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BlazeModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterBlaze
extends ModelAdapter {
    public ModelAdapterBlaze() {
        super(EntityType.BLAZE, "blaze", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new BlazeModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof BlazeModel)) {
            return null;
        }
        BlazeModel blazemodel = (BlazeModel)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.getFieldValue(blazemodel, Reflector.ModelBlaze_blazeHead);
        }
        String s = "stick";
        if (modelPart.startsWith(s)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(blazemodel, Reflector.ModelBlaze_blazeSticks);
            if (amodelrenderer == null) {
                return null;
            }
            String s1 = modelPart.substring(s.length());
            int i = Config.parseInt(s1, -1);
            return --i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "stick1", "stick2", "stick3", "stick4", "stick5", "stick6", "stick7", "stick8", "stick9", "stick10", "stick11", "stick12"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        BlazeRenderer blazerenderer = new BlazeRenderer(entityrenderermanager);
        blazerenderer.entityModel = (BlazeModel)modelBase;
        blazerenderer.shadowSize = shadowSize;
        return blazerenderer;
    }
}
