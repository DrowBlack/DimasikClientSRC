package net.minecraft.entity.passive.horse;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LlamaFollowCaravanGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.entity.ai.goal.RunAroundLikeCrazyGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LlamaEntity
extends AbstractChestedHorseEntity
implements IRangedAttackMob {
    private static final Ingredient field_234243_bC_ = Ingredient.fromItems(Items.WHEAT, Blocks.HAY_BLOCK.asItem());
    private static final DataParameter<Integer> DATA_STRENGTH_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DATA_COLOR_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> DATA_VARIANT_ID = EntityDataManager.createKey(LlamaEntity.class, DataSerializers.VARINT);
    private boolean didSpit;
    @Nullable
    private LlamaEntity caravanHead;
    @Nullable
    private LlamaEntity caravanTail;

    public LlamaEntity(EntityType<? extends LlamaEntity> type, World worldIn) {
        super((EntityType<? extends AbstractChestedHorseEntity>)type, worldIn);
    }

    public boolean isTraderLlama() {
        return false;
    }

    private void setStrength(int strengthIn) {
        this.dataManager.set(DATA_STRENGTH_ID, Math.max(1, Math.min(5, strengthIn)));
    }

    private void setRandomStrength() {
        int i = this.rand.nextFloat() < 0.04f ? 5 : 3;
        this.setStrength(1 + this.rand.nextInt(i));
    }

    public int getStrength() {
        return this.dataManager.get(DATA_STRENGTH_ID);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putInt("Strength", this.getStrength());
        if (!this.horseChest.getStackInSlot(1).isEmpty()) {
            compound.put("DecorItem", this.horseChest.getStackInSlot(1).write(new CompoundNBT()));
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.setStrength(compound.getInt("Strength"));
        super.readAdditional(compound);
        this.setVariant(compound.getInt("Variant"));
        if (compound.contains("DecorItem", 10)) {
            this.horseChest.setInventorySlotContents(1, ItemStack.read(compound.getCompound("DecorItem")));
        }
        this.func_230275_fc_();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new RunAroundLikeCrazyGoal(this, 1.2));
        this.goalSelector.addGoal(2, new LlamaFollowCaravanGoal(this, 2.1f));
        this.goalSelector.addGoal(3, new RangedAttackGoal(this, 1.25, 40, 20.0f));
        this.goalSelector.addGoal(3, new PanicGoal(this, 1.2));
        this.goalSelector.addGoal(4, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.0));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new DefendTargetGoal(this));
    }

    public static AttributeModifierMap.MutableAttribute func_234244_fu_() {
        return LlamaEntity.func_234234_eJ_().createMutableAttribute(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(DATA_STRENGTH_ID, 0);
        this.dataManager.register(DATA_COLOR_ID, -1);
        this.dataManager.register(DATA_VARIANT_ID, 0);
    }

    public int getVariant() {
        return MathHelper.clamp(this.dataManager.get(DATA_VARIANT_ID), 0, 3);
    }

    public void setVariant(int variantIn) {
        this.dataManager.set(DATA_VARIANT_ID, variantIn);
    }

    @Override
    protected int getInventorySize() {
        return this.hasChest() ? 2 + 3 * this.getInventoryColumns() : super.getInventorySize();
    }

    @Override
    public void updatePassenger(Entity passenger) {
        if (this.isPassenger(passenger)) {
            float f = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180));
            float f1 = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180));
            float f2 = 0.3f;
            passenger.setPosition(this.getPosX() + (double)(0.3f * f1), this.getPosY() + this.getMountedYOffset() + passenger.getYOffset(), this.getPosZ() - (double)(0.3f * f));
        }
    }

    @Override
    public double getMountedYOffset() {
        return (double)this.getHeight() * 0.67;
    }

    @Override
    public boolean canBeSteered() {
        return false;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return field_234243_bC_.test(stack);
    }

    @Override
    protected boolean handleEating(PlayerEntity player, ItemStack stack) {
        SoundEvent soundevent;
        int i = 0;
        int j = 0;
        float f = 0.0f;
        boolean flag = false;
        Item item = stack.getItem();
        if (item == Items.WHEAT) {
            i = 10;
            j = 3;
            f = 2.0f;
        } else if (item == Blocks.HAY_BLOCK.asItem()) {
            i = 90;
            j = 6;
            f = 10.0f;
            if (this.isTame() && this.getGrowingAge() == 0 && this.canFallInLove()) {
                flag = true;
                this.setInLove(player);
            }
        }
        if (this.getHealth() < this.getMaxHealth() && f > 0.0f) {
            this.heal(f);
            flag = true;
        }
        if (this.isChild() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getPosXRandom(1.0), this.getPosYRandom() + 0.5, this.getPosZRandom(1.0), 0.0, 0.0, 0.0);
            if (!this.world.isRemote) {
                this.addGrowth(i);
            }
            flag = true;
        }
        if (j > 0 && (flag || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            flag = true;
            if (!this.world.isRemote) {
                this.increaseTemper(j);
            }
        }
        if (flag && !this.isSilent() && (soundevent = this.func_230274_fe_()) != null) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), this.func_230274_fe_(), this.getSoundCategory(), 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
        }
        return flag;
    }

    @Override
    protected boolean isMovementBlocked() {
        return this.getShouldBeDead() || this.isEatingHaystack();
    }

    @Override
    @Nullable
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        int i;
        this.setRandomStrength();
        if (spawnDataIn instanceof LlamaData) {
            i = ((LlamaData)spawnDataIn).variant;
        } else {
            i = this.rand.nextInt(4);
            spawnDataIn = new LlamaData(i);
        }
        this.setVariant(i);
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.ENTITY_LLAMA_ANGRY;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.ENTITY_LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_LLAMA_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent func_230274_fe_() {
        return SoundEvents.ENTITY_LLAMA_EAT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15f, 1.0f);
    }

    @Override
    protected void playChestEquipSound() {
        this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0f, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    public void makeMad() {
        SoundEvent soundevent = this.getAngrySound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public int getInventoryColumns() {
        return this.getStrength();
    }

    @Override
    public boolean func_230276_fq_() {
        return true;
    }

    @Override
    public boolean func_230277_fr_() {
        return !this.horseChest.getStackInSlot(1).isEmpty();
    }

    @Override
    public boolean isArmor(ItemStack stack) {
        Item item = stack.getItem();
        return ItemTags.CARPETS.contains(item);
    }

    @Override
    public boolean func_230264_L__() {
        return false;
    }

    @Override
    public void onInventoryChanged(IInventory invBasic) {
        DyeColor dyecolor = this.getColor();
        super.onInventoryChanged(invBasic);
        DyeColor dyecolor1 = this.getColor();
        if (this.ticksExisted > 20 && dyecolor1 != null && dyecolor1 != dyecolor) {
            this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5f, 1.0f);
        }
    }

    @Override
    protected void func_230275_fc_() {
        if (!this.world.isRemote) {
            super.func_230275_fc_();
            this.setColor(LlamaEntity.getCarpetColor(this.horseChest.getStackInSlot(1)));
        }
    }

    private void setColor(@Nullable DyeColor color) {
        this.dataManager.set(DATA_COLOR_ID, color == null ? -1 : color.getId());
    }

    @Nullable
    private static DyeColor getCarpetColor(ItemStack p_195403_0_) {
        Block block = Block.getBlockFromItem(p_195403_0_.getItem());
        return block instanceof CarpetBlock ? ((CarpetBlock)block).getColor() : null;
    }

    @Nullable
    public DyeColor getColor() {
        int i = this.dataManager.get(DATA_COLOR_ID);
        return i == -1 ? null : DyeColor.byId(i);
    }

    @Override
    public int getMaxTemper() {
        return 30;
    }

    @Override
    public boolean canMateWith(AnimalEntity otherAnimal) {
        return otherAnimal != this && otherAnimal instanceof LlamaEntity && this.canMate() && ((LlamaEntity)otherAnimal).canMate();
    }

    @Override
    public LlamaEntity func_241840_a(ServerWorld p_241840_1_, AgeableEntity p_241840_2_) {
        LlamaEntity llamaentity = this.createChild();
        this.setOffspringAttributes(p_241840_2_, llamaentity);
        LlamaEntity llamaentity1 = (LlamaEntity)p_241840_2_;
        int i = this.rand.nextInt(Math.max(this.getStrength(), llamaentity1.getStrength())) + 1;
        if (this.rand.nextFloat() < 0.03f) {
            ++i;
        }
        llamaentity.setStrength(i);
        llamaentity.setVariant(this.rand.nextBoolean() ? this.getVariant() : llamaentity1.getVariant());
        return llamaentity;
    }

    protected LlamaEntity createChild() {
        return EntityType.LLAMA.create(this.world);
    }

    private void spit(LivingEntity target) {
        LlamaSpitEntity llamaspitentity = new LlamaSpitEntity(this.world, this);
        double d0 = target.getPosX() - this.getPosX();
        double d1 = target.getPosYHeight(0.3333333333333333) - llamaspitentity.getPosY();
        double d2 = target.getPosZ() - this.getPosZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2) * 0.2f;
        llamaspitentity.shoot(d0, d1 + (double)f, d2, 1.5f, 10.0f);
        if (!this.isSilent()) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0f, 1.0f + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f);
        }
        this.world.addEntity(llamaspitentity);
        this.didSpit = true;
    }

    private void setDidSpit(boolean didSpitIn) {
        this.didSpit = didSpitIn;
    }

    @Override
    public boolean onLivingFall(float distance, float damageMultiplier) {
        int i = this.calculateFallDamage(distance, damageMultiplier);
        if (i <= 0) {
            return false;
        }
        if (distance >= 6.0f) {
            this.attackEntityFrom(DamageSource.FALL, i);
            if (this.isBeingRidden()) {
                for (Entity entity : this.getRecursivePassengers()) {
                    entity.attackEntityFrom(DamageSource.FALL, i);
                }
            }
        }
        this.playFallSound();
        return true;
    }

    public void leaveCaravan() {
        if (this.caravanHead != null) {
            this.caravanHead.caravanTail = null;
        }
        this.caravanHead = null;
    }

    public void joinCaravan(LlamaEntity caravanHeadIn) {
        this.caravanHead = caravanHeadIn;
        this.caravanHead.caravanTail = this;
    }

    public boolean hasCaravanTrail() {
        return this.caravanTail != null;
    }

    public boolean inCaravan() {
        return this.caravanHead != null;
    }

    @Nullable
    public LlamaEntity getCaravanHead() {
        return this.caravanHead;
    }

    @Override
    protected double followLeashSpeed() {
        return 2.0;
    }

    @Override
    protected void followMother() {
        if (!this.inCaravan() && this.isChild()) {
            super.followMother();
        }
    }

    @Override
    public boolean canEatGrass() {
        return false;
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        this.spit(target);
    }

    @Override
    public Vector3d func_241205_ce_() {
        return new Vector3d(0.0, 0.75 * (double)this.getEyeHeight(), (double)this.getWidth() * 0.5);
    }

    static class HurtByTargetGoal
    extends net.minecraft.entity.ai.goal.HurtByTargetGoal {
        public HurtByTargetGoal(LlamaEntity llama) {
            super(llama, new Class[0]);
        }

        @Override
        public boolean shouldContinueExecuting() {
            if (this.goalOwner instanceof LlamaEntity) {
                LlamaEntity llamaentity = (LlamaEntity)this.goalOwner;
                if (llamaentity.didSpit) {
                    llamaentity.setDidSpit(false);
                    return false;
                }
            }
            return super.shouldContinueExecuting();
        }
    }

    static class DefendTargetGoal
    extends NearestAttackableTargetGoal<WolfEntity> {
        public DefendTargetGoal(LlamaEntity llama) {
            super(llama, WolfEntity.class, 16, false, true, p_220789_0_ -> !((WolfEntity)p_220789_0_).isTamed());
        }

        @Override
        protected double getTargetDistance() {
            return super.getTargetDistance() * 0.25;
        }
    }

    static class LlamaData
    extends AgeableEntity.AgeableData {
        public final int variant;

        private LlamaData(int variantIn) {
            super(true);
            this.variant = variantIn;
        }
    }
}
