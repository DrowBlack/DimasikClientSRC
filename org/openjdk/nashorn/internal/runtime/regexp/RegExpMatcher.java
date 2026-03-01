package org.openjdk.nashorn.internal.runtime.regexp;

import java.util.regex.MatchResult;

public interface RegExpMatcher
extends MatchResult {
    public boolean search(int var1);

    public String getInput();
}
