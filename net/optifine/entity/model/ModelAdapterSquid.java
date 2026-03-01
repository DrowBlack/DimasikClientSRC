package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SquidRenderer;
import net.minecraft.client.renderer.entity.model.SquidModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public class ModelAdapterSquid
extends ModelAdapter {
    public ModelAdapterSquid() {
        super(EntityType.SQUID, "squid", 0.7f);
    }

    @Override
    public Model makeModel() {
        return new SquidModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof SquidModel)) {
            return null;
        }
        SquidModel squidmodel = (SquidModel)model;
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.getFieldValue(squidmodel, Reflector.ModelSquid_body);
        }
        String s = "tentacle";
        if (modelPart.startsWith(s)) {
            ModelRenderer[] amodelrenderer = (ModelRenderer[])Reflector.getFieldValue(squidmodel, Reflector.ModelSquid_tentacles);
            if (amodelrenderer == null) {
                return null;
            }
            String s1 = modelPart.substring(s.length());
            int i = Config.parseInt(s1, -1);
            return --i >= 0 && i < amodelrenderer.length ? amodelrenderer[i] : null;
        }
        return null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "tentacle1", "tentacle2", "tentacle3", "tentacle4", "tentacle5", "tentacle6", "tentacle7", "tentacle8"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        SquidRenderer squidrenderer = new SquidRenderer(entityrenderermanager);
        squidrenderer.entityModel = (SquidModel)modelBase;
        squidrenderer.shadowSize = shadowSize;
        return squidrenderer;
    }
}
