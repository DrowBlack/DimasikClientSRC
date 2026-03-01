package dimasik.managers.hook.main;

import dimasik.helpers.interfaces.IFinderModules;
import dimasik.helpers.interfaces.IManager;
import dimasik.managers.module.Module;
import dimasik.modules.combat.AimAssist;
import dimasik.modules.combat.AimBot;
import dimasik.modules.combat.AntiBot;
import dimasik.modules.combat.AntiCrystal;
import dimasik.modules.combat.Aura;
import dimasik.modules.combat.AutoArmor;
import dimasik.modules.combat.AutoCrystal;
import dimasik.modules.combat.AutoSwap;
import dimasik.modules.combat.AutoTotem;
import dimasik.modules.combat.CrystalAura;
import dimasik.modules.combat.ElytraTarget;
import dimasik.modules.combat.HitBox;
import dimasik.modules.combat.ItemSwapFix;
import dimasik.modules.combat.TriggerBot;
import dimasik.modules.combat.Velocity;
import dimasik.modules.combat.WallsBypass;
import dimasik.modules.misc.AhHelper;
import dimasik.modules.misc.AncientXray;
import dimasik.modules.misc.AutoContract;
import dimasik.modules.misc.AutoFish;
import dimasik.modules.misc.AutoLeave;
import dimasik.modules.misc.AutoMessage;
import dimasik.modules.misc.Blink;
import dimasik.modules.misc.ChatHelper;
import dimasik.modules.misc.ClickAction;
import dimasik.modules.misc.ClientSound;
import dimasik.modules.misc.ClipHelper;
import dimasik.modules.misc.CreeperFarm;
import dimasik.modules.misc.FTHelper;
import dimasik.modules.misc.GriefJoiner;
import dimasik.modules.misc.HWHelper;
import dimasik.modules.misc.HitSound;
import dimasik.modules.misc.ItemScroller;
import dimasik.modules.misc.LeaveTracker;
import dimasik.modules.misc.NotifSettings;
import dimasik.modules.misc.Optimization;
import dimasik.modules.misc.PacketDebug;
import dimasik.modules.misc.RWHelper;
import dimasik.modules.misc.SRPSpoofer;
import dimasik.modules.misc.Spinner;
import dimasik.modules.misc.TNTTimer;
import dimasik.modules.misc.UnHook;
import dimasik.modules.misc.XCarry;
import dimasik.modules.movement.AirStuck;
import dimasik.modules.movement.AutoSprint;
import dimasik.modules.movement.AutoTPLoot;
import dimasik.modules.movement.BlockFly;
import dimasik.modules.movement.DragonFly;
import dimasik.modules.movement.ElytraBounce;
import dimasik.modules.movement.ElytraFly;
import dimasik.modules.movement.ElytraMotion;
import dimasik.modules.movement.Fly;
import dimasik.modules.movement.FlyTest;
import dimasik.modules.movement.GuiMove;
import dimasik.modules.movement.NoDelay;
import dimasik.modules.movement.NoSlow;
import dimasik.modules.movement.NoWeb;
import dimasik.modules.movement.Phase;
import dimasik.modules.movement.Speed;
import dimasik.modules.movement.Timer;
import dimasik.modules.movement.TpBack;
import dimasik.modules.player.AutoAccept;
import dimasik.modules.player.AutoDuel;
import dimasik.modules.player.AutoEat;
import dimasik.modules.player.AutoPotion;
import dimasik.modules.player.AutoRepair;
import dimasik.modules.player.AutoTool;
import dimasik.modules.player.AutoWeb;
import dimasik.modules.player.ChestStealer;
import dimasik.modules.player.ElytraSwap;
import dimasik.modules.player.FastBreak;
import dimasik.modules.player.FixHP;
import dimasik.modules.player.FreeCam;
import dimasik.modules.player.ItemFinder;
import dimasik.modules.player.ItemsCooldown;
import dimasik.modules.player.LockSlot;
import dimasik.modules.player.NameProtect;
import dimasik.modules.player.NoFriendDamage;
import dimasik.modules.player.NoInteract;
import dimasik.modules.player.NoPush;
import dimasik.modules.player.Noclip;
import dimasik.modules.player.PearlTarget;
import dimasik.modules.player.PvPHelper;
import dimasik.modules.player.RGExploit;
import dimasik.modules.player.RagelikPaster;
import dimasik.modules.player.VoiceChat;
import dimasik.modules.render.Arrows;
import dimasik.modules.render.Aspect;
import dimasik.modules.render.BetterChat;
import dimasik.modules.render.BetterMinecraft;
import dimasik.modules.render.BetterTab;
import dimasik.modules.render.BlockEsp;
import dimasik.modules.render.ChinaHat;
import dimasik.modules.render.ChunkAnimator;
import dimasik.modules.render.ClickGui;
import dimasik.modules.render.Crosshair;
import dimasik.modules.render.CustomModel;
import dimasik.modules.render.CustomWorld;
import dimasik.modules.render.ESP;
import dimasik.modules.render.FireworkEsp;
import dimasik.modules.render.FullBright;
import dimasik.modules.render.Interface;
import dimasik.modules.render.ItemESP;
import dimasik.modules.render.NoGameOverlay;
import dimasik.modules.render.Particles;
import dimasik.modules.render.PearlPrediction;
import dimasik.modules.render.ShulkerView;
import dimasik.modules.render.SwingAnimations;
import dimasik.modules.render.TargetESP;
import dimasik.modules.render.Tracers;
import java.util.ArrayList;
import java.util.Comparator;
import lombok.Generated;
import ru.dreamix.class2native.CompileNativeCalls;

public class ModuleManagers
extends ArrayList<Module>
implements IManager<Module>,
IFinderModules<Module> {
    private final Aura aura = new Aura();
    private final AutoSprint autoSprint = new AutoSprint();
    private final ClickGui clickGui = new ClickGui();
    private final AntiBot antiBot = new AntiBot();
    private final NoDelay noDelay = new NoDelay();
    private final AutoTotem autoTotem = new AutoTotem();
    private final TpBack tpBack = new TpBack();
    private final NoGameOverlay noGameOverlay = new NoGameOverlay();
    private final SRPSpoofer srpSpoofer = new SRPSpoofer();
    private final NoWeb noWeb = new NoWeb();
    private final GuiMove guiMove = new GuiMove();
    private final VoiceChat voiceChat = new VoiceChat();
    private final AutoPotion autoPotion = new AutoPotion();
    private final AutoDuel autoDuel = new AutoDuel();
    private final NoPush noPush = new NoPush();
    private final Optimization optimization = new Optimization();
    private final HitBox hitBox = new HitBox();
    private final HWHelper hwHelper = new HWHelper();
    private final XCarry xCarry = new XCarry();
    private final AutoAccept autoAccept = new AutoAccept();
    private final ChestStealer chestStealer = new ChestStealer();
    private final TriggerBot triggerBot = new TriggerBot();
    private final ElytraSwap elytraSwap = new ElytraSwap();
    private final ElytraMotion elytraMotion = new ElytraMotion();
    private final RWHelper rwHelper = new RWHelper();
    private final ElytraFly elytraFly = new ElytraFly();
    private final PacketDebug packetDebug = new PacketDebug();
    private final LeaveTracker leaveTracker = new LeaveTracker();
    private final BetterChat betterChat = new BetterChat();
    private final Interface interfaces = new Interface();
    private final NoInteract noInteract = new NoInteract();
    private final ESP esp = new ESP();
    private final ItemScroller itemScroller = new ItemScroller();
    private final NoFriendDamage noFriendDamage = new NoFriendDamage();
    private final PvPHelper pvpHelper = new PvPHelper();
    private final ItemESP itemESP = new ItemESP();
    private final FreeCam freeCam = new FreeCam();
    private final AutoLeave autoLeave = new AutoLeave();
    private final PearlTarget pearlTarget = new PearlTarget();
    private final ClickAction clickAction = new ClickAction();
    private final UnHook unHook = new UnHook();
    private final AutoSwap autoSwap = new AutoSwap();
    private final Tracers tracers = new Tracers();
    private final CustomWorld customWorld = new CustomWorld();
    private final Fly fly = new Fly();
    private final SwingAnimations swingAnimations = new SwingAnimations();
    private final FixHP fixHP = new FixHP();
    private final CustomModel customModel = new CustomModel();
    private final ItemsCooldown itemsCooldown = new ItemsCooldown();
    private final Spinner spinner = new Spinner();
    private final AirStuck airStuck = new AirStuck();
    private final HitSound hitSound = new HitSound();
    private final Speed speed = new Speed();
    private final Particles particles = new Particles();
    private final NoSlow noSlow = new NoSlow();
    private final ClientSound clientSound = new ClientSound();
    private final TargetESP targetESP = new TargetESP();
    private final ElytraBounce elytraBounce = new ElytraBounce();
    private final FullBright fullBright = new FullBright();
    private final ItemFinder itemFinder = new ItemFinder();
    private final RGExploit rgExploit = new RGExploit();
    private final ElytraTarget elytraTarget = new ElytraTarget();
    private final DragonFly dragonFly = new DragonFly();
    private final FastBreak fastBreak = new FastBreak();
    private final Velocity velocity = new Velocity();
    private final GriefJoiner griefJoiner = new GriefJoiner();
    private final BetterMinecraft betterMinecraft = new BetterMinecraft();
    private final NameProtect nameProtect = new NameProtect();
    private final PearlPrediction pearlPrediction = new PearlPrediction();
    private final Arrows arrows = new Arrows();
    private final BlockEsp blockEsp = new BlockEsp();
    private final AutoEat autoEat = new AutoEat();
    private final ChinaHat chinaHat = new ChinaHat();
    private final AutoTool autoTool = new AutoTool();
    private final Noclip noClip = new Noclip();
    private final AutoCrystal autoCrystal = new AutoCrystal();
    private final AimBot aimBot = new AimBot();
    private final ItemSwapFix itemSwapFix = new ItemSwapFix();
    private final BetterTab betterTab = new BetterTab();
    private final Crosshair crosshair = new Crosshair();
    private final FireworkEsp fireworkEsp = new FireworkEsp();
    private final Aspect aspect = new Aspect();
    private final AutoMessage autoMessage = new AutoMessage();
    private final AutoContract autoContract = new AutoContract();
    private final AntiCrystal antiCrystal = new AntiCrystal();
    private final CrystalAura crystalAura = new CrystalAura();
    private final ClipHelper clipHelper = new ClipHelper();
    private final BlockFly scaffold = new BlockFly();
    private final NotifSettings notifSettings = new NotifSettings();
    private final RagelikPaster ragelikPaster = new RagelikPaster();
    private final Timer timer = new Timer();
    private final AimAssist aimAssist = new AimAssist();
    private final ShulkerView shulkerView = new ShulkerView();
    private final ChunkAnimator chunkAnimator = new ChunkAnimator();
    private final ChatHelper chatHelper = new ChatHelper();
    private final AhHelper ahHelper = new AhHelper();
    private final AncientXray ancientXray = new AncientXray();
    private final CreeperFarm creeperFarm = new CreeperFarm();
    private final LockSlot lockSlot = new LockSlot();
    private final AutoTPLoot autoTPLoot = new AutoTPLoot();
    private final FlyTest flyTest = new FlyTest();
    private final WallsBypass wallsBypass = new WallsBypass();
    private final AutoArmor autoArmor = new AutoArmor();
    private final Blink blink = new Blink();
    private final FTHelper ftHelper = new FTHelper();
    private final TNTTimer tntTimer = new TNTTimer();
    private final AutoRepair autoRepair = new AutoRepair();
    private final AutoWeb autoWeb = new AutoWeb();
    private final Phase phase = new Phase();
    private final AutoFish autoFish = new AutoFish();

    @CompileNativeCalls
    public ModuleManagers() {
        this.init();
    }

    @Override
    @CompileNativeCalls
    public void init() {
        this.register(this.aura);
        this.register(this.autoWeb);
        this.register(this.phase);
        this.register(this.autoFish);
        this.register(this.flyTest);
        this.register(this.autoRepair);
        this.register(this.tntTimer);
        this.register(this.timer);
        this.register(this.hwHelper);
        this.register(this.autoSprint);
        this.register(this.clickGui);
        this.register(this.aimAssist);
        this.register(this.wallsBypass);
        this.register(this.ragelikPaster);
        this.register(this.clipHelper);
        this.register(this.notifSettings);
        this.register(this.antiBot);
        this.register(this.aspect);
        this.register(this.shulkerView);
        this.register(this.chunkAnimator);
        this.register(this.chatHelper);
        this.register(this.ahHelper);
        this.register(this.ancientXray);
        this.register(this.creeperFarm);
        this.register(this.lockSlot);
        this.register(this.autoTPLoot);
        this.register(this.scaffold);
        this.register(this.itemSwapFix);
        this.register(this.antiCrystal);
        this.register(this.autoMessage);
        this.register(this.autoContract);
        this.register(this.betterTab);
        this.register(this.crosshair);
        this.register(this.fireworkEsp);
        this.register(this.aimBot);
        this.register(this.noDelay);
        this.register(this.autoTotem);
        this.register(this.tpBack);
        this.register(this.noGameOverlay);
        this.register(this.srpSpoofer);
        this.register(this.noWeb);
        this.register(this.guiMove);
        this.register(this.voiceChat);
        this.register(this.autoPotion);
        this.register(this.autoDuel);
        this.register(this.noPush);
        this.register(this.optimization);
        this.register(this.hitBox);
        this.register(this.xCarry);
        this.register(this.autoAccept);
        this.register(this.crystalAura);
        this.register(this.chestStealer);
        this.register(this.triggerBot);
        this.register(this.elytraSwap);
        this.register(this.elytraMotion);
        this.register(this.rwHelper);
        this.register(this.elytraFly);
        this.register(this.packetDebug);
        this.register(this.leaveTracker);
        this.register(this.betterChat);
        this.register(this.interfaces);
        this.register(this.noInteract);
        this.register(this.esp);
        this.register(this.itemScroller);
        this.register(this.noFriendDamage);
        this.register(this.pvpHelper);
        this.register(this.itemESP);
        this.register(this.freeCam);
        this.register(this.autoLeave);
        this.register(this.pearlTarget);
        this.register(this.clickAction);
        this.register(this.unHook);
        this.register(this.autoSwap);
        this.register(this.tracers);
        this.register(this.customWorld);
        this.register(this.fly);
        this.register(this.swingAnimations);
        this.register(this.fixHP);
        this.register(this.customModel);
        this.register(this.itemsCooldown);
        this.register(this.spinner);
        this.register(this.airStuck);
        this.register(this.hitSound);
        this.register(this.speed);
        this.register(this.particles);
        this.register(this.noSlow);
        this.register(this.clientSound);
        this.register(this.targetESP);
        this.register(this.elytraBounce);
        this.register(this.fullBright);
        this.register(this.itemFinder);
        this.register(this.rgExploit);
        this.register(this.elytraTarget);
        this.register(this.dragonFly);
        this.register(this.fastBreak);
        this.register(this.velocity);
        this.register(this.griefJoiner);
        this.register(this.betterMinecraft);
        this.register(this.nameProtect);
        this.register(this.pearlPrediction);
        this.register(this.arrows);
        this.register(this.blockEsp);
        this.register(this.autoEat);
        this.register(this.chinaHat);
        this.register(this.autoTool);
        this.register(this.noClip);
        this.register(this.autoCrystal);
        this.sortModulesByName();
    }

    @Override
    @CompileNativeCalls
    public void register(Module module) {
        this.add(module);
    }

    private void sortModulesByName() {
        this.sort(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER));
    }

    @Override
    public <T extends Module> T findName(String name) {
        return (T)((Module)this.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Module> T findClass(Class<T> clazz) {
        return (T)((Module)this.stream().filter(module -> module.getClass() == clazz).findAny().orElse(null));
    }

    @Generated
    public Aura getAura() {
        return this.aura;
    }

    @Generated
    public AutoSprint getAutoSprint() {
        return this.autoSprint;
    }

    @Generated
    public ClickGui getClickGui() {
        return this.clickGui;
    }

    @Generated
    public AntiBot getAntiBot() {
        return this.antiBot;
    }

    @Generated
    public NoDelay getNoDelay() {
        return this.noDelay;
    }

    @Generated
    public AutoTotem getAutoTotem() {
        return this.autoTotem;
    }

    @Generated
    public TpBack getTpBack() {
        return this.tpBack;
    }

    @Generated
    public NoGameOverlay getNoGameOverlay() {
        return this.noGameOverlay;
    }

    @Generated
    public SRPSpoofer getSrpSpoofer() {
        return this.srpSpoofer;
    }

    @Generated
    public NoWeb getNoWeb() {
        return this.noWeb;
    }

    @Generated
    public GuiMove getGuiMove() {
        return this.guiMove;
    }

    @Generated
    public VoiceChat getVoiceChat() {
        return this.voiceChat;
    }

    @Generated
    public AutoPotion getAutoPotion() {
        return this.autoPotion;
    }

    @Generated
    public AutoDuel getAutoDuel() {
        return this.autoDuel;
    }

    @Generated
    public NoPush getNoPush() {
        return this.noPush;
    }

    @Generated
    public Optimization getOptimization() {
        return this.optimization;
    }

    @Generated
    public HitBox getHitBox() {
        return this.hitBox;
    }

    @Generated
    public HWHelper getHwHelper() {
        return this.hwHelper;
    }

    @Generated
    public XCarry getXCarry() {
        return this.xCarry;
    }

    @Generated
    public AutoAccept getAutoAccept() {
        return this.autoAccept;
    }

    @Generated
    public ChestStealer getChestStealer() {
        return this.chestStealer;
    }

    @Generated
    public TriggerBot getTriggerBot() {
        return this.triggerBot;
    }

    @Generated
    public ElytraSwap getElytraSwap() {
        return this.elytraSwap;
    }

    @Generated
    public ElytraMotion getElytraMotion() {
        return this.elytraMotion;
    }

    @Generated
    public RWHelper getRwHelper() {
        return this.rwHelper;
    }

    @Generated
    public ElytraFly getElytraFly() {
        return this.elytraFly;
    }

    @Generated
    public PacketDebug getPacketDebug() {
        return this.packetDebug;
    }

    @Generated
    public LeaveTracker getLeaveTracker() {
        return this.leaveTracker;
    }

    @Generated
    public BetterChat getBetterChat() {
        return this.betterChat;
    }

    @Generated
    public Interface getInterfaces() {
        return this.interfaces;
    }

    @Generated
    public NoInteract getNoInteract() {
        return this.noInteract;
    }

    @Generated
    public ESP getEsp() {
        return this.esp;
    }

    @Generated
    public ItemScroller getItemScroller() {
        return this.itemScroller;
    }

    @Generated
    public NoFriendDamage getNoFriendDamage() {
        return this.noFriendDamage;
    }

    @Generated
    public PvPHelper getPvpHelper() {
        return this.pvpHelper;
    }

    @Generated
    public ItemESP getItemESP() {
        return this.itemESP;
    }

    @Generated
    public FreeCam getFreeCam() {
        return this.freeCam;
    }

    @Generated
    public AutoLeave getAutoLeave() {
        return this.autoLeave;
    }

    @Generated
    public PearlTarget getPearlTarget() {
        return this.pearlTarget;
    }

    @Generated
    public ClickAction getClickAction() {
        return this.clickAction;
    }

    @Generated
    public UnHook getUnHook() {
        return this.unHook;
    }

    @Generated
    public AutoSwap getAutoSwap() {
        return this.autoSwap;
    }

    @Generated
    public Tracers getTracers() {
        return this.tracers;
    }

    @Generated
    public CustomWorld getCustomWorld() {
        return this.customWorld;
    }

    @Generated
    public Fly getFly() {
        return this.fly;
    }

    @Generated
    public SwingAnimations getSwingAnimations() {
        return this.swingAnimations;
    }

    @Generated
    public FixHP getFixHP() {
        return this.fixHP;
    }

    @Generated
    public CustomModel getCustomModel() {
        return this.customModel;
    }

    @Generated
    public ItemsCooldown getItemsCooldown() {
        return this.itemsCooldown;
    }

    @Generated
    public Spinner getSpinner() {
        return this.spinner;
    }

    @Generated
    public AirStuck getAirStuck() {
        return this.airStuck;
    }

    @Generated
    public HitSound getHitSound() {
        return this.hitSound;
    }

    @Generated
    public Speed getSpeed() {
        return this.speed;
    }

    @Generated
    public Particles getParticles() {
        return this.particles;
    }

    @Generated
    public NoSlow getNoSlow() {
        return this.noSlow;
    }

    @Generated
    public ClientSound getClientSound() {
        return this.clientSound;
    }

    @Generated
    public TargetESP getTargetESP() {
        return this.targetESP;
    }

    @Generated
    public ElytraBounce getElytraBounce() {
        return this.elytraBounce;
    }

    @Generated
    public FullBright getFullBright() {
        return this.fullBright;
    }

    @Generated
    public ItemFinder getItemFinder() {
        return this.itemFinder;
    }

    @Generated
    public RGExploit getRgExploit() {
        return this.rgExploit;
    }

    @Generated
    public ElytraTarget getElytraTarget() {
        return this.elytraTarget;
    }

    @Generated
    public DragonFly getDragonFly() {
        return this.dragonFly;
    }

    @Generated
    public FastBreak getFastBreak() {
        return this.fastBreak;
    }

    @Generated
    public Velocity getVelocity() {
        return this.velocity;
    }

    @Generated
    public GriefJoiner getGriefJoiner() {
        return this.griefJoiner;
    }

    @Generated
    public BetterMinecraft getBetterMinecraft() {
        return this.betterMinecraft;
    }

    @Generated
    public NameProtect getNameProtect() {
        return this.nameProtect;
    }

    @Generated
    public PearlPrediction getPearlPrediction() {
        return this.pearlPrediction;
    }

    @Generated
    public Arrows getArrows() {
        return this.arrows;
    }

    @Generated
    public BlockEsp getBlockEsp() {
        return this.blockEsp;
    }

    @Generated
    public AutoEat getAutoEat() {
        return this.autoEat;
    }

    @Generated
    public ChinaHat getChinaHat() {
        return this.chinaHat;
    }

    @Generated
    public AutoTool getAutoTool() {
        return this.autoTool;
    }

    @Generated
    public Noclip getNoClip() {
        return this.noClip;
    }

    @Generated
    public AutoCrystal getAutoCrystal() {
        return this.autoCrystal;
    }

    @Generated
    public AimBot getAimBot() {
        return this.aimBot;
    }

    @Generated
    public ItemSwapFix getItemSwapFix() {
        return this.itemSwapFix;
    }

    @Generated
    public BetterTab getBetterTab() {
        return this.betterTab;
    }

    @Generated
    public Crosshair getCrosshair() {
        return this.crosshair;
    }

    @Generated
    public FireworkEsp getFireworkEsp() {
        return this.fireworkEsp;
    }

    @Generated
    public Aspect getAspect() {
        return this.aspect;
    }

    @Generated
    public AutoMessage getAutoMessage() {
        return this.autoMessage;
    }

    @Generated
    public AutoContract getAutoContract() {
        return this.autoContract;
    }

    @Generated
    public AntiCrystal getAntiCrystal() {
        return this.antiCrystal;
    }

    @Generated
    public CrystalAura getCrystalAura() {
        return this.crystalAura;
    }

    @Generated
    public ClipHelper getClipHelper() {
        return this.clipHelper;
    }

    @Generated
    public BlockFly getScaffold() {
        return this.scaffold;
    }

    @Generated
    public NotifSettings getNotifSettings() {
        return this.notifSettings;
    }

    @Generated
    public RagelikPaster getRagelikPaster() {
        return this.ragelikPaster;
    }

    @Generated
    public Timer getTimer() {
        return this.timer;
    }

    @Generated
    public AimAssist getAimAssist() {
        return this.aimAssist;
    }

    @Generated
    public ShulkerView getShulkerView() {
        return this.shulkerView;
    }

    @Generated
    public ChunkAnimator getChunkAnimator() {
        return this.chunkAnimator;
    }

    @Generated
    public ChatHelper getChatHelper() {
        return this.chatHelper;
    }

    @Generated
    public AhHelper getAhHelper() {
        return this.ahHelper;
    }

    @Generated
    public AncientXray getAncientXray() {
        return this.ancientXray;
    }

    @Generated
    public CreeperFarm getCreeperFarm() {
        return this.creeperFarm;
    }

    @Generated
    public LockSlot getLockSlot() {
        return this.lockSlot;
    }

    @Generated
    public AutoTPLoot getAutoTPLoot() {
        return this.autoTPLoot;
    }

    @Generated
    public FlyTest getFlyTest() {
        return this.flyTest;
    }

    @Generated
    public WallsBypass getWallsBypass() {
        return this.wallsBypass;
    }

    @Generated
    public AutoArmor getAutoArmor() {
        return this.autoArmor;
    }

    @Generated
    public Blink getBlink() {
        return this.blink;
    }

    @Generated
    public FTHelper getFtHelper() {
        return this.ftHelper;
    }

    @Generated
    public TNTTimer getTntTimer() {
        return this.tntTimer;
    }

    @Generated
    public AutoRepair getAutoRepair() {
        return this.autoRepair;
    }

    @Generated
    public AutoWeb getAutoWeb() {
        return this.autoWeb;
    }

    @Generated
    public Phase getPhase() {
        return this.phase;
    }

    @Generated
    public AutoFish getAutoFish() {
        return this.autoFish;
    }
}
