package com.mojang.bridge.game;

import java.util.UUID;

public interface GameSession {
    public int getPlayerCount();

    public boolean isRemoteServer();

    public String getDifficulty();

    public String getGameMode();

    public UUID getSessionId();
}
