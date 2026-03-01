package lombok.core;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AgentLauncher {
    private static final List<AgentInfo> AGENTS = Collections.unmodifiableList(Arrays.asList(new EclipsePatcherInfo()));

    public static void runAgents(String agentArgs, Instrumentation instrumentation, boolean injected, Class<?> launchingContext) throws Throwable {
        for (AgentInfo info : AGENTS) {
            try {
                Class<?> agentClass = Class.forName(info.className());
                AgentLaunchable agent = (AgentLaunchable)agentClass.getConstructor(new Class[0]).newInstance(new Object[0]);
                agent.runAgent(agentArgs, instrumentation, injected, launchingContext);
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    t = t.getCause();
                }
                info.problem(t, instrumentation);
            }
        }
    }

    private static abstract class AgentInfo {
        private AgentInfo() {
        }

        abstract String className();

        void problem(Throwable t, Instrumentation instrumentation) throws Throwable {
            if (t instanceof ClassNotFoundException) {
                return;
            }
            if (t instanceof ClassCastException) {
                throw new InternalError("Lombok bug. Class: " + this.className() + " is not an implementation of lombok.core.Agent");
            }
            if (t instanceof IllegalAccessError) {
                throw new InternalError("Lombok bug. Class: " + this.className() + " is not public");
            }
            if (t instanceof InstantiationException) {
                throw new InternalError("Lombok bug. Class: " + this.className() + " is not concrete or has no public no-args constructor");
            }
            throw t;
        }
    }

    public static interface AgentLaunchable {
        public void runAgent(String var1, Instrumentation var2, boolean var3, Class<?> var4) throws Exception;
    }

    private static class EclipsePatcherInfo
    extends AgentInfo {
        private EclipsePatcherInfo() {
        }

        @Override
        String className() {
            return "lombok.eclipse.agent.EclipsePatcher";
        }
    }
}
