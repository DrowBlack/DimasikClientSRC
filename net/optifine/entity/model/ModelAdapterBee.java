package net.optifine.entity.model;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BeeModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterBee
extends ModelAdapter {
    private static Map<String, Integer> mapParts = ModelAdapterBee.makeMapParts();

    public ModelAdapterBee() {
        super(EntityType.BEE, "bee", 0.4f);
    }

    @Override
    public Model makeModel() {
        return new BeeModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof BeeModel)) {
            return null;
        }
        BeeModel beemodel = (BeeModel)model;
        if (mapParts.containsKey(modelPart)) {
            int i = mapParts.get(modelPart);
            return (ModelRenderer)Reflector.getFieldValue(beemodel, Reflector.ModelBee_ModelRenderers, i);
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return mapParts.keySet().toArray(new String[0]);
    }

    private static Map<String, Integer> makeMapParts() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put("body", 0);
        map.put("torso", 1);
        map.put("right_wing", 2);
        map.put("left_wing", 3);
        map.put("front_legs", 4);
        map.put("middle_legs", 5);
        map.put("back_legs", 6);
        map.put("stinger", 7);
        map.put("left_antenna", 8);
        map.put("right_antenna", 9);
        return map;
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        BeeRenderer beerenderer = new BeeRenderer(entityrenderermanager);
        beerenderer.entityModel = (BeeModel)modelBase;
        beerenderer.shadowSize = shadowSize;
        return beerenderer;
    }
}
