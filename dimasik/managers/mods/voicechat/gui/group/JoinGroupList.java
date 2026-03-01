package dimasik.managers.mods.voicechat.gui.group;

import dimasik.managers.mods.voicechat.gui.group.JoinGroupEntry;
import dimasik.managers.mods.voicechat.gui.group.JoinGroupScreen;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenBase;
import dimasik.managers.mods.voicechat.gui.widgets.ListScreenListBase;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.common.ClientGroup;
import dimasik.managers.mods.voicechat.voice.common.PlayerState;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;

public class JoinGroupList
extends ListScreenListBase<JoinGroupEntry> {
    protected final ListScreenBase parent;

    public JoinGroupList(ListScreenBase parent, int width, int height, int top, int size) {
        super(width, height, top, size);
        this.parent = parent;
        this.updateGroups();
    }

    private void updateGroups() {
        Map<UUID, JoinGroupEntry.Group> groups = ClientManager.getGroupManager().getGroups().stream().filter(clientGroup -> !clientGroup.isHidden()).collect(Collectors.toMap(ClientGroup::getId, JoinGroupEntry.Group::new));
        List<PlayerState> playerStates = ClientManager.getPlayerStateManager().getPlayerStates(true);
        for (PlayerState state : playerStates) {
            JoinGroupEntry.Group group2;
            if (!state.hasGroup() || (group2 = groups.get(state.getGroup())) == null) continue;
            group2.getMembers().add(state);
        }
        groups.values().forEach(group -> group.getMembers().sort(Comparator.comparing(PlayerState::getName)));
        this.replaceEntries(groups.values().stream().map(group -> new JoinGroupEntry(this.parent, (JoinGroupEntry.Group)group)).sorted(Comparator.comparing(o -> o.getGroup().getGroup().getName())).collect(Collectors.toList()));
    }

    public static void update() {
        Screen screen = Minecraft.getInstance().currentScreen;
        if (screen instanceof JoinGroupScreen) {
            JoinGroupScreen joinGroupScreen = (JoinGroupScreen)screen;
            joinGroupScreen.groupList.updateGroups();
        }
    }

    public boolean isEmpty() {
        return this.getChildren().isEmpty();
    }
}
