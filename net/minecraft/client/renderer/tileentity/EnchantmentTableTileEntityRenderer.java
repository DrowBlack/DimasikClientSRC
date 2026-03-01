package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BookModel;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.EnchantingTableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public class EnchantmentTableTileEntityRenderer
extends TileEntityRenderer<EnchantingTableTileEntity> {
    public static final RenderMaterial TEXTURE_BOOK = new RenderMaterial(AtlasTexture.LOCATION_BLOCKS_TEXTURE, new ResourceLocation("entity/enchanting_table_book"));
    private final BookModel modelBook = new BookModel();

    public EnchantmentTableTileEntityRenderer(TileEntityRendererDispatcher p_i226010_1_) {
        super(p_i226010_1_);
    }

    @Override
    public void render(EnchantingTableTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        float f1;
        matrixStackIn.push();
        matrixStackIn.translate(0.5, 0.75, 0.5);
        float f = (float)tileEntityIn.ticks + partialTicks;
        matrixStackIn.translate(0.0, 0.1f + MathHelper.sin(f * 0.1f) * 0.01f, 0.0);
        for (f1 = tileEntityIn.nextPageAngle - tileEntityIn.pageAngle; f1 >= (float)Math.PI; f1 -= (float)Math.PI * 2) {
        }
        while (f1 < (float)(-Math.PI)) {
            f1 += (float)Math.PI * 2;
        }
        float f2 = tileEntityIn.pageAngle + f1 * partialTicks;
        matrixStackIn.rotate(Vector3f.YP.rotation(-f2));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(80.0f));
        float f3 = MathHelper.lerp(partialTicks, tileEntityIn.field_195524_g, tileEntityIn.field_195523_f);
        float f4 = MathHelper.frac(f3 + 0.25f) * 1.6f - 0.3f;
        float f5 = MathHelper.frac(f3 + 0.75f) * 1.6f - 0.3f;
        float f6 = MathHelper.lerp(partialTicks, tileEntityIn.pageTurningSpeed, tileEntityIn.nextPageTurningSpeed);
        this.modelBook.setBookState(f, MathHelper.clamp(f4, 0.0f, 1.0f), MathHelper.clamp(f5, 0.0f, 1.0f), f6);
        IVertexBuilder ivertexbuilder = TEXTURE_BOOK.getBuffer(bufferIn, RenderType::getEntitySolid);
        this.modelBook.renderAll(matrixStackIn, ivertexbuilder, combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStackIn.pop();
    }
}
