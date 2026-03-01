package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ShulkerBulletEntity
extends ProjectileEntity {
    private Entity target;
    @Nullable
    private Direction direction;
    private int steps;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    @Nullable
    private UUID targetUniqueId;

    public ShulkerBulletEntity(EntityType<? extends ShulkerBulletEntity> p_i50161_1_, World p_i50161_2_) {
        super((EntityType<? extends ProjectileEntity>)p_i50161_1_, p_i50161_2_);
        this.noClip = true;
    }

    public ShulkerBulletEntity(World worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn) {
        this((EntityType<? extends ShulkerBulletEntity>)EntityType.SHULKER_BULLET, worldIn);
        this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
        this.setMotion(motionXIn, motionYIn, motionZIn);
    }

    public ShulkerBulletEntity(World worldIn, LivingEntity ownerIn, Entity targetIn, Direction.Axis p_i46772_4_) {
        this((EntityType<? extends ShulkerBulletEntity>)EntityType.SHULKER_BULLET, worldIn);
        this.setShooter(ownerIn);
        BlockPos blockpos = ownerIn.getPosition();
        double d0 = (double)blockpos.getX() + 0.5;
        double d1 = (double)blockpos.getY() + 0.5;
        double d2 = (double)blockpos.getZ() + 0.5;
        this.setLocationAndAngles(d0, d1, d2, this.rotationYaw, this.rotationPitch);
        this.target = targetIn;
        this.direction = Direction.UP;
        this.selectNextMoveDirection(p_i46772_4_);
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        if (this.target != null) {
            compound.putUniqueId("Target", this.target.getUniqueID());
        }
        if (this.direction != null) {
            compound.putInt("Dir", this.direction.getIndex());
        }
        compound.putInt("Steps", this.steps);
        compound.putDouble("TXD", this.targetDeltaX);
        compound.putDouble("TYD", this.targetDeltaY);
        compound.putDouble("TZD", this.targetDeltaZ);
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.steps = compound.getInt("Steps");
        this.targetDeltaX = compound.getDouble("TXD");
        this.targetDeltaY = compound.getDouble("TYD");
        this.targetDeltaZ = compound.getDouble("TZD");
        if (compound.contains("Dir", 99)) {
            this.direction = Direction.byIndex(compound.getInt("Dir"));
        }
        if (compound.hasUniqueId("Target")) {
            this.targetUniqueId = compound.getUniqueId("Target");
        }
    }

    @Override
    protected void registerData() {
    }

    private void setDirection(@Nullable Direction directionIn) {
        this.direction = directionIn;
    }

    private void selectNextMoveDirection(@Nullable Direction.Axis p_184569_1_) {
        BlockPos blockpos;
        double d0 = 0.5;
        if (this.target == null) {
            blockpos = this.getPosition().down();
        } else {
            d0 = (double)this.target.getHeight() * 0.5;
            blockpos = new BlockPos(this.target.getPosX(), this.target.getPosY() + d0, this.target.getPosZ());
        }
        double d1 = (double)blockpos.getX() + 0.5;
        double d2 = (double)blockpos.getY() + d0;
        double d3 = (double)blockpos.getZ() + 0.5;
        Direction direction = null;
        if (!blockpos.withinDistance(this.getPositionVec(), 2.0)) {
            BlockPos blockpos1 = this.getPosition();
            ArrayList<Direction> list = Lists.newArrayList();
            if (p_184569_1_ != Direction.Axis.X) {
                if (blockpos1.getX() < blockpos.getX() && this.world.isAirBlock(blockpos1.east())) {
                    list.add(Direction.EAST);
                } else if (blockpos1.getX() > blockpos.getX() && this.world.isAirBlock(blockpos1.west())) {
                    list.add(Direction.WEST);
                }
            }
            if (p_184569_1_ != Direction.Axis.Y) {
                if (blockpos1.getY() < blockpos.getY() && this.world.isAirBlock(blockpos1.up())) {
                    list.add(Direction.UP);
                } else if (blockpos1.getY() > blockpos.getY() && this.world.isAirBlock(blockpos1.down())) {
                    list.add(Direction.DOWN);
                }
            }
            if (p_184569_1_ != Direction.Axis.Z) {
                if (blockpos1.getZ() < blockpos.getZ() && this.world.isAirBlock(blockpos1.south())) {
                    list.add(Direction.SOUTH);
                } else if (blockpos1.getZ() > blockpos.getZ() && this.world.isAirBlock(blockpos1.north())) {
                    list.add(Direction.NORTH);
                }
            }
            direction = Direction.getRandomDirection(this.rand);
            if (list.isEmpty()) {
                for (int i = 5; !this.world.isAirBlock(blockpos1.offset(direction)) && i > 0; --i) {
                    direction = Direction.getRandomDirection(this.rand);
                }
            } else {
                direction = (Direction)list.get(this.rand.nextInt(list.size()));
            }
            d1 = this.getPosX() + (double)direction.getXOffset();
            d2 = this.getPosY() + (double)direction.getYOffset();
            d3 = this.getPosZ() + (double)direction.getZOffset();
        }
        this.setDirection(direction);
        double d6 = d1 - this.getPosX();
        double d7 = d2 - this.getPosY();
        double d4 = d3 - this.getPosZ();
        double d5 = MathHelper.sqrt(d6 * d6 + d7 * d7 + d4 * d4);
        if (d5 == 0.0) {
            this.targetDeltaX = 0.0;
            this.targetDeltaY = 0.0;
            this.targetDeltaZ = 0.0;
        } else {
            this.targetDeltaX = d6 / d5 * 0.15;
            this.targetDeltaY = d7 / d5 * 0.15;
            this.targetDeltaZ = d4 / d5 * 0.15;
        }
        this.isAirBorne = true;
        this.steps = 10 + this.rand.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL) {
            this.remove();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
            RayTraceResult raytraceresult;
            if (this.target == null && this.targetUniqueId != null) {
                this.target = ((ServerWorld)this.world).getEntityByUuid(this.targetUniqueId);
                if (this.target == null) {
                    this.targetUniqueId = null;
                }
            }
            if (this.target == null || !this.target.isAlive() || this.target instanceof PlayerEntity && ((PlayerEntity)this.target).isSpectator()) {
                if (!this.hasNoGravity()) {
                    this.setMotion(this.getMotion().add(0.0, -0.04, 0.0));
                }
            } else {
                this.targetDeltaX = MathHelper.clamp(this.targetDeltaX * 1.025, -1.0, 1.0);
                this.targetDeltaY = MathHelper.clamp(this.targetDeltaY * 1.025, -1.0, 1.0);
                this.targetDeltaZ = MathHelper.clamp(this.targetDeltaZ * 1.025, -1.0, 1.0);
                Vector3d vector3d = this.getMotion();
                this.setMotion(vector3d.add((this.targetDeltaX - vector3d.x) * 0.2, (this.targetDeltaY - vector3d.y) * 0.2, (this.targetDeltaZ - vector3d.z) * 0.2));
            }
            if ((raytraceresult = ProjectileHelper.func_234618_a_(this, this::func_230298_a_)).getType() != RayTraceResult.Type.MISS) {
                this.onImpact(raytraceresult);
            }
        }
        this.doBlockCollisions();
        Vector3d vector3d1 = this.getMotion();
        this.setPosition(this.getPosX() + vector3d1.x, this.getPosY() + vector3d1.y, this.getPosZ() + vector3d1.z);
        ProjectileHelper.rotateTowardsMovement(this, 0.5f);
        if (this.world.isRemote) {
            this.world.addParticle(ParticleTypes.END_ROD, this.getPosX() - vector3d1.x, this.getPosY() - vector3d1.y + 0.15, this.getPosZ() - vector3d1.z, 0.0, 0.0, 0.0);
        } else if (this.target != null && !this.target.removed) {
            if (this.steps > 0) {
                --this.steps;
                if (this.steps == 0) {
                    this.selectNextMoveDirection(this.direction == null ? null : this.direction.getAxis());
                }
            }
            if (this.direction != null) {
                BlockPos blockpos = this.getPosition();
                Direction.Axis direction$axis = this.direction.getAxis();
                if (this.world.isTopSolid(blockpos.offset(this.direction), this)) {
                    this.selectNextMoveDirection(direction$axis);
                } else {
                    BlockPos blockpos1 = this.target.getPosition();
                    if (direction$axis == Direction.Axis.X && blockpos.getX() == blockpos1.getX() || direction$axis == Direction.Axis.Z && blockpos.getZ() == blockpos1.getZ() || direction$axis == Direction.Axis.Y && blockpos.getY() == blockpos1.getY()) {
                        this.selectNextMoveDirection(direction$axis);
                    }
                }
            }
        }
    }

    @Override
    protected boolean func_230298_a_(Entity p_230298_1_) {
        return super.func_230298_a_(p_230298_1_) && !p_230298_1_.noClip;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 16384.0;
    }

    @Override
    public float getBrightness() {
        return 1.0f;
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult p_213868_1_) {
        super.onEntityHit(p_213868_1_);
        Entity entity = p_213868_1_.getEntity();
        Entity entity1 = this.func_234616_v_();
        LivingEntity livingentity = entity1 instanceof LivingEntity ? (LivingEntity)entity1 : null;
        boolean flag = entity.attackEntityFrom(DamageSource.causeIndirectDamage(this, livingentity).setProjectile(), 4.0f);
        if (flag) {
            this.applyEnchantments(livingentity, entity);
            if (entity instanceof LivingEntity) {
                ((LivingEntity)entity).addPotionEffect(new EffectInstance(Effects.LEVITATION, 200));
            }
        }
    }

    @Override
    protected void func_230299_a_(BlockRayTraceResult p_230299_1_) {
        super.func_230299_a_(p_230299_1_);
        ((ServerWorld)this.world).spawnParticle(ParticleTypes.EXPLOSION, this.getPosX(), this.getPosY(), this.getPosZ(), 2, 0.2, 0.2, 0.2, 0.0);
        this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HIT, 1.0f, 1.0f);
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        super.onImpact(result);
        this.remove();
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (!this.world.isRemote) {
            this.playSound(SoundEvents.ENTITY_SHULKER_BULLET_HURT, 1.0f, 1.0f);
            ((ServerWorld)this.world).spawnParticle(ParticleTypes.CRIT, this.getPosX(), this.getPosY(), this.getPosZ(), 15, 0.2, 0.2, 0.2, 0.0);
            this.remove();
        }
        return true;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
