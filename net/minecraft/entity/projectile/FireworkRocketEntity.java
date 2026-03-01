package net.minecraft.entity.projectile;

import dimasik.Load;
import dimasik.events.main.player.EventElytra;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.client.ClientManagers;
import dimasik.utils.math.MathUtils;
import java.lang.invoke.LambdaMetafactory;
import java.util.OptionalInt;
import java.util.function.IntConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FireworkRocketEntity
extends ProjectileEntity
implements IRendersAsItem,
IFastAccess {
    private static final DataParameter<ItemStack> FIREWORK_ITEM = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.ITEMSTACK);
    private static final DataParameter<OptionalInt> BOOSTED_ENTITY_ID = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.OPTIONAL_VARINT);
    private static final DataParameter<Boolean> field_213895_d = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.BOOLEAN);
    private int fireworkAge;
    private int lifetime;
    private LivingEntity boostedEntity;
    public static boolean flying;

    public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_i50164_1_, World p_i50164_2_) {
        super((EntityType<? extends ProjectileEntity>)p_i50164_1_, p_i50164_2_);
    }

    public FireworkRocketEntity(World worldIn, double x, double y, double z, ItemStack givenItem) {
        super((EntityType<? extends ProjectileEntity>)EntityType.FIREWORK_ROCKET, worldIn);
        this.fireworkAge = 0;
        this.setPosition(x, y, z);
        int i = 1;
        if (!givenItem.isEmpty() && givenItem.hasTag()) {
            this.dataManager.set(FIREWORK_ITEM, givenItem.copy());
            i += givenItem.getOrCreateChildTag("Fireworks").getByte("Flight");
        }
        this.setMotion(this.rand.nextGaussian() * 0.001, 0.05, this.rand.nextGaussian() * 0.001);
        this.lifetime = 10 * i + this.rand.nextInt(6) + this.rand.nextInt(7);
    }

    public FireworkRocketEntity(World p_i231581_1_, @Nullable Entity p_i231581_2_, double p_i231581_3_, double p_i231581_5_, double p_i231581_7_, ItemStack p_i231581_9_) {
        this(p_i231581_1_, p_i231581_3_, p_i231581_5_, p_i231581_7_, p_i231581_9_);
        this.setShooter(p_i231581_2_);
    }

    public FireworkRocketEntity(World p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
        this(p_i47367_1_, p_i47367_3_, p_i47367_3_.getPosX(), p_i47367_3_.getPosY(), p_i47367_3_.getPosZ(), p_i47367_2_);
        this.dataManager.set(BOOSTED_ENTITY_ID, OptionalInt.of(p_i47367_3_.getEntityId()));
        this.boostedEntity = p_i47367_3_;
    }

    public FireworkRocketEntity(World p_i50165_1_, ItemStack p_i50165_2_, double p_i50165_3_, double p_i50165_5_, double p_i50165_7_, boolean p_i50165_9_) {
        this(p_i50165_1_, p_i50165_3_, p_i50165_5_, p_i50165_7_, p_i50165_2_);
        this.dataManager.set(field_213895_d, p_i50165_9_);
    }

    public FireworkRocketEntity(World p_i231582_1_, ItemStack p_i231582_2_, Entity p_i231582_3_, double p_i231582_4_, double p_i231582_6_, double p_i231582_8_, boolean p_i231582_10_) {
        this(p_i231582_1_, p_i231582_2_, p_i231582_4_, p_i231582_6_, p_i231582_8_, p_i231582_10_);
        this.setShooter(p_i231582_3_);
    }

    @Override
    protected void registerData() {
        this.dataManager.register(FIREWORK_ITEM, ItemStack.EMPTY);
        this.dataManager.register(BOOSTED_ENTITY_ID, OptionalInt.empty());
        this.dataManager.register(field_213895_d, false);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0 && !this.isAttachedToEntity();
    }

    @Override
    public boolean isInRangeToRender3d(double x, double y, double z) {
        return super.isInRangeToRender3d(x, y, z) && !this.isAttachedToEntity();
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void tick() {
        block34: {
            block30: {
                block32: {
                    block33: {
                        block31: {
                            super.tick();
                            FireworkRocketEntity.flying = true;
                            ldada = false;
                            if (!this.isAttachedToEntity()) break block30;
                            if (this.boostedEntity == null) {
                                this.dataManager.get(FireworkRocketEntity.BOOSTED_ENTITY_ID).ifPresent((IntConsumer)LambdaMetafactory.metafactory(null, null, null, (I)V, lambda$tick$0(int ), (I)V)((FireworkRocketEntity)this));
                            }
                            elytraTarget = Load.getInstance().getHooks().getModuleManagers().getElytraTarget();
                            aura = Load.getInstance().getHooks().getModuleManagers().getAura();
                            elytrahelper = Load.getInstance().getHooks().getModuleManagers().getElytraSwap();
                            fly = Load.getInstance().getHooks().getModuleManagers().getFly();
                            if (aura.isToggled() && aura.selfRotation != null) {
                                killauraY1 = aura.selfRotation.y;
                                killauraX1 = MathHelper.wrapDegrees(aura.selfRotation.x);
                            } else {
                                killauraY1 = FireworkRocketEntity.mc.player.rotationPitch;
                                killauraX1 = MathHelper.wrapDegrees(FireworkRocketEntity.mc.player.rotationYaw);
                            }
                            if (aura.getTarget() == null || !fly.isToggled()) break block31;
                            xz = 0.1f + ((Float)elytrahelper.boostx.getValue()).floatValue();
                            y = aura.attacksave ? -0.0 : -0.20000000298023224;
                            break block32;
                        }
                        if (aura.getTarget() == null || !fly.isToggled() || !(FireworkRocketEntity.mc.player.getDistance(aura.getTarget()) < ((Float)aura.getDistance().getValue()).floatValue())) break block33;
                        xz = -0.4000000059604645;
                        y = -0.4000000059604645;
                        break block32;
                    }
                    if (!elytrahelper.isToggled() || !((Boolean)elytrahelper.booster.getValue()).booleanValue() || !((Boolean)elytrahelper.boosterTest.getValue()).booleanValue()) ** GOTO lbl-1000
                    v0 = (Boolean)elytrahelper.boosterTest.getValue() != false ? -26 : -20;
                    if (!(killauraY1 <= (float)v0)) ** GOTO lbl-1000
                    v1 = (Boolean)elytrahelper.boosterTest.getValue() != false ? -50 : -54;
                    if (killauraY1 >= (float)v1 && FireworkRocketEntity.mc.player.isElytraFlying()) {
                        v2 = (Boolean)elytrahelper.sponsorBoost.getValue() != false ? (ClientManagers.isConnect44BPSServer() != false || ClientManagers.isConnect50BPSServer() != false || ClientManagers.isConnect46BPSServer() != false ? 0.347 : 0.4000000059604645) + (double)elytraTarget.speedtop : (xz = 0.31 + (double)elytraTarget.speedtop);
                        y = (Boolean)elytrahelper.sponsorBoost.getValue() != false ? (ClientManagers.isConnect44BPSServer() != false || ClientManagers.isConnect50BPSServer() != false || ClientManagers.isConnect46BPSServer() != false ? 0.347 : 0.4000000059604645) + (double)elytraTarget.speedtop : 0.31 + (double)elytraTarget.speedtop;
                        ldada = true;
                    } else if (elytrahelper.isToggled() && ((Boolean)elytrahelper.booster.getValue()).booleanValue() && ((Boolean)elytrahelper.boosterTest.getValue()).booleanValue() && killauraY1 >= 20.0f && killauraY1 <= 55.0f && FireworkRocketEntity.mc.player.isElytraFlying()) {
                        xz = (Boolean)elytrahelper.sponsorBoost.getValue() != false ? 0.3 + (double)elytraTarget.speedtop : 0.3;
                        y = (Boolean)elytrahelper.sponsorBoost.getValue() != false ? 0.3 + (double)elytraTarget.speedtop : 0.3;
                        ldada = true;
                    } else if (elytrahelper.isToggled() && ((Boolean)elytrahelper.booster.getValue()).booleanValue() && ((Boolean)elytrahelper.boosterTest.getValue()).booleanValue() && killauraY1 >= 55.0f && killauraY1 <= 85.0f && FireworkRocketEntity.mc.player.isElytraFlying()) {
                        xz = (Boolean)elytrahelper.sponsorBoost.getValue() != false ? 0.3 + (double)elytraTarget.speedtop : 0.3;
                        y = (Boolean)elytrahelper.sponsorBoost.getValue() != false ? 0.3 + (double)elytraTarget.speedtop : 0.3;
                        ldada = true;
                    } else if (elytrahelper.isToggled() && ((Boolean)elytrahelper.booster.getValue()).booleanValue()) {
                        xz = 0.1f + ((Float)elytrahelper.boostx.getValue()).floatValue();
                        y = 0.1f + ((Float)elytrahelper.boosty.getValue()).floatValue();
                    } else {
                        xz = 0.10000000149011612;
                        y = 0.10000000149011612;
                    }
                }
                if (((Boolean)elytrahelper.boosterTest.getValue()).booleanValue() && elytrahelper.isToggled() && !ldada) {
                    if (MathUtils.clump(killauraX1, -145.0, -120.0)) {
                        xz = ClientManagers.isConnect50BPSServer() != false ? 0.5f : (ClientManagers.isConnect46BPSServer() != false ? 0.42f : (ClientManagers.isConnect44BPSServer() != false ? 0.3343f : 0.23f));
                    }
                    if (MathUtils.clump(killauraX1, -60.0, -25.0)) {
                        xz = ClientManagers.isConnect50BPSServer() != false ? 0.36f : (ClientManagers.isConnect46BPSServer() != false ? 0.36f : (ClientManagers.isConnect44BPSServer() != false ? 0.3344f : 0.23f));
                    }
                    if (MathUtils.clump(killauraX1, 120.0, 144.0)) {
                        xz = ClientManagers.isConnect50BPSServer() != false ? 0.5f : (ClientManagers.isConnect46BPSServer() != false ? 0.42f : (ClientManagers.isConnect44BPSServer() != false ? 0.3343f : 0.23f));
                    }
                    if (MathUtils.clump(killauraX1, 34.0, 59.0)) {
                        xz = ClientManagers.isConnect50BPSServer() != false ? 0.36f : (ClientManagers.isConnect46BPSServer() != false ? 0.36f : (ClientManagers.isConnect44BPSServer() != false ? 0.3344f : 0.23f));
                    }
                    if (MathUtils.clump(killauraX1, 124.0, 146.0)) {
                        xz = ClientManagers.isConnect44BPSServer() != false ? 0.3343f : 0.23f;
                    }
                    if (MathUtils.clump(killauraX1, -100.0, -63.0)) {
                        xz = ClientManagers.isConnect44BPSServer() != false ? 0.16f : 0.14f;
                    }
                    if (MathUtils.clump(killauraX1, -20.0, 20.0)) {
                        xz = ClientManagers.isConnect44BPSServer() != false ? 0.16f : 0.14f;
                    }
                    if (!MathUtils.clump(killauraX1, -160.0, 160.0)) {
                        xz = ClientManagers.isConnect44BPSServer() != false ? 0.16f : 0.14f;
                    }
                    if (MathUtils.clump(killauraX1, 70.0, 110.0)) {
                        xz = ClientManagers.isConnect44BPSServer() != false ? 0.16f : 0.14f;
                    }
                    if (MathUtils.clump(killauraY1, 80.0, 91.0)) {
                        y = 0.17499999701976776;
                    }
                }
                if (Load.getInstance().getHooks().getModuleManagers().getAura().getTarget() != null && FireworkRocketEntity.mc.player.getDistanceSq(vector3d1 = Load.getInstance().getHooks().getModuleManagers().getAura().getTarget().getPositionVec().add(Load.getInstance().getHooks().getModuleManagers().getAura().getTarget().getForward().normalize().scale(5.0))) < 3.200000047683716) {
                    xz = 0.10000000149011612;
                    y = 0.10000000149011612;
                }
                if (this.boostedEntity != null) {
                    vector3d1 = this.boostedEntity.getMotion();
                    if (this.boostedEntity.isElytraFlying()) {
                        speed = 1.5f;
                        eventElytra = new EventElytra(this.boostedEntity.rotationPitch, this.boostedEntity.rotationYaw, speed, speed);
                        Load.getInstance().getEvents().call(eventElytra);
                        vector3d = this.getVectorForRotation(eventElytra.getPitch(), eventElytra.getYaw());
                        this.boostedEntity.setMotion(vector3d1.add(vector3d.x * xz + (vector3d.x * 1.5 - vector3d1.x) * 0.5, vector3d.y * y + (vector3d.y * 1.5 - vector3d1.y) * 0.5, vector3d.z * xz + (vector3d.z * 1.5 - vector3d1.z) * 0.5));
                    }
                    this.setPosition(this.boostedEntity.getPosX(), this.boostedEntity.getPosY(), this.boostedEntity.getPosZ());
                    this.setMotion(vector3d1);
                }
                break block34;
            }
            if (!this.func_213889_i()) {
                d2 = this.collidedHorizontally != false ? 1.0 : 1.15;
                this.setMotion(this.getMotion().mul(d2, 1.0, d2).add(0.0, 0.04, 0.0));
            }
            vector3d2 = this.getMotion();
            this.move(MoverType.SELF, vector3d2);
            this.setMotion(vector3d2);
        }
        raytraceresult = ProjectileHelper.func_234618_a_(this, (Predicate<Entity>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, func_230298_a_(net.minecraft.entity.Entity ), (Lnet/minecraft/entity/Entity;)Z)((FireworkRocketEntity)this));
        if (!this.noClip) {
            this.onImpact(raytraceresult);
            this.isAirBorne = true;
        }
        this.func_234617_x_();
        if (this.fireworkAge == 0 && !this.isSilent()) {
            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0f, 1.0f);
        }
        ++this.fireworkAge;
        if (this.world.isRemote && this.fireworkAge % 2 < 2) {
            this.world.addParticle(ParticleTypes.FIREWORK, this.getPosX(), this.getPosY() - 0.3, this.getPosZ(), this.rand.nextGaussian() * 0.05, -this.getMotion().y * 0.5, this.rand.nextGaussian() * 0.05);
        }
        if (!this.world.isRemote && this.fireworkAge > this.lifetime) {
            this.func_213893_k();
        }
    }

    private void func_213893_k() {
        this.world.setEntityState(this, (byte)17);
        this.dealExplosionDamage();
        this.remove();
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        if (!this.world.isRemote) {
            this.func_213893_k();
        }
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        BlockPos blockpos = new BlockPos(p_230299_1_.getPos());
        this.world.getBlockState(blockpos).onEntityCollision(this.world, blockpos, this);
        if (!this.world.isRemote() && this.func_213894_l()) {
            this.func_213893_k();
        }
        super.func_230299_a_(p_230299_1_);
    }

    private boolean func_213894_l() {
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
        ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        return listnbt != null && !listnbt.isEmpty();
    }

    private void dealExplosionDamage() {
        ListNBT listnbt;
        float f = 0.0f;
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
        ListNBT listNBT = listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
        if (listnbt != null && !listnbt.isEmpty()) {
            f = 5.0f + (float)(listnbt.size() * 2);
        }
        if (f > 0.0f) {
            if (this.boostedEntity != null) {
                this.boostedEntity.attackEntityFrom(DamageSource.func_233548_a_(this, this.func_234616_v_()), 5.0f + (float)(listnbt.size() * 2));
            }
            double d0 = 5.0;
            Vector3d vector3d = this.getPositionVec();
            for (LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(5.0))) {
                if (livingentity == this.boostedEntity || this.getDistanceSq(livingentity) > 25.0) continue;
                boolean flag = false;
                for (int i = 0; i < 2; ++i) {
                    Vector3d vector3d1 = new Vector3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5 * (double)i), livingentity.getPosZ());
                    BlockRayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vector3d, vector3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                    if (((RayTraceResult)raytraceresult).getType() != RayTraceResult.Type.MISS) continue;
                    flag = true;
                    break;
                }
                if (!flag) continue;
                float f1 = f * (float)Math.sqrt((5.0 - (double)this.getDistance(livingentity)) / 5.0);
                livingentity.attackEntityFrom(DamageSource.func_233548_a_(this, this.func_234616_v_()), f1);
            }
        }
    }

    private boolean isAttachedToEntity() {
        return this.dataManager.get(BOOSTED_ENTITY_ID).isPresent();
    }

    public boolean func_213889_i() {
        return this.dataManager.get(field_213895_d);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id == 17 && this.world.isRemote) {
            if (!this.func_213894_l()) {
                for (int i = 0; i < this.rand.nextInt(3) + 2; ++i) {
                    this.world.addParticle(ParticleTypes.POOF, this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextGaussian() * 0.05, 0.005, this.rand.nextGaussian() * 0.05);
                }
            } else {
                ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
                CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
                Vector3d vector3d = this.getMotion();
                this.world.makeFireworks(this.getPosX(), this.getPosY(), this.getPosZ(), vector3d.x, vector3d.y, vector3d.z, compoundnbt);
            }
        }
        super.handleStatusUpdate(id);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt("Life", this.fireworkAge);
        compound.putInt("LifeTime", this.lifetime);
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        if (!itemstack.isEmpty()) {
            compound.put("FireworksItem", itemstack.write(new CompoundNBT()));
        }
        compound.putBoolean("ShotAtAngle", this.dataManager.get(field_213895_d));
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.fireworkAge = compound.getInt("Life");
        this.lifetime = compound.getInt("LifeTime");
        ItemStack itemstack = ItemStack.read(compound.getCompound("FireworksItem"));
        if (!itemstack.isEmpty()) {
            this.dataManager.set(FIREWORK_ITEM, itemstack);
        }
        if (compound.contains("ShotAtAngle")) {
            this.dataManager.set(field_213895_d, compound.getBoolean("ShotAtAngle"));
        }
    }

    @Override
    public ItemStack getItem() {
        ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
        return itemstack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemstack;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }

    private /* synthetic */ void lambda$tick$0(int p_213891_1_) {
        Entity entity = this.world.getEntityByID(p_213891_1_);
        if (entity instanceof LivingEntity) {
            this.boostedEntity = (LivingEntity)entity;
        }
    }
}
