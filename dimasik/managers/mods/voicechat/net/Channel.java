package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.net.Packet;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.server.MinecraftServer;

public class Channel<T extends Packet<T>> {
    @Nullable
    private NetManager.ServerReceiver<T> serverListener;

    public void setServerListener(NetManager.ServerReceiver<T> packetReceiver) {
        this.serverListener = packetReceiver;
    }

    public void onServerPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetHandler handler, T packet) {
        server.execute(() -> {
            if (this.serverListener != null) {
                this.serverListener.onPacket(server, player, handler, (Packet)packet);
            }
        });
    }
}
