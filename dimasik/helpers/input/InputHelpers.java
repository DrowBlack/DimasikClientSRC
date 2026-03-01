package dimasik.helpers.input;

import dimasik.Load;
import dimasik.events.main.input.EventInput;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.module.Module;
import dimasik.managers.module.option.api.Option;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.notification.api.Notification;
import dimasik.managers.notification.api.Pattern;
import dimasik.utils.client.SoundUtils;

public class InputHelpers
implements IFastAccess {
    public InputHelpers(int key) {
        if (key >= 0) {
            boolean flag = InputHelpers.mc.currentScreen == null || key != 341;
            for (Module m : Load.getInstance().getHooks().getModuleManagers()) {
                if (m.getCurrentKey() == key) {
                    m.toggle();
                }
                for (Option<?> option : m.getSettingList()) {
                    CheckboxOption checkboxOption;
                    if (option instanceof CheckboxOption && (checkboxOption = (CheckboxOption)option).getKey() == key && checkboxOption.getVisible().getAsBoolean()) {
                        checkboxOption.setValue((Boolean)checkboxOption.getValue() == false);
                        if (((Boolean)checkboxOption.getValue()).booleanValue()) {
                            SoundUtils.playSound("enable");
                            Load.getInstance().getHooks().getNotificationManagers().register(new Notification(checkboxOption.getVisualName() + " has been enabled", "Option Info", 1500L, checkboxOption.getModule()).setPattern(Pattern.ENABLE));
                        } else {
                            SoundUtils.playSound("disable");
                            Load.getInstance().getHooks().getNotificationManagers().register(new Notification(checkboxOption.getVisualName() + " has been disabled", "Option Info", 1500L, checkboxOption.getModule()).setPattern(Pattern.DISABLE));
                        }
                    }
                    if (!(option instanceof MultiOption)) continue;
                    MultiOption multiOption = (MultiOption)option;
                    for (MultiOptionValue value : multiOption.getValues()) {
                        if (value.getKey() != key) continue;
                        value.setToggle(!value.isToggle());
                        if (value.isToggle()) {
                            SoundUtils.playSound("enable");
                            Load.getInstance().getHooks().getNotificationManagers().register(new Notification(value.getVisualName() + " has been enabled", "Option Info", 1500L, multiOption.getModule()).setPattern(Pattern.ENABLE));
                            continue;
                        }
                        SoundUtils.playSound("disable");
                        Load.getInstance().getHooks().getNotificationManagers().register(new Notification(value.getVisualName() + " has been disabled", "Option Info", 1500L, multiOption.getModule()).setPattern(Pattern.DISABLE));
                    }
                }
            }
            EventInput eventInput = new EventInput(key);
            Load.getInstance().getEvents().call(eventInput);
            Load.getInstance().getHooks().getMacroManagers().press(key);
        }
    }
}
