package dimasik.itemics.api.pathing.calc;

import dimasik.itemics.api.process.IItemicsProcess;
import dimasik.itemics.api.process.PathingCommand;
import java.util.Optional;

public interface IPathingControlManager {
    public void registerProcess(IItemicsProcess var1);

    public Optional<IItemicsProcess> mostRecentInControl();

    public Optional<PathingCommand> mostRecentCommand();
}
