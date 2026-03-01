package dimasik.itemics.command.defaults;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.Settings;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.IItemicsChatControl;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.exception.CommandInvalidStateException;
import dimasik.itemics.api.command.exception.CommandInvalidTypeException;
import dimasik.itemics.api.command.helpers.Paginator;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import dimasik.itemics.api.utils.SettingsUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class SetCommand
extends Command {
    public SetCommand(IItemics itemics) {
        super(itemics, "set", "setting", "settings");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        boolean doingSomething;
        boolean paginate;
        String arg;
        String string = arg = args.hasAny() ? args.getString().toLowerCase(Locale.US) : "list";
        if (Arrays.asList("s", "save").contains(arg)) {
            SettingsUtil.save(Itemics.settings());
            this.logDirect("Settings saved");
            return;
        }
        boolean viewModified = Arrays.asList("m", "mod", "modified").contains(arg);
        boolean viewAll = Arrays.asList("all", "l", "list").contains(arg);
        boolean bl = paginate = viewModified || viewAll;
        if (paginate) {
            String search = args.hasAny() && args.peekAsOrNull(Integer.class) == null ? args.getString() : "";
            args.requireMax(1);
            List toPaginate = (viewModified ? SettingsUtil.modifiedSettings(Itemics.settings()) : Itemics.settings().allSettings).stream().filter(s -> !SettingsUtil.javaOnlySetting(s)).filter(s -> s.getName().toLowerCase(Locale.US).contains(search.toLowerCase(Locale.US))).sorted((s1, s2) -> String.CASE_INSENSITIVE_ORDER.compare(s1.getName(), s2.getName())).collect(Collectors.toList());
            Paginator.paginate(args, new Paginator(toPaginate), () -> this.logDirect(!search.isEmpty() ? String.format("All %ssettings containing the string '%s':", viewModified ? "modified " : "", search) : String.format("All %ssettings:", viewModified ? "modified " : "")), setting -> {
                StringTextComponent typeComponent = new StringTextComponent(String.format(" (%s)", SettingsUtil.settingTypeToString(setting)));
                typeComponent.setStyle(typeComponent.getStyle().setFormatting(TextFormatting.DARK_GRAY));
                StringTextComponent hoverComponent = new StringTextComponent("");
                hoverComponent.setStyle(hoverComponent.getStyle().setFormatting(TextFormatting.GRAY));
                hoverComponent.appendString(setting.getName());
                hoverComponent.appendString(String.format("\nType: %s", SettingsUtil.settingTypeToString(setting)));
                hoverComponent.appendString(String.format("\n\nValue:\n%s", SettingsUtil.settingValueToString(setting)));
                hoverComponent.appendString(String.format("\n\nDefault Value:\n%s", SettingsUtil.settingDefaultToString(setting)));
                String commandSuggestion = (String)Itemics.settings().prefix.value + String.format("set %s ", setting.getName());
                StringTextComponent component = new StringTextComponent(setting.getName());
                component.setStyle(component.getStyle().setFormatting(TextFormatting.GRAY));
                component.append(typeComponent);
                component.setStyle(component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent)).setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, commandSuggestion)));
                return component;
            }, IItemicsChatControl.FORCE_COMMAND_PREFIX + "set " + arg + " " + search);
            return;
        }
        args.requireMax(1);
        boolean resetting = arg.equalsIgnoreCase("reset");
        boolean toggling = arg.equalsIgnoreCase("toggle");
        boolean bl2 = doingSomething = resetting || toggling;
        if (resetting) {
            if (!args.hasAny()) {
                this.logDirect("Please specify 'all' as an argument to reset to confirm you'd really like to do this");
                this.logDirect("ALL settings will be reset. Use the 'set modified' or 'modified' commands to see what will be reset.");
                this.logDirect("Specify a setting name instead of 'all' to only reset one setting");
            } else if (args.peekString().equalsIgnoreCase("all")) {
                SettingsUtil.modifiedSettings(Itemics.settings()).forEach(Settings.Setting::reset);
                this.logDirect("All settings have been reset to their default values");
                SettingsUtil.save(Itemics.settings());
                return;
            }
        }
        if (toggling) {
            args.requireMin(1);
        }
        String settingName = doingSomething ? args.getString() : arg;
        Settings.Setting setting2 = Itemics.settings().allSettings.stream().filter(s -> s.getName().equalsIgnoreCase(settingName)).findFirst().orElse(null);
        if (setting2 == null) {
            throw new CommandInvalidTypeException(args.consumed(), "a valid setting");
        }
        if (SettingsUtil.javaOnlySetting(setting2)) {
            throw new CommandInvalidStateException(String.format("Setting %s can only be used via the api.", setting2.getName()));
        }
        if (!doingSomething && !args.hasAny()) {
            this.logDirect(String.format("Value of setting %s:", setting2.getName()));
            this.logDirect(SettingsUtil.settingValueToString(setting2));
        } else {
            String oldValue = SettingsUtil.settingValueToString(setting2);
            if (resetting) {
                setting2.reset();
            } else if (toggling) {
                Settings.Setting asBoolSetting;
                if (setting2.getValueClass() != Boolean.class) {
                    throw new CommandInvalidTypeException(args.consumed(), "a toggleable setting", "some other setting");
                }
                Settings.Setting setting3 = asBoolSetting = setting2;
                setting3.value = (Boolean)setting3.value ^ true;
                this.logDirect(String.format("Toggled setting %s to %s", setting2.getName(), Boolean.toString((Boolean)setting2.value)));
            } else {
                String newValue = args.getString();
                try {
                    SettingsUtil.parseAndApply(Itemics.settings(), arg, newValue);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                    throw new CommandInvalidTypeException(args.consumed(), "a valid value", t);
                }
            }
            if (!toggling) {
                this.logDirect(String.format("Successfully %s %s to %s", resetting ? "reset" : "set", setting2.getName(), SettingsUtil.settingValueToString(setting2)));
            }
            StringTextComponent oldValueComponent = new StringTextComponent(String.format("Old value: %s", oldValue));
            oldValueComponent.setStyle(oldValueComponent.getStyle().setFormatting(TextFormatting.GRAY).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to set the setting back to this value"))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, IItemicsChatControl.FORCE_COMMAND_PREFIX + String.format("set %s %s", setting2.getName(), oldValue))));
            this.logDirect(oldValueComponent);
            if (setting2.getName().equals("chatControl") && !((Boolean)setting2.value).booleanValue() && !((Boolean)Itemics.settings().chatControlAnyway.value).booleanValue() || setting2.getName().equals("chatControlAnyway") && !((Boolean)setting2.value).booleanValue() && !((Boolean)Itemics.settings().chatControl.value).booleanValue()) {
                this.logDirect("Warning: Chat commands will no longer work. If you want to revert this change, use prefix control (if enabled) or click the old value listed above.", TextFormatting.RED);
            } else if (setting2.getName().equals("prefixControl") && !((Boolean)setting2.value).booleanValue()) {
                this.logDirect("Warning: Prefixed commands will no longer work. If you want to revert this change, use chat control (if enabled) or click the old value listed above.", TextFormatting.RED);
            }
        }
        SettingsUtil.save(Itemics.settings());
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            String arg = args.getString();
            if (args.hasExactlyOne() && !Arrays.asList("s", "save").contains(args.peekString().toLowerCase(Locale.US))) {
                if (arg.equalsIgnoreCase("reset")) {
                    return new TabCompleteHelper().addModifiedSettings().prepend("all").filterPrefix(args.getString()).stream();
                }
                if (arg.equalsIgnoreCase("toggle")) {
                    return new TabCompleteHelper().addToggleableSettings().filterPrefix(args.getString()).stream();
                }
                Settings.Setting<?> setting = Itemics.settings().byLowerName.get(arg.toLowerCase(Locale.US));
                if (setting != null) {
                    if (setting.getType() == Boolean.class) {
                        TabCompleteHelper helper = new TabCompleteHelper();
                        if (((Boolean)setting.value).booleanValue()) {
                            helper.append("true", "false");
                        } else {
                            helper.append("false", "true");
                        }
                        return helper.filterPrefix(args.getString()).stream();
                    }
                    return Stream.of(SettingsUtil.settingValueToString(setting));
                }
            } else if (!args.hasAny()) {
                return new TabCompleteHelper().addSettings().sortAlphabetically().prepend("list", "modified", "reset", "toggle", "save").filterPrefix(arg).stream();
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "View or change settings";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Using the set command, you can manage all of Itemics's settings. Almost every aspect is controlled by these settings - go wild!", "", "Usage:", "> set - Same as `set list`", "> set list [page] - View all settings", "> set modified [page] - View modified settings", "> set <setting> - View the current value of a setting", "> set <setting> <value> - Set the value of a setting", "> set reset all - Reset ALL SETTINGS to their defaults", "> set reset <setting> - Reset a setting to its default", "> set toggle <setting> - Toggle a boolean setting", "> set save - Save all settings (this is automatic tho)");
    }
}
