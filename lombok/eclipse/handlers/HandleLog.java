package lombok.eclipse.handlers;

import java.util.List;
import lombok.ConfigurationKeys;
import lombok.CustomLog;
import lombok.core.AnnotationValues;
import lombok.core.configuration.IdentifierName;
import lombok.core.configuration.LogDeclaration;
import lombok.core.handlers.HandlerUtil;
import lombok.core.handlers.LoggingFramework;
import lombok.eclipse.EclipseAnnotationHandler;
import lombok.eclipse.EclipseNode;
import lombok.eclipse.handlers.EclipseHandlerUtil;
import lombok.eclipse.handlers.SetGeneratedByVisitor;
import lombok.extern.apachecommons.CommonsLog;
import lombok.extern.flogger.Flogger;
import lombok.extern.java.Log;
import lombok.extern.jbosslog.JBossLog;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.ClassLiteralAccess;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.NullLiteral;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.StringLiteral;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;

public class HandleLog {
    private static final IdentifierName LOG = IdentifierName.valueOf("log");

    private HandleLog() {
        throw new UnsupportedOperationException();
    }

    public static void processAnnotation(LoggingFramework framework, AnnotationValues<? extends java.lang.annotation.Annotation> annotation, Annotation source, EclipseNode annotationNode) {
        EclipseNode owner = (EclipseNode)annotationNode.up();
        switch (owner.getKind()) {
            case TYPE: {
                boolean notAClass;
                IdentifierName logFieldName = annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_NAME);
                if (logFieldName == null) {
                    logFieldName = LOG;
                }
                boolean useStatic = !Boolean.FALSE.equals(annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_ANY_FIELD_IS_STATIC));
                TypeDeclaration typeDecl = null;
                if (owner.get() instanceof TypeDeclaration) {
                    typeDecl = (TypeDeclaration)owner.get();
                }
                int modifiers = typeDecl == null ? 0 : typeDecl.modifiers;
                boolean bl = notAClass = (modifiers & 0x2200) != 0;
                if (typeDecl == null || notAClass) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " is legal only on classes and enums.");
                    return;
                }
                if (EclipseHandlerUtil.fieldExists(logFieldName.getName(), owner) != EclipseHandlerUtil.MemberExistsResult.NOT_EXISTS) {
                    annotationNode.addWarning("Field '" + logFieldName + "' already exists.");
                    return;
                }
                if (EclipseHandlerUtil.isRecord(owner) && !useStatic) {
                    annotationNode.addError("Logger fields must be static in records.");
                    return;
                }
                if (useStatic && !EclipseHandlerUtil.isStaticAllowed(owner)) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " is not supported on non-static nested classes.");
                    return;
                }
                Object valueGuess = annotation.getValueGuess("topic");
                Expression loggerTopic = (Expression)annotation.getActualExpression("topic");
                if (valueGuess instanceof String && ((String)valueGuess).trim().isEmpty()) {
                    loggerTopic = null;
                }
                if (framework.getDeclaration().getParametersWithTopic() == null && loggerTopic != null) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " does not allow a topic.");
                    loggerTopic = null;
                }
                if (framework.getDeclaration().getParametersWithoutTopic() == null && loggerTopic == null) {
                    annotationNode.addError(String.valueOf(framework.getAnnotationAsString()) + " requires a topic.");
                    loggerTopic = new StringLiteral(new char[0], 0, 0, 0);
                }
                ClassLiteralAccess loggingType = HandleLog.selfType(owner, source);
                FieldDeclaration fieldDeclaration = HandleLog.createField(framework, source, loggingType, logFieldName.getName(), useStatic, loggerTopic);
                fieldDeclaration.traverse((ASTVisitor)new SetGeneratedByVisitor((ASTNode)source), typeDecl.staticInitializerScope);
                EclipseHandlerUtil.injectFieldAndMarkGenerated(owner, fieldDeclaration);
                owner.rebuild();
                break;
            }
        }
    }

    public static ClassLiteralAccess selfType(EclipseNode type, Annotation source) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        TypeDeclaration typeDeclaration = (TypeDeclaration)type.get();
        SingleTypeReference typeReference = new SingleTypeReference(typeDeclaration.name, p);
        EclipseHandlerUtil.setGeneratedBy(typeReference, (ASTNode)source);
        ClassLiteralAccess result = new ClassLiteralAccess(source.sourceEnd, (TypeReference)typeReference);
        EclipseHandlerUtil.setGeneratedBy(result, (ASTNode)source);
        return result;
    }

    private static FieldDeclaration createField(LoggingFramework framework, Annotation source, ClassLiteralAccess loggingType, String logFieldName, boolean useStatic, Expression loggerTopic) {
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        long p = (long)pS << 32 | (long)pE;
        FieldDeclaration fieldDecl = new FieldDeclaration(logFieldName.toCharArray(), 0, -1);
        EclipseHandlerUtil.setGeneratedBy(fieldDecl, (ASTNode)source);
        fieldDecl.declarationSourceEnd = -1;
        fieldDecl.modifiers = 2 | (useStatic ? 8 : 0) | 0x10;
        LogDeclaration logDeclaration = framework.getDeclaration();
        fieldDecl.type = EclipseHandlerUtil.createTypeReference(logDeclaration.getLoggerType().getName(), (ASTNode)source);
        MessageSend factoryMethodCall = new MessageSend();
        EclipseHandlerUtil.setGeneratedBy(factoryMethodCall, (ASTNode)source);
        factoryMethodCall.receiver = EclipseHandlerUtil.createNameReference(logDeclaration.getLoggerFactoryType().getName(), source);
        factoryMethodCall.selector = logDeclaration.getLoggerFactoryMethod().getCharArray();
        List<LogDeclaration.LogFactoryParameter> parameters = loggerTopic != null ? logDeclaration.getParametersWithTopic() : logDeclaration.getParametersWithoutTopic();
        factoryMethodCall.arguments = HandleLog.createFactoryParameters(loggingType, source, parameters, loggerTopic);
        factoryMethodCall.nameSourcePosition = p;
        factoryMethodCall.sourceStart = pS;
        factoryMethodCall.sourceEnd = factoryMethodCall.statementEnd = pE;
        fieldDecl.initialization = factoryMethodCall;
        return fieldDecl;
    }

    private static final Expression[] createFactoryParameters(ClassLiteralAccess loggingType, Annotation source, List<LogDeclaration.LogFactoryParameter> parameters, Expression loggerTopic) {
        Expression[] expressions = new Expression[parameters.size()];
        int pS = source.sourceStart;
        int pE = source.sourceEnd;
        int i = 0;
        while (i < parameters.size()) {
            LogDeclaration.LogFactoryParameter parameter = parameters.get(i);
            switch (parameter) {
                case TYPE: {
                    expressions[i] = HandleLog.createFactoryTypeParameter(loggingType, source);
                    break;
                }
                case NAME: {
                    long p = (long)pS << 32 | (long)pE;
                    MessageSend factoryParameterCall = new MessageSend();
                    EclipseHandlerUtil.setGeneratedBy(factoryParameterCall, (ASTNode)source);
                    factoryParameterCall.receiver = HandleLog.createFactoryTypeParameter(loggingType, source);
                    factoryParameterCall.selector = "getName".toCharArray();
                    factoryParameterCall.nameSourcePosition = p;
                    factoryParameterCall.sourceStart = pS;
                    factoryParameterCall.sourceEnd = factoryParameterCall.statementEnd = pE;
                    expressions[i] = factoryParameterCall;
                    break;
                }
                case TOPIC: {
                    expressions[i] = EclipseHandlerUtil.copyAnnotationMemberValue(loggerTopic);
                    break;
                }
                case NULL: {
                    expressions[i] = new NullLiteral(pS, pE);
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

    private static final Expression createFactoryTypeParameter(ClassLiteralAccess loggingType, Annotation source) {
        TypeReference copy = EclipseHandlerUtil.copyType(loggingType.type, (ASTNode)source);
        ClassLiteralAccess result = new ClassLiteralAccess(source.sourceEnd, copy);
        EclipseHandlerUtil.setGeneratedBy(result, (ASTNode)source);
        return result;
    }

    public static class HandleCommonsLog
    extends EclipseAnnotationHandler<CommonsLog> {
        @Override
        public void handle(AnnotationValues<CommonsLog> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_COMMONS_FLAG_USAGE, "@apachecommons.CommonsLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.COMMONS, annotation, source, annotationNode);
        }
    }

    public static class HandleCustomLog
    extends EclipseAnnotationHandler<CustomLog> {
        @Override
        public void handle(AnnotationValues<CustomLog> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_CUSTOM_FLAG_USAGE, "@CustomLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            LogDeclaration logDeclaration = annotationNode.getAst().readConfiguration(ConfigurationKeys.LOG_CUSTOM_DECLARATION);
            if (logDeclaration == null) {
                annotationNode.addError("The @CustomLog annotation is not configured; please set lombok.log.custom.declaration in lombok.config.");
                return;
            }
            LoggingFramework framework = new LoggingFramework(CustomLog.class, logDeclaration);
            HandleLog.processAnnotation(framework, annotation, source, annotationNode);
        }
    }

    public static class HandleFloggerLog
    extends EclipseAnnotationHandler<Flogger> {
        @Override
        public void handle(AnnotationValues<Flogger> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_FLOGGER_FLAG_USAGE, "@Flogger", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.FLOGGER, annotation, source, annotationNode);
        }
    }

    public static class HandleJBossLog
    extends EclipseAnnotationHandler<JBossLog> {
        @Override
        public void handle(AnnotationValues<JBossLog> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_JBOSSLOG_FLAG_USAGE, "@JBossLog", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.JBOSSLOG, annotation, source, annotationNode);
        }
    }

    public static class HandleJulLog
    extends EclipseAnnotationHandler<Log> {
        @Override
        public void handle(AnnotationValues<Log> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_JUL_FLAG_USAGE, "@java.Log", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.JUL, annotation, source, annotationNode);
        }
    }

    public static class HandleLog4j2Log
    extends EclipseAnnotationHandler<Log4j2> {
        @Override
        public void handle(AnnotationValues<Log4j2> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J2_FLAG_USAGE, "@Log4j2", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.LOG4J2, annotation, source, annotationNode);
        }
    }

    public static class HandleLog4jLog
    extends EclipseAnnotationHandler<Log4j> {
        @Override
        public void handle(AnnotationValues<Log4j> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_LOG4J_FLAG_USAGE, "@Log4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.LOG4J, annotation, source, annotationNode);
        }
    }

    public static class HandleSlf4jLog
    extends EclipseAnnotationHandler<Slf4j> {
        @Override
        public void handle(AnnotationValues<Slf4j> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_SLF4J_FLAG_USAGE, "@Slf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.SLF4J, annotation, source, annotationNode);
        }
    }

    public static class HandleXSlf4jLog
    extends EclipseAnnotationHandler<XSlf4j> {
        @Override
        public void handle(AnnotationValues<XSlf4j> annotation, Annotation source, EclipseNode annotationNode) {
            HandlerUtil.handleFlagUsage(annotationNode, ConfigurationKeys.LOG_XSLF4J_FLAG_USAGE, "@XSlf4j", ConfigurationKeys.LOG_ANY_FLAG_USAGE, "any @Log");
            HandleLog.processAnnotation(LoggingFramework.XSLF4J, annotation, source, annotationNode);
        }
    }
}
