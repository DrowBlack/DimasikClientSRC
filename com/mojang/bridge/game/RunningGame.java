package com.mojang.bridge.game;

import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.launcher.SessionEventListener;

public interface RunningGame {
    public GameVersion getVersion();

    public Language getSelectedLanguage();

    public GameSession getCurrentSession();

    public PerformanceMetrics getPerformanceMetrics();

    public void setSessionEventListener(SessionEventListener var1);
}
