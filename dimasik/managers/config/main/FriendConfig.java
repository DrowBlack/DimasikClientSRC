package dimasik.managers.config.main;

import dimasik.Load;
import dimasik.managers.config.api.Config;

public class FriendConfig
extends Config {
    public FriendConfig() {
        super("friend", "dimasik/client");
    }

    @Override
    protected void save() {
        this.write(Load.getInstance().getHooks().getFriendManagers().get().toString().replace("[", "").replace("]", ""));
    }

    @Override
    protected void load() {
        String info = this.read();
        String[] friends = info.split(", ");
        Load.getInstance().getHooks().getFriendManagers().clear();
        for (String s : friends) {
            Load.getInstance().getHooks().getFriendManagers().add(s);
        }
    }
}
