package org.openjdk.nashorn.internal.runtime.regexp.joni.constants;

public interface RegexState {
    public static final int NORMAL = 0;
    public static final int SEARCHING = 1;
    public static final int COMPILING = -1;
    public static final int MODIFY = -2;
}
