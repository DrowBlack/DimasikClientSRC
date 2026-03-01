package net.minecraft.client.network.status;

import net.minecraft.network.INetHandler;
import net.minecraft.network.status.server.SPongPacket;
import net.minecraft.network.status.server.SServerInfoPacket;

public interface IClientStatusNetHandler
extends INetHandler {
    public void handleServerInfo(SServerInfoPacket var1);

    public void handlePong(SPongPacket var1);
}
