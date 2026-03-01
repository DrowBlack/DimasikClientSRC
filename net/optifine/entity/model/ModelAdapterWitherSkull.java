package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.WitherSkullRenderer;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterWitherSkull
extends ModelAdapter {
    public ModelAdapterWitherSkull() {
        super(EntityType.WITHER_SKULL, "wither_skull", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new GenericHeadModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof GenericHeadModel)) {
            return null;
        }
        GenericHeadModel genericheadmodel = (GenericHeadModel)model;
        return modelPart.equals("head") ? (ModelRenderer)Reflector.ModelGenericHead_skeletonHead.getValue(genericheadmodel) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        WitherSkullRenderer witherskullrenderer = new WitherSkullRenderer(entityrenderermanager);
        if (!Reflector.RenderWitherSkull_model.exists()) {
            Config.warn("Field not found: RenderWitherSkull_model");
            return null;
        }
        Reflector.setFieldValue(witherskullrenderer, Reflector.RenderWitherSkull_model, modelBase);
        witherskullrenderer.shadowSize = shadowSize;
        return witherskullrenderer;
    }
}
