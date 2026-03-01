package org.openjdk.nashorn.internal.runtime;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.openjdk.nashorn.internal.codegen.CompilerConstants;
import org.openjdk.nashorn.internal.objects.Global;
import org.openjdk.nashorn.internal.runtime.Context;
import org.openjdk.nashorn.internal.runtime.ECMAException;
import org.openjdk.nashorn.internal.runtime.JSErrorType;
import org.openjdk.nashorn.internal.runtime.ParserException;
import org.openjdk.nashorn.internal.scripts.JS;

public final class ECMAErrors {
    private static final String MESSAGES_RESOURCE = "org.openjdk.nashorn.internal.runtime.resources.Messages";
    private static final ResourceBundle MESSAGES_BUNDLE = ResourceBundle.getBundle("org.openjdk.nashorn.internal.runtime.resources.Messages", Locale.getDefault());
    private static final String scriptPackage;

    private ECMAErrors() {
    }

    private static ECMAException error(Object thrown, Throwable cause) {
        return new ECMAException(thrown, cause);
    }

    public static ECMAException asEcmaException(ParserException e) {
        return ECMAErrors.asEcmaException(Context.getGlobal(), e);
    }

    public static ECMAException asEcmaException(Global global, ParserException e) {
        JSErrorType errorType = e.getErrorType();
        assert (errorType != null) : "error type for " + e + " was null";
        String msg = e.getMessage();
        switch (errorType) {
            case ERROR: {
                return ECMAErrors.error(global.newError(msg), e);
            }
            case EVAL_ERROR: {
                return ECMAErrors.error(global.newEvalError(msg), e);
            }
            case RANGE_ERROR: {
                return ECMAErrors.error(global.newRangeError(msg), e);
            }
            case REFERENCE_ERROR: {
                return ECMAErrors.error(global.newReferenceError(msg), e);
            }
            case SYNTAX_ERROR: {
                return ECMAErrors.error(global.newSyntaxError(msg), e);
            }
            case TYPE_ERROR: {
                return ECMAErrors.error(global.newTypeError(msg), e);
            }
            case URI_ERROR: {
                return ECMAErrors.error(global.newURIError(msg), e);
            }
        }
        throw new AssertionError((Object)e.getMessage());
    }

    public static ECMAException syntaxError(String msgId, String ... args) {
        return ECMAErrors.syntaxError(Context.getGlobal(), msgId, args);
    }

    public static ECMAException syntaxError(Global global, String msgId, String ... args) {
        return ECMAErrors.syntaxError(global, null, msgId, args);
    }

    public static ECMAException syntaxError(Throwable cause, String msgId, String ... args) {
        return ECMAErrors.syntaxError(Context.getGlobal(), cause, msgId, args);
    }

    public static ECMAException syntaxError(Global global, Throwable cause, String msgId, String ... args) {
        String msg = ECMAErrors.getMessage("syntax.error." + msgId, args);
        return ECMAErrors.error(global.newSyntaxError(msg), cause);
    }

    public static ECMAException typeError(String msgId, String ... args) {
        return ECMAErrors.typeError(Context.getGlobal(), msgId, args);
    }

    public static ECMAException typeError(Global global, String msgId, String ... args) {
        return ECMAErrors.typeError(global, null, msgId, args);
    }

    public static ECMAException typeError(Throwable cause, String msgId, String ... args) {
        return ECMAErrors.typeError(Context.getGlobal(), cause, msgId, args);
    }

    public static ECMAException typeError(Global global, Throwable cause, String msgId, String ... args) {
        String msg = ECMAErrors.getMessage("type.error." + msgId, args);
        return ECMAErrors.error(global.newTypeError(msg), cause);
    }

    public static ECMAException rangeError(String msgId, String ... args) {
        return ECMAErrors.rangeError(Context.getGlobal(), msgId, args);
    }

    public static ECMAException rangeError(Global global, String msgId, String ... args) {
        return ECMAErrors.rangeError(global, null, msgId, args);
    }

    public static ECMAException rangeError(Throwable cause, String msgId, String ... args) {
        return ECMAErrors.rangeError(Context.getGlobal(), cause, msgId, args);
    }

    public static ECMAException rangeError(Global global, Throwable cause, String msgId, String ... args) {
        String msg = ECMAErrors.getMessage("range.error." + msgId, args);
        return ECMAErrors.error(global.newRangeError(msg), cause);
    }

    public static ECMAException referenceError(String msgId, String ... args) {
        return ECMAErrors.referenceError(Context.getGlobal(), msgId, args);
    }

    public static ECMAException referenceError(Global global, String msgId, String ... args) {
        return ECMAErrors.referenceError(global, null, msgId, args);
    }

    public static ECMAException referenceError(Throwable cause, String msgId, String ... args) {
        return ECMAErrors.referenceError(Context.getGlobal(), cause, msgId, args);
    }

    public static ECMAException referenceError(Global global, Throwable cause, String msgId, String ... args) {
        String msg = ECMAErrors.getMessage("reference.error." + msgId, args);
        return ECMAErrors.error(global.newReferenceError(msg), cause);
    }

    public static ECMAException uriError(String msgId, String ... args) {
        return ECMAErrors.uriError(Context.getGlobal(), msgId, args);
    }

    public static ECMAException uriError(Global global, String msgId, String ... args) {
        return ECMAErrors.uriError(global, null, msgId, args);
    }

    public static ECMAException uriError(Throwable cause, String msgId, String ... args) {
        return ECMAErrors.uriError(Context.getGlobal(), cause, msgId, args);
    }

    public static ECMAException uriError(Global global, Throwable cause, String msgId, String ... args) {
        String msg = ECMAErrors.getMessage("uri.error." + msgId, args);
        return ECMAErrors.error(global.newURIError(msg), cause);
    }

    public static String getMessage(String msgId, String ... args) {
        try {
            return new MessageFormat(MESSAGES_BUNDLE.getString(msgId)).format(args);
        }
        catch (MissingResourceException e) {
            throw new RuntimeException("no message resource found for message id: " + msgId);
        }
    }

    public static boolean isScriptFrame(StackTraceElement frame) {
        String className = frame.getClassName();
        if (className.startsWith(scriptPackage) && !CompilerConstants.isInternalMethodName(frame.getMethodName())) {
            String source = frame.getFileName();
            return source != null && !source.endsWith(".java");
        }
        return false;
    }

    static {
        String name = JS.class.getName();
        scriptPackage = name.substring(0, name.lastIndexOf(46));
    }
}
