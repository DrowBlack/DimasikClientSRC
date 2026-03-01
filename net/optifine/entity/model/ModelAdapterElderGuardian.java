package net.optifine.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ElderGuardianRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.GuardianModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.EntityType;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.entity.model.ModelAdapterGuardian;

public class ModelAdapterElderGuardian
extends ModelAdapterGuardian {
    public ModelAdapterElderGuardian() {
        super(EntityType.ELDER_GUARDIAN, "elder_guardian", 0.5f);
    }

    @Override
    public IEntityRenderer makeEntityRender(Model modelBase, float shadowSize) {
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        ElderGuardianRenderer elderguardianrenderer = new ElderGuardianRenderer(entityrenderermanager);
        elderguardianrenderer.entityModel = (GuardianModel)modelBase;
        elderguardianrenderer.shadowSize = shadowSize;
        return elderguardianrenderer;
    }
}
