package net.optifine;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.optifine.IRandomEntity;

public class RandomEntity
implements IRandomEntity {
    private Entity entity;

    @Override
    public int getId() {
        UUID uuid = this.entity.getUniqueID();
        long i = uuid.getLeastSignificantBits();
        return (int)(i & Integer.MAX_VALUE);
    }

    @Override
    public BlockPos getSpawnPosition() {
        return this.entity.getDataManager().spawnPosition;
    }

    @Override
    public Biome getSpawnBiome() {
        return this.entity.getDataManager().spawnBiome;
    }

    @Override
    public String getName() {
        return this.entity.hasCustomName() ? this.entity.getCustomName().getString() : null;
    }

    @Override
    public int getHealth() {
        if (!(this.entity instanceof LivingEntity)) {
            return 0;
        }
        LivingEntity livingentity = (LivingEntity)this.entity;
        return (int)livingentity.getHealth();
    }

    @Override
    public int getMaxHealth() {
        if (!(this.entity instanceof LivingEntity)) {
            return 0;
        }
        LivingEntity livingentity = (LivingEntity)this.entity;
        return (int)livingentity.getMaxHealth();
    }

    public Entity getEntity() {
        return this.entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
