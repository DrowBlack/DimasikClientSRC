package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SnowManRenderer;
import net.minecraft.client.renderer.entity.model.SnowManModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterSnowman
extends ModelAdapter {
    public ModelAdapterSnowman() {
        super(EntityType.SNOW_GOLEM, "snow_golem", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new SnowManModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof SnowManModel)) {
            return null;
        }
        SnowManModel snowmanmodel = (SnowManModel)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 0);
        }
        if (modelPart.equals("body_bottom")) {
            return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 1);
        }
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 2);
        }
        if (modelPart.equals("right_hand")) {
            return (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 3);
        }
        return modelPart.equals("left_hand") ? (ModelRenderer)Reflector.ModelSnowman_ModelRenderers.getValue(snowmanmodel, 4) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "body_bottom", "head", "right_hand", "left_hand"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SnowManRenderer snowmanrenderer = new SnowManRenderer(entityrenderermanager);
        snowmanrenderer.entityModel = (SnowManModel)modelBase;
        snowmanrenderer.shadowSize = shadowSize;
        return snowmanrenderer;
    }
}
