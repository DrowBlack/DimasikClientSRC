package com.ibm.icu.impl.locale;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.UResource;
import com.ibm.icu.impl.locale.LSR;
import com.ibm.icu.impl.locale.XLikelySubtags;
import com.ibm.icu.util.BytesTrie;
import com.ibm.icu.util.LocaleMatcher;
import com.ibm.icu.util.ULocale;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;

public class LocaleDistance {
    public static final int END_OF_SUBTAG = 128;
    public static final int DISTANCE_SKIP_SCRIPT = 128;
    private static final int DISTANCE_IS_FINAL = 256;
    private static final int DISTANCE_IS_FINAL_OR_SKIP_SCRIPT = 384;
    public static final int IX_DEF_LANG_DISTANCE = 0;
    public static final int IX_DEF_SCRIPT_DISTANCE = 1;
    public static final int IX_DEF_REGION_DISTANCE = 2;
    public static final int IX_MIN_REGION_DISTANCE = 3;
    public static final int IX_LIMIT = 4;
    private static final int ABOVE_THRESHOLD = 100;
    private static final boolean DEBUG_OUTPUT = false;
    private final BytesTrie trie;
    private final byte[] regionToPartitionsIndex;
    private final String[] partitionArrays;
    private final Set<LSR> paradigmLSRs;
    private final int defaultLanguageDistance;
    private final int defaultScriptDistance;
    private final int defaultRegionDistance;
    private final int minRegionDistance;
    private final int defaultDemotionPerDesiredLocale;
    public static final LocaleDistance INSTANCE = new LocaleDistance(Data.load());

    private LocaleDistance(Data data) {
        this.trie = new BytesTrie(data.trie, 0);
        this.regionToPartitionsIndex = data.regionToPartitionsIndex;
        this.partitionArrays = data.partitionArrays;
        this.paradigmLSRs = data.paradigmLSRs;
        this.defaultLanguageDistance = data.distances[0];
        this.defaultScriptDistance = data.distances[1];
        this.defaultRegionDistance = data.distances[2];
        this.minRegionDistance = data.distances[3];
        LSR en = new LSR("en", "Latn", "US");
        LSR enGB = new LSR("en", "Latn", "GB");
        this.defaultDemotionPerDesiredLocale = this.getBestIndexAndDistance(en, new LSR[]{enGB}, 50, LocaleMatcher.FavorSubtag.LANGUAGE) & 0xFF;
    }

    public int testOnlyDistance(ULocale desired, ULocale supported, int threshold, LocaleMatcher.FavorSubtag favorSubtag) {
        LSR supportedLSR = XLikelySubtags.INSTANCE.makeMaximizedLsrFrom(supported);
        LSR desiredLSR = XLikelySubtags.INSTANCE.makeMaximizedLsrFrom(desired);
        return this.getBestIndexAndDistance(desiredLSR, new LSR[]{supportedLSR}, threshold, favorSubtag) & 0xFF;
    }

    public int getBestIndexAndDistance(LSR desired, LSR[] supportedLSRs, int threshold, LocaleMatcher.FavorSubtag favorSubtag) {
        BytesTrie iter = new BytesTrie(this.trie);
        int desLangDistance = LocaleDistance.trieNext(iter, desired.language, false);
        long desLangState = desLangDistance >= 0 && supportedLSRs.length > 1 ? iter.getState64() : 0L;
        int bestIndex = -1;
        for (int slIndex = 0; slIndex < supportedLSRs.length; ++slIndex) {
            int scriptDistance;
            int flags;
            LSR supported = supportedLSRs[slIndex];
            boolean star = false;
            int distance = desLangDistance;
            if (distance >= 0) {
                assert ((distance & 0x100) == 0);
                if (slIndex != 0) {
                    iter.resetToState64(desLangState);
                }
                distance = LocaleDistance.trieNext(iter, supported.language, true);
            }
            if (distance >= 0) {
                flags = distance & 0x180;
                distance &= 0xFFFFFE7F;
            } else {
                distance = desired.language.equals(supported.language) ? 0 : this.defaultLanguageDistance;
                flags = 0;
                star = true;
            }
            assert (0 <= distance && distance <= 100);
            if (favorSubtag == LocaleMatcher.FavorSubtag.SCRIPT) {
                distance >>= 2;
            }
            if (distance >= threshold) continue;
            if (star || flags != 0) {
                scriptDistance = desired.script.equals(supported.script) ? 0 : this.defaultScriptDistance;
            } else {
                scriptDistance = LocaleDistance.getDesSuppScriptDistance(iter, iter.getState64(), desired.script, supported.script);
                flags = scriptDistance & 0x100;
                scriptDistance &= 0xFFFFFEFF;
            }
            if ((distance += scriptDistance) >= threshold) continue;
            if (!desired.region.equals(supported.region)) {
                if (star || (flags & 0x100) != 0) {
                    distance += this.defaultRegionDistance;
                } else {
                    int remainingThreshold = threshold - distance;
                    if (this.minRegionDistance >= remainingThreshold) continue;
                    distance += LocaleDistance.getRegionPartitionsDistance(iter, iter.getState64(), this.partitionsForRegion(desired), this.partitionsForRegion(supported), remainingThreshold);
                }
            }
            if (distance >= threshold) continue;
            if (distance == 0) {
                return slIndex << 8;
            }
            bestIndex = slIndex;
            threshold = distance;
        }
        return bestIndex >= 0 ? bestIndex << 8 | threshold : -156;
    }

    private static final int getDesSuppScriptDistance(BytesTrie iter, long startState, String desired, String supported) {
        int distance = LocaleDistance.trieNext(iter, desired, false);
        if (distance >= 0) {
            distance = LocaleDistance.trieNext(iter, supported, true);
        }
        if (distance < 0) {
            BytesTrie.Result result = iter.resetToState64(startState).next(42);
            assert (result.hasValue());
            if (desired.equals(supported)) {
                distance = 0;
            } else {
                distance = iter.getValue();
                assert (distance >= 0);
            }
            if (result == BytesTrie.Result.FINAL_VALUE) {
                distance |= 0x100;
            }
        }
        return distance;
    }

    private static final int getRegionPartitionsDistance(BytesTrie iter, long startState, String desiredPartitions, String supportedPartitions, int threshold) {
        int desLength = desiredPartitions.length();
        int suppLength = supportedPartitions.length();
        if (desLength == 1 && suppLength == 1) {
            BytesTrie.Result result = iter.next(desiredPartitions.charAt(0) | 0x80);
            if (result.hasNext() && (result = iter.next(supportedPartitions.charAt(0) | 0x80)).hasValue()) {
                return iter.getValue();
            }
            return LocaleDistance.getFallbackRegionDistance(iter, startState);
        }
        int regionDistance = 0;
        boolean star = false;
        int di = 0;
        while (true) {
            BytesTrie.Result result;
            if ((result = iter.next(desiredPartitions.charAt(di++) | 0x80)).hasNext()) {
                long desState = suppLength > 1 ? iter.getState64() : 0L;
                int si = 0;
                while (true) {
                    int d;
                    if ((result = iter.next(supportedPartitions.charAt(si++) | 0x80)).hasValue()) {
                        d = iter.getValue();
                    } else if (star) {
                        d = 0;
                    } else {
                        d = LocaleDistance.getFallbackRegionDistance(iter, startState);
                        star = true;
                    }
                    if (d >= threshold) {
                        return d;
                    }
                    if (regionDistance < d) {
                        regionDistance = d;
                    }
                    if (si < suppLength) {
                        iter.resetToState64(desState);
                        continue;
                    }
                    break;
                }
            } else if (!star) {
                int d = LocaleDistance.getFallbackRegionDistance(iter, startState);
                if (d >= threshold) {
                    return d;
                }
                if (regionDistance < d) {
                    regionDistance = d;
                }
                star = true;
            }
            if (di >= desLength) break;
            iter.resetToState64(startState);
        }
        return regionDistance;
    }

    private static final int getFallbackRegionDistance(BytesTrie iter, long startState) {
        BytesTrie.Result result = iter.resetToState64(startState).next(42);
        assert (result.hasValue());
        int distance = iter.getValue();
        assert (distance >= 0);
        return distance;
    }

    private static final int trieNext(BytesTrie iter, String s, boolean wantValue) {
        if (s.isEmpty()) {
            return -1;
        }
        int i = 0;
        int end = s.length() - 1;
        while (true) {
            char c = s.charAt(i);
            if (i < end) {
                if (!iter.next(c).hasNext()) {
                    return -1;
                }
            } else {
                BytesTrie.Result result = iter.next(c | 0x80);
                if (wantValue) {
                    if (result.hasValue()) {
                        int value = iter.getValue();
                        if (result == BytesTrie.Result.FINAL_VALUE) {
                            value |= 0x100;
                        }
                        return value;
                    }
                } else if (result.hasNext()) {
                    return 0;
                }
                return -1;
            }
            ++i;
        }
    }

    public String toString() {
        return this.testOnlyGetDistanceTable().toString();
    }

    private String partitionsForRegion(LSR lsr) {
        byte pIndex = this.regionToPartitionsIndex[lsr.regionIndex];
        return this.partitionArrays[pIndex];
    }

    public boolean isParadigmLSR(LSR lsr) {
        return this.paradigmLSRs.contains(lsr);
    }

    public int getDefaultScriptDistance() {
        return this.defaultScriptDistance;
    }

    int getDefaultRegionDistance() {
        return this.defaultRegionDistance;
    }

    public int getDefaultDemotionPerDesiredLocale() {
        return this.defaultDemotionPerDesiredLocale;
    }

    public Map<String, Integer> testOnlyGetDistanceTable() {
        TreeMap<String, Integer> map = new TreeMap<String, Integer>();
        StringBuilder sb = new StringBuilder();
        for (BytesTrie.Entry entry : this.trie) {
            sb.setLength(0);
            int length = entry.bytesLength();
            for (int i = 0; i < length; ++i) {
                byte b = entry.byteAt(i);
                if (b == 42) {
                    sb.append("*-*-");
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
            map.put(sb.toString(), entry.value);
        }
        return map;
    }

    public void testOnlyPrintDistanceTable() {
        for (Map.Entry<String, Integer> mapping : this.testOnlyGetDistanceTable().entrySet()) {
            String suffix = "";
            int value = mapping.getValue();
            if ((value & 0x80) != 0) {
                value &= 0xFFFFFF7F;
                suffix = " skip script";
            }
            System.out.println(mapping.getKey() + '=' + value + suffix);
        }
    }

    public static final class Data {
        public byte[] trie;
        public byte[] regionToPartitionsIndex;
        public String[] partitionArrays;
        public Set<LSR> paradigmLSRs;
        public int[] distances;

        public Data(byte[] trie, byte[] regionToPartitionsIndex, String[] partitionArrays, Set<LSR> paradigmLSRs, int[] distances) {
            this.trie = trie;
            this.regionToPartitionsIndex = regionToPartitionsIndex;
            this.partitionArrays = partitionArrays;
            this.paradigmLSRs = paradigmLSRs;
            this.distances = distances;
        }

        private static UResource.Value getValue(UResource.Table table, String key, UResource.Value value) {
            if (!table.findValue(key, value)) {
                throw new MissingResourceException("langInfo.res missing data", "", "match/" + key);
            }
            return value;
        }

        public static Data load() throws MissingResourceException {
            int[] distances;
            Set<LSR> paradigmLSRs;
            ICUResourceBundle langInfo = ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b", "langInfo", ICUResourceBundle.ICU_DATA_CLASS_LOADER, ICUResourceBundle.OpenType.DIRECT);
            UResource.Value value = langInfo.getValueWithFallback("match");
            UResource.Table matchTable = value.getTable();
            ByteBuffer buffer = Data.getValue(matchTable, "trie", value).getBinary();
            byte[] trie = new byte[buffer.remaining()];
            buffer.get(trie);
            buffer = Data.getValue(matchTable, "regionToPartitions", value).getBinary();
            byte[] regionToPartitions = new byte[buffer.remaining()];
            buffer.get(regionToPartitions);
            if (regionToPartitions.length < 1677) {
                throw new MissingResourceException("langInfo.res binary data too short", "", "match/regionToPartitions");
            }
            String[] partitions = Data.getValue(matchTable, "partitions", value).getStringArray();
            if (matchTable.findValue("paradigms", value)) {
                String[] paradigms = value.getStringArray();
                paradigmLSRs = new HashSet(paradigms.length / 3);
                for (int i = 0; i < paradigms.length; i += 3) {
                    paradigmLSRs.add(new LSR(paradigms[i], paradigms[i + 1], paradigms[i + 2]));
                }
            } else {
                paradigmLSRs = Collections.emptySet();
            }
            if ((distances = Data.getValue(matchTable, "distances", value).getIntVector()).length < 4) {
                throw new MissingResourceException("langInfo.res intvector too short", "", "match/distances");
            }
            return new Data(trie, regionToPartitions, partitions, paradigmLSRs, distances);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!this.getClass().equals(other.getClass())) {
                return false;
            }
            Data od = (Data)other;
            return Arrays.equals(this.trie, od.trie) && Arrays.equals(this.regionToPartitionsIndex, od.regionToPartitionsIndex) && Arrays.equals(this.partitionArrays, od.partitionArrays) && this.paradigmLSRs.equals(od.paradigmLSRs) && Arrays.equals(this.distances, od.distances);
        }
    }
}
