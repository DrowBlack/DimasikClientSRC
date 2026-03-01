package lombok.core.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationKey;
import lombok.core.configuration.ConfigurationParser;
import lombok.core.configuration.ConfigurationSource;

public final class SingleConfigurationSource
implements ConfigurationSource {
    private final Map<ConfigurationKey<?>, ConfigurationSource.Result> values = new HashMap();
    private final List<ConfigurationFile> imports;

    public static ConfigurationSource parse(ConfigurationFile context, ConfigurationParser parser) {
        final HashMap values = new HashMap();
        final ArrayList<ConfigurationFile> imports = new ArrayList<ConfigurationFile>();
        ConfigurationParser.Collector collector = new ConfigurationParser.Collector(){

            @Override
            public void addImport(ConfigurationFile importFile, ConfigurationFile context, int lineNumber) {
                imports.add(importFile);
            }

            @Override
            public void clear(ConfigurationKey<?> key, ConfigurationFile context, int lineNumber) {
                values.put(key, new ConfigurationSource.Result(null, true));
            }

            @Override
            public void set(ConfigurationKey<?> key, Object value, ConfigurationFile context, int lineNumber) {
                values.put(key, new ConfigurationSource.Result(value, true));
            }

            @Override
            public void add(ConfigurationKey<?> key, Object value, ConfigurationFile context, int lineNumber) {
                this.modifyList(key, value, true);
            }

            @Override
            public void remove(ConfigurationKey<?> key, Object value, ConfigurationFile context, int lineNumber) {
                this.modifyList(key, value, false);
            }

            private void modifyList(ConfigurationKey<?> key, Object value, boolean add) {
                List<ConfigurationSource.ListModification> list;
                ConfigurationSource.Result result = (ConfigurationSource.Result)values.get(key);
                if (result == null || result.getValue() == null) {
                    list = new ArrayList();
                    values.put(key, new ConfigurationSource.Result(list, result != null));
                } else {
                    list = (List)result.getValue();
                }
                list.add(new ConfigurationSource.ListModification(value, add));
            }
        };
        parser.parse(context, collector);
        return new SingleConfigurationSource(values, imports);
    }

    private SingleConfigurationSource(Map<ConfigurationKey<?>, ConfigurationSource.Result> values, List<ConfigurationFile> imports) {
        for (Map.Entry<ConfigurationKey<?>, ConfigurationSource.Result> entry : values.entrySet()) {
            ConfigurationSource.Result result = entry.getValue();
            if (result.getValue() instanceof List) {
                this.values.put(entry.getKey(), new ConfigurationSource.Result(Collections.unmodifiableList((List)result.getValue()), result.isAuthoritative()));
                continue;
            }
            this.values.put(entry.getKey(), result);
        }
        this.imports = Collections.unmodifiableList(imports);
    }

    @Override
    public ConfigurationSource.Result resolve(ConfigurationKey<?> key) {
        return this.values.get(key);
    }

    @Override
    public List<ConfigurationFile> imports() {
        return this.imports;
    }
}
