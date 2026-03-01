package dimasik.itemics.api;

import dimasik.itemics.api.utils.NotificationHelper;
import dimasik.itemics.api.utils.SettingsUtil;
import dimasik.itemics.api.utils.TypeUtils;
import dimasik.itemics.api.utils.gui.ItemicsToast;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.text.ITextComponent;

public final class Settings {
    public final Setting<Boolean> allowBreak = new Setting<Boolean>(true);
    public final Setting<List<Block>> allowBreakAnyway = new Setting(new ArrayList());
    public final Setting<Boolean> allowSprint = new Setting<Boolean>(true);
    public final Setting<Boolean> allowPlace = new Setting<Boolean>(true);
    public final Setting<Boolean> allowInventory = new Setting<Boolean>(false);
    public final Setting<Boolean> assumeExternalAutoTool = new Setting<Boolean>(false);
    public final Setting<Boolean> autoTool = new Setting<Boolean>(true);
    public final Setting<Double> blockPlacementPenalty = new Setting<Double>(20.0);
    public final Setting<Double> blockBreakAdditionalPenalty = new Setting<Double>(2.0);
    public final Setting<Double> jumpPenalty = new Setting<Double>(2.0);
    public final Setting<Double> walkOnWaterOnePenalty = new Setting<Double>(3.0);
    public final Setting<Boolean> strictLiquidCheck = new Setting<Boolean>(false);
    public final Setting<Boolean> allowWaterBucketFall = new Setting<Boolean>(true);
    public final Setting<Boolean> assumeWalkOnWater = new Setting<Boolean>(false);
    public final Setting<Boolean> assumeWalkOnLava = new Setting<Boolean>(false);
    public final Setting<Boolean> assumeStep = new Setting<Boolean>(false);
    public final Setting<Boolean> assumeSafeWalk = new Setting<Boolean>(false);
    public final Setting<Boolean> allowJumpAt256 = new Setting<Boolean>(false);
    public final Setting<Boolean> allowParkourAscend = new Setting<Boolean>(true);
    public final Setting<Boolean> allowDiagonalDescend = new Setting<Boolean>(false);
    public final Setting<Boolean> allowDiagonalAscend = new Setting<Boolean>(false);
    public final Setting<Boolean> allowDownward = new Setting<Boolean>(true);
    public final Setting<List<Item>> acceptableThrowawayItems = new Setting<ArrayList<Item>>(new ArrayList<Item>(Arrays.asList(Blocks.DIRT.asItem(), Blocks.COBBLESTONE.asItem(), Blocks.NETHERRACK.asItem(), Blocks.STONE.asItem())));
    public final Setting<List<Block>> blocksToAvoid = new Setting(new ArrayList());
    public final Setting<List<Block>> blocksToDisallowBreaking = new Setting(new ArrayList());
    public final Setting<List<Block>> blocksToAvoidBreaking = new Setting<ArrayList<Block>>(new ArrayList<Block>(Arrays.asList(Blocks.CRAFTING_TABLE, Blocks.FURNACE, Blocks.CHEST, Blocks.TRAPPED_CHEST)));
    public final Setting<Double> avoidBreakingMultiplier = new Setting<Double>(0.1);
    public final Setting<List<Block>> buildIgnoreBlocks = new Setting<ArrayList<Block>>(new ArrayList<Block>(Arrays.asList(new Block[0])));
    public final Setting<List<Block>> buildSkipBlocks = new Setting<ArrayList<Block>>(new ArrayList<Block>(Arrays.asList(new Block[0])));
    public final Setting<Map<Block, List<Block>>> buildValidSubstitutes = new Setting(new HashMap());
    public final Setting<Map<Block, List<Block>>> buildSubstitutes = new Setting(new HashMap());
    public final Setting<List<Block>> okIfAir = new Setting<ArrayList<Block>>(new ArrayList<Block>(Arrays.asList(new Block[0])));
    public final Setting<Boolean> buildIgnoreExisting = new Setting<Boolean>(false);
    public final Setting<Boolean> buildIgnoreDirection = new Setting<Boolean>(false);
    public final Setting<List<String>> buildIgnoreProperties = new Setting<ArrayList<String>>(new ArrayList<String>(Arrays.asList(new String[0])));
    public final Setting<Boolean> avoidUpdatingFallingBlocks = new Setting<Boolean>(true);
    public final Setting<Boolean> allowVines = new Setting<Boolean>(false);
    public final Setting<Boolean> allowWalkOnBottomSlab = new Setting<Boolean>(true);
    public final Setting<Boolean> allowParkour = new Setting<Boolean>(false);
    public final Setting<Boolean> allowParkourPlace = new Setting<Boolean>(false);
    public final Setting<Boolean> considerPotionEffects = new Setting<Boolean>(true);
    public final Setting<Boolean> sprintAscends = new Setting<Boolean>(true);
    public final Setting<Boolean> overshootTraverse = new Setting<Boolean>(true);
    public final Setting<Boolean> pauseMiningForFallingBlocks = new Setting<Boolean>(true);
    public final Setting<Integer> rightClickSpeed = new Setting<Integer>(4);
    public final Setting<Double> randomLooking113 = new Setting<Double>(2.0);
    public final Setting<Float> blockReachDistance = new Setting<Float>(Float.valueOf(4.5f));
    public final Setting<Double> randomLooking = new Setting<Double>(0.01);
    public final Setting<Double> costHeuristic = new Setting<Double>(3.563);
    public final Setting<Integer> pathingMaxChunkBorderFetch = new Setting<Integer>(50);
    public final Setting<Double> backtrackCostFavoringCoefficient = new Setting<Double>(0.5);
    public final Setting<Boolean> avoidance = new Setting<Boolean>(false);
    public final Setting<Double> mobSpawnerAvoidanceCoefficient = new Setting<Double>(2.0);
    public final Setting<Integer> mobSpawnerAvoidanceRadius = new Setting<Integer>(16);
    public final Setting<Double> mobAvoidanceCoefficient = new Setting<Double>(1.5);
    public final Setting<Integer> mobAvoidanceRadius = new Setting<Integer>(8);
    public final Setting<Boolean> rightClickContainerOnArrival = new Setting<Boolean>(true);
    public final Setting<Boolean> enterPortal = new Setting<Boolean>(true);
    public final Setting<Boolean> minimumImprovementRepropagation = new Setting<Boolean>(true);
    public final Setting<Boolean> cutoffAtLoadBoundary = new Setting<Boolean>(false);
    public final Setting<Double> maxCostIncrease = new Setting<Double>(10.0);
    public final Setting<Integer> costVerificationLookahead = new Setting<Integer>(5);
    public final Setting<Double> pathCutoffFactor = new Setting<Double>(0.9);
    public final Setting<Integer> pathCutoffMinimumLength = new Setting<Integer>(30);
    public final Setting<Integer> planningTickLookahead = new Setting<Integer>(150);
    public final Setting<Integer> pathingMapDefaultSize = new Setting<Integer>(1024);
    public final Setting<Float> pathingMapLoadFactor = new Setting<Float>(Float.valueOf(0.75f));
    public final Setting<Integer> maxFallHeightNoWater = new Setting<Integer>(3);
    public final Setting<Integer> maxFallHeightBucket = new Setting<Integer>(20);
    public final Setting<Boolean> allowOvershootDiagonalDescend = new Setting<Boolean>(true);
    public final Setting<Boolean> simplifyUnloadedYCoord = new Setting<Boolean>(true);
    public final Setting<Boolean> repackOnAnyBlockChange = new Setting<Boolean>(true);
    public final Setting<Integer> movementTimeoutTicks = new Setting<Integer>(100);
    public final Setting<Long> primaryTimeoutMS = new Setting<Long>(500L);
    public final Setting<Long> failureTimeoutMS = new Setting<Long>(2000L);
    public final Setting<Long> planAheadPrimaryTimeoutMS = new Setting<Long>(4000L);
    public final Setting<Long> planAheadFailureTimeoutMS = new Setting<Long>(5000L);
    public final Setting<Boolean> slowPath = new Setting<Boolean>(false);
    public final Setting<Long> slowPathTimeDelayMS = new Setting<Long>(100L);
    public final Setting<Long> slowPathTimeoutMS = new Setting<Long>(40000L);
    public final Setting<Boolean> doBedWaypoints = new Setting<Boolean>(true);
    public final Setting<Boolean> doDeathWaypoints = new Setting<Boolean>(false);
    public final Setting<Boolean> chunkCaching = new Setting<Boolean>(true);
    public final Setting<Boolean> pruneRegionsFromRAM = new Setting<Boolean>(true);
    public final Setting<Boolean> backfill = new Setting<Boolean>(false);
    public final Setting<Boolean> logAsToast = new Setting<Boolean>(false);
    public final Setting<Long> toastTimer = new Setting<Long>(5000L);
    public final Setting<Boolean> chatDebug = new Setting<Boolean>(false);
    public final Setting<Boolean> chatControl = new Setting<Boolean>(true);
    public final Setting<Boolean> chatControlAnyway = new Setting<Boolean>(false);
    public final Setting<Boolean> renderPath = new Setting<Boolean>(true);
    public final Setting<Boolean> renderPathAsLine = new Setting<Boolean>(false);
    public final Setting<Boolean> renderGoal = new Setting<Boolean>(true);
    public final Setting<Boolean> renderGoalAnimated = new Setting<Boolean>(true);
    public final Setting<Boolean> renderSelectionBoxes = new Setting<Boolean>(true);
    public final Setting<Boolean> renderGoalIgnoreDepth = new Setting<Boolean>(true);
    public final Setting<Boolean> renderGoalXZBeacon = new Setting<Boolean>(false);
    public final Setting<Boolean> renderSelectionBoxesIgnoreDepth = new Setting<Boolean>(true);
    public final Setting<Boolean> renderPathIgnoreDepth = new Setting<Boolean>(true);
    public final Setting<Float> pathRenderLineWidthPixels = new Setting<Float>(Float.valueOf(5.0f));
    public final Setting<Float> goalRenderLineWidthPixels = new Setting<Float>(Float.valueOf(3.0f));
    public final Setting<Boolean> fadePath = new Setting<Boolean>(false);
    public final Setting<Boolean> freeLook = new Setting<Boolean>(true);
    public final Setting<Boolean> antiCheatCompatibility = new Setting<Boolean>(true);
    public final Setting<Boolean> pathThroughCachedOnly = new Setting<Boolean>(false);
    public final Setting<Boolean> sprintInWater = new Setting<Boolean>(true);
    public final Setting<Boolean> blacklistClosestOnFailure = new Setting<Boolean>(true);
    public final Setting<Boolean> renderCachedChunks = new Setting<Boolean>(false);
    public final Setting<Float> cachedChunksOpacity = new Setting<Float>(Float.valueOf(0.5f));
    public final Setting<Boolean> prefixControl = new Setting<Boolean>(true);
    public final Setting<String> prefix = new Setting<String>("#");
    public final Setting<Boolean> shortitemicsPrefix = new Setting<Boolean>(false);
    public final Setting<Boolean> echoCommands = new Setting<Boolean>(true);
    public final Setting<Boolean> censorCoordinates = new Setting<Boolean>(false);
    public final Setting<Boolean> censorRanCommands = new Setting<Boolean>(false);
    public final Setting<Boolean> itemSaver = new Setting<Boolean>(false);
    public final Setting<Integer> itemSaverThreshold = new Setting<Integer>(10);
    public final Setting<Boolean> preferSilkTouch = new Setting<Boolean>(false);
    public final Setting<Boolean> walkWhileBreaking = new Setting<Boolean>(true);
    public final Setting<Boolean> splicePath = new Setting<Boolean>(true);
    public final Setting<Integer> maxPathHistoryLength = new Setting<Integer>(300);
    public final Setting<Integer> pathHistoryCutoffAmount = new Setting<Integer>(50);
    public final Setting<Integer> mineGoalUpdateInterval = new Setting<Integer>(5);
    public final Setting<Integer> maxCachedWorldScanCount = new Setting<Integer>(10);
    public final Setting<Integer> minYLevelWhileMining = new Setting<Integer>(0);
    public final Setting<Boolean> allowOnlyExposedOres = new Setting<Boolean>(false);
    public final Setting<Integer> allowOnlyExposedOresDistance = new Setting<Integer>(1);
    public final Setting<Boolean> exploreForBlocks = new Setting<Boolean>(true);
    public final Setting<Integer> worldExploringChunkOffset = new Setting<Integer>(0);
    public final Setting<Integer> exploreChunkSetMinimumSize = new Setting<Integer>(10);
    public final Setting<Integer> exploreMaintainY = new Setting<Integer>(64);
    public final Setting<Boolean> replantCrops = new Setting<Boolean>(true);
    public final Setting<Boolean> replantNetherWart = new Setting<Boolean>(false);
    public final Setting<Boolean> extendCacheOnThreshold = new Setting<Boolean>(false);
    public final Setting<Boolean> buildInLayers = new Setting<Boolean>(false);
    public final Setting<Boolean> layerOrder = new Setting<Boolean>(false);
    public final Setting<Integer> layerHeight = new Setting<Integer>(1);
    public final Setting<Integer> startAtLayer = new Setting<Integer>(0);
    public final Setting<Boolean> skipFailedLayers = new Setting<Boolean>(false);
    public final Setting<Boolean> buildOnlySelection = new Setting<Boolean>(false);
    public final Setting<Vector3i> buildRepeat = new Setting<Vector3i>(new Vector3i(0, 0, 0));
    public final Setting<Integer> buildRepeatCount = new Setting<Integer>(-1);
    public final Setting<Boolean> buildRepeatSneaky = new Setting<Boolean>(true);
    public final Setting<Boolean> breakFromAbove = new Setting<Boolean>(false);
    public final Setting<Boolean> goalBreakFromAbove = new Setting<Boolean>(false);
    public final Setting<Boolean> mapArtMode = new Setting<Boolean>(false);
    public final Setting<Boolean> okIfWater = new Setting<Boolean>(false);
    public final Setting<Integer> incorrectSize = new Setting<Integer>(100);
    public final Setting<Double> breakCorrectBlockPenaltyMultiplier = new Setting<Double>(10.0);
    public final Setting<Boolean> schematicOrientationX = new Setting<Boolean>(false);
    public final Setting<Boolean> schematicOrientationY = new Setting<Boolean>(false);
    public final Setting<Boolean> schematicOrientationZ = new Setting<Boolean>(false);
    public final Setting<String> schematicFallbackExtension = new Setting<String>("schematic");
    public final Setting<Integer> builderTickScanRadius = new Setting<Integer>(5);
    public final Setting<Boolean> mineScanDroppedItems = new Setting<Boolean>(true);
    public final Setting<Long> mineDropLoiterDurationMSThanksLouca = new Setting<Long>(250L);
    public final Setting<Boolean> distanceTrim = new Setting<Boolean>(true);
    public final Setting<Boolean> cancelOnGoalInvalidation = new Setting<Boolean>(true);
    public final Setting<Integer> axisHeight = new Setting<Integer>(120);
    public final Setting<Boolean> disconnectOnArrival = new Setting<Boolean>(false);
    public final Setting<Boolean> legitMine = new Setting<Boolean>(false);
    public final Setting<Integer> legitMineYLevel = new Setting<Integer>(11);
    public final Setting<Boolean> legitMineIncludeDiagonals = new Setting<Boolean>(false);
    public final Setting<Boolean> forceInternalMining = new Setting<Boolean>(true);
    public final Setting<Boolean> internalMiningAirException = new Setting<Boolean>(true);
    public final Setting<Double> followOffsetDistance = new Setting<Double>(0.0);
    public final Setting<Float> followOffsetDirection = new Setting<Float>(Float.valueOf(0.0f));
    public final Setting<Integer> followRadius = new Setting<Integer>(3);
    public final Setting<Boolean> disableCompletionCheck = new Setting<Boolean>(false);
    public final Setting<Long> cachedChunksExpirySeconds = new Setting<Long>(-1L);
    public final Setting<Consumer<ITextComponent>> logger = new Setting<Consumer<ITextComponent>>(msg -> Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage((ITextComponent)msg));
    public final Setting<BiConsumer<String, Boolean>> notifier = new Setting<BiConsumer<String, Boolean>>(NotificationHelper::notify);
    public final Setting<BiConsumer<ITextComponent, ITextComponent>> toaster = new Setting<BiConsumer<ITextComponent, ITextComponent>>(ItemicsToast::addOrUpdate);
    public final Setting<Boolean> verboseCommandExceptions = new Setting<Boolean>(false);
    public final Setting<Double> yLevelBoxSize = new Setting<Double>(15.0);
    public final Setting<Color> colorCurrentPath = new Setting<Color>(Color.RED);
    public final Setting<Color> colorNextPath = new Setting<Color>(Color.MAGENTA);
    public final Setting<Color> colorBlocksToBreak = new Setting<Color>(Color.RED);
    public final Setting<Color> colorBlocksToPlace = new Setting<Color>(Color.GREEN);
    public final Setting<Color> colorBlocksToWalkInto = new Setting<Color>(Color.MAGENTA);
    public final Setting<Color> colorBestPathSoFar = new Setting<Color>(Color.BLUE);
    public final Setting<Color> colorMostRecentConsidered = new Setting<Color>(Color.CYAN);
    public final Setting<Color> colorGoalBox = new Setting<Color>(Color.GREEN);
    public final Setting<Color> colorInvertedGoalBox = new Setting<Color>(Color.RED);
    public final Setting<Color> colorSelection = new Setting<Color>(Color.CYAN);
    public final Setting<Color> colorSelectionPos1 = new Setting<Color>(Color.BLACK);
    public final Setting<Color> colorSelectionPos2 = new Setting<Color>(Color.ORANGE);
    public final Setting<Float> selectionOpacity = new Setting<Float>(Float.valueOf(0.5f));
    public final Setting<Float> selectionLineWidth = new Setting<Float>(Float.valueOf(2.0f));
    public final Setting<Boolean> renderSelection = new Setting<Boolean>(true);
    public final Setting<Boolean> renderSelectionIgnoreDepth = new Setting<Boolean>(true);
    public final Setting<Boolean> renderSelectionCorners = new Setting<Boolean>(true);
    public final Setting<Boolean> useSwordToMine = new Setting<Boolean>(true);
    public final Setting<Boolean> desktopNotifications = new Setting<Boolean>(false);
    public final Setting<Boolean> notificationOnPathComplete = new Setting<Boolean>(true);
    public final Setting<Boolean> notificationOnFarmFail = new Setting<Boolean>(true);
    public final Setting<Boolean> notificationOnBuildFinished = new Setting<Boolean>(true);
    public final Setting<Boolean> notificationOnExploreFinished = new Setting<Boolean>(true);
    public final Setting<Boolean> notificationOnMineFail = new Setting<Boolean>(true);
    public final Map<String, Setting<?>> byLowerName;
    public final List<Setting<?>> allSettings;
    public final Map<Setting<?>, Type> settingTypes;

    Settings() {
        Field[] temp = this.getClass().getFields();
        HashMap<String, Setting> tmpByName = new HashMap<String, Setting>();
        ArrayList<Setting> tmpAll = new ArrayList<Setting>();
        HashMap<Setting, Type> tmpSettingTypes = new HashMap<Setting, Type>();
        try {
            for (Field field : temp) {
                String name;
                if (!field.getType().equals(Setting.class)) continue;
                Setting setting = (Setting)field.get(this);
                setting.name = name = field.getName();
                if (tmpByName.containsKey(name = name.toLowerCase())) {
                    throw new IllegalStateException("Duplicate setting name");
                }
                tmpByName.put(name, setting);
                tmpAll.add(setting);
                tmpSettingTypes.put(setting, ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0]);
            }
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        this.byLowerName = Collections.unmodifiableMap(tmpByName);
        this.allSettings = Collections.unmodifiableList(tmpAll);
        this.settingTypes = Collections.unmodifiableMap(tmpSettingTypes);
    }

    public <T> List<Setting<T>> getAllValuesByType(Class<T> cla$$) {
        ArrayList<Setting<T>> result = new ArrayList<Setting<T>>();
        for (Setting<?> setting : this.allSettings) {
            if (!setting.getValueClass().equals(cla$$)) continue;
            result.add(setting);
        }
        return result;
    }

    public final class Setting<T> {
        public T value;
        public final T defaultValue;
        private String name;

        private Setting(T value) {
            if (value == null) {
                throw new IllegalArgumentException("Cannot determine value type class from null");
            }
            this.value = value;
            this.defaultValue = value;
        }

        @Deprecated
        public final T get() {
            return this.value;
        }

        public final String getName() {
            return this.name;
        }

        public Class<T> getValueClass() {
            return TypeUtils.resolveBaseClass(this.getType());
        }

        public String toString() {
            return SettingsUtil.settingToString(this);
        }

        public void reset() {
            this.value = this.defaultValue;
        }

        public final Type getType() {
            return Settings.this.settingTypes.get(this);
        }
    }
}
