package org.codehaus.plexus.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;

public class StringUtils {
    public static String clean(String str) {
        return str == null ? "" : str.trim();
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String deleteWhitespace(String str) {
        StringBuilder buffer = new StringBuilder();
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            buffer.append(str.charAt(i));
        }
        return buffer.toString();
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    @Deprecated
    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equalsIgnoreCase(str2);
    }

    public static int indexOfAny(String str, String[] searchStrs) {
        if (str == null || searchStrs == null) {
            return -1;
        }
        int sz = searchStrs.length;
        int ret = Integer.MAX_VALUE;
        for (String searchStr : searchStrs) {
            int tmp = str.indexOf(searchStr);
            if (tmp == -1 || tmp >= ret) continue;
            ret = tmp;
        }
        return ret == Integer.MAX_VALUE ? -1 : ret;
    }

    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if (str == null || searchStrs == null) {
            return -1;
        }
        int ret = -1;
        for (String searchStr : searchStrs) {
            int tmp = str.lastIndexOf(searchStr);
            if (tmp <= ret) continue;
            ret = tmp;
        }
        return ret;
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static String left(String str, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
        }
        if (str == null || str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    public static String right(String str, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
        }
        if (str == null || str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    public static String mid(String str, int pos, int len) {
        if (pos < 0 || str != null && pos > str.length()) {
            throw new StringIndexOutOfBoundsException("String index " + pos + " is out of bounds");
        }
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len + " is less than zero");
        }
        if (str == null) {
            return null;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    public static String[] split(String str) {
        return StringUtils.split(str, null, -1);
    }

    public static String[] split(String text, String separator) {
        return StringUtils.split(text, separator, -1);
    }

    public static String[] split(String str, String separator, int max) {
        StringTokenizer tok = separator == null ? new StringTokenizer(str) : new StringTokenizer(str, separator);
        int listSize = tok.countTokens();
        if (max > 0 && listSize > max) {
            listSize = max;
        }
        String[] list = new String[listSize];
        int i = 0;
        int lastTokenEnd = 0;
        while (tok.hasMoreTokens()) {
            int lastTokenBegin;
            if (max > 0 && i == listSize - 1) {
                String endToken = tok.nextToken();
                lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
                list[i] = str.substring(lastTokenBegin);
                break;
            }
            list[i] = tok.nextToken();
            lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
            lastTokenEnd = lastTokenBegin + list[i].length();
            ++i;
        }
        return list;
    }

    public static String concatenate(Object[] array) {
        return StringUtils.join(array, "");
    }

    public static String join(Object[] array, String separator) {
        int arraySize;
        if (separator == null) {
            separator = "";
        }
        int bufSize = (arraySize = array.length) == 0 ? 0 : (array[0].toString().length() + separator.length()) * arraySize;
        StringBuilder buf = new StringBuilder(bufSize);
        for (int i = 0; i < arraySize; ++i) {
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    public static String join(Iterator<?> iterator, String separator) {
        if (separator == null) {
            separator = "";
        }
        StringBuilder buf = new StringBuilder(256);
        while (iterator.hasNext()) {
            buf.append(iterator.next());
            if (!iterator.hasNext()) continue;
            buf.append(separator);
        }
        return buf.toString();
    }

    public static String replaceOnce(String text, char repl, char with) {
        return StringUtils.replace(text, repl, with, 1);
    }

    public static String replace(String text, char repl, char with) {
        return StringUtils.replace(text, repl, with, -1);
    }

    public static String replace(String text, char repl, char with, int max) {
        return StringUtils.replace(text, String.valueOf(repl), String.valueOf(with), max);
    }

    public static String replaceOnce(String text, String repl, String with) {
        return StringUtils.replace(text, repl, with, 1);
    }

    public static String replace(String text, String repl, String with) {
        return StringUtils.replace(text, repl, with, -1);
    }

    public static String replace(String text, String repl, String with, int max) {
        int end;
        if (text == null || repl == null || with == null || repl.length() == 0) {
            return text;
        }
        StringBuilder buf = new StringBuilder(text.length());
        int start = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text, start, end).append(with);
            start = end + repl.length();
            if (--max != 0) continue;
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    public static String overlayString(String text, String overlay, int start, int end) {
        return new StringBuilder(start + overlay.length() + text.length() - end + 1).append(text, 0, start).append(overlay).append(text, end, text.length()).toString();
    }

    public static String center(String str, int size) {
        return StringUtils.center(str, size, " ");
    }

    public static String center(String str, int size, String delim) {
        int sz = str.length();
        int p = size - sz;
        if (p < 1) {
            return str;
        }
        str = StringUtils.leftPad(str, sz + p / 2, delim);
        str = StringUtils.rightPad(str, size, delim);
        return str;
    }

    public static String chomp(String str) {
        return StringUtils.chomp(str, "\n");
    }

    public static String chomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx);
        }
        return str;
    }

    public static String chompLast(String str) {
        return StringUtils.chompLast(str, "\n");
    }

    public static String chompLast(String str, String sep) {
        if (str.length() == 0) {
            return str;
        }
        String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub)) {
            return str.substring(0, str.length() - sep.length());
        }
        return str;
    }

    public static String getChomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length()) {
            return sep;
        }
        if (idx != -1) {
            return str.substring(idx);
        }
        return "";
    }

    public static String prechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(idx + sep.length());
        }
        return str;
    }

    public static String getPrechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx + sep.length());
        }
        return "";
    }

    public static String chop(String str) {
        if ("".equals(str)) {
            return "";
        }
        if (str.length() == 1) {
            return "";
        }
        int lastIdx = str.length() - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);
        if (last == '\n' && ret.charAt(lastIdx - 1) == '\r') {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }

    public static String chopNewline(String str) {
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                --lastIdx;
            }
        } else {
            ++lastIdx;
        }
        return str.substring(0, lastIdx);
    }

    public static String escape(String str) {
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(2 * sz);
        block12: for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (ch > '\u0fff') {
                buffer.append("\\u" + Integer.toHexString(ch));
                continue;
            }
            if (ch > '\u00ff') {
                buffer.append("\\u0" + Integer.toHexString(ch));
                continue;
            }
            if (ch > '\u007f') {
                buffer.append("\\u00" + Integer.toHexString(ch));
                continue;
            }
            if (ch < ' ') {
                switch (ch) {
                    case '\b': {
                        buffer.append('\\');
                        buffer.append('b');
                        break;
                    }
                    case '\n': {
                        buffer.append('\\');
                        buffer.append('n');
                        break;
                    }
                    case '\t': {
                        buffer.append('\\');
                        buffer.append('t');
                        break;
                    }
                    case '\f': {
                        buffer.append('\\');
                        buffer.append('f');
                        break;
                    }
                    case '\r': {
                        buffer.append('\\');
                        buffer.append('r');
                        break;
                    }
                    default: {
                        if (ch > '\u000f') {
                            buffer.append("\\u00" + Integer.toHexString(ch));
                            break;
                        }
                        buffer.append("\\u000" + Integer.toHexString(ch));
                        break;
                    }
                }
                continue;
            }
            switch (ch) {
                case '\'': {
                    buffer.append('\\');
                    buffer.append('\'');
                    continue block12;
                }
                case '\"': {
                    buffer.append('\\');
                    buffer.append('\"');
                    continue block12;
                }
                case '\\': {
                    buffer.append('\\');
                    buffer.append('\\');
                    continue block12;
                }
                default: {
                    buffer.append(ch);
                }
            }
        }
        return buffer.toString();
    }

    public static String repeat(String str, int repeat) {
        StringBuilder buffer = new StringBuilder(repeat * str.length());
        for (int i = 0; i < repeat; ++i) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    public static String rightPad(String str, int size) {
        return StringUtils.rightPad(str, size, " ");
    }

    public static String rightPad(String str, int size, String delim) {
        if ((size = (size - str.length()) / delim.length()) > 0) {
            str = str + StringUtils.repeat(delim, size);
        }
        return str;
    }

    public static String leftPad(String str, int size) {
        return StringUtils.leftPad(str, size, " ");
    }

    public static String leftPad(String str, int size, String delim) {
        if ((size = (size - str.length()) / delim.length()) > 0) {
            str = StringUtils.repeat(delim, size) + str;
        }
        return str;
    }

    public static String strip(String str) {
        return StringUtils.strip(str, null);
    }

    public static String strip(String str, String delim) {
        str = StringUtils.stripStart(str, delim);
        return StringUtils.stripEnd(str, delim);
    }

    public static String[] stripAll(String[] strs) {
        return StringUtils.stripAll(strs, null);
    }

    public static String[] stripAll(String[] strs, String delimiter) {
        if (strs == null || strs.length == 0) {
            return strs;
        }
        int sz = strs.length;
        String[] newArr = new String[sz];
        for (int i = 0; i < sz; ++i) {
            newArr[i] = StringUtils.strip(strs[i], delimiter);
        }
        return newArr;
    }

    public static String stripEnd(String str, String strip) {
        int end;
        if (str == null) {
            return null;
        }
        if (strip == null) {
            for (end = str.length(); end != 0 && Character.isWhitespace(str.charAt(end - 1)); --end) {
            }
        } else {
            while (end != 0 && strip.indexOf(str.charAt(end - 1)) != -1) {
                --end;
            }
        }
        return str.substring(0, end);
    }

    public static String stripStart(String str, String strip) {
        int start;
        if (str == null) {
            return null;
        }
        int sz = str.length();
        if (strip == null) {
            for (start = 0; start != sz && Character.isWhitespace(str.charAt(start)); ++start) {
            }
        } else {
            while (start != sz && strip.indexOf(str.charAt(start)) != -1) {
                ++start;
            }
        }
        return str.substring(start);
    }

    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String uncapitalise(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return "";
        }
        return new StringBuilder(str.length()).append(Character.toLowerCase(str.charAt(0))).append(str, 1, str.length()).toString();
    }

    public static String capitalise(String str) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return "";
        }
        return new StringBuilder(str.length()).append(Character.toTitleCase(str.charAt(0))).append(str, 1, str.length()).toString();
    }

    public static String swapCase(String str) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(sz);
        boolean whitespace = false;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            char tmp = Character.isUpperCase(ch) ? Character.toLowerCase(ch) : (Character.isTitleCase(ch) ? Character.toLowerCase(ch) : (Character.isLowerCase(ch) ? (whitespace ? Character.toTitleCase(ch) : Character.toUpperCase(ch)) : ch));
            buffer.append(tmp);
            whitespace = Character.isWhitespace(ch);
        }
        return buffer.toString();
    }

    public static String capitaliseAllWords(String str) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(sz);
        boolean space = true;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                space = true;
                continue;
            }
            if (space) {
                buffer.append(Character.toTitleCase(ch));
                space = false;
                continue;
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    public static String uncapitaliseAllWords(String str) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(sz);
        boolean space = true;
        for (int i = 0; i < sz; ++i) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                space = true;
                continue;
            }
            if (space) {
                buffer.append(Character.toLowerCase(ch));
                space = false;
                continue;
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    public static String getNestedString(String str, String tag) {
        return StringUtils.getNestedString(str, tag, tag);
    }

    public static String getNestedString(String str, String open, String close) {
        int end;
        if (str == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1 && (end = str.indexOf(close, start + open.length())) != -1) {
            return str.substring(start + open.length(), end);
        }
        return null;
    }

    public static int countMatches(String str, String sub) {
        if (sub.equals("")) {
            return 0;
        }
        if (str == null) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            ++count;
            idx += sub.length();
        }
        return count;
    }

    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isLetter(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isWhitespace(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isLetter(str.charAt(i)) || str.charAt(i) == ' ') continue;
            return false;
        }
        return true;
    }

    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isLetterOrDigit(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isLetterOrDigit(str.charAt(i)) || str.charAt(i) == ' ') continue;
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isDigit(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; ++i) {
            if (Character.isDigit(str.charAt(i)) || str.charAt(i) == ' ') continue;
            return false;
        }
        return true;
    }

    @Deprecated
    public static String defaultString(Object obj) {
        return StringUtils.defaultString(obj, "");
    }

    @Deprecated
    public static String defaultString(Object obj, String defaultString) {
        return Objects.toString(obj, defaultString);
    }

    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    public static String reverseDelimitedString(String str, String delimiter) {
        Object[] strs = StringUtils.split(str, delimiter);
        StringUtils.reverseArray(strs);
        return StringUtils.join(strs, delimiter);
    }

    private static void reverseArray(Object[] array) {
        int i = 0;
        for (int j = array.length - 1; j > i; --j, ++i) {
            Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }

    public static String abbreviate(String s, int maxWidth) {
        return StringUtils.abbreviate(s, 0, maxWidth);
    }

    public static String abbreviate(String s, int offset, int maxWidth) {
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (s.length() <= maxWidth) {
            return s;
        }
        if (offset > s.length()) {
            offset = s.length();
        }
        if (s.length() - offset < maxWidth - 3) {
            offset = s.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return s.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + (maxWidth - 3) < s.length()) {
            return "..." + StringUtils.abbreviate(s.substring(offset), maxWidth - 3);
        }
        return "..." + s.substring(s.length() - (maxWidth - 3));
    }

    public static String difference(String s1, String s2) {
        int at = StringUtils.differenceAt(s1, s2);
        if (at == -1) {
            return "";
        }
        return s2.substring(at);
    }

    public static int differenceAt(String s1, String s2) {
        int i;
        for (i = 0; i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i); ++i) {
        }
        if (i < s2.length() || i < s1.length()) {
            return i;
        }
        return -1;
    }

    public static String interpolate(String text, Map<?, ?> namespace) {
        Iterator<?> keys = namespace.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next().toString();
            Object obj = namespace.get(key);
            if (obj == null) {
                throw new NullPointerException("The value of the key '" + key + "' is null.");
            }
            String value = obj.toString();
            text = StringUtils.replace(text, "${" + key + "}", value);
            if (key.contains(" ")) continue;
            text = StringUtils.replace(text, "$" + key, value);
        }
        return text;
    }

    public static String removeAndHump(String data, String replaceThis) {
        StringBuilder out = new StringBuilder();
        String temp = data;
        StringTokenizer st = new StringTokenizer(temp, replaceThis);
        while (st.hasMoreTokens()) {
            String element = (String)st.nextElement();
            out.append(StringUtils.capitalizeFirstLetter(element));
        }
        return out.toString();
    }

    public static String capitalizeFirstLetter(String data) {
        char firstLetter = Character.toTitleCase(data.substring(0, 1).charAt(0));
        String restLetters = data.substring(1);
        return firstLetter + restLetters;
    }

    public static String lowercaseFirstLetter(String data) {
        char firstLetter = Character.toLowerCase(data.substring(0, 1).charAt(0));
        String restLetters = data.substring(1);
        return firstLetter + restLetters;
    }

    public static String addAndDeHump(String view) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < view.length(); ++i) {
            if (i != 0 && Character.isUpperCase(view.charAt(i))) {
                sb.append('-');
            }
            sb.append(view.charAt(i));
        }
        return sb.toString().trim().toLowerCase(Locale.ENGLISH);
    }

    public static String quoteAndEscape(String source, char quoteChar) {
        return StringUtils.quoteAndEscape(source, quoteChar, new char[]{quoteChar}, new char[]{' '}, '\\', false);
    }

    public static String quoteAndEscape(String source, char quoteChar, char[] quotingTriggers) {
        return StringUtils.quoteAndEscape(source, quoteChar, new char[]{quoteChar}, quotingTriggers, '\\', false);
    }

    public static String quoteAndEscape(String source, char quoteChar, char[] escapedChars, char escapeChar, boolean force) {
        return StringUtils.quoteAndEscape(source, quoteChar, escapedChars, new char[]{' '}, escapeChar, force);
    }

    public static String quoteAndEscape(String source, char quoteChar, char[] escapedChars, char[] quotingTriggers, char escapeChar, boolean force) {
        return StringUtils.quoteAndEscape(source, quoteChar, escapedChars, quotingTriggers, escapeChar + "%s", force);
    }

    public static String quoteAndEscape(String source, char quoteChar, char[] escapedChars, char[] quotingTriggers, String escapePattern, boolean force) {
        if (source == null) {
            return null;
        }
        if (!force && source.startsWith(Character.toString(quoteChar)) && source.endsWith(Character.toString(quoteChar))) {
            return source;
        }
        String escaped = StringUtils.escape(source, escapedChars, escapePattern);
        boolean quote = false;
        if (force) {
            quote = true;
        } else if (!escaped.equals(source)) {
            quote = true;
        } else {
            for (char quotingTrigger : quotingTriggers) {
                if (escaped.indexOf(quotingTrigger) <= -1) continue;
                quote = true;
                break;
            }
        }
        if (quote) {
            return quoteChar + escaped + quoteChar;
        }
        return escaped;
    }

    public static String escape(String source, char[] escapedChars, char escapeChar) {
        return StringUtils.escape(source, escapedChars, escapeChar + "%s");
    }

    public static String escape(String source, char[] escapedChars, String escapePattern) {
        if (source == null) {
            return null;
        }
        char[] eqc = new char[escapedChars.length];
        System.arraycopy(escapedChars, 0, eqc, 0, escapedChars.length);
        Arrays.sort(eqc);
        StringBuilder buffer = new StringBuilder(source.length());
        for (int i = 0; i < source.length(); ++i) {
            char c = source.charAt(i);
            int result = Arrays.binarySearch(eqc, c);
            if (result > -1) {
                buffer.append(String.format(escapePattern, Character.valueOf(c)));
                continue;
            }
            buffer.append(c);
        }
        return buffer.toString();
    }

    public static String removeDuplicateWhitespace(String s) {
        StringBuilder result = new StringBuilder();
        int length = s.length();
        boolean isPreviousWhiteSpace = false;
        for (int i = 0; i < length; ++i) {
            char c = s.charAt(i);
            boolean thisCharWhiteSpace = Character.isWhitespace(c);
            if (!isPreviousWhiteSpace || !thisCharWhiteSpace) {
                result.append(c);
            }
            isPreviousWhiteSpace = thisCharWhiteSpace;
        }
        return result.toString();
    }

    public static String unifyLineSeparators(String s) {
        return StringUtils.unifyLineSeparators(s, System.getProperty("line.separator"));
    }

    public static String unifyLineSeparators(String s, String ls) {
        if (s == null) {
            return null;
        }
        if (ls == null) {
            ls = System.getProperty("line.separator");
        }
        if (!(ls.equals("\n") || ls.equals("\r") || ls.equals("\r\n"))) {
            throw new IllegalArgumentException("Requested line separator is invalid.");
        }
        int length = s.length();
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            if (s.charAt(i) == '\r') {
                if (i + 1 < length && s.charAt(i + 1) == '\n') {
                    ++i;
                }
                buffer.append(ls);
                continue;
            }
            if (s.charAt(i) == '\n') {
                buffer.append(ls);
                continue;
            }
            buffer.append(s.charAt(i));
        }
        return buffer.toString();
    }

    public static boolean contains(String str, char searchChar) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) >= 0;
    }

    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.contains(searchStr);
    }
}
