package com.ibm.icu.impl;

import com.ibm.icu.impl.CalType;
import com.ibm.icu.impl.Grego;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.util.ICUException;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.util.Arrays;

public class EraRules {
    private static final int MAX_ENCODED_START_YEAR = Short.MAX_VALUE;
    private static final int MIN_ENCODED_START_YEAR = Short.MIN_VALUE;
    public static final int MIN_ENCODED_START = EraRules.encodeDate(Short.MIN_VALUE, 1, 1);
    private static final int YEAR_MASK = -65536;
    private static final int MONTH_MASK = 65280;
    private static final int DAY_MASK = 255;
    private int[] startDates;
    private int numEras;
    private int currentEra;

    private EraRules(int[] startDates, int numEras) {
        this.startDates = startDates;
        this.numEras = numEras;
        this.initCurrentEra();
    }

    public static EraRules getInstance(CalType calType, boolean includeTentativeEra) {
        UResourceBundle supplementalDataRes = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt66b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
        UResourceBundle calendarDataRes = supplementalDataRes.get("calendarData");
        UResourceBundle calendarTypeRes = calendarDataRes.get(calType.getId());
        UResourceBundle erasRes = calendarTypeRes.get("eras");
        int numEras = erasRes.getSize();
        int firstTentativeIdx = Integer.MAX_VALUE;
        int[] startDates = new int[numEras];
        UResourceBundleIterator itr = erasRes.getIterator();
        while (itr.hasNext()) {
            UResourceBundle eraRuleRes = itr.next();
            String eraIdxStr = eraRuleRes.getKey();
            int eraIdx = -1;
            try {
                eraIdx = Integer.parseInt(eraIdxStr);
            }
            catch (NumberFormatException e) {
                throw new ICUException("Invald era rule key:" + eraIdxStr + " in era rule data for " + calType.getId());
            }
            if (eraIdx < 0 || eraIdx >= numEras) {
                throw new ICUException("Era rule key:" + eraIdxStr + " in era rule data for " + calType.getId() + " must be in range [0, " + (numEras - 1) + "]");
            }
            if (EraRules.isSet(startDates[eraIdx])) {
                throw new ICUException("Dupulicated era rule for rule key:" + eraIdxStr + " in era rule data for " + calType.getId());
            }
            boolean hasName = true;
            boolean hasEnd = false;
            UResourceBundleIterator ruleItr = eraRuleRes.getIterator();
            while (ruleItr.hasNext()) {
                UResourceBundle res = ruleItr.next();
                String key = res.getKey();
                if (key.equals("start")) {
                    int[] fields = res.getIntVector();
                    if (fields.length != 3 || !EraRules.isValidRuleStartDate(fields[0], fields[1], fields[2])) {
                        throw new ICUException("Invalid era rule date data:" + Arrays.toString(fields) + " in era rule data for " + calType.getId());
                    }
                    startDates[eraIdx] = EraRules.encodeDate(fields[0], fields[1], fields[2]);
                    continue;
                }
                if (key.equals("named")) {
                    String val = res.getString();
                    if (!val.equals("false")) continue;
                    hasName = false;
                    continue;
                }
                if (!key.equals("end")) continue;
                hasEnd = true;
            }
            if (EraRules.isSet(startDates[eraIdx])) {
                if (hasEnd) {
                    // empty if block
                }
            } else if (hasEnd) {
                if (eraIdx != 0) {
                    throw new ICUException("Era data for " + eraIdxStr + " in era rule data for " + calType.getId() + " has only end rule.");
                }
                startDates[eraIdx] = MIN_ENCODED_START;
            } else {
                throw new ICUException("Missing era start/end rule date for key:" + eraIdxStr + " in era rule data for " + calType.getId());
            }
            if (hasName) {
                if (eraIdx < firstTentativeIdx) continue;
                throw new ICUException("Non-tentative era(" + eraIdx + ") must be placed before the first tentative era");
            }
            if (eraIdx >= firstTentativeIdx) continue;
            firstTentativeIdx = eraIdx;
        }
        if (firstTentativeIdx < Integer.MAX_VALUE && !includeTentativeEra) {
            return new EraRules(startDates, firstTentativeIdx);
        }
        return new EraRules(startDates, numEras);
    }

    public int getNumberOfEras() {
        return this.numEras;
    }

    public int[] getStartDate(int eraIdx, int[] fillIn) {
        if (eraIdx < 0 || eraIdx >= this.numEras) {
            throw new IllegalArgumentException("eraIdx is out of range");
        }
        return EraRules.decodeDate(this.startDates[eraIdx], fillIn);
    }

    public int getStartYear(int eraIdx) {
        if (eraIdx < 0 || eraIdx >= this.numEras) {
            throw new IllegalArgumentException("eraIdx is out of range");
        }
        int[] fields = EraRules.decodeDate(this.startDates[eraIdx], null);
        return fields[0];
    }

    public int getEraIndex(int year, int month, int day) {
        if (month < 1 || month > 12 || day < 1 || day > 31) {
            throw new IllegalArgumentException("Illegal date - year:" + year + "month:" + month + "day:" + day);
        }
        int high = this.numEras;
        int low = EraRules.compareEncodedDateWithYMD(this.startDates[this.getCurrentEraIndex()], year, month, day) <= 0 ? this.getCurrentEraIndex() : 0;
        while (low < high - 1) {
            int i = (low + high) / 2;
            if (EraRules.compareEncodedDateWithYMD(this.startDates[i], year, month, day) <= 0) {
                low = i;
                continue;
            }
            high = i;
        }
        return low;
    }

    public int getCurrentEraIndex() {
        return this.currentEra;
    }

    private void initCurrentEra() {
        int eraIdx;
        long localMillis = System.currentTimeMillis();
        TimeZone zone = TimeZone.getDefault();
        localMillis += (long)zone.getOffset(localMillis);
        int[] fields = Grego.timeToFields(localMillis, null);
        int currentEncodedDate = EraRules.encodeDate(fields[0], fields[1] + 1, fields[2]);
        for (eraIdx = this.numEras - 1; eraIdx > 0 && currentEncodedDate < this.startDates[eraIdx]; --eraIdx) {
        }
        this.currentEra = eraIdx;
    }

    private static boolean isSet(int startDate) {
        return startDate != 0;
    }

    private static boolean isValidRuleStartDate(int year, int month, int day) {
        return year >= Short.MIN_VALUE && year <= Short.MAX_VALUE && month >= 1 && month <= 12 && day >= 1 && day <= 31;
    }

    private static int encodeDate(int year, int month, int day) {
        return year << 16 | month << 8 | day;
    }

    private static int[] decodeDate(int encodedDate, int[] fillIn) {
        int day;
        int month;
        int year;
        if (encodedDate == MIN_ENCODED_START) {
            year = Integer.MIN_VALUE;
            month = 1;
            day = 1;
        } else {
            year = (encodedDate & 0xFFFF0000) >> 16;
            month = (encodedDate & 0xFF00) >> 8;
            day = encodedDate & 0xFF;
        }
        if (fillIn != null && fillIn.length >= 3) {
            fillIn[0] = year;
            fillIn[1] = month;
            fillIn[2] = day;
            return fillIn;
        }
        int[] result = new int[]{year, month, day};
        return result;
    }

    private static int compareEncodedDateWithYMD(int encoded, int year, int month, int day) {
        if (year < Short.MIN_VALUE) {
            if (encoded == MIN_ENCODED_START) {
                if (year > Integer.MIN_VALUE || month > 1 || day > 1) {
                    return -1;
                }
                return 0;
            }
            return 1;
        }
        if (year > Short.MAX_VALUE) {
            return -1;
        }
        int tmp = EraRules.encodeDate(year, month, day);
        if (encoded < tmp) {
            return -1;
        }
        if (encoded == tmp) {
            return 0;
        }
        return 1;
    }
}
