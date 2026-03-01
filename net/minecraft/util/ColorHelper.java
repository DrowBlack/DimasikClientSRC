package net.minecraft.util;

public class ColorHelper {

    public static class PackedColor {
        public static int getAlpha(int packedColor) {
            return packedColor >>> 24;
        }

        public static int getRed(int packedColor) {
            return packedColor >> 16 & 0xFF;
        }

        public static int getGreen(int packedColor) {
            return packedColor >> 8 & 0xFF;
        }

        public static int getBlue(int packedColor) {
            return packedColor & 0xFF;
        }

        public static int packColor(int alpha, int red, int green, int blue) {
            return alpha << 24 | red << 16 | green << 8 | blue;
        }

        public static int blendColors(int packedColourOne, int packedColorTwo) {
            return PackedColor.packColor(PackedColor.getAlpha(packedColourOne) * PackedColor.getAlpha(packedColorTwo) / 255, PackedColor.getRed(packedColourOne) * PackedColor.getRed(packedColorTwo) / 255, PackedColor.getGreen(packedColourOne) * PackedColor.getGreen(packedColorTwo) / 255, PackedColor.getBlue(packedColourOne) * PackedColor.getBlue(packedColorTwo) / 255);
        }
    }
}
