package com.ibm.icu.util;

import com.ibm.icu.impl.CacheBase;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUResourceTableAccess;
import com.ibm.icu.impl.LocaleIDParser;
import com.ibm.icu.impl.LocaleIDs;
import com.ibm.icu.impl.LocaleUtility;
import com.ibm.icu.impl.SoftCache;
import com.ibm.icu.impl.locale.AsciiUtil;
import com.ibm.icu.impl.locale.BaseLocale;
import com.ibm.icu.impl.locale.Extension;
import com.ibm.icu.impl.locale.InternalLocaleBuilder;
import com.ibm.icu.impl.locale.KeyTypeData;
import com.ibm.icu.impl.locale.LanguageTag;
import com.ibm.icu.impl.locale.LocaleExtensions;
import com.ibm.icu.impl.locale.LocaleSyntaxException;
import com.ibm.icu.impl.locale.ParseStatus;
import com.ibm.icu.impl.locale.UnicodeLocaleExtension;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.LocaleDisplayNames;
import com.ibm.icu.util.IllformedLocaleException;
import com.ibm.icu.util.UResourceBundle;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public final class ULocale
implements Serializable,
Comparable<ULocale> {
    private static final long serialVersionUID = 3715177670352309217L;
    private static final Pattern UND_PATTERN = Pattern.compile("^und(?=$|[_-])", 2);
    private static CacheBase<String, String, Void> nameCache = new SoftCache<String, String, Void>(){

        @Override
        protected String createInstance(String tmpLocaleID, Void unused) {
            return new LocaleIDParser(tmpLocaleID).getName();
        }
    };
    public static final ULocale ENGLISH = new ULocale("en", Locale.ENGLISH);
    public static final ULocale FRENCH = new ULocale("fr", Locale.FRENCH);
    public static final ULocale GERMAN = new ULocale("de", Locale.GERMAN);
    public static final ULocale ITALIAN = new ULocale("it", Locale.ITALIAN);
    public static final ULocale JAPANESE = new ULocale("ja", Locale.JAPANESE);
    public static final ULocale KOREAN = new ULocale("ko", Locale.KOREAN);
    public static final ULocale CHINESE = new ULocale("zh", Locale.CHINESE);
    public static final ULocale SIMPLIFIED_CHINESE = new ULocale("zh_Hans");
    public static final ULocale TRADITIONAL_CHINESE = new ULocale("zh_Hant");
    public static final ULocale FRANCE = new ULocale("fr_FR", Locale.FRANCE);
    public static final ULocale GERMANY = new ULocale("de_DE", Locale.GERMANY);
    public static final ULocale ITALY = new ULocale("it_IT", Locale.ITALY);
    public static final ULocale JAPAN = new ULocale("ja_JP", Locale.JAPAN);
    public static final ULocale KOREA = new ULocale("ko_KR", Locale.KOREA);
    public static final ULocale CHINA;
    public static final ULocale PRC;
    public static final ULocale TAIWAN;
    public static final ULocale UK;
    public static final ULocale US;
    public static final ULocale CANADA;
    public static final ULocale CANADA_FRENCH;
    private static final String EMPTY_STRING = "";
    private static final char UNDERSCORE = '_';
    private static final Locale EMPTY_LOCALE;
    private static final String LOCALE_ATTRIBUTE_KEY = "attribute";
    public static final ULocale ROOT;
    private static final SoftCache<Locale, ULocale, Void> CACHE;
    private volatile transient Locale locale;
    private String localeID;
    private volatile transient BaseLocale baseLocale;
    private volatile transient LocaleExtensions extensions;
    private static String[][] CANONICALIZE_MAP;
    private static Locale defaultLocale;
    private static ULocale defaultULocale;
    private static Locale[] defaultCategoryLocales;
    private static ULocale[] defaultCategoryULocales;
    private static final String LANG_DIR_STRING = "root-en-es-pt-zh-ja-ko-de-fr-it-ar+he+fa+ru-nl-pl-th-tr-";
    public static Type ACTUAL_LOCALE;
    public static Type VALID_LOCALE;
    private static final String UNDEFINED_LANGUAGE = "und";
    private static final String UNDEFINED_SCRIPT = "Zzzz";
    private static final String UNDEFINED_REGION = "ZZ";
    public static final char PRIVATE_USE_EXTENSION = 'x';
    public static final char UNICODE_LOCALE_EXTENSION = 'u';

    private ULocale(String localeID, Locale locale) {
        this.localeID = localeID;
        this.locale = locale;
    }

    private ULocale(Locale loc) {
        this.localeID = ULocale.getName(ULocale.forLocale(loc).toString());
        this.locale = loc;
    }

    public static ULocale forLocale(Locale loc) {
        if (loc == null) {
            return null;
        }
        return CACHE.getInstance(loc, null);
    }

    public ULocale(String localeID) {
        this.localeID = ULocale.getName(localeID);
    }

    public ULocale(String a, String b) {
        this(a, b, null);
    }

    public ULocale(String a, String b, String c) {
        this.localeID = ULocale.getName(ULocale.lscvToID(a, b, c, EMPTY_STRING));
    }

    public static ULocale createCanonical(String nonCanonicalID) {
        return new ULocale(ULocale.canonicalize(nonCanonicalID), (Locale)null);
    }

    private static String lscvToID(String lang, String script, String country, String variant) {
        StringBuilder buf = new StringBuilder();
        if (lang != null && lang.length() > 0) {
            buf.append(lang);
        }
        if (script != null && script.length() > 0) {
            buf.append('_');
            buf.append(script);
        }
        if (country != null && country.length() > 0) {
            buf.append('_');
            buf.append(country);
        }
        if (variant != null && variant.length() > 0) {
            if (country == null || country.length() == 0) {
                buf.append('_');
            }
            buf.append('_');
            buf.append(variant);
        }
        return buf.toString();
    }

    public Locale toLocale() {
        if (this.locale == null) {
            this.locale = JDKLocaleHelper.toLocale(this);
        }
        return this.locale;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ULocale getDefault() {
        Class<ULocale> clazz = ULocale.class;
        synchronized (ULocale.class) {
            if (defaultULocale == null) {
                // ** MonitorExit[var0] (shouldn't be in output)
                return ROOT;
            }
            Locale currentDefault = Locale.getDefault();
            if (!defaultLocale.equals(currentDefault)) {
                defaultLocale = currentDefault;
                defaultULocale = ULocale.forLocale(currentDefault);
                if (!JDKLocaleHelper.hasLocaleCategories()) {
                    for (Category cat : Category.values()) {
                        int idx = cat.ordinal();
                        ULocale.defaultCategoryLocales[idx] = currentDefault;
                        ULocale.defaultCategoryULocales[idx] = ULocale.forLocale(currentDefault);
                    }
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return defaultULocale;
        }
    }

    public static synchronized void setDefault(ULocale newLocale) {
        defaultLocale = newLocale.toLocale();
        Locale.setDefault(defaultLocale);
        defaultULocale = newLocale;
        for (Category cat : Category.values()) {
            ULocale.setDefault(cat, newLocale);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ULocale getDefault(Category category) {
        Class<ULocale> clazz = ULocale.class;
        synchronized (ULocale.class) {
            int idx = category.ordinal();
            if (defaultCategoryULocales[idx] == null) {
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return ROOT;
            }
            if (JDKLocaleHelper.hasLocaleCategories()) {
                Locale currentCategoryDefault = JDKLocaleHelper.getDefault(category);
                if (!defaultCategoryLocales[idx].equals(currentCategoryDefault)) {
                    ULocale.defaultCategoryLocales[idx] = currentCategoryDefault;
                    ULocale.defaultCategoryULocales[idx] = ULocale.forLocale(currentCategoryDefault);
                }
            } else {
                Locale currentDefault = Locale.getDefault();
                if (!defaultLocale.equals(currentDefault)) {
                    defaultLocale = currentDefault;
                    defaultULocale = ULocale.forLocale(currentDefault);
                    for (Category cat : Category.values()) {
                        int tmpIdx = cat.ordinal();
                        ULocale.defaultCategoryLocales[tmpIdx] = currentDefault;
                        ULocale.defaultCategoryULocales[tmpIdx] = ULocale.forLocale(currentDefault);
                    }
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return defaultCategoryULocales[idx];
        }
    }

    public static synchronized void setDefault(Category category, ULocale newLocale) {
        Locale newJavaDefault = newLocale.toLocale();
        int idx = category.ordinal();
        ULocale.defaultCategoryULocales[idx] = newLocale;
        ULocale.defaultCategoryLocales[idx] = newJavaDefault;
        JDKLocaleHelper.setDefault(category, newJavaDefault);
    }

    public Object clone() {
        return this;
    }

    public int hashCode() {
        return this.localeID.hashCode();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ULocale) {
            return this.localeID.equals(((ULocale)obj).localeID);
        }
        return false;
    }

    @Override
    public int compareTo(ULocale other) {
        if (this == other) {
            return 0;
        }
        int cmp = 0;
        cmp = this.getLanguage().compareTo(other.getLanguage());
        if (cmp == 0 && (cmp = this.getScript().compareTo(other.getScript())) == 0 && (cmp = this.getCountry().compareTo(other.getCountry())) == 0 && (cmp = this.getVariant().compareTo(other.getVariant())) == 0) {
            Iterator<String> thisKwdItr = this.getKeywords();
            Iterator<String> otherKwdItr = other.getKeywords();
            if (thisKwdItr == null) {
                cmp = otherKwdItr == null ? 0 : -1;
            } else if (otherKwdItr == null) {
                cmp = 1;
            } else {
                while (cmp == 0 && thisKwdItr.hasNext()) {
                    String otherKey;
                    if (!otherKwdItr.hasNext()) {
                        cmp = 1;
                        break;
                    }
                    String thisKey = thisKwdItr.next();
                    cmp = thisKey.compareTo(otherKey = otherKwdItr.next());
                    if (cmp != 0) continue;
                    String thisVal = this.getKeywordValue(thisKey);
                    String otherVal = other.getKeywordValue(otherKey);
                    if (thisVal == null) {
                        cmp = otherVal == null ? 0 : -1;
                        continue;
                    }
                    if (otherVal == null) {
                        cmp = 1;
                        continue;
                    }
                    cmp = thisVal.compareTo(otherVal);
                }
                if (cmp == 0 && otherKwdItr.hasNext()) {
                    cmp = -1;
                }
            }
        }
        return cmp < 0 ? -1 : (cmp > 0 ? 1 : 0);
    }

    public static ULocale[] getAvailableLocales() {
        return (ULocale[])ICUResourceBundle.getAvailableULocales().clone();
    }

    public static Collection<ULocale> getAvailableLocalesByType(AvailableType type) {
        List<Object> result;
        if (type == null) {
            throw new IllegalArgumentException();
        }
        if (type == AvailableType.WITH_LEGACY_ALIASES) {
            result = new ArrayList();
            Collections.addAll(result, ICUResourceBundle.getAvailableULocales(AvailableType.DEFAULT));
            Collections.addAll(result, ICUResourceBundle.getAvailableULocales(AvailableType.ONLY_LEGACY_ALIASES));
        } else {
            result = Arrays.asList(ICUResourceBundle.getAvailableULocales(type));
        }
        return Collections.unmodifiableList(result);
    }

    public static String[] getISOCountries() {
        return LocaleIDs.getISOCountries();
    }

    public static String[] getISOLanguages() {
        return LocaleIDs.getISOLanguages();
    }

    public String getLanguage() {
        return this.base().getLanguage();
    }

    public static String getLanguage(String localeID) {
        return new LocaleIDParser(localeID).getLanguage();
    }

    public String getScript() {
        return this.base().getScript();
    }

    public static String getScript(String localeID) {
        return new LocaleIDParser(localeID).getScript();
    }

    public String getCountry() {
        return this.base().getRegion();
    }

    public static String getCountry(String localeID) {
        return new LocaleIDParser(localeID).getCountry();
    }

    @Deprecated
    public static String getRegionForSupplementalData(ULocale locale, boolean inferRegion) {
        String regionUpper;
        String region = locale.getKeywordValue("rg");
        if (region != null && region.length() == 6 && (regionUpper = AsciiUtil.toUpperString(region)).endsWith("ZZZZ")) {
            return regionUpper.substring(0, 2);
        }
        region = locale.getCountry();
        if (region.length() == 0 && inferRegion) {
            ULocale maximized = ULocale.addLikelySubtags(locale);
            region = maximized.getCountry();
        }
        return region;
    }

    public String getVariant() {
        return this.base().getVariant();
    }

    public static String getVariant(String localeID) {
        return new LocaleIDParser(localeID).getVariant();
    }

    public static String getFallback(String localeID) {
        return ULocale.getFallbackString(ULocale.getName(localeID));
    }

    public ULocale getFallback() {
        if (this.localeID.length() == 0 || this.localeID.charAt(0) == '@') {
            return null;
        }
        return new ULocale(ULocale.getFallbackString(this.localeID), (Locale)null);
    }

    private static String getFallbackString(String fallback) {
        int last;
        int extStart = fallback.indexOf(64);
        if (extStart == -1) {
            extStart = fallback.length();
        }
        if ((last = fallback.lastIndexOf(95, extStart)) == -1) {
            last = 0;
        } else {
            while (last > 0 && fallback.charAt(last - 1) == '_') {
                --last;
            }
        }
        return fallback.substring(0, last) + fallback.substring(extStart);
    }

    public String getBaseName() {
        return ULocale.getBaseName(this.localeID);
    }

    public static String getBaseName(String localeID) {
        if (localeID.indexOf(64) == -1) {
            return localeID;
        }
        return new LocaleIDParser(localeID).getBaseName();
    }

    public String getName() {
        return this.localeID;
    }

    private static int getShortestSubtagLength(String localeID) {
        int localeIDLength;
        int length = localeIDLength = localeID.length();
        boolean reset = true;
        int tmpLength = 0;
        for (int i = 0; i < localeIDLength; ++i) {
            if (localeID.charAt(i) != '_' && localeID.charAt(i) != '-') {
                if (reset) {
                    reset = false;
                    tmpLength = 0;
                }
                ++tmpLength;
                continue;
            }
            if (tmpLength != 0 && tmpLength < length) {
                length = tmpLength;
            }
            reset = true;
        }
        return length;
    }

    public static String getName(String localeID) {
        String tmpLocaleID;
        if (localeID != null && !localeID.contains("@") && ULocale.getShortestSubtagLength(localeID) == 1) {
            tmpLocaleID = ULocale.forLanguageTag(localeID).getName();
            if (tmpLocaleID.length() == 0) {
                tmpLocaleID = localeID;
            }
        } else {
            tmpLocaleID = "root".equalsIgnoreCase(localeID) ? EMPTY_STRING : UND_PATTERN.matcher(localeID).replaceFirst(EMPTY_STRING);
        }
        return nameCache.getInstance(tmpLocaleID, null);
    }

    public String toString() {
        return this.localeID;
    }

    public Iterator<String> getKeywords() {
        return ULocale.getKeywords(this.localeID);
    }

    public static Iterator<String> getKeywords(String localeID) {
        return new LocaleIDParser(localeID).getKeywords();
    }

    public String getKeywordValue(String keywordName) {
        return ULocale.getKeywordValue(this.localeID, keywordName);
    }

    public static String getKeywordValue(String localeID, String keywordName) {
        return new LocaleIDParser(localeID).getKeywordValue(keywordName);
    }

    public static String canonicalize(String localeID) {
        LocaleIDParser parser = new LocaleIDParser(localeID, true);
        String baseName = parser.getBaseName();
        boolean foundVariant = false;
        if (localeID.equals(EMPTY_STRING)) {
            return EMPTY_STRING;
        }
        for (int i = 0; i < CANONICALIZE_MAP.length; ++i) {
            String[] vals = CANONICALIZE_MAP[i];
            if (!vals[0].equals(baseName)) continue;
            foundVariant = true;
            parser.setBaseName(vals[1]);
            break;
        }
        if (!foundVariant && parser.getLanguage().equals("nb") && parser.getVariant().equals("NY")) {
            parser.setBaseName(ULocale.lscvToID("nn", parser.getScript(), parser.getCountry(), null));
        }
        return parser.getName();
    }

    public ULocale setKeywordValue(String keyword, String value) {
        return new ULocale(ULocale.setKeywordValue(this.localeID, keyword, value), (Locale)null);
    }

    public static String setKeywordValue(String localeID, String keyword, String value) {
        LocaleIDParser parser = new LocaleIDParser(localeID);
        parser.setKeywordValue(keyword, value);
        return parser.getName();
    }

    public String getISO3Language() {
        return ULocale.getISO3Language(this.localeID);
    }

    public static String getISO3Language(String localeID) {
        return LocaleIDs.getISO3Language(ULocale.getLanguage(localeID));
    }

    public String getISO3Country() {
        return ULocale.getISO3Country(this.localeID);
    }

    public static String getISO3Country(String localeID) {
        return LocaleIDs.getISO3Country(ULocale.getCountry(localeID));
    }

    public boolean isRightToLeft() {
        String script = this.getScript();
        if (script.length() == 0) {
            ULocale likely;
            int langIndex;
            String lang = this.getLanguage();
            if (!lang.isEmpty() && (langIndex = LANG_DIR_STRING.indexOf(lang)) >= 0) {
                switch (LANG_DIR_STRING.charAt(langIndex + lang.length())) {
                    case '-': {
                        return false;
                    }
                    case '+': {
                        return true;
                    }
                }
            }
            if ((script = (likely = ULocale.addLikelySubtags(this)).getScript()).length() == 0) {
                return false;
            }
        }
        int scriptCode = UScript.getCodeFromName(script);
        return UScript.isRightToLeft(scriptCode);
    }

    public String getDisplayLanguage() {
        return ULocale.getDisplayLanguageInternal(this, ULocale.getDefault(Category.DISPLAY), false);
    }

    public String getDisplayLanguage(ULocale displayLocale) {
        return ULocale.getDisplayLanguageInternal(this, displayLocale, false);
    }

    public static String getDisplayLanguage(String localeID, String displayLocaleID) {
        return ULocale.getDisplayLanguageInternal(new ULocale(localeID), new ULocale(displayLocaleID), false);
    }

    public static String getDisplayLanguage(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayLanguageInternal(new ULocale(localeID), displayLocale, false);
    }

    public String getDisplayLanguageWithDialect() {
        return ULocale.getDisplayLanguageInternal(this, ULocale.getDefault(Category.DISPLAY), true);
    }

    public String getDisplayLanguageWithDialect(ULocale displayLocale) {
        return ULocale.getDisplayLanguageInternal(this, displayLocale, true);
    }

    public static String getDisplayLanguageWithDialect(String localeID, String displayLocaleID) {
        return ULocale.getDisplayLanguageInternal(new ULocale(localeID), new ULocale(displayLocaleID), true);
    }

    public static String getDisplayLanguageWithDialect(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayLanguageInternal(new ULocale(localeID), displayLocale, true);
    }

    private static String getDisplayLanguageInternal(ULocale locale, ULocale displayLocale, boolean useDialect) {
        String lang = useDialect ? locale.getBaseName() : locale.getLanguage();
        return LocaleDisplayNames.getInstance(displayLocale).languageDisplayName(lang);
    }

    public String getDisplayScript() {
        return ULocale.getDisplayScriptInternal(this, ULocale.getDefault(Category.DISPLAY));
    }

    @Deprecated
    public String getDisplayScriptInContext() {
        return ULocale.getDisplayScriptInContextInternal(this, ULocale.getDefault(Category.DISPLAY));
    }

    public String getDisplayScript(ULocale displayLocale) {
        return ULocale.getDisplayScriptInternal(this, displayLocale);
    }

    @Deprecated
    public String getDisplayScriptInContext(ULocale displayLocale) {
        return ULocale.getDisplayScriptInContextInternal(this, displayLocale);
    }

    public static String getDisplayScript(String localeID, String displayLocaleID) {
        return ULocale.getDisplayScriptInternal(new ULocale(localeID), new ULocale(displayLocaleID));
    }

    @Deprecated
    public static String getDisplayScriptInContext(String localeID, String displayLocaleID) {
        return ULocale.getDisplayScriptInContextInternal(new ULocale(localeID), new ULocale(displayLocaleID));
    }

    public static String getDisplayScript(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayScriptInternal(new ULocale(localeID), displayLocale);
    }

    @Deprecated
    public static String getDisplayScriptInContext(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayScriptInContextInternal(new ULocale(localeID), displayLocale);
    }

    private static String getDisplayScriptInternal(ULocale locale, ULocale displayLocale) {
        return LocaleDisplayNames.getInstance(displayLocale).scriptDisplayName(locale.getScript());
    }

    private static String getDisplayScriptInContextInternal(ULocale locale, ULocale displayLocale) {
        return LocaleDisplayNames.getInstance(displayLocale).scriptDisplayNameInContext(locale.getScript());
    }

    public String getDisplayCountry() {
        return ULocale.getDisplayCountryInternal(this, ULocale.getDefault(Category.DISPLAY));
    }

    public String getDisplayCountry(ULocale displayLocale) {
        return ULocale.getDisplayCountryInternal(this, displayLocale);
    }

    public static String getDisplayCountry(String localeID, String displayLocaleID) {
        return ULocale.getDisplayCountryInternal(new ULocale(localeID), new ULocale(displayLocaleID));
    }

    public static String getDisplayCountry(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayCountryInternal(new ULocale(localeID), displayLocale);
    }

    private static String getDisplayCountryInternal(ULocale locale, ULocale displayLocale) {
        return LocaleDisplayNames.getInstance(displayLocale).regionDisplayName(locale.getCountry());
    }

    public String getDisplayVariant() {
        return ULocale.getDisplayVariantInternal(this, ULocale.getDefault(Category.DISPLAY));
    }

    public String getDisplayVariant(ULocale displayLocale) {
        return ULocale.getDisplayVariantInternal(this, displayLocale);
    }

    public static String getDisplayVariant(String localeID, String displayLocaleID) {
        return ULocale.getDisplayVariantInternal(new ULocale(localeID), new ULocale(displayLocaleID));
    }

    public static String getDisplayVariant(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayVariantInternal(new ULocale(localeID), displayLocale);
    }

    private static String getDisplayVariantInternal(ULocale locale, ULocale displayLocale) {
        return LocaleDisplayNames.getInstance(displayLocale).variantDisplayName(locale.getVariant());
    }

    public static String getDisplayKeyword(String keyword) {
        return ULocale.getDisplayKeywordInternal(keyword, ULocale.getDefault(Category.DISPLAY));
    }

    public static String getDisplayKeyword(String keyword, String displayLocaleID) {
        return ULocale.getDisplayKeywordInternal(keyword, new ULocale(displayLocaleID));
    }

    public static String getDisplayKeyword(String keyword, ULocale displayLocale) {
        return ULocale.getDisplayKeywordInternal(keyword, displayLocale);
    }

    private static String getDisplayKeywordInternal(String keyword, ULocale displayLocale) {
        return LocaleDisplayNames.getInstance(displayLocale).keyDisplayName(keyword);
    }

    public String getDisplayKeywordValue(String keyword) {
        return ULocale.getDisplayKeywordValueInternal(this, keyword, ULocale.getDefault(Category.DISPLAY));
    }

    public String getDisplayKeywordValue(String keyword, ULocale displayLocale) {
        return ULocale.getDisplayKeywordValueInternal(this, keyword, displayLocale);
    }

    public static String getDisplayKeywordValue(String localeID, String keyword, String displayLocaleID) {
        return ULocale.getDisplayKeywordValueInternal(new ULocale(localeID), keyword, new ULocale(displayLocaleID));
    }

    public static String getDisplayKeywordValue(String localeID, String keyword, ULocale displayLocale) {
        return ULocale.getDisplayKeywordValueInternal(new ULocale(localeID), keyword, displayLocale);
    }

    private static String getDisplayKeywordValueInternal(ULocale locale, String keyword, ULocale displayLocale) {
        keyword = AsciiUtil.toLowerString(keyword.trim());
        String value = locale.getKeywordValue(keyword);
        return LocaleDisplayNames.getInstance(displayLocale).keyValueDisplayName(keyword, value);
    }

    public String getDisplayName() {
        return ULocale.getDisplayNameInternal(this, ULocale.getDefault(Category.DISPLAY));
    }

    public String getDisplayName(ULocale displayLocale) {
        return ULocale.getDisplayNameInternal(this, displayLocale);
    }

    public static String getDisplayName(String localeID, String displayLocaleID) {
        return ULocale.getDisplayNameInternal(new ULocale(localeID), new ULocale(displayLocaleID));
    }

    public static String getDisplayName(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayNameInternal(new ULocale(localeID), displayLocale);
    }

    private static String getDisplayNameInternal(ULocale locale, ULocale displayLocale) {
        return LocaleDisplayNames.getInstance(displayLocale).localeDisplayName(locale);
    }

    public String getDisplayNameWithDialect() {
        return ULocale.getDisplayNameWithDialectInternal(this, ULocale.getDefault(Category.DISPLAY));
    }

    public String getDisplayNameWithDialect(ULocale displayLocale) {
        return ULocale.getDisplayNameWithDialectInternal(this, displayLocale);
    }

    public static String getDisplayNameWithDialect(String localeID, String displayLocaleID) {
        return ULocale.getDisplayNameWithDialectInternal(new ULocale(localeID), new ULocale(displayLocaleID));
    }

    public static String getDisplayNameWithDialect(String localeID, ULocale displayLocale) {
        return ULocale.getDisplayNameWithDialectInternal(new ULocale(localeID), displayLocale);
    }

    private static String getDisplayNameWithDialectInternal(ULocale locale, ULocale displayLocale) {
        return LocaleDisplayNames.getInstance(displayLocale, LocaleDisplayNames.DialectHandling.DIALECT_NAMES).localeDisplayName(locale);
    }

    public String getCharacterOrientation() {
        return ICUResourceTableAccess.getTableString("com/ibm/icu/impl/data/icudt66b", this, "layout", "characters", "characters");
    }

    public String getLineOrientation() {
        return ICUResourceTableAccess.getTableString("com/ibm/icu/impl/data/icudt66b", this, "layout", "lines", "lines");
    }

    public static ULocale acceptLanguage(String acceptLanguageList, ULocale[] availableLocales, boolean[] fallback) {
        if (acceptLanguageList == null) {
            throw new NullPointerException();
        }
        ULocale[] acceptList = null;
        try {
            acceptList = ULocale.parseAcceptLanguage(acceptLanguageList, true);
        }
        catch (ParseException pe) {
            acceptList = null;
        }
        if (acceptList == null) {
            return null;
        }
        return ULocale.acceptLanguage(acceptList, availableLocales, fallback);
    }

    public static ULocale acceptLanguage(ULocale[] acceptLanguageList, ULocale[] availableLocales, boolean[] fallback) {
        if (fallback != null) {
            fallback[0] = true;
        }
        for (int i = 0; i < acceptLanguageList.length; ++i) {
            ULocale aLocale = acceptLanguageList[i];
            boolean[] setFallback = fallback;
            do {
                for (int j = 0; j < availableLocales.length; ++j) {
                    ULocale minAvail;
                    if (availableLocales[j].equals(aLocale)) {
                        if (setFallback != null) {
                            setFallback[0] = false;
                        }
                        return availableLocales[j];
                    }
                    if (aLocale.getScript().length() != 0 || availableLocales[j].getScript().length() <= 0 || !availableLocales[j].getLanguage().equals(aLocale.getLanguage()) || !availableLocales[j].getCountry().equals(aLocale.getCountry()) || !availableLocales[j].getVariant().equals(aLocale.getVariant()) || (minAvail = ULocale.minimizeSubtags(availableLocales[j])).getScript().length() != 0) continue;
                    if (setFallback != null) {
                        setFallback[0] = false;
                    }
                    return aLocale;
                }
                Locale loc = aLocale.toLocale();
                Locale parent = LocaleUtility.fallback(loc);
                aLocale = parent != null ? new ULocale(parent) : null;
                setFallback = null;
            } while (aLocale != null);
        }
        return null;
    }

    public static ULocale acceptLanguage(String acceptLanguageList, boolean[] fallback) {
        return ULocale.acceptLanguage(acceptLanguageList, ULocale.getAvailableLocales(), fallback);
    }

    public static ULocale acceptLanguage(ULocale[] acceptLanguageList, boolean[] fallback) {
        return ULocale.acceptLanguage(acceptLanguageList, ULocale.getAvailableLocales(), fallback);
    }

    static ULocale[] parseAcceptLanguage(String acceptLanguage, boolean isLenient) throws ParseException {
        int n;
        class ULocaleAcceptLanguageQ
        implements Comparable<ULocaleAcceptLanguageQ> {
            private double q;
            private double serial;

            public ULocaleAcceptLanguageQ(double theq, int theserial) {
                this.q = theq;
                this.serial = theserial;
            }

            @Override
            public int compareTo(ULocaleAcceptLanguageQ other) {
                if (this.q > other.q) {
                    return -1;
                }
                if (this.q < other.q) {
                    return 1;
                }
                if (this.serial < other.serial) {
                    return -1;
                }
                if (this.serial > other.serial) {
                    return 1;
                }
                return 0;
            }
        }
        TreeMap<ULocaleAcceptLanguageQ, ULocale> map = new TreeMap<ULocaleAcceptLanguageQ, ULocale>();
        StringBuilder languageRangeBuf = new StringBuilder();
        StringBuilder qvalBuf = new StringBuilder();
        int state = 0;
        acceptLanguage = acceptLanguage + ",";
        boolean subTag = false;
        boolean q1 = false;
        for (n = 0; n < acceptLanguage.length(); ++n) {
            boolean gotLanguageQ = false;
            char c = acceptLanguage.charAt(n);
            switch (state) {
                case 0: {
                    if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z') {
                        languageRangeBuf.append(c);
                        state = 1;
                        subTag = false;
                        break;
                    }
                    if (c == '*') {
                        languageRangeBuf.append(c);
                        state = 2;
                        break;
                    }
                    if (c == ' ' || c == '\t') break;
                    state = -1;
                    break;
                }
                case 1: {
                    if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z') {
                        languageRangeBuf.append(c);
                        break;
                    }
                    if (c == '-') {
                        subTag = true;
                        languageRangeBuf.append(c);
                        break;
                    }
                    if (c == '_') {
                        if (isLenient) {
                            subTag = true;
                            languageRangeBuf.append(c);
                            break;
                        }
                        state = -1;
                        break;
                    }
                    if ('0' <= c && c <= '9') {
                        if (subTag) {
                            languageRangeBuf.append(c);
                            break;
                        }
                        state = -1;
                        break;
                    }
                    if (c == ',') {
                        gotLanguageQ = true;
                        break;
                    }
                    if (c == ' ' || c == '\t') {
                        state = 3;
                        break;
                    }
                    if (c == ';') {
                        state = 4;
                        break;
                    }
                    state = -1;
                    break;
                }
                case 2: {
                    if (c == ',') {
                        gotLanguageQ = true;
                        break;
                    }
                    if (c == ' ' || c == '\t') {
                        state = 3;
                        break;
                    }
                    if (c == ';') {
                        state = 4;
                        break;
                    }
                    state = -1;
                    break;
                }
                case 3: {
                    if (c == ',') {
                        gotLanguageQ = true;
                        break;
                    }
                    if (c == ';') {
                        state = 4;
                        break;
                    }
                    if (c == ' ' || c == '\t') break;
                    state = -1;
                    break;
                }
                case 4: {
                    if (c == 'q') {
                        state = 5;
                        break;
                    }
                    if (c == ' ' || c == '\t') break;
                    state = -1;
                    break;
                }
                case 5: {
                    if (c == '=') {
                        state = 6;
                        break;
                    }
                    if (c == ' ' || c == '\t') break;
                    state = -1;
                    break;
                }
                case 6: {
                    if (c == '0') {
                        q1 = false;
                        qvalBuf.append(c);
                        state = 7;
                        break;
                    }
                    if (c == '1') {
                        qvalBuf.append(c);
                        state = 7;
                        break;
                    }
                    if (c == '.') {
                        if (isLenient) {
                            qvalBuf.append(c);
                            state = 8;
                            break;
                        }
                        state = -1;
                        break;
                    }
                    if (c == ' ' || c == '\t') break;
                    state = -1;
                    break;
                }
                case 7: {
                    if (c == '.') {
                        qvalBuf.append(c);
                        state = 8;
                        break;
                    }
                    if (c == ',') {
                        gotLanguageQ = true;
                        break;
                    }
                    if (c == ' ' || c == '\t') {
                        state = 10;
                        break;
                    }
                    state = -1;
                    break;
                }
                case 8: {
                    if ('0' <= c && c <= '9') {
                        if (q1 && c != '0' && !isLenient) {
                            state = -1;
                            break;
                        }
                        qvalBuf.append(c);
                        state = 9;
                        break;
                    }
                    state = -1;
                    break;
                }
                case 9: {
                    if ('0' <= c && c <= '9') {
                        if (q1 && c != '0') {
                            state = -1;
                            break;
                        }
                        qvalBuf.append(c);
                        break;
                    }
                    if (c == ',') {
                        gotLanguageQ = true;
                        break;
                    }
                    if (c == ' ' || c == '\t') {
                        state = 10;
                        break;
                    }
                    state = -1;
                    break;
                }
                case 10: {
                    if (c == ',') {
                        gotLanguageQ = true;
                        break;
                    }
                    if (c == ' ' || c == '\t') break;
                    state = -1;
                }
            }
            if (state == -1) {
                throw new ParseException("Invalid Accept-Language", n);
            }
            if (!gotLanguageQ) continue;
            double q = 1.0;
            if (qvalBuf.length() != 0) {
                try {
                    q = Double.parseDouble(qvalBuf.toString());
                }
                catch (NumberFormatException nfe) {
                    q = 1.0;
                }
                if (q > 1.0) {
                    q = 1.0;
                }
            }
            if (languageRangeBuf.charAt(0) != '*') {
                int serial = map.size();
                ULocaleAcceptLanguageQ entry = new ULocaleAcceptLanguageQ(q, serial);
                map.put(entry, new ULocale(ULocale.canonicalize(languageRangeBuf.toString())));
            }
            languageRangeBuf.setLength(0);
            qvalBuf.setLength(0);
            state = 0;
        }
        if (state != 0) {
            throw new ParseException("Invalid AcceptlLanguage", n);
        }
        ULocale[] acceptList = map.values().toArray(new ULocale[map.size()]);
        return acceptList;
    }

    public static ULocale addLikelySubtags(ULocale loc) {
        String newLocaleID;
        String[] tags = new String[3];
        String trailing = null;
        int trailingIndex = ULocale.parseTagString(loc.localeID, tags);
        if (trailingIndex < loc.localeID.length()) {
            trailing = loc.localeID.substring(trailingIndex);
        }
        return (newLocaleID = ULocale.createLikelySubtagsString(tags[0], tags[1], tags[2], trailing)) == null ? loc : new ULocale(newLocaleID);
    }

    public static ULocale minimizeSubtags(ULocale loc) {
        return ULocale.minimizeSubtags(loc, Minimize.FAVOR_REGION);
    }

    @Deprecated
    public static ULocale minimizeSubtags(ULocale loc, Minimize fieldToFavor) {
        String maximizedLocaleID;
        String[] tags = new String[3];
        int trailingIndex = ULocale.parseTagString(loc.localeID, tags);
        String originalLang = tags[0];
        String originalScript = tags[1];
        String originalRegion = tags[2];
        String originalTrailing = null;
        if (trailingIndex < loc.localeID.length()) {
            originalTrailing = loc.localeID.substring(trailingIndex);
        }
        if (ULocale.isEmptyString(maximizedLocaleID = ULocale.createLikelySubtagsString(originalLang, originalScript, originalRegion, null))) {
            return loc;
        }
        String tag = ULocale.createLikelySubtagsString(originalLang, null, null, null);
        if (tag.equals(maximizedLocaleID)) {
            String newLocaleID = ULocale.createTagString(originalLang, null, null, originalTrailing);
            return new ULocale(newLocaleID);
        }
        if (fieldToFavor == Minimize.FAVOR_REGION) {
            if (originalRegion.length() != 0 && (tag = ULocale.createLikelySubtagsString(originalLang, null, originalRegion, null)).equals(maximizedLocaleID)) {
                String newLocaleID = ULocale.createTagString(originalLang, null, originalRegion, originalTrailing);
                return new ULocale(newLocaleID);
            }
            if (originalScript.length() != 0 && (tag = ULocale.createLikelySubtagsString(originalLang, originalScript, null, null)).equals(maximizedLocaleID)) {
                String newLocaleID = ULocale.createTagString(originalLang, originalScript, null, originalTrailing);
                return new ULocale(newLocaleID);
            }
        } else {
            if (originalScript.length() != 0 && (tag = ULocale.createLikelySubtagsString(originalLang, originalScript, null, null)).equals(maximizedLocaleID)) {
                String newLocaleID = ULocale.createTagString(originalLang, originalScript, null, originalTrailing);
                return new ULocale(newLocaleID);
            }
            if (originalRegion.length() != 0 && (tag = ULocale.createLikelySubtagsString(originalLang, null, originalRegion, null)).equals(maximizedLocaleID)) {
                String newLocaleID = ULocale.createTagString(originalLang, null, originalRegion, originalTrailing);
                return new ULocale(newLocaleID);
            }
        }
        return loc;
    }

    private static boolean isEmptyString(String string) {
        return string == null || string.length() == 0;
    }

    private static void appendTag(String tag, StringBuilder buffer) {
        if (buffer.length() != 0) {
            buffer.append('_');
        }
        buffer.append(tag);
    }

    private static String createTagString(String lang, String script, String region, String trailing, String alternateTags) {
        LocaleIDParser parser = null;
        boolean regionAppended = false;
        StringBuilder tag = new StringBuilder();
        if (!ULocale.isEmptyString(lang)) {
            ULocale.appendTag(lang, tag);
        } else if (ULocale.isEmptyString(alternateTags)) {
            ULocale.appendTag(UNDEFINED_LANGUAGE, tag);
        } else {
            parser = new LocaleIDParser(alternateTags);
            String alternateLang = parser.getLanguage();
            ULocale.appendTag(!ULocale.isEmptyString(alternateLang) ? alternateLang : UNDEFINED_LANGUAGE, tag);
        }
        if (!ULocale.isEmptyString(script)) {
            ULocale.appendTag(script, tag);
        } else if (!ULocale.isEmptyString(alternateTags)) {
            String alternateScript;
            if (parser == null) {
                parser = new LocaleIDParser(alternateTags);
            }
            if (!ULocale.isEmptyString(alternateScript = parser.getScript())) {
                ULocale.appendTag(alternateScript, tag);
            }
        }
        if (!ULocale.isEmptyString(region)) {
            ULocale.appendTag(region, tag);
            regionAppended = true;
        } else if (!ULocale.isEmptyString(alternateTags)) {
            String alternateRegion;
            if (parser == null) {
                parser = new LocaleIDParser(alternateTags);
            }
            if (!ULocale.isEmptyString(alternateRegion = parser.getCountry())) {
                ULocale.appendTag(alternateRegion, tag);
                regionAppended = true;
            }
        }
        if (trailing != null && trailing.length() > 1) {
            int separators = 0;
            if (trailing.charAt(0) == '_') {
                if (trailing.charAt(1) == '_') {
                    separators = 2;
                }
            } else {
                separators = 1;
            }
            if (regionAppended) {
                if (separators == 2) {
                    tag.append(trailing.substring(1));
                } else {
                    tag.append(trailing);
                }
            } else {
                if (separators == 1) {
                    tag.append('_');
                }
                tag.append(trailing);
            }
        }
        return tag.toString();
    }

    static String createTagString(String lang, String script, String region, String trailing) {
        return ULocale.createTagString(lang, script, region, trailing, null);
    }

    private static int parseTagString(String localeID, String[] tags) {
        LocaleIDParser parser = new LocaleIDParser(localeID);
        String lang = parser.getLanguage();
        String script = parser.getScript();
        String region = parser.getCountry();
        tags[0] = ULocale.isEmptyString(lang) ? UNDEFINED_LANGUAGE : lang;
        tags[1] = script.equals(UNDEFINED_SCRIPT) ? EMPTY_STRING : script;
        tags[2] = region.equals(UNDEFINED_REGION) ? EMPTY_STRING : region;
        String variant = parser.getVariant();
        if (!ULocale.isEmptyString(variant)) {
            int index = localeID.indexOf(variant);
            return index > 0 ? index - 1 : index;
        }
        int index = localeID.indexOf(64);
        return index == -1 ? localeID.length() : index;
    }

    private static String lookupLikelySubtags(String localeId) {
        UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b", "likelySubtags");
        try {
            return bundle.getString(localeId);
        }
        catch (MissingResourceException e) {
            return null;
        }
    }

    private static String createLikelySubtagsString(String lang, String script, String region, String variants) {
        String searchTag;
        String likelySubtags;
        if (!ULocale.isEmptyString(script) && !ULocale.isEmptyString(region) && (likelySubtags = ULocale.lookupLikelySubtags(searchTag = ULocale.createTagString(lang, script, region, null))) != null) {
            return ULocale.createTagString(null, null, null, variants, likelySubtags);
        }
        if (!ULocale.isEmptyString(script) && (likelySubtags = ULocale.lookupLikelySubtags(searchTag = ULocale.createTagString(lang, script, null, null))) != null) {
            return ULocale.createTagString(null, null, region, variants, likelySubtags);
        }
        if (!ULocale.isEmptyString(region) && (likelySubtags = ULocale.lookupLikelySubtags(searchTag = ULocale.createTagString(lang, null, region, null))) != null) {
            return ULocale.createTagString(null, script, null, variants, likelySubtags);
        }
        searchTag = ULocale.createTagString(lang, null, null, null);
        likelySubtags = ULocale.lookupLikelySubtags(searchTag);
        if (likelySubtags != null) {
            return ULocale.createTagString(null, script, region, variants, likelySubtags);
        }
        return null;
    }

    public String getExtension(char key) {
        if (!LocaleExtensions.isValidKey(key)) {
            throw new IllegalArgumentException("Invalid extension key: " + key);
        }
        return this.extensions().getExtensionValue(Character.valueOf(key));
    }

    public Set<Character> getExtensionKeys() {
        return this.extensions().getKeys();
    }

    public Set<String> getUnicodeLocaleAttributes() {
        return this.extensions().getUnicodeLocaleAttributes();
    }

    public String getUnicodeLocaleType(String key) {
        if (!LocaleExtensions.isValidUnicodeLocaleKey(key)) {
            throw new IllegalArgumentException("Invalid Unicode locale key: " + key);
        }
        return this.extensions().getUnicodeLocaleType(key);
    }

    public Set<String> getUnicodeLocaleKeys() {
        return this.extensions().getUnicodeLocaleKeys();
    }

    public String toLanguageTag() {
        BaseLocale base = this.base();
        LocaleExtensions exts = this.extensions();
        if (base.getVariant().equalsIgnoreCase("POSIX")) {
            base = BaseLocale.getInstance(base.getLanguage(), base.getScript(), base.getRegion(), EMPTY_STRING);
            if (exts.getUnicodeLocaleType("va") == null) {
                InternalLocaleBuilder ilocbld = new InternalLocaleBuilder();
                try {
                    ilocbld.setLocale(BaseLocale.ROOT, exts);
                    ilocbld.setUnicodeLocaleKeyword("va", "posix");
                    exts = ilocbld.getLocaleExtensions();
                }
                catch (LocaleSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        LanguageTag tag = LanguageTag.parseLocale(base, exts);
        StringBuilder buf = new StringBuilder();
        String subtag = tag.getLanguage();
        if (subtag.length() > 0) {
            buf.append(LanguageTag.canonicalizeLanguage(subtag));
        }
        if ((subtag = tag.getScript()).length() > 0) {
            buf.append("-");
            buf.append(LanguageTag.canonicalizeScript(subtag));
        }
        if ((subtag = tag.getRegion()).length() > 0) {
            buf.append("-");
            buf.append(LanguageTag.canonicalizeRegion(subtag));
        }
        List<String> subtags = tag.getVariants();
        for (String s : subtags) {
            buf.append("-");
            buf.append(LanguageTag.canonicalizeVariant(s));
        }
        subtags = tag.getExtensions();
        for (String s : subtags) {
            buf.append("-");
            buf.append(LanguageTag.canonicalizeExtension(s));
        }
        subtag = tag.getPrivateuse();
        if (subtag.length() > 0) {
            if (buf.length() > 0) {
                buf.append("-");
            }
            buf.append("x").append("-");
            buf.append(LanguageTag.canonicalizePrivateuse(subtag));
        }
        return buf.toString();
    }

    public static ULocale forLanguageTag(String languageTag) {
        LanguageTag tag = LanguageTag.parse(languageTag, null);
        InternalLocaleBuilder bldr = new InternalLocaleBuilder();
        bldr.setLanguageTag(tag);
        return ULocale.getInstance(bldr.getBaseLocale(), bldr.getLocaleExtensions());
    }

    public static String toUnicodeLocaleKey(String keyword) {
        String bcpKey = KeyTypeData.toBcpKey(keyword);
        if (bcpKey == null && UnicodeLocaleExtension.isKey(keyword)) {
            bcpKey = AsciiUtil.toLowerString(keyword);
        }
        return bcpKey;
    }

    public static String toUnicodeLocaleType(String keyword, String value) {
        String bcpType = KeyTypeData.toBcpType(keyword, value, null, null);
        if (bcpType == null && UnicodeLocaleExtension.isType(value)) {
            bcpType = AsciiUtil.toLowerString(value);
        }
        return bcpType;
    }

    public static String toLegacyKey(String keyword) {
        String legacyKey = KeyTypeData.toLegacyKey(keyword);
        if (legacyKey == null && keyword.matches("[0-9a-zA-Z]+")) {
            legacyKey = AsciiUtil.toLowerString(keyword);
        }
        return legacyKey;
    }

    public static String toLegacyType(String keyword, String value) {
        String legacyType = KeyTypeData.toLegacyType(keyword, value, null, null);
        if (legacyType == null && value.matches("[0-9a-zA-Z]+([_/\\-][0-9a-zA-Z]+)*")) {
            legacyType = AsciiUtil.toLowerString(value);
        }
        return legacyType;
    }

    private static ULocale getInstance(BaseLocale base, LocaleExtensions exts) {
        String id = ULocale.lscvToID(base.getLanguage(), base.getScript(), base.getRegion(), base.getVariant());
        Set<Character> extKeys = exts.getKeys();
        if (!extKeys.isEmpty()) {
            TreeMap<String, String> kwds = new TreeMap<String, String>();
            for (Character key : extKeys) {
                Extension ext = exts.getExtension(key);
                if (ext instanceof UnicodeLocaleExtension) {
                    UnicodeLocaleExtension uext = (UnicodeLocaleExtension)ext;
                    Set<String> ukeys = uext.getUnicodeLocaleKeys();
                    for (String bcpKey : ukeys) {
                        String bcpType = uext.getUnicodeLocaleType(bcpKey);
                        String lkey = ULocale.toLegacyKey(bcpKey);
                        String ltype = ULocale.toLegacyType(bcpKey, bcpType.length() == 0 ? "yes" : bcpType);
                        if (lkey.equals("va") && ltype.equals("posix") && base.getVariant().length() == 0) {
                            id = id + "_POSIX";
                            continue;
                        }
                        kwds.put(lkey, ltype);
                    }
                    Set<String> uattributes = uext.getUnicodeLocaleAttributes();
                    if (uattributes.size() <= 0) continue;
                    StringBuilder attrbuf = new StringBuilder();
                    for (String attr : uattributes) {
                        if (attrbuf.length() > 0) {
                            attrbuf.append('-');
                        }
                        attrbuf.append(attr);
                    }
                    kwds.put(LOCALE_ATTRIBUTE_KEY, attrbuf.toString());
                    continue;
                }
                kwds.put(String.valueOf(key), ext.getValue());
            }
            if (!kwds.isEmpty()) {
                StringBuilder buf = new StringBuilder(id);
                buf.append("@");
                Set kset = kwds.entrySet();
                boolean insertSep = false;
                for (Map.Entry kwd : kset) {
                    if (insertSep) {
                        buf.append(";");
                    } else {
                        insertSep = true;
                    }
                    buf.append((String)kwd.getKey());
                    buf.append("=");
                    buf.append((String)kwd.getValue());
                }
                id = buf.toString();
            }
        }
        return new ULocale(id);
    }

    private BaseLocale base() {
        if (this.baseLocale == null) {
            String variant = EMPTY_STRING;
            String region = EMPTY_STRING;
            String script = EMPTY_STRING;
            String language = EMPTY_STRING;
            if (!this.equals(ROOT)) {
                LocaleIDParser lp = new LocaleIDParser(this.localeID);
                language = lp.getLanguage();
                script = lp.getScript();
                region = lp.getCountry();
                variant = lp.getVariant();
            }
            this.baseLocale = BaseLocale.getInstance(language, script, region, variant);
        }
        return this.baseLocale;
    }

    private LocaleExtensions extensions() {
        if (this.extensions == null) {
            Iterator<String> kwitr = this.getKeywords();
            if (kwitr == null) {
                this.extensions = LocaleExtensions.EMPTY_EXTENSIONS;
            } else {
                InternalLocaleBuilder intbld = new InternalLocaleBuilder();
                while (kwitr.hasNext()) {
                    String key = kwitr.next();
                    if (key.equals(LOCALE_ATTRIBUTE_KEY)) {
                        String[] uattributes;
                        for (String uattr : uattributes = this.getKeywordValue(key).split("[-_]")) {
                            try {
                                intbld.addUnicodeLocaleAttribute(uattr);
                            }
                            catch (LocaleSyntaxException localeSyntaxException) {
                                // empty catch block
                            }
                        }
                        continue;
                    }
                    if (key.length() >= 2) {
                        String bcpKey = ULocale.toUnicodeLocaleKey(key);
                        String bcpType = ULocale.toUnicodeLocaleType(key, this.getKeywordValue(key));
                        if (bcpKey == null || bcpType == null) continue;
                        try {
                            intbld.setUnicodeLocaleKeyword(bcpKey, bcpType);
                        }
                        catch (LocaleSyntaxException localeSyntaxException) {}
                        continue;
                    }
                    if (key.length() != 1 || key.charAt(0) == 'u') continue;
                    try {
                        intbld.setExtension(key.charAt(0), this.getKeywordValue(key).replace("_", "-"));
                    }
                    catch (LocaleSyntaxException localeSyntaxException) {
                    }
                }
                this.extensions = intbld.getLocaleExtensions();
            }
        }
        return this.extensions;
    }

    static {
        PRC = CHINA = new ULocale("zh_Hans_CN");
        TAIWAN = new ULocale("zh_Hant_TW");
        UK = new ULocale("en_GB", Locale.UK);
        US = new ULocale("en_US", Locale.US);
        CANADA = new ULocale("en_CA", Locale.CANADA);
        CANADA_FRENCH = new ULocale("fr_CA", Locale.CANADA_FRENCH);
        EMPTY_LOCALE = new Locale(EMPTY_STRING, EMPTY_STRING);
        ROOT = new ULocale(EMPTY_STRING, EMPTY_LOCALE);
        CACHE = new SoftCache<Locale, ULocale, Void>(){

            @Override
            protected ULocale createInstance(Locale key, Void unused) {
                return JDKLocaleHelper.toULocale(key);
            }
        };
        CANONICALIZE_MAP = new String[][]{{"art_LOJBAN", "jbo"}, {"cel_GAULISH", "cel__GAULISH"}, {"de_1901", "de__1901"}, {"de_1906", "de__1906"}, {"en_BOONT", "en__BOONT"}, {"en_SCOUSE", "en__SCOUSE"}, {"hy__AREVELA", "hy", null, null}, {"hy__AREVMDA", "hyw", null, null}, {"sl_ROZAJ", "sl__ROZAJ"}, {"zh_GAN", "zh__GAN"}, {"zh_GUOYU", "zh"}, {"zh_HAKKA", "zh__HAKKA"}, {"zh_MIN", "zh__MIN"}, {"zh_MIN_NAN", "zh__MINNAN"}, {"zh_WUU", "zh__WUU"}, {"zh_XIANG", "zh__XIANG"}, {"zh_YUE", "zh__YUE"}};
        defaultLocale = Locale.getDefault();
        defaultCategoryLocales = new Locale[Category.values().length];
        defaultCategoryULocales = new ULocale[Category.values().length];
        defaultULocale = ULocale.forLocale(defaultLocale);
        if (JDKLocaleHelper.hasLocaleCategories()) {
            for (Category cat : Category.values()) {
                int idx = cat.ordinal();
                ULocale.defaultCategoryLocales[idx] = JDKLocaleHelper.getDefault(cat);
                ULocale.defaultCategoryULocales[idx] = ULocale.forLocale(defaultCategoryLocales[idx]);
            }
        } else {
            for (Category cat : Category.values()) {
                int idx = cat.ordinal();
                ULocale.defaultCategoryLocales[idx] = defaultLocale;
                ULocale.defaultCategoryULocales[idx] = defaultULocale;
            }
        }
        ACTUAL_LOCALE = new Type();
        VALID_LOCALE = new Type();
    }

    private static final class JDKLocaleHelper {
        private static boolean hasLocaleCategories = false;
        private static Method mGetDefault;
        private static Method mSetDefault;
        private static Object eDISPLAY;
        private static Object eFORMAT;

        private JDKLocaleHelper() {
        }

        public static boolean hasLocaleCategories() {
            return hasLocaleCategories;
        }

        /*
         * WARNING - void declaration
         */
        public static ULocale toULocale(Locale loc) {
            String language = loc.getLanguage();
            String script = ULocale.EMPTY_STRING;
            String country = loc.getCountry();
            String variant = loc.getVariant();
            TreeSet<String> attributes = null;
            TreeMap<String, String> keywords = null;
            script = loc.getScript();
            Set<Character> extKeys = loc.getExtensionKeys();
            if (!extKeys.isEmpty()) {
                for (Character extKey : extKeys) {
                    if (extKey.charValue() == 'u') {
                        Set<String> uAttributes = loc.getUnicodeLocaleAttributes();
                        if (!uAttributes.isEmpty()) {
                            attributes = new TreeSet<String>();
                            for (String string : uAttributes) {
                                attributes.add(string);
                            }
                        }
                        Set<String> set = loc.getUnicodeLocaleKeys();
                        for (String kwKey : set) {
                            String kwVal = loc.getUnicodeLocaleType(kwKey);
                            if (kwVal == null) continue;
                            if (kwKey.equals("va")) {
                                variant = variant.length() == 0 ? kwVal : kwVal + "_" + variant;
                                continue;
                            }
                            if (keywords == null) {
                                keywords = new TreeMap();
                            }
                            keywords.put(kwKey, kwVal);
                        }
                        continue;
                    }
                    String extVal = loc.getExtension(extKey.charValue());
                    if (extVal == null) continue;
                    if (keywords == null) {
                        keywords = new TreeMap<String, String>();
                    }
                    keywords.put(String.valueOf(extKey), extVal);
                }
            }
            if (language.equals("no") && country.equals("NO") && variant.equals("NY")) {
                language = "nn";
                variant = ULocale.EMPTY_STRING;
            }
            StringBuilder buf = new StringBuilder(language);
            if (script.length() > 0) {
                buf.append('_');
                buf.append(script);
            }
            if (country.length() > 0) {
                buf.append('_');
                buf.append(country);
            }
            if (variant.length() > 0) {
                if (country.length() == 0) {
                    buf.append('_');
                }
                buf.append('_');
                buf.append(variant);
            }
            if (attributes != null) {
                StringBuilder attrBuf = new StringBuilder();
                for (String string : attributes) {
                    if (attrBuf.length() != 0) {
                        attrBuf.append('-');
                    }
                    attrBuf.append(string);
                }
                if (keywords == null) {
                    keywords = new TreeMap();
                }
                keywords.put(ULocale.LOCALE_ATTRIBUTE_KEY, attrBuf.toString());
            }
            if (keywords != null) {
                buf.append('@');
                boolean addSep = false;
                for (Map.Entry entry : keywords.entrySet()) {
                    void var12_25;
                    String string = (String)entry.getKey();
                    String kwVal = (String)entry.getValue();
                    if (string.length() != 1) {
                        String string2 = ULocale.toLegacyKey(string);
                        kwVal = ULocale.toLegacyType(string2, kwVal.length() == 0 ? "yes" : kwVal);
                    }
                    if (addSep) {
                        buf.append(';');
                    } else {
                        addSep = true;
                    }
                    buf.append((String)var12_25);
                    buf.append('=');
                    buf.append(kwVal);
                }
            }
            return new ULocale(ULocale.getName(buf.toString()), loc);
        }

        public static Locale toLocale(ULocale uloc) {
            Locale loc = null;
            String ulocStr = uloc.getName();
            if (uloc.getScript().length() > 0 || ulocStr.contains("@")) {
                String tag = uloc.toLanguageTag();
                tag = AsciiUtil.toUpperString(tag);
                loc = Locale.forLanguageTag(tag);
            }
            if (loc == null) {
                loc = new Locale(uloc.getLanguage(), uloc.getCountry(), uloc.getVariant());
            }
            return loc;
        }

        public static Locale getDefault(Category category) {
            if (hasLocaleCategories) {
                Object cat = null;
                switch (category) {
                    case DISPLAY: {
                        cat = eDISPLAY;
                        break;
                    }
                    case FORMAT: {
                        cat = eFORMAT;
                    }
                }
                if (cat != null) {
                    try {
                        return (Locale)mGetDefault.invoke(null, cat);
                    }
                    catch (InvocationTargetException invocationTargetException) {
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                    }
                    catch (IllegalAccessException illegalAccessException) {
                        // empty catch block
                    }
                }
            }
            return Locale.getDefault();
        }

        public static void setDefault(Category category, Locale newLocale) {
            if (hasLocaleCategories) {
                Object cat = null;
                switch (category) {
                    case DISPLAY: {
                        cat = eDISPLAY;
                        break;
                    }
                    case FORMAT: {
                        cat = eFORMAT;
                    }
                }
                if (cat != null) {
                    try {
                        mSetDefault.invoke(null, cat, newLocale);
                    }
                    catch (InvocationTargetException invocationTargetException) {
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                    }
                    catch (IllegalAccessException illegalAccessException) {
                        // empty catch block
                    }
                }
            }
        }

        static {
            try {
                Class<?>[] classes;
                Class<?> cCategory = null;
                for (Class<?> c : classes = Locale.class.getDeclaredClasses()) {
                    if (!c.getName().equals("java.util.Locale$Category")) continue;
                    cCategory = c;
                    break;
                }
                if (cCategory != null) {
                    ?[] enumConstants;
                    mGetDefault = Locale.class.getDeclaredMethod("getDefault", cCategory);
                    mSetDefault = Locale.class.getDeclaredMethod("setDefault", cCategory, Locale.class);
                    Method mName = cCategory.getMethod("name", null);
                    for (Object e : enumConstants = cCategory.getEnumConstants()) {
                        String catVal = (String)mName.invoke(e, (Object[])null);
                        if (catVal.equals("DISPLAY")) {
                            eDISPLAY = e;
                            continue;
                        }
                        if (!catVal.equals("FORMAT")) continue;
                        eFORMAT = e;
                    }
                    if (eDISPLAY != null && eFORMAT != null) {
                        hasLocaleCategories = true;
                    }
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (InvocationTargetException invocationTargetException) {
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
    }

    public static final class Builder {
        private final InternalLocaleBuilder _locbld = new InternalLocaleBuilder();

        public Builder setLocale(ULocale locale) {
            try {
                this._locbld.setLocale(locale.base(), locale.extensions());
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder setLanguageTag(String languageTag) {
            ParseStatus sts = new ParseStatus();
            LanguageTag tag = LanguageTag.parse(languageTag, sts);
            if (sts.isError()) {
                throw new IllformedLocaleException(sts.getErrorMessage(), sts.getErrorIndex());
            }
            this._locbld.setLanguageTag(tag);
            return this;
        }

        public Builder setLanguage(String language) {
            try {
                this._locbld.setLanguage(language);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder setScript(String script) {
            try {
                this._locbld.setScript(script);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder setRegion(String region) {
            try {
                this._locbld.setRegion(region);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder setVariant(String variant) {
            try {
                this._locbld.setVariant(variant);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder setExtension(char key, String value) {
            try {
                this._locbld.setExtension(key, value);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder setUnicodeLocaleKeyword(String key, String type) {
            try {
                this._locbld.setUnicodeLocaleKeyword(key, type);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder addUnicodeLocaleAttribute(String attribute) {
            try {
                this._locbld.addUnicodeLocaleAttribute(attribute);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder removeUnicodeLocaleAttribute(String attribute) {
            try {
                this._locbld.removeUnicodeLocaleAttribute(attribute);
            }
            catch (LocaleSyntaxException e) {
                throw new IllformedLocaleException(e.getMessage(), e.getErrorIndex());
            }
            return this;
        }

        public Builder clear() {
            this._locbld.clear();
            return this;
        }

        public Builder clearExtensions() {
            this._locbld.clearExtensions();
            return this;
        }

        public ULocale build() {
            return ULocale.getInstance(this._locbld.getBaseLocale(), this._locbld.getLocaleExtensions());
        }
    }

    @Deprecated
    public static enum Minimize {
        FAVOR_SCRIPT,
        FAVOR_REGION;

    }

    public static final class Type {
        private Type() {
        }
    }

    public static enum Category {
        DISPLAY,
        FORMAT;

    }

    public static enum AvailableType {
        DEFAULT,
        ONLY_LEGACY_ALIASES,
        WITH_LEGACY_ALIASES;

    }
}
