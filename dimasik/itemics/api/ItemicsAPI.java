package dimasik.itemics.api;

import dimasik.itemics.api.IItemicsProvider;
import dimasik.itemics.api.Settings;
import dimasik.itemics.api.utils.SettingsUtil;

public final class ItemicsAPI {
    private static final IItemicsProvider provider;
    private static final Settings settings;

    public static IItemicsProvider getProvider() {
        return provider;
    }

    public static Settings getSettings() {
        return settings;
    }

    static {
        settings = new Settings();
        SettingsUtil.readAndApply(settings);
        try {
            provider = (IItemicsProvider)Class.forName("dimasik.itemics.ItemicsProvider").newInstance();
        }
        catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
