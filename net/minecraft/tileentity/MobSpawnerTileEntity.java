package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.spawner.AbstractSpawner;

public class MobSpawnerTileEntity
extends TileEntity
implements ITickableTileEntity {
    private final AbstractSpawner spawnerLogic = new AbstractSpawner(){

        @Override
        public void broadcastEvent(int id) {
            MobSpawnerTileEntity.this.world.addBlockEvent(MobSpawnerTileEntity.this.pos, Blocks.SPAWNER, id, 0);
        }

        @Override
        public World getWorld() {
            return MobSpawnerTileEntity.this.world;
        }

        @Override
        public BlockPos getSpawnerPosition() {
            return MobSpawnerTileEntity.this.pos;
        }

        @Override
        public void setNextSpawnData(WeightedSpawnerEntity nextSpawnData) {
            super.setNextSpawnData(nextSpawnData);
            if (this.getWorld() != null) {
                BlockState blockstate = this.getWorld().getBlockState(this.getSpawnerPosition());
                this.getWorld().notifyBlockUpdate(MobSpawnerTileEntity.this.pos, blockstate, blockstate, 4);
            }
        }
    };

    public MobSpawnerTileEntity() {
        super(TileEntityType.MOB_SPAWNER);
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        this.spawnerLogic.read(nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        this.spawnerLogic.write(compound);
        return compound;
    }

    @Override
    public void tick() {
        this.spawnerLogic.tick();
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 1, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundnbt = this.write(new CompoundNBT());
        compoundnbt.remove("SpawnPotentials");
        return compoundnbt;
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        return this.spawnerLogic.setDelayToMin(id) ? true : super.receiveClientEvent(id, type);
    }

    @Override
    public boolean onlyOpsCanSetNbt() {
        return true;
    }

    public AbstractSpawner getSpawnerBaseLogic() {
        return this.spawnerLogic;
    }
}
