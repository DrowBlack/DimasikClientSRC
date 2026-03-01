package net.optifine.entity.model;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ChestedHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.HorseArmorChestsModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterHorse;
import net.optifine.reflect.Reflector;

public class ModelAdapterMule
extends ModelAdapterHorse {
    public ModelAdapterMule() {
        super(EntityType.MULE, "mule", 0.75f);
    }

    @Override
    public Model makeModel() {
        return new HorseArmorChestsModel(0.92f);
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        if (!(model instanceof HorseArmorChestsModel)) {
            return null;
        }
        HorseArmorChestsModel horsearmorchestsmodel = (HorseArmorChestsModel)model;
        if (modelPart.equals("left_chest")) {
            return (ModelRenderer)Reflector.ModelHorseChests_ModelRenderers.getValue(horsearmorchestsmodel, 0);
        }
        return modelPart.equals("right_chest") ? (ModelRenderer)Reflector.ModelHorseChests_ModelRenderers.getValue(horsearmorchestsmodel, 1) : super.getModelRenderer(model, modelPart);
    }

    @Override
    public String[] getModelRendererNames() {
        ArrayList<String> list = new ArrayList<String>(Arrays.asList(super.getModelRendererNames()));
        list.add("left_chest");
        list.add("right_chest");
        return list.toArray(new String[list.size()]);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ChestedHorseRenderer chestedhorserenderer = new ChestedHorseRenderer(entityrenderermanager, 0.92f);
        chestedhorserenderer.entityModel = (EntityModel)modelBase;
        chestedhorserenderer.shadowSize = shadowSize;
        return chestedhorserenderer;
    }
}
