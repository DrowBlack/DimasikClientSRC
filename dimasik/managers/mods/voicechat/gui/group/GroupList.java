package dimasik.managers.mods.voicechat.gui.group;

import dimasik.managers.mods.voicechat.gui.group.GroupEntry;
import dimasik.managers.mods.voicechat.gui.group.GroupScreen;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenListBase;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class GroupList
extends ListScreenListBase<GroupEntry> {
    protected final ListScreenBase parent;

    public GroupList(ListScreenBase parent, int width, int height, int top, int size) {
        super(width, height, top, size);
        this.parent = parent;
        this.updateMembers();
    }

    public void updateMembers() {
        List<PlayerState> playerStates = ClientManager.getPlayerStateManager().getPlayerStates(true);
        UUID group = ClientManager.getPlayerStateManager().getGroupID();
        if (group == null) {
            this.clearEntries();
            this.minecraft.displayGuiScreen(null);
            return;
        }
        boolean changed = false;
        LinkedList<GroupEntry> toRemove = new LinkedList<GroupEntry>();
        for (GroupEntry entry : this.getChildren()) {
            PlayerState state = ClientManager.getPlayerStateManager().getState(entry.getState().getUuid());
            if (state == null) {
                toRemove.add(entry);
                changed = true;
                continue;
            }
            entry.setState(state);
            if (this.isInGroup(state, group)) continue;
            toRemove.add(entry);
            changed = true;
        }
        for (GroupEntry entry : toRemove) {
            this.removeEntry(entry);
        }
        for (PlayerState state : playerStates) {
            if (!this.isInGroup(state, group) || !this.getChildren().stream().noneMatch(groupEntry -> groupEntry.getState().getUuid().equals(state.getUuid()))) continue;
            this.addEntry(new GroupEntry(this.parent, state));
            changed = true;
        }
        if (changed) {
            this.getChildren().sort(Comparator.comparing(o -> o.getState().getName()));
        }
    }

    public static void update() {
        Screen screen = Minecraft.getInstance().currentScreen;
        if (screen instanceof GroupScreen) {
            GroupScreen groupScreen = (GroupScreen)screen;
            groupScreen.groupList.updateMembers();
        }
    }

    private boolean isInGroup(PlayerState state, UUID group) {
        return state.hasGroup() && state.getGroup().equals(group);
    }
}
