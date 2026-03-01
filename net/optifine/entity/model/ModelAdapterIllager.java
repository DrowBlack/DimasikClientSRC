package net.optifine.entity.model;

import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.reflect.Reflector;

public abstract class ModelAdapterIllager
extends ModelAdapter {
    public ModelAdapterIllager(EntityType type, String name, float shadowSize) {
        super(type, name, shadowSize);
    }

    public ModelAdapterIllager(EntityType type, String name, float shadowSize, String[] aliases) {
        super(type, name, shadowSize, aliases);
    }

    @Override
    public ModelRenderer getModelRenderer(Model model, String modelPart) {
        ModelRenderer modelrenderer;
        if (!(model instanceof IllagerModel)) {
            return null;
        }
        IllagerModel illagermodel = (IllagerModel)model;
        if (modelPart.equals("head")) {
            return (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 0);
        }
        if (modelPart.equals("hat")) {
            return (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 1);
        }
        if (modelPart.equals("body")) {
            return (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 2);
        }
        if (modelPart.equals("arms")) {
            return (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 3);
        }
        if (modelPart.equals("right_leg")) {
            return (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 4);
        }
        if (modelPart.equals("left_leg")) {
            return (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 5);
        }
        if (modelPart.equals("nose") && (modelrenderer = (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 0)) != null) {
            return modelrenderer.getChild(0);
        }
        if (modelPart.equals("right_arm")) {
            return (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 6);
        }
        return modelPart.equals("left_arm") ? (ModelRenderer)Reflector.ModelIllager_ModelRenderers.getValue(illagermodel, 7) : null;
    }

    @Override
    public String[] getModelRendererNames() {
        return new String[]{"head", "hat", "body", "arms", "right_leg", "left_leg", "nose", "right_arm", "left_arm"};
    }
}
