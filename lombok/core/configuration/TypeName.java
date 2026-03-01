package lombok.core.configuration;

import lombok.core.JavaIdentifiers;
import lombok.core.configuration.ConfigurationValueType;

public final class TypeName
implements ConfigurationValueType {
    private final String name;

    private TypeName(String name) {
        this.name = name;
    }

    public static TypeName valueOf(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String trimmedName = name.trim();
        String[] stringArray = trimmedName.split("\\.");
        int n = stringArray.length;
        int n2 = 0;
        while (n2 < n) {
            String identifier = stringArray[n2];
            if (!JavaIdentifiers.isValidJavaIdentifier(identifier)) {
                throw new IllegalArgumentException("Invalid type name " + trimmedName + " (part " + identifier + ")");
            }
            ++n2;
        }
        return new TypeName(trimmedName);
    }

    public static String description() {
        return "type-name";
    }

    public static String exampleValue() {
        return "<fully.qualified.Type>";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof TypeName)) {
            return false;
        }
        return this.name.equals(((TypeName)obj).name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public char[] getCharArray() {
        return this.name.toCharArray();
    }
}
