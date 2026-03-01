package net.minecraft.entity.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class LeashKnotEntity
extends HangingEntity {
    public LeashKnotEntity(EntityType<? extends LeashKnotEntity> p_i50223_1_, World world) {
        super((EntityType<? extends HangingEntity>)p_i50223_1_, world);
    }

    public LeashKnotEntity(World worldIn, BlockPos hangingPositionIn) {
        super(EntityType.LEASH_KNOT, worldIn, hangingPositionIn);
        this.setPosition((double)hangingPositionIn.getX() + 0.5, (double)hangingPositionIn.getY() + 0.5, (double)hangingPositionIn.getZ() + 0.5);
        float f = 0.125f;
        float f1 = 0.1875f;
        float f2 = 0.25f;
        this.setBoundingBox(new AxisAlignedBB(this.getPosX() - 0.1875, this.getPosY() - 0.25 + 0.125, this.getPosZ() - 0.1875, this.getPosX() + 0.1875, this.getPosY() + 0.25 + 0.125, this.getPosZ() + 0.1875));
        this.forceSpawn = true;
    }

    @Override
    public void setPosition(double x, double y, double z) {
        super.setPosition((double)MathHelper.floor(x) + 0.5, (double)MathHelper.floor(y) + 0.5, (double)MathHelper.floor(z) + 0.5);
    }

    @Override
    protected void updateBoundingBox() {
        this.setRawPosition((double)this.hangingPosition.getX() + 0.5, (double)this.hangingPosition.getY() + 0.5, (double)this.hangingPosition.getZ() + 0.5);
    }

    @Override
    public void updateFacingWithBoundingBox(Direction facingDirectionIn) {
    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    protected float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return -0.0625f;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 1024.0;
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
    }

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (this.world.isRemote) {
            return ActionResultType.SUCCESS;
        }
        boolean flag = false;
        double d0 = 7.0;
        List<MobEntity> list = this.world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(this.getPosX() - 7.0, this.getPosY() - 7.0, this.getPosZ() - 7.0, this.getPosX() + 7.0, this.getPosY() + 7.0, this.getPosZ() + 7.0));
        for (MobEntity mobentity : list) {
            if (mobentity.getLeashHolder() != player) continue;
            mobentity.setLeashHolder(this, true);
            flag = true;
        }
        if (!flag) {
            this.remove();
            if (player.abilities.isCreativeMode) {
                for (MobEntity mobentity1 : list) {
                    if (!mobentity1.getLeashed() || mobentity1.getLeashHolder() != this) continue;
                    mobentity1.clearLeashed(true, false);
                }
            }
        }
        return ActionResultType.CONSUME;
    }

    @Override
    public boolean onValidSurface() {
        return this.world.getBlockState(this.hangingPosition).getBlock().isIn(BlockTags.FENCES);
    }

    public static LeashKnotEntity create(World world, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        for (LeashKnotEntity leashknotentity : world.getEntitiesWithinAABB(LeashKnotEntity.class, new AxisAlignedBB((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0))) {
            if (!leashknotentity.getHangingPosition().equals(pos)) continue;
            return leashknotentity;
        }
        LeashKnotEntity leashknotentity1 = new LeashKnotEntity(world, pos);
        world.addEntity(leashknotentity1);
        leashknotentity1.playPlaceSound();
        return leashknotentity1;
    }

    @Override
    public void playPlaceSound() {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0f, 1.0f);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnObjectPacket(this, this.getType(), 0, this.getHangingPosition());
    }

    @Override
    public Vector3d getLeashPosition(float partialTicks) {
        return this.func_242282_l(partialTicks).add(0.0, 0.2, 0.0);
    }
}
