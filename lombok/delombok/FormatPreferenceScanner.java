package lombok.delombok;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import lombok.delombok.FormatPreferences;

public class FormatPreferenceScanner {
    private FormatPreferences tryEasy(FormatPreferences preferences, boolean force) {
        int count = 0;
        for (Map.Entry<String, String> e : preferences.rawMap.entrySet()) {
            if ("scan".equalsIgnoreCase(e.getValue())) continue;
            ++count;
        }
        if (force || count >= FormatPreferences.KEYS.size()) {
            return preferences;
        }
        return null;
    }

    public FormatPreferences scan(FormatPreferences preferences, final CharSequence source) {
        FormatPreferences fps = this.tryEasy(preferences, source == null);
        if (fps != null) {
            return fps;
        }
        try {
            return FormatPreferenceScanner.scan_(preferences, new Reader(){
                int pos = 0;
                int max;
                {
                    this.max = charSequence.length();
                }

                @Override
                public void close() throws IOException {
                }

                @Override
                public int read(char[] b, int p, int len) throws IOException {
                    int read = 0;
                    if (this.pos >= this.max) {
                        return -1;
                    }
                    int i = p;
                    while (i < p + len) {
                        b[i] = source.charAt(this.pos++);
                        ++read;
                        if (this.pos == this.max) {
                            return read;
                        }
                        ++i;
                    }
                    return len;
                }
            });
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FormatPreferences scan(FormatPreferences preferences, char[] source) {
        FormatPreferences fps = this.tryEasy(preferences, source == null);
        if (fps != null) {
            return fps;
        }
        try {
            return FormatPreferenceScanner.scan_(preferences, new CharArrayReader(source));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FormatPreferences scan(FormatPreferences preferences, Reader in) throws IOException {
        FormatPreferences fps = this.tryEasy(preferences, in == null);
        if (fps != null) {
            return fps;
        }
        return FormatPreferenceScanner.scan_(preferences, in);
    }

    private static FormatPreferences scan_(FormatPreferences preferences, Reader in) throws IOException {
        int filledEmpties = 0;
        ArrayList<String> indents = new ArrayList<String>();
        char[] buffer = new char[32700];
        int pos = 1;
        int end = 0;
        StringBuilder indentSoFar = new StringBuilder();
        boolean inIndent = true;
        boolean inComment = false;
        char lastChar = ' ';
        while (true) {
            if (pos >= end) {
                int r = in.read(buffer);
                if (r == -1) break;
                pos = 0;
                end = r;
                continue;
            }
            char c = buffer[pos++];
            if (inComment) {
                if (lastChar == '*' && c == '/') {
                    inComment = false;
                }
                lastChar = c;
                continue;
            }
            if (lastChar == '/' && c == '*') {
                inComment = true;
                lastChar = ' ';
                indentSoFar.setLength(0);
                inIndent = false;
                continue;
            }
            if (inIndent) {
                boolean w = Character.isWhitespace(c);
                if (c == '\n') {
                    if (indentSoFar.length() > 0 && indentSoFar.charAt(indentSoFar.length() - 1) == '\r') {
                        indentSoFar.setLength(indentSoFar.length() - 1);
                    }
                    if (indentSoFar.length() > 0) {
                        ++filledEmpties;
                    }
                    indents.add(indentSoFar.toString());
                    indentSoFar.setLength(0);
                    lastChar = c;
                    continue;
                }
                if (w) {
                    indentSoFar.append(c);
                    lastChar = c;
                    continue;
                }
                if (indentSoFar.length() > 0) {
                    indents.add(indentSoFar.toString());
                    indentSoFar.setLength(0);
                }
                lastChar = c;
                inIndent = false;
                continue;
            }
            lastChar = c;
            if (c != 10) continue;
            inIndent = true;
            indentSoFar.setLength(0);
        }
        String indent = null;
        int lowestSpaceCount = Integer.MAX_VALUE;
        for (String ind : indents) {
            if (ind.indexOf(9) > -1) {
                indent = "\t";
                break;
            }
            if (ind.length() < 2 || ind.length() > 8 || ind.length() >= lowestSpaceCount) continue;
            lowestSpaceCount = ind.length();
        }
        if (lowestSpaceCount == Integer.MAX_VALUE) {
            indent = "\t";
        }
        if (indent == null) {
            char[] id = new char[lowestSpaceCount];
            Arrays.fill(id, ' ');
            indent = new String(id);
        }
        return new FormatPreferences(preferences.rawMap, indent, filledEmpties > 0);
    }
}
