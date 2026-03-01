package lombok.javac.handlers;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import lombok.ConfigurationKeys;
import lombok.CustomLog;
import lombok.core.AnnotationValues;
import lombok.core.configuration.IdentifierName;
import lombok.core.configuration.LogDeclaration;
import lombok.core.handlers.HandlerUtil;
import lombok.core.handlers.LoggingFramework;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.flogger.Flogger;
import lombok.extern.java.Log;
import lombok.extern.jbosslog.JBossLog;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import lombok.javac.Javac;
import lombok.javac.JavacAnnotationHandler;
import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

public class HandleLog {
    private static final IdentifierName LOG = IdentifierName.valueOf("log");

    private HandleLog() {
        throw new UnsupportedOperationException();
    }

    public static void processAnnotation(LoggingFramework framework, AnnotationValues<?> annotation, JavacNode annotationNode) {
        JavacHandlerUtil.deleteAnnotationIfNeccessary(annotationNode, framework.getAnnotationClass());
        JavacNode typeNode = (JavacNode)annotationNode.up();
        switch (typeNode.getKind()) {
            case TYPE: {
                boolean useStatic;
                IdentifierName logFieldName = annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_NAME);
                if (logFieldName == null) {
                    logFieldName = LOG;
                }
                boolean bl = useStatic = !Boolean.FALSE.equals(annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_IS_STATIC));
                if ((((JCTree.JCClassDecl)typeNode.get()).mods.flags & 0x200L) != 0L) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " is legal only on classes and enums.");
                    return;
                }
                if (JavacHandlerUtil.fieldExists(logFieldName.getName(), typeNode) != JavacHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                    annotationNode.addWarning("Field '" + logFieldName + "' already exists.");
                    return;
                }
                if (JavacHandlerUtil.isRecord(typeNode) && !useStatic) {
                    annotationNode.addError("Logger fields must be static in records.");
                    return;
                }
                if (useStatic && !JavacHandlerUtil.isStaticAllowed(typeNode)) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " is not supported on non-static nested classes.");
                    return;
                }
                Object valueGuess = annotation.getValueGuess("topic");
                JCTree.JCExpression loggerTopic = (JCTree.JCExpression)annotation.getActualExpression("topic");
                if (valueGuess instanceof String && ((String)valueGuess).trim().isEmpty()) {
                    loggerTopic = null;
                }
                if (framework.getDeclaration().getParametersWithTopic() == null && loggerTopic != null) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " does not allow a topic.");
                    loggerTopic = null;
                }
                if (framework.getDeclaration().getParametersWithoutTopic() == null && loggerTopic == null) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " requires a topic.");
                    loggerTopic = typeNode.getTreeMaker().Literal("");
                }
                JCTree.JCFieldAccess loggingType = HandleLog.selfType(typeNode);
                HandleLog.createField(framework, typeNode, loggingType, annotationNode, logFieldName.getName(), useStatic, loggerTopic);
                break;
            }
            default: {
                annotationNode.addError("@Log is legal only on types.");
            }
        }
    }

    public static JCTree.JCFieldAccess selfType(JavacNode typeNode) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        Name name = ((JCTree.JCClassDecl)typeNode.get()).name;
        return maker.Select(maker.Ident(name), typeNode.toName("class"));
    }

    private static boolean createField(LoggingFramework framework, JavacNode typeNode, JCTree.JCFieldAccess loggingType, JavacNode source, String logFieldName, boolean useStatic, JCTree.JCExpression loggerTopic) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        LogDeclaration logDeclaration = framework.getDeclaration();
        JCTree.JCExpression loggerType = JavacHandlerUtil.chainDotsString(typeNode, logDeclaration.getLoggerType().getName());
        JCTree.JCExpression factoryMethod = JavacHandlerUtil.chainDotsString(typeNode, String.valueOf(logDeclaration.getLoggerFactoryType().getName()) + "." + logDeclaration.getLoggerFactoryMethod().getName());
        java.util.List<LogDeclaration.LogFactoryParameter> parameters = loggerTopic != null ? logDeclaration.getParametersWithTopic() : logDeclaration.getParametersWithoutTopic();
        JCTree.JCExpression[] factoryParameters = HandleLog.createFactoryParameters(typeNode, loggingType, parameters, loggerTopic);
        JCTree.JCMethodInvocation factoryMethodCall = maker.Apply(List.<JCTree.JCExpression>nil(), factoryMethod, List.from(factoryParameters));
        JCTree.JCVariableDecl fieldDecl = JavacHandlerUtil.recursiveSetGeneratedBy(maker.VarDef(maker.Modifiers(0x12 | (useStatic ? 8 : 0)), typeNode.toName(logFieldName), loggerType, factoryMethodCall), source);
        if (JavacHandlerUtil.isRecord(typeNode) && Javac.getJavaCompilerVersion() < 16) {
            JavacHandlerUtil.injectField(typeNode, fieldDecl);
        } else {
            JavacHandlerUtil.injectFieldAndMarkGenerated(typeNode, fieldDecl);
        }
        return true;
    }

    private static JCTree.JCExpression[] createFactoryParameters(JavacNode typeNode, JCTree.JCFieldAccess loggingType, java.util.List<LogDeclaration.LogFactoryParameter> parameters, JCTree.JCExpression loggerTopic) {
        JCTree.JCExpression[] expressions = new JCTree.JCExpression[parameters.size()];
        JavacTreeMaker maker = typeNode.getTreeMaker();
        int i = 0;
        while (i < parameters.size()) {
            LogDeclaration.LogFactoryParameter parameter = parameters.get(i);
            switch (parameter) {
                case TYPE: {
                    expressions[i] = JavacHandlerUtil.cloneType(maker, loggingType, typeNode);
                    break;
                }
                case NAME: {
                    JCTree.JCFieldAccess method = maker.Select(loggingType, typeNode.toName("getName"));
                    expressions[i] = maker.Apply(List.<JCTree.JCExpression>nil(), method, List.<JCTree.JCExpression>nil());
                    break;
                }
                case TOPIC: {
                    if (loggerTopic instanceof JCTree.JCLiteral) {
                        expressions[i] = maker.Literal(((JCTree.JCLiteral)loggerTopic).value);
                        break;
                    }
                    expressions[i] = JavacHandlerUtil.cloneType(maker, loggerTopic, typeNode);
                    break;
                }
                case NULL: {
                    expressions[i] = maker.Literal(Javac.CTC_BOT, null);
                    break;
                }
                default: {
                    throw new IllegalStateException("Unknown logger factory parameter type: " + (Object)((Object)parameter));
                }
            }
            ++i;
        }
        return expressions;
    }

    public static class HandleCommonsLog
    extends JavacAnnotationHandler<CommonsLog> {
        @Override
        public void handle(AnnotationValues<CommonsLog> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_COMMONS_FLAG_USAGE, "@apachecommons.CommonsLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.COMMONS, annotation, annotationNode);
        }
    }

    public static class HandleCustomLog
    extends JavacAnnotationHandler<CustomLog> {
        @Override
        public void handle(AnnotationValues<CustomLog> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_CUSTOM_FLAG_USAGE, "@CustomLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            LogDeclaration logDeclaration = annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_CUSTOM_DECLARATION);
            if (logDeclaration == null) {
                annotationNode.addError("The @CustomLog is not configured; please set lombok.log.custom.declaration in lombok.config.");
                return;
            }
            LoggingFramework framework = new LoggingFramework(CustomLog.class, logDeclaration);
            HandleLog.processAnnotation(framework, annotation, annotationNode);
        }
    }

    public static class HandleFloggerLog
    extends JavacAnnotationHandler<Flogger> {
        @Override
        public void handle(AnnotationValues<Flogger> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_FLOGGER_FLAG_USAGE, "@Flogger", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.FLOGGER, annotation, annotationNode);
        }
    }

    public static class HandleJBossLog
    extends JavacAnnotationHandler<JBossLog> {
        @Override
        public void handle(AnnotationValues<JBossLog> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_JBOSSLOG_FLAG_USAGE, "@JBossLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.JBOSSLOG, annotation, annotationNode);
        }
    }

    public static class HandleJulLog
    extends JavacAnnotationHandler<Log> {
        @Override
        public void handle(AnnotationValues<Log> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_JUL_FLAG_USAGE, "@java.Log", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.JUL, annotation, annotationNode);
        }
    }

    public static class HandleLog4j2Log
    extends JavacAnnotationHandler<Log4j2> {
        @Override
        public void handle(AnnotationValues<Log4j2> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J2_FLAG_USAGE, "@Log4j2", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.LOG4J2, annotation, annotationNode);
        }
    }

    public static class HandleLog4jLog
    extends JavacAnnotationHandler<Log4j> {
        @Override
        public void handle(AnnotationValues<Log4j> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J_FLAG_USAGE, "@Log4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.LOG4J, annotation, annotationNode);
        }
    }

    public static class HandleSlf4jLog
    extends JavacAnnotationHandler<Slf4j> {
        @Override
        public void handle(AnnotationValues<Slf4j> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_SLF4J_FLAG_USAGE, "@Slf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.SLF4J, annotation, annotationNode);
        }
    }

    public static class HandleXSlf4jLog
    extends JavacAnnotationHandler<XSlf4j> {
        @Override
        public void handle(AnnotationValues<XSlf4j> annotation, JCTree.JCAnnotation ast, JavacNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_XSLF4J_FLAG_USAGE, "@XSlf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.XSLF4J, annotation, annotationNode);
        }
    }
}
