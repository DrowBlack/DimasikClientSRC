package dimasik.modules.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.utils.time.TimerUtils;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.Generated;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;

public class AutoDuel
extends Module {
    private static final Pattern pattern = Pattern.compile("^\\w{3,16}$");
    private final SelectOption mode = new SelectOption("\u0420\u0435\u0436\u0438\u043c", 0, new SelectOptionValue("Ball"), new SelectOptionValue("Shield"), new SelectOptionValue("Spikes"), new SelectOptionValue("Netherite"), new SelectOptionValue("CheatParadise"), new SelectOptionValue("Bow"), new SelectOptionValue("Classic"), new SelectOptionValue("Totems"), new SelectOptionValue("NoDebuff"));
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventReceivePacket> receive = this::send;
    private final List<String> sent = Lists.newArrayList();
    private int currentPlayerIndex = 0;
    private final TimerUtils counter = new TimerUtils();

    public AutoDuel() {
        super("AutoDuel", Category.MISC);
        this.settings(this.mode);
    }

    public void update(EventUpdate e) {
        List<String> players = this.getOnlinePlayers();
        this.lastPosX = AutoDuel.mc.player.getPosX();
        this.lastPosY = AutoDuel.mc.player.getPosY();
        this.lastPosZ = AutoDuel.mc.player.getPosZ();
        if (this.counter.hasTimeElapsed(800L * (long)players.size())) {
            this.sent.clear();
            this.currentPlayerIndex = 0;
            this.counter.reset();
        }
        if (!players.isEmpty()) {
            Container container;
            if (this.counter.hasTimeElapsed(1000L)) {
                String player;
                if (this.currentPlayerIndex >= players.size()) {
                    this.currentPlayerIndex = 0;
                }
                if (!this.sent.contains(player = players.get(this.currentPlayerIndex)) && !player.equals(AutoDuel.mc.session.getProfile().getName())) {
                    AutoDuel.mc.player.sendChatMessage("/duel " + player);
                    this.sent.add(player);
                }
                ++this.currentPlayerIndex;
                this.counter.reset();
            }
            if ((container = AutoDuel.mc.player.openContainer) instanceof ChestContainer) {
                ChestContainer chest = (ChestContainer)container;
                if (AutoDuel.mc.currentScreen.getTitle().getString().contains("\u0412\u044b\u0431\u043e\u0440 \u043d\u0430\u0431\u043e\u0440\u0430 (1/1)")) {
                    if (this.counter.hasTimeElapsed(150L)) {
                        AutoDuel.mc.playerController.windowClick(chest.windowId, Slot.valueOf((String)((SelectOptionValue)this.mode.getValue()).getName()).slot, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                        this.counter.reset();
                    }
                } else if (AutoDuel.mc.currentScreen.getTitle().getString().contains("\u041d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430 \u043f\u043e\u0435\u0434\u0438\u043d\u043a\u0430") && this.counter.hasTimeElapsed(150L)) {
                    AutoDuel.mc.playerController.windowClick(chest.windowId, 0, 0, ClickType.QUICK_MOVE, AutoDuel.mc.player);
                    this.counter.reset();
                }
            }
        }
    }

    public void send(EventReceivePacket event) {
        SChatPacket chat;
        String text;
        IPacket<?> packet = event.getPacket();
        if (packet instanceof SChatPacket && ((text = (chat = (SChatPacket)packet).getChatComponent().getString().toLowerCase()).contains("\u043d\u0430\u0447\u0430\u043b\u043e") && text.contains("\u0447\u0435\u0440\u0435\u0437") && text.contains("\u0441\u0435\u043a\u0443\u043d\u0434!") || text.equals(""))) {
            this.toggle();
        }
    }

    private List<String> getOnlinePlayers() {
        return AutoDuel.mc.player.connection.getPlayerInfoMap().stream().map(NetworkPlayerInfo::getGameProfile).map(GameProfile::getName).filter(profileName -> pattern.matcher((CharSequence)profileName).matches()).collect(Collectors.toList());
    }

    public static enum Slot {
        Shield(0),
        Spikes(1),
        Bow(2),
        Totems(3),
        NoDebuff(4),
        Ball(5),
        Classic(6),
        CheatParadise(7),
        Netherite(8);

        private final int slot;

        private Slot(int slot) {
            this.slot = slot;
        }

        @Generated
        public int getSlot() {
            return this.slot;
        }
    }
}
