package dimasik.managers.mods.voicechat;

import com.sun.jna.Platform;
import de.maxhenkel.configbuilder.ConfigBuilder;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.config.ClientConfig;
import dimasik.managers.mods.voicechat.config.VolumeConfig;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.macos.VersionCheck;
import dimasik.managers.mods.voicechat.plugins.impl.opus.OpusManager;
import dimasik.managers.mods.voicechat.profile.UsernameCache;
import dimasik.managers.mods.voicechat.resourcepacks.VoiceChatResourcePack;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class VoicechatClient {
    public static ClientConfig CLIENT_CONFIG;
    public static VolumeConfig VOLUME_CONFIG;
    public static UsernameCache USERNAME_CACHE;
    public static VoiceChatResourcePack CLASSIC_ICONS;
    public static VoiceChatResourcePack WHITE_ICONS;
    public static VoiceChatResourcePack BLACK_ICONS;

    public void initializeConfigs() {
        this.fixVolumeConfig();
        CLIENT_CONFIG = ConfigBuilder.builder(ClientConfig::new).path(Voicechat.getVoicechatConfigFolder().resolve("voicechat-client.properties")).build();
        VOLUME_CONFIG = new VolumeConfig(Voicechat.getVoicechatConfigFolder().resolve("voicechat-volumes.properties"));
        USERNAME_CACHE = new UsernameCache(Voicechat.getVoicechatConfigFolder().resolve("username-cache.json").toFile());
    }

    public void initializeClient() {
        this.initializeConfigs();
        ClientManager.instance();
        OpusManager.opusNativeCheck();
        if (Platform.isMac()) {
            if (!VersionCheck.isMacOSNativeCompatible()) {
                Voicechat.LOGGER.warn("Your MacOS version is incompatible with {}", CommonCompatibilityManager.INSTANCE.getModName());
            }
            if (!VoicechatClient.CLIENT_CONFIG.javaMicrophoneImplementation.get().booleanValue()) {
                VoicechatClient.CLIENT_CONFIG.javaMicrophoneImplementation.set(true).save();
            }
        }
    }

    private void fixVolumeConfig() {
        Path oldLocation = Voicechat.getConfigFolder().resolve("voicechat-volumes.properties");
        Path newLocation = Voicechat.getVoicechatConfigFolder().resolve("voicechat-volumes.properties");
        if (!newLocation.toFile().exists() && oldLocation.toFile().exists()) {
            try {
                Files.move(oldLocation, newLocation, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException e) {
                Voicechat.LOGGER.error("Failed to move volumes config", e);
            }
        }
    }
}
