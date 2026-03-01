package cpw.mods.modlauncher;

import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;

public class ArgumentHandler {
    private String[] args;
    private OptionSet optionSet;
    private OptionSpec<String> profileOption;
    private OptionSpec<Path> gameDirOption;
    private OptionSpec<Path> assetsDirOption;
    private OptionSpec<Path> minecraftJarOption;
    private OptionSpec<String> nonOption;
    private OptionSpec<String> launchTarget;
    private OptionSpec<String> uuidOption;

    Path setArgs(String[] args) {
        this.args = args;
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<Path> gameDir = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING));
        parser.allowsUnrecognizedOptions();
        OptionSet optionSet = parser.parse(args);
        return optionSet.valueOf(gameDir);
    }

    void processArguments(Environment env, Consumer<OptionParser> parserConsumer, BiConsumer<OptionSet, BiFunction<String, OptionSet, ITransformationService.OptionResult>> resultConsumer) {
        OptionParser parser = new OptionParser();
        parser.allowsUnrecognizedOptions();
        this.profileOption = parser.accepts("version", "The version we launched with").withRequiredArg();
        this.gameDirOption = parser.accepts("gameDir", "Alternative game directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING));
        this.assetsDirOption = parser.accepts("assetsDir", "Assets directory").withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING));
        this.minecraftJarOption = parser.accepts("minecraftJar", "Path to minecraft jar").withRequiredArg().withValuesConvertedBy(new PathConverter(PathProperties.READABLE)).withValuesSeparatedBy(',');
        this.uuidOption = parser.accepts("uuid", "The UUID of the logging in player").withRequiredArg();
        this.launchTarget = parser.accepts("launchTarget", "LauncherService target to launch").withRequiredArg();
        parserConsumer.accept(parser);
        this.nonOption = parser.nonOptions();
        this.optionSet = parser.parse(this.args);
        env.computePropertyIfAbsent(IEnvironment.Keys.VERSION.get(), s -> this.optionSet.valueOf(this.profileOption));
        env.computePropertyIfAbsent(IEnvironment.Keys.GAMEDIR.get(), f -> this.optionSet.valueOf(this.gameDirOption));
        env.computePropertyIfAbsent(IEnvironment.Keys.ASSETSDIR.get(), f -> this.optionSet.valueOf(this.assetsDirOption));
        env.computePropertyIfAbsent(IEnvironment.Keys.LAUNCHTARGET.get(), f -> this.optionSet.valueOf(this.launchTarget));
        env.computePropertyIfAbsent(IEnvironment.Keys.UUID.get(), f -> this.optionSet.valueOf(this.uuidOption));
        resultConsumer.accept(this.optionSet, this::optionResults);
    }

    Path[] getSpecialJars() {
        return this.optionSet.valuesOf(this.minecraftJarOption).toArray(new Path[0]);
    }

    String getLaunchTarget() {
        return this.optionSet.valueOf(this.launchTarget);
    }

    private ITransformationService.OptionResult optionResults(final String serviceName, final OptionSet set) {
        return new ITransformationService.OptionResult(){

            @Override
            @Nonnull
            public <V> V value(OptionSpec<V> option) {
                this.checkOwnership(option);
                return set.valueOf(option);
            }

            @Override
            @Nonnull
            public <V> List<V> values(OptionSpec<V> option) {
                this.checkOwnership(option);
                return set.valuesOf(option);
            }

            private <V> void checkOwnership(OptionSpec<V> option) {
                if (!option.options().stream().allMatch(opt -> opt.startsWith(serviceName + ".") || !opt.contains("."))) {
                    throw new IllegalArgumentException("Cannot process non-arguments");
                }
            }
        };
    }

    public String[] buildArgumentList() {
        ArrayList<String> args = new ArrayList<String>();
        this.addOptionToString(this.profileOption, this.optionSet, args);
        this.addOptionToString(this.gameDirOption, this.optionSet, args);
        this.addOptionToString(this.assetsDirOption, this.optionSet, args);
        this.addOptionToString(this.uuidOption, this.optionSet, args);
        List<?> nonOptionList = this.optionSet.nonOptionArguments();
        nonOptionList.stream().map(Object::toString).forEach(args::add);
        return args.toArray(new String[0]);
    }

    private void addOptionToString(OptionSpec<?> option, OptionSet optionSet, List<String> appendTo) {
        if (optionSet.has(option)) {
            appendTo.add("--" + option.options().get(0));
            appendTo.add(option.value(optionSet).toString());
        }
    }
}
