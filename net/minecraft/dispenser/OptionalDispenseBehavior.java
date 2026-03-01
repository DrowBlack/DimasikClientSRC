package net.minecraft.dispenser;

import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;

public abstract class OptionalDispenseBehavior
extends DefaultDispenseItemBehavior {
    private boolean successful = true;

    public boolean isSuccessful() {
        return this.successful;
    }

    public void setSuccessful(boolean success) {
        this.successful = success;
    }

    @Override
    protected void playDispenseSound(IBlockSource source) {
        source.getWorld().playEvent(this.isSuccessful() ? 1000 : 1001, source.getBlockPos(), 0);
    }
}
