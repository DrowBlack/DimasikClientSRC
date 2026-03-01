package lombok.core.configuration;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.ConfigurationKeys;
import lombok.core.SpiLoadUtil;

public interface ConfigurationKeysLoader {

    public static class LoaderLoader {
        private static final AtomicBoolean alreadyLoaded = new AtomicBoolean(false);

        private LoaderLoader() {
        }

        public static void loadAllConfigurationKeys() {
            if (alreadyLoaded.get()) {
                return;
            }
            try {
                Class.forName(ConfigurationKeys.class.getName());
            }
            catch (Throwable throwable) {}
            try {
                try {
                    Iterator<ConfigurationKeysLoader> iterator = SpiLoadUtil.findServices(ConfigurationKeysLoader.class, ConfigurationKeysLoader.class.getClassLoader()).iterator();
                    while (iterator.hasNext()) {
                        try {
                            iterator.next();
                        }
                        catch (Exception exception) {}
                    }
                }
                catch (IOException e) {
                    throw new RuntimeException("Can't load config keys; services file issue.", e);
                }
            }
            finally {
                alreadyLoaded.set(true);
            }
        }
    }
}
