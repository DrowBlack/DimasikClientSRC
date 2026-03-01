package net.minecraft.world;

public class FoliageColors {
    private static int[] foliageBuffer = new int[65536];

    public static void setFoliageBiomeColorizer(int[] foliageBufferIn) {
        foliageBuffer = foliageBufferIn;
    }

    public static int get(double temperature, double humidity) {
        int i = (int)((1.0 - temperature) * 255.0);
        int j = (int)((1.0 - (humidity *= temperature)) * 255.0);
        return foliageBuffer[j << 8 | i];
    }

    public static int getSpruce() {
        return 0x619961;
    }

    public static int getBirch() {
        return 8431445;
    }

    public static int getDefault() {
        return 4764952;
    }
}
