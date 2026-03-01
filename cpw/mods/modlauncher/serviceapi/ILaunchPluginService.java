package cpw.mods.modlauncher.serviceapi;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public interface ILaunchPluginService {
    public String name();

    public EnumSet<Phase> handlesClass(Type var1, boolean var2);

    default public EnumSet<Phase> handlesClass(Type classType, boolean isEmpty, String reason) {
        return this.handlesClass(classType, isEmpty);
    }

    public boolean processClass(Phase var1, ClassNode var2, Type var3);

    default public boolean processClass(Phase phase, ClassNode classNode, Type classType, String reason) {
        return this.processClass(phase, classNode, classType);
    }

    @Deprecated
    default public void addResource(Path resource, String name) {
    }

    default public void addResources(List<Map.Entry<String, Path>> resources) {
    }

    default public void initializeLaunch(ITransformerLoader transformerLoader, Path[] specialPaths) {
    }

    default public <T> T getExtension() {
        return null;
    }

    default public void customAuditConsumer(String className, Consumer<String[]> auditDataAcceptor) {
    }

    public static interface ITransformerLoader {
        public byte[] buildTransformedClassNodeFor(String var1) throws ClassNotFoundException;
    }

    public static enum Phase {
        BEFORE,
        AFTER;

    }
}
