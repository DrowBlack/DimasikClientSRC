package lombok.core;

import java.util.Collections;
import java.util.List;

public abstract class LombokApp {
    public abstract int runApp(List<String> var1) throws Exception;

    public abstract String getAppName();

    public abstract String getAppDescription();

    public List<String> getAppAliases() {
        return Collections.emptyList();
    }

    public boolean isDebugTool() {
        return false;
    }
}
