package com.ibm.icu.impl.number.parse;

import com.ibm.icu.impl.StaticUnicodeSets;
import com.ibm.icu.impl.StringSegment;
import com.ibm.icu.impl.number.DecimalQuantity_DualStorageBCD;
import com.ibm.icu.impl.number.Grouper;
import com.ibm.icu.impl.number.parse.DecimalMatcher;
import com.ibm.icu.impl.number.parse.IgnorablesMatcher;
import com.ibm.icu.impl.number.parse.NumberParseMatcher;
import com.ibm.icu.impl.number.parse.ParsedNumber;
import com.ibm.icu.impl.number.parse.ParsingUtils;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.UnicodeSet;

public class ScientificMatcher
implements NumberParseMatcher {
    private final String exponentSeparatorString;
    private final DecimalMatcher exponentMatcher;
    private final IgnorablesMatcher ignorablesMatcher;
    private final String customMinusSign;
    private final String customPlusSign;

    public static ScientificMatcher getInstance(DecimalFormatSymbols symbols, Grouper grouper) {
        return new ScientificMatcher(symbols, grouper);
    }

    private ScientificMatcher(DecimalFormatSymbols symbols, Grouper grouper) {
        this.exponentSeparatorString = symbols.getExponentSeparator();
        this.exponentMatcher = DecimalMatcher.getInstance(symbols, grouper, 48);
        this.ignorablesMatcher = IgnorablesMatcher.getInstance(32768);
        String minusSign = symbols.getMinusSignString();
        this.customMinusSign = ParsingUtils.safeContains(ScientificMatcher.minusSignSet(), minusSign) ? null : minusSign;
        String plusSign = symbols.getPlusSignString();
        this.customPlusSign = ParsingUtils.safeContains(ScientificMatcher.plusSignSet(), plusSign) ? null : plusSign;
    }

    private static UnicodeSet minusSignSet() {
        return StaticUnicodeSets.get(StaticUnicodeSets.Key.MINUS_SIGN);
    }

    private static UnicodeSet plusSignSet() {
        return StaticUnicodeSets.get(StaticUnicodeSets.Key.PLUS_SIGN);
    }

    @Override
    public boolean match(StringSegment segment, ParsedNumber result) {
        if (!result.seenNumber()) {
            return false;
        }
        if (0 != (result.flags & 8)) {
            return false;
        }
        int initialOffset = segment.getOffset();
        int overlap = segment.getCommonPrefixLength(this.exponentSeparatorString);
        if (overlap == this.exponentSeparatorString.length()) {
            boolean wasNull;
            if (segment.length() == overlap) {
                return true;
            }
            segment.adjustOffset(overlap);
            this.ignorablesMatcher.match(segment, null);
            if (segment.length() == 0) {
                segment.setOffset(initialOffset);
                return true;
            }
            int exponentSign = 1;
            if (segment.startsWith(ScientificMatcher.minusSignSet())) {
                exponentSign = -1;
                segment.adjustOffsetByCodePoint();
            } else if (segment.startsWith(ScientificMatcher.plusSignSet())) {
                segment.adjustOffsetByCodePoint();
            } else if (segment.startsWith(this.customMinusSign)) {
                overlap = segment.getCommonPrefixLength(this.customMinusSign);
                if (overlap != this.customMinusSign.length()) {
                    segment.setOffset(initialOffset);
                    return true;
                }
                exponentSign = -1;
                segment.adjustOffset(overlap);
            } else if (segment.startsWith(this.customPlusSign)) {
                overlap = segment.getCommonPrefixLength(this.customPlusSign);
                if (overlap != this.customPlusSign.length()) {
                    segment.setOffset(initialOffset);
                    return true;
                }
                segment.adjustOffset(overlap);
            }
            if (segment.length() == 0) {
                segment.setOffset(initialOffset);
                return true;
            }
            this.ignorablesMatcher.match(segment, null);
            if (segment.length() == 0) {
                segment.setOffset(initialOffset);
                return true;
            }
            boolean bl = wasNull = result.quantity == null;
            if (wasNull) {
                result.quantity = new DecimalQuantity_DualStorageBCD();
            }
            int digitsOffset = segment.getOffset();
            boolean digitsReturnValue = this.exponentMatcher.match(segment, result, exponentSign);
            if (wasNull) {
                result.quantity = null;
            }
            if (segment.getOffset() != digitsOffset) {
                result.flags |= 8;
            } else {
                segment.setOffset(initialOffset);
            }
            return digitsReturnValue;
        }
        return overlap == segment.length();
    }

    @Override
    public boolean smokeTest(StringSegment segment) {
        return segment.startsWith(this.exponentSeparatorString);
    }

    @Override
    public void postProcess(ParsedNumber result) {
    }

    public String toString() {
        return "<ScientificMatcher " + this.exponentSeparatorString + ">";
    }
}
