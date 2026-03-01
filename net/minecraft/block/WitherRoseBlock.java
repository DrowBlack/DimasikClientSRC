package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class WitherRoseBlock
extends FlowerBlock {
    public WitherRoseBlock(Effect effectIn, AbstractBlock.Properties propertiesIn) {
        super(effectIn, 8, propertiesIn);
    }

    @Override
    protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return super.isValidGround(state, worldIn, pos) || state.isIn(Blocks.NETHERRACK) || state.isIn(Blocks.SOUL_SAND) || state.isIn(Blocks.SOUL_SOIL);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        VoxelShape voxelshape = this.getShape(stateIn, worldIn, pos, ISelectionContext.dummy());
        Vector3d vector3d = voxelshape.getBoundingBox().getCenter();
        double d0 = (double)pos.getX() + vector3d.x;
        double d1 = (double)pos.getZ() + vector3d.z;
        for (int i = 0; i < 3; ++i) {
            if (!rand.nextBoolean()) continue;
            worldIn.addParticle(ParticleTypes.SMOKE, d0 + rand.nextDouble() / 5.0, (double)pos.getY() + (0.5 - rand.nextDouble()), d1 + rand.nextDouble() / 5.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        LivingEntity livingentity;
        if (!worldIn.isRemote && worldIn.getDifficulty() != Difficulty.PEACEFUL && entityIn instanceof LivingEntity && !(livingentity = (LivingEntity)entityIn).isInvulnerableTo(DamageSource.WITHER)) {
            livingentity.addPotionEffect(new EffectInstance(Effects.WITHER, 40));
        }
    }
}
