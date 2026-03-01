package net.minecraft.client.multiplayer;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ServerData {
    public String serverName;
    public String serverIP;
    public ITextComponent populationInfo;
    public ITextComponent serverMOTD;
    public long pingToServer;
    public int version = SharedConstants.getVersion().getProtocolVersion();
    public ITextComponent gameVersion = new StringTextComponent(SharedConstants.getVersion().getName());
    public boolean pinged;
    public List<ITextComponent> playerList = Collections.emptyList();
    private ServerResourceMode resourceMode = ServerResourceMode.PROMPT;
    @Nullable
    private String serverIcon;
    @Nullable
    private String cachedResourcePackSha1;
    private boolean lanServer;

    public ServerData(String name, String ip, boolean isLan) {
        this.serverName = name;
        this.serverIP = ip;
        this.lanServer = isLan;
    }

    public CompoundNBT getNBTCompound() {
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putString("name", this.serverName);
        compoundnbt.putString("ip", this.serverIP);
        if (this.serverIcon != null) {
            compoundnbt.putString("icon", this.serverIcon);
        }
        if (this.resourceMode == ServerResourceMode.ENABLED) {
            compoundnbt.putBoolean("acceptTextures", true);
        } else if (this.resourceMode == ServerResourceMode.DISABLED) {
            compoundnbt.putBoolean("acceptTextures", false);
        }
        if (this.cachedResourcePackSha1 != null && !this.cachedResourcePackSha1.isEmpty()) {
            compoundnbt.putString("rpSha1", this.cachedResourcePackSha1);
        }
        return compoundnbt;
    }

    public ServerResourceMode getResourceMode() {
        return this.resourceMode;
    }

    public void setResourceMode(ServerResourceMode mode) {
        this.resourceMode = mode;
    }

    public static ServerData getServerDataFromNBTCompound(CompoundNBT nbtCompound) {
        ServerData serverdata = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"), false);
        if (nbtCompound.contains("icon", 8)) {
            serverdata.setBase64EncodedIconData(nbtCompound.getString("icon"));
        }
        if (nbtCompound.contains("acceptTextures", 1)) {
            if (nbtCompound.getBoolean("acceptTextures")) {
                serverdata.setResourceMode(ServerResourceMode.ENABLED);
            } else {
                serverdata.setResourceMode(ServerResourceMode.DISABLED);
            }
        } else {
            serverdata.setResourceMode(ServerResourceMode.PROMPT);
        }
        if (nbtCompound.contains("rpSha1", 8)) {
            serverdata.setCachedResourcePackSha1(nbtCompound.getString("rpSha1"));
        }
        return serverdata;
    }

    @Nullable
    public String getBase64EncodedIconData() {
        return this.serverIcon;
    }

    public void setBase64EncodedIconData(@Nullable String icon) {
        this.serverIcon = icon;
    }

    @Nullable
    public String getCachedResourcePackSha1() {
        return this.cachedResourcePackSha1;
    }

    public void setCachedResourcePackSha1(@Nullable String sha1) {
        this.cachedResourcePackSha1 = sha1;
    }

    public boolean isOnLAN() {
        return this.lanServer;
    }

    public void copyFrom(ServerData serverDataIn) {
        this.serverIP = serverDataIn.serverIP;
        this.serverName = serverDataIn.serverName;
        this.setResourceMode(serverDataIn.getResourceMode());
        this.serverIcon = serverDataIn.serverIcon;
        this.lanServer = serverDataIn.lanServer;
    }

    public static enum ServerResourceMode {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final ITextComponent motd;

        private ServerResourceMode(String name) {
            this.motd = new TranslationTextComponent("addServer.resourcePack." + name);
        }

        public ITextComponent getMotd() {
            return this.motd;
        }
    }
}
