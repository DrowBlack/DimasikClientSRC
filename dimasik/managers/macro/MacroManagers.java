package dimasik.managers.macro;

import dimasik.Load;
import dimasik.helpers.interfaces.IFinderModules;
import dimasik.helpers.interfaces.IManager;
import dimasik.managers.config.main.MacroConfig;
import dimasik.managers.macro.api.Macro;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class MacroManagers
extends ArrayList<Macro>
implements IManager<Macro>,
IFinderModules<Macro> {
    public void press(int key) {
        try {
            this.stream().filter(macro -> macro.getKey() == key).forEach((? super T macro) -> Minecraft.getInstance().player.sendChatMessage(macro.getMessage()));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String message, int key) {
        this.register(new Macro(message, key));
        ((MacroConfig)Load.getInstance().getHooks().getConfigManagers().findClass(MacroConfig.class)).fastSave();
    }

    public void delete(int key) {
        this.removeIf(macro -> macro.getKey() == key);
        ((MacroConfig)Load.getInstance().getHooks().getConfigManagers().findClass(MacroConfig.class)).fastSave();
    }

    public void clears() {
        this.clear();
        ((MacroConfig)Load.getInstance().getHooks().getConfigManagers().findClass(MacroConfig.class)).fastSave();
    }

    @Override
    public <T extends Macro> T findName(String name) {
        return (T)((Macro)this.stream().filter(macro -> macro.getMessage().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Macro> T findClass(Class<T> clazz) {
        return (T)((Macro)this.stream().filter(macro -> macro.getClass() == clazz).findAny().orElse(null));
    }

    @Override
    public void register(Macro macro) {
        this.add(macro);
    }
}
