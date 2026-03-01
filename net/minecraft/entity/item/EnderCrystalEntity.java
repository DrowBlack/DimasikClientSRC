package net.minecraft.entity.item;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.server.ServerWorld;

public class EnderCrystalEntity
extends Entity {
    private static final DataParameter<Optional<BlockPos>> BEAM_TARGET = EntityDataManager.createKey(EnderCrystalEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
    private static final DataParameter<Boolean> SHOW_BOTTOM = EntityDataManager.createKey(EnderCrystalEntity.class, DataSerializers.BOOLEAN);
    public int innerRotation;

    public EnderCrystalEntity(EntityType<? extends EnderCrystalEntity> p_i50231_1_, World world) {
        super(p_i50231_1_, world);
        this.preventEntitySpawning = true;
        this.innerRotation = this.rand.nextInt(100000);
    }

    public EnderCrystalEntity(World worldIn, double x, double y, double z) {
        this((EntityType<? extends EnderCrystalEntity>)EntityType.END_CRYSTAL, worldIn);
        this.setPosition(x, y, z);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void registerData() {
        this.getDataManager().register(BEAM_TARGET, Optional.empty());
        this.getDataManager().register(SHOW_BOTTOM, true);
    }

    @Override
    public void tick() {
        ++this.innerRotation;
        if (this.world instanceof ServerWorld) {
            BlockPos blockpos = this.getPosition();
            if (((ServerWorld)this.world).func_241110_C_() != null && this.world.getBlockState(blockpos).isAir()) {
                this.world.setBlockState(blockpos, AbstractFireBlock.getFireForPlacement(this.world, blockpos));
            }
        }
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.getBeamTarget() != null) {
            compound.put("BeamTarget", NBTUtil.writeBlockPos(this.getBeamTarget()));
        }
        compound.putBoolean("ShowBottom", this.shouldShowBottom());
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.contains("BeamTarget", 10)) {
            this.setBeamTarget(NBTUtil.readBlockPos(compound.getCompound("BeamTarget")));
        }
        if (compound.contains("ShowBottom", 1)) {
            this.setShowBottom(compound.getBoolean("ShowBottom"));
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (source.getTrueSource() instanceof EnderDragonEntity) {
            return false;
        }
        if (!this.removed && !this.world.isRemote) {
            this.remove();
            if (!source.isExplosion()) {
                this.world.createExplosion(null, this.getPosX(), this.getPosY(), this.getPosZ(), 6.0f, Explosion.Mode.DESTROY);
            }
            this.onCrystalDestroyed(source);
        }
        return true;
    }

    @Override
    public void onKillCommand() {
        this.onCrystalDestroyed(DamageSource.GENERIC);
        super.onKillCommand();
    }

    private void onCrystalDestroyed(DamageSource source) {
        DragonFightManager dragonfightmanager;
        if (this.world instanceof ServerWorld && (dragonfightmanager = ((ServerWorld)this.world).func_241110_C_()) != null) {
            dragonfightmanager.onCrystalDestroyed(this, source);
        }
    }

    public void setBeamTarget(@Nullable BlockPos beamTarget) {
        this.getDataManager().set(BEAM_TARGET, Optional.ofNullable(beamTarget));
    }

    @Nullable
    public BlockPos getBeamTarget() {
        return this.getDataManager().get(BEAM_TARGET).orElse(null);
    }

    public void setShowBottom(boolean showBottom) {
        this.getDataManager().set(SHOW_BOTTOM, showBottom);
    }

    public boolean shouldShowBottom() {
        return this.getDataManager().get(SHOW_BOTTOM);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return super.isInRangeToRenderDist(distance) || this.getBeamTarget() != null;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this);
    }
}
