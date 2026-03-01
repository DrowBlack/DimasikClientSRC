package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IllusionerRenderer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterIllager;

public class ModelAdapterIllusioner
extends ModelAdapterIllager {
    public ModelAdapterIllusioner() {
        super(EntityType.ILLUSIONER, "illusioner", 0.5f, new String[]{"illusion_illager"});
    }

    @Override
    public Model makeModel() {
        IllagerModel illagermodel = new IllagerModel(0.0f, 0.0f, 64, 64);
        illagermodel.func_205062_a().showModel = true;
        return illagermodel;
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        IllusionerRenderer illusionerrenderer = new IllusionerRenderer(entityrenderermanager);
        illusionerrenderer.entityModel = (IllagerModel)modelBase;
        illusionerrenderer.shadowSize = shadowSize;
        return illusionerrenderer;
    }
}
