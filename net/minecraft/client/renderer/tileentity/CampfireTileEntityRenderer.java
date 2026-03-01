package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.CampfireTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.vector.Vector3f;

public class CampfireTileEntityRenderer
extends TileEntityRenderer<CampfireTileEntity> {
    public CampfireTileEntityRenderer(TileEntityRendererDispatcher p_i226007_1_) {
        super(p_i226007_1_);
    }

    @Override
    public void render(CampfireTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Direction direction = tileEntityIn.getBlockState().get(CampfireBlock.FACING);
        NonNullList<ItemStack> nonnulllist = tileEntityIn.getInventory();
        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = nonnulllist.get(i);
            if (itemstack == ItemStack.EMPTY) continue;
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 0.44921875, 0.5);
            Direction direction1 = Direction.byHorizontalIndex((i + direction.getHorizontalIndex()) % 4);
            float f = -direction1.getHorizontalAngle();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0f));
            matrixStackIn.translate(-0.3125, -0.3125, 0.0);
            matrixStackIn.scale(0.375f, 0.375f, 0.375f);
            Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
            matrixStackIn.pop();
        }
    }
}
