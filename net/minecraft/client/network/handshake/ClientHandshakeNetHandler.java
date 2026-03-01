package net.minecraft.client.network.handshake;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.IHandshakeNetHandler;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

public class ClientHandshakeNetHandler
implements IHandshakeNetHandler {
    private final MinecraftServer server;
    private final NetworkManager networkManager;

    public ClientHandshakeNetHandler(MinecraftServer mcServerIn, NetworkManager networkManagerIn) {
        this.server = mcServerIn;
        this.networkManager = networkManagerIn;
    }

    @Override
    public void processHandshake(CHandshakePacket packetIn) {
        this.networkManager.setConnectionState(packetIn.getRequestedState());
        this.networkManager.setNetHandler(new ServerLoginNetHandler(this.server, this.networkManager));
    }

    @Override
    public void onDisconnect(ITextComponent reason) {
    }

    @Override
    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }
}
