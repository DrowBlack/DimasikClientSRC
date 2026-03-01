package cpw.mods.modlauncher.api;

import java.util.Map;
import java.util.function.BiFunction;

public interface INameMappingService {
    public String mappingName();

    public String mappingVersion();

    public Map.Entry<String, String> understanding();

    public BiFunction<Domain, String, String> namingFunction();

    public static enum Domain {
        CLASS,
        METHOD,
        FIELD;

    }
}
