package com.ibm.icu.impl.locale;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.UResource;
import com.ibm.icu.impl.locale.LSR;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.ULocale;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.TreeMap;

public final class XLikelySubtags {
    private static final String PSEUDO_ACCENTS_PREFIX = "'";
    private static final String PSEUDO_BIDI_PREFIX = "+";
    private static final String PSEUDO_CRACKED_PREFIX = ",";
    public static final int SKIP_SCRIPT = 1;
    private static final boolean DEBUG_OUTPUT = false;
    public static final XLikelySubtags INSTANCE = new XLikelySubtags(Data.load());
    private final Map<String, String> languageAliases;
    private final Map<String, String> regionAliases;
    private final BytesTrie trie;
    private final long trieUndState;
    private final long trieUndZzzzState;
    private final int defaultLsrIndex;
    private final long[] trieFirstLetterStates = new long[26];
    private final LSR[] lsrs;

    private XLikelySubtags(Data data) {
        this.languageAliases = data.languageAliases;
        this.regionAliases = data.regionAliases;
        this.trie = new BytesTrie(data.trie, 0);
        this.lsrs = data.lsrs;
        BytesTrie.Result result = this.trie.next(42);
        assert (result.hasNext());
        this.trieUndState = this.trie.getState64();
        result = this.trie.next(42);
        assert (result.hasNext());
        this.trieUndZzzzState = this.trie.getState64();
        result = this.trie.next(42);
        assert (result.hasValue());
        this.defaultLsrIndex = this.trie.getValue();
        this.trie.reset();
        for (int c = 97; c <= 122; c = (int)((char)(c + 1))) {
            result = this.trie.next(c);
            if (result == BytesTrie.Result.NO_VALUE) {
                this.trieFirstLetterStates[c - 97] = this.trie.getState64();
            }
            this.trie.reset();
        }
    }

    public ULocale canonicalize(ULocale locale) {
        String lang = locale.getLanguage();
        String lang2 = this.languageAliases.get(lang);
        String region = locale.getCountry();
        String region2 = this.regionAliases.get(region);
        if (lang2 != null || region2 != null) {
            return new ULocale(lang2 == null ? lang : lang2, locale.getScript(), region2 == null ? region : region2);
        }
        return locale;
    }

    private static String getCanonical(Map<String, String> aliases, String alias) {
        String canonical = aliases.get(alias);
        return canonical == null ? alias : canonical;
    }

    public LSR makeMaximizedLsrFrom(ULocale locale) {
        String name = locale.getName();
        if (name.startsWith("@x=")) {
            String tag = locale.toLanguageTag();
            assert (tag.startsWith("x-"));
            return new LSR(tag, "", "");
        }
        return this.makeMaximizedLsr(locale.getLanguage(), locale.getScript(), locale.getCountry(), locale.getVariant());
    }

    public LSR makeMaximizedLsrFrom(Locale locale) {
        String tag = locale.toLanguageTag();
        if (tag.startsWith("x-")) {
            return new LSR(tag, "", "");
        }
        return this.makeMaximizedLsr(locale.getLanguage(), locale.getScript(), locale.getCountry(), locale.getVariant());
    }

    private LSR makeMaximizedLsr(String language, String script, String region, String variant) {
        if (region.length() == 2 && region.charAt(0) == 'X') {
            switch (region.charAt(1)) {
                case 'A': {
                    return new LSR(PSEUDO_ACCENTS_PREFIX + language, PSEUDO_ACCENTS_PREFIX + script, region);
                }
                case 'B': {
                    return new LSR(PSEUDO_BIDI_PREFIX + language, PSEUDO_BIDI_PREFIX + script, region);
                }
                case 'C': {
                    return new LSR(PSEUDO_CRACKED_PREFIX + language, PSEUDO_CRACKED_PREFIX + script, region);
                }
            }
        }
        if (variant.startsWith("PS")) {
            switch (variant) {
                case "PSACCENT": {
                    return new LSR(PSEUDO_ACCENTS_PREFIX + language, PSEUDO_ACCENTS_PREFIX + script, region.isEmpty() ? "XA" : region);
                }
                case "PSBIDI": {
                    return new LSR(PSEUDO_BIDI_PREFIX + language, PSEUDO_BIDI_PREFIX + script, region.isEmpty() ? "XB" : region);
                }
                case "PSCRACK": {
                    return new LSR(PSEUDO_CRACKED_PREFIX + language, PSEUDO_CRACKED_PREFIX + script, region.isEmpty() ? "XC" : region);
                }
            }
        }
        language = XLikelySubtags.getCanonical(this.languageAliases, language);
        region = XLikelySubtags.getCanonical(this.regionAliases, region);
        return this.maximize(language, script, region);
    }

    private LSR maximize(String language, String script, String region) {
        long state;
        int c0;
        if (language.equals("und")) {
            language = "";
        }
        if (script.equals("Zzzz")) {
            script = "";
        }
        if (region.equals("ZZ")) {
            region = "";
        }
        if (!(script.isEmpty() || region.isEmpty() || language.isEmpty())) {
            return new LSR(language, script, region);
        }
        int retainOldMask = 0;
        BytesTrie iter = new BytesTrie(this.trie);
        int value = language.length() >= 2 && 0 <= (c0 = language.charAt(0) - 97) && c0 <= 25 && (state = this.trieFirstLetterStates[c0]) != 0L ? XLikelySubtags.trieNext(iter.resetToState64(state), language, 1) : XLikelySubtags.trieNext(iter, language, 0);
        if (value >= 0) {
            if (!language.isEmpty()) {
                retainOldMask |= 4;
            }
            state = iter.getState64();
        } else {
            retainOldMask |= 4;
            iter.resetToState64(this.trieUndState);
            state = 0L;
        }
        if (value > 0) {
            if (value == 1) {
                value = 0;
            }
            if (!script.isEmpty()) {
                retainOldMask |= 2;
            }
        } else {
            value = XLikelySubtags.trieNext(iter, script, 0);
            if (value >= 0) {
                if (!script.isEmpty()) {
                    retainOldMask |= 2;
                }
                state = iter.getState64();
            } else {
                retainOldMask |= 2;
                if (state == 0L) {
                    iter.resetToState64(this.trieUndZzzzState);
                } else {
                    iter.resetToState64(state);
                    value = XLikelySubtags.trieNext(iter, "", 0);
                    assert (value >= 0);
                    state = iter.getState64();
                }
            }
        }
        if (value > 0) {
            if (!region.isEmpty()) {
                retainOldMask |= 1;
            }
        } else {
            value = XLikelySubtags.trieNext(iter, region, 0);
            if (value >= 0) {
                if (!region.isEmpty()) {
                    retainOldMask |= 1;
                }
            } else {
                retainOldMask |= 1;
                if (state == 0L) {
                    value = this.defaultLsrIndex;
                } else {
                    iter.resetToState64(state);
                    value = XLikelySubtags.trieNext(iter, "", 0);
                    assert (value > 0);
                }
            }
        }
        LSR result = this.lsrs[value];
        if (language.isEmpty()) {
            language = "und";
        }
        if (retainOldMask == 0) {
            return result;
        }
        if ((retainOldMask & 4) == 0) {
            language = result.language;
        }
        if ((retainOldMask & 2) == 0) {
            script = result.script;
        }
        if ((retainOldMask & 1) == 0) {
            region = result.region;
        }
        return new LSR(language, script, region);
    }

    private static final int trieNext(BytesTrie iter, String s, int i) {
        BytesTrie.Result result;
        if (s.isEmpty()) {
            result = iter.next(42);
        } else {
            int end = s.length() - 1;
            while (true) {
                char c = s.charAt(i);
                if (i >= end) {
                    result = iter.next(c | 0x80);
                    break;
                }
                if (!iter.next(c).hasNext()) {
                    return -1;
                }
                ++i;
            }
        }
        switch (result) {
            case NO_MATCH: {
                return -1;
            }
            case NO_VALUE: {
                return 0;
            }
            case INTERMEDIATE_VALUE: {
                assert (iter.getValue() == 1);
                return 1;
            }
            case FINAL_VALUE: {
                return iter.getValue();
            }
        }
        return -1;
    }

    LSR minimizeSubtags(String languageIn, String scriptIn, String regionIn, ULocale.Minimize fieldToFavor) {
        LSR result2;
        LSR result = this.maximize(languageIn, scriptIn, regionIn);
        BytesTrie iter = new BytesTrie(this.trie);
        int value = XLikelySubtags.trieNext(iter, result.language, 0);
        assert (value >= 0);
        if (value == 0) {
            value = XLikelySubtags.trieNext(iter, "", 0);
            assert (value >= 0);
            if (value == 0) {
                value = XLikelySubtags.trieNext(iter, "", 0);
            }
        }
        assert (value > 0);
        LSR value00 = this.lsrs[value];
        boolean favorRegionOk = false;
        if (result.script.equals(value00.script)) {
            if (result.region.equals(value00.region)) {
                return new LSR(result.language, "", "");
            }
            if (fieldToFavor == ULocale.Minimize.FAVOR_REGION) {
                return new LSR(result.language, "", result.region);
            }
            favorRegionOk = true;
        }
        if ((result2 = this.maximize(languageIn, scriptIn, "")).equals(result)) {
            return new LSR(result.language, result.script, "");
        }
        if (favorRegionOk) {
            return new LSR(result.language, "", result.region);
        }
        return result;
    }

    private Map<String, LSR> getTable() {
        TreeMap<String, LSR> map = new TreeMap<String, LSR>();
        StringBuilder sb = new StringBuilder();
        for (BytesTrie.Entry entry : this.trie) {
            sb.setLength(0);
            int length = entry.bytesLength();
            int i = 0;
            while (i < length) {
                byte b;
                if ((b = entry.byteAt(i++)) == 42) {
                    sb.append("*-");
                    continue;
                }
                if (b >= 0) {
                    sb.append((char)b);
                    continue;
                }
                sb.append((char)(b & 0x7F)).append('-');
            }
            assert (sb.length() > 0 && sb.charAt(sb.length() - 1) == '-');
            sb.setLength(sb.length() - 1);
            map.put(sb.toString(), this.lsrs[entry.value]);
        }
        return map;
    }

    public String toString() {
        return this.getTable().toString();
    }

    public static final class Data {
        public final Map<String, String> languageAliases;
        public final Map<String, String> regionAliases;
        public final byte[] trie;
        public final LSR[] lsrs;

        public Data(Map<String, String> languageAliases, Map<String, String> regionAliases, byte[] trie, LSR[] lsrs) {
            this.languageAliases = languageAliases;
            this.regionAliases = regionAliases;
            this.trie = trie;
            this.lsrs = lsrs;
        }

        private static UResource.Value getValue(UResource.Table table, String key, UResource.Value value) {
            if (!table.findValue(key, value)) {
                throw new MissingResourceException("langInfo.res missing data", "", "likely/" + key);
            }
            return value;
        }

        public static Data load() throws MissingResourceException {
            Map<String, String> regionAliases;
            Map<String, String> languageAliases;
            ICUResourceBundle langInfo = ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b", "langInfo", ICUResourceBundle.ICU_DATA_CLASS_LOADER, ICUResourceBundle.OpenType.DIRECT);
            UResource.Value value = langInfo.getValueWithFallback("likely");
            UResource.Table likelyTable = value.getTable();
            if (likelyTable.findValue("languageAliases", value)) {
                String[] pairs = value.getStringArray();
                languageAliases = new HashMap(pairs.length / 2);
                for (int i = 0; i < pairs.length; i += 2) {
                    languageAliases.put(pairs[i], pairs[i + 1]);
                }
            } else {
                languageAliases = Collections.emptyMap();
            }
            if (likelyTable.findValue("regionAliases", value)) {
                String[] pairs = value.getStringArray();
                regionAliases = new HashMap(pairs.length / 2);
                for (int i = 0; i < pairs.length; i += 2) {
                    regionAliases.put(pairs[i], pairs[i + 1]);
                }
            } else {
                regionAliases = Collections.emptyMap();
            }
            ByteBuffer buffer = Data.getValue(likelyTable, "trie", value).getBinary();
            byte[] trie = new byte[buffer.remaining()];
            buffer.get(trie);
            String[] lsrSubtags = Data.getValue(likelyTable, "lsrs", value).getStringArray();
            LSR[] lsrs = new LSR[lsrSubtags.length / 3];
            int i = 0;
            int j = 0;
            while (i < lsrSubtags.length) {
                lsrs[j] = new LSR(lsrSubtags[i], lsrSubtags[i + 1], lsrSubtags[i + 2]);
                i += 3;
                ++j;
            }
            return new Data(languageAliases, regionAliases, trie, lsrs);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!this.getClass().equals(other.getClass())) {
                return false;
            }
            Data od = (Data)other;
            return this.languageAliases.equals(od.languageAliases) && this.regionAliases.equals(od.regionAliases) && Arrays.equals(this.trie, od.trie) && Arrays.equals(this.lsrs, od.lsrs);
        }
    }
}
