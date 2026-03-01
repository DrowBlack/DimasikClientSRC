package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterSlime
extends ModelAdapter {
    public ModelAdapterSlime() {
        super(EntityType.SLIME, "slime", 0.25f);
    }

    @Override
    public Model makeModel() {
        return new SlimeModel(16);
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof SlimeModel)) {
            return null;
        }
        SlimeModel slimemodel = (SlimeModel)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 0);
        }
        if (modelPart.equals("left_eye")) {
            return (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 1);
        }
        if (modelPart.equals("right_eye")) {
            return (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 2);
        }
        return modelPart.equals("mouth") ? (ModelRenderer)Reflector.getFieldValue(slimemodel, Reflector.ModelSlime_ModelRenderers, 3) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "left_eye", "right_eye", "mouth"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SlimeRenderer slimerenderer = new SlimeRenderer(entityrenderermanager);
        slimerenderer.entityModel = (SlimeModel)modelBase;
        slimerenderer.shadowSize = shadowSize;
        return slimerenderer;
    }
}
