package org.openjdk.nashorn.internal.objects.annotations;

public interface Attribute {
    public static final int NOT_WRITABLE = 1;
    public static final int NOT_ENUMERABLE = 2;
    public static final int NOT_CONFIGURABLE = 4;
    public static final int IS_ACCESSOR = 4096;
    public static final int CONSTANT = 5;
    public static final int NON_ENUMERABLE_CONSTANT = 7;
    public static final int DEFAULT_ATTRIBUTES = 0;
}
