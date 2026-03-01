package lombok.core.configuration;

import java.util.List;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationKey;

public interface ConfigurationSource {
    public Result resolve(ConfigurationKey<?> var1);

    public List<ConfigurationFile> imports();

    public static final class ListModification {
        private final Object value;
        private final boolean added;

        public ListModification(Object value, boolean added) {
            this.value = value;
            this.added = added;
        }

        public Object getValue() {
            return this.value;
        }

        public boolean isAdded() {
            return this.added;
        }
    }

    public static final class Result {
        private final Object value;
        private final boolean authoritative;

        public Result(Object value, boolean authoritative) {
            this.value = value;
            this.authoritative = authoritative;
        }

        public Object getValue() {
            return this.value;
        }

        public boolean isAuthoritative() {
            return this.authoritative;
        }

        public String toString() {
            return String.valueOf(String.valueOf(this.value)) + (this.authoritative ? " (set)" : " (delta)");
        }
    }
}
