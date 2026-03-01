package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBannerBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class BannerTileEntityRenderer
extends TileEntityRenderer<BannerTileEntity> {
    private final ModelRenderer field_228833_a_ = BannerTileEntityRenderer.getModelRender();
    private final ModelRenderer field_228834_c_ = new ModelRenderer(64, 64, 44, 0);
    private final ModelRenderer field_228835_d_;

    public BannerTileEntityRenderer(TileEntityRendererDispatcher p_i226002_1_) {
        super(p_i226002_1_);
        this.field_228834_c_.addBox(-1.0f, -30.0f, -1.0f, 2.0f, 42.0f, 2.0f, 0.0f);
        this.field_228835_d_ = new ModelRenderer(64, 64, 0, 42);
        this.field_228835_d_.addBox(-10.0f, -32.0f, -1.0f, 20.0f, 2.0f, 2.0f, 0.0f);
    }

    public static ModelRenderer getModelRender() {
        ModelRenderer modelrenderer = new ModelRenderer(64, 64, 0, 0);
        modelrenderer.addBox(-10.0f, 0.0f, -2.0f, 20.0f, 40.0f, 1.0f, 0.0f);
        return modelrenderer;
    }

    @Override
    public void render(BannerTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        List<Pair<BannerPattern, DyeColor>> list = tileEntityIn.getPatternList();
        if (list != null) {
            long i;
            float f = 0.6666667f;
            boolean flag = tileEntityIn.getWorld() == null;
            matrixStackIn.push();
            if (flag) {
                i = 0L;
                matrixStackIn.translate(0.5, 0.5, 0.5);
                this.field_228834_c_.showModel = true;
            } else {
                i = tileEntityIn.getWorld().getGameTime();
                BlockState blockstate = tileEntityIn.getBlockState();
                if (blockstate.getBlock() instanceof BannerBlock) {
                    matrixStackIn.translate(0.5, 0.5, 0.5);
                    float f1 = (float)(-blockstate.get(BannerBlock.ROTATION).intValue() * 360) / 16.0f;
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f1));
                    this.field_228834_c_.showModel = true;
                } else {
                    matrixStackIn.translate(0.5, -0.1666666716337204, 0.5);
                    float f3 = -blockstate.get(WallBannerBlock.HORIZONTAL_FACING).getHorizontalAngle();
                    matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f3));
                    matrixStackIn.translate(0.0, -0.3125, -0.4375);
                    this.field_228834_c_.showModel = false;
                }
            }
            matrixStackIn.push();
            matrixStackIn.scale(0.6666667f, -0.6666667f, -0.6666667f);
            IVertexBuilder ivertexbuilder = ModelBakery.LOCATION_BANNER_BASE.getBuffer(bufferIn, RenderType::getEntitySolid);
            this.field_228834_c_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            this.field_228835_d_.render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
            BlockPos blockpos = tileEntityIn.getPos();
            float f2 = ((float)Math.floorMod((long)(blockpos.getX() * 7 + blockpos.getY() * 9 + blockpos.getZ() * 13) + i, 100L) + partialTicks) / 100.0f;
            this.field_228833_a_.rotateAngleX = (-0.0125f + 0.01f * MathHelper.cos((float)Math.PI * 2 * f2)) * (float)Math.PI;
            this.field_228833_a_.rotationPointY = -32.0f;
            BannerTileEntityRenderer.func_230180_a_(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, this.field_228833_a_, ModelBakery.LOCATION_BANNER_BASE, true, list);
            matrixStackIn.pop();
            matrixStackIn.pop();
        }
    }

    public static void func_230180_a_(MatrixStack p_230180_0_, IRenderTypeBuffer p_230180_1_, int p_230180_2_, int p_230180_3_, ModelRenderer p_230180_4_, RenderMaterial p_230180_5_, boolean p_230180_6_, List<Pair<BannerPattern, DyeColor>> p_230180_7_) {
        BannerTileEntityRenderer.func_241717_a_(p_230180_0_, p_230180_1_, p_230180_2_, p_230180_3_, p_230180_4_, p_230180_5_, p_230180_6_, p_230180_7_, false);
    }

    public static void func_241717_a_(MatrixStack p_241717_0_, IRenderTypeBuffer p_241717_1_, int p_241717_2_, int p_241717_3_, ModelRenderer p_241717_4_, RenderMaterial p_241717_5_, boolean p_241717_6_, List<Pair<BannerPattern, DyeColor>> p_241717_7_, boolean p_241717_8_) {
        p_241717_4_.render(p_241717_0_, p_241717_5_.getItemRendererBuffer(p_241717_1_, RenderType::getEntitySolid, p_241717_8_), p_241717_2_, p_241717_3_);
        for (int i = 0; i < 17 && i < p_241717_7_.size(); ++i) {
            Pair<BannerPattern, DyeColor> pair = p_241717_7_.get(i);
            float[] afloat = pair.getSecond().getColorComponentValues();
            RenderMaterial rendermaterial = new RenderMaterial(p_241717_6_ ? Atlases.BANNER_ATLAS : Atlases.SHIELD_ATLAS, pair.getFirst().getTextureLocation(p_241717_6_));
            p_241717_4_.render(p_241717_0_, rendermaterial.getBuffer(p_241717_1_, RenderType::getEntityNoOutline), p_241717_2_, p_241717_3_, afloat[0], afloat[1], afloat[2], 1.0f);
        }
    }
}
