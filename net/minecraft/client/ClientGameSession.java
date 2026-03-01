package net.minecraft.client;

import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.world.ClientWorld;

public class ClientGameSession
implements GameSession {
    private final int playerCount;
    private final boolean remoteServer;
    private final String difficulty;
    private final String gameMode;
    private final UUID sessionId;

    public ClientGameSession(ClientWorld world, ClientPlayerEntity player, ClientPlayNetHandler netHandler) {
        this.playerCount = netHandler.getPlayerInfoMap().size();
        this.remoteServer = !netHandler.getNetworkManager().isLocalChannel();
        this.difficulty = world.getDifficulty().getTranslationKey();
        NetworkPlayerInfo networkplayerinfo = netHandler.getPlayerInfo(player.getUniqueID());
        this.gameMode = networkplayerinfo != null ? networkplayerinfo.getGameType().getName() : "unknown";
        this.sessionId = netHandler.getSessionId();
    }

    @Override
    public int getPlayerCount() {
        return this.playerCount;
    }

    @Override
    public boolean isRemoteServer() {
        return this.remoteServer;
    }

    @Override
    public String getDifficulty() {
        return this.difficulty;
    }

    @Override
    public String getGameMode() {
        return this.gameMode;
    }

    @Override
    public UUID getSessionId() {
        return this.sessionId;
    }
}
