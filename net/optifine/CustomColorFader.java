package net.optifine;

import net.minecraft.util.math.vector.Vector3d;
import net.optifine.Config;

public class CustomColorFader {
    private Vector3d color = null;
    private long timeUpdate = System.currentTimeMillis();

    public Vector3d getColor(double x, double y, double z) {
        if (this.color == null) {
            this.color = new Vector3d(x, y, z);
            return this.color;
        }
        long i = System.currentTimeMillis();
        long j = i - this.timeUpdate;
        if (j == 0L) {
            return this.color;
        }
        this.timeUpdate = i;
        if (Math.abs(x - this.color.x) < 0.004 && Math.abs(y - this.color.y) < 0.004 && Math.abs(z - this.color.z) < 0.004) {
            return this.color;
        }
        double d0 = (double)j * 0.001;
        d0 = Config.limit(d0, 0.0, 1.0);
        double d1 = x - this.color.x;
        double d2 = y - this.color.y;
        double d3 = z - this.color.z;
        double d4 = this.color.x + d1 * d0;
        double d5 = this.color.y + d2 * d0;
        double d6 = this.color.z + d3 * d0;
        this.color = new Vector3d(d4, d5, d6);
        return this.color;
    }
}
