package dimasik.managers.mods.voicechat;

import de.maxhenkel.configbuilder.ConfigBuilder;
import dimasik.managers.mods.voicechat.command.VoicechatCommands;
import dimasik.managers.mods.voicechat.config.ServerConfig;
import dimasik.managers.mods.voicechat.config.Translations;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.logging.Log4JVoicechatLogger;
import dimasik.managers.mods.voicechat.logging.VoicechatLogger;
import dimasik.managers.mods.voicechat.plugins.PluginManager;
import dimasik.managers.mods.voicechat.voice.server.ServerVoiceEvents;
import java.nio.file.Path;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import ru.dreamix.class2native.CompileNativeCalls;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
public abstract class Voicechat {
    public static final String MODID = "voicechat";
    public static final VoicechatLogger LOGGER = new Log4JVoicechatLogger("voicechat");
    public static ServerVoiceEvents SERVER;
    public static ServerConfig SERVER_CONFIG;
    public static Translations TRANSLATIONS;
    public static int COMPATIBILITY_VERSION;
    public static final Pattern GROUP_REGEX;

    @CompileNativeCalls
    public void initialize() {
        if (Voicechat.debugMode()) {
            LOGGER.warn("Running in debug mode - Don't leave this enabled in production!", new Object[0]);
        }
        LOGGER.info("Compatibility version {}", COMPATIBILITY_VERSION);
        this.initializeConfigs();
        CommonCompatibilityManager.INSTANCE.getNetManager().init();
        SERVER = new ServerVoiceEvents();
        this.initPlugins();
        this.registerCommands();
    }

    protected void initPlugins() {
        PluginManager.instance().init();
    }

    protected void registerCommands() {
        CommonCompatibilityManager.INSTANCE.onRegisterServerCommands(VoicechatCommands::register);
    }

    public void initializeConfigs() {
        SERVER_CONFIG = ConfigBuilder.builder(ServerConfig::new).path(this.getVoicechatConfigFolderInternal().resolve("voicechat-server.properties")).build();
        TRANSLATIONS = ConfigBuilder.builder(Translations::new).path(this.getVoicechatConfigFolderInternal().resolve("translations.properties")).build();
    }

    public static boolean debugMode() {
        return false;
    }

    protected Path getVoicechatConfigFolderInternal() {
        return Voicechat.getVoicechatConfigFolder();
    }

    public static Path getVoicechatConfigFolder() {
        return Minecraft.getInstance().gameDir.toPath().resolve(MODID);
    }

    public static Path getConfigFolder() {
        return Minecraft.getInstance().gameDir.toPath().resolve(MODID).resolve("config");
    }

    static {
        COMPATIBILITY_VERSION = 18;
        GROUP_REGEX = Pattern.compile("^[^\\n\\r\\t\\s][^\\n\\r\\t]{0,23}$");
    }
}
