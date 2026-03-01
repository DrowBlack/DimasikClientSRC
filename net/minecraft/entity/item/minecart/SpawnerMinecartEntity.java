package net.minecraft.entity.item.minecart;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;

public class SpawnerMinecartEntity
extends AbstractMinecartEntity {
    private final AbstractSpawner mobSpawnerLogic = new AbstractSpawner(){

        @Override
        public void broadcastEvent(int id) {
            SpawnerMinecartEntity.this.world.setEntityState(SpawnerMinecartEntity.this, (byte)id);
        }

        @Override
        public World getWorld() {
            return SpawnerMinecartEntity.this.world;
        }

        @Override
        public BlockPos getSpawnerPosition() {
            return SpawnerMinecartEntity.this.getPosition();
        }
    };

    public SpawnerMinecartEntity(EntityType<? extends SpawnerMinecartEntity> type, World world) {
        super(type, world);
    }

    public SpawnerMinecartEntity(World worldIn, double x, double y, double z) {
        super(EntityType.SPAWNER_MINECART, worldIn, x, y, z);
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.SPAWNER;
    }

    @Override
    public BlockState getDefaultDisplayTile() {
        return Blocks.SPAWNER.getDefaultState();
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.mobSpawnerLogic.read(compound);
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        this.mobSpawnerLogic.write(compound);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        this.mobSpawnerLogic.setDelayToMin(id);
    }

    @Override
    public void tick() {
        super.tick();
        this.mobSpawnerLogic.tick();
    }

    @Override
    public boolean ignoreItemEntityData() {
        return true;
    }
}
