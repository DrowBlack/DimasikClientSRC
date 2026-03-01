package org.codehaus.plexus.util;

import java.io.File;
import java.util.ArrayList;
import org.codehaus.plexus.util.MatchPattern;

public class MatchPatterns {
    private final MatchPattern[] patterns;

    private MatchPatterns(MatchPattern[] patterns) {
        this.patterns = patterns;
    }

    public boolean matches(String name, boolean isCaseSensitive) {
        String[] tokenized = MatchPattern.tokenizePathToString(name, File.separator);
        return this.matches(name, tokenized, isCaseSensitive);
    }

    public boolean matches(String name, String[] tokenizedName, boolean isCaseSensitive) {
        char[][] tokenizedNameChar = new char[tokenizedName.length][];
        for (int i = 0; i < tokenizedName.length; ++i) {
            tokenizedNameChar[i] = tokenizedName[i].toCharArray();
        }
        for (MatchPattern pattern : this.patterns) {
            if (!pattern.matchPath(name, tokenizedNameChar, isCaseSensitive)) continue;
            return true;
        }
        return false;
    }

    public boolean matchesPatternStart(String name, boolean isCaseSensitive) {
        for (MatchPattern includesPattern : this.patterns) {
            if (!includesPattern.matchPatternStart(name, isCaseSensitive)) continue;
            return true;
        }
        return false;
    }

    public static MatchPatterns from(String ... sources) {
        int length = sources.length;
        MatchPattern[] result = new MatchPattern[length];
        for (int i = 0; i < length; ++i) {
            result[i] = MatchPattern.fromString(sources[i]);
        }
        return new MatchPatterns(result);
    }

    public static MatchPatterns from(Iterable<String> strings) {
        return new MatchPatterns(MatchPatterns.getMatchPatterns(strings));
    }

    private static MatchPattern[] getMatchPatterns(Iterable<String> items) {
        ArrayList<MatchPattern> result = new ArrayList<MatchPattern>();
        for (String string : items) {
            result.add(MatchPattern.fromString(string));
        }
        return result.toArray(new MatchPattern[result.size()]);
    }
}
