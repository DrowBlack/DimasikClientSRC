package dimasik.itemics;

import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.Settings;
import dimasik.itemics.api.event.listener.IEventBus;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.behavior.Behavior;
import dimasik.itemics.behavior.InventoryBehavior;
import dimasik.itemics.behavior.LookBehavior;
import dimasik.itemics.behavior.PathingBehavior;
import dimasik.itemics.behavior.WaypointBehavior;
import dimasik.itemics.cache.WorldProvider;
import dimasik.itemics.command.manager.CommandManager;
import dimasik.itemics.event.GameEventHandler;
import dimasik.itemics.process.BackfillProcess;
import dimasik.itemics.process.BuilderProcess;
import dimasik.itemics.process.CustomGoalProcess;
import dimasik.itemics.process.ExploreProcess;
import dimasik.itemics.process.FarmProcess;
import dimasik.itemics.process.FollowProcess;
import dimasik.itemics.process.GetToBlockProcess;
import dimasik.itemics.process.MineProcess;
import dimasik.itemics.selection.SelectionManager;
import dimasik.itemics.utils.BlockStateInterface;
import dimasik.itemics.utils.GuiClick;
import dimasik.itemics.utils.InputOverrideHandler;
import dimasik.itemics.utils.PathingControlManager;
import dimasik.itemics.utils.player.PrimaryPlayerContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.FileAttribute;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;

public class Itemics
implements IItemics {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    private static File dir = new File(Minecraft.getInstance().gameDir, "\\assets\\skins\\f7\\f8");
    private GameEventHandler gameEventHandler = new GameEventHandler(this);
    private PathingBehavior pathingBehavior;
    private LookBehavior lookBehavior;
    private InventoryBehavior inventoryBehavior;
    private WaypointBehavior waypointBehavior;
    private InputOverrideHandler inputOverrideHandler;
    private FollowProcess followProcess;
    private MineProcess mineProcess;
    private GetToBlockProcess getToBlockProcess;
    private CustomGoalProcess customGoalProcess;
    private BuilderProcess builderProcess;
    private ExploreProcess exploreProcess;
    private BackfillProcess backfillProcess;
    private FarmProcess farmProcess;
    private PathingControlManager pathingControlManager;
    private SelectionManager selectionManager;
    private CommandManager commandManager;
    private IPlayerContext playerContext = PrimaryPlayerContext.INSTANCE;
    private WorldProvider worldProvider;
    public BlockStateInterface bsi;

    Itemics() {
        this.pathingBehavior = new PathingBehavior(this);
        this.lookBehavior = new LookBehavior(this);
        this.inventoryBehavior = new InventoryBehavior(this);
        this.inputOverrideHandler = new InputOverrideHandler(this);
        this.waypointBehavior = new WaypointBehavior(this);
        this.pathingControlManager = new PathingControlManager(this);
        this.followProcess = new FollowProcess(this);
        this.pathingControlManager.registerProcess(this.followProcess);
        this.mineProcess = new MineProcess(this);
        this.pathingControlManager.registerProcess(this.mineProcess);
        this.customGoalProcess = new CustomGoalProcess(this);
        this.pathingControlManager.registerProcess(this.customGoalProcess);
        this.getToBlockProcess = new GetToBlockProcess(this);
        this.pathingControlManager.registerProcess(this.getToBlockProcess);
        this.builderProcess = new BuilderProcess(this);
        this.pathingControlManager.registerProcess(this.builderProcess);
        this.exploreProcess = new ExploreProcess(this);
        this.pathingControlManager.registerProcess(this.exploreProcess);
        this.backfillProcess = new BackfillProcess(this);
        this.pathingControlManager.registerProcess(this.backfillProcess);
        this.farmProcess = new FarmProcess(this);
        this.pathingControlManager.registerProcess(this.farmProcess);
        this.worldProvider = new WorldProvider();
        this.selectionManager = new SelectionManager(this);
        this.commandManager = new CommandManager(this);
    }

    @Override
    public PathingControlManager getPathingControlManager() {
        return this.pathingControlManager;
    }

    public void registerBehavior(Behavior behavior) {
        this.gameEventHandler.registerEventListener(behavior);
    }

    @Override
    public InputOverrideHandler getInputOverrideHandler() {
        return this.inputOverrideHandler;
    }

    @Override
    public CustomGoalProcess getCustomGoalProcess() {
        return this.customGoalProcess;
    }

    @Override
    public GetToBlockProcess getGetToBlockProcess() {
        return this.getToBlockProcess;
    }

    @Override
    public IPlayerContext getPlayerContext() {
        return this.playerContext;
    }

    @Override
    public FollowProcess getFollowProcess() {
        return this.followProcess;
    }

    @Override
    public BuilderProcess getBuilderProcess() {
        return this.builderProcess;
    }

    public InventoryBehavior getInventoryBehavior() {
        return this.inventoryBehavior;
    }

    @Override
    public LookBehavior getLookBehavior() {
        return this.lookBehavior;
    }

    @Override
    public ExploreProcess getExploreProcess() {
        return this.exploreProcess;
    }

    @Override
    public MineProcess getMineProcess() {
        return this.mineProcess;
    }

    @Override
    public FarmProcess getFarmProcess() {
        return this.farmProcess;
    }

    @Override
    public PathingBehavior getPathingBehavior() {
        return this.pathingBehavior;
    }

    @Override
    public SelectionManager getSelectionManager() {
        return this.selectionManager;
    }

    @Override
    public WorldProvider getWorldProvider() {
        return this.worldProvider;
    }

    @Override
    public IEventBus getGameEventHandler() {
        return this.gameEventHandler;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public void openClick() {
        new Thread(() -> {
            try {
                Thread.sleep(100L);
                Helper.mc.execute(() -> Helper.mc.displayGuiScreen(new GuiClick()));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }).start();
    }

    public static Settings settings() {
        return ItemicsAPI.getSettings();
    }

    public static File getDir() {
        return dir;
    }

    public static Executor getExecutor() {
        return threadPool;
    }

    static {
        if (!Files.exists(dir.toPath(), new LinkOption[0])) {
            try {
                Files.createDirectories(dir.toPath(), new FileAttribute[0]);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }
}
