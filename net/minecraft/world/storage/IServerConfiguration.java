package net.minecraft.world.storage;

import com.mojang.serialization.Lifecycle;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.datafix.codec.DatapackCodec;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.IServerWorldInfo;

public interface IServerConfiguration {
    public DatapackCodec getDatapackCodec();

    public void setDatapackCodec(DatapackCodec var1);

    public boolean isModded();

    public Set<String> getServerBranding();

    public void addServerBranding(String var1, boolean var2);

    default public void addToCrashReport(CrashReportCategory category) {
        category.addDetail("Known server brands", () -> String.join((CharSequence)", ", this.getServerBranding()));
        category.addDetail("Level was modded", () -> Boolean.toString(this.isModded()));
        category.addDetail("Level storage version", () -> {
            int i = this.getStorageVersionId();
            return String.format("0x%05X - %s", i, this.getStorageVersionName(i));
        });
    }

    default public String getStorageVersionName(int storageVersionId) {
        switch (storageVersionId) {
            case 19132: {
                return "McRegion";
            }
            case 19133: {
                return "Anvil";
            }
        }
        return "Unknown?";
    }

    @Nullable
    public CompoundNBT getCustomBossEventData();

    public void setCustomBossEventData(@Nullable CompoundNBT var1);

    public IServerWorldInfo getServerWorldInfo();

    public WorldSettings getWorldSettings();

    public CompoundNBT serialize(DynamicRegistries var1, @Nullable CompoundNBT var2);

    public boolean isHardcore();

    public int getStorageVersionId();

    public String getWorldName();

    public GameType getGameType();

    public void setGameType(GameType var1);

    public boolean areCommandsAllowed();

    public Difficulty getDifficulty();

    public void setDifficulty(Difficulty var1);

    public boolean isDifficultyLocked();

    public void setDifficultyLocked(boolean var1);

    public GameRules getGameRulesInstance();

    public CompoundNBT getHostPlayerNBT();

    public CompoundNBT getDragonFightData();

    public void setDragonFightData(CompoundNBT var1);

    public DimensionGeneratorSettings getDimensionGeneratorSettings();

    public Lifecycle getLifecycle();
}
