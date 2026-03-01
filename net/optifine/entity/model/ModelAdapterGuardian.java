package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.GuardianRenderer;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterGuardian
extends ModelAdapter {
    public ModelAdapterGuardian() {
        super(EntityType.GUARDIAN, "guardian", 0.5f);
    }

    public ModelAdapterGuardian(EntityType entityType, String name, float shadowSize) {
        super(entityType, name, shadowSize);
    }

    @Override
    public Model makeModel() {
        return new GuardianModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof GuardianModel)) {
            return null;
        }
        GuardianModel guardianmodel = (GuardianModel)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_body);
        }
        if (modelPart.equals("eye")) {
            return (ModelRenderer)Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_eye);
        }
        String s = "spine";
        if (modelPart.startsWith(s)) {
            ModelRenderer[] amodelrenderer1 = (ModelRenderer[])Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_spines);
            if (amodelrenderer1 == null) {
                return null;
            }
            String s3 = modelPart.substring(s.length());
            int j = Config.parseInt(s3, -1);
            return --j >= 0 && j < amodelrenderer1.length ? amodelrenderer1[j] : null;
        }
        String s1 = "tail";
        if (modelPart.startsWith(s1)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(guardianmodel, Reflector.ModelGuardian_tail);
            if (amodelrenderer == null) {
                return null;
            }
            String s2 = modelPart.substring(s1.length());
            int i = Config.parseInt(s2, -1);
            return --i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "eye", "spine1", "spine2", "spine3", "spine4", "spine5", "spine6", "spine7", "spine8", "spine9", "spine10", "spine11", "spine12", "tail1", "tail2", "tail3"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        GuardianRenderer guardianrenderer = new GuardianRenderer(entityrenderermanager);
        guardianrenderer.entityModel = (GuardianModel)modelBase;
        guardianrenderer.shadowSize = shadowSize;
        return guardianrenderer;
    }
}
