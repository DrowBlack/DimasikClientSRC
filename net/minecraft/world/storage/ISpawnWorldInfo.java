package net.minecraft.world.storage;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.IWorldInfo;

public interface ISpawnWorldInfo
extends IWorldInfo {
    public void setSpawnX(int var1);

    public void setSpawnY(int var1);

    public void setSpawnZ(int var1);

    public void setSpawnAngle(float var1);

    default public void setSpawn(BlockPos spawnPoint, float angle) {
        this.setSpawnX(spawnPoint.getX());
        this.setSpawnY(spawnPoint.getY());
        this.setSpawnZ(spawnPoint.getZ());
        this.setSpawnAngle(angle);
    }
}
