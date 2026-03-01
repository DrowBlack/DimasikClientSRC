package com.mojang.bridge.game;

import java.util.Date;

public interface GameVersion {
    public String getId();

    public String getName();

    public String getReleaseTarget();

    public int getWorldVersion();

    public int getProtocolVersion();

    public int getPackVersion();

    public Date getBuildTime();

    public boolean isStable();
}
