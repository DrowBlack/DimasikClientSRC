package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.FoxRenderer;
import net.minecraft.client.renderer.entity.model.FoxModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterFox
extends ModelAdapter {
    private static Map<String, Integer> mapPartFields = null;

    public ModelAdapterFox() {
        super(EntityType.FOX, "fox", 0.4f);
    }

    @Override
    public Model makeModel() {
        return new FoxModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof FoxModel)) {
            return null;
        }
        FoxModel foxmodel = (FoxModel)model;
        Map<String, Integer> map = ModelAdapterFox.getMapPartFields();
        if (map.containsKey(modelPart)) {
            int i = map.get(modelPart);
            return (ModelRenderer)Reflector.getFieldValue(foxmodel, Reflector.ModelFox_ModelRenderers, i);
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return ModelAdapterFox.getMapPartFields().keySet().toArray(new String[0]);
    }

    private static Map<String, Integer> getMapPartFields() {
        if (mapPartFields != null) {
            return mapPartFields;
        }
        mapPartFields = new HashMap<String, Integer>();
        mapPartFields.put("head", 0);
        mapPartFields.put("body", 4);
        mapPartFields.put("leg1", 5);
        mapPartFields.put("leg2", 6);
        mapPartFields.put("leg3", 7);
        mapPartFields.put("leg4", 8);
        mapPartFields.put("tail", 9);
        return mapPartFields;
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        FoxRenderer foxrenderer = new FoxRenderer(entityrenderermanager);
        foxrenderer.entityModel = (FoxModel)modelBase;
        foxrenderer.shadowSize = shadowSize;
        return foxrenderer;
    }
}
