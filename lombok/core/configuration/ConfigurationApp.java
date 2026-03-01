package lombok.core.configuration;

import com.zwitserloot.cmdreader.CmdReader;
import com.zwitserloot.cmdreader.Description;
import com.zwitserloot.cmdreader.Excludes;
import com.zwitserloot.cmdreader.FullName;
import com.zwitserloot.cmdreader.InvalidCommandLineException;
import com.zwitserloot.cmdreader.Mandatory;
import com.zwitserloot.cmdreader.Requires;
import com.zwitserloot.cmdreader.Sequential;
import com.zwitserloot.cmdreader.Shorthand;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import lombok.ConfigurationKeys;
import lombok.core.LombokApp;
import lombok.core.configuration.BubblingConfigurationResolver;
import lombok.core.configuration.ConfigurationDataType;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationKey;
import lombok.core.configuration.ConfigurationKeysLoader;
import lombok.core.configuration.ConfigurationParser;
import lombok.core.configuration.ConfigurationProblemReporter;
import lombok.core.configuration.FileSystemSourceCache;

public class ConfigurationApp
extends LombokApp {
    private static final URI NO_CONFIG = URI.create("");
    private PrintStream out = System.out;
    private PrintStream err = System.err;
    private static final ConfigurationProblemReporter VOID = new ConfigurationProblemReporter(){

        @Override
        public void report(String sourceDescription, String problem, int lineNumber, CharSequence line) {
        }
    };

    @Override
    public String getAppName() {
        return "config";
    }

    @Override
    public String getAppDescription() {
        return "Prints the configurations for the provided paths to standard out.";
    }

    @Override
    public List<String> getAppAliases() {
        return Arrays.asList("configuration", "config", "conf", "settings");
    }

    @Override
    public int runApp(List<String> raw) throws Exception {
        CmdArgs args;
        CmdReader<CmdArgs> reader = CmdReader.of(CmdArgs.class);
        try {
            args = reader.make(raw.toArray(new String[0]));
            if (args.help) {
                this.out.println(reader.generateCommandLineHelp("java -jar lombok.jar configuration"));
                return 0;
            }
        }
        catch (InvalidCommandLineException e) {
            this.err.println(e.getMessage());
            this.err.println(reader.generateCommandLineHelp("java -jar lombok.jar configuration"));
            return 1;
        }
        ConfigurationKeysLoader.LoaderLoader.loadAllConfigurationKeys();
        Collection<ConfigurationKey<?>> keys = this.checkKeys(args.key);
        if (keys == null) {
            return 1;
        }
        boolean verbose = args.verbose;
        if (args.generate) {
            return this.generate(keys, verbose, !args.key.isEmpty());
        }
        return this.display(keys, verbose, args.paths, !args.key.isEmpty(), args.notMentioned);
    }

    public ConfigurationApp redirectOutput(PrintStream out, PrintStream err) {
        if (out != null) {
            this.out = out;
        }
        if (err != null) {
            this.err = err;
        }
        return this;
    }

    public int generate(Collection<ConfigurationKey<?>> keys, boolean verbose, boolean explicit) {
        for (ConfigurationKey<?> key : keys) {
            boolean hasDescription;
            if (!explicit && key.isHidden()) continue;
            String keyName = key.getKeyName();
            ConfigurationDataType type = key.getType();
            String description = key.getDescription();
            boolean bl = hasDescription = description != null && !description.isEmpty();
            if (!verbose) {
                this.out.println(keyName);
                if (hasDescription) {
                    this.out.print("  ");
                    this.out.println(description);
                }
                this.out.println();
                continue;
            }
            this.out.printf("##%n## Key : %s%n## Type: %s%n", keyName, type);
            if (hasDescription) {
                this.out.printf("##%n## %s%n", description);
            }
            this.out.printf("##%n## Examples:%n#%n", new Object[0]);
            this.out.printf("# clear %s%n", keyName);
            String exampleValue = type.getParser().exampleValue();
            if (type.isList()) {
                this.out.printf("# %s += %s%n", keyName, exampleValue);
                this.out.printf("# %s -= %s%n", keyName, exampleValue);
            } else {
                this.out.printf("# %s = %s%n", keyName, exampleValue);
            }
            this.out.printf("#%n%n", new Object[0]);
        }
        if (!verbose) {
            this.out.println("Use --verbose for more information.");
        }
        return 0;
    }

    public int display(Collection<ConfigurationKey<?>> keys, boolean verbose, Collection<String> argsPaths, boolean explicitKeys, boolean notMentioned) throws Exception {
        TreeMap<URI, Set<String>> sharedDirectories = this.findSharedDirectories(argsPaths);
        if (sharedDirectories == null) {
            return 1;
        }
        Set<String> none = sharedDirectories.remove(NO_CONFIG);
        if (none != null) {
            if (none.size() == 1) {
                this.out.printf("No 'lombok.config' found for '%s'.%n", none.iterator().next());
            } else {
                this.out.println("No 'lombok.config' found for: ");
                for (String path : none) {
                    this.out.printf("- %s%n", path);
                }
            }
        }
        final ArrayList problems = new ArrayList();
        ConfigurationProblemReporter reporter = new ConfigurationProblemReporter(){

            @Override
            public void report(String sourceDescription, String problem, int lineNumber, CharSequence line) {
                problems.add(String.format("%s: %s (%s:%d)", problem, line, sourceDescription, lineNumber));
            }
        };
        FileSystemSourceCache cache = new FileSystemSourceCache();
        ConfigurationParser parser = new ConfigurationParser(reporter);
        boolean first = true;
        for (Map.Entry<URI, Set<String>> entry : sharedDirectories.entrySet()) {
            Set<String> paths;
            if (!first) {
                this.out.printf("%n%n", new Object[0]);
            }
            if ((paths = entry.getValue()).size() == 1) {
                if (argsPaths.size() != 1) {
                    this.out.printf("Configuration for '%s'.%n%n", paths.iterator().next());
                }
            } else {
                this.out.printf("Configuration for:%n", new Object[0]);
                for (String path : paths) {
                    this.out.printf("- %s%n", path);
                }
                this.out.println();
            }
            URI directory = entry.getKey();
            BubblingConfigurationResolver resolver = new BubblingConfigurationResolver(cache.forUri(directory), cache.fileToSource(parser));
            Map<ConfigurationKey<?>, Collection<String>> traces = this.trace(keys, directory, notMentioned);
            boolean printed = false;
            for (ConfigurationKey<?> key : keys) {
                Object value = resolver.resolve(key);
                Collection<String> modifications = traces.get(key);
                if (modifications.isEmpty() && !explicitKeys) continue;
                if (printed && verbose) {
                    this.out.println();
                }
                this.printValue(key, value, verbose, modifications);
                printed = true;
            }
            if (!printed) {
                this.out.println("<default>");
            }
            first = false;
        }
        if (!problems.isEmpty()) {
            this.err.printf("Problems in the configuration files:%n", new Object[0]);
            for (String problem : problems) {
                this.err.printf("- %s%n", problem);
            }
            return 2;
        }
        return 0;
    }

    private void printValue(ConfigurationKey<?> key, Object value, boolean verbose, Collection<String> history) {
        if (verbose) {
            this.out.printf("# %s%n", key.getDescription());
        }
        if (value == null) {
            this.out.printf("clear %s%n", key.getKeyName());
        } else if (value instanceof List) {
            List list = (List)value;
            if (list.isEmpty()) {
                this.out.printf("clear %s%n", key.getKeyName());
            }
            for (Object element : list) {
                this.out.printf("%s += %s%n", key.getKeyName(), element);
            }
        } else {
            this.out.printf("%s = %s%n", key.getKeyName(), value);
        }
        if (!verbose) {
            return;
        }
        for (String modification : history) {
            this.out.printf("# %s%n", modification);
        }
    }

    private Map<ConfigurationKey<?>, ? extends Collection<String>> trace(Collection<ConfigurationKey<?>> keys, URI directory, boolean notMentioned) throws Exception {
        HashMap result = new HashMap();
        for (ConfigurationKey<?> key : keys) {
            result.put(key, new ArrayList());
        }
        HashSet used = new HashSet();
        boolean stopBubbling = false;
        HashSet<ConfigurationFile> visited = new HashSet<ConfigurationFile>();
        ConfigurationFile context = ConfigurationFile.forDirectory(new File(directory));
        while (context != null && !stopBubbling) {
            if (context.exists()) {
                ArrayDeque<Source> round = new ArrayDeque<Source>();
                round.push(new Source(context, context.description()));
                while (!round.isEmpty()) {
                    Source current = (Source)round.pop();
                    if (current == null || !visited.add(current.file) || !current.file.exists()) continue;
                    Map<ConfigurationKey<?>, List<String>> traces = this.trace(current.file, keys, round);
                    stopBubbling = this.stopBubbling(traces.get(ConfigurationKeys.STOP_BUBBLING));
                    for (ConfigurationKey<?> key : keys) {
                        List<String> modifications = traces.get(key);
                        if (modifications == null) {
                            modifications = new ArrayList<String>();
                            if (notMentioned) {
                                modifications.add("");
                                modifications.add(String.valueOf(current.description) + ":");
                                modifications.add("     <'" + key.getKeyName() + "' not mentioned>");
                            }
                        } else {
                            used.add(key);
                            modifications.add(0, String.valueOf(current.description) + ":");
                            modifications.add(0, "");
                        }
                        ((List)result.get(key)).addAll(0, modifications);
                    }
                }
            }
            context = context.parent();
        }
        for (ConfigurationKey<?> key : keys) {
            if (used.contains(key)) {
                List modifications = (List)result.get(key);
                modifications.remove(0);
                if (!stopBubbling) continue;
                String mostRecent = (String)modifications.get(0);
                modifications.set(0, String.valueOf(mostRecent.substring(0, mostRecent.length() - 1)) + " (stopped bubbling):");
                continue;
            }
            result.put(key, Collections.emptyList());
        }
        return result;
    }

    private Map<ConfigurationKey<?>, List<String>> trace(ConfigurationFile context, final Collection<ConfigurationKey<?>> keys, final Deque<Source> round) throws IOException {
        final HashMap result = new HashMap();
        ConfigurationParser.Collector collector = new ConfigurationParser.Collector(){

            @Override
            public void addImport(ConfigurationFile importFile, ConfigurationFile context, int lineNumber) {
                round.push(new Source(importFile, String.valueOf(importFile.description()) + " (imported from " + context.description() + ":" + lineNumber + ")"));
            }

            @Override
            public void clear(ConfigurationKey<?> key, ConfigurationFile context, int lineNumber) {
                this.trace(key, "clear " + key.getKeyName(), lineNumber);
            }

            @Override
            public void set(ConfigurationKey<?> key, Object value, ConfigurationFile context, int lineNumber) {
                this.trace(key, String.valueOf(key.getKeyName()) + " = " + value, lineNumber);
            }

            @Override
            public void add(ConfigurationKey<?> key, Object value, ConfigurationFile context, int lineNumber) {
                this.trace(key, String.valueOf(key.getKeyName()) + " += " + value, lineNumber);
            }

            @Override
            public void remove(ConfigurationKey<?> key, Object value, ConfigurationFile context, int lineNumber) {
                this.trace(key, String.valueOf(key.getKeyName()) + " -= " + value, lineNumber);
            }

            private void trace(ConfigurationKey<?> key, String message, int lineNumber) {
                if (!keys.contains(key) && key != ConfigurationKeys.STOP_BUBBLING) {
                    return;
                }
                ArrayList<String> traces = (ArrayList<String>)result.get(key);
                if (traces == null) {
                    traces = new ArrayList<String>();
                    result.put(key, traces);
                }
                traces.add(String.format("%4d: %s", lineNumber, message));
            }
        };
        new ConfigurationParser(VOID).parse(context, collector);
        return result;
    }

    private boolean stopBubbling(List<String> stops) {
        return stops != null && !stops.isEmpty() && stops.get(stops.size() - 1).endsWith("true");
    }

    private Collection<ConfigurationKey<?>> checkKeys(List<String> keyList) {
        Map<String, ConfigurationKey<?>> registeredKeys = ConfigurationKey.registeredKeys();
        if (keyList.isEmpty()) {
            return registeredKeys.values();
        }
        ArrayList keys = new ArrayList();
        for (String keyName : keyList) {
            ConfigurationKey<?> key = registeredKeys.get(keyName);
            if (key == null) {
                this.err.printf("Unknown key '%s'%n", keyName);
                return null;
            }
            keys.remove(key);
            keys.add(key);
        }
        return keys;
    }

    private TreeMap<URI, Set<String>> findSharedDirectories(Collection<String> paths) {
        TreeMap<URI, Set<String>> sharedDirectories = new TreeMap<URI, Set<String>>(new Comparator<URI>(){

            @Override
            public int compare(URI o1, URI o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (String path : paths) {
            File file = new File(path);
            if (!file.exists()) {
                this.err.printf("File not found: '%s'%n", path);
                return null;
            }
            URI first = this.findFirstLombokDirectory(file);
            Set<String> sharedBy = sharedDirectories.get(first);
            if (sharedBy == null) {
                sharedBy = new TreeSet<String>();
                sharedDirectories.put(first, sharedBy);
            }
            sharedBy.add(path);
        }
        return sharedDirectories;
    }

    private URI findFirstLombokDirectory(File file) {
        File current = new File(file.toURI().normalize());
        if (file.isFile()) {
            current = current.getParentFile();
        }
        while (current != null) {
            if (new File(current, "lombok.config").exists()) {
                return current.toURI();
            }
            current = current.getParentFile();
        }
        return NO_CONFIG;
    }

    public static class CmdArgs {
        @Sequential
        @Mandatory(onlyIfNot={"help", "generate"})
        @Description(value="Paths to java files or directories the configuration is to be printed for.")
        private List<String> paths = new ArrayList<String>();
        @Shorthand(value={"g"})
        @Excludes(value={"paths"})
        @Description(value="Generates a list containing all the available configuration parameters. Add --verbose to print more information.")
        boolean generate = false;
        @Shorthand(value={"v"})
        @Description(value="Displays more information.")
        boolean verbose = false;
        @Shorthand(value={"n"})
        @FullName(value="not-mentioned")
        @Requires(value={"verbose"})
        @Description(value="Also display files that don't mention the key.")
        boolean notMentioned = false;
        @Shorthand(value={"k"})
        @Description(value="Limit the result to these keys.")
        private List<String> key = new ArrayList<String>();
        @Shorthand(value={"h", "?"})
        @Description(value="Shows this help text.")
        boolean help = false;
    }

    private static final class Source {
        final ConfigurationFile file;
        final String description;

        Source(ConfigurationFile file, String description) {
            this.file = file;
            this.description = description;
        }
    }
}
