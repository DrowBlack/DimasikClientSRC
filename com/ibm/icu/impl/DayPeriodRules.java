package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.UResource;
import com.ibm.icu.util.ICUException;
import com.ibm.icu.util.ULocale;
import java.util.HashMap;
import java.util.Map;

public final class DayPeriodRules {
    private static final DayPeriodRulesData DATA = DayPeriodRules.loadData();
    private boolean hasMidnight = false;
    private boolean hasNoon = false;
    private DayPeriod[] dayPeriodForHour = new DayPeriod[24];

    private DayPeriodRules() {
    }

    public static DayPeriodRules getInstance(ULocale locale) {
        String localeCode = locale.getBaseName();
        if (localeCode.isEmpty()) {
            localeCode = "root";
        }
        Integer ruleSetNum = null;
        while (ruleSetNum == null && (ruleSetNum = DayPeriodRules.DATA.localesToRuleSetNumMap.get(localeCode)) == null && !(localeCode = ULocale.getFallback(localeCode)).isEmpty()) {
        }
        if (ruleSetNum == null || DayPeriodRules.DATA.rules[ruleSetNum] == null) {
            return null;
        }
        return DayPeriodRules.DATA.rules[ruleSetNum];
    }

    public double getMidPointForDayPeriod(DayPeriod dayPeriod) {
        int startHour = this.getStartHourForDayPeriod(dayPeriod);
        int endHour = this.getEndHourForDayPeriod(dayPeriod);
        double midPoint = (double)(startHour + endHour) / 2.0;
        if (startHour > endHour && (midPoint += 12.0) >= 24.0) {
            midPoint -= 24.0;
        }
        return midPoint;
    }

    private static DayPeriodRulesData loadData() {
        DayPeriodRulesData data = new DayPeriodRulesData();
        ICUResourceBundle rb = ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b", "dayPeriods", ICUResourceBundle.ICU_DATA_CLASS_LOADER, true);
        DayPeriodRulesCountSink countSink = new DayPeriodRulesCountSink(data);
        rb.getAllItemsWithFallback("rules", countSink);
        data.rules = new DayPeriodRules[data.maxRuleSetNum + 1];
        DayPeriodRulesDataSink sink = new DayPeriodRulesDataSink(data);
        rb.getAllItemsWithFallback("", sink);
        return data;
    }

    private int getStartHourForDayPeriod(DayPeriod dayPeriod) throws IllegalArgumentException {
        if (dayPeriod == DayPeriod.MIDNIGHT) {
            return 0;
        }
        if (dayPeriod == DayPeriod.NOON) {
            return 12;
        }
        if (this.dayPeriodForHour[0] == dayPeriod && this.dayPeriodForHour[23] == dayPeriod) {
            for (int i = 22; i >= 1; --i) {
                if (this.dayPeriodForHour[i] == dayPeriod) continue;
                return i + 1;
            }
        } else {
            for (int i = 0; i <= 23; ++i) {
                if (this.dayPeriodForHour[i] != dayPeriod) continue;
                return i;
            }
        }
        throw new IllegalArgumentException();
    }

    private int getEndHourForDayPeriod(DayPeriod dayPeriod) {
        if (dayPeriod == DayPeriod.MIDNIGHT) {
            return 0;
        }
        if (dayPeriod == DayPeriod.NOON) {
            return 12;
        }
        if (this.dayPeriodForHour[0] == dayPeriod && this.dayPeriodForHour[23] == dayPeriod) {
            for (int i = 1; i <= 22; ++i) {
                if (this.dayPeriodForHour[i] == dayPeriod) continue;
                return i;
            }
        } else {
            for (int i = 23; i >= 0; --i) {
                if (this.dayPeriodForHour[i] != dayPeriod) continue;
                return i + 1;
            }
        }
        throw new IllegalArgumentException();
    }

    public boolean hasMidnight() {
        return this.hasMidnight;
    }

    public boolean hasNoon() {
        return this.hasNoon;
    }

    public DayPeriod getDayPeriodForHour(int hour) {
        return this.dayPeriodForHour[hour];
    }

    private void add(int startHour, int limitHour, DayPeriod period) {
        for (int i = startHour; i != limitHour; ++i) {
            if (i == 24) {
                i = 0;
            }
            this.dayPeriodForHour[i] = period;
        }
    }

    private static int parseSetNum(String setNumStr) {
        if (!setNumStr.startsWith("set")) {
            throw new ICUException("Set number should start with \"set\".");
        }
        String numStr = setNumStr.substring(3);
        return Integer.parseInt(numStr);
    }

    private static class DayPeriodRulesCountSink
    extends UResource.Sink {
        private DayPeriodRulesData data;

        private DayPeriodRulesCountSink(DayPeriodRulesData data) {
            this.data = data;
        }

        @Override
        public void put(UResource.Key key, UResource.Value value, boolean noFallback) {
            UResource.Table rules = value.getTable();
            int i = 0;
            while (rules.getKeyAndValue(i, key, value)) {
                int setNum = DayPeriodRules.parseSetNum(key.toString());
                if (setNum > this.data.maxRuleSetNum) {
                    this.data.maxRuleSetNum = setNum;
                }
                ++i;
            }
        }
    }

    private static final class DayPeriodRulesDataSink
    extends UResource.Sink {
        private DayPeriodRulesData data;
        private int[] cutoffs = new int[25];
        private int ruleSetNum;
        private DayPeriod period;
        private CutoffType cutoffType;

        private DayPeriodRulesDataSink(DayPeriodRulesData data) {
            this.data = data;
        }

        @Override
        public void put(UResource.Key key, UResource.Value value, boolean noFallback) {
            UResource.Table dayPeriodData = value.getTable();
            int i = 0;
            while (dayPeriodData.getKeyAndValue(i, key, value)) {
                if (key.contentEquals("locales")) {
                    UResource.Table locales = value.getTable();
                    int j = 0;
                    while (locales.getKeyAndValue(j, key, value)) {
                        int setNum = DayPeriodRules.parseSetNum(value.getString());
                        this.data.localesToRuleSetNumMap.put(key.toString(), setNum);
                        ++j;
                    }
                } else if (key.contentEquals("rules")) {
                    UResource.Table rules = value.getTable();
                    this.processRules(rules, key, value);
                }
                ++i;
            }
        }

        private void processRules(UResource.Table rules, UResource.Key key, UResource.Value value) {
            int i = 0;
            while (rules.getKeyAndValue(i, key, value)) {
                this.ruleSetNum = DayPeriodRules.parseSetNum(key.toString());
                this.data.rules[this.ruleSetNum] = new DayPeriodRules();
                UResource.Table ruleSet = value.getTable();
                int j = 0;
                while (ruleSet.getKeyAndValue(j, key, value)) {
                    this.period = DayPeriod.fromStringOrNull(key);
                    if (this.period == null) {
                        throw new ICUException("Unknown day period in data.");
                    }
                    UResource.Table periodDefinition = value.getTable();
                    int k = 0;
                    while (periodDefinition.getKeyAndValue(k, key, value)) {
                        if (value.getType() == 0) {
                            CutoffType type = CutoffType.fromStringOrNull(key);
                            this.addCutoff(type, value.getString());
                        } else {
                            this.cutoffType = CutoffType.fromStringOrNull(key);
                            UResource.Array cutoffArray = value.getArray();
                            int length = cutoffArray.getSize();
                            for (int l = 0; l < length; ++l) {
                                cutoffArray.getValue(l, value);
                                this.addCutoff(this.cutoffType, value.getString());
                            }
                        }
                        ++k;
                    }
                    this.setDayPeriodForHoursFromCutoffs();
                    for (k = 0; k < this.cutoffs.length; ++k) {
                        this.cutoffs[k] = 0;
                    }
                    ++j;
                }
                for (DayPeriod period : this.data.rules[this.ruleSetNum].dayPeriodForHour) {
                    if (period != null) continue;
                    throw new ICUException("Rules in data don't cover all 24 hours (they should).");
                }
                ++i;
            }
        }

        private void addCutoff(CutoffType type, String hourStr) {
            int hour;
            if (type == null) {
                throw new ICUException("Cutoff type not recognized.");
            }
            int n = hour = DayPeriodRulesDataSink.parseHour(hourStr);
            this.cutoffs[n] = this.cutoffs[n] | 1 << type.ordinal();
        }

        private void setDayPeriodForHoursFromCutoffs() {
            DayPeriodRules rule = this.data.rules[this.ruleSetNum];
            block0: for (int startHour = 0; startHour <= 24; ++startHour) {
                if ((this.cutoffs[startHour] & 1 << CutoffType.AT.ordinal()) > 0) {
                    if (startHour == 0 && this.period == DayPeriod.MIDNIGHT) {
                        rule.hasMidnight = true;
                    } else if (startHour == 12 && this.period == DayPeriod.NOON) {
                        rule.hasNoon = true;
                    } else {
                        throw new ICUException("AT cutoff must only be set for 0:00 or 12:00.");
                    }
                }
                if ((this.cutoffs[startHour] & 1 << CutoffType.FROM.ordinal()) <= 0 && (this.cutoffs[startHour] & 1 << CutoffType.AFTER.ordinal()) <= 0) continue;
                int hour = startHour + 1;
                while (true) {
                    if (hour == startHour) {
                        throw new ICUException("FROM/AFTER cutoffs must have a matching BEFORE cutoff.");
                    }
                    if (hour == 25) {
                        hour = 0;
                    }
                    if ((this.cutoffs[hour] & 1 << CutoffType.BEFORE.ordinal()) > 0) {
                        rule.add(startHour, hour, this.period);
                        continue block0;
                    }
                    ++hour;
                }
            }
        }

        private static int parseHour(String str) {
            int firstColonPos = str.indexOf(58);
            if (firstColonPos < 0 || !str.substring(firstColonPos).equals(":00")) {
                throw new ICUException("Cutoff time must end in \":00\".");
            }
            String hourStr = str.substring(0, firstColonPos);
            if (firstColonPos != 1 && firstColonPos != 2) {
                throw new ICUException("Cutoff time must begin with h: or hh:");
            }
            int hour = Integer.parseInt(hourStr);
            if (hour < 0 || hour > 24) {
                throw new ICUException("Cutoff hour must be between 0 and 24, inclusive.");
            }
            return hour;
        }
    }

    private static final class DayPeriodRulesData {
        Map<String, Integer> localesToRuleSetNumMap = new HashMap<String, Integer>();
        DayPeriodRules[] rules;
        int maxRuleSetNum = -1;

        private DayPeriodRulesData() {
        }
    }

    private static enum CutoffType {
        BEFORE,
        AFTER,
        FROM,
        AT;


        private static CutoffType fromStringOrNull(CharSequence str) {
            if ("from".contentEquals(str)) {
                return FROM;
            }
            if ("before".contentEquals(str)) {
                return BEFORE;
            }
            if ("after".contentEquals(str)) {
                return AFTER;
            }
            if ("at".contentEquals(str)) {
                return AT;
            }
            return null;
        }
    }

    public static enum DayPeriod {
        MIDNIGHT,
        NOON,
        MORNING1,
        AFTERNOON1,
        EVENING1,
        NIGHT1,
        MORNING2,
        AFTERNOON2,
        EVENING2,
        NIGHT2,
        AM,
        PM;

        public static DayPeriod[] VALUES;

        private static DayPeriod fromStringOrNull(CharSequence str) {
            if ("midnight".contentEquals(str)) {
                return MIDNIGHT;
            }
            if ("noon".contentEquals(str)) {
                return NOON;
            }
            if ("morning1".contentEquals(str)) {
                return MORNING1;
            }
            if ("afternoon1".contentEquals(str)) {
                return AFTERNOON1;
            }
            if ("evening1".contentEquals(str)) {
                return EVENING1;
            }
            if ("night1".contentEquals(str)) {
                return NIGHT1;
            }
            if ("morning2".contentEquals(str)) {
                return MORNING2;
            }
            if ("afternoon2".contentEquals(str)) {
                return AFTERNOON2;
            }
            if ("evening2".contentEquals(str)) {
                return EVENING2;
            }
            if ("night2".contentEquals(str)) {
                return NIGHT2;
            }
            if ("am".contentEquals(str)) {
                return AM;
            }
            if ("pm".contentEquals(str)) {
                return PM;
            }
            return null;
        }

        static {
            VALUES = DayPeriod.values();
        }
    }
}
