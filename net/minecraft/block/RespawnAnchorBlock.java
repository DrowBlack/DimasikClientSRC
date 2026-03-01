package net.minecraft.block;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TransportationHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ICollisionReader;
import net.minecraft.world.World;

public class RespawnAnchorBlock
extends Block {
    public static final IntegerProperty CHARGES = BlockStateProperties.CHARGES;
    private static final ImmutableList<Vector3i> field_242676_b = ImmutableList.of(new Vector3i(0, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(0, 0, 1), new Vector3i(1, 0, 0), new Vector3i(-1, 0, -1), new Vector3i(1, 0, -1), new Vector3i(-1, 0, 1), new Vector3i(1, 0, 1));
    private static final ImmutableList<Vector3i> field_242677_c = ((ImmutableList.Builder)((ImmutableList.Builder)((ImmutableList.Builder)((ImmutableList.Builder)new ImmutableList.Builder().addAll(field_242676_b)).addAll(field_242676_b.stream().map(Vector3i::down).iterator())).addAll(field_242676_b.stream().map(Vector3i::up).iterator())).add(new Vector3i(0, 1, 0))).build();

    public RespawnAnchorBlock(AbstractBlock.Properties properties) {
        super(properties);
        this.setDefaultState((BlockState)((BlockState)this.stateContainer.getBaseState()).with(CHARGES, 0));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        ServerPlayerEntity serverplayerentity;
        ItemStack itemstack = player.getHeldItem(handIn);
        if (handIn == Hand.MAIN_HAND && !RespawnAnchorBlock.isValidFuel(itemstack) && RespawnAnchorBlock.isValidFuel(player.getHeldItem(Hand.OFF_HAND))) {
            return ActionResultType.PASS;
        }
        if (RespawnAnchorBlock.isValidFuel(itemstack) && RespawnAnchorBlock.notFullyCharged(state)) {
            RespawnAnchorBlock.chargeAnchor(worldIn, pos, state);
            if (!player.abilities.isCreativeMode) {
                itemstack.shrink(1);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (state.get(CHARGES) == 0) {
            return ActionResultType.PASS;
        }
        if (!RespawnAnchorBlock.doesRespawnAnchorWork(worldIn)) {
            if (!worldIn.isRemote) {
                this.triggerExplosion(state, worldIn, pos);
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        if (!(worldIn.isRemote || (serverplayerentity = (ServerPlayerEntity)player).func_241141_L_() == worldIn.getDimensionKey() && serverplayerentity.func_241140_K_().equals(pos))) {
            serverplayerentity.func_242111_a(worldIn.getDimensionKey(), pos, 0.0f, false, true);
            worldIn.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.CONSUME;
    }

    private static boolean isValidFuel(ItemStack stack) {
        return stack.getItem() == Items.GLOWSTONE;
    }

    private static boolean notFullyCharged(BlockState state) {
        return state.get(CHARGES) < 4;
    }

    private static boolean isNearWater(BlockPos pos, World world) {
        FluidState fluidstate = world.getFluidState(pos);
        if (!fluidstate.isTagged(FluidTags.WATER)) {
            return false;
        }
        if (fluidstate.isSource()) {
            return true;
        }
        float f = fluidstate.getLevel();
        if (f < 2.0f) {
            return false;
        }
        FluidState fluidstate1 = world.getFluidState(pos.down());
        return !fluidstate1.isTagged(FluidTags.WATER);
    }

    private void triggerExplosion(BlockState state, World world, BlockPos pos) {
        world.removeBlock(pos, false);
        boolean flag = Direction.Plane.HORIZONTAL.getDirectionValues().map(pos::offset).anyMatch(posIn -> RespawnAnchorBlock.isNearWater(posIn, world));
        final boolean flag1 = flag || world.getFluidState(pos.up()).isTagged(FluidTags.WATER);
        ExplosionContext explosioncontext = new ExplosionContext(){

            @Override
            public Optional<Float> getExplosionResistance(Explosion explosion, IBlockReader reader, BlockPos pos, BlockState state, FluidState fluid) {
                return pos.equals(pos) && flag1 ? Optional.of(Float.valueOf(Blocks.WATER.getExplosionResistance())) : super.getExplosionResistance(explosion, reader, pos, state, fluid);
            }
        };
        world.createExplosion(null, DamageSource.func_233546_a_(), explosioncontext, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, 5.0f, true, Explosion.Mode.DESTROY);
    }

    public static boolean doesRespawnAnchorWork(World world) {
        return world.getDimensionType().doesRespawnAnchorWorks();
    }

    public static void chargeAnchor(World world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, (BlockState)state.with(CHARGES, state.get(CHARGES) + 1), 3);
        world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        if (stateIn.get(CHARGES) != 0) {
            if (rand.nextInt(100) == 0) {
                worldIn.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            double d0 = (double)pos.getX() + 0.5 + (0.5 - rand.nextDouble());
            double d1 = (double)pos.getY() + 1.0;
            double d2 = (double)pos.getZ() + 0.5 + (0.5 - rand.nextDouble());
            double d3 = (double)rand.nextFloat() * 0.04;
            worldIn.addParticle(ParticleTypes.REVERSE_PORTAL, d0, d1, d2, 0.0, d3, 0.0);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CHARGES);
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public static int getChargeScale(BlockState state, int scale) {
        return MathHelper.floor((float)(state.get(CHARGES) - 0) / 4.0f * (float)scale);
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return RespawnAnchorBlock.getChargeScale(blockState, 15);
    }

    public static Optional<Vector3d> findRespawnPoint(EntityType<?> entity, ICollisionReader reader, BlockPos pos) {
        Optional<Vector3d> optional = RespawnAnchorBlock.func_242678_a(entity, reader, pos, true);
        return optional.isPresent() ? optional : RespawnAnchorBlock.func_242678_a(entity, reader, pos, false);
    }

    private static Optional<Vector3d> func_242678_a(EntityType<?> type, ICollisionReader collisionReader, BlockPos pos, boolean checkCanSpawn) {
        BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
        for (Vector3i vector3i : field_242677_c) {
            blockpos$mutable.setPos(pos).func_243531_h(vector3i);
            Vector3d vector3d = TransportationHelper.func_242379_a(type, collisionReader, blockpos$mutable, checkCanSpawn);
            if (vector3d == null) continue;
            return Optional.of(vector3d);
        }
        return Optional.empty();
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }
}
