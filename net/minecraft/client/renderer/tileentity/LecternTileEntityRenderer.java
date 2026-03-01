package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.tileentity.EnchantmentTableTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.LecternTileEntity;
import net.minecraft.util.math.vector.Vector3f;

public class LecternTileEntityRenderer
extends TileEntityRenderer<LecternTileEntity> {
    private final BookModel field_217656_d = new BookModel();

    public LecternTileEntityRenderer(TileEntityRendererDispatcher p_i226011_1_) {
        super(p_i226011_1_);
    }

    @Override
    public void render(LecternTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockState blockstate = tileEntityIn.getBlockState();
        if (blockstate.get(LecternBlock.HAS_BOOK).booleanValue()) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5, 1.0625, 0.5);
            float f = blockstate.get(LecternBlock.FACING).rotateY().getHorizontalAngle();
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
            matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(67.5f));
            matrixStackIn.translate(0.0, -0.125, 0.0);
            this.field_217656_d.setBookState(0.0f, 0.1f, 0.9f, 1.2f);
            IVertexBuilder ivertexbuilder = EnchantmentTableTileEntityRenderer.TEXTURE_BOOK.getBuffer(bufferIn, RenderType::getEntitySolid);
            this.field_217656_d.renderAll(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);
            matrixStackIn.pop();
        }
    }
}
