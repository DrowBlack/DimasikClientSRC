package net.minecraft.util.math.vector;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

public final class Quaternion {
    public static final Quaternion ONE = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Vector3f axis, float angle, boolean degrees) {
        if (degrees) {
            angle *= (float)Math.PI / 180;
        }
        float f = Quaternion.sin(angle / 2.0f);
        this.x = axis.getX() * f;
        this.y = axis.getY() * f;
        this.z = axis.getZ() * f;
        this.w = Quaternion.cos(angle / 2.0f);
    }

    public Quaternion(float xAngle, float yAngle, float zAngle, boolean degrees) {
        if (degrees) {
            xAngle *= (float)Math.PI / 180;
            yAngle *= (float)Math.PI / 180;
            zAngle *= (float)Math.PI / 180;
        }
        float f = Quaternion.sin(0.5f * xAngle);
        float f1 = Quaternion.cos(0.5f * xAngle);
        float f2 = Quaternion.sin(0.5f * yAngle);
        float f3 = Quaternion.cos(0.5f * yAngle);
        float f4 = Quaternion.sin(0.5f * zAngle);
        float f5 = Quaternion.cos(0.5f * zAngle);
        this.x = f * f3 * f5 + f1 * f2 * f4;
        this.y = f1 * f2 * f5 - f * f3 * f4;
        this.z = f * f2 * f5 + f1 * f3 * f4;
        this.w = f1 * f3 * f5 - f * f2 * f4;
    }

    public Quaternion(Quaternion quaternionIn) {
        this.x = quaternionIn.x;
        this.y = quaternionIn.y;
        this.z = quaternionIn.z;
        this.w = quaternionIn.w;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            Quaternion quaternion = (Quaternion)p_equals_1_;
            if (Float.compare(quaternion.x, this.x) != 0) {
                return false;
            }
            if (Float.compare(quaternion.y, this.y) != 0) {
                return false;
            }
            if (Float.compare(quaternion.z, this.z) != 0) {
                return false;
            }
            return Float.compare(quaternion.w, this.w) == 0;
        }
        return false;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        return 31 * i + Float.floatToIntBits(this.w);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("Quaternion[").append(this.getW()).append(" + ");
        stringbuilder.append(this.getX()).append("i + ");
        stringbuilder.append(this.getY()).append("j + ");
        stringbuilder.append(this.getZ()).append("k]");
        return stringbuilder.toString();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getW() {
        return this.w;
    }

    public void multiply(Quaternion quaternionIn) {
        float f = this.getX();
        float f1 = this.getY();
        float f2 = this.getZ();
        float f3 = this.getW();
        float f4 = quaternionIn.getX();
        float f5 = quaternionIn.getY();
        float f6 = quaternionIn.getZ();
        float f7 = quaternionIn.getW();
        this.x = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
        this.y = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
        this.z = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
        this.w = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
    }

    public void multiply(float valueIn) {
        this.x *= valueIn;
        this.y *= valueIn;
        this.z *= valueIn;
        this.w *= valueIn;
    }

    public void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public void set(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
        this.x = p_227066_1_;
        this.y = p_227066_2_;
        this.z = p_227066_3_;
        this.w = p_227066_4_;
    }

    private static float cos(float p_214904_0_) {
        return (float)Math.cos(p_214904_0_);
    }

    private static float sin(float p_214903_0_) {
        return (float)Math.sin(p_214903_0_);
    }

    public void normalize() {
        float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
        if (f > 1.0E-6f) {
            float f1 = MathHelper.fastInvSqrt(f);
            this.x *= f1;
            this.y *= f1;
            this.z *= f1;
            this.w *= f1;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 0.0f;
        }
    }

    public Quaternion copy() {
        return new Quaternion(this);
    }
}
