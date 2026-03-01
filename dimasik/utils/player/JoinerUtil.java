package dimasik.utils.player;

import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.events.main.packet.EventSendPacket;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.client.ClientManagers;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.time.TimerUtils;
import lombok.Generated;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

public class JoinerUtil
implements IFastAccess {
    int toggleKey = -1;
    int grief = -1;
    boolean sucsess = true;
    boolean isCd = false;
    private final TimerUtils timerUtil = new TimerUtils();
    private final TimerUtils timerCd = new TimerUtils();

    public void onUpd(EventUpdate event) {
        if (!this.sucsess) {
            this.handleEventUpdate();
        }
    }

    public void key(EventInput event) {
        if (event.getKey() == this.toggleKey) {
            this.sucsess = true;
        }
    }

    public void packet(EventSendPacket eventPacket) {
        SChatPacket packet;
        String message;
        IPacket iPacket;
        if (eventPacket.getPacket() instanceof SJoinGamePacket) {
            if (JoinerUtil.mc.ingameGUI.getTabList().header == null) {
                return;
            }
            String string = TextFormatting.getTextWithoutFormattingCodes(JoinerUtil.mc.ingameGUI.getTabList().header.getString());
            if (!string.contains("Lobby")) {
                return;
            }
            ChatUtils.addMessage("\u0412\u044b \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0437\u0430\u0448\u043b\u0438 \u043d\u0430 " + this.grief + " \u0433\u0440\u0438\u0444!");
            this.sucsess = true;
        }
        if ((iPacket = eventPacket.getPacket()) instanceof SChatPacket && (message = TextFormatting.getTextWithoutFormattingCodes((packet = (SChatPacket)iPacket).getChatComponent().getString())).contains("\u041f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u043e \u0441\u0435\u043a\u0443\u043d\u0434")) {
            this.isCd = true;
            if (this.timerCd.hasTimeElapsed(180L)) {
                this.isCd = false;
            }
        }
    }

    private void handleEventUpdate() {
        if (JoinerUtil.mc.currentScreen == null) {
            if (JoinerUtil.mc.player.ticksExisted < 5) {
                JoinerUtil.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            }
        } else if (JoinerUtil.mc.currentScreen instanceof ChestScreen) {
            try {
                int numberGrief = this.grief;
                ContainerScreen container = (ContainerScreen)JoinerUtil.mc.currentScreen;
                for (int i = 0; i < ((Container)container.getContainer()).inventorySlots.size(); ++i) {
                    String s = ((Container)container.getContainer()).inventorySlots.get(i).getStack().getDisplayName().getString();
                    if ((ClientManagers.isConnectedToServer("playrw") || ClientManagers.isConnectedToServer("reallyworld")) && s.contains("\u0413\u0420\u0418\u0424\u0415\u0420\u0421\u041a\u041e\u0415 \u0412\u042b\u0416\u0418\u0412\u0410\u041d\u0418\u0415 (1.16.5-1.20.4)") && this.timerUtil.hasTimeElapsed(50L) && !this.isCd) {
                        JoinerUtil.mc.playerController.windowClick(JoinerUtil.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, JoinerUtil.mc.player);
                        this.timerUtil.reset();
                    }
                    if (!s.contains("\u0413\u0420\u0418\u0424 #" + numberGrief) || !this.timerUtil.hasTimeElapsed(50L) || this.isCd) continue;
                    JoinerUtil.mc.playerController.windowClick(JoinerUtil.mc.player.openContainer.windowId, i, 0, ClickType.PICKUP, JoinerUtil.mc.player);
                    this.timerUtil.reset();
                    String string = TextFormatting.getTextWithoutFormattingCodes(JoinerUtil.mc.ingameGUI.getTabList().header.getString());
                    if (!string.contains("\u0413\u0420\u0418\u0424\u0415\u0420\u0421\u041a\u041e\u0415 \u0412\u042b\u0416\u0418\u0412\u0410\u041d\u0418\u0415")) continue;
                    this.sucsess = true;
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static void selectCompass() {
        int slot = SwapHelpers.getHotBarSlot(Items.COMPASS);
        if (slot == -1) {
            return;
        }
        JoinerUtil.mc.player.inventory.currentItem = slot;
        JoinerUtil.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
    }

    public void onEnable() {
        JoinerUtil.selectCompass();
        JoinerUtil.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
    }

    @Generated
    public void setToggleKey(int toggleKey) {
        this.toggleKey = toggleKey;
    }

    @Generated
    public void setGrief(int grief) {
        this.grief = grief;
    }

    @Generated
    public void setSucsess(boolean sucsess) {
        this.sucsess = sucsess;
    }

    @Generated
    public void setCd(boolean isCd) {
        this.isCd = isCd;
    }

    @Generated
    public int getToggleKey() {
        return this.toggleKey;
    }

    @Generated
    public int getGrief() {
        return this.grief;
    }

    @Generated
    public boolean isSucsess() {
        return this.sucsess;
    }

    @Generated
    public boolean isCd() {
        return this.isCd;
    }

    @Generated
    public TimerUtils getTimerUtil() {
        return this.timerUtil;
    }

    @Generated
    public TimerUtils getTimerCd() {
        return this.timerCd;
    }
}
