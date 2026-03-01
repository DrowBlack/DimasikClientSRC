package lombok.core.configuration;

import lombok.eclipse.handlers.EclipseHandlerUtil;

public interface ConfigurationProblemReporter {
    public static final ConfigurationProblemReporter CONSOLE = new ConfigurationProblemReporter(){

        @Override
        public void report(String sourceDescription, String problem, int lineNumber, CharSequence line) {
            try {
                EclipseHandlerUtil.warning(String.format("%s (%s:%d)", problem, sourceDescription, lineNumber), null);
            }
            catch (Throwable throwable) {}
            System.err.printf("%s (%s:%d)\n", problem, sourceDescription, lineNumber);
        }
    };

    public void report(String var1, String var2, int var3, CharSequence var4);
}
