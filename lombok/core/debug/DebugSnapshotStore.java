package lombok.core.debug;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.core.debug.DebugSnapshot;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

public class DebugSnapshotStore {
    public static final DebugSnapshotStore INSTANCE = new DebugSnapshotStore();
    public static final boolean GLOBAL_DSS_DISABLE_SWITCH = true;
    private final Map<CompilationUnitDeclaration, List<DebugSnapshot>> map = new WeakHashMap<CompilationUnitDeclaration, List<DebugSnapshot>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void snapshot(CompilationUnitDeclaration owner, String message, Object ... params) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void log(CompilationUnitDeclaration owner, String message, Object ... params) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String print(CompilationUnitDeclaration owner, String message, Object ... params) {
        return null;
    }
}
