package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.CowModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.optifine.Config;

public class MooshroomMushroomLayer<T extends MooshroomEntity>
extends LayerRenderer<T, CowModel<T>> {
    private ModelRenderer modelRendererMushroom;
    private static final ResourceLocation LOCATION_MUSHROOM_RED = new ResourceLocation("textures/entity/cow/red_mushroom.png");
    private static final ResourceLocation LOCATION_MUSHROOM_BROWN = new ResourceLocation("textures/entity/cow/brown_mushroom.png");
    private static boolean hasTextureMushroomRed = false;
    private static boolean hasTextureMushroomBrown = false;

    public MooshroomMushroomLayer(IEntityRenderer<T, CowModel<T>> rendererIn) {
        super(rendererIn);
        IEntityRenderer<T, CowModel<T>> mooshroomrenderer = rendererIn;
        this.modelRendererMushroom = new ModelRenderer(mooshroomrenderer.getEntityModel());
        this.modelRendererMushroom.setTextureSize(16, 16);
        this.modelRendererMushroom.rotationPointX = 8.0f;
        this.modelRendererMushroom.rotationPointZ = 8.0f;
        this.modelRendererMushroom.rotateAngleY = MathHelper.PI / 4.0f;
        int[][] aint = new int[][]{null, null, {16, 16, 0, 0}, {16, 16, 0, 0}, null, null};
        this.modelRendererMushroom.addBox(aint, -10.0f, 0.0f, 0.0f, 20.0f, 16.0f, 0.0f, 0.0f);
        int[][] aint1 = new int[][]{null, null, null, null, {16, 16, 0, 0}, {16, 16, 0, 0}};
        this.modelRendererMushroom.addBox(aint1, 0.0f, 0.0f, -10.0f, 0.0f, 16.0f, 20.0f, 0.0f);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!((AgeableEntity)entitylivingbaseIn).isChild() && !((Entity)entitylivingbaseIn).isInvisible()) {
            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
            BlockState blockstate = ((MooshroomEntity)entitylivingbaseIn).getMooshroomType().getRenderState();
            ResourceLocation resourcelocation = this.getCustomMushroom(blockstate);
            IVertexBuilder ivertexbuilder = null;
            if (resourcelocation != null) {
                ivertexbuilder = bufferIn.getBuffer(RenderType.getEntityCutout(resourcelocation));
            }
            int i = LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0f);
            matrixStackIn.push();
            matrixStackIn.translate(0.2f, -0.35f, 0.5);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-48.0f));
            matrixStackIn.scale(-1.0f, -1.0f, 1.0f);
            matrixStackIn.translate(-0.5, -0.5, -0.5);
            if (resourcelocation != null) {
                this.modelRendererMushroom.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
            } else {
                blockrendererdispatcher.renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
            }
            matrixStackIn.pop();
            matrixStackIn.push();
            matrixStackIn.translate(0.2f, -0.35f, 0.5);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(42.0f));
            matrixStackIn.translate(0.1f, 0.0, -0.6f);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-48.0f));
            matrixStackIn.scale(-1.0f, -1.0f, 1.0f);
            matrixStackIn.translate(-0.5, -0.5, -0.5);
            if (resourcelocation != null) {
                this.modelRendererMushroom.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
            } else {
                blockrendererdispatcher.renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
            }
            matrixStackIn.pop();
            matrixStackIn.push();
            ((CowModel)this.getEntityModel()).getHead().translateRotate(matrixStackIn);
            matrixStackIn.translate(0.0, -0.7f, -0.2f);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-78.0f));
            matrixStackIn.scale(-1.0f, -1.0f, 1.0f);
            matrixStackIn.translate(-0.5, -0.5, -0.5);
            if (resourcelocation != null) {
                this.modelRendererMushroom.render(matrixStackIn, ivertexbuilder, packedLightIn, i);
            } else {
                blockrendererdispatcher.renderBlock(blockstate, matrixStackIn, bufferIn, packedLightIn, i);
            }
            matrixStackIn.pop();
        }
    }

    private ResourceLocation getCustomMushroom(BlockState p_getCustomMushroom_1_) {
        Block block = p_getCustomMushroom_1_.getBlock();
        if (block == Blocks.RED_MUSHROOM && hasTextureMushroomRed) {
            return LOCATION_MUSHROOM_RED;
        }
        return block == Blocks.BROWN_MUSHROOM && hasTextureMushroomBrown ? LOCATION_MUSHROOM_BROWN : null;
    }

    public static void update() {
        hasTextureMushroomRed = Config.hasResource(LOCATION_MUSHROOM_RED);
        hasTextureMushroomBrown = Config.hasResource(LOCATION_MUSHROOM_BROWN);
    }
}
