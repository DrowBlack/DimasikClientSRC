package dimasik.managers.config.main;

import dimasik.Load;
import dimasik.managers.config.api.Config;

public class StaffConfig
extends Config {
    public StaffConfig() {
        super("staff", "dimasik/client");
    }

    @Override
    protected void save() {
        this.write(Load.getInstance().getHooks().getStaffManagers().get().toString().replace("[", "").replace("]", ""));
    }

    @Override
    protected void load() {
        String info = this.read();
        String[] staffs = info.split(", ");
        Load.getInstance().getHooks().getStaffManagers().clear();
        for (String s : staffs) {
            Load.getInstance().getHooks().getStaffManagers().add(s);
        }
    }
}
