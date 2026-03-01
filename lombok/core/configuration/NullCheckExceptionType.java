package lombok.core.configuration;

import lombok.core.LombokImmutableList;
import lombok.core.configuration.ExampleValueString;

@ExampleValueString(value="[NullPointerException | IllegalArgumentException | Assertion | JDK | Guava]")
public enum NullCheckExceptionType {
    ILLEGAL_ARGUMENT_EXCEPTION{

        @Override
        public String getExceptionType() {
            return "java.lang.IllegalArgumentException";
        }

        @Override
        public LombokImmutableList<String> getMethod() {
            return null;
        }
    }
    ,
    NULL_POINTER_EXCEPTION{

        @Override
        public String getExceptionType() {
            return "java.lang.NullPointerException";
        }

        @Override
        public LombokImmutableList<String> getMethod() {
            return null;
        }
    }
    ,
    ASSERTION{

        @Override
        public String getExceptionType() {
            return null;
        }

        @Override
        public LombokImmutableList<String> getMethod() {
            return null;
        }
    }
    ,
    JDK{

        @Override
        public String getExceptionType() {
            return null;
        }

        @Override
        public LombokImmutableList<String> getMethod() {
            return METHOD_JDK;
        }
    }
    ,
    GUAVA{

        @Override
        public String getExceptionType() {
            return null;
        }

        @Override
        public LombokImmutableList<String> getMethod() {
            return METHOD_GUAVA;
        }
    };

    private static final LombokImmutableList<String> METHOD_JDK;
    private static final LombokImmutableList<String> METHOD_GUAVA;

    static {
        METHOD_JDK = LombokImmutableList.of("java", "util", "Objects", "requireNonNull");
        METHOD_GUAVA = LombokImmutableList.of("com", "google", "common", "base", "Preconditions", "checkNotNull", new String[0]);
    }

    private NullCheckExceptionType() {
    }

    public String toExceptionMessage(String fieldName, String customMessage) {
        if (customMessage == null) {
            return String.valueOf(fieldName) + " is marked non-null but is null";
        }
        return customMessage.replace("%s", fieldName);
    }

    public abstract String getExceptionType();

    public abstract LombokImmutableList<String> getMethod();

    /* synthetic */ NullCheckExceptionType(String string, int n, NullCheckExceptionType nullCheckExceptionType) {
        this();
    }
}
