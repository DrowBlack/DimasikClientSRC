package com.mojang.bridge.launcher;

import com.mojang.bridge.game.GameSession;

public interface SessionEventListener {
    public static final SessionEventListener NONE = new SessionEventListener(){};

    default public void onStartGameSession(GameSession session) {
    }

    default public void onLeaveGameSession(GameSession session) {
    }
}
