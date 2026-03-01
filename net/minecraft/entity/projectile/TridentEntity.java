package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TridentEntity
extends AbstractArrowEntity {
    private static final DataParameter<Byte> LOYALTY_LEVEL = EntityDataManager.createKey(TridentEntity.class, DataSerializers.BYTE);
    private static final DataParameter<Boolean> field_226571_aq_ = EntityDataManager.createKey(TridentEntity.class, DataSerializers.BOOLEAN);
    private ItemStack thrownStack = new ItemStack(Items.TRIDENT);
    private boolean dealtDamage;
    public int returningTicks;

    public TridentEntity(EntityType<? extends TridentEntity> type, World worldIn) {
        super((EntityType<? extends AbstractArrowEntity>)type, worldIn);
    }

    public TridentEntity(World worldIn, LivingEntity thrower, ItemStack thrownStackIn) {
        super(EntityType.TRIDENT, thrower, worldIn);
        this.thrownStack = thrownStackIn.copy();
        this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(thrownStackIn));
        this.dataManager.set(field_226571_aq_, thrownStackIn.hasEffect());
    }

    public TridentEntity(World worldIn, double x, double y, double z) {
        super(EntityType.TRIDENT, x, y, z, worldIn);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(LOYALTY_LEVEL, (byte)0);
        this.dataManager.register(field_226571_aq_, false);
    }

    @Override
    public void tick() {
        if (this.timeInGround > 4) {
            this.dealtDamage = true;
        }
        Entity entity = this.func_234616_v_();
        if ((this.dealtDamage || this.getNoClip()) && entity != null) {
            byte i = this.dataManager.get(LOYALTY_LEVEL);
            if (i > 0 && !this.shouldReturnToThrower()) {
                if (!this.world.isRemote && this.pickupStatus == AbstractArrowEntity.PickupStatus.ALLOWED) {
                    this.entityDropItem(this.getArrowStack(), 0.1f);
                }
                this.remove();
            } else if (i > 0) {
                this.setNoClip(true);
                Vector3d vector3d = new Vector3d(entity.getPosX() - this.getPosX(), entity.getPosYEye() - this.getPosY(), entity.getPosZ() - this.getPosZ());
                this.setRawPosition(this.getPosX(), this.getPosY() + vector3d.y * 0.015 * (double)i, this.getPosZ());
                if (this.world.isRemote) {
                    this.lastTickPosY = this.getPosY();
                }
                double d0 = 0.05 * (double)i;
                this.setMotion(this.getMotion().scale(0.95).add(vector3d.normalize().scale(d0)));
                if (this.returningTicks == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0f, 1.0f);
                }
                ++this.returningTicks;
            }
        }
        super.tick();
    }

    private boolean shouldReturnToThrower() {
        Entity entity = this.func_234616_v_();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        }
        return false;
    }

    @Override
    protected ItemStack getArrowStack() {
        return this.thrownStack.copy();
    }

    public boolean func_226572_w_() {
        return this.dataManager.get(field_226571_aq_);
    }

    @Override
    @Nullable
    protected EntityRayTraceResult rayTraceEntities(Vector3d startVec, Vector3d endVec) {
        return this.dealtDamage ? null : super.rayTraceEntities(startVec, endVec);
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        BlockPos blockpos;
        Entity entity1;
        Entity entity = p_213868_1_.getEntity();
        float f = 8.0f;
        if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            f += EnchantmentHelper.getModifierForCreature(this.thrownStack, livingentity.getCreatureAttribute());
        }
        DamageSource damagesource = DamageSource.causeTridentDamage(this, (entity1 = this.func_234616_v_()) == null ? this : entity1);
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.ITEM_TRIDENT_HIT;
        if (entity.attackEntityFrom(damagesource, f)) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity)entity;
                if (entity1 instanceof LivingEntity) {
                    EnchantmentHelper.applyThornEnchantments(livingentity1, entity1);
                    EnchantmentHelper.applyArthropodEnchantments((LivingEntity)entity1, livingentity1);
                }
                this.arrowHit(livingentity1);
            }
        }
        this.setMotion(this.getMotion().mul(-0.01, -0.1, -0.01));
        float f1 = 1.0f;
        if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.thrownStack) && this.world.canSeeSky(blockpos = entity.getPosition())) {
            LightningBoltEntity lightningboltentity = EntityType.LIGHTNING_BOLT.create(this.world);
            lightningboltentity.moveForced(Vector3d.copyCenteredHorizontally(blockpos));
            lightningboltentity.setCaster(entity1 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity1 : null);
            this.world.addEntity(lightningboltentity);
            soundevent = SoundEvents.ITEM_TRIDENT_THUNDER;
            f1 = 5.0f;
        }
        this.playSound(soundevent, f1, 1.0f);
    }

    @Override
    protected SoundEvent getHitEntitySound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onCollideWithPlayer(PlayerEntity entityIn) {
        Entity entity = this.func_234616_v_();
        if (entity == null || entity.getUniqueID() == entityIn.getUniqueID()) {
            super.onCollideWithPlayer(entityIn);
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        if (compound.contains("Trident", 10)) {
            this.thrownStack = ItemStack.read(compound.getCompound("Trident"));
        }
        this.dealtDamage = compound.getBoolean("DealtDamage");
        this.dataManager.set(LOYALTY_LEVEL, (byte)EnchantmentHelper.getLoyaltyModifier(this.thrownStack));
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.put("Trident", this.thrownStack.write(new CompoundNBT()));
        compound.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void func_225516_i_() {
        byte i = this.dataManager.get(LOYALTY_LEVEL);
        if (this.pickupStatus != AbstractArrowEntity.PickupStatus.ALLOWED || i <= 0) {
            super.func_225516_i_();
        }
    }

    @Override
    protected float getWaterDrag() {
        return 0.99f;
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return true;
    }
}
