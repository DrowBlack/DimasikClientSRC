package net.minecraft.client.renderer.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StructureTileEntityRenderer
extends TileEntityRenderer<StructureBlockTileEntity> {
    public StructureTileEntityRenderer(TileEntityRendererDispatcher p_i226017_1_) {
        super(p_i226017_1_);
    }

    @Override
    public void render(StructureBlockTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (Minecraft.getInstance().player.canUseCommandBlock() || Minecraft.getInstance().player.isSpectator()) {
            BlockPos blockpos = tileEntityIn.getPosition();
            BlockPos blockpos1 = tileEntityIn.getStructureSize();
            if (blockpos1.getX() >= 1 && blockpos1.getY() >= 1 && blockpos1.getZ() >= 1 && (tileEntityIn.getMode() == StructureMode.SAVE || tileEntityIn.getMode() == StructureMode.LOAD)) {
                double d7;
                double d6;
                double d4;
                double d2;
                double d0 = blockpos.getX();
                double d1 = blockpos.getZ();
                double d5 = blockpos.getY();
                double d8 = d5 + (double)blockpos1.getY();
                double d3 = switch (tileEntityIn.getMirror()) {
                    case Mirror.LEFT_RIGHT -> {
                        d2 = blockpos1.getX();
                        yield -blockpos1.getZ();
                    }
                    case Mirror.FRONT_BACK -> {
                        d2 = -blockpos1.getX();
                        yield blockpos1.getZ();
                    }
                    default -> {
                        d2 = blockpos1.getX();
                        yield blockpos1.getZ();
                    }
                };
                double d9 = switch (tileEntityIn.getRotation()) {
                    case Rotation.CLOCKWISE_90 -> {
                        d4 = d3 < 0.0 ? d0 : d0 + 1.0;
                        d6 = d2 < 0.0 ? d1 + 1.0 : d1;
                        d7 = d4 - d3;
                        yield d6 + d2;
                    }
                    case Rotation.CLOCKWISE_180 -> {
                        d4 = d2 < 0.0 ? d0 : d0 + 1.0;
                        d6 = d3 < 0.0 ? d1 : d1 + 1.0;
                        d7 = d4 - d2;
                        yield d6 - d3;
                    }
                    case Rotation.COUNTERCLOCKWISE_90 -> {
                        d4 = d3 < 0.0 ? d0 + 1.0 : d0;
                        d6 = d2 < 0.0 ? d1 : d1 + 1.0;
                        d7 = d4 + d3;
                        yield d6 - d2;
                    }
                    default -> {
                        d4 = d2 < 0.0 ? d0 + 1.0 : d0;
                        d6 = d3 < 0.0 ? d1 + 1.0 : d1;
                        d7 = d4 + d2;
                        yield d6 + d3;
                    }
                };
                float f = 1.0f;
                float f1 = 0.9f;
                float f2 = 0.5f;
                IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.getLines());
                if (tileEntityIn.getMode() == StructureMode.SAVE || tileEntityIn.showsBoundingBox()) {
                    WorldRenderer.drawBoundingBox(matrixStackIn, ivertexbuilder, d4, d5, d6, d7, d8, d9, 0.9f, 0.9f, 0.9f, 1.0f, 0.5f, 0.5f, 0.5f);
                }
                if (tileEntityIn.getMode() == StructureMode.SAVE && tileEntityIn.showsAir()) {
                    this.func_228880_a_(tileEntityIn, ivertexbuilder, blockpos, true, matrixStackIn);
                    this.func_228880_a_(tileEntityIn, ivertexbuilder, blockpos, false, matrixStackIn);
                }
            }
        }
    }

    private void func_228880_a_(StructureBlockTileEntity p_228880_1_, IVertexBuilder p_228880_2_, BlockPos p_228880_3_, boolean p_228880_4_, MatrixStack p_228880_5_) {
        World iblockreader = p_228880_1_.getWorld();
        BlockPos blockpos = p_228880_1_.getPos();
        BlockPos blockpos1 = blockpos.add(p_228880_3_);
        for (BlockPos blockpos2 : BlockPos.getAllInBoxMutable(blockpos1, blockpos1.add(p_228880_1_.getStructureSize()).add(-1, -1, -1))) {
            BlockState blockstate = iblockreader.getBlockState(blockpos2);
            boolean flag = blockstate.isAir();
            boolean flag1 = blockstate.isIn(Blocks.STRUCTURE_VOID);
            if (!flag && !flag1) continue;
            float f = flag ? 0.05f : 0.0f;
            double d0 = (float)(blockpos2.getX() - blockpos.getX()) + 0.45f - f;
            double d1 = (float)(blockpos2.getY() - blockpos.getY()) + 0.45f - f;
            double d2 = (float)(blockpos2.getZ() - blockpos.getZ()) + 0.45f - f;
            double d3 = (float)(blockpos2.getX() - blockpos.getX()) + 0.55f + f;
            double d4 = (float)(blockpos2.getY() - blockpos.getY()) + 0.55f + f;
            double d5 = (float)(blockpos2.getZ() - blockpos.getZ()) + 0.55f + f;
            if (p_228880_4_) {
                WorldRenderer.drawBoundingBox(p_228880_5_, p_228880_2_, d0, d1, d2, d3, d4, d5, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
                continue;
            }
            if (flag) {
                WorldRenderer.drawBoundingBox(p_228880_5_, p_228880_2_, d0, d1, d2, d3, d4, d5, 0.5f, 0.5f, 1.0f, 1.0f, 0.5f, 0.5f, 1.0f);
                continue;
            }
            WorldRenderer.drawBoundingBox(p_228880_5_, p_228880_2_, d0, d1, d2, d3, d4, d5, 1.0f, 0.25f, 0.25f, 1.0f, 1.0f, 0.25f, 0.25f);
        }
    }

    @Override
    public boolean isGlobalRenderer(StructureBlockTileEntity te) {
        return true;
    }
}
