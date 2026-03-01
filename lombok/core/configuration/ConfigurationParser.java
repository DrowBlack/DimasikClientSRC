package lombok.core.configuration;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.core.configuration.ConfigurationDataType;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationKey;
import lombok.core.configuration.ConfigurationProblemReporter;

public class ConfigurationParser {
    private static final Pattern LINE = Pattern.compile("(?:clear\\s+([^=]+))|(?:(\\S*?)\\s*([-+]?=)\\s*(.*?))");
    private static final Pattern NEWLINE_FINDER = Pattern.compile("^[\t ]*(.*?)[\t\r ]*$", 8);
    private static final Pattern IMPORT = Pattern.compile("import\\s+(.+?)");
    private ConfigurationProblemReporter reporter;

    public ConfigurationParser(ConfigurationProblemReporter reporter) {
        if (reporter == null) {
            throw new NullPointerException("reporter");
        }
        this.reporter = reporter;
    }

    public void parse(ConfigurationFile context, Collector collector) {
        CharSequence contents = this.contents(context);
        if (contents == null) {
            return;
        }
        Map<String, ConfigurationKey<?>> registeredKeys = ConfigurationKey.registeredKeys();
        int lineNumber = 0;
        Matcher lineMatcher = NEWLINE_FINDER.matcher(contents);
        boolean importsAllowed = true;
        while (lineMatcher.find()) {
            boolean listOperator;
            String stringValue;
            CharSequence line = contents.subSequence(lineMatcher.start(1), lineMatcher.end(1));
            ++lineNumber;
            if (line.length() == 0 || line.charAt(0) == '#') continue;
            Matcher importMatcher = IMPORT.matcher(line);
            if (importMatcher.matches()) {
                if (!importsAllowed) {
                    this.reporter.report(context.description(), "Imports are only allowed in the top of the file", lineNumber, line);
                    continue;
                }
                String imported = importMatcher.group(1);
                ConfigurationFile importFile = context.resolve(imported);
                if (importFile == null) {
                    this.reporter.report(context.description(), "Import is not valid", lineNumber, line);
                    continue;
                }
                if (!importFile.exists()) {
                    this.reporter.report(context.description(), "Imported file does not exist", lineNumber, line);
                    continue;
                }
                collector.addImport(importFile, context, lineNumber);
                continue;
            }
            Matcher matcher = LINE.matcher(line);
            if (!matcher.matches()) {
                this.reporter.report(context.description(), "Invalid line", lineNumber, line);
                continue;
            }
            importsAllowed = false;
            String operator = null;
            String keyName = null;
            if (matcher.group(1) == null) {
                keyName = matcher.group(2);
                operator = matcher.group(3);
                stringValue = matcher.group(4);
            } else {
                keyName = matcher.group(1);
                operator = "clear";
                stringValue = null;
            }
            ConfigurationKey<?> key = registeredKeys.get(keyName);
            if (key == null) {
                this.reporter.report(context.description(), "Unknown key '" + keyName + "'", lineNumber, line);
                continue;
            }
            ConfigurationDataType type = key.getType();
            boolean bl = listOperator = operator.equals("+=") || operator.equals("-=");
            if (listOperator && !type.isList()) {
                this.reporter.report(context.description(), "'" + keyName + "' is not a list and doesn't support " + operator + " (only = and clear)", lineNumber, line);
                continue;
            }
            if (operator.equals("=") && type.isList()) {
                this.reporter.report(context.description(), "'" + keyName + "' is a list and cannot be assigned to (use +=, -= and clear instead)", lineNumber, line);
                continue;
            }
            Object value = null;
            if (stringValue != null) {
                try {
                    value = type.getParser().parse(stringValue);
                }
                catch (Exception exception) {
                    this.reporter.report(context.description(), "Error while parsing the value for '" + keyName + "' value '" + stringValue + "' (should be " + type.getParser().exampleValue() + ")", lineNumber, line);
                    continue;
                }
            }
            if (operator.equals("clear")) {
                collector.clear(key, context, lineNumber);
                continue;
            }
            if (operator.equals("=")) {
                collector.set(key, value, context, lineNumber);
                continue;
            }
            if (operator.equals("+=")) {
                collector.add(key, value, context, lineNumber);
                continue;
            }
            collector.remove(key, value, context, lineNumber);
        }
    }

    private CharSequence contents(ConfigurationFile context) {
        try {
            return context.contents();
        }
        catch (IOException e) {
            this.reporter.report(context.description(), "Exception while reading file: " + e.getMessage(), 0, null);
            return null;
        }
    }

    public static interface Collector {
        public void addImport(ConfigurationFile var1, ConfigurationFile var2, int var3);

        public void clear(ConfigurationKey<?> var1, ConfigurationFile var2, int var3);

        public void set(ConfigurationKey<?> var1, Object var2, ConfigurationFile var3, int var4);

        public void add(ConfigurationKey<?> var1, Object var2, ConfigurationFile var3, int var4);

        public void remove(ConfigurationKey<?> var1, Object var2, ConfigurationFile var3, int var4);
    }
}
