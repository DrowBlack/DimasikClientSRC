package net.minecraft.network.status.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.lang.reflect.Type;
import net.minecraft.client.network.status.IClientStatusNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;

public class SServerInfoPacket
implements IPacket<IClientStatusNetHandler> {
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter((Type)((Object)ServerStatusResponse.Version.class), new ServerStatusResponse.Version.Serializer()).registerTypeAdapter((Type)((Object)ServerStatusResponse.Players.class), new ServerStatusResponse.Players.Serializer()).registerTypeAdapter((Type)((Object)ServerStatusResponse.class), new ServerStatusResponse.Serializer()).registerTypeHierarchyAdapter(ITextComponent.class, new ITextComponent.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
    private ServerStatusResponse response;

    public SServerInfoPacket() {
    }

    public SServerInfoPacket(ServerStatusResponse responseIn) {
        this.response = responseIn;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.response = JSONUtils.fromJson(GSON, buf.readString(Short.MAX_VALUE), ServerStatusResponse.class);
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeString(GSON.toJson(this.response));
    }

    @Override
    public void processPacket(IClientStatusNetHandler handler) {
        handler.handleServerInfo(this);
    }

    public ServerStatusResponse getResponse() {
        return this.response;
    }
}
