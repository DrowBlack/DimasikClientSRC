package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.RuleBasedTransliterator;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeReplacer;
import com.ibm.icu.text.UnicodeSet;

class StringReplacer
implements UnicodeReplacer {
    private String output;
    private int cursorPos;
    private boolean hasCursor;
    private boolean isComplex;
    private final RuleBasedTransliterator.Data data;

    public StringReplacer(String theOutput, int theCursorPos, RuleBasedTransliterator.Data theData) {
        this.output = theOutput;
        this.cursorPos = theCursorPos;
        this.hasCursor = true;
        this.data = theData;
        this.isComplex = true;
    }

    public StringReplacer(String theOutput, RuleBasedTransliterator.Data theData) {
        this.output = theOutput;
        this.cursorPos = 0;
        this.hasCursor = false;
        this.data = theData;
        this.isComplex = true;
    }

    @Override
    public int replace(Replaceable text, int start, int limit, int[] cursor) {
        int outLen;
        int newStart = 0;
        if (!this.isComplex) {
            text.replace(start, limit, this.output);
            outLen = this.output.length();
            newStart = this.cursorPos;
        } else {
            int tempStart;
            StringBuffer buf = new StringBuffer();
            this.isComplex = false;
            int destStart = tempStart = text.length();
            if (start > 0) {
                int len = UTF16.getCharCount(text.char32At(start - 1));
                text.copy(start - len, start, tempStart);
                destStart += len;
            } else {
                text.replace(tempStart, tempStart, "\uffff");
                ++destStart;
            }
            int destLimit = destStart;
            int tempExtra = 0;
            int oOutput = 0;
            while (oOutput < this.output.length()) {
                UnicodeReplacer r;
                int c;
                int nextIndex;
                if (oOutput == this.cursorPos) {
                    newStart = buf.length() + destLimit - destStart;
                }
                if ((nextIndex = oOutput + UTF16.getCharCount(c = UTF16.charAt(this.output, oOutput))) == this.output.length()) {
                    tempExtra = UTF16.getCharCount(text.char32At(limit));
                    text.copy(limit, limit + tempExtra, destLimit);
                }
                if ((r = this.data.lookupReplacer(c)) == null) {
                    UTF16.append(buf, c);
                } else {
                    this.isComplex = true;
                    if (buf.length() > 0) {
                        text.replace(destLimit, destLimit, buf.toString());
                        destLimit += buf.length();
                        buf.setLength(0);
                    }
                    int len = r.replace(text, destLimit, destLimit, cursor);
                    destLimit += len;
                }
                oOutput = nextIndex;
            }
            if (buf.length() > 0) {
                text.replace(destLimit, destLimit, buf.toString());
                destLimit += buf.length();
            }
            if (oOutput == this.cursorPos) {
                newStart = destLimit - destStart;
            }
            outLen = destLimit - destStart;
            text.copy(destStart, destLimit, start);
            text.replace(tempStart + outLen, destLimit + tempExtra + outLen, "");
            text.replace(start + outLen, limit + outLen, "");
        }
        if (this.hasCursor) {
            if (this.cursorPos < 0) {
                int n;
                newStart = start;
                for (n = this.cursorPos; n < 0 && newStart > 0; newStart -= UTF16.getCharCount(text.char32At(newStart - 1)), ++n) {
                }
                newStart += n;
            } else if (this.cursorPos > this.output.length()) {
                int n;
                newStart = start + outLen;
                for (n = this.cursorPos - this.output.length(); n > 0 && newStart < text.length(); newStart += UTF16.getCharCount(text.char32At(newStart)), --n) {
                }
                newStart += n;
            } else {
                newStart += start;
            }
            cursor[0] = newStart;
        }
        return outLen;
    }

    @Override
    public String toReplacerPattern(boolean escapeUnprintable) {
        StringBuffer rule = new StringBuffer();
        StringBuffer quoteBuf = new StringBuffer();
        int cursor = this.cursorPos;
        if (this.hasCursor && cursor < 0) {
            while (cursor++ < 0) {
                Utility.appendToRule(rule, 64, true, escapeUnprintable, quoteBuf);
            }
        }
        for (int i = 0; i < this.output.length(); ++i) {
            char c;
            UnicodeReplacer r;
            if (this.hasCursor && i == cursor) {
                Utility.appendToRule(rule, 124, true, escapeUnprintable, quoteBuf);
            }
            if ((r = this.data.lookupReplacer(c = this.output.charAt(i))) == null) {
                Utility.appendToRule(rule, c, false, escapeUnprintable, quoteBuf);
                continue;
            }
            StringBuffer buf = new StringBuffer(" ");
            buf.append(r.toReplacerPattern(escapeUnprintable));
            buf.append(' ');
            Utility.appendToRule(rule, buf.toString(), true, escapeUnprintable, quoteBuf);
        }
        if (this.hasCursor && cursor > this.output.length()) {
            cursor -= this.output.length();
            while (cursor-- > 0) {
                Utility.appendToRule(rule, 64, true, escapeUnprintable, quoteBuf);
            }
            Utility.appendToRule(rule, 124, true, escapeUnprintable, quoteBuf);
        }
        Utility.appendToRule(rule, -1, true, escapeUnprintable, quoteBuf);
        return rule.toString();
    }

    @Override
    public void addReplacementSetTo(UnicodeSet toUnionTo) {
        int ch;
        for (int i = 0; i < this.output.length(); i += UTF16.getCharCount(ch)) {
            ch = UTF16.charAt(this.output, i);
            UnicodeReplacer r = this.data.lookupReplacer(ch);
            if (r == null) {
                toUnionTo.add(ch);
                continue;
            }
            r.addReplacementSetTo(toUnionTo);
        }
    }
}
