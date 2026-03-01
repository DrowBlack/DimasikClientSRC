package de.maxhenkel.configbuilder.custom.serializer;

import de.maxhenkel.configbuilder.custom.IntegerList;
import de.maxhenkel.configbuilder.entry.serializer.ValueSerializer;
import java.util.ArrayList;
import javax.annotation.Nullable;

public class IntegerListValueSerializer
implements ValueSerializer<IntegerList> {
    public static final IntegerListValueSerializer INSTANCE = new IntegerListValueSerializer();

    @Override
    @Nullable
    public IntegerList deserialize(String str) {
        ArrayList<Integer> resultList = new ArrayList<Integer>();
        for (String s : str.split(",")) {
            try {
                resultList.add(Integer.valueOf(s));
            }
            catch (NumberFormatException e) {
                return null;
            }
        }
        return IntegerList.of(resultList);
    }

    @Override
    public String serialize(IntegerList val) {
        ArrayList<String> resultList = new ArrayList<String>(val.size());
        for (Integer i : val) {
            resultList.add(String.valueOf(i));
        }
        return String.join((CharSequence)",", resultList);
    }
}
