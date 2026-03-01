package com.ibm.icu.impl;

public final class SimpleFormatterImpl {
    private static final int ARG_NUM_LIMIT = 256;
    private static final char LEN1_CHAR = '\u0101';
    private static final char LEN2_CHAR = '\u0102';
    private static final char LEN3_CHAR = '\u0103';
    private static final char SEGMENT_LENGTH_ARGUMENT_CHAR = '\uffff';
    private static final int MAX_SEGMENT_LENGTH = 65279;
    private static final String[][] COMMON_PATTERNS = new String[][]{{"{0} {1}", "\u0002\u0000\u0101 \u0001"}, {"{0} ({1})", "\u0002\u0000\u0102 (\u0001\u0101)"}, {"{0}, {1}", "\u0002\u0000\u0102, \u0001"}, {"{0} \u2013 {1}", "\u0002\u0000\u0103 \u2013 \u0001"}};

    private SimpleFormatterImpl() {
    }

    public static String compileToStringMinMaxArguments(CharSequence pattern, StringBuilder sb, int min, int max) {
        int argCount;
        if (min <= 2 && 2 <= max) {
            for (String[] pair : COMMON_PATTERNS) {
                if (!pair[0].contentEquals(pattern)) continue;
                assert (pair[1].charAt(0) == '\u0002');
                return pair[1];
            }
        }
        int patternLength = pattern.length();
        sb.ensureCapacity(patternLength);
        sb.setLength(1);
        int textLength = 0;
        int maxArg = -1;
        boolean inQuote = false;
        int i = 0;
        while (i < patternLength) {
            int c;
            if ((c = pattern.charAt(i++)) == 39) {
                if (i < patternLength && (c = pattern.charAt(i)) == 39) {
                    ++i;
                } else {
                    if (inQuote) {
                        inQuote = false;
                        continue;
                    }
                    if (c == 123 || c == 125) {
                        ++i;
                        inQuote = true;
                    } else {
                        c = 39;
                    }
                }
            } else if (!inQuote && c == 123) {
                int argNumber;
                if (textLength > 0) {
                    sb.setCharAt(sb.length() - textLength - 1, (char)(256 + textLength));
                    textLength = 0;
                }
                if (i + 1 < patternLength && 0 <= (argNumber = pattern.charAt(i) - 48) && argNumber <= 9 && pattern.charAt(i + 1) == '}') {
                    i += 2;
                } else {
                    int argStart = i - 1;
                    argNumber = -1;
                    if (i < patternLength) {
                        char c2 = pattern.charAt(i++);
                        c = c2;
                        if ('1' <= c2 && c <= 57) {
                            argNumber = c - 48;
                            while (i < patternLength) {
                                char c3 = pattern.charAt(i++);
                                c = c3;
                                if ('0' <= c3 && c <= 57 && (argNumber = argNumber * 10 + (c - 48)) < 256) continue;
                            }
                        }
                    }
                    if (argNumber < 0 || c != 125) {
                        throw new IllegalArgumentException("Argument syntax error in pattern \"" + pattern + "\" at index " + argStart + ": " + pattern.subSequence(argStart, i));
                    }
                }
                if (argNumber > maxArg) {
                    maxArg = argNumber;
                }
                sb.append((char)argNumber);
                continue;
            }
            if (textLength == 0) {
                sb.append('\uffff');
            }
            sb.append((char)c);
            if (++textLength != 65279) continue;
            textLength = 0;
        }
        if (textLength > 0) {
            sb.setCharAt(sb.length() - textLength - 1, (char)(256 + textLength));
        }
        if ((argCount = maxArg + 1) < min) {
            throw new IllegalArgumentException("Fewer than minimum " + min + " arguments in pattern \"" + pattern + "\"");
        }
        if (argCount > max) {
            throw new IllegalArgumentException("More than maximum " + max + " arguments in pattern \"" + pattern + "\"");
        }
        sb.setCharAt(0, (char)argCount);
        return sb.toString();
    }

    public static int getArgumentLimit(String compiledPattern) {
        return compiledPattern.charAt(0);
    }

    public static String formatCompiledPattern(String compiledPattern, CharSequence ... values) {
        return SimpleFormatterImpl.formatAndAppend(compiledPattern, new StringBuilder(), null, values).toString();
    }

    public static String formatRawPattern(String pattern, int min, int max, CharSequence ... values) {
        StringBuilder sb = new StringBuilder();
        String compiledPattern = SimpleFormatterImpl.compileToStringMinMaxArguments(pattern, sb, min, max);
        sb.setLength(0);
        return SimpleFormatterImpl.formatAndAppend(compiledPattern, sb, null, values).toString();
    }

    public static StringBuilder formatAndAppend(String compiledPattern, StringBuilder appendTo, int[] offsets, CharSequence ... values) {
        int valuesLength;
        int n = valuesLength = values != null ? values.length : 0;
        if (valuesLength < SimpleFormatterImpl.getArgumentLimit(compiledPattern)) {
            throw new IllegalArgumentException("Too few values.");
        }
        return SimpleFormatterImpl.format(compiledPattern, values, appendTo, null, true, offsets);
    }

    public static StringBuilder formatAndReplace(String compiledPattern, StringBuilder result, int[] offsets, CharSequence ... values) {
        int valuesLength;
        int n = valuesLength = values != null ? values.length : 0;
        if (valuesLength < SimpleFormatterImpl.getArgumentLimit(compiledPattern)) {
            throw new IllegalArgumentException("Too few values.");
        }
        int firstArg = -1;
        String resultCopy = null;
        if (SimpleFormatterImpl.getArgumentLimit(compiledPattern) > 0) {
            int i = 1;
            while (i < compiledPattern.length()) {
                char n2;
                if ((n2 = compiledPattern.charAt(i++)) < '\u0100') {
                    if (values[n2] != result) continue;
                    if (i == 2) {
                        firstArg = n2;
                        continue;
                    }
                    if (resultCopy != null) continue;
                    resultCopy = result.toString();
                    continue;
                }
                i += n2 - 256;
            }
        }
        if (firstArg < 0) {
            result.setLength(0);
        }
        return SimpleFormatterImpl.format(compiledPattern, values, result, resultCopy, false, offsets);
    }

    public static String getTextWithNoArguments(String compiledPattern) {
        int capacity = compiledPattern.length() - 1 - SimpleFormatterImpl.getArgumentLimit(compiledPattern);
        StringBuilder sb = new StringBuilder(capacity);
        int i = 1;
        while (i < compiledPattern.length()) {
            int segmentLength;
            if ((segmentLength = compiledPattern.charAt(i++) - 256) <= 0) continue;
            int limit = i + segmentLength;
            sb.append(compiledPattern, i, limit);
            i = limit;
        }
        return sb.toString();
    }

    private static StringBuilder format(String compiledPattern, CharSequence[] values, StringBuilder result, String resultCopy, boolean forbidResultAsValue, int[] offsets) {
        int i;
        int offsetsLength;
        if (offsets == null) {
            offsetsLength = 0;
        } else {
            offsetsLength = offsets.length;
            for (i = 0; i < offsetsLength; ++i) {
                offsets[i] = -1;
            }
        }
        i = 1;
        while (i < compiledPattern.length()) {
            char n;
            if ((n = compiledPattern.charAt(i++)) < '\u0100') {
                CharSequence value = values[n];
                if (value == result) {
                    if (forbidResultAsValue) {
                        throw new IllegalArgumentException("Value must not be same object as result");
                    }
                    if (i == 2) {
                        if (n >= offsetsLength) continue;
                        offsets[n] = 0;
                        continue;
                    }
                    if (n < offsetsLength) {
                        offsets[n] = result.length();
                    }
                    result.append(resultCopy);
                    continue;
                }
                if (n < offsetsLength) {
                    offsets[n] = result.length();
                }
                result.append(value);
                continue;
            }
            int limit = i + (n - 256);
            result.append(compiledPattern, i, limit);
            i = limit;
        }
        return result;
    }

    public static class Int64Iterator {
        public static final long DONE = -1L;

        public static long step(CharSequence compiledPattern, long state, StringBuffer output) {
            int i = (int)(state >>> 32);
            assert (i < compiledPattern.length());
            ++i;
            while (i < compiledPattern.length() && compiledPattern.charAt(i) > '\u0100') {
                int limit = i + compiledPattern.charAt(i) + 1 - 256;
                output.append(compiledPattern, i + 1, limit);
                i = limit;
            }
            if (i == compiledPattern.length()) {
                return -1L;
            }
            return (long)i << 32 | (long)compiledPattern.charAt(i);
        }

        public static int getArgIndex(long state) {
            return (int)state;
        }
    }
}
