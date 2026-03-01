package dimasik.itemics.api.command.datatypes;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.cache.IWaypoint;
import dimasik.itemics.api.cache.IWaypointCollection;
import dimasik.itemics.api.command.datatypes.IDatatypeContext;
import dimasik.itemics.api.command.datatypes.IDatatypeFor;
import dimasik.itemics.api.command.exception.CommandException;
import dimasik.itemics.api.command.helpers.TabCompleteHelper;
import java.util.Comparator;
import java.util.stream.Stream;

public enum ForWaypoints implements IDatatypeFor<IWaypoint[]>
{
    INSTANCE;


    @Override
    public IWaypoint[] get(IDatatypeContext ctx) throws CommandException {
        String input = ctx.getConsumer().getString();
        IWaypoint.Tag tag = IWaypoint.Tag.getByName(input);
        return tag == null ? ForWaypoints.getWaypointsByName(ctx.getItemics(), input) : ForWaypoints.getWaypointsByTag(ctx.getItemics(), tag);
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper().append(ForWaypoints.getWaypointNames(ctx.getItemics())).sortAlphabetically().prepend(IWaypoint.Tag.getAllNames()).filterPrefix(ctx.getConsumer().getString()).stream();
    }

    public static IWaypointCollection waypoints(IItemics itemics) {
        return itemics.getWorldProvider().getCurrentWorld().getWaypoints();
    }

    public static IWaypoint[] getWaypoints(IItemics itemics) {
        return (IWaypoint[])ForWaypoints.waypoints(itemics).getAllWaypoints().stream().sorted(Comparator.comparingLong(IWaypoint::getCreationTimestamp).reversed()).toArray(IWaypoint[]::new);
    }

    public static String[] getWaypointNames(IItemics itemics) {
        return (String[])Stream.of(ForWaypoints.getWaypoints(itemics)).map(IWaypoint::getName).filter(name -> !name.isEmpty()).toArray(String[]::new);
    }

    public static IWaypoint[] getWaypointsByTag(IItemics itemics, IWaypoint.Tag tag) {
        return (IWaypoint[])ForWaypoints.waypoints(itemics).getByTag(tag).stream().sorted(Comparator.comparingLong(IWaypoint::getCreationTimestamp).reversed()).toArray(IWaypoint[]::new);
    }

    public static IWaypoint[] getWaypointsByName(IItemics itemics, String name) {
        return (IWaypoint[])Stream.of(ForWaypoints.getWaypoints(itemics)).filter(waypoint -> waypoint.getName().equalsIgnoreCase(name)).toArray(IWaypoint[]::new);
    }
}
