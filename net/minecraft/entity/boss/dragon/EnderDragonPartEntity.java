package net.minecraft.entity.boss.dragon;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;

public class EnderDragonPartEntity
extends Entity {
    public final EnderDragonEntity dragon;
    public final String field_213853_c;
    private final EntitySize field_213854_d;

    public EnderDragonPartEntity(EnderDragonEntity dragon, String p_i50232_2_, float p_i50232_3_, float p_i50232_4_) {
        super(dragon.getType(), dragon.world);
        this.field_213854_d = EntitySize.flexible(p_i50232_3_, p_i50232_4_);
        this.recalculateSize();
        this.dragon = dragon;
        this.field_213853_c = p_i50232_2_;
    }

    @Override
    protected void registerData() {
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return this.isInvulnerableTo(source) ? false : this.dragon.attackEntityPartFrom(this, source, amount);
    }

    @Override
    public boolean isEntityEqual(Entity entityIn) {
        return this == entityIn || this.dragon == entityIn;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EntitySize getSize(Pose poseIn) {
        return this.field_213854_d;
    }
}
