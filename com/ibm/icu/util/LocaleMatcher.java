package com.ibm.icu.util;

import com.ibm.icu.impl.locale.LSR;
import com.ibm.icu.impl.locale.LocaleDistance;
import com.ibm.icu.impl.locale.XLikelySubtags;
import com.ibm.icu.util.LocalePriorityList;
import com.ibm.icu.util.ULocale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class LocaleMatcher {
    private static final LSR UND_LSR = new LSR("und", "", "");
    private static final ULocale UND_ULOCALE = new ULocale("und");
    private static final Locale UND_LOCALE = new Locale("und");
    private static final Locale EMPTY_LOCALE = new Locale("");
    private static final boolean TRACE_MATCHER = false;
    private final int thresholdDistance;
    private final int demotionPerDesiredLocale;
    private final FavorSubtag favorSubtag;
    private final ULocale[] supportedULocales;
    private final Locale[] supportedLocales;
    private final Map<LSR, Integer> supportedLsrToIndex;
    private final LSR[] supportedLSRs;
    private final int[] supportedIndexes;
    private final ULocale defaultULocale;
    private final Locale defaultLocale;
    private final int defaultLocaleIndex;

    public static Builder builder() {
        return new Builder();
    }

    public LocaleMatcher(LocalePriorityList supportedLocales) {
        this(LocaleMatcher.builder().setSupportedULocales(supportedLocales.getULocales()));
    }

    public LocaleMatcher(String supportedLocales) {
        this(LocaleMatcher.builder().setSupportedLocales(supportedLocales));
    }

    private LocaleMatcher(Builder builder) {
        this.thresholdDistance = builder.thresholdDistance < 0 ? LocaleDistance.INSTANCE.getDefaultScriptDistance() : builder.thresholdDistance;
        int supportedLocalesLength = builder.supportedLocales != null ? builder.supportedLocales.size() : 0;
        ULocale udef = builder.defaultLocale;
        Locale def = null;
        int idef = -1;
        this.supportedULocales = new ULocale[supportedLocalesLength];
        this.supportedLocales = new Locale[supportedLocalesLength];
        LSR[] lsrs = new LSR[supportedLocalesLength];
        LSR defLSR = null;
        if (udef != null) {
            def = udef.toLocale();
            defLSR = LocaleMatcher.getMaximalLsrOrUnd(udef);
        }
        int i = 0;
        if (supportedLocalesLength > 0) {
            Iterator iterator = builder.supportedLocales.iterator();
            while (iterator.hasNext()) {
                ULocale[] locale;
                this.supportedULocales[i] = locale = (ULocale[])iterator.next();
                this.supportedLocales[i] = locale.toLocale();
                LSR lsr = lsrs[i] = LocaleMatcher.getMaximalLsrOrUnd((ULocale)locale);
                if (idef < 0 && defLSR != null && lsr.equals(defLSR)) {
                    idef = i;
                }
                ++i;
            }
        }
        this.supportedLsrToIndex = new LinkedHashMap<LSR, Integer>(supportedLocalesLength);
        LinkedHashMap otherLsrToIndex = null;
        if (idef >= 0) {
            this.supportedLsrToIndex.put(defLSR, idef);
        }
        i = 0;
        for (ULocale locale : this.supportedULocales) {
            if (i == idef) {
                ++i;
                continue;
            }
            LSR lsr = lsrs[i];
            if (defLSR == null) {
                assert (i == 0);
                udef = locale;
                def = this.supportedLocales[0];
                defLSR = lsr;
                idef = 0;
                this.supportedLsrToIndex.put(lsr, 0);
            } else if (idef < 0 || !lsr.equals(defLSR)) {
                if (LocaleDistance.INSTANCE.isParadigmLSR(lsr)) {
                    LocaleMatcher.putIfAbsent(this.supportedLsrToIndex, lsr, i);
                } else {
                    if (otherLsrToIndex == null) {
                        otherLsrToIndex = new LinkedHashMap(supportedLocalesLength);
                    }
                    LocaleMatcher.putIfAbsent(otherLsrToIndex, lsr, i);
                }
            }
            ++i;
        }
        if (otherLsrToIndex != null) {
            this.supportedLsrToIndex.putAll(otherLsrToIndex);
        }
        int supportedLSRsLength = this.supportedLsrToIndex.size();
        this.supportedLSRs = new LSR[supportedLSRsLength];
        this.supportedIndexes = new int[supportedLSRsLength];
        i = 0;
        for (Map.Entry<LSR, Integer> entry : this.supportedLsrToIndex.entrySet()) {
            this.supportedLSRs[i] = entry.getKey();
            this.supportedIndexes[i++] = entry.getValue();
        }
        this.defaultULocale = udef;
        this.defaultLocale = def;
        this.defaultLocaleIndex = idef;
        this.demotionPerDesiredLocale = builder.demotion == Demotion.NONE ? 0 : LocaleDistance.INSTANCE.getDefaultDemotionPerDesiredLocale();
        this.favorSubtag = builder.favor;
    }

    private static final void putIfAbsent(Map<LSR, Integer> lsrToIndex, LSR lsr, int i) {
        Integer index = lsrToIndex.get(lsr);
        if (index == null) {
            lsrToIndex.put(lsr, i);
        }
    }

    private static final LSR getMaximalLsrOrUnd(ULocale locale) {
        if (locale.equals(UND_ULOCALE)) {
            return UND_LSR;
        }
        return XLikelySubtags.INSTANCE.makeMaximizedLsrFrom(locale);
    }

    private static final LSR getMaximalLsrOrUnd(Locale locale) {
        if (locale.equals(UND_LOCALE) || locale.equals(EMPTY_LOCALE)) {
            return UND_LSR;
        }
        return XLikelySubtags.INSTANCE.makeMaximizedLsrFrom(locale);
    }

    public ULocale getBestMatch(ULocale desiredLocale) {
        LSR desiredLSR = LocaleMatcher.getMaximalLsrOrUnd(desiredLocale);
        int suppIndex = this.getBestSuppIndex(desiredLSR, null);
        return suppIndex >= 0 ? this.supportedULocales[suppIndex] : this.defaultULocale;
    }

    public ULocale getBestMatch(Iterable<ULocale> desiredLocales) {
        Iterator<ULocale> desiredIter = desiredLocales.iterator();
        if (!desiredIter.hasNext()) {
            return this.defaultULocale;
        }
        ULocaleLsrIterator lsrIter = new ULocaleLsrIterator(desiredIter);
        LSR desiredLSR = lsrIter.next();
        int suppIndex = this.getBestSuppIndex(desiredLSR, lsrIter);
        return suppIndex >= 0 ? this.supportedULocales[suppIndex] : this.defaultULocale;
    }

    public ULocale getBestMatch(String desiredLocaleList) {
        return this.getBestMatch(LocalePriorityList.add(desiredLocaleList).build());
    }

    public Locale getBestLocale(Locale desiredLocale) {
        LSR desiredLSR = LocaleMatcher.getMaximalLsrOrUnd(desiredLocale);
        int suppIndex = this.getBestSuppIndex(desiredLSR, null);
        return suppIndex >= 0 ? this.supportedLocales[suppIndex] : this.defaultLocale;
    }

    public Locale getBestLocale(Iterable<Locale> desiredLocales) {
        Iterator<Locale> desiredIter = desiredLocales.iterator();
        if (!desiredIter.hasNext()) {
            return this.defaultLocale;
        }
        LocaleLsrIterator lsrIter = new LocaleLsrIterator(desiredIter);
        LSR desiredLSR = lsrIter.next();
        int suppIndex = this.getBestSuppIndex(desiredLSR, lsrIter);
        return suppIndex >= 0 ? this.supportedLocales[suppIndex] : this.defaultLocale;
    }

    private Result defaultResult() {
        return new Result(null, this.defaultULocale, null, this.defaultLocale, -1, this.defaultLocaleIndex);
    }

    private Result makeResult(ULocale desiredLocale, ULocaleLsrIterator lsrIter, int suppIndex) {
        if (suppIndex < 0) {
            return this.defaultResult();
        }
        if (desiredLocale != null) {
            return new Result(desiredLocale, this.supportedULocales[suppIndex], null, this.supportedLocales[suppIndex], 0, suppIndex);
        }
        return new Result(lsrIter.remembered, this.supportedULocales[suppIndex], null, this.supportedLocales[suppIndex], lsrIter.bestDesiredIndex, suppIndex);
    }

    private Result makeResult(Locale desiredLocale, LocaleLsrIterator lsrIter, int suppIndex) {
        if (suppIndex < 0) {
            return this.defaultResult();
        }
        if (desiredLocale != null) {
            return new Result(null, this.supportedULocales[suppIndex], desiredLocale, this.supportedLocales[suppIndex], 0, suppIndex);
        }
        return new Result(null, this.supportedULocales[suppIndex], lsrIter.remembered, this.supportedLocales[suppIndex], lsrIter.bestDesiredIndex, suppIndex);
    }

    public Result getBestMatchResult(ULocale desiredLocale) {
        LSR desiredLSR = LocaleMatcher.getMaximalLsrOrUnd(desiredLocale);
        int suppIndex = this.getBestSuppIndex(desiredLSR, null);
        return this.makeResult(desiredLocale, null, suppIndex);
    }

    public Result getBestMatchResult(Iterable<ULocale> desiredLocales) {
        Iterator<ULocale> desiredIter = desiredLocales.iterator();
        if (!desiredIter.hasNext()) {
            return this.defaultResult();
        }
        ULocaleLsrIterator lsrIter = new ULocaleLsrIterator(desiredIter);
        LSR desiredLSR = lsrIter.next();
        int suppIndex = this.getBestSuppIndex(desiredLSR, lsrIter);
        return this.makeResult(null, lsrIter, suppIndex);
    }

    public Result getBestLocaleResult(Locale desiredLocale) {
        LSR desiredLSR = LocaleMatcher.getMaximalLsrOrUnd(desiredLocale);
        int suppIndex = this.getBestSuppIndex(desiredLSR, null);
        return this.makeResult(desiredLocale, null, suppIndex);
    }

    public Result getBestLocaleResult(Iterable<Locale> desiredLocales) {
        Iterator<Locale> desiredIter = desiredLocales.iterator();
        if (!desiredIter.hasNext()) {
            return this.defaultResult();
        }
        LocaleLsrIterator lsrIter = new LocaleLsrIterator(desiredIter);
        LSR desiredLSR = lsrIter.next();
        int suppIndex = this.getBestSuppIndex(desiredLSR, lsrIter);
        return this.makeResult(null, lsrIter, suppIndex);
    }

    private int getBestSuppIndex(LSR desiredLSR, LsrIterator remainingIter) {
        int desiredIndex = 0;
        int bestSupportedLsrIndex = -1;
        int bestDistance = this.thresholdDistance;
        while (true) {
            Integer index;
            if ((index = this.supportedLsrToIndex.get(desiredLSR)) != null) {
                int suppIndex = index;
                if (remainingIter != null) {
                    remainingIter.rememberCurrent(desiredIndex);
                }
                return suppIndex;
            }
            int bestIndexAndDistance = LocaleDistance.INSTANCE.getBestIndexAndDistance(desiredLSR, this.supportedLSRs, bestDistance, this.favorSubtag);
            if (bestIndexAndDistance >= 0) {
                bestDistance = bestIndexAndDistance & 0xFF;
                if (remainingIter != null) {
                    remainingIter.rememberCurrent(desiredIndex);
                }
                bestSupportedLsrIndex = bestIndexAndDistance >> 8;
            }
            if ((bestDistance -= this.demotionPerDesiredLocale) <= 0 || remainingIter == null || !remainingIter.hasNext()) break;
            desiredLSR = (LSR)remainingIter.next();
            ++desiredIndex;
        }
        if (bestSupportedLsrIndex < 0) {
            return -1;
        }
        int suppIndex = this.supportedIndexes[bestSupportedLsrIndex];
        return suppIndex;
    }

    @Deprecated
    public double match(ULocale desired, ULocale desiredMax, ULocale supported, ULocale supportedMax) {
        int distance = LocaleDistance.INSTANCE.getBestIndexAndDistance(LocaleMatcher.getMaximalLsrOrUnd(desired), new LSR[]{LocaleMatcher.getMaximalLsrOrUnd(supported)}, this.thresholdDistance, this.favorSubtag) & 0xFF;
        return (double)(100 - distance) / 100.0;
    }

    public ULocale canonicalize(ULocale locale) {
        return XLikelySubtags.INSTANCE.canonicalize(locale);
    }

    public String toString() {
        StringBuilder s = new StringBuilder().append("{LocaleMatcher");
        if (this.supportedULocales.length > 0) {
            s.append(" supported={").append(this.supportedULocales[0].toString());
            for (int i = 1; i < this.supportedULocales.length; ++i) {
                s.append(", ").append(this.supportedULocales[i].toString());
            }
            s.append('}');
        }
        s.append(" default=").append(Objects.toString(this.defaultULocale));
        if (this.favorSubtag != null) {
            s.append(" distance=").append(this.favorSubtag.toString());
        }
        if (this.thresholdDistance >= 0) {
            s.append(String.format(" threshold=%d", this.thresholdDistance));
        }
        s.append(String.format(" demotion=%d", this.demotionPerDesiredLocale));
        return s.append('}').toString();
    }

    private static final class LocaleLsrIterator
    extends LsrIterator {
        private Iterator<Locale> locales;
        private Locale current;
        private Locale remembered;

        LocaleLsrIterator(Iterator<Locale> locales) {
            this.locales = locales;
        }

        @Override
        public boolean hasNext() {
            return this.locales.hasNext();
        }

        @Override
        public LSR next() {
            this.current = this.locales.next();
            return LocaleMatcher.getMaximalLsrOrUnd(this.current);
        }

        @Override
        public void rememberCurrent(int desiredIndex) {
            this.bestDesiredIndex = desiredIndex;
            this.remembered = this.current;
        }
    }

    private static final class ULocaleLsrIterator
    extends LsrIterator {
        private Iterator<ULocale> locales;
        private ULocale current;
        private ULocale remembered;

        ULocaleLsrIterator(Iterator<ULocale> locales) {
            this.locales = locales;
        }

        @Override
        public boolean hasNext() {
            return this.locales.hasNext();
        }

        @Override
        public LSR next() {
            this.current = this.locales.next();
            return LocaleMatcher.getMaximalLsrOrUnd(this.current);
        }

        @Override
        public void rememberCurrent(int desiredIndex) {
            this.bestDesiredIndex = desiredIndex;
            this.remembered = this.current;
        }
    }

    public static final class Builder {
        private List<ULocale> supportedLocales;
        private int thresholdDistance = -1;
        private Demotion demotion;
        private ULocale defaultLocale;
        private FavorSubtag favor;

        private Builder() {
        }

        public Builder setSupportedLocales(String locales) {
            return this.setSupportedULocales(LocalePriorityList.add(locales).build().getULocales());
        }

        public Builder setSupportedULocales(Collection<ULocale> locales) {
            this.supportedLocales = new ArrayList<ULocale>(locales);
            return this;
        }

        public Builder setSupportedLocales(Collection<Locale> locales) {
            this.supportedLocales = new ArrayList<ULocale>(locales.size());
            for (Locale locale : locales) {
                this.supportedLocales.add(ULocale.forLocale(locale));
            }
            return this;
        }

        public Builder addSupportedULocale(ULocale locale) {
            if (this.supportedLocales == null) {
                this.supportedLocales = new ArrayList<ULocale>();
            }
            this.supportedLocales.add(locale);
            return this;
        }

        public Builder addSupportedLocale(Locale locale) {
            return this.addSupportedULocale(ULocale.forLocale(locale));
        }

        public Builder setDefaultULocale(ULocale defaultLocale) {
            this.defaultLocale = defaultLocale;
            return this;
        }

        public Builder setDefaultLocale(Locale defaultLocale) {
            this.defaultLocale = ULocale.forLocale(defaultLocale);
            return this;
        }

        public Builder setFavorSubtag(FavorSubtag subtag) {
            this.favor = subtag;
            return this;
        }

        public Builder setDemotionPerDesiredLocale(Demotion demotion) {
            this.demotion = demotion;
            return this;
        }

        @Deprecated
        public Builder internalSetThresholdDistance(int thresholdDistance) {
            if (thresholdDistance > 100) {
                thresholdDistance = 100;
            }
            this.thresholdDistance = thresholdDistance;
            return this;
        }

        public LocaleMatcher build() {
            return new LocaleMatcher(this);
        }

        public String toString() {
            StringBuilder s = new StringBuilder().append("{LocaleMatcher.Builder");
            if (this.supportedLocales != null && !this.supportedLocales.isEmpty()) {
                s.append(" supported={").append(this.supportedLocales.toString()).append('}');
            }
            if (this.defaultLocale != null) {
                s.append(" default=").append(this.defaultLocale.toString());
            }
            if (this.favor != null) {
                s.append(" distance=").append(this.favor.toString());
            }
            if (this.thresholdDistance >= 0) {
                s.append(String.format(" threshold=%d", this.thresholdDistance));
            }
            if (this.demotion != null) {
                s.append(" demotion=").append(this.demotion.toString());
            }
            return s.append('}').toString();
        }
    }

    public static final class Result {
        private final ULocale desiredULocale;
        private final ULocale supportedULocale;
        private final Locale desiredLocale;
        private final Locale supportedLocale;
        private final int desiredIndex;
        private final int supportedIndex;

        private Result(ULocale udesired, ULocale usupported, Locale desired, Locale supported, int desIndex, int suppIndex) {
            this.desiredULocale = udesired;
            this.supportedULocale = usupported;
            this.desiredLocale = desired;
            this.supportedLocale = supported;
            this.desiredIndex = desIndex;
            this.supportedIndex = suppIndex;
        }

        public ULocale getDesiredULocale() {
            return this.desiredULocale == null && this.desiredLocale != null ? ULocale.forLocale(this.desiredLocale) : this.desiredULocale;
        }

        public Locale getDesiredLocale() {
            return this.desiredLocale == null && this.desiredULocale != null ? this.desiredULocale.toLocale() : this.desiredLocale;
        }

        public ULocale getSupportedULocale() {
            return this.supportedULocale;
        }

        public Locale getSupportedLocale() {
            return this.supportedLocale;
        }

        public int getDesiredIndex() {
            return this.desiredIndex;
        }

        public int getSupportedIndex() {
            return this.supportedIndex;
        }

        public ULocale makeResolvedULocale() {
            String variants;
            ULocale bestDesired = this.getDesiredULocale();
            if (this.supportedULocale == null || bestDesired == null || this.supportedULocale.equals(bestDesired)) {
                return this.supportedULocale;
            }
            ULocale.Builder b = new ULocale.Builder().setLocale(this.supportedULocale);
            String region = bestDesired.getCountry();
            if (!region.isEmpty()) {
                b.setRegion(region);
            }
            if (!(variants = bestDesired.getVariant()).isEmpty()) {
                b.setVariant(variants);
            }
            for (char extensionKey : bestDesired.getExtensionKeys()) {
                b.setExtension(extensionKey, bestDesired.getExtension(extensionKey));
            }
            return b.build();
        }

        public Locale makeResolvedLocale() {
            ULocale resolved = this.makeResolvedULocale();
            return resolved != null ? resolved.toLocale() : null;
        }
    }

    public static enum Demotion {
        NONE,
        REGION;

    }

    public static enum FavorSubtag {
        LANGUAGE,
        SCRIPT;

    }

    private static abstract class LsrIterator
    implements Iterator<LSR> {
        int bestDesiredIndex = -1;

        private LsrIterator() {
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        public abstract void rememberCurrent(int var1);
    }
}
