package com.ibm.icu.impl.number.range;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.StandardPlural;
import com.ibm.icu.impl.UResource;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.MissingResourceException;

public class StandardPluralRanges {
    StandardPlural[] flatTriples;
    int numTriples = 0;

    private static void getPluralRangesData(ULocale locale, StandardPluralRanges out) {
        String set;
        StringBuilder sb = new StringBuilder();
        ICUResourceBundle resource = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b", "pluralRanges");
        sb.append("locales/");
        sb.append(locale.getLanguage());
        String key = sb.toString();
        try {
            set = resource.getStringWithFallback(key);
        }
        catch (MissingResourceException e) {
            return;
        }
        sb.setLength(0);
        sb.append("rules/");
        sb.append(set);
        key = sb.toString();
        PluralRangesDataSink sink = new PluralRangesDataSink(out);
        resource.getAllItemsWithFallback(key, sink);
    }

    public StandardPluralRanges(ULocale locale) {
        StandardPluralRanges.getPluralRangesData(locale, this);
    }

    private void addPluralRange(StandardPlural first, StandardPlural second, StandardPlural result) {
        this.flatTriples[3 * this.numTriples] = first;
        this.flatTriples[3 * this.numTriples + 1] = second;
        this.flatTriples[3 * this.numTriples + 2] = result;
        ++this.numTriples;
    }

    private void setCapacity(int length) {
        this.flatTriples = new StandardPlural[length * 3];
    }

    public StandardPlural resolve(StandardPlural first, StandardPlural second) {
        for (int i = 0; i < this.numTriples; ++i) {
            if (first != this.flatTriples[3 * i] || second != this.flatTriples[3 * i + 1]) continue;
            return this.flatTriples[3 * i + 2];
        }
        return StandardPlural.OTHER;
    }

    private static final class PluralRangesDataSink
    extends UResource.Sink {
        StandardPluralRanges output;

        PluralRangesDataSink(StandardPluralRanges output) {
            this.output = output;
        }

        @Override
        public void put(UResource.Key key, UResource.Value value, boolean noFallback) {
            UResource.Array entriesArray = value.getArray();
            this.output.setCapacity(entriesArray.getSize());
            int i = 0;
            while (entriesArray.getValue(i, value)) {
                UResource.Array pluralFormsArray = value.getArray();
                pluralFormsArray.getValue(0, value);
                StandardPlural first = StandardPlural.fromString(value.getString());
                pluralFormsArray.getValue(1, value);
                StandardPlural second = StandardPlural.fromString(value.getString());
                pluralFormsArray.getValue(2, value);
                StandardPlural result = StandardPlural.fromString(value.getString());
                this.output.addPluralRange(first, second, result);
                ++i;
            }
        }
    }
}
