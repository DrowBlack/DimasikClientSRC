package lombok.core.handlers;

import java.util.Arrays;
import java.util.List;
import lombok.core.runtimeDependencies.RuntimeDependencyInfo;

public class SneakyThrowsAndCleanupDependencyInfo
implements RuntimeDependencyInfo {
    @Override
    public List<String> getRuntimeDependencies() {
        return Arrays.asList("lombok/Lombok.class");
    }

    @Override
    public List<String> getRuntimeDependentsDescriptions() {
        return Arrays.asList("@SneakyThrows (only when delomboking - using @SneakyThrows in code that is compiled with lombok on the classpath does not create the dependency)", "@Cleanup (only when delomboking - using @Cleanup in code that is compiled with lombok on the classpath does not create the dependency)");
    }
}
