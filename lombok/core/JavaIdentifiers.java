package lombok.core;

import java.util.regex.Pattern;
import lombok.core.LombokImmutableList;

public class JavaIdentifiers {
    private static final LombokImmutableList<String> KEYWORDS = LombokImmutableList.of("public", "private", "protected", "default", "switch", "case", "for", "do", "goto", "const", "strictfp", "while", "if", "else", "byte", "short", "int", "long", "float", "double", "void", "boolean", "char", "null", "false", "true", "continue", "break", "return", "instanceof", "synchronized", "volatile", "transient", "final", "static", "interface", "class", "extends", "implements", "throws", "throw", "catch", "try", "finally", "abstract", "assert", "enum", "import", "package", "native", "new", "super", "this");
    private static final Pattern PRIMITIVE_TYPE_NAME_PATTERN = Pattern.compile("^(?:boolean|byte|short|int|long|float|double|char)$");

    private JavaIdentifiers() {
    }

    public static boolean isValidJavaIdentifier(String identifier) {
        if (identifier == null) {
            return false;
        }
        if (identifier.isEmpty()) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
            return false;
        }
        int i = 1;
        while (i < identifier.length()) {
            if (!Character.isJavaIdentifierPart(identifier.charAt(i))) {
                return false;
            }
            ++i;
        }
        return !JavaIdentifiers.isKeyword(identifier);
    }

    public static boolean isKeyword(String keyword) {
        return KEYWORDS.contains(keyword);
    }

    public static boolean isPrimitive(String typeName) {
        return PRIMITIVE_TYPE_NAME_PATTERN.matcher(typeName).matches();
    }
}
