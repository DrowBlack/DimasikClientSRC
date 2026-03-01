package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.model.MinecartModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterMinecart
extends ModelAdapter {
    public ModelAdapterMinecart() {
        super(EntityType.MINECART, "minecart", 0.5f);
    }

    protected ModelAdapterMinecart(EntityType type, String name, float shadow) {
        super(type, name, shadow);
    }

    @Override
    public Model makeModel() {
        return new MinecartModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof MinecartModel)) {
            return null;
        }
        MinecartModel minecartmodel = (MinecartModel)model;
        ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.ModelMinecart_sideModels.getValue(minecartmodel);
        if (amodelrenderer != null) {
            if (modelPart.equals("bottom")) {
                return amodelrenderer[0];
            }
            if (modelPart.equals("back")) {
                return amodelrenderer[1];
            }
            if (modelPart.equals("front")) {
                return amodelrenderer[2];
            }
            if (modelPart.equals("right")) {
                return amodelrenderer[3];
            }
            if (modelPart.equals("left")) {
                return amodelrenderer[4];
            }
            if (modelPart.equals("dirt")) {
                return amodelrenderer[5];
            }
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"bottom", "back", "front", "right", "left", "dirt"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        MinecartRenderer minecartrenderer = new MinecartRenderer(entityrenderermanager);
        if (!Reflector.RenderMinecart_modelMinecart.exists()) {
            Config.warn("Field not found: RenderMinecart.modelMinecart");
            return null;
        }
        Reflector.setFieldValue(minecartrenderer, Reflector.RenderMinecart_modelMinecart, modelBase);
        minecartrenderer.shadowSize = shadowSize;
        return minecartrenderer;
    }
}
