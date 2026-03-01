package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BoatModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class BoatRenderer
extends EntityRenderer<BoatEntity> {
    private static final ResourceLocation[] BOAT_TEXTURES = new ResourceLocation[]{new ResourceLocation("textures/entity/boat/oak.png"), new ResourceLocation("textures/entity/boat/spruce.png"), new ResourceLocation("textures/entity/boat/birch.png"), new ResourceLocation("textures/entity/boat/jungle.png"), new ResourceLocation("textures/entity/boat/acacia.png"), new ResourceLocation("textures/entity/boat/dark_oak.png")};
    protected final BoatModel modelBoat = new BoatModel();

    public BoatRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.8f;
    }

    @Override
    public void render(BoatEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        float f2;
        matrixStackIn.push();
        matrixStackIn.translate(0.0, 0.375, 0.0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(180.0f - entityYaw));
        float f = (float)entityIn.getTimeSinceHit() - partialTicks;
        float f1 = entityIn.getDamageTaken() - partialTicks;
        if (f1 < 0.0f) {
            f1 = 0.0f;
        }
        if (f > 0.0f) {
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(MathHelper.sin(f) * f * f1 / 10.0f * (float)entityIn.getForwardDirection()));
        }
        if (!MathHelper.epsilonEquals(f2 = entityIn.getRockingAngle(partialTicks), 0.0f)) {
            matrixStackIn.rotate(new Quaternion(new Vector3f(1.0f, 0.0f, 1.0f), entityIn.getRockingAngle(partialTicks), true));
        }
        matrixStackIn.scale(-1.0f, -1.0f, 1.0f);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(90.0f));
        this.modelBoat.setRotationAngles(entityIn, partialTicks, 0.0f, -0.1f, 0.0f, 0.0f);
        IVertexBuilder ivertexbuilder = bufferIn.getBuffer(this.modelBoat.getRenderType(this.getEntityTexture(entityIn)));
        this.modelBoat.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        if (!entityIn.canSwim()) {
            IVertexBuilder ivertexbuilder1 = bufferIn.getBuffer(RenderType.getWaterMask());
            this.modelBoat.func_228245_c_().render(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY);
        }
        matrixStackIn.pop();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(BoatEntity entity) {
        return BOAT_TEXTURES[entity.getBoatType().ordinal()];
    }
}
