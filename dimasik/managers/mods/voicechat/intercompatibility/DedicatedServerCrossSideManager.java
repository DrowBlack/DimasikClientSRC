package dimasik.managers.mods.voicechat.intercompatibility;

import com.sun.jna.Platform;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.intercompatibility.CrossSideManager;
import dimasik.managers.mods.voicechat.macos.VersionCheck;
import net.minecraft.server.MinecraftServer;

public class DedicatedServerCrossSideManager
extends CrossSideManager {
    @Override
    public int getMtuSize() {
        return Voicechat.SERVER_CONFIG.voiceChatMtuSize.get();
    }

    @Override
    public boolean useNatives() {
        if (Platform.isMac() && !VersionCheck.isMacOSNativeCompatible()) {
            return false;
        }
        return Voicechat.SERVER_CONFIG.useNatives.get();
    }

    @Override
    public boolean shouldRunVoiceChatServer(MinecraftServer server) {
        return true;
    }
}
