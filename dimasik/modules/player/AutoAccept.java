package dimasik.modules.player;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.managers.friend.api.Friend;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.utils.client.ChatUtils;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;

public class AutoAccept
extends Module {
    private final CheckboxOption friendsOnly = new CheckboxOption("Friends Only", true);
    private final EventListener<EventReceivePacket> receiev = this::receive;

    public AutoAccept() {
        super("AutoAccept", Category.PLAYER);
        this.settings(this.friendsOnly);
    }

    public void receive(EventReceivePacket e) {
        SChatPacket p;
        String raw;
        IPacket<?> iPacket;
        if (AutoAccept.mc.player != null && AutoAccept.mc.world != null && (iPacket = e.getPacket()) instanceof SChatPacket && ((raw = (p = (SChatPacket)iPacket).getChatComponent().getString()).contains("\u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f") || raw.contains("has requested teleport") || raw.contains("\u043f\u0440\u043e\u0441\u0438\u0442 \u043a \u0432\u0430\u043c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f"))) {
            boolean tpAccept = this.isTpAccept(raw);
            if (!tpAccept && ((Boolean)this.friendsOnly.getValue()).booleanValue()) {
                return;
            }
            AutoAccept.mc.player.sendChatMessage("/tpaccept");
        }
    }

    private boolean isTpAccept(String raw) {
        boolean tpAccept = false;
        try {
            String friendMessage = raw.split(" ")[0];
            ChatUtils.addMessage(friendMessage);
            for (Friend friend : Load.getInstance().getHooks().getFriendManagers()) {
                if (!friendMessage.replace(" ", "").replace("\u00a76", "").replace("\u00a77", "").equals(friend.getName())) continue;
                tpAccept = true;
                break;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return tpAccept;
    }
}
