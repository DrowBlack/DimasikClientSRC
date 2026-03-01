package net.optifine.entity.model;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.DolphinRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.DolphinModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.entity.model.ModelRendererUtils;

public class ModelAdapterDolphin
extends ModelAdapter {
    public ModelAdapterDolphin() {
        super(EntityType.DOLPHIN, "dolphin", 0.7f);
    }

    @Override
    public Model makeModel() {
        return new DolphinModel();
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof DolphinModel)) {
            return null;
        }
        DolphinModel dolphinmodel = (DolphinModel)model;
        Iterator<ModelRenderer> iterator = dolphinmodel.getParts().iterator();
        ModelRenderer modelrenderer = ModelRendererUtils.getModelRenderer(iterator, 0);
        if (modelrenderer == null) {
            return null;
        }
        if (modelPart.equals("body")) {
            return modelrenderer;
        }
        if (modelPart.equals("back_fin")) {
            return modelrenderer.getChild(0);
        }
        if (modelPart.equals("left_fin")) {
            return modelrenderer.getChild(1);
        }
        if (modelPart.equals("right_fin")) {
            return modelrenderer.getChild(2);
        }
        if (modelPart.equals("tail")) {
            return modelrenderer.getChild(3);
        }
        if (modelPart.equals("tail_fin")) {
            return modelrenderer.getChild(3).getChild(0);
        }
        return modelPart.equals("head") ? modelrenderer.getChild(4) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"body", "back_fin", "left_fin", "right_fin", "tail", "tail_fin", "head"};
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        DolphinRenderer dolphinrenderer = new DolphinRenderer(entityrenderermanager);
        dolphinrenderer.entityModel = (DolphinModel)modelBase;
        dolphinrenderer.shadowSize = shadowSize;
        return dolphinrenderer;
    }
}
