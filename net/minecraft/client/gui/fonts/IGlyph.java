package net.minecraft.client.gui.fonts;

public interface IGlyph {
    public float getAdvance();

    default public float getAdvance(boolean p_223274_1_) {
        return this.getAdvance() + (p_223274_1_ ? this.getBoldOffset() : 0.0f);
    }

    default public float getBearingX() {
        return 0.0f;
    }

    default public float getBoldOffset() {
        return 1.0f;
    }

    default public float getShadowOffset() {
        return 1.0f;
    }
}
