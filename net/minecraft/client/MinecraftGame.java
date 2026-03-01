package net.minecraft.client;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import net.minecraft.client.ClientGameSession;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.SharedConstants;

public class MinecraftGame
implements RunningGame {
    private final Minecraft gameInstance;
    @Nullable
    private final Launcher launcher;
    private SessionEventListener sessionListener = SessionEventListener.NONE;

    public MinecraftGame(Minecraft gameInstance) {
        this.gameInstance = gameInstance;
        this.launcher = Bridge.getLauncher();
        if (this.launcher != null) {
            this.launcher.registerGame(this);
        }
    }

    @Override
    public GameVersion getVersion() {
        return SharedConstants.getVersion();
    }

    @Override
    public Language getSelectedLanguage() {
        return this.gameInstance.getLanguageManager().getCurrentLanguage();
    }

    @Override
    @Nullable
    public GameSession getCurrentSession() {
        ClientWorld clientworld = this.gameInstance.world;
        return clientworld == null ? null : new ClientGameSession(clientworld, this.gameInstance.player, this.gameInstance.player.connection);
    }

    @Override
    public PerformanceMetrics getPerformanceMetrics() {
        FrameTimer frametimer = this.gameInstance.getFrameTimer();
        long i = Integer.MAX_VALUE;
        long j = Integer.MIN_VALUE;
        long k = 0L;
        for (long l : frametimer.getFrames()) {
            i = Math.min(i, l);
            j = Math.max(j, l);
            k += l;
        }
        return new MinecraftPerformanceMetrics((int)i, (int)j, (int)(k / (long)frametimer.getFrames().length), frametimer.getFrames().length);
    }

    @Override
    public void setSessionEventListener(SessionEventListener p_setSessionEventListener_1_) {
        this.sessionListener = p_setSessionEventListener_1_;
    }

    public void startGameSession() {
        this.sessionListener.onStartGameSession(this.getCurrentSession());
    }

    public void leaveGameSession() {
        this.sessionListener.onLeaveGameSession(this.getCurrentSession());
    }

    static class MinecraftPerformanceMetrics
    implements PerformanceMetrics {
        private final int minTime;
        private final int maxTime;
        private final int averageTime;
        private final int sampleCount;

        public MinecraftPerformanceMetrics(int minTime, int maxTime, int averageTime, int sampleCount) {
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.averageTime = averageTime;
            this.sampleCount = sampleCount;
        }

        @Override
        public int getMinTime() {
            return this.minTime;
        }

        @Override
        public int getMaxTime() {
            return this.maxTime;
        }

        @Override
        public int getAverageTime() {
            return this.averageTime;
        }

        @Override
        public int getSampleCount() {
            return this.sampleCount;
        }
    }
}
