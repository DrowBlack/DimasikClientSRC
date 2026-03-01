package dimasik;

import dimasik.events.api.EventManagers;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.config.main.AltConfig;
import dimasik.managers.config.main.ClientConfig;
import dimasik.managers.config.main.DraggableConfig;
import dimasik.managers.config.main.FriendConfig;
import dimasik.managers.config.main.MacroConfig;
import dimasik.managers.config.main.ModuleConfig;
import dimasik.managers.config.main.StaffConfig;
import dimasik.managers.hook.HookManagers;
import dimasik.managers.mods.voicechat.VoicechatClientInit;
import dimasik.managers.mods.voicechat.VoicechatInit;
import dimasik.proxy.ProxyConfig;
import dimasik.ui.alt.AltScreen;
import dimasik.ui.autobuy.BuyScreen;
import dimasik.ui.screen.UIScreen;
import dimasik.utils.discord.rpc.DiscordManager;
import java.io.File;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import ru.dreamix.class2native.CompileNativeCalls;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.ONLY_GENERATIVE)
public class Load
implements IFastAccess {
    private static final double startTime = System.currentTimeMillis();
    private static Load instance;
    private final HookManagers hooks = new HookManagers();
    private final UIScreen uiScreen;
    private final AltScreen altScreen;
    private final DiscordManager discordManager;
    private final EventManagers events;
    private final BuyScreen buyScreen;

    @CompileNativeCalls
    public Load() {
        instance = this;
        this.discordManager = new DiscordManager();
        this.uiScreen = new UIScreen();
        this.buyScreen = new BuyScreen();
        this.altScreen = new AltScreen();
        this.events = new EventManagers();
        new VoicechatInit().init();
        new VoicechatClientInit().init();
        ProxyConfig.loadConfig();
        this.autoLoad();
    }

    @CompileNativeCalls
    @AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.ONLY_GENERATIVE)
    private void autoLoad() {
        ((ClientConfig)this.hooks.getConfigManagers().findClass(ClientConfig.class)).fastLoad();
        ((FriendConfig)this.hooks.getConfigManagers().findClass(FriendConfig.class)).fastLoad();
        ((StaffConfig)this.hooks.getConfigManagers().findClass(StaffConfig.class)).fastLoad();
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).fastLoad();
        ((DraggableConfig)this.hooks.getConfigManagers().findClass(DraggableConfig.class)).fastLoad();
        ((MacroConfig)this.hooks.getConfigManagers().findClass(MacroConfig.class)).fastLoad();
        ((AltConfig)Load.getInstance().getHooks().getConfigManagers().findClass(AltConfig.class)).fastLoad();
    }

    private void autoSave() {
        ((FriendConfig)this.hooks.getConfigManagers().findClass(FriendConfig.class)).fastSave();
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).setName("module");
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).setPath(new File(Minecraft.getInstance().gameDir, "dimasik/configs"));
        ((ModuleConfig)this.hooks.getConfigManagers().findClass(ModuleConfig.class)).fastSave();
        ((DraggableConfig)this.hooks.getConfigManagers().findClass(DraggableConfig.class)).fastSave();
        ((ClientConfig)this.hooks.getConfigManagers().findClass(ClientConfig.class)).fastSave();
        ((AltConfig)Load.getInstance().getHooks().getConfigManagers().findClass(AltConfig.class)).fastSave();
    }

    public void shutDown() {
        this.autoSave();
    }

    @Generated
    public HookManagers getHooks() {
        return this.hooks;
    }

    @Generated
    public UIScreen getUiScreen() {
        return this.uiScreen;
    }

    @Generated
    public AltScreen getAltScreen() {
        return this.altScreen;
    }

    @Generated
    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }

    @Generated
    public EventManagers getEvents() {
        return this.events;
    }

    @Generated
    public BuyScreen getBuyScreen() {
        return this.buyScreen;
    }

    @Generated
    public static double getStartTime() {
        return startTime;
    }

    @Generated
    public static Load getInstance() {
        return instance;
    }
}
