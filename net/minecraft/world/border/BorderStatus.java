package net.minecraft.world.border;

public enum BorderStatus {
    GROWING(4259712),
    SHRINKING(0xFF3030),
    STATIONARY(2138367);

    private final int color;

    private BorderStatus(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}
