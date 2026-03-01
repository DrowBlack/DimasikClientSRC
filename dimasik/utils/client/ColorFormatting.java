package dimasik.utils.client;

import dimasik.helpers.render.ColorHelpers;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;

public final class ColorFormatting {
    public static Pattern PATTERN = Pattern.compile("\\$\\{(rgba|rgb)\\((\\d{1,3}),(\\d{1,3}),(\\d{1,3})(?:,(\\d{1,3}))?\\)}|\\$\\{reset}", 2);

    public static String getColor(int red, int green, int blue) {
        return String.format("${rgb(%s,%s,%s)}", red, green, blue);
    }

    public static String getColor(int red, int green, int blue, int alpha) {
        return String.format("${rgba(%s,%s,%s,%s)}", red, green, blue, alpha);
    }

    public static String getColor(int color) {
        return String.format("${rgba(%s,%s,%s,%s)}", ColorHelpers.getRed(color), ColorHelpers.getGreen(color), ColorHelpers.getBlue(color), ColorHelpers.getAlpha(color));
    }

    public static String reset() {
        return "${reset}";
    }

    public static String removeFormatting(String text) {
        return PATTERN.matcher(text).replaceAll("");
    }

    public static String typeRGB() {
        return "rgb";
    }

    public static String typeRGBA() {
        return "rgba";
    }

    public static String replaceColor(String text, int red, int green, int blue) {
        return PATTERN.matcher(text).replaceAll(Matcher.quoteReplacement(ColorFormatting.getColor(red, green, blue)));
    }

    public static String replaceColor(String text, int red, int green, int blue, int alpha) {
        return PATTERN.matcher(text).replaceAll(Matcher.quoteReplacement(ColorFormatting.getColor(red, green, blue, alpha)));
    }

    @Generated
    private ColorFormatting() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
