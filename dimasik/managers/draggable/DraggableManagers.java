package dimasik.managers.draggable;

import dimasik.helpers.interfaces.IFinderModules;
import dimasik.helpers.interfaces.IManager;
import dimasik.helpers.module.interfaces.ArmorHud;
import dimasik.helpers.module.interfaces.Cooldowns;
import dimasik.helpers.module.interfaces.Information;
import dimasik.helpers.module.interfaces.KeyBinds;
import dimasik.helpers.module.interfaces.NearList;
import dimasik.helpers.module.interfaces.PotionList;
import dimasik.helpers.module.interfaces.StaffList;
import dimasik.helpers.module.interfaces.TargetHud;
import dimasik.helpers.module.interfaces.WaterMark;
import dimasik.managers.draggable.api.Component;
import java.util.ArrayList;
import ru.dreamix.class2native.CompileNativeCalls;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
public class DraggableManagers
extends ArrayList<Component>
implements IManager<Component>,
IFinderModules<Component> {
    @CompileNativeCalls
    public DraggableManagers() {
        this.init();
    }

    @Override
    @CompileNativeCalls
    public void init() {
        this.register(new PotionList());
        this.register(new NearList());
        this.register(new KeyBinds());
        this.register(new Cooldowns());
        this.register(new StaffList());
        this.register(new WaterMark());
        this.register(new ArmorHud());
        this.register(new Information());
        this.register(new TargetHud());
    }

    @Override
    public void register(Component component) {
        this.add(component);
    }

    @Override
    public <T extends Component> T findName(String name) {
        return (T)((Component)this.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Component> T findClass(Class<T> clazz) {
        return (T)((Component)this.stream().filter(module -> module.getClass() == clazz).findAny().orElse(null));
    }
}
