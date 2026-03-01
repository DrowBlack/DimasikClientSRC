package de.maxhenkel.configbuilder.custom.serializer;

import de.maxhenkel.configbuilder.custom.StringMap;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

public class StringMapValueSerializer
implements ValueSerializer<StringMap> {
    public static final StringMapValueSerializer INSTANCE = new StringMapValueSerializer();
    public static final Pattern QUOTE_ESCAPE_PATTERN = Pattern.compile("\"((?:(?![\"\\\\]).|\\\\.)*)\"\\s*=\\s*\"((?:(?![\"\\\\]).|\\\\.)*)\"");

    @Override
    @Nullable
    public StringMap deserialize(String str) {
        boolean matches = QUOTE_ESCAPE_PATTERN.splitAsStream(str).allMatch(s -> s.trim().isEmpty() || s.trim().equals(","));
        if (!matches) {
            return null;
        }
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        Matcher matcher = QUOTE_ESCAPE_PATTERN.matcher(str);
        while (matcher.find()) {
            map.put(StringMapValueSerializer.unescape(matcher.group(1)), StringMapValueSerializer.unescape(matcher.group(2)));
        }
        return StringMap.of(map);
    }

    @Override
    public String serialize(StringMap val) {
        ArrayList<String> resultList = new ArrayList<String>(val.size());
        for (Map.Entry entry : val.entrySet()) {
            resultList.add("\"" + StringMapValueSerializer.escape((String)entry.getKey()) + "\"=\"" + StringMapValueSerializer.escape((String)entry.getValue()) + "\"");
        }
        return String.join((CharSequence)",", resultList);
    }

    private static String escape(String input) {
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String unescape(String input) {
        return input.replace("\\\"", "\"").replace("\\\\", "\\");
    }
}
