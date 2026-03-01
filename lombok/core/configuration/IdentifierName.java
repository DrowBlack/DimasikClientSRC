package lombok.core.configuration;

import lombok.core.JavaIdentifiers;
import lombok.core.configuration.ConfigurationValueType;

public final class IdentifierName
implements ConfigurationValueType {
    private final String name;

    private IdentifierName(String name) {
        this.name = name;
    }

    public static IdentifierName valueOf(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String trimmedName = name.trim();
        if (!JavaIdentifiers.isValidJavaIdentifier(trimmedName)) {
            throw new IllegalArgumentException("Invalid identifier " + trimmedName);
        }
        return new IdentifierName(trimmedName);
    }

    public static String description() {
        return "identifier-name";
    }

    public static String exampleValue() {
        return "<javaIdentifier>";
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof IdentifierName)) {
            return false;
        }
        return this.name.equals(((IdentifierName)obj).name);
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
