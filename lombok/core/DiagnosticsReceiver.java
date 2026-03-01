package lombok.core;

public interface DiagnosticsReceiver {
    public static final DiagnosticsReceiver CONSOLE = new DiagnosticsReceiver(){

        @Override
        public void addError(String message) {
            System.err.println("Error: " + message);
        }

        @Override
        public void addWarning(String message) {
            System.out.println("Warning: " + message);
        }
    };

    public void addError(String var1);

    public void addWarning(String var1);
}
