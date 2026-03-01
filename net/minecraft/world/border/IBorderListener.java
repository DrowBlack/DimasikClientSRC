package net.minecraft.world.border;

import net.minecraft.world.border.WorldBorder;

public interface IBorderListener {
    public void onSizeChanged(WorldBorder var1, double var2);

    public void onTransitionStarted(WorldBorder var1, double var2, double var4, long var6);

    public void onCenterChanged(WorldBorder var1, double var2, double var4);

    public void onWarningTimeChanged(WorldBorder var1, int var2);

    public void onWarningDistanceChanged(WorldBorder var1, int var2);

    public void onDamageAmountChanged(WorldBorder var1, double var2);

    public void onDamageBufferChanged(WorldBorder var1, double var2);

    public static class Impl
    implements IBorderListener {
        private final WorldBorder worldBorder;

        public Impl(WorldBorder border) {
            this.worldBorder = border;
        }

        @Override
        public void onSizeChanged(WorldBorder border, double newSize) {
            this.worldBorder.setTransition(newSize);
        }

        @Override
        public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            this.worldBorder.setTransition(oldSize, newSize, time);
        }

        @Override
        public void onCenterChanged(WorldBorder border, double x, double z) {
            this.worldBorder.setCenter(x, z);
        }

        @Override
        public void onWarningTimeChanged(WorldBorder border, int newTime) {
            this.worldBorder.setWarningTime(newTime);
        }

        @Override
        public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
            this.worldBorder.setWarningDistance(newDistance);
        }

        @Override
        public void onDamageAmountChanged(WorldBorder border, double newAmount) {
            this.worldBorder.setDamagePerBlock(newAmount);
        }

        @Override
        public void onDamageBufferChanged(WorldBorder border, double newSize) {
            this.worldBorder.setDamageBuffer(newSize);
        }
    }
}
