package dimasik.modules.misc;

import dimasik.Load;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.config.main.ClientConfig;
import dimasik.managers.hook.main.ModuleManagers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.utils.client.ChatUtils;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Generated;
import net.optifine.shaders.Shaders;

public class UnHook
extends Module {
    private final List<Module> backup = new CopyOnWriteArrayList<Module>();

    public UnHook() {
        super("UnHook", Category.MISC);
    }

    @Override
    public void onEnabled() {
        assert (UnHook.mc.player != null);
        this.toggle();
        ClientManagers.update();
        ChatUtils.addClientMessage("\u0414\u043b\u044f \u0442\u043e\u0433\u043e \u0447\u0442\u043e\u0431\u044b \u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0447\u0438\u0442, \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435: " + ClientManagers.getRandom());
        ChatUtils.addClientMessage("\u0423 \u0432\u0430\u0441 \u0435\u0441\u0442\u044c 10 \u0441\u0435\u043a\u0443\u043d\u0434 \u0434\u043e \u043e\u0447\u0438\u0441\u0442\u043a\u0438 \u0447\u0430\u0442\u0430.");
        new Thread(() -> {
            try {
                Thread.sleep(10000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            UnHook.mc.ingameGUI.getChatGUI().clearChatMessages(true);
        }).start();
        ModuleManagers modules = Load.getInstance().getHooks().getModuleManagers();
        this.backup.clear();
        for (Module module : modules) {
            if (!module.isToggled() || module == this) continue;
            this.backup.add(module);
            module.toggle();
        }
        UnHook.mc.fileResourcepacks = new File(System.getenv("appdata") + "\\.minecraft\\resourcepacks");
        Shaders.shaderPacksDir = new File(System.getenv("appdata") + "\\.minecraft\\shaderpacks");
        ClientManagers.setUnHook(true);
        ((ClientConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ClientConfig.class)).fastSave();
    }

    @Generated
    public List<Module> getBackup() {
        return this.backup;
    }
}
