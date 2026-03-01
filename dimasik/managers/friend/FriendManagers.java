package dimasik.managers.friend;

import dimasik.Load;
import dimasik.helpers.interfaces.IFinderModules;
import dimasik.helpers.interfaces.IManager;
import dimasik.managers.config.main.FriendConfig;
import dimasik.managers.friend.api.Friend;
import java.util.ArrayList;
import java.util.List;

public class FriendManagers
extends ArrayList<Friend>
implements IManager<Friend>,
IFinderModules<Friend> {
    public FriendManagers() {
        this.init();
    }

    @Override
    public void init() {
    }

    public void add(String friend) {
        this.register(new Friend(friend));
        ((FriendConfig)Load.getInstance().getHooks().getConfigManagers().findClass(FriendConfig.class)).fastSave();
    }

    public boolean is(String friend) {
        return this.stream().anyMatch(is -> is.getName().equals(friend));
    }

    public void remove(String friend) {
        this.removeIf(Friend2 -> Friend2.getName().equalsIgnoreCase(friend));
        ((FriendConfig)Load.getInstance().getHooks().getConfigManagers().findClass(FriendConfig.class)).fastSave();
    }

    public void clears() {
        this.clear();
        ((FriendConfig)Load.getInstance().getHooks().getConfigManagers().findClass(FriendConfig.class)).fastSave();
    }

    @Deprecated(forRemoval=true, since="3.0")
    public List<String> get() {
        ArrayList<String> friends = new ArrayList<String>();
        for (Friend friend : Load.getInstance().getHooks().getFriendManagers()) {
            String name = friend.getName();
            friends.add(name);
        }
        return friends;
    }

    @Override
    public <T extends Friend> T findName(String name) {
        return (T)((Friend)this.stream().filter(command -> command.getName().equals(name)).findAny().orElse(null));
    }

    @Override
    public <T extends Friend> T findClass(Class<T> clazz) {
        return (T)((Friend)this.stream().filter(command -> command.getClass() == clazz).findAny().orElse(null));
    }

    @Override
    public void register(Friend friend) {
        this.add(friend);
    }
}
