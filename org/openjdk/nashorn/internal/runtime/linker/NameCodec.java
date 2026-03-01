package org.openjdk.nashorn.internal.runtime.linker;

public final class NameCodec {
    private static final char ESCAPE_C = '\\';
    private static final char NULL_ESCAPE_C = '=';
    private static final String NULL_ESCAPE = "\\=";
    public static final String EMPTY_NAME = new String(new char[]{'\\', '='});
    private static final String DANGEROUS_CHARS = "\\/.;:$[]<>";
    private static final String REPLACEMENT_CHARS = "-|,?!%{}^_";
    private static final int DANGEROUS_CHAR_FIRST_INDEX = 1;
    private static final long[] SPECIAL_BITMAP = new long[2];

    private NameCodec() {
    }

    public static String encode(String name) {
        String bn = NameCodec.mangle(name);
        assert (bn == name || NameCodec.looksMangled(bn)) : bn;
        assert (name.equals(NameCodec.decode(bn))) : name;
        return bn;
    }

    public static String decode(String name) {
        String sn = name;
        if (!sn.isEmpty() && NameCodec.looksMangled(name)) {
            sn = NameCodec.demangle(name);
            assert (name.equals(NameCodec.mangle(sn))) : name + " => " + sn + " => " + NameCodec.mangle(sn);
        }
        return sn;
    }

    private static boolean looksMangled(String s) {
        return s.charAt(0) == '\\';
    }

    private static String mangle(String s) {
        if (s.length() == 0) {
            return NULL_ESCAPE;
        }
        StringBuilder sb = null;
        int slen = s.length();
        for (int i = 0; i < slen; ++i) {
            char c = s.charAt(i);
            boolean needEscape = false;
            if (c == '\\') {
                if (i + 1 < slen) {
                    char c1 = s.charAt(i + 1);
                    if (i == 0 && c1 == '=' || c1 != NameCodec.originalOfReplacement(c1)) {
                        needEscape = true;
                    }
                }
            } else {
                needEscape = NameCodec.isDangerous(c);
            }
            if (!needEscape) {
                if (sb == null) continue;
                sb.append(c);
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder(s.length() + 10);
                if (s.charAt(0) != '\\' && i > 0) {
                    sb.append(NULL_ESCAPE);
                }
                sb.append(s, 0, i);
            }
            sb.append('\\');
            sb.append(NameCodec.replacementOf(c));
        }
        if (sb != null) {
            return sb.toString();
        }
        return s;
    }

    private static String demangle(String s) {
        StringBuilder sb = null;
        int stringStart = 0;
        if (s.startsWith(NULL_ESCAPE)) {
            stringStart = 2;
        }
        int slen = s.length();
        for (int i = stringStart; i < slen; ++i) {
            char rc;
            char oc;
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < slen && (oc = NameCodec.originalOfReplacement(rc = s.charAt(i + 1))) != rc) {
                if (sb == null) {
                    sb = new StringBuilder(s.length());
                    sb.append(s, stringStart, i);
                }
                ++i;
                c = oc;
            }
            if (sb == null) continue;
            sb.append(c);
        }
        if (sb != null) {
            return sb.toString();
        }
        return s.substring(stringStart);
    }

    private static boolean isSpecial(char c) {
        if (c >>> 6 < SPECIAL_BITMAP.length) {
            return (SPECIAL_BITMAP[c >>> 6] >> c & 1L) != 0L;
        }
        return false;
    }

    private static char replacementOf(char c) {
        if (!NameCodec.isSpecial(c)) {
            return c;
        }
        int i = DANGEROUS_CHARS.indexOf(c);
        if (i < 0) {
            return c;
        }
        return REPLACEMENT_CHARS.charAt(i);
    }

    private static char originalOfReplacement(char c) {
        if (!NameCodec.isSpecial(c)) {
            return c;
        }
        int i = REPLACEMENT_CHARS.indexOf(c);
        if (i < 0) {
            return c;
        }
        return DANGEROUS_CHARS.charAt(i);
    }

    private static boolean isDangerous(char c) {
        if (!NameCodec.isSpecial(c)) {
            return false;
        }
        return DANGEROUS_CHARS.indexOf(c) >= 1;
    }

    static {
        String SPECIAL = "\\/.;:$[]<>-|,?!%{}^_";
        for (char c : "\\/.;:$[]<>-|,?!%{}^_".toCharArray()) {
            int n = c >>> 6;
            SPECIAL_BITMAP[n] = SPECIAL_BITMAP[n] | 1L << c;
        }
    }
}
