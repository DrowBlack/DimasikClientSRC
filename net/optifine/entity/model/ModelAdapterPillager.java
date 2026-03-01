package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PillagerRenderer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterIllager;

public class ModelAdapterPillager
extends ModelAdapterIllager {
    public ModelAdapterPillager() {
        super(EntityType.PILLAGER, "pillager", 0.5f);
    }

    @Override
    public Model makeModel() {
        return new IllagerModel(0.0f, 0.0f, 64, 64);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        PillagerRenderer pillagerrenderer = new PillagerRenderer(entityrenderermanager);
        pillagerrenderer.entityModel = (IllagerModel)modelBase;
        pillagerrenderer.shadowSize = shadowSize;
        return pillagerrenderer;
    }
}
