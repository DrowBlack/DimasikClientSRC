package de.maxhenkel.configbuilder.custom.serializer;

import de.maxhenkel.configbuilder.custom.StringList;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import java.util.ArrayList;

public class StringListValueSerializer
implements ValueSerializer<StringList> {
    public static final StringListValueSerializer INSTANCE = new StringListValueSerializer();

    @Override
    public StringList deserialize(String str) {
        ArrayList<String> resultList = new ArrayList<String>();
        for (String s : str.split("(?<!\\\\),")) {
            resultList.add(s.replace("\\,", ","));
        }
        return StringList.of(resultList);
    }

    @Override
    public String serialize(StringList val) {
        ArrayList<String> resultList = new ArrayList<String>(val.size());
        for (String str : val) {
            resultList.add(str.replace(",", "\\,"));
        }
        return String.join((CharSequence)",", resultList);
    }
}
