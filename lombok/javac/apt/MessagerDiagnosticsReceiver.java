package lombok.javac.apt;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import lombok.core.DiagnosticsReceiver;

public class MessagerDiagnosticsReceiver
implements DiagnosticsReceiver {
    private final Messager messager;

    public MessagerDiagnosticsReceiver(Messager messager) {
        this.messager = messager;
    }

    @Override
    public void addWarning(String message) {
        this.messager.printMessage(Diagnostic.Kind.WARNING, message);
    }

    @Override
    public void addError(String message) {
        this.messager.printMessage(Diagnostic.Kind.ERROR, message);
    }
}
