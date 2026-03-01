package dimasik.managers.mods.voicechat.intercompatibility;

import com.sun.jna.Platform;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.intercompatibility.CrossSideManager;
import dimasik.managers.mods.voicechat.macos.VersionCheck;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechatConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;

public class ClientCrossSideManager
extends CrossSideManager {
    @Override
    public int getMtuSize() {
        ClientVoicechatConnection connection;
        ClientVoicechat client = ClientManager.getClient();
        if (client != null && (connection = client.getConnection()) != null) {
            return connection.getData().getMtuSize();
        }
        return Voicechat.SERVER_CONFIG.voiceChatMtuSize.get();
    }

    @Override
    public boolean useNatives() {
        if (Platform.isMac() && !VersionCheck.isMacOSNativeCompatible()) {
            return false;
        }
        if (VoicechatClient.CLIENT_CONFIG == null) {
            return Voicechat.SERVER_CONFIG.useNatives.get();
        }
        return VoicechatClient.CLIENT_CONFIG.useNatives.get();
    }

    @Override
    public boolean shouldRunVoiceChatServer(MinecraftServer server) {
        return server instanceof DedicatedServer || VoicechatClient.CLIENT_CONFIG == null || VoicechatClient.CLIENT_CONFIG.runLocalServer.get() != false;
    }
}
