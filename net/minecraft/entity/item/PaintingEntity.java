package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnPaintingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class PaintingEntity
extends HangingEntity {
    public PaintingType art;

    public PaintingEntity(EntityType<? extends PaintingEntity> type, World worldIn) {
        super((EntityType<? extends HangingEntity>)type, worldIn);
    }

    public PaintingEntity(World worldIn, BlockPos pos, Direction facing) {
        super(EntityType.PAINTING, worldIn, pos);
        ArrayList<PaintingType> list = Lists.newArrayList();
        int i = 0;
        Iterator iterator = Registry.MOTIVE.iterator();
        while (iterator.hasNext()) {
            PaintingType paintingtype;
            this.art = paintingtype = (PaintingType)iterator.next();
            this.updateFacingWithBoundingBox(facing);
            if (!this.onValidSurface()) continue;
            list.add(paintingtype);
            int j = paintingtype.getWidth() * paintingtype.getHeight();
            if (j <= i) continue;
            i = j;
        }
        if (!list.isEmpty()) {
            Iterator iterator2 = list.iterator();
            while (iterator2.hasNext()) {
                PaintingType paintingtype1 = (PaintingType)iterator2.next();
                if (paintingtype1.getWidth() * paintingtype1.getHeight() >= i) continue;
                iterator2.remove();
            }
            this.art = (PaintingType)list.get(this.rand.nextInt(list.size()));
        }
        this.updateFacingWithBoundingBox(facing);
    }

    public PaintingEntity(World worldIn, BlockPos pos, Direction facing, PaintingType artIn) {
        this(worldIn, pos, facing);
        this.art = artIn;
        this.updateFacingWithBoundingBox(facing);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        compound.putString("Motive", Registry.MOTIVE.getKey(this.art).toString());
        compound.putByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
        super.writeAdditional(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        this.art = Registry.MOTIVE.getOrDefault(ResourceLocation.tryCreate(compound.getString("Motive")));
        this.facingDirection = Direction.byHorizontalIndex(compound.getByte("Facing"));
        super.readAdditional(compound);
        this.updateFacingWithBoundingBox(this.facingDirection);
    }

    @Override
    public int getWidthPixels() {
        return this.art == null ? 1 : this.art.getWidth();
    }

    @Override
    public int getHeightPixels() {
        return this.art == null ? 1 : this.art.getHeight();
    }

    @Override
    public void onBroken(@Nullable Entity brokenEntity) {
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.playSound(SoundEvents.ENTITY_PAINTING_BREAK, 1.0f, 1.0f);
            if (brokenEntity instanceof PlayerEntity) {
                PlayerEntity playerentity = (PlayerEntity)brokenEntity;
                if (playerentity.abilities.isCreativeMode) {
                    return;
                }
            }
            this.entityDropItem(Items.PAINTING);
        }
    }

    @Override
    public void playPlaceSound() {
        this.playSound(SoundEvents.ENTITY_PAINTING_PLACE, 1.0f, 1.0f);
    }

    @Override
    public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.setPosition(x, y, z);
    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        BlockPos blockpos = this.hangingPosition.add(x - this.getPosX(), y - this.getPosY(), z - this.getPosZ());
        this.setPosition(blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return new SSpawnPaintingPacket(this);
    }
}
