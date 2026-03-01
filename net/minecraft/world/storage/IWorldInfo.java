package net.minecraft.world.storage;

import net.minecraft.crash.CrashReportCategory;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public interface IWorldInfo {
    public int getSpawnX();

    public int getSpawnY();

    public int getSpawnZ();

    public float getSpawnAngle();

    public long getGameTime();

    public long getDayTime();

    public boolean isThundering();

    public boolean isRaining();

    public void setRaining(boolean var1);

    public boolean isHardcore();

    public GameRules getGameRulesInstance();

    public Difficulty getDifficulty();

    public boolean isDifficultyLocked();

    default public void addToCrashReport(CrashReportCategory category) {
        category.addDetail("Level spawn location", () -> CrashReportCategory.getCoordinateInfo(this.getSpawnX(), this.getSpawnY(), this.getSpawnZ()));
        category.addDetail("Level time", () -> String.format("%d game time, %d day time", this.getGameTime(), this.getDayTime()));
    }
}
