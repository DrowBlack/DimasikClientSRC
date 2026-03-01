package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.ServerLevel;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import java.util.Objects;
import net.minecraft.world.server.ServerWorld;

public class ServerLevelImpl
implements ServerLevel {
    private final ServerWorld serverLevel;

    public ServerLevelImpl(ServerWorld serverLevel) {
        this.serverLevel = serverLevel;
    }

    @Override
    public Object getServerLevel() {
        return CommonCompatibilityManager.INSTANCE.createRawApiLevel(this.serverLevel);
    }

    public ServerWorld getRawServerLevel() {
        return this.serverLevel;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ServerLevelImpl that = (ServerLevelImpl)object;
        return Objects.equals(this.serverLevel, that.serverLevel);
    }

    public int hashCode() {
        return this.serverLevel != null ? this.serverLevel.hashCode() : 0;
    }
}
