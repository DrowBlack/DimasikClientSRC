package com.mojang.bridge.launcher;

import com.mojang.bridge.launcher.Launcher;

public interface LauncherProvider {
    public Launcher createLauncher();
}
