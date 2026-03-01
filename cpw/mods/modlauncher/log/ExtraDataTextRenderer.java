package cpw.mods.modlauncher.log;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformerAuditTrail;
import java.util.Optional;
import org.apache.logging.log4j.core.pattern.TextRenderer;

public class ExtraDataTextRenderer
implements TextRenderer {
    private final TextRenderer wrapped;
    private final Optional<ITransformerAuditTrail> auditData;
    private ThreadLocal<TransformerContext> currentClass = new ThreadLocal();

    ExtraDataTextRenderer(TextRenderer wrapped) {
        this.wrapped = wrapped;
        this.auditData = Optional.ofNullable(Launcher.INSTANCE).map(Launcher::environment).flatMap(env -> env.getProperty(IEnvironment.Keys.AUDITTRAIL.get()));
    }

    @Override
    public void render(String input, StringBuilder output, String styleName) {
        if ("StackTraceElement.ClassName".equals(styleName)) {
            this.currentClass.set(new TransformerContext());
            this.currentClass.get().setClassName(input);
        } else if ("StackTraceElement.MethodName".equals(styleName)) {
            TransformerContext transformerContext = this.currentClass.get();
            if (transformerContext != null) {
                transformerContext.setMethodName(input);
            }
        } else if ("Suffix".equals(styleName)) {
            TransformerContext classContext = this.currentClass.get();
            this.currentClass.remove();
            if (classContext != null) {
                Optional<String> auditLine = this.auditData.map(data -> data.getAuditString(classContext.getClassName()));
                this.wrapped.render(" {" + auditLine.orElse("") + "}", output, "StackTraceElement.Transformers");
            }
            return;
        }
        this.wrapped.render(input, output, styleName);
    }

    @Override
    public void render(StringBuilder input, StringBuilder output) {
        this.wrapped.render(input, output);
    }

    private static class TransformerContext {
        private String className;
        private String methodName;

        private TransformerContext() {
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getClassName() {
            return this.className;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return this.methodName;
        }

        public String toString() {
            return this.getClassName() + "." + this.getMethodName();
        }
    }
}
