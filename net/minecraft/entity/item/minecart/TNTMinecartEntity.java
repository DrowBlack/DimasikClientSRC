package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class TNTMinecartEntity
extends AbstractMinecartEntity {
    private int minecartTNTFuse = -1;

    public TNTMinecartEntity(EntityType<? extends TNTMinecartEntity> type, World world) {
        super(type, world);
    }

    public TNTMinecartEntity(World worldIn, double x, double y, double z) {
        super(EntityType.TNT_MINECART, worldIn, x, y, z);
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.TNT;
    }

    @Override
    public BlockState getDefaultDisplayTile() {
        return Blocks.TNT.getDefaultState();
    }

    @Override
    public void tick() {
        double d0;
        super.tick();
        if (this.minecartTNTFuse > 0) {
            --this.minecartTNTFuse;
            this.world.addParticle(ParticleTypes.SMOKE, this.getPosX(), this.getPosY() + 0.5, this.getPosZ(), 0.0, 0.0, 0.0);
        } else if (this.minecartTNTFuse == 0) {
            this.explodeCart(TNTMinecartEntity.horizontalMag(this.getMotion()));
        }
        if (this.collidedHorizontally && (d0 = TNTMinecartEntity.horizontalMag(this.getMotion())) >= (double)0.01f) {
            this.explodeCart(d0);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        AbstractArrowEntity abstractarrowentity;
        Entity entity = source.getImmediateSource();
        if (entity instanceof AbstractArrowEntity && (abstractarrowentity = (AbstractArrowEntity)entity).isBurning()) {
            this.explodeCart(abstractarrowentity.getMotion().lengthSquared());
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void killMinecart(DamageSource source) {
        double d0 = TNTMinecartEntity.horizontalMag(this.getMotion());
        if (!(source.isFireDamage() || source.isExplosion() || d0 >= (double)0.01f)) {
            super.killMinecart(source);
            if (!source.isExplosion() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                this.entityDropItem(Blocks.TNT);
            }
        } else if (this.minecartTNTFuse < 0) {
            this.ignite();
            this.minecartTNTFuse = this.rand.nextInt(20) + this.rand.nextInt(20);
        }
    }

    protected void explodeCart(double radiusModifier) {
        if (!this.world.isRemote) {
            double d0 = Math.sqrt(radiusModifier);
            if (d0 > 5.0) {
                d0 = 5.0;
            }
            this.world.createExplosion(this, this.getPosX(), this.getPosY(), this.getPosZ(), (float)(4.0 + this.rand.nextDouble() * 1.5 * d0), Explosion.Mode.BREAK);
            this.remove();
        }
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        if (distance >= 3.0f) {
            float f = distance / 10.0f;
            this.explodeCart(f * f);
        }
        return super.onLivingFall(distance, damageMultiplier);
    }

    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
        if (receivingPower && this.minecartTNTFuse < 0) {
            this.ignite();
        }
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 10) {
            this.ignite();
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void ignite() {
        this.minecartTNTFuse = 80;
        if (!this.world.isRemote) {
            this.world.setEntityState(this, (byte)10);
            if (!this.isSilent()) {
                this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    public int getFuseTicks() {
        return this.minecartTNTFuse;
    }

    public boolean isIgnited() {
        return this.minecartTNTFuse > -1;
    }

    @Override
    public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, FluidState fluidState, float explosionPower) {
        return !this.isIgnited() || !blockStateIn.isIn(BlockTags.RAILS) && !worldIn.getBlockState(pos.up()).isIn(BlockTags.RAILS) ? super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn, fluidState, explosionPower) : 0.0f;
    }

    @Override
    public boolean canExplosionDestroyBlock(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, BlockState blockStateIn, float explosionPower) {
        return !this.isIgnited() || !blockStateIn.isIn(BlockTags.RAILS) && !worldIn.getBlockState(pos.up()).isIn(BlockTags.RAILS) ? super.canExplosionDestroyBlock(explosionIn, worldIn, pos, blockStateIn, explosionPower) : false;
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("TNTFuse", 99)) {
            this.minecartTNTFuse = compound.getInt("TNTFuse");
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("TNTFuse", this.minecartTNTFuse);
    }
}
