package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.command.main.AutoContractCommand;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.time.TimerUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.network.play.server.SOpenWindowPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

public class AutoContract
extends Module {
    private final TimerUtils timerUtil = new TimerUtils();
    boolean joinHub = false;
    boolean joinGame = false;
    private final TimerUtils timerUtil1 = new TimerUtils();
    public SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Grief"), new SelectOptionValue("Mega grief"));
    private final SliderOption griefSelection = new SliderOption("Grief number", 1.0f, 1.0f, 54.0f).increment(1.0f).visible(() -> this.mode.getSelected("Grief"));
    private static final SwapHelpers swapHelpers = new SwapHelpers();
    private final EventListener<EventReceivePacket> receive = this::receive;
    private final EventListener<EventUpdate> update = this::update;

    public AutoContract() {
        super("AutoContract", Category.MISC);
        this.settings(this.mode, this.griefSelection);
    }

    @Override
    public void onEnabled() {
        if (AutoContractCommand.nickName != null) {
            if (!ClientManagers.isPvpMode()) {
                AutoContract.mc.player.sendChatMessage("/hub");
            }
            ChatUtils.addClientMessage("\u0414\u043b\u044f \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u044f \u043c\u043e\u0434\u0443\u043b\u044f \u043f\u0440\u043e\u0436\u043c\u0438\u0442\u0435 INSERT");
            AutoContract.selectCompass();
            AutoContract.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        }
        super.onEnabled();
    }

    public void receive(EventReceivePacket event) {
        String message;
        SChatPacket packet;
        IPacket<?> pidor;
        if (InputMappings.isKeyDown(mc.getMainWindow().getHandle(), 260)) {
            this.toggle();
        }
        if (ClientManagers.isPvpMode()) {
            ChatUtils.addClientMessage("\u0412\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c \u0432 PVP-\u0440\u0435\u0436\u0438\u043c\u0435!");
            this.toggle();
        }
        if (AutoContractCommand.nickName == null) {
            ChatUtils.addClientMessage("\u0423\u043a\u0430\u0436\u0438\u0442\u0435 \u0446\u0435\u043b\u044c! .contract <name>");
            this.toggle();
        }
        if (event.getPacket() instanceof SJoinGamePacket) {
            this.joinGame = true;
            this.timerUtil1.reset();
        }
        if ((pidor = event.getPacket()) instanceof SChatPacket) {
            packet = (SChatPacket)pidor;
            message = TextFormatting.getTextWithoutFormattingCodes(packet.getChatComponent().getString());
            if (message.contains("\u0412\u0430\u0448\u0430 \u0446\u0435\u043b\u044c") && message.contains(AutoContractCommand.nickName)) {
                ChatUtils.addClientMessage("\u041d\u0430\u0439\u0434\u0435\u043d \u043d\u0443\u0436\u043d\u044b\u0439 \u0438\u0433\u0440\u043e\u043a");
                this.toggle();
            }
            if (message.contains("\u0412\u0430\u0448\u0430 \u0446\u0435\u043b\u044c") || message.contains("\u041d\u0435 \u0441\u043f\u0430\u043c\u044c!") || message.contains("\u041d\u0435 \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u043e\u0441\u044c")) {
                this.joinGame = false;
                this.joinHub = true;
                this.timerUtil.reset();
                this.timerUtil1.reset();
            }
        }
        if (pidor instanceof SChatPacket && ((message = TextFormatting.getTextWithoutFormattingCodes((packet = (SChatPacket)pidor).getChatComponent().getString())).contains("\u041a \u0441\u043e\u0436\u0430\u043b\u0435\u043d\u0438\u044e \u0441\u0435\u0440\u0432\u0435\u0440 \u043f\u0435\u0440\u0435\u043f\u043e\u043b\u043d\u0435\u043d") || message.contains("\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 20 \u0441\u0435\u043a\u0443\u043d\u0434!") || message.contains("\u0431\u043e\u043b\u044c\u0448\u043e\u0439 \u043f\u043e\u0442\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432"))) {
            AutoContract.selectCompass();
            AutoContract.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
        }
        if (event.getPacket() instanceof SOpenWindowPacket && !AutoContract.mc.world.getScoreboard().getScoreObjectives().isEmpty() && (((SOpenWindowPacket)event.getPacket()).getTitle().getString().contains("\u00a7f\ua201\ua000\ua202\ua301\ua202\ua001\u00a70\ua203\ua100") || ((SOpenWindowPacket)event.getPacket()).getTitle().getString().contains("\u00a7f\ua201\ua000\ua202\ua305\ua202\ua001\u00a70\ua203\ua102"))) {
            AutoContract.mc.player.sendChatMessage("/hub");
        }
    }

    public void update(EventUpdate event) {
        boolean NullPlayer = true;
        for (NetworkPlayerInfo info : mc.getConnection().getPlayerInfoMap()) {
            if (!info.getGameProfile().getName().equals(AutoContractCommand.nickName)) continue;
            NullPlayer = false;
            break;
        }
        if (this.joinGame && !NullPlayer) {
            AutoContract.mc.player.sendChatMessage("/contract get");
            this.joinGame = false;
        }
        if (this.joinHub && this.timerUtil.hasTimeElapsed(500L)) {
            this.joinHub = false;
            AutoContract.mc.player.sendChatMessage("/hub");
            this.timerUtil.reset();
        }
        this.handleEventUpdate();
    }

    private void handleEventUpdate() {
        if (AutoContract.mc.currentScreen == null) {
            if (AutoContract.mc.player.ticksExisted < 5) {
                AutoContract.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        } else {
            Screen screen = AutoContract.mc.currentScreen;
            if (screen instanceof ChestScreen) {
                ChestScreen container = (ChestScreen)screen;
                try {
                    String s;
                    int i;
                    int numberGrief = ((Float)this.griefSelection.getValue()).intValue();
                    if (this.mode.getSelected("Grief")) {
                        for (i = 0; i < ((ChestContainer)container.getContainer()).inventorySlots.size(); ++i) {
                            s = ((Slot)((ChestContainer)container.getContainer()).inventorySlots.get(i)).getStack().getDisplayName().getString();
                            if (AutoContract.mc.player.openContainer.windowId != 0 && this.timerUtil.hasTimeElapsed(400L)) {
                                this.timerUtil.reset();
                                int oldId = AutoContract.mc.player.openContainer.windowId;
                                AutoContract.mc.playerController.windowClick(oldId, 21, 0, ClickType.PICKUP, AutoContract.mc.player);
                            }
                            if (!s.contains("\u0413\u0420\u0418\u0424 #" + numberGrief) || !this.timerUtil.hasTimeElapsed(50L)) continue;
                            AutoContract.mc.playerController.windowClick(AutoContract.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, AutoContract.mc.player);
                            this.timerUtil.reset();
                        }
                    }
                    if (this.mode.getSelected("Mega grief")) {
                        for (i = 0; i < ((ChestContainer)container.getContainer()).inventorySlots.size(); ++i) {
                            s = ((Slot)((ChestContainer)container.getContainer()).inventorySlots.get(i)).getStack().getDisplayName().getString();
                            if (!ClientManagers.isConnectedToServer("reallyworld") && !ClientManagers.isConnectedToServer("playrw") || !s.contains("\u041c\u0415\u0413\u0410 \u0413\u0420\u0418\u0424 (1.16.5)") || !this.timerUtil.hasTimeElapsed(50L)) continue;
                            AutoContract.mc.playerController.windowClick(AutoContract.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, AutoContract.mc.player);
                            this.timerUtil.reset();
                        }
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
    }

    public static void selectCompass() {
        int slot = swapHelpers.find(Items.COMPASS);
        if (slot == -1) {
            return;
        }
        AutoContract.mc.player.inventory.currentItem = slot;
        AutoContract.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
    }
}
