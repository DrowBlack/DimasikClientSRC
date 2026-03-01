package net.minecraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftVersion
implements GameVersion {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final GameVersion GAME_VERSION = new MinecraftVersion();
    private final String id;
    private final String name;
    private final boolean stable;
    private final int worldVersion;
    private final int protocolVersion;
    private final int packVersion;
    private final Date buildTime;
    private final String releaseTarget;

    private MinecraftVersion() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "1.16.5";
        this.stable = true;
        this.worldVersion = 2586;
        this.protocolVersion = SharedConstants.getProtocolVersion();
        this.packVersion = 6;
        this.buildTime = new Date();
        this.releaseTarget = "1.16.5";
    }

    private MinecraftVersion(JsonObject json) {
        this.id = JSONUtils.getString(json, "id");
        this.name = JSONUtils.getString(json, "name");
        this.releaseTarget = JSONUtils.getString(json, "release_target");
        this.stable = JSONUtils.getBoolean(json, "stable");
        this.worldVersion = JSONUtils.getInt(json, "world_version");
        this.protocolVersion = JSONUtils.getInt(json, "protocol_version");
        this.packVersion = JSONUtils.getInt(json, "pack_version");
        this.buildTime = Date.from(ZonedDateTime.parse(JSONUtils.getString(json, "build_time")).toInstant());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static GameVersion load() {
        try (InputStream inputstream = MinecraftVersion.class.getResourceAsStream("/version.json");){
            MinecraftVersion minecraftversion;
            if (inputstream == null) {
                LOGGER.warn("Missing version information!");
                GameVersion gameVersion = GAME_VERSION;
                return gameVersion;
            }
            try (InputStreamReader inputstreamreader = new InputStreamReader(inputstream);){
                minecraftversion = new MinecraftVersion(JSONUtils.fromJson(inputstreamreader));
            }
            MinecraftVersion minecraftVersion = minecraftversion;
            return minecraftVersion;
        }
        catch (JsonParseException | IOException ioexception) {
            throw new IllegalStateException("Game version information is corrupt", ioexception);
        }
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getReleaseTarget() {
        return this.releaseTarget;
    }

    @Override
    public int getWorldVersion() {
        return this.worldVersion;
    }

    @Override
    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    @Override
    public int getPackVersion() {
        return this.packVersion;
    }

    @Override
    public Date getBuildTime() {
        return this.buildTime;
    }

    @Override
    public boolean isStable() {
        return this.stable;
    }
}
