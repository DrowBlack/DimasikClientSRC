package com.ibm.icu.impl.coll;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.coll.CollationDataReader;
import com.ibm.icu.impl.coll.CollationRoot;
import com.ibm.icu.impl.coll.CollationTailoring;
import com.ibm.icu.util.ICUUncheckedIOException;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.MissingResourceException;

public final class CollationLoader {
    private static volatile String rootRules = null;

    private CollationLoader() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadRootRules() {
        if (rootRules != null) {
            return;
        }
        Class<CollationLoader> clazz = CollationLoader.class;
        synchronized (CollationLoader.class) {
            if (rootRules == null) {
                UResourceBundle rootBundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b/coll", ULocale.ROOT);
                rootRules = rootBundle.getString("UCARules");
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return;
        }
    }

    public static String getRootRules() {
        CollationLoader.loadRootRules();
        return rootRules;
    }

    static String loadRules(ULocale locale, String collationType) {
        UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b/coll", locale);
        ICUResourceBundle data = ((ICUResourceBundle)bundle).getWithFallback("collations/" + ASCII.toLowerCase(collationType));
        String rules = data.getString("Sequence");
        return rules;
    }

    private static final UResourceBundle findWithFallback(UResourceBundle table, String entryName) {
        return ((ICUResourceBundle)table).findWithFallback(entryName);
    }

    public static CollationTailoring loadTailoring(ULocale locale, Output<ULocale> outValidLocale) {
        UResourceBundle actualBundle;
        UResourceBundle data;
        UResourceBundle collations;
        CollationTailoring root = CollationRoot.getRoot();
        String localeName = locale.getName();
        if (localeName.length() == 0 || localeName.equals("root")) {
            outValidLocale.value = ULocale.ROOT;
            return root;
        }
        ICUResourceBundle bundle = null;
        try {
            bundle = ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b/coll", locale, ICUResourceBundle.OpenType.LOCALE_ROOT);
        }
        catch (MissingResourceException e) {
            outValidLocale.value = ULocale.ROOT;
            return root;
        }
        ULocale validLocale = ((UResourceBundle)bundle).getULocale();
        String validLocaleName = validLocale.getName();
        if (validLocaleName.length() == 0 || validLocaleName.equals("root")) {
            validLocale = ULocale.ROOT;
        }
        outValidLocale.value = validLocale;
        try {
            collations = bundle.get("collations");
            if (collations == null) {
                return root;
            }
        }
        catch (MissingResourceException ignored) {
            return root;
        }
        String type = locale.getKeywordValue("collation");
        String defaultType = "standard";
        String defT = ((ICUResourceBundle)collations).findStringWithFallback("default");
        if (defT != null) {
            defaultType = defT;
        }
        if ((data = CollationLoader.findWithFallback(collations, type = type == null || type.equals("default") ? defaultType : ASCII.toLowerCase(type))) == null && type.length() > 6 && type.startsWith("search")) {
            type = "search";
            data = CollationLoader.findWithFallback(collations, type);
        }
        if (data == null && !type.equals(defaultType)) {
            type = defaultType;
            data = CollationLoader.findWithFallback(collations, type);
        }
        if (data == null && !type.equals("standard")) {
            type = "standard";
            data = CollationLoader.findWithFallback(collations, type);
        }
        if (data == null) {
            return root;
        }
        ULocale actualLocale = data.getULocale();
        String actualLocaleName = actualLocale.getName();
        if (actualLocaleName.length() == 0 || actualLocaleName.equals("root")) {
            actualLocale = ULocale.ROOT;
            if (type.equals("standard")) {
                return root;
            }
        }
        CollationTailoring t = new CollationTailoring(root.settings);
        t.actualLocale = actualLocale;
        UResourceBundle binary = data.get("%%CollationBin");
        ByteBuffer inBytes = binary.getBinary();
        try {
            CollationDataReader.read(root, inBytes, t);
        }
        catch (IOException e) {
            throw new ICUUncheckedIOException("Failed to load collation tailoring data for locale:" + actualLocale + " type:" + type, e);
        }
        try {
            t.setRulesResource(data.get("Sequence"));
        }
        catch (MissingResourceException e) {
            // empty catch block
        }
        if (!type.equals(defaultType)) {
            outValidLocale.value = validLocale.setKeywordValue("collation", type);
        }
        if (!actualLocale.equals(validLocale) && (defT = ((ICUResourceBundle)(actualBundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b/coll", actualLocale))).findStringWithFallback("collations/default")) != null) {
            defaultType = defT;
        }
        if (!type.equals(defaultType)) {
            t.actualLocale = t.actualLocale.setKeywordValue("collation", type);
        }
        return t;
    }

    private static final class ASCII {
        private ASCII() {
        }

        static String toLowerCase(String s) {
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if ('A' > c || c > 'Z') continue;
                StringBuilder sb = new StringBuilder(s.length());
                sb.append(s, 0, i).append((char)(c + 32));
                while (++i < s.length()) {
                    c = s.charAt(i);
                    if ('A' <= c && c <= 'Z') {
                        c = (char)(c + 32);
                    }
                    sb.append(c);
                }
                return sb.toString();
            }
            return s;
        }
    }
}
