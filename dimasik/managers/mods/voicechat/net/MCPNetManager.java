package dimasik.managers.mods.voicechat.net;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.net.Channel;
import dimasik.managers.mods.voicechat.net.ClientServerChannel;
import dimasik.managers.mods.voicechat.net.NetManager;
import dimasik.managers.mods.voicechat.net.Packet;
import dimasik.managers.mods.voicechat.net.RequestSecretPacket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;

public class MCPNetManager
extends NetManager {
    private static final Map<ResourceLocation, BiConsumer<PacketBuffer, NetworkContext>> PACKET_HANDLERS = new HashMap<ResourceLocation, BiConsumer<PacketBuffer, NetworkContext>>();

    @Override
    public <T extends Packet<T>> Channel<T> registerReceiver(Class<T> packetType, boolean toClient, boolean toServer) {
        ClientServerChannel channel = new ClientServerChannel();
        try {
            Packet dummyPacket = (Packet)packetType.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            ResourceLocation packetId = dummyPacket.getIdentifier();
            if (packetId == null) {
                throw new IllegalArgumentException("Packet identifier cannot be null for " + packetType.getSimpleName());
            }
            PACKET_HANDLERS.put(packetId, (buffer, context) -> {
                try {
                    Packet packet = (Packet)packetType.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                    packet.fromBytes((PacketBuffer)buffer);
                    if (toServer && context.isServerSide()) {
                        ServerPlayNetHandler handler = context.getServerHandler();
                        if (handler != null && handler.player != null) {
                            if (!Voicechat.SERVER.isCompatible(handler.player) && !packetType.equals(RequestSecretPacket.class)) {
                                Voicechat.LOGGER.warn("Incompatible client for packet {}", packetId);
                                return;
                            }
                            channel.onServerPacket(handler.player.getServer(), handler.player, handler.player.connection, packet);
                        } else {
                            Voicechat.LOGGER.warn("Invalid server handler for packet {}", packetId);
                        }
                    } else if (toClient && !context.isServerSide()) {
                        this.onClientPacket(channel, packet);
                    } else {
                        Voicechat.LOGGER.warn("Packet {} not allowed on {} side (toClient={}, toServer={})", packetId, context.isServerSide() ? "server" : "client", toClient, toServer);
                    }
                }
                catch (Exception e) {
                    Voicechat.LOGGER.error("Failed to process packet {}: {}", packetId, e.getMessage());
                }
            });
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to register packet receiver for " + packetType.getSimpleName(), e);
        }
        return channel;
    }

    private <T extends Packet<T>> void onClientPacket(ClientServerChannel<T> channel, T packet) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft != null && minecraft.getConnection() != null) {
            minecraft.execute(() -> channel.onClientPacket(minecraft, minecraft.getConnection(), packet));
        } else {
            Voicechat.LOGGER.warn("Client connection is null when processing packet {}", packet.getIdentifier());
        }
    }

    public static void handlePacket(ResourceLocation packetId, PacketBuffer buffer, NetworkContext context) {
        String side = context.isServerSide() ? "server" : "client";
        BiConsumer<PacketBuffer, NetworkContext> handler = PACKET_HANDLERS.get(packetId);
        if (handler != null) {
            handler.accept(buffer, context);
        } else {
            Voicechat.LOGGER.warn("No handler found for packet {} on {}", packetId, side);
        }
    }

    public static class NetworkContext {
        private final NetworkManager networkManager;
        private final boolean isServerSide;
        private final ServerPlayNetHandler serverHandler;

        public NetworkContext(NetworkManager networkManager, boolean isServerSide, ServerPlayNetHandler serverHandler) {
            this.networkManager = networkManager;
            this.isServerSide = isServerSide;
            this.serverHandler = serverHandler;
        }

        public boolean isServerSide() {
            return this.isServerSide;
        }

        @Generated
        public NetworkManager getNetworkManager() {
            return this.networkManager;
        }

        @Generated
        public ServerPlayNetHandler getServerHandler() {
            return this.serverHandler;
        }
    }
}
