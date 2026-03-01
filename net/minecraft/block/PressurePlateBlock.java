package net.minecraft.block;

import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock
extends AbstractPressurePlateBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final Sensitivity sensitivity;

    protected PressurePlateBlock(Sensitivity sensitivityIn, AbstractBlock.Properties propertiesIn) {
        super(propertiesIn);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(POWERED, false));
        this.sensitivity = sensitivityIn;
    }

    @Override
    protected int getRedstoneStrength(BlockState state) {
        return state.get(POWERED) != false ? 15 : 0;
    }

    @Override
    protected BlockState setRedstoneStrength(BlockState state, int strength) {
        return (BlockState)state.with(POWERED, strength > 0);
    }

    @Override
    protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
        } else {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.8f);
        }
    }

    @Override
    protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
        if (this.material != Material.WOOD && this.material != Material.NETHER_WOOD) {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.5f);
        } else {
            worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.7f);
        }
    }

    @Override
    protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
        List<Entity> list;
        AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
        switch (this.sensitivity) {
            case EVERYTHING: {
                list = worldIn.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
                break;
            }
            case MOBS: {
                list = worldIn.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);
                break;
            }
            default: {
                return 0;
            }
        }
        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.doesEntityNotTriggerPressurePlate()) continue;
                return 15;
            }
        }
        return 0;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    public static enum Sensitivity {
        EVERYTHING,
        MOBS;

    }
}
