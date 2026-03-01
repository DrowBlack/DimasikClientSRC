package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.HoglinRenderer;
import net.minecraft.client.renderer.entity.model.BoarModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterHoglin
extends ModelAdapter {
    private static Map<String, Integer> mapParts = ModelAdapterHoglin.makeMapParts();

    public ModelAdapterHoglin() {
        super(EntityType.HOGLIN, "hoglin", 0.7f);
    }

    public ModelAdapterHoglin(EntityType entityType, String name, float shadowSize) {
        super(entityType, name, shadowSize);
    }

    @Override
    public Model makeModel() {
        return new BoarModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof BoarModel)) {
            return null;
        }
        BoarModel boarmodel = (BoarModel)model;
        if (mapParts.containsKey(modelPart)) {
            int i = mapParts.get(modelPart);
            return (ModelRenderer)Reflector.getFieldValue(boarmodel, Reflector.ModelBoar_ModelRenderers, i);
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return mapParts.keySet().toArray(new String[0]);
    }

    private static Map<String, Integer> makeMapParts() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("head", 0);
        map.put("right_ear", 1);
        map.put("left_ear", 2);
        map.put("body", 3);
        map.put("front_right_leg", 4);
        map.put("front_left_leg", 5);
        map.put("back_right_leg", 6);
        map.put("back_left_leg", 7);
        map.put("mane", 8);
        return map;
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        HoglinRenderer hoglinrenderer = new HoglinRenderer(entityrenderermanager);
        hoglinrenderer.entityModel = (BoarModel)modelBase;
        hoglinrenderer.shadowSize = shadowSize;
        return hoglinrenderer;
    }
}
