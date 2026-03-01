package dimasik.modules.misc;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.helpers.module.interfaces.StaffList;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.notification.api.Notification;
import dimasik.managers.notification.api.Pattern;
import java.util.HashMap;
import java.util.Map;

public class NotifSettings
extends Module {
    public final CheckboxOption staff = new CheckboxOption("\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0441\u0442\u0430\u0444\u0444\u0435", false);
    public final CheckboxOption donitem = new CheckboxOption("\u041f\u043e\u0434\u043d\u044f\u0442\u044b\u0435 \u0446\u0435\u043d\u043d\u043e\u0441\u0442\u0438", false);
    private final Map<String, StaffList.Status> prevStaffStatus = new HashMap<String, StaffList.Status>();
    private final EventListener<EventUpdate> update = this::onUpdate;

    public NotifSettings() {
        super("Notif. Settings", Category.MISC);
        this.settings(this.staff, this.donitem);
    }

    public void onUpdate(EventUpdate event) {
        StaffList staffList;
        if (((Boolean)this.staff.getValue()).booleanValue() && (staffList = (StaffList)Load.getInstance().getHooks().getDraggableManagers().findName("StaffList")) != null) {
            for (StaffList.Staff s : staffList.getStaffPlayers()) {
                Notification notif;
                String name = s.getName();
                StaffList.Status current = s.getStatus();
                StaffList.Status prev = this.prevStaffStatus.get(name);
                if (prev != null && prev == current) continue;
                this.prevStaffStatus.put(name, current);
                if (current == StaffList.Status.NONE) {
                    notif = new Notification("\u0421\u0442\u0430\u0444\u0444 " + name + " \u0430\u043a\u0442\u0438\u0432\u0435\u043d!", "Staff Info", 2000L, Load.getInstance().getHooks().getModuleManagers().getInterfaces()).setPattern(Pattern.ENABLE);
                } else {
                    if (current != StaffList.Status.VANISHED) continue;
                    notif = new Notification("\u0421\u0442\u0430\u0444\u0444 " + name + " \u0432 \u0432\u0430\u043d\u0438\u0448\u0435!", "Staff Info", 2000L, Load.getInstance().getHooks().getModuleManagers().getInterfaces()).setPattern(Pattern.ERROR);
                }
                Load.getInstance().getHooks().getNotificationManagers().register(notif);
            }
        }
    }
}
