package net.minecraft.client.renderer;

import net.minecraft.util.math.BlockPos;

public class DestroyBlockProgress
implements Comparable<DestroyBlockProgress> {
    private final int miningPlayerEntId;
    private final BlockPos position;
    private int partialBlockProgress;
    private int createdAtCloudUpdateTick;

    public DestroyBlockProgress(int miningPlayerEntIdIn, BlockPos positionIn) {
        this.miningPlayerEntId = miningPlayerEntIdIn;
        this.position = positionIn;
    }

    public BlockPos getPosition() {
        return this.position;
    }

    public void setPartialBlockDamage(int damage) {
        if (damage > 10) {
            damage = 10;
        }
        this.partialBlockProgress = damage;
    }

    public int getPartialBlockDamage() {
        return this.partialBlockProgress;
    }

    public void setCloudUpdateTick(int createdAtCloudUpdateTickIn) {
        this.createdAtCloudUpdateTick = createdAtCloudUpdateTickIn;
    }

    public int getCreationCloudUpdateTick() {
        return this.createdAtCloudUpdateTick;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            DestroyBlockProgress destroyblockprogress = (DestroyBlockProgress)p_equals_1_;
            return this.miningPlayerEntId == destroyblockprogress.miningPlayerEntId;
        }
        return false;
    }

    public int hashCode() {
        return Integer.hashCode(this.miningPlayerEntId);
    }

    @Override
    public int compareTo(DestroyBlockProgress p_compareTo_1_) {
        return this.partialBlockProgress != p_compareTo_1_.partialBlockProgress ? Integer.compare(this.partialBlockProgress, p_compareTo_1_.partialBlockProgress) : Integer.compare(this.miningPlayerEntId, p_compareTo_1_.miningPlayerEntId);
    }
}
