package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.TridentRenderer;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterTrident
extends ModelAdapter {
    public ModelAdapterTrident() {
        super(EntityType.TRIDENT, "trident", 0.0f);
    }

    @Override
    public Model makeModel() {
        return new TridentModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof TridentModel)) {
            return null;
        }
        TridentModel tridentmodel = (TridentModel)model;
        return modelPart.equals("body") ? (ModelRenderer)Reflector.ModelTrident_tridentRenderer.getValue(tridentmodel) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        TridentRenderer tridentrenderer = new TridentRenderer(entityrenderermanager);
        if (!Reflector.RenderTrident_modelTrident.exists()) {
            Config.warn("Field not found: RenderTrident.modelTrident");
            return null;
        }
        Reflector.setFieldValue(tridentrenderer, Reflector.RenderTrident_modelTrident, modelBase);
        tridentrenderer.shadowSize = shadowSize;
        return tridentrenderer;
    }
}
