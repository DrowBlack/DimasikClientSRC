package dimasik.itemics.api;

import dimasik.itemics.api.behavior.ILookBehavior;
import dimasik.itemics.api.behavior.IPathingBehavior;
import dimasik.itemics.api.cache.IWorldProvider;
import dimasik.itemics.api.command.manager.ICommandManager;
import dimasik.itemics.api.event.listener.IEventBus;
import dimasik.itemics.api.pathing.calc.IPathingControlManager;
import dimasik.itemics.api.process.IBuilderProcess;
import dimasik.itemics.api.process.ICustomGoalProcess;
import dimasik.itemics.api.process.IExploreProcess;
import dimasik.itemics.api.process.IFarmProcess;
import dimasik.itemics.api.process.IFollowProcess;
import dimasik.itemics.api.process.IGetToBlockProcess;
import dimasik.itemics.api.process.IMineProcess;
import dimasik.itemics.api.selection.ISelectionManager;
import dimasik.itemics.api.utils.IInputOverrideHandler;
import dimasik.itemics.api.utils.IPlayerContext;

public interface IItemics {
    public IPathingBehavior getPathingBehavior();

    public ILookBehavior getLookBehavior();

    public IFollowProcess getFollowProcess();

    public IMineProcess getMineProcess();

    public IBuilderProcess getBuilderProcess();

    public IExploreProcess getExploreProcess();

    public IFarmProcess getFarmProcess();

    public ICustomGoalProcess getCustomGoalProcess();

    public IGetToBlockProcess getGetToBlockProcess();

    public IWorldProvider getWorldProvider();

    public IPathingControlManager getPathingControlManager();

    public IInputOverrideHandler getInputOverrideHandler();

    public IPlayerContext getPlayerContext();

    public IEventBus getGameEventHandler();

    public ISelectionManager getSelectionManager();

    public ICommandManager getCommandManager();

    public void openClick();
}
