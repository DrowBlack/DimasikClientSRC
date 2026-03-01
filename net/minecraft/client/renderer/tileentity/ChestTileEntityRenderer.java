package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;
import java.util.Calendar;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

public class ChestTileEntityRenderer<T extends TileEntity>
extends TileEntityRenderer<T> {
    private final ModelRenderer singleLid;
    private final ModelRenderer singleBottom;
    private final ModelRenderer singleLatch;
    private final ModelRenderer rightLid;
    private final ModelRenderer rightBottom;
    private final ModelRenderer rightLatch;
    private final ModelRenderer leftLid;
    private final ModelRenderer leftBottom;
    private final ModelRenderer leftLatch;
    private boolean isChristmas;

    public ChestTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
            this.isChristmas = true;
        }
        this.singleBottom = new ModelRenderer(64, 64, 0, 19);
        this.singleBottom.addBox(1.0f, 0.0f, 1.0f, 14.0f, 10.0f, 14.0f, 0.0f);
        this.singleLid = new ModelRenderer(64, 64, 0, 0);
        this.singleLid.addBox(1.0f, 0.0f, 0.0f, 14.0f, 5.0f, 14.0f, 0.0f);
        this.singleLid.rotationPointY = 9.0f;
        this.singleLid.rotationPointZ = 1.0f;
        this.singleLatch = new ModelRenderer(64, 64, 0, 0);
        this.singleLatch.addBox(7.0f, -1.0f, 15.0f, 2.0f, 4.0f, 1.0f, 0.0f);
        this.singleLatch.rotationPointY = 8.0f;
        this.rightBottom = new ModelRenderer(64, 64, 0, 19);
        this.rightBottom.addBox(1.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.rightLid = new ModelRenderer(64, 64, 0, 0);
        this.rightLid.addBox(1.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.rightLid.rotationPointY = 9.0f;
        this.rightLid.rotationPointZ = 1.0f;
        this.rightLatch = new ModelRenderer(64, 64, 0, 0);
        this.rightLatch.addBox(15.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.rightLatch.rotationPointY = 8.0f;
        this.leftBottom = new ModelRenderer(64, 64, 0, 19);
        this.leftBottom.addBox(0.0f, 0.0f, 1.0f, 15.0f, 10.0f, 14.0f, 0.0f);
        this.leftLid = new ModelRenderer(64, 64, 0, 0);
        this.leftLid.addBox(0.0f, 0.0f, 0.0f, 15.0f, 5.0f, 14.0f, 0.0f);
        this.leftLid.rotationPointY = 9.0f;
        this.leftLid.rotationPointZ = 1.0f;
        this.leftLatch = new ModelRenderer(64, 64, 0, 0);
        this.leftLatch.addBox(0.0f, -1.0f, 15.0f, 1.0f, 4.0f, 1.0f, 0.0f);
        this.leftLatch.rotationPointY = 8.0f;
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        World world = ((TileEntity)tileEntityIn).getWorld();
        boolean flag = world != null;
        BlockState blockstate = flag ? ((TileEntity)tileEntityIn).getBlockState() : (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
        ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
        Block block = blockstate.getBlock();
        if (block instanceof AbstractChestBlock) {
            AbstractChestBlock abstractchestblock = (AbstractChestBlock)block;
            boolean flag1 = chesttype != ChestType.SINGLE;
            matrixStackIn.push();
            float f = blockstate.get(ChestBlock.FACING).getHorizontalAngle();
            matrixStackIn.translate(0.5, 0.5, 0.5);
            matrixStackIn.rotate(Vector3f.YP.rotationDegrees(-f));
            matrixStackIn.translate(-0.5, -0.5, -0.5);
            TileEntityMerger.ICallbackWrapper<Object> icallbackwrapper = flag ? abstractchestblock.combine(blockstate, world, ((TileEntity)tileEntityIn).getPos(), true) : TileEntityMerger.ICallback::func_225537_b_;
            float f1 = icallbackwrapper.apply(ChestBlock.getLidRotationCallback((IChestLid)tileEntityIn)).get(partialTicks);
            f1 = 1.0f - f1;
            f1 = 1.0f - f1 * f1 * f1;
            int i = ((Int2IntFunction)icallbackwrapper.apply(new DualBrightnessCallback())).applyAsInt(combinedLightIn);
            RenderMaterial rendermaterial = Atlases.getChestMaterial(tileEntityIn, chesttype, this.isChristmas);
            IVertexBuilder ivertexbuilder = rendermaterial.getBuffer(bufferIn, RenderType::getEntityCutout);
            if (flag1) {
                if (chesttype == ChestType.LEFT) {
                    this.renderModels(matrixStackIn, ivertexbuilder, this.leftLid, this.leftLatch, this.leftBottom, f1, i, combinedOverlayIn);
                } else {
                    this.renderModels(matrixStackIn, ivertexbuilder, this.rightLid, this.rightLatch, this.rightBottom, f1, i, combinedOverlayIn);
                }
            } else {
                this.renderModels(matrixStackIn, ivertexbuilder, this.singleLid, this.singleLatch, this.singleBottom, f1, i, combinedOverlayIn);
            }
            matrixStackIn.pop();
        }
    }

    private void renderModels(MatrixStack matrixStackIn, IVertexBuilder bufferIn, ModelRenderer chestLid, ModelRenderer chestLatch, ModelRenderer chestBottom, float lidAngle, int combinedLightIn, int combinedOverlayIn) {
        chestLatch.rotateAngleX = chestLid.rotateAngleX = -(lidAngle * 1.5707964f);
        chestLid.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestLatch.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
        chestBottom.render(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
    }
}
