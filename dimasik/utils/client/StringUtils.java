package dimasik.utils.client;

import dimasik.managers.font.Font;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

public final class StringUtils {
    private static final Map<String, TextComponent> PREFIX_MAP = StringUtils.createPrefixMap();

    public static String trim(String text, float width, Font font, float size) {
        StringBuilder trimmedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            trimmedText.append(c);
            if (!(font.getWidth(String.valueOf(trimmedText) + "...", size) > width)) continue;
            if (trimmedText.length() <= 3) break;
            trimmedText.setLength(trimmedText.length() - 3);
            if (trimmedText.charAt(trimmedText.length() - 1) == ',') {
                trimmedText.setLength(trimmedText.length() - 1);
            }
            if (trimmedText.charAt(trimmedText.length() - 1) == ' ') {
                trimmedText.setLength(trimmedText.length() - 1);
            }
            trimmedText.append("...");
            break;
        }
        return trimmedText.toString();
    }

    public static String getDonate(PlayerEntity player) {
        if (StringUtils.prefix(player.getPrefix().getString().replace(" ", "")) != null) {
            return StringUtils.prefix(player.getPrefix().getString().replace(" ", "")).getString();
        }
        return "";
    }

    public static String smallCaps(String text) {
        HashMap<Character, Character> key = new HashMap<Character, Character>();
        key.put(Character.valueOf('\u1d00'), Character.valueOf('A'));
        key.put(Character.valueOf('\u0299'), Character.valueOf('B'));
        key.put(Character.valueOf('\u1d04'), Character.valueOf('C'));
        key.put(Character.valueOf('\u1d05'), Character.valueOf('D'));
        key.put(Character.valueOf('\u1d07'), Character.valueOf('E'));
        key.put(Character.valueOf('\ua730'), Character.valueOf('F'));
        key.put(Character.valueOf('\u0262'), Character.valueOf('G'));
        key.put(Character.valueOf('\u029c'), Character.valueOf('H'));
        key.put(Character.valueOf('\u026a'), Character.valueOf('I'));
        key.put(Character.valueOf('\u1d0a'), Character.valueOf('J'));
        key.put(Character.valueOf('\u1d0b'), Character.valueOf('K'));
        key.put(Character.valueOf('\u029f'), Character.valueOf('L'));
        key.put(Character.valueOf('\u1d0d'), Character.valueOf('M'));
        key.put(Character.valueOf('\u0274'), Character.valueOf('N'));
        key.put(Character.valueOf('\u1d0f'), Character.valueOf('O'));
        key.put(Character.valueOf('\u1d18'), Character.valueOf('P'));
        key.put(Character.valueOf('\u01eb'), Character.valueOf('Q'));
        key.put(Character.valueOf('\u0280'), Character.valueOf('R'));
        key.put(Character.valueOf('\ua731'), Character.valueOf('S'));
        key.put(Character.valueOf('\u1d1b'), Character.valueOf('T'));
        key.put(Character.valueOf('\u1d1c'), Character.valueOf('U'));
        key.put(Character.valueOf('\u1d20'), Character.valueOf('V'));
        key.put(Character.valueOf('\u1d21'), Character.valueOf('W'));
        key.put(Character.valueOf('x'), Character.valueOf('X'));
        key.put(Character.valueOf('\u028f'), Character.valueOf('Y'));
        key.put(Character.valueOf('\u1d22'), Character.valueOf('Z'));
        StringBuilder builder = new StringBuilder();
        for (char c : text.toCharArray()) {
            builder.append(key.get(Character.valueOf(c)));
        }
        return builder.toString();
    }

    public static TextComponent prefix(String text) {
        return PREFIX_MAP.get(text);
    }

    public static int priority(String text) {
        if (!text.isEmpty()) {
            int i = 0;
            for (TextComponent prefix : PREFIX_MAP.values()) {
                if (prefix.getString().equals(text)) {
                    return i;
                }
                ++i;
            }
        }
        return 0;
    }

    private static Map<String, TextComponent> createPrefixMap() {
        HashMap<String, TextComponent> map = new HashMap<String, TextComponent>();
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua500\u00a77\u00a77", "PLAYER", TextFormatting.GRAY);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua504\u00a77\u00a77", "HERO", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua508\u00a77\u00a77", "TITAN", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua512\u00a77\u00a77", "AVENGER", TextFormatting.GREEN);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua516\u00a77\u00a77", "OVERLORD", TextFormatting.AQUA);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua520\u00a77\u00a77", "MAGISTER", TextFormatting.GOLD);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua524\u00a77\u00a77", "IMPERATOR", TextFormatting.RED);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua528\u00a77\u00a77", "DRAGON", TextFormatting.LIGHT_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua532\u00a77\u00a77", "BULL", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua536\u00a77\u00a77", "TIGER", TextFormatting.GOLD);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua540\u00a77\u00a77", "HYDRA", TextFormatting.DARK_GREEN);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua544\u00a77\u00a77", "DRACULA", TextFormatting.DARK_RED);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua548\u00a77\u00a77", "COBRA", TextFormatting.GREEN);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua552\u00a77\u00a77", "RABBIT", TextFormatting.WHITE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua556\u00a77\u00a77", "BUNNY", TextFormatting.BLACK);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua560\u00a77\u00a77", "D.HELPER", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua509\u00a77\u00a77", "HELPER", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua513\u00a77\u00a77", "ML.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua517\u00a77\u00a77", "MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua521\u00a77\u00a77", "MODER+", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua525\u00a77\u00a77", "ST.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua529\u00a77\u00a77", "GL.MODER", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua533\u00a77\u00a77", "ML.ADMIN", TextFormatting.AQUA);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua537\u00a77\u00a77", "ADMIN", TextFormatting.RED);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua501\u00a77\u00a77", "MEDIA", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\ua501\u00a77", "MEDIA", TextFormatting.DARK_GRAY);
        StringUtils.addPrefixes(map, "\u00a7f\ua502\u00a77", "D.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua506\u00a77", "D.MODER", TextFormatting.GRAY);
        StringUtils.addPrefixes(map, "\u00a7f\ua509\u00a77", "HELPER", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a7f\ua510\u00a77", "D.GL.MODER", TextFormatting.DARK_BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua513\u00a77", "ML.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua514\u00a77", "D.GL.MODER", TextFormatting.GRAY);
        StringUtils.addPrefixes(map, "\u00a7f\ua517\u00a77", "MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua518\u00a77", "D.ST.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua521\u00a77", "MODER+", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\ua522\u00a77", "D.ST.MODER", TextFormatting.GRAY);
        StringUtils.addPrefixes(map, "\u00a7f\ua525\u00a77", "ST.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua526\u00a77", "D.ML.ADMIN", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua528\u00a77", "DRAGON", TextFormatting.LIGHT_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\ua529\u00a77", "GL.MODER", TextFormatting.DARK_BLUE);
        StringUtils.addPrefixes(map, "\u00a7f\ua530\u00a77", "D.ML.ADMIN", TextFormatting.GRAY);
        StringUtils.addPrefixes(map, "\u00a7f\ua532\u00a77", "BULL", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\ua533\u00a77", "ML.ADMIN", TextFormatting.AQUA);
        StringUtils.addPrefixes(map, "\u00a7f\ua534\u00a77", "OWNER", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\ua536\u00a77", "TIGER", TextFormatting.GOLD);
        StringUtils.addPrefixes(map, "\u00a7f\ua537\u00a77", "ADMIN", TextFormatting.RED);
        StringUtils.addPrefixes(map, "\u00a7f\ua538\u00a77", "WINNER", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a7f\ua540\u00a77", "HYDRA", TextFormatting.DARK_GREEN);
        StringUtils.addPrefixes(map, "\u00a7f\ua541\u00a77", "ASSISTAN", TextFormatting.DARK_AQUA);
        StringUtils.addPrefixes(map, "\u00a7f\ua542\u00a77", "SPONSOR", TextFormatting.GREEN);
        StringUtils.addPrefixes(map, "\u00a7f\ua544\u00a77", "DRACULA", TextFormatting.DARK_RED);
        StringUtils.addPrefixes(map, "\u00a7f\ua545\u00a77", "MEDIA+", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a7f\ua548\u00a77", "COBRA", TextFormatting.GREEN);
        StringUtils.addPrefixes(map, "\u00a7f\ua549\u00a77", "YT+", TextFormatting.RED);
        StringUtils.addPrefixes(map, "\u00a7f\ua552\u00a77", "RABBIT", TextFormatting.WHITE);
        StringUtils.addPrefixes(map, "\u00a7f\ua553\u00a77", "GHOST", TextFormatting.DARK_GRAY);
        StringUtils.addPrefixes(map, "\u00a7f\ua556\u00a77", "BUNNY", TextFormatting.BLACK);
        StringUtils.addPrefixes(map, "\u00a7f\ua557\u00a77", "D.ADMIN", TextFormatting.RED);
        StringUtils.addPrefixes(map, "\u00a7f\ua560\u00a77", "D.HELPER", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a7f\ua561\u00a77", "ST.HELPER", TextFormatting.GOLD);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua501\u00a77", "MEDIA", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua500\u00a77\u00a77", "PLAYER", TextFormatting.GRAY);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua504\u00a77\u00a77", "HERO", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua508\u00a77", "TITAN", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua509\u00a77\u00a77", "HELPER", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua512\u00a77\u00a77", "AVENGER", TextFormatting.GREEN);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua513\u00a77\u00a77", "ML.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua516\u00a77", "OVERLORD", TextFormatting.AQUA);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua517\u00a77\u00a77", "MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua520\u00a77", "MAGISTER", TextFormatting.GOLD);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua521\u00a77\u00a77", "MODER+", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua524\u00a77\u00a77", "IMPERATOR", TextFormatting.RED);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua525\u00a77", "ST.MODER", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua528\u00a77\u00a77", "DRAGON", TextFormatting.LIGHT_PURPLE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua529\u00a77\u00a77", "GL.MODER", TextFormatting.DARK_BLUE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua532\u00a77\u00a77", "BULL", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua533\u00a77", "ML.ADMIN", TextFormatting.AQUA);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua536\u00a77\u00a77", "TIGER", TextFormatting.GOLD);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua537\u00a77\u00a77", "ADMIN", TextFormatting.RED);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua540\u00a77", "HYDRA", TextFormatting.DARK_GREEN);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua541\u00a77\u00a77", "LEGENDA", TextFormatting.GOLD);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua544\u00a77\u00a77", "DRACULA", TextFormatting.DARK_RED);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua545\u00a77", "RAIN", TextFormatting.BLUE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua548\u00a77\u00a77", "COBRA", TextFormatting.GREEN);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua549\u00a77\u00a77", "LIME", TextFormatting.GREEN);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua552\u00a77\u00a77", "RABBIT", TextFormatting.WHITE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua553\u00a77", "SAKURA", TextFormatting.DARK_PURPLE);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua556\u00a77\u00a77", "BUNNY", TextFormatting.BLACK);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua557\u00a77", "SOLAR", TextFormatting.YELLOW);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua560\u00a77\u00a77", "D.HELPER", TextFormatting.YELLOW);
        StringTextComponent yt = new StringTextComponent("");
        yt.append(new StringTextComponent("Y").mergeStyle(TextFormatting.RED));
        yt.append(new StringTextComponent("T").mergeStyle(TextFormatting.WHITE));
        StringUtils.addPrefixes(map, "\u00a7f\ua505\u00a77", yt);
        StringUtils.addPrefixes(map, "\u00a70\u00a7r\u00a7f\ua505\u00a77\u00a77", yt);
        StringUtils.addPrefixes(map, "\u00a7f\u00a7f\ua505\u00a77\u00a77", yt);
        return map;
    }

    private static void addPrefixes(Map<String, TextComponent> map, String suffix, String displayText, TextFormatting color) {
        TextComponent component = (TextComponent)new StringTextComponent(displayText).mergeStyle(color);
        map.put("\u00a7c\u25cf" + suffix, component);
        map.put("\u00a7a\u25cf" + suffix, component);
        map.put("\u00a76\u25cf" + suffix, component);
    }

    private static void addPrefixes(Map<String, TextComponent> map, String suffix, TextComponent component) {
        map.put("\u00a7c\u25cf" + suffix, component);
        map.put("\u00a7a\u25cf" + suffix, component);
        map.put("\u00a76\u25cf" + suffix, component);
    }

    @Generated
    private StringUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
