package net.minecraft.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class PotionEntity
extends ProjectileItemEntity
implements IRendersAsItem {
    public static final Predicate<LivingEntity> WATER_SENSITIVE = LivingEntity::isWaterSensitive;

    public PotionEntity(EntityType<? extends PotionEntity> typeIn, World worldIn) {
        super((EntityType<? extends ProjectileItemEntity>)typeIn, worldIn);
    }

    public PotionEntity(World worldIn, LivingEntity livingEntityIn) {
        super((EntityType<? extends ProjectileItemEntity>)EntityType.POTION, livingEntityIn, worldIn);
    }

    public PotionEntity(World worldIn, double x, double y, double z) {
        super((EntityType<? extends ProjectileItemEntity>)EntityType.POTION, x, y, z, worldIn);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    public float getGravityVelocity() {
        return 0.05f;
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        super.func_230299_a_(p_230299_1_);
        if (!this.world.isRemote) {
            ItemStack itemstack = this.getItem();
            Potion potion = PotionUtils.getPotionFromItem(itemstack);
            List<EffectInstance> list = PotionUtils.getEffectsFromStack(itemstack);
            boolean flag = potion == Potions.WATER && list.isEmpty();
            Direction direction = p_230299_1_.getFace();
            BlockPos blockpos = p_230299_1_.getPos();
            BlockPos blockpos1 = blockpos.offset(direction);
            if (flag) {
                this.extinguishFires(blockpos1, direction);
                this.extinguishFires(blockpos1.offset(direction.getOpposite()), direction);
                for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                    this.extinguishFires(blockpos1.offset(direction1), direction1);
                }
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        if (!this.world.isRemote) {
            boolean flag;
            ItemStack itemstack = this.getItem();
            Potion potion = PotionUtils.getPotionFromItem(itemstack);
            List<EffectInstance> list = PotionUtils.getEffectsFromStack(itemstack);
            boolean bl = flag = potion == Potions.WATER && list.isEmpty();
            if (flag) {
                this.applyWater();
            } else if (!list.isEmpty()) {
                if (this.isLingering()) {
                    this.makeAreaOfEffectCloud(itemstack, potion);
                } else {
                    this.func_213888_a(list, result.getType() == RayTraceResult.Type.ENTITY ? ((EntityRayTraceResult)result).getEntity() : null);
                }
            }
            int i = potion.hasInstantEffect() ? 2007 : 2002;
            this.world.playEvent(i, this.getPosition(), PotionUtils.getColor(itemstack));
            this.remove();
        }
    }

    private void applyWater() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb, WATER_SENSITIVE);
        if (!list.isEmpty()) {
            for (LivingEntity livingentity : list) {
                double d0 = this.getDistanceSq(livingentity);
                if (!(d0 < 16.0) || !livingentity.isWaterSensitive()) continue;
                livingentity.attackEntityFrom(DamageSource.causeIndirectMagicDamage(livingentity, this.func_234616_v_()), 1.0f);
            }
        }
    }

    private void func_213888_a(List<EffectInstance> p_213888_1_, @Nullable Entity p_213888_2_) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);
        if (!list.isEmpty()) {
            for (LivingEntity livingentity : list) {
                double d0;
                if (!livingentity.canBeHitWithPotion() || !((d0 = this.getDistanceSq(livingentity)) < 16.0)) continue;
                double d1 = 1.0 - Math.sqrt(d0) / 4.0;
                if (livingentity == p_213888_2_) {
                    d1 = 1.0;
                }
                for (EffectInstance effectinstance : p_213888_1_) {
                    Effect effect = effectinstance.getPotion();
                    if (effect.isInstant()) {
                        effect.affectEntity(this, this.func_234616_v_(), livingentity, effectinstance.getAmplifier(), d1);
                        continue;
                    }
                    int i = (int)(d1 * (double)effectinstance.getDuration() + 0.5);
                    if (i <= 20) continue;
                    livingentity.addPotionEffect(new EffectInstance(effect, i, effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.doesShowParticles()));
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack p_190542_1_, Potion p_190542_2_) {
        AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ());
        Entity entity = this.func_234616_v_();
        if (entity instanceof LivingEntity) {
            areaeffectcloudentity.setOwner((LivingEntity)entity);
        }
        areaeffectcloudentity.setRadius(3.0f);
        areaeffectcloudentity.setRadiusOnUse(-0.5f);
        areaeffectcloudentity.setWaitTime(10);
        areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());
        areaeffectcloudentity.setPotion(p_190542_2_);
        for (EffectInstance effectinstance : PotionUtils.getFullEffectsFromItem(p_190542_1_)) {
            areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
        }
        CompoundNBT compoundnbt = p_190542_1_.getTag();
        if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99)) {
            areaeffectcloudentity.setColor(compoundnbt.getInt("CustomPotionColor"));
        }
        this.world.addEntity(areaeffectcloudentity);
    }

    private boolean isLingering() {
        return this.getItem().getItem() == Items.LINGERING_POTION;
    }

    private void extinguishFires(BlockPos pos, Direction p_184542_2_) {
        BlockState blockstate = this.world.getBlockState(pos);
        if (blockstate.isIn(BlockTags.FIRE)) {
            this.world.removeBlock(pos, false);
        } else if (CampfireBlock.isLit(blockstate)) {
            this.world.playEvent(null, 1009, pos, 0);
            CampfireBlock.extinguish(this.world, pos, blockstate);
            this.world.setBlockState(pos, (BlockState)blockstate.with(CampfireBlock.LIT, false));
        }
    }
}
