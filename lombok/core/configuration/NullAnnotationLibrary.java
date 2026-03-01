package lombok.core.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.core.configuration.ConfigurationValueType;

public final class NullAnnotationLibrary
implements ConfigurationValueType {
    private final String key;
    private final String nonNullAnnotation;
    private final String nullableAnnotation;
    private final boolean typeUse;
    public static final NullAnnotationLibrary NONE = new NullAnnotationLibrary("none", null, null, false);
    public static final NullAnnotationLibrary JAVAX = new NullAnnotationLibrary("javax", "javax.annotation.Nonnull", "javax.annotation.Nullable", false);
    public static final NullAnnotationLibrary JAKARTA = new NullAnnotationLibrary("jakarta", "jakarta.annotation.Nonnull", "jakarta.annotation.Nullable", false);
    public static final NullAnnotationLibrary ECLIPSE = new NullAnnotationLibrary("eclipse", "org.eclipse.jdt.annotation.NonNull", "org.eclipse.jdt.annotation.Nullable", true);
    public static final NullAnnotationLibrary JETBRAINS = new NullAnnotationLibrary("jetbrains", "org.jetbrains.annotations.NotNull", "org.jetbrains.annotations.Nullable", false);
    public static final NullAnnotationLibrary NETBEANS = new NullAnnotationLibrary("netbeans", "org.netbeans.api.annotations.common.NonNull", "org.netbeans.api.annotations.common.NullAllowed", false);
    public static final NullAnnotationLibrary ANDROIDX = new NullAnnotationLibrary("androidx", "androidx.annotation.NonNull", "androidx.annotation.Nullable", false);
    public static final NullAnnotationLibrary ANDROID_SUPPORT = new NullAnnotationLibrary("android.support", "android.support.annotation.NonNull", "android.support.annotation.Nullable", false);
    public static final NullAnnotationLibrary CHECKERFRAMEWORK = new NullAnnotationLibrary("checkerframework", "org.checkerframework.checker.nullness.qual.NonNull", "org.checkerframework.checker.nullness.qual.Nullable", true);
    public static final NullAnnotationLibrary FINDBUGS = new NullAnnotationLibrary("findbugs", "edu.umd.cs.findbugs.annotations.NonNull", "edu.umd.cs.findbugs.annotations.Nullable", false);
    public static final NullAnnotationLibrary SPRING = new NullAnnotationLibrary("spring", "org.springframework.lang.NonNull", "org.springframework.lang.Nullable", false);
    public static final NullAnnotationLibrary JML = new NullAnnotationLibrary("jml", "org.jmlspecs.annotation.NonNull", "org.jmlspecs.annotation.Nullable", false);
    private static final List<NullAnnotationLibrary> ALL_AVAILABLE;
    private static final String EXAMPLE_VALUE;
    private static final String MSG = "Not an identifier (provide a fully qualified type for custom: nullity annotations): ";

    static {
        ArrayList<NullAnnotationLibrary> out = new ArrayList<NullAnnotationLibrary>();
        StringBuilder example = new StringBuilder();
        Field[] fieldArray = NullAnnotationLibrary.class.getDeclaredFields();
        int n = fieldArray.length;
        int n2 = 0;
        while (n2 < n) {
            Field f = fieldArray[n2];
            if (f.getType() == NullAnnotationLibrary.class && Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
                try {
                    NullAnnotationLibrary nal = (NullAnnotationLibrary)f.get(null);
                    out.add(nal);
                    example.append(nal.key).append(" | ");
                }
                catch (IllegalAccessException illegalAccessException) {}
            }
            ++n2;
        }
        out.trimToSize();
        example.append("CUSTOM:com.foo.my.nonnull.annotation:com.foo.my.nullable.annotation");
        ALL_AVAILABLE = Collections.unmodifiableList(out);
        EXAMPLE_VALUE = example.toString();
    }

    private NullAnnotationLibrary(String key, String nonNullAnnotation, String nullableAnnotation, boolean typeUse) {
        this.key = key;
        this.nonNullAnnotation = nonNullAnnotation;
        this.nullableAnnotation = nullableAnnotation;
        this.typeUse = typeUse;
    }

    public String getNonNullAnnotation() {
        return this.nonNullAnnotation;
    }

    public String getNullableAnnotation() {
        return this.nullableAnnotation;
    }

    public boolean isTypeUse() {
        return this.typeUse;
    }

    public static NullAnnotationLibrary custom(String nonNullAnnotation, String nullableAnnotation, boolean typeUse) {
        String typeUseStr;
        if (nonNullAnnotation == null && nullableAnnotation == null) {
            return NONE;
        }
        String string = typeUseStr = typeUse ? "TYPE_USE:" : "";
        if (nullableAnnotation == null) {
            return new NullAnnotationLibrary("custom:" + typeUseStr + nonNullAnnotation, nonNullAnnotation, null, typeUse);
        }
        if (nonNullAnnotation == null) {
            return new NullAnnotationLibrary("custom::" + typeUseStr + nullableAnnotation, null, nullableAnnotation, typeUse);
        }
        return new NullAnnotationLibrary("custom:" + typeUseStr + nonNullAnnotation + ":" + nullableAnnotation, nonNullAnnotation, nullableAnnotation, typeUse);
    }

    public static String description() {
        return "nullity-annotation-library";
    }

    public static String exampleValue() {
        return EXAMPLE_VALUE;
    }

    public static NullAnnotationLibrary valueOf(String in) {
        String ci;
        String string = ci = in == null ? "" : in.toLowerCase();
        if (ci.length() == 0) {
            return NONE;
        }
        for (NullAnnotationLibrary nal : ALL_AVAILABLE) {
            if (!nal.key.equals(ci)) continue;
            return nal;
        }
        if (!ci.startsWith("custom:")) {
            StringBuilder out = new StringBuilder("Invalid null annotation library. Valid options: ");
            for (NullAnnotationLibrary nal : ALL_AVAILABLE) {
                out.append(nal.key).append(", ");
            }
            out.setLength(out.length() - 2);
            out.append(" or CUSTOM:[TYPE_USE:]nonnull.annotation.type:nullable.annotation.type");
            throw new IllegalArgumentException(out.toString());
        }
        boolean typeUse = ci.startsWith("custom:type_use:");
        int start = typeUse ? 16 : 7;
        int split = ci.indexOf(58, start);
        if (split == -1) {
            String nonNullAnnotation = in.substring(start);
            return NullAnnotationLibrary.custom(NullAnnotationLibrary.verifyTypeName(nonNullAnnotation), null, typeUse);
        }
        String nonNullAnnotation = in.substring(start, split);
        String nullableAnnotation = in.substring(split + 1);
        return NullAnnotationLibrary.custom(NullAnnotationLibrary.verifyTypeName(nonNullAnnotation), NullAnnotationLibrary.verifyTypeName(nullableAnnotation), typeUse);
    }

    private static String verifyTypeName(String fqn) {
        boolean atStart = true;
        int i = 0;
        while (i < fqn.length()) {
            char c = fqn.charAt(i);
            if (Character.isJavaIdentifierStart(c)) {
                atStart = false;
            } else {
                if (atStart) {
                    throw new IllegalArgumentException(MSG + fqn);
                }
                if (c == '.') {
                    atStart = true;
                } else if (!Character.isJavaIdentifierPart(c)) {
                    throw new IllegalArgumentException(MSG + fqn);
                }
            }
            ++i;
        }
        if (atStart) {
            throw new IllegalArgumentException(MSG + fqn);
        }
        return fqn;
    }
}
