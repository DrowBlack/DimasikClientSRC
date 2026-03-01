package dimasik.managers.mods.voicechat.intercompatibility;

import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.intercompatibility.DedicatedServerCrossSideManager;
import net.minecraft.server.MinecraftServer;

public abstract class CrossSideManager {
    private static CrossSideManager instance;

    public static CrossSideManager get() {
        if (instance == null) {
            if (CommonCompatibilityManager.INSTANCE.isDedicatedServer()) {
                instance = new DedicatedServerCrossSideManager();
            } else {
                try {
                    Class<?> crossSideManagerClass = Class.forName("dimasik.managers.mods.voicechat.intercompatibility.ClientCrossSideManager");
                    instance = (CrossSideManager)crossSideManagerClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return instance;
    }

    public abstract int getMtuSize();

    public abstract boolean useNatives();

    public abstract boolean shouldRunVoiceChatServer(MinecraftServer var1);
}
