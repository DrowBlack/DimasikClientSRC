package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SpiderModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterSpider
extends ModelAdapter {
    public ModelAdapterSpider() {
        super(EntityType.SPIDER, "spider", 1.0f);
    }

    protected ModelAdapterSpider(EntityType type, String name, float shadowSize) {
        super(type, name, shadowSize);
    }

    @Override
    public Model makeModel() {
        return new SpiderModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof SpiderModel)) {
            return null;
        }
        SpiderModel spidermodel = (SpiderModel)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 0);
        }
        if (modelPart.equals("neck")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 1);
        }
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 2);
        }
        if (modelPart.equals("leg1")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 3);
        }
        if (modelPart.equals("leg2")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 4);
        }
        if (modelPart.equals("leg3")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 5);
        }
        if (modelPart.equals("leg4")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 6);
        }
        if (modelPart.equals("leg5")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 7);
        }
        if (modelPart.equals("leg6")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 8);
        }
        if (modelPart.equals("leg7")) {
            return (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 9);
        }
        return modelPart.equals("leg8") ? (ModelRenderer)Reflector.ModelSpider_ModelRenderers.getValue(spidermodel, 10) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "neck", "body", "leg1", "leg2", "leg3", "leg4", "leg5", "leg6", "leg7", "leg8"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SpiderRenderer spiderrenderer = new SpiderRenderer(entityrenderermanager);
        spiderrenderer.entityModel = (EntityModel)modelBase;
        spiderrenderer.shadowSize = shadowSize;
        return spiderrenderer;
    }
}
