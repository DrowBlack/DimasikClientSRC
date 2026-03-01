package com.ibm.icu.impl;

import com.ibm.icu.impl.Relation;
import com.ibm.icu.lang.CharSequences;
import com.ibm.icu.util.ICUException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class StringRange {
    private static final boolean DEBUG = false;
    public static final Comparator<int[]> COMPARE_INT_ARRAYS = new Comparator<int[]>(){

        @Override
        public int compare(int[] o1, int[] o2) {
            int minIndex = Math.min(o1.length, o2.length);
            for (int i = 0; i < minIndex; ++i) {
                int diff = o1[i] - o2[i];
                if (diff == 0) continue;
                return diff;
            }
            return o1.length - o2.length;
        }
    };

    public static void compact(Set<String> source, Adder adder, boolean shorterPairs, boolean moreCompact) {
        if (!moreCompact) {
            String start = null;
            String end = null;
            boolean bl = false;
            int prefixLen = 0;
            for (String s : source) {
                int n;
                if (start != null) {
                    int currentCp;
                    if (s.regionMatches(0, start, 0, prefixLen) && (currentCp = s.codePointAt(prefixLen)) == 1 + n && s.length() == prefixLen + Character.charCount(currentCp)) {
                        end = s;
                        n = currentCp;
                        continue;
                    }
                    adder.add(start, end == null ? null : (!shorterPairs ? end : end.substring(prefixLen, end.length())));
                }
                start = s;
                end = null;
                n = s.codePointBefore(s.length());
                prefixLen = s.length() - Character.charCount(n);
            }
            adder.add(start, end == null ? null : (!shorterPairs ? end : end.substring(prefixLen, end.length())));
        } else {
            Relation<Integer, Ranges> lengthToArrays = Relation.of(new TreeMap(), TreeSet.class);
            for (String string : source) {
                Ranges item = new Ranges(string);
                lengthToArrays.put(item.size(), item);
            }
            for (Map.Entry entry : lengthToArrays.keyValuesSet()) {
                LinkedList<Ranges> compacted = StringRange.compact((Integer)entry.getKey(), (Set)entry.getValue());
                for (Ranges ranges : compacted) {
                    adder.add(ranges.start(), ranges.end(shorterPairs));
                }
            }
        }
    }

    public static void compact(Set<String> source, Adder adder, boolean shorterPairs) {
        StringRange.compact(source, adder, shorterPairs, false);
    }

    private static LinkedList<Ranges> compact(int size, Set<Ranges> inputRanges) {
        LinkedList<Ranges> ranges = new LinkedList<Ranges>(inputRanges);
        for (int i = size - 1; i >= 0; --i) {
            Ranges last = null;
            Iterator it = ranges.iterator();
            while (it.hasNext()) {
                Ranges item = (Ranges)it.next();
                if (last == null) {
                    last = item;
                    continue;
                }
                if (last.merge(i, item)) {
                    it.remove();
                    continue;
                }
                last = item;
            }
        }
        return ranges;
    }

    public static Collection<String> expand(String start, String end, boolean requireSameLength, Collection<String> output) {
        if (start == null || end == null) {
            throw new ICUException("Range must have 2 valid strings");
        }
        int[] startCps = CharSequences.codePoints(start);
        int[] endCps = CharSequences.codePoints(end);
        int startOffset = startCps.length - endCps.length;
        if (requireSameLength && startOffset != 0) {
            throw new ICUException("Range must have equal-length strings");
        }
        if (startOffset < 0) {
            throw new ICUException("Range must have start-length \u2265 end-length");
        }
        if (endCps.length == 0) {
            throw new ICUException("Range must have end-length > 0");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < startOffset; ++i) {
            builder.appendCodePoint(startCps[i]);
        }
        StringRange.add(0, startOffset, startCps, endCps, builder, output);
        return output;
    }

    private static void add(int endIndex, int startOffset, int[] starts, int[] ends, StringBuilder builder, Collection<String> output) {
        int start = starts[endIndex + startOffset];
        int end = ends[endIndex];
        if (start > end) {
            throw new ICUException("Range must have x\u1d62 \u2264 y\u1d62 for each index i");
        }
        boolean last = endIndex == ends.length - 1;
        int startLen = builder.length();
        for (int i = start; i <= end; ++i) {
            builder.appendCodePoint(i);
            if (last) {
                output.add(builder.toString());
            } else {
                StringRange.add(endIndex + 1, startOffset, starts, ends, builder, output);
            }
            builder.setLength(startLen);
        }
    }

    static final class Ranges
    implements Comparable<Ranges> {
        private final Range[] ranges;

        public Ranges(String s) {
            int[] array = CharSequences.codePoints(s);
            this.ranges = new Range[array.length];
            for (int i = 0; i < array.length; ++i) {
                this.ranges[i] = new Range(array[i], array[i]);
            }
        }

        public boolean merge(int pivot, Ranges other) {
            for (int i = this.ranges.length - 1; i >= 0; --i) {
                if (!(i == pivot ? this.ranges[i].max != other.ranges[i].min - 1 : !this.ranges[i].equals(other.ranges[i]))) continue;
                return false;
            }
            this.ranges[pivot].max = other.ranges[pivot].max;
            return true;
        }

        public String start() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < this.ranges.length; ++i) {
                result.appendCodePoint(this.ranges[i].min);
            }
            return result.toString();
        }

        public String end(boolean mostCompact) {
            int i;
            int firstDiff = this.firstDifference();
            if (firstDiff == this.ranges.length) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            int n = i = mostCompact ? firstDiff : 0;
            while (i < this.ranges.length) {
                result.appendCodePoint(this.ranges[i].max);
                ++i;
            }
            return result.toString();
        }

        public int firstDifference() {
            for (int i = 0; i < this.ranges.length; ++i) {
                if (this.ranges[i].min == this.ranges[i].max) continue;
                return i;
            }
            return this.ranges.length;
        }

        public Integer size() {
            return this.ranges.length;
        }

        @Override
        public int compareTo(Ranges other) {
            int diff = this.ranges.length - other.ranges.length;
            if (diff != 0) {
                return diff;
            }
            for (int i = 0; i < this.ranges.length; ++i) {
                diff = this.ranges[i].compareTo(other.ranges[i]);
                if (diff == 0) continue;
                return diff;
            }
            return 0;
        }

        public String toString() {
            String start = this.start();
            String end = this.end(false);
            return end == null ? start : start + "~" + end;
        }
    }

    static final class Range
    implements Comparable<Range> {
        int min;
        int max;

        public Range(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public boolean equals(Object obj) {
            return this == obj || obj != null && obj instanceof Range && this.compareTo((Range)obj) == 0;
        }

        @Override
        public int compareTo(Range that) {
            int diff = this.min - that.min;
            if (diff != 0) {
                return diff;
            }
            return this.max - that.max;
        }

        public int hashCode() {
            return this.min * 37 + this.max;
        }

        public String toString() {
            StringBuilder result = new StringBuilder().appendCodePoint(this.min);
            return this.min == this.max ? result.toString() : result.append('~').appendCodePoint(this.max).toString();
        }
    }

    public static interface Adder {
        public void add(String var1, String var2);
    }
}
