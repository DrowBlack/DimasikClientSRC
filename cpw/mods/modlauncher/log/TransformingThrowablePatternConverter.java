package cpw.mods.modlauncher.log;

import cpw.mods.modlauncher.log.ExtraDataTextRenderer;
import java.util.Collections;
import joptsimple.internal.Strings;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.PlainTextRenderer;
import org.apache.logging.log4j.core.pattern.ThrowablePatternConverter;

@Plugin(name="TransformingThrowablePatternConverter", category="Converter")
@ConverterKeys(value={"tEx"})
public class TransformingThrowablePatternConverter
extends ThrowablePatternConverter {
    static final String SUFFIXFLAG = "\u2603\u2603\u2603\u2603\u2603SUFFIXFLAG\u2603\u2603\u2603\u2603\u2603";

    protected TransformingThrowablePatternConverter(Configuration config, String[] options) {
        super("TransformingThrowable", "throwable", options, config);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        ThrowableProxy proxy = event.getThrownProxy();
        Throwable throwable = event.getThrown();
        if ((throwable != null || proxy != null) && this.options.anyLines()) {
            if (proxy == null) {
                super.format(event, toAppendTo);
                return;
            }
            int len = toAppendTo.length();
            if (len > 0 && !Character.isWhitespace(toAppendTo.charAt(len - 1))) {
                toAppendTo.append(' ');
            }
            ExtraDataTextRenderer textRenderer = new ExtraDataTextRenderer(this.options.getTextRenderer());
            proxy.formatExtendedStackTraceTo(toAppendTo, this.options.getIgnorePackages(), textRenderer, SUFFIXFLAG, this.options.getSeparator());
        }
    }

    public static TransformingThrowablePatternConverter newInstance(Configuration config, String[] options) {
        return new TransformingThrowablePatternConverter(config, options);
    }

    public static String generateEnhancedStackTrace(Throwable throwable) {
        ThrowableProxy proxy = new ThrowableProxy(throwable);
        StringBuilder buffer = new StringBuilder();
        ExtraDataTextRenderer textRenderer = new ExtraDataTextRenderer(PlainTextRenderer.getInstance());
        proxy.formatExtendedStackTraceTo(buffer, Collections.emptyList(), textRenderer, SUFFIXFLAG, Strings.LINE_SEPARATOR);
        return buffer.toString();
    }
}
