package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

public class ConduitTileEntityRenderer
extends TileEntityRenderer<ConduitTileEntity> {
    public static final RenderMaterial BASE_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/base"));
    public static final RenderMaterial CAGE_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/cage"));
    public static final RenderMaterial WIND_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind"));
    public static final RenderMaterial VERTICAL_WIND_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/wind_vertical"));
    public static final RenderMaterial OPEN_EYE_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/open_eye"));
    public static final RenderMaterial CLOSED_EYE_TEXTURE = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/conduit/closed_eye"));
    private final ModelRenderer field_228872_h_ = new ModelRenderer(16, 16, 0, 0);
    private final ModelRenderer field_228873_i_;
    private final ModelRenderer field_228874_j_;
    private final ModelRenderer field_228875_k_;

    public ConduitTileEntityRenderer(TileEntityRendererDispatcher p_i226009_1_) {
        super(p_i226009_1_);
        this.field_228872_h_.addBox(-4.0f, -4.0f, 0.0f, 8.0f, 8.0f, 0.0f, 0.01f);
        this.field_228873_i_ = new ModelRenderer(64, 32, 0, 0);
        this.field_228873_i_.addBox(-8.0f, -8.0f, -8.0f, 16.0f, 16.0f, 16.0f);
        this.field_228874_j_ = new ModelRenderer(32, 16, 0, 0);
        this.field_228874_j_.addBox(-3.0f, -3.0f, -3.0f, 6.0f, 6.0f, 6.0f);
        this.field_228875_k_ = new ModelRenderer(32, 16, 0, 0);
        this.field_228875_k_.addBox(-4.0f, -4.0f, -4.0f, 8.0f, 8.0f, 8.0f);
    }

    @Override
    public void render(ConduitTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float f = (float)tileEntityIn.ticksExisted + partialTicks;
        if (!tileEntityIn.isActive()) {
            float f5 = tileEntityIn.getActiveRotation(0.0f);
            IVertexBuilder ivertexbuilder1 = BASE_TEXTURE.getBuffer(bufferIn, RenderType::getEntitySolid);
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 0.5, 0.5);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f5));
            this.field_228874_j_.render(matrixStackIn, ivertexbuilder1, combinedLightIn, combinedOverlayIn);
            matrixStackIn.pop();
        } else {
            float f1 = tileEntityIn.getActiveRotation(partialTicks) * 57.295776f;
            float f2 = MathHelper.sin(f * 0.1f) / 2.0f + 0.5f;
            f2 = f2 * f2 + f2;
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 0.3f + f2 * 0.2f, 0.5);
            Vector3f vector3f = new Vector3f(0.5f, 1.0f, 0.5f);
            vector3f.normalize();
            matrixStackIn.rotate(new Quaternion(vector3f, f1, true));
            this.field_228875_k_.render(matrixStackIn, CAGE_TEXTURE.getBuffer(bufferIn, RenderType::getEntityCutoutNoCull), combinedLightIn, combinedOverlayIn);
            matrixStackIn.pop();
            int i = tileEntityIn.ticksExisted / 66 % 3;
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 0.5, 0.5);
            if (i == 1) {
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0f));
            } else if (i == 2) {
                matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(90.0f));
            }
            IVertexBuilder ivertexbuilder = (i == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).getBuffer(bufferIn, RenderType::getEntityCutoutNoCull);
            this.field_228873_i_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            matrixStackIn.pop();
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 0.5, 0.5);
            matrixStackIn.scale(0.875f, 0.875f, 0.875f);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(180.0f));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0f));
            this.field_228873_i_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            matrixStackIn.pop();
            ActiveRenderInfo activerenderinfo = this.renderDispatcher.renderInfo;
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 0.3f + f2 * 0.2f, 0.5);
            matrixStackIn.scale(0.5f, 0.5f, 0.5f);
            float f3 = -activerenderinfo.getYaw();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f3));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(activerenderinfo.getPitch()));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(180.0f));
            float f4 = 1.3333334f;
            matrixStackIn.scale(1.3333334f, 1.3333334f, 1.3333334f);
            this.field_228872_h_.render(matrixStackIn, (tileEntityIn.isEyeOpen() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).getBuffer(bufferIn, RenderType::getEntityCutoutNoCull), combinedLightIn, combinedOverlayIn);
            matrixStackIn.pop();
        }
    }
}
