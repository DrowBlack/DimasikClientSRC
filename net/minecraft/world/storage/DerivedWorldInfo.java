package net.minecraft.world.storage;

import java.util.UUID;
import net.minecraft.command.TimerCallbackManager;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.IServerWorldInfo;

public class DerivedWorldInfo
implements IServerWorldInfo {
    private final IServerConfiguration configuration;
    private final IServerWorldInfo delegate;

    public DerivedWorldInfo(IServerConfiguration configuration, IServerWorldInfo delegate) {
        this.configuration = configuration;
        this.delegate = delegate;
    }

    @Override
    public int getSpawnX() {
        return this.delegate.getSpawnX();
    }

    @Override
    public int getSpawnY() {
        return this.delegate.getSpawnY();
    }

    @Override
    public int getSpawnZ() {
        return this.delegate.getSpawnZ();
    }

    @Override
    public float getSpawnAngle() {
        return this.delegate.getSpawnAngle();
    }

    @Override
    public long getGameTime() {
        return this.delegate.getGameTime();
    }

    @Override
    public long getDayTime() {
        return this.delegate.getDayTime();
    }

    @Override
    public String getWorldName() {
        return this.configuration.getWorldName();
    }

    @Override
    public int getClearWeatherTime() {
        return this.delegate.getClearWeatherTime();
    }

    @Override
    public void setClearWeatherTime(int time) {
    }

    @Override
    public boolean isThundering() {
        return this.delegate.isThundering();
    }

    @Override
    public int getThunderTime() {
        return this.delegate.getThunderTime();
    }

    @Override
    public boolean isRaining() {
        return this.delegate.isRaining();
    }

    @Override
    public int getRainTime() {
        return this.delegate.getRainTime();
    }

    @Override
    public GameType getGameType() {
        return this.configuration.getGameType();
    }

    @Override
    public void setSpawnX(int x) {
    }

    @Override
    public void setSpawnY(int y) {
    }

    @Override
    public void setSpawnZ(int z) {
    }

    @Override
    public void setSpawnAngle(float angle) {
    }

    @Override
    public void setGameTime(long time) {
    }

    @Override
    public void setDayTime(long time) {
    }

    @Override
    public void setSpawn(BlockPos spawnPoint, float angle) {
    }

    @Override
    public void setThundering(boolean thunderingIn) {
    }

    @Override
    public void setThunderTime(int time) {
    }

    @Override
    public void setRaining(boolean isRaining) {
    }

    @Override
    public void setRainTime(int time) {
    }

    @Override
    public void setGameType(GameType type) {
    }

    @Override
    public boolean isHardcore() {
        return this.configuration.isHardcore();
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.configuration.areCommandsAllowed();
    }

    @Override
    public boolean isInitialized() {
        return this.delegate.isInitialized();
    }

    @Override
    public void setInitialized(boolean initializedIn) {
    }

    @Override
    public GameRules getGameRulesInstance() {
        return this.configuration.getGameRulesInstance();
    }

    @Override
    public WorldBorder.Serializer getWorldBorderSerializer() {
        return this.delegate.getWorldBorderSerializer();
    }

    @Override
    public void setWorldBorderSerializer(WorldBorder.Serializer serializer) {
    }

    @Override
    public Difficulty getDifficulty() {
        return this.configuration.getDifficulty();
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.configuration.isDifficultyLocked();
    }

    @Override
    public TimerCallbackManager<MinecraftServer> getScheduledEvents() {
        return this.delegate.getScheduledEvents();
    }

    @Override
    public int getWanderingTraderSpawnDelay() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnDelay(int delay) {
    }

    @Override
    public int getWanderingTraderSpawnChance() {
        return 0;
    }

    @Override
    public void setWanderingTraderSpawnChance(int chance) {
    }

    @Override
    public void setWanderingTraderID(UUID id) {
    }

    @Override
    public void addToCrashReport(CrashReportCategory category) {
        category.addDetail("Derived", true);
        this.delegate.addToCrashReport(category);
    }
}
