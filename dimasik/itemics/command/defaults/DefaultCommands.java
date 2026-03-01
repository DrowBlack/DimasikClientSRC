package dimasik.itemics.command.defaults;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.command.Command;
import dimasik.itemics.api.command.ICommand;
import dimasik.itemics.command.defaults.AxisCommand;
import dimasik.itemics.command.defaults.BlacklistCommand;
import dimasik.itemics.command.defaults.BuildCommand;
import dimasik.itemics.command.defaults.ClickCommand;
import dimasik.itemics.command.defaults.ComeCommand;
import dimasik.itemics.command.defaults.CommandAlias;
import dimasik.itemics.command.defaults.ETACommand;
import dimasik.itemics.command.defaults.ExecutionControlCommands;
import dimasik.itemics.command.defaults.ExploreCommand;
import dimasik.itemics.command.defaults.ExploreFilterCommand;
import dimasik.itemics.command.defaults.FarmCommand;
import dimasik.itemics.command.defaults.FindCommand;
import dimasik.itemics.command.defaults.FollowCommand;
import dimasik.itemics.command.defaults.ForceCancelCommand;
import dimasik.itemics.command.defaults.GcCommand;
import dimasik.itemics.command.defaults.GoalCommand;
import dimasik.itemics.command.defaults.GotoCommand;
import dimasik.itemics.command.defaults.HelpCommand;
import dimasik.itemics.command.defaults.InvertCommand;
import dimasik.itemics.command.defaults.LitematicaCommand;
import dimasik.itemics.command.defaults.MineCommand;
import dimasik.itemics.command.defaults.PathCommand;
import dimasik.itemics.command.defaults.ProcCommand;
import dimasik.itemics.command.defaults.ReloadAllCommand;
import dimasik.itemics.command.defaults.RenderCommand;
import dimasik.itemics.command.defaults.RepackCommand;
import dimasik.itemics.command.defaults.SaveAllCommand;
import dimasik.itemics.command.defaults.SelCommand;
import dimasik.itemics.command.defaults.SetCommand;
import dimasik.itemics.command.defaults.SurfaceCommand;
import dimasik.itemics.command.defaults.ThisWayCommand;
import dimasik.itemics.command.defaults.TunnelCommand;
import dimasik.itemics.command.defaults.VersionCommand;
import dimasik.itemics.command.defaults.WaypointsCommand;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DefaultCommands {
    private DefaultCommands() {
    }

    public static List<ICommand> createAll(IItemics itemics) {
        Objects.requireNonNull(itemics);
        ArrayList<Command> commands = new ArrayList<Command>(Arrays.asList(new HelpCommand(itemics), new SetCommand(itemics), new CommandAlias(itemics, Arrays.asList("modified", "mod", "itemics", "modifiedsettings"), "List modified settings", "set modified"), new CommandAlias(itemics, "reset", "Reset all settings or just one", "set reset"), new GoalCommand(itemics), new GotoCommand(itemics), new PathCommand(itemics), new ProcCommand(itemics), new ETACommand(itemics), new VersionCommand(itemics), new RepackCommand(itemics), new BuildCommand(itemics), new LitematicaCommand(itemics), new ComeCommand(itemics), new AxisCommand(itemics), new ForceCancelCommand(itemics), new GcCommand(itemics), new InvertCommand(itemics), new TunnelCommand(itemics), new RenderCommand(itemics), new FarmCommand(itemics), new FollowCommand(itemics), new ExploreFilterCommand(itemics), new ReloadAllCommand(itemics), new SaveAllCommand(itemics), new ExploreCommand(itemics), new BlacklistCommand(itemics), new FindCommand(itemics), new MineCommand(itemics), new ClickCommand(itemics), new SurfaceCommand(itemics), new ThisWayCommand(itemics), new WaypointsCommand(itemics), new CommandAlias(itemics, "sethome", "Sets your home waypoint", "waypoints save home"), new CommandAlias(itemics, "home", "Path to your home waypoint", "waypoints goto home"), new SelCommand(itemics)));
        ExecutionControlCommands prc = new ExecutionControlCommands(itemics);
        commands.add(prc.pauseCommand);
        commands.add(prc.resumeCommand);
        commands.add(prc.pausedCommand);
        commands.add(prc.cancelCommand);
        return Collections.unmodifiableList(commands);
    }
}
