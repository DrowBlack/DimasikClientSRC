package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.net.AddCategoryPacket;
import dimasik.managers.mods.voicechat.net.AddGroupPacket;
import dimasik.managers.mods.voicechat.net.Channel;
import dimasik.managers.mods.voicechat.net.CreateGroupPacket;
import dimasik.managers.mods.voicechat.net.JoinGroupPacket;
import dimasik.managers.mods.voicechat.net.JoinedGroupPacket;
import dimasik.managers.mods.voicechat.net.LeaveGroupPacket;
import dimasik.managers.mods.voicechat.net.Packet;
import dimasik.managers.mods.voicechat.net.PlayerStatePacket;
import dimasik.managers.mods.voicechat.net.PlayerStatesPacket;
import dimasik.managers.mods.voicechat.net.RemoveCategoryPacket;
import dimasik.managers.mods.voicechat.net.RemoveGroupPacket;
import dimasik.managers.mods.voicechat.net.RequestSecretPacket;
import dimasik.managers.mods.voicechat.net.SecretPacket;
import dimasik.managers.mods.voicechat.net.UpdateStatePacket;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SCustomPayloadPlayPacket;
import net.minecraft.server.MinecraftServer;

public abstract class NetManager {
    public Channel<UpdateStatePacket> updateStateChannel;
    public Channel<PlayerStatePacket> playerStateChannel;
    public Channel<PlayerStatesPacket> playerStatesChannel;
    public Channel<SecretPacket> secretChannel;
    public Channel<RequestSecretPacket> requestSecretChannel;
    public Channel<AddGroupPacket> addGroupChannel;
    public Channel<RemoveGroupPacket> removeGroupChannel;
    public Channel<JoinGroupPacket> joinGroupChannel;
    public Channel<CreateGroupPacket> createGroupChannel;
    public Channel<LeaveGroupPacket> leaveGroupChannel;
    public Channel<JoinedGroupPacket> joinedGroupChannel;
    public Channel<AddCategoryPacket> addCategoryChannel;
    public Channel<RemoveCategoryPacket> removeCategoryChannel;

    public void init() {
        this.updateStateChannel = this.registerReceiver(UpdateStatePacket.class, false, true);
        this.playerStateChannel = this.registerReceiver(PlayerStatePacket.class, true, false);
        this.playerStatesChannel = this.registerReceiver(PlayerStatesPacket.class, true, false);
        this.secretChannel = this.registerReceiver(SecretPacket.class, true, false);
        this.requestSecretChannel = this.registerReceiver(RequestSecretPacket.class, false, true);
        this.addGroupChannel = this.registerReceiver(AddGroupPacket.class, true, false);
        this.removeGroupChannel = this.registerReceiver(RemoveGroupPacket.class, true, false);
        this.joinGroupChannel = this.registerReceiver(JoinGroupPacket.class, false, true);
        this.createGroupChannel = this.registerReceiver(CreateGroupPacket.class, false, true);
        this.leaveGroupChannel = this.registerReceiver(LeaveGroupPacket.class, false, true);
        this.joinedGroupChannel = this.registerReceiver(JoinedGroupPacket.class, true, false);
        this.addCategoryChannel = this.registerReceiver(AddCategoryPacket.class, true, false);
        this.removeCategoryChannel = this.registerReceiver(RemoveCategoryPacket.class, true, false);
    }

    public abstract <T extends Packet<T>> Channel<T> registerReceiver(Class<T> var1, boolean var2, boolean var3);

    public static void sendToClient(ServerPlayerEntity player, Packet<?> packet) {
        try {
            if (player == null || player.connection == null) {
                return;
            }
            PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            packet.toBytes(buffer);
            SCustomPayloadPlayPacket customPacket = new SCustomPayloadPlayPacket(packet.getIdentifier(), buffer);
            player.connection.sendPacket(customPacket);
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to send packet {} to client: {}", packet.getIdentifier(), e.getMessage());
        }
    }

    public static interface ServerReceiver<T extends Packet<T>> {
        public void onPacket(MinecraftServer var1, ServerPlayerEntity var2, ServerPlayNetHandler var3, T var4);
    }
}
