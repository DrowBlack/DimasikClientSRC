package net.optifine.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BoatModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.entity.model.ModelRendererUtils;
import net.optifine.reflect.Reflector;

public class ModelAdapterBoat
extends ModelAdapter {
    public ModelAdapterBoat() {
        super(EntityType.BOAT, "boat", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new BoatModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof BoatModel)) {
            return null;
        }
        BoatModel boatmodel = (BoatModel)model;
        Iterable immutablelist = boatmodel.getParts();
        if (immutablelist != null) {
            if (modelPart.equals("bottom")) {
                return ModelRendererUtils.getModelRenderer((ImmutableList<ModelRenderer>)immutablelist, 0);
            }
            if (modelPart.equals("back")) {
                return ModelRendererUtils.getModelRenderer((ImmutableList<ModelRenderer>)immutablelist, 1);
            }
            if (modelPart.equals("front")) {
                return ModelRendererUtils.getModelRenderer((ImmutableList<ModelRenderer>)immutablelist, 2);
            }
            if (modelPart.equals("right")) {
                return ModelRendererUtils.getModelRenderer((ImmutableList<ModelRenderer>)immutablelist, 3);
            }
            if (modelPart.equals("left")) {
                return ModelRendererUtils.getModelRenderer((ImmutableList<ModelRenderer>)immutablelist, 4);
            }
            if (modelPart.equals("paddle_left")) {
                return ModelRendererUtils.getModelRenderer((ImmutableList<ModelRenderer>)immutablelist, 5);
            }
            if (modelPart.equals("paddle_right")) {
                return ModelRendererUtils.getModelRenderer((ImmutableList<ModelRenderer>)immutablelist, 6);
            }
        }
        return modelPart.equals("bottom_no_water") ? boatmodel.func_228245_c_() : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"bottom", "back", "front", "right", "left", "paddle_left", "paddle_right", "bottom_no_water"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        BoatRenderer boatrenderer = new BoatRenderer(entityrenderermanager);
        if (!Reflector.RenderBoat_modelBoat.exists()) {
            Config.warn("Field not found: RenderBoat.modelBoat");
            return null;
        }
        Reflector.setFieldValue(boatrenderer, Reflector.RenderBoat_modelBoat, modelBase);
        boatrenderer.shadowSize = shadowSize;
        return boatrenderer;
    }
}
