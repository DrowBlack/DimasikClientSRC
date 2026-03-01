package dimasik.itemics.command.defaults;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.cache.IWaypoint;
import dimasik.itemics.api.cache.IWaypointCollection;
import dimasik.itemics.api.cache.IWorldData;
import dimasik.itemics.api.cache.Waypoint;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.IItemicsChatControl;
import dimasik.itemics.api.command.argument.IArgConsumer;
import dimasik.itemics.api.command.datatypes.ForWaypoints;
import dimasik.itemics.api.command.datatypes.RelativeBlockPos;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.exception.CommandInvalidStateException;
import dimasik.itemics.api.command.exception.CommandInvalidTypeException;
import dimasik.itemics.api.command.helpers.Paginator;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import dimasik.itemics.api.pathing.goals.GoalBlock;
import dimasik.itemics.api.utils.BetterBlockPos;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class WaypointsCommand
extends Command {
    private Map<IWorldData, List<IWaypoint>> deletedWaypoints = new HashMap<IWorldData, List<IWaypoint>>();

    public WaypointsCommand(IItemics itemics) {
        super(itemics, "waypoints", "waypoint", "wp");
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        v0 = action = args.hasAny() != false ? Action.getByName(args.getString()) : Action.LIST;
        if (action == null) {
            throw new CommandInvalidTypeException(args.consumed(), "an action");
        }
        toComponent = (BiFunction<IWaypoint, Action, ITextComponent>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;, lambda$execute$0(java.lang.String dimasik.itemics.api.cache.IWaypoint dimasik.itemics.command.defaults.WaypointsCommand$Action ), (Ldimasik/itemics/api/cache/IWaypoint;Ldimasik/itemics/command/defaults/WaypointsCommand$Action;)Lnet/minecraft/util/text/ITextComponent;)((String)label);
        transform = (Function<IWaypoint, ITextComponent>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$execute$1(java.util.function.BiFunction dimasik.itemics.command.defaults.WaypointsCommand$Action dimasik.itemics.api.cache.IWaypoint ), (Ldimasik/itemics/api/cache/IWaypoint;)Lnet/minecraft/util/text/ITextComponent;)(toComponent, (Action)action);
        if (action != Action.LIST) ** GOTO lbl18
        v1 = tag = args.hasAny() != false ? IWaypoint.Tag.getByName(args.peekString()) : null;
        if (tag != null) {
            args.get();
        }
        v2 = waypoints = tag != null ? ForWaypoints.getWaypointsByTag(this.itemics, tag) : ForWaypoints.getWaypoints(this.itemics);
        if (waypoints.length > 0) {
            args.requireMax(1);
            Paginator.paginate(args, waypoints, (Runnable)LambdaMetafactory.metafactory(null, null, null, ()V, lambda$execute$2(dimasik.itemics.api.cache.IWaypoint$Tag ), ()V)((WaypointsCommand)this, (IWaypoint.Tag)tag), transform, String.format("%s%s %s%s", new Object[]{IItemicsChatControl.FORCE_COMMAND_PREFIX, label, action.names[0], tag != null ? " " + tag.getName() : ""}));
        } else {
            args.requireMax(0);
            throw new CommandInvalidStateException(tag != null ? "No waypoints found by that tag" : "No waypoints found");
lbl18:
            // 1 sources

            if (action == Action.SAVE) {
                v3 = tag = args.hasAny() != false ? IWaypoint.Tag.getByName(args.peekString()) : null;
                if (tag == null) {
                    tag = IWaypoint.Tag.USER;
                } else {
                    args.get();
                }
                name = args.hasExactlyOne() != false || args.hasExactly(4) != false ? args.getString() : "";
                pos = args.hasAny() != false ? (BetterBlockPos)args.getDatatypePost(RelativeBlockPos.INSTANCE, this.ctx.playerFeet()) : this.ctx.playerFeet();
                args.requireMax(0);
                waypoint = new Waypoint(name, tag, pos);
                ForWaypoints.waypoints(this.itemics).addWaypoint(waypoint);
                component = new StringTextComponent("Waypoint added: ");
                component.setStyle(component.getStyle().setFormatting(TextFormatting.GRAY));
                component.append(toComponent.apply(waypoint, Action.INFO));
                this.logDirect(new ITextComponent[]{component});
            } else if (action == Action.CLEAR) {
                args.requireMax(1);
                tag = IWaypoint.Tag.getByName(args.getString());
                for (IWaypoint waypoint : waypoints = ForWaypoints.getWaypointsByTag(this.itemics, tag)) {
                    ForWaypoints.waypoints(this.itemics).removeWaypoint(waypoint);
                }
                this.deletedWaypoints.computeIfAbsent(this.itemics.getWorldProvider().getCurrentWorld(), (Function<IWorldData, List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$execute$3(dimasik.itemics.api.cache.IWorldData ), (Ldimasik/itemics/api/cache/IWorldData;)Ljava/util/List;)()).addAll(Arrays.asList(waypoints));
                textComponent = new StringTextComponent(String.format("Cleared %d waypoints, click to restore them", new Object[]{waypoints.length}));
                textComponent.setStyle(textComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%s%s restore @ %s", new Object[]{IItemicsChatControl.FORCE_COMMAND_PREFIX, label, Stream.of(waypoints).map((Function<IWaypoint, String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$execute$4(dimasik.itemics.api.cache.IWaypoint ), (Ldimasik/itemics/api/cache/IWaypoint;)Ljava/lang/String;)()).collect(Collectors.joining(" "))}))));
                this.logDirect(new ITextComponent[]{textComponent});
            } else if (action == Action.RESTORE) {
                waypoints = new ArrayList<E>();
                deletedWaypoints = this.deletedWaypoints.getOrDefault(this.itemics.getWorldProvider().getCurrentWorld(), Collections.emptyList());
                if (args.peekString().equals("@")) {
                    args.get();
                    block5: while (args.hasAny()) {
                        timestamp = args.getAs(Long.class);
                        for (IWaypoint waypoint : deletedWaypoints) {
                            if (waypoint.getCreationTimestamp() != timestamp) continue;
                            waypoints.add(waypoint);
                            continue block5;
                        }
                    }
                } else {
                    args.requireExactly(1);
                    size = deletedWaypoints.size();
                    amount = Math.min(size, args.getAs(Integer.class));
                    waypoints = new ArrayList<T>(deletedWaypoints.subList(size - amount, size));
                }
                waypoints.forEach((Consumer<IWaypoint>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)V, addWaypoint(dimasik.itemics.api.cache.IWaypoint ), (Ldimasik/itemics/api/cache/IWaypoint;)V)((IWaypointCollection)ForWaypoints.waypoints(this.itemics)));
                deletedWaypoints.removeIf((Predicate<IWaypoint>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, contains(java.lang.Object ), (Ldimasik/itemics/api/cache/IWaypoint;)Z)(waypoints));
                this.logDirect(String.format("Restored %d waypoints", new Object[]{waypoints.size()}));
            } else {
                waypoints = (IWaypoint[])args.getDatatypeFor(ForWaypoints.INSTANCE);
                waypoint = null;
                if (args.hasAny() && args.peekString().equals("@")) {
                    args.requireExactly(2);
                    args.get();
                    timestamp = args.getAs(Long.class);
                    for (IWaypoint iWaypoint : waypoints) {
                        if (iWaypoint.getCreationTimestamp() != timestamp) continue;
                        waypoint = iWaypoint;
                        break;
                    }
                    if (waypoint == null) {
                        throw new CommandInvalidStateException("Timestamp was specified but no waypoint was found");
                    }
                } else {
                    switch (waypoints.length) {
                        case 0: {
                            throw new CommandInvalidStateException("No waypoints found");
                        }
                        case 1: {
                            waypoint = waypoints[0];
                            break;
                        }
                    }
                }
                if (waypoint == null) {
                    args.requireMax(1);
                    Paginator.paginate(args, waypoints, (Runnable)LambdaMetafactory.metafactory(null, null, null, ()V, lambda$execute$5(), ()V)((WaypointsCommand)this), transform, String.format("%s%s %s %s", new Object[]{IItemicsChatControl.FORCE_COMMAND_PREFIX, label, action.names[0], args.consumedString()}));
                } else if (action == Action.INFO) {
                    this.logDirect(new ITextComponent[]{transform.apply(waypoint)});
                    this.logDirect(String.format("Position: %s", new Object[]{waypoint.getLocation()}));
                    deleteComponent = new StringTextComponent("Click to delete this waypoint");
                    deleteComponent.setStyle(deleteComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%s%s delete %s @ %d", new Object[]{IItemicsChatControl.FORCE_COMMAND_PREFIX, label, waypoint.getTag().getName(), waypoint.getCreationTimestamp()}))));
                    goalComponent = new StringTextComponent("Click to set goal to this waypoint");
                    goalComponent.setStyle(goalComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%s%s goal %s @ %d", new Object[]{IItemicsChatControl.FORCE_COMMAND_PREFIX, label, waypoint.getTag().getName(), waypoint.getCreationTimestamp()}))));
                    recreateComponent = new StringTextComponent("Click to show a command to recreate this waypoint");
                    recreateComponent.setStyle(recreateComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format("%s%s save %s %s %s %s %s", new Object[]{Itemics.settings().prefix.value, label, waypoint.getTag().getName(), waypoint.getName(), waypoint.getLocation().x, waypoint.getLocation().y, waypoint.getLocation().z}))));
                    backComponent = new StringTextComponent("Click to return to the waypoints list");
                    backComponent.setStyle(backComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%s%s list", new Object[]{IItemicsChatControl.FORCE_COMMAND_PREFIX, label}))));
                    this.logDirect(new ITextComponent[]{deleteComponent});
                    this.logDirect(new ITextComponent[]{goalComponent});
                    this.logDirect(new ITextComponent[]{recreateComponent});
                    this.logDirect(new ITextComponent[]{backComponent});
                } else if (action == Action.DELETE) {
                    ForWaypoints.waypoints(this.itemics).removeWaypoint(waypoint);
                    this.deletedWaypoints.computeIfAbsent(this.itemics.getWorldProvider().getCurrentWorld(), (Function<IWorldData, List>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$execute$6(dimasik.itemics.api.cache.IWorldData ), (Ldimasik/itemics/api/cache/IWorldData;)Ljava/util/List;)()).add(waypoint);
                    textComponent = new StringTextComponent("That waypoint has successfully been deleted, click to restore it");
                    textComponent.setStyle(textComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%s%s restore @ %s", new Object[]{IItemicsChatControl.FORCE_COMMAND_PREFIX, label, waypoint.getCreationTimestamp()}))));
                    this.logDirect(new ITextComponent[]{textComponent});
                } else if (action == Action.GOAL) {
                    goal = new GoalBlock(waypoint.getLocation());
                    this.itemics.getCustomGoalProcess().setGoal(goal);
                    this.logDirect(String.format("Goal: %s", new Object[]{goal}));
                } else if (action == Action.GOTO) {
                    goal = new GoalBlock(waypoint.getLocation());
                    this.itemics.getCustomGoalProcess().setGoalAndPath(goal);
                    this.logDirect(String.format("Going to: %s", new Object[]{goal}));
                }
            }
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            if (args.hasExactlyOne()) {
                return new TabCompleteHelper().append(Action.getAllNames()).sortAlphabetically().filterPrefix(args.getString()).stream();
            }
            Action action = Action.getByName(args.getString());
            if (args.hasExactlyOne()) {
                if (action == Action.LIST || action == Action.SAVE || action == Action.CLEAR) {
                    return new TabCompleteHelper().append(IWaypoint.Tag.getAllNames()).sortAlphabetically().filterPrefix(args.getString()).stream();
                }
                if (action == Action.RESTORE) {
                    return Stream.empty();
                }
                return args.tabCompleteDatatype(ForWaypoints.INSTANCE);
            }
            if (args.has(3) && action == Action.SAVE) {
                args.get();
                args.get();
                return args.tabCompleteDatatype(RelativeBlockPos.INSTANCE);
            }
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Manage waypoints";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("The waypoint command allows you to manage Itemics's waypoints.", "", "Waypoints can be used to mark positions for later. Waypoints are each given a tag and an optional name.", "", "Note that the info, delete, and goal commands let you specify a waypoint by tag. If there is more than one waypoint with a certain tag, then they will let you select which waypoint you mean.", "", "Missing arguments for the save command use the USER tag, creating an unnamed waypoint and your current position as defaults.", "", "Usage:", "> wp [l/list] - List all waypoints.", "> wp <l/list> <tag> - List all waypoints by tag.", "> wp <s/save> - Save an unnamed USER waypoint at your current position", "> wp <s/save> [tag] [name] [pos] - Save a waypoint with the specified tag, name and position.", "> wp <i/info/show> <tag/name> - Show info on a waypoint by tag or name.", "> wp <d/delete> <tag/name> - Delete a waypoint by tag or name.", "> wp <restore> <n> - Restore the last n deleted waypoints.", "> wp <c/clear> <tag> - Delete all waypoints with the specified tag.", "> wp <g/goal> <tag/name> - Set a goal to a waypoint by tag or name.", "> wp <goto> <tag/name> - Set a goal to a waypoint by tag or name and start pathing.");
    }

    private static /* synthetic */ List lambda$execute$6(IWorldData k) {
        return new ArrayList();
    }

    private /* synthetic */ void lambda$execute$5() {
        this.logDirect("Multiple waypoints were found:");
    }

    private static /* synthetic */ String lambda$execute$4(IWaypoint wp) {
        return Long.toString(wp.getCreationTimestamp());
    }

    private static /* synthetic */ List lambda$execute$3(IWorldData k) {
        return new ArrayList();
    }

    private /* synthetic */ void lambda$execute$2(IWaypoint.Tag tag) {
        this.logDirect(tag != null ? String.format("All waypoints by tag %s:", tag.name()) : "All waypoints:");
    }

    private static /* synthetic */ ITextComponent lambda$execute$1(BiFunction toComponent, Action action, IWaypoint waypoint) {
        return (ITextComponent)toComponent.apply(waypoint, action == Action.LIST ? Action.INFO : action);
    }

    private static /* synthetic */ ITextComponent lambda$execute$0(String label, IWaypoint waypoint, Action _action) {
        StringTextComponent component = new StringTextComponent("");
        StringTextComponent tagComponent = new StringTextComponent(waypoint.getTag().name() + " ");
        tagComponent.setStyle(tagComponent.getStyle().setFormatting(TextFormatting.GRAY));
        String name = waypoint.getName();
        StringTextComponent nameComponent = new StringTextComponent(!name.isEmpty() ? name : "<empty>");
        nameComponent.setStyle(nameComponent.getStyle().setFormatting(!name.isEmpty() ? TextFormatting.GRAY : TextFormatting.DARK_GRAY));
        StringTextComponent timestamp = new StringTextComponent(" @ " + String.valueOf(new Date(waypoint.getCreationTimestamp())));
        timestamp.setStyle(timestamp.getStyle().setFormatting(TextFormatting.DARK_GRAY));
        component.append(tagComponent);
        component.append(nameComponent);
        component.append(timestamp);
        component.setStyle(component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to select"))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%s%s %s %s @ %d", IItemicsChatControl.FORCE_COMMAND_PREFIX, label, _action.names[0], waypoint.getTag().getName(), waypoint.getCreationTimestamp()))));
        return component;
    }

    private static enum Action {
        LIST("list", "get", "l"),
        CLEAR("clear", "c"),
        SAVE("save", "s"),
        INFO("info", "show", "i"),
        DELETE("delete", "d"),
        RESTORE("restore"),
        GOAL("goal", "g"),
        GOTO("goto");

        private final String[] names;

        private Action(String ... names) {
            this.names = names;
        }

        public static Action getByName(String name) {
            for (Action action : Action.values()) {
                for (String alias : action.names) {
                    if (!alias.equalsIgnoreCase(name)) continue;
                    return action;
                }
            }
            return null;
        }

        public static String[] getAllNames() {
            HashSet<String> names = new HashSet<String>();
            for (Action action : Action.values()) {
                names.addAll(Arrays.asList(action.names));
            }
            return names.toArray(new String[0]);
        }
    }
}
