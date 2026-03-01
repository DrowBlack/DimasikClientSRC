package dimasik.helpers.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.utils.math.MathUtils;
import java.awt.Color;
import java.nio.ByteBuffer;
import lombok.Generated;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public final class ColorHelpers {
    public static int COLOR_NONE = ColorHelpers.rgbaFloat(1.0f, 1.0f, 1.0f, 1.0f);

    public static int setAlphaColor(int color, float alpha) {
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        RenderSystem.color4f(red, green, blue, alpha);
        return color;
    }

    public static float[] rgba(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
    }

    public static Color setAlpha2(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), MathHelper.clamp(alpha, 0, 255));
    }

    public static int setAlpha(int color, int alpha) {
        return MathHelper.clamp(alpha, 0, 255) << 24 | color & 0xFFFFFF;
    }

    public static void setColor(int color) {
        ColorHelpers.setAlphaColor(color, (float)(color >> 24 & 0xFF) / 255.0f);
    }

    public static float[] rgb(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
    }

    public static Color random() {
        return new Color(Color.HSBtoRGB((float)Math.random(), (float)(0.75 + Math.random() / 4.0), (float)(0.75 + Math.random() / 4.0)));
    }

    public static int getRed(int hex) {
        return hex >> 16 & 0xFF;
    }

    public static int getGreen(int hex) {
        return hex >> 8 & 0xFF;
    }

    public static int getBlue(int hex) {
        return hex & 0xFF;
    }

    public static int getAlpha(int hex) {
        return hex >> 24 & 0xFF;
    }

    public static StringTextComponent gradient(String message, int first, int end) {
        StringTextComponent text = new StringTextComponent("");
        for (int i = 0; i < message.length(); ++i) {
            text.append(new StringTextComponent(String.valueOf(message.charAt(i))).setStyle(Style.EMPTY.setColor(new net.minecraft.util.text.Color(ColorHelpers.interpolateColor(first, end, (float)i / (float)message.length())))));
        }
        return text;
    }

    public static float[] getRGBAf(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
    }

    public static int getColorWithDarkness(int color, float darkness) {
        float[] rgb = ColorHelpers.getRGBAf(color);
        return ColorHelpers.rgba((int)(rgb[0] * 255.0f / darkness), (int)(rgb[1] * 255.0f / darkness), (int)(rgb[2] * 255.0f / darkness), (int)(rgb[3] * 255.0f));
    }

    public static int getColorWithAlpha(int color, float alpha) {
        float[] rgb = ColorHelpers.getRGBAf(color);
        return ColorHelpers.rgba((int)(rgb[0] * 255.0f), (int)(rgb[1] * 255.0f), (int)(rgb[2] * 255.0f), (int)alpha);
    }

    public static int getColorWithAlpha(int color, double alpha) {
        float[] rgb = ColorHelpers.getRGBAf(color);
        return ColorHelpers.rgba((int)(rgb[0] * 255.0f), (int)(rgb[1] * 255.0f), (int)(rgb[2] * 255.0f), (int)alpha);
    }

    public static int interpolateColor(int color1, int color2, double offset) {
        float[] rgba1 = ColorHelpers.getRGBAf(color1);
        float[] rgba2 = ColorHelpers.getRGBAf(color2);
        double r = (double)rgba1[0] + (double)(rgba2[0] - rgba1[0]) * offset;
        double g = (double)rgba1[1] + (double)(rgba2[1] - rgba1[1]) * offset;
        double b = (double)rgba1[2] + (double)(rgba2[2] - rgba1[2]) * offset;
        double a = (double)rgba1[3] + (double)(rgba2[3] - rgba1[3]) * offset;
        return ColorHelpers.rgba((int)(r * 255.0), (int)(g * 255.0), (int)(b * 255.0), (int)(a * 255.0));
    }

    public static int interpolateColorsBackAndForth(int speed, int index, int start, int end) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return ColorHelpers.interpolateColor(start, end, (float)angle / 360.0f);
    }

    public static int rgba(int r, int g, int b, int a) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, float a) {
        return (int)a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgba(int r, int g, int b, double a) {
        return (int)a << 24 | r << 16 | g << 8 | b;
    }

    public static int rgb(int r, int g, int b) {
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    public static int rgbaFloat(float r, float g, float b, float a) {
        return (int)(MathUtils.clamp(a, 0.0f, 1.0f) * 255.0f) << 24 | (int)(MathUtils.clamp(r, 0.0f, 1.0f) * 255.0f) << 16 | (int)(MathUtils.clamp(g, 0.0f, 1.0f) * 255.0f) << 8 | (int)(MathUtils.clamp(b, 0.0f, 1.0f) * 255.0f);
    }

    public static int rgbFloat(float r, float g, float b) {
        return 0xFF000000 | (int)(MathUtils.clamp(r, 0.0f, 1.0f) * 255.0f) << 16 | (int)(MathUtils.clamp(g, 0.0f, 1.0f) * 255.0f) << 8 | (int)(MathUtils.clamp(b, 0.0f, 1.0f) * 255.0f);
    }

    public static String RGBtoHEXString(int color) {
        return Integer.toHexString(color).substring(2);
    }

    public static int getColorFromPixel(int x, int y) {
        ByteBuffer rgb = BufferUtils.createByteBuffer(3);
        GL11.glReadPixels(x, y, 1, 1, 6407, 5121, rgb);
        return ColorHelpers.rgb(rgb.get(0) & 0xFF, rgb.get(1) & 0xFF, rgb.get(2) & 0xFF);
    }

    public static int HUEtoRGB(int value) {
        float hue = (float)value / 360.0f;
        return Color.HSBtoRGB(hue, 1.0f, 1.0f);
    }

    public static int HSBtoRGB(float hue, float saturation, float brightness) {
        int r = 0;
        int g = 0;
        int b = 0;
        if (saturation == 0.0f) {
            g = b = (int)(brightness * 255.0f + 0.5f);
            r = b;
        } else {
            float h = (hue - (float)Math.floor(hue)) * 6.0f;
            float f = h - (float)Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - saturation * (1.0f - f));
            switch ((int)h) {
                case 0: {
                    r = (int)(brightness * 255.0f + 0.5f);
                    g = (int)(t * 255.0f + 0.5f);
                    b = (int)(p * 255.0f + 0.5f);
                    break;
                }
                case 1: {
                    r = (int)(q * 255.0f + 0.5f);
                    g = (int)(brightness * 255.0f + 0.5f);
                    b = (int)(p * 255.0f + 0.5f);
                    break;
                }
                case 2: {
                    r = (int)(p * 255.0f + 0.5f);
                    g = (int)(brightness * 255.0f + 0.5f);
                    b = (int)(t * 255.0f + 0.5f);
                    break;
                }
                case 3: {
                    r = (int)(p * 255.0f + 0.5f);
                    g = (int)(q * 255.0f + 0.5f);
                    b = (int)(brightness * 255.0f + 0.5f);
                    break;
                }
                case 4: {
                    r = (int)(t * 255.0f + 0.5f);
                    g = (int)(p * 255.0f + 0.5f);
                    b = (int)(brightness * 255.0f + 0.5f);
                    break;
                }
                case 5: {
                    r = (int)(brightness * 255.0f + 0.5f);
                    g = (int)(p * 255.0f + 0.5f);
                    b = (int)(q * 255.0f + 0.5f);
                }
            }
        }
        return 0xFF000000 | r << 16 | g << 8 | b;
    }

    public static void glHexColor(int hex, int alpha) {
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        RenderSystem.color4f(red, green, blue, (float)alpha / 255.0f);
    }

    public static void glHexColor(int hex, float alpha) {
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        RenderSystem.color4f(red, green, blue, alpha);
    }

    public static void glHexColor(int color) {
        ColorHelpers.glHexColor(color, (float)(color >> 24 & 0xFF) / 255.0f);
    }

    public static float getSkyRainbow(float speed, int index) {
        boolean n = false;
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        float hue = (float)angle / 360.0f;
        angle = (int)((double)angle % 360.0);
        return Color.HSBtoRGB((double)((float)((double)n / 360.0)) < 0.5 ? -((float)((double)angle / 360.0)) : (float)((double)angle / 360.0), 1.0f, 1.0f);
    }

    public static int astolfo(float yDist, float yTotal, float saturation, float speedt) {
        float hue;
        float speed = 1800.0f;
        for (hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (yTotal - yDist) * speedt; hue > speed; hue -= speed) {
        }
        if ((double)(hue /= speed) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.HSBtoRGB(hue += 0.5f, saturation, 1.0f);
    }

    public static int getThemeColor(int color) {
        if (color > Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors.length) {
            return Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors[Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors.length - 1];
        }
        return Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors[color - 1];
    }

    public static Color getThemeColor2(int colorIndex) {
        int[] themeColors = Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().colors;
        if (colorIndex <= 0) {
            return new Color(themeColors[0]);
        }
        if (colorIndex > themeColors.length) {
            return new Color(themeColors[themeColors.length - 1]);
        }
        return new Color(themeColors[colorIndex - 1]);
    }

    public static int getTheme(int index) {
        return Load.getInstance().getHooks().getThemeManagers().getCurrentTheme().getColor(index);
    }

    public static int getColorStyle(float hue) {
        return Color.HSBtoRGB(hue / 360.0f, 0.8f, 1.0f);
    }

    public static int hexToRgb(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() != 6) {
            throw new IllegalArgumentException("\u041d\u0435\u0434\u043e\u043f\u0443\u0441\u0442\u0438\u043c\u044b\u0439 \u0444\u043e\u0440\u043c\u0430\u0442 HEX: " + hex);
        }
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        return ColorHelpers.rgb(r, g, b);
    }

    public static int gradient(int speed, int index, int ... colors) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int colorIndex = (int)((float)angle / 360.0f * (float)colors.length);
        if (colorIndex == colors.length) {
            --colorIndex;
        }
        int color1 = colors[colorIndex];
        int color2 = colors[colorIndex == colors.length - 1 ? 0 : colorIndex + 1];
        return ColorHelpers.interpolateColor(color1, color2, (float)angle / 360.0f * (float)colors.length - (float)colorIndex);
    }

    public static int overCol(int color1, int color2, float percent01) {
        float percent = MathHelper.clamp(percent01, 0.0f, 1.0f);
        return ColorHelpers.rgba((int)MathUtils.lerp(ColorHelpers.getRed(color1), ColorHelpers.getRed(color2), percent), (int)MathUtils.lerp(ColorHelpers.getGreen(color1), ColorHelpers.getGreen(color2), percent), (int)MathUtils.lerp(ColorHelpers.getBlue(color1), ColorHelpers.getBlue(color2), percent), (int)MathUtils.lerp(ColorHelpers.getAlpha(color1), ColorHelpers.getAlpha(color2), percent));
    }

    public static int overCol(int color1, int color2) {
        return ColorHelpers.overCol(color1, color2, 0.5f);
    }

    @Generated
    private ColorHelpers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static class IntColor {
        public static float[] rgb(int color) {
            return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
        }

        public static int rgba(int r, int g, int b, int a) {
            return a << 24 | r << 16 | g << 8 | b;
        }

        public static int getRed(int hex) {
            return hex >> 16 & 0xFF;
        }

        public static int getGreen(int hex) {
            return hex >> 8 & 0xFF;
        }

        public static int getBlue(int hex) {
            return hex & 0xFF;
        }

        public static int getAlpha(int hex) {
            return hex >> 24 & 0xFF;
        }
    }
}
