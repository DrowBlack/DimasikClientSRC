package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

public class ShulkerBoxTileEntityRenderer
extends TileEntityRenderer<ShulkerBoxTileEntity> {
    private final ShulkerModel<?> model;

    public ShulkerBoxTileEntityRenderer(ShulkerModel<?> p_i226013_1_, TileEntityRendererDispatcher p_i226013_2_) {
        super(p_i226013_2_);
        this.model = p_i226013_1_;
    }

    @Override
    public void render(ShulkerBoxTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        DyeColor dyecolor;
        BlockState blockstate;
        Direction direction = Direction.UP;
        if (tileEntityIn.hasWorld() && (blockstate = tileEntityIn.getWorld().getBlockState(tileEntityIn.getPos())).getBlock() instanceof ShulkerBoxBlock) {
            direction = blockstate.get(ShulkerBoxBlock.FACING);
        }
        RenderMaterial rendermaterial = (dyecolor = tileEntityIn.getColor()) == null ? Atlases.DEFAULT_SHULKER_TEXTURE : Atlases.SHULKER_TEXTURES.get(dyecolor.getId());
        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.5, 0.5);
        float f = 0.9995f;
        matrixStackIn.scale(0.9995f, 0.9995f, 0.9995f);
        matrixStackIn.rotate(direction.getRotation());
        matrixStackIn.scale(1.0f, -1.0f, -1.0f);
        matrixStackIn.translate(0.0, -1.0, 0.0);
        IVertexBuilder ivertexbuilder = rendermaterial.getBuffer(bufferIn, RenderType::getEntityCutoutNoCull);
        this.model.getBase().render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        matrixStackIn.translate(0.0, -tileEntityIn.getProgress(partialTicks) * 0.5f, 0.0);
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(270.0f * tileEntityIn.getProgress(partialTicks)));
        this.model.getLid().render(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn);
        matrixStackIn.pop();
    }
}
