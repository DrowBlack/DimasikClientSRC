package lombok.core.configuration;

import java.util.Collection;
import java.util.Collections;
import lombok.core.configuration.ConfigurationKey;

public final class AllowHelper {
    private static final Collection<? extends ConfigurationKey<?>> ALLOWABLE = Collections.emptySet();

    private AllowHelper() {
    }

    public static boolean isAllowable(ConfigurationKey<?> key) {
        return ALLOWABLE.contains(key);
    }
}
