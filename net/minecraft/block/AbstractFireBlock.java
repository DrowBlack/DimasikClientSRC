package net.minecraft.block;

import java.util.Optional;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import net.minecraft.block.PortalSize;
import net.minecraft.block.SoulFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class AbstractFireBlock
extends Block {
    private final float fireDamage;
    protected static final VoxelShape shapeDown = Block.makeCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);

    public AbstractFireBlock(AbstractBlock.Properties properties, float fireDamage) {
        super(properties);
        this.fireDamage = fireDamage;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return AbstractFireBlock.getFireForPlacement(context.getWorld(), context.getPos());
    }

    public static BlockState getFireForPlacement(IBlockReader reader, BlockPos pos) {
        BlockPos blockpos = pos.down();
        BlockState blockstate = reader.getBlockState(blockpos);
        return SoulFireBlock.shouldLightSoulFire(blockstate.getBlock()) ? Blocks.SOUL_FIRE.getDefaultState() : ((FireBlock)Blocks.FIRE).getStateForPlacement(reader, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shapeDown;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        block12: {
            block11: {
                BlockPos blockpos;
                BlockState blockstate;
                if (rand.nextInt(24) == 0) {
                    worldIn.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0f + rand.nextFloat(), rand.nextFloat() * 0.7f + 0.3f, false);
                }
                if (this.canBurn(blockstate = worldIn.getBlockState(blockpos = pos.down())) || blockstate.isSolidSide(worldIn, blockpos, Direction.UP)) break block11;
                if (this.canBurn(worldIn.getBlockState(pos.west()))) {
                    for (int j = 0; j < 2; ++j) {
                        double d3 = (double)pos.getX() + rand.nextDouble() * (double)0.1f;
                        double d8 = (double)pos.getY() + rand.nextDouble();
                        double d13 = (double)pos.getZ() + rand.nextDouble();
                        worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d3, d8, d13, 0.0, 0.0, 0.0);
                    }
                }
                if (this.canBurn(worldIn.getBlockState(pos.east()))) {
                    for (int k = 0; k < 2; ++k) {
                        double d4 = (double)(pos.getX() + 1) - rand.nextDouble() * (double)0.1f;
                        double d9 = (double)pos.getY() + rand.nextDouble();
                        double d14 = (double)pos.getZ() + rand.nextDouble();
                        worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d4, d9, d14, 0.0, 0.0, 0.0);
                    }
                }
                if (this.canBurn(worldIn.getBlockState(pos.north()))) {
                    for (int l = 0; l < 2; ++l) {
                        double d5 = (double)pos.getX() + rand.nextDouble();
                        double d10 = (double)pos.getY() + rand.nextDouble();
                        double d15 = (double)pos.getZ() + rand.nextDouble() * (double)0.1f;
                        worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d5, d10, d15, 0.0, 0.0, 0.0);
                    }
                }
                if (this.canBurn(worldIn.getBlockState(pos.south()))) {
                    for (int i1 = 0; i1 < 2; ++i1) {
                        double d6 = (double)pos.getX() + rand.nextDouble();
                        double d11 = (double)pos.getY() + rand.nextDouble();
                        double d16 = (double)(pos.getZ() + 1) - rand.nextDouble() * (double)0.1f;
                        worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d6, d11, d16, 0.0, 0.0, 0.0);
                    }
                }
                if (!this.canBurn(worldIn.getBlockState(pos.up()))) break block12;
                for (int j1 = 0; j1 < 2; ++j1) {
                    double d7 = (double)pos.getX() + rand.nextDouble();
                    double d12 = (double)(pos.getY() + 1) - rand.nextDouble() * (double)0.1f;
                    double d17 = (double)pos.getZ() + rand.nextDouble();
                    worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d7, d12, d17, 0.0, 0.0, 0.0);
                }
                break block12;
            }
            for (int i = 0; i < 3; ++i) {
                double d0 = (double)pos.getX() + rand.nextDouble();
                double d1 = (double)pos.getY() + rand.nextDouble() * 0.5 + 0.5;
                double d2 = (double)pos.getZ() + rand.nextDouble();
                worldIn.addParticle(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
            }
        }
    }

    protected abstract boolean canBurn(BlockState var1);

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (!entityIn.isImmuneToFire()) {
            entityIn.forceFireTicks(entityIn.getFireTimer() + 1);
            if (entityIn.getFireTimer() == 0) {
                entityIn.setFire(8);
            }
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, this.fireDamage);
        }
        super.onEntityCollision(state, worldIn, pos, entityIn);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.isIn(state.getBlock())) {
            Optional<PortalSize> optional;
            if (AbstractFireBlock.canLightPortal(worldIn) && (optional = PortalSize.func_242964_a(worldIn, pos, Direction.Axis.X)).isPresent()) {
                optional.get().placePortalBlocks();
                return;
            }
            if (!state.isValidPosition(worldIn, pos)) {
                worldIn.removeBlock(pos, false);
            }
        }
    }

    private static boolean canLightPortal(World world) {
        return world.getDimensionKey() == World.OVERWORLD || world.getDimensionKey() == World.THE_NETHER;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!worldIn.isRemote()) {
            worldIn.playEvent(null, 1009, pos, 0);
        }
    }

    public static boolean canLightBlock(World world, BlockPos pos, Direction direction) {
        BlockState blockstate = world.getBlockState(pos);
        if (!blockstate.isAir()) {
            return false;
        }
        return AbstractFireBlock.getFireForPlacement(world, pos).isValidPosition(world, pos) || AbstractFireBlock.shouldLightPortal(world, pos, direction);
    }

    private static boolean shouldLightPortal(World world, BlockPos pos, Direction directionIn) {
        if (!AbstractFireBlock.canLightPortal(world)) {
            return false;
        }
        BlockPos.Mutable blockpos$mutable = pos.toMutable();
        boolean flag = false;
        for (Direction direction : Direction.values()) {
            if (!world.getBlockState(blockpos$mutable.setPos(pos).move(direction)).isIn(Blocks.OBSIDIAN)) continue;
            flag = true;
            break;
        }
        if (!flag) {
            return false;
        }
        Direction.Axis direction$axis = directionIn.getAxis().isHorizontal() ? directionIn.rotateYCCW().getAxis() : Direction.Plane.HORIZONTAL.func_244803_b(world.rand);
        return PortalSize.func_242964_a(world, pos, direction$axis).isPresent();
    }
}
