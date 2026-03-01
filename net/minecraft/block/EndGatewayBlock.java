package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class EndGatewayBlock
extends ContainerBlock {
    protected EndGatewayBlock(AbstractBlock.Properties builder) {
        super(builder);
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new EndGatewayTileEntity();
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof EndGatewayTileEntity) {
            int i = ((EndGatewayTileEntity)tileentity).getParticleAmount();
            for (int j = 0; j < i; ++j) {
                double d0 = (double)pos.getX() + rand.nextDouble();
                double d1 = (double)pos.getY() + rand.nextDouble();
                double d2 = (double)pos.getZ() + rand.nextDouble();
                double d3 = (rand.nextDouble() - 0.5) * 0.5;
                double d4 = (rand.nextDouble() - 0.5) * 0.5;
                double d5 = (rand.nextDouble() - 0.5) * 0.5;
                int k = rand.nextInt(2) * 2 - 1;
                if (rand.nextBoolean()) {
                    d2 = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
                    d5 = rand.nextFloat() * 2.0f * (float)k;
                } else {
                    d0 = (double)pos.getX() + 0.5 + 0.25 * (double)k;
                    d3 = rand.nextFloat() * 2.0f * (float)k;
                }
                worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
            }
        }
    }

    @Override
    public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isReplaceable(BlockState state, Fluid fluid) {
        return false;
    }
}
