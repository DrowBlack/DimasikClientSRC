package net.minecraft.util.math.vector;

import lombok.Generated;

public class Vector2f {
    public static final Vector2f ZERO = new Vector2f(0.0f, 0.0f);
    public static final Vector2f ONE = new Vector2f(1.0f, 1.0f);
    public static final Vector2f UNIT_X = new Vector2f(1.0f, 0.0f);
    public static final Vector2f NEGATIVE_UNIT_X = new Vector2f(-1.0f, 0.0f);
    public static final Vector2f UNIT_Y = new Vector2f(0.0f, 1.0f);
    public static final Vector2f NEGATIVE_UNIT_Y = new Vector2f(0.0f, -1.0f);
    public static final Vector2f MAX = new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vector2f MIN = new Vector2f(Float.MIN_VALUE, Float.MIN_VALUE);
    public float x;
    public float y;

    public Vector2f(float xIn, float yIn) {
        this.x = xIn;
        this.y = yIn;
    }

    public boolean equals(Vector2f other) {
        return this.x == other.x && this.y == other.y;
    }

    @Generated
    public void setX(float x) {
        this.x = x;
    }

    @Generated
    public void setY(float y) {
        this.y = y;
    }
}
