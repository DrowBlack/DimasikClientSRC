package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.RuleBasedTransliterator;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeMatcher;
import com.ibm.icu.text.UnicodeReplacer;
import com.ibm.icu.text.UnicodeSet;

class StringMatcher
implements UnicodeMatcher,
UnicodeReplacer {
    private String pattern;
    private int matchStart;
    private int matchLimit;
    private int segmentNumber;
    private final RuleBasedTransliterator.Data data;

    public StringMatcher(String theString, int segmentNum, RuleBasedTransliterator.Data theData) {
        this.data = theData;
        this.pattern = theString;
        this.matchLimit = -1;
        this.matchStart = -1;
        this.segmentNumber = segmentNum;
    }

    public StringMatcher(String theString, int start, int limit, int segmentNum, RuleBasedTransliterator.Data theData) {
        this(theString.substring(start, limit), segmentNum, theData);
    }

    @Override
    public int matches(Replaceable text, int[] offset, int limit, boolean incremental) {
        int[] cursor = new int[]{offset[0]};
        if (limit < cursor[0]) {
            for (int i = this.pattern.length() - 1; i >= 0; --i) {
                char keyChar = this.pattern.charAt(i);
                UnicodeMatcher subm = this.data.lookupMatcher(keyChar);
                if (subm == null) {
                    if (cursor[0] > limit && keyChar == text.charAt(cursor[0])) {
                        cursor[0] = cursor[0] - 1;
                        continue;
                    }
                    return 0;
                }
                int m = subm.matches(text, cursor, limit, incremental);
                if (m == 2) continue;
                return m;
            }
            if (this.matchStart < 0) {
                this.matchStart = cursor[0] + 1;
                this.matchLimit = offset[0] + 1;
            }
        } else {
            for (int i = 0; i < this.pattern.length(); ++i) {
                if (incremental && cursor[0] == limit) {
                    return 1;
                }
                char keyChar = this.pattern.charAt(i);
                UnicodeMatcher subm = this.data.lookupMatcher(keyChar);
                if (subm == null) {
                    if (cursor[0] < limit && keyChar == text.charAt(cursor[0])) {
                        cursor[0] = cursor[0] + 1;
                        continue;
                    }
                    return 0;
                }
                int m = subm.matches(text, cursor, limit, incremental);
                if (m == 2) continue;
                return m;
            }
            this.matchStart = offset[0];
            this.matchLimit = cursor[0];
        }
        offset[0] = cursor[0];
        return 2;
    }

    @Override
    public String toPattern(boolean escapeUnprintable) {
        StringBuffer result = new StringBuffer();
        StringBuffer quoteBuf = new StringBuffer();
        if (this.segmentNumber > 0) {
            result.append('(');
        }
        for (int i = 0; i < this.pattern.length(); ++i) {
            char keyChar = this.pattern.charAt(i);
            UnicodeMatcher m = this.data.lookupMatcher(keyChar);
            if (m == null) {
                Utility.appendToRule(result, keyChar, false, escapeUnprintable, quoteBuf);
                continue;
            }
            Utility.appendToRule(result, m.toPattern(escapeUnprintable), true, escapeUnprintable, quoteBuf);
        }
        if (this.segmentNumber > 0) {
            result.append(')');
        }
        Utility.appendToRule(result, -1, true, escapeUnprintable, quoteBuf);
        return result.toString();
    }

    @Override
    public boolean matchesIndexValue(int v) {
        if (this.pattern.length() == 0) {
            return true;
        }
        int c = UTF16.charAt(this.pattern, 0);
        UnicodeMatcher m = this.data.lookupMatcher(c);
        return m == null ? (c & 0xFF) == v : m.matchesIndexValue(v);
    }

    @Override
    public void addMatchSetTo(UnicodeSet toUnionTo) {
        int ch;
        for (int i = 0; i < this.pattern.length(); i += UTF16.getCharCount(ch)) {
            ch = UTF16.charAt(this.pattern, i);
            UnicodeMatcher matcher = this.data.lookupMatcher(ch);
            if (matcher == null) {
                toUnionTo.add(ch);
                continue;
            }
            matcher.addMatchSetTo(toUnionTo);
        }
    }

    @Override
    public int replace(Replaceable text, int start, int limit, int[] cursor) {
        int outLen = 0;
        int dest = limit;
        if (this.matchStart >= 0 && this.matchStart != this.matchLimit) {
            text.copy(this.matchStart, this.matchLimit, dest);
            outLen = this.matchLimit - this.matchStart;
        }
        text.replace(start, limit, "");
        return outLen;
    }

    @Override
    public String toReplacerPattern(boolean escapeUnprintable) {
        StringBuffer rule = new StringBuffer("$");
        Utility.appendNumber(rule, this.segmentNumber, 10, 1);
        return rule.toString();
    }

    public void resetMatch() {
        this.matchLimit = -1;
        this.matchStart = -1;
    }

    @Override
    public void addReplacementSetTo(UnicodeSet toUnionTo) {
    }
}
