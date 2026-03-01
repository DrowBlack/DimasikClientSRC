package dimasik.managers.config;

import dimasik.helpers.interfaces.IFinderModules;
import dimasik.helpers.interfaces.IManager;
import dimasik.managers.config.api.Config;
import dimasik.managers.config.main.AltConfig;
import dimasik.managers.config.main.ClientConfig;
import dimasik.managers.config.main.DraggableConfig;
import dimasik.managers.config.main.FriendConfig;
import dimasik.managers.config.main.MacroConfig;
import dimasik.managers.config.main.ModuleConfig;
import dimasik.managers.config.main.StaffConfig;
import dimasik.managers.config.main.WayConfig;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class ConfigManagers
extends ArrayList<Config>
implements IManager<Config>,
IFinderModules<Config> {
    protected final String suffix = ".sk";

    public ConfigManagers() {
        this.init();
    }

    @Override
    public void init() {
        this.register(new ModuleConfig());
        this.register(new FriendConfig());
        this.register(new DraggableConfig());
        this.register(new ClientConfig());
        this.register(new MacroConfig());
        this.register(new MacroConfig());
        this.register(new StaffConfig());
        this.register(new AltConfig());
        this.register(new WayConfig());
    }

    @Override
    public void register(Config config) {
        this.add(config);
    }

    public void load(String name) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        this.forEach(Config::fastLoad);
    }

    public void save(String name) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        this.forEach(Config::fastSave);
    }

    public void load(String name, String path) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setPath(new File(Minecraft.getInstance().gameDir, path));
        this.forEach(Config::fastLoad);
    }

    public void save(String name, String path) {
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setName(name);
        ((ModuleConfig)this.findClass(ModuleConfig.class)).setPath(new File(Minecraft.getInstance().gameDir, path));
        this.forEach(Config::fastSave);
    }

    @Override
    public <T extends Config> T findName(String name) {
        return (T)((Config)this.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Config> T findClass(Class<T> clazz) {
        return (T)((Config)this.stream().filter(module -> module.getClass() == clazz).findAny().orElse(null));
    }

    public List<String> getConfigs() {
        File path = new File(Minecraft.getInstance().gameDir, "dimasik/configs/custom");
        File[] arrFiles = path.listFiles();
        assert (arrFiles != null);
        ArrayList<String> arrStrings = new ArrayList<String>();
        for (File file : arrFiles) {
            if (!file.getName().endsWith(".sk")) continue;
            String name = file.getName();
            arrStrings.add(name.substring(0, name.length() - 3));
        }
        return arrStrings;
    }
}
