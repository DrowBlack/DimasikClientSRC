package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.time.TimerUtils;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

public class GriefJoiner
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Grief"), new SelectOptionValue("Mega Grief"));
    private final SliderOption grief = new SliderOption("Grief", 1.0f, 1.0f, 54.0f).increment(1.0f).visible(() -> this.mode.getSelected("Grief"));
    private final SliderOption delay = new SliderOption("Delay", 1000.0f, 10.0f, 3000.0f).increment(10.0f);
    private final TimerUtils timer = new TimerUtils();
    private final SwapHelpers swap = new SwapHelpers();
    private final EventListener<EventUpdate> update = this::update;

    public GriefJoiner() {
        super("GriefJoiner", Category.MISC);
        this.settings(this.mode, this.grief, this.delay);
    }

    @Override
    public void onEnabled() {
        if (GriefJoiner.mc.player == null) {
            return;
        }
        int slot = this.swap.find(Items.COMPASS);
        if (this.swap.haveHotBar(slot) && slot != -1) {
            GriefJoiner.mc.player.connection.sendPacket(new CHeldItemChangePacket(this.swap.format(slot)));
            GriefJoiner.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            this.timer.reset();
        }
    }

    public void update(EventUpdate event) {
        int mega;
        if (this.mode.getSelected("Grief")) {
            int griefName;
            int survival = this.swap.find("\u0413\u0420\u0418\u0424\u0415\u0420\u0421\u041a\u041e\u0415 \u0412\u042b\u0416\u0418\u0412\u0410\u041d\u0418\u0415 (1");
            if (survival != -1 && this.timer.hasTimeElapsed(((Float)this.delay.getValue()).longValue())) {
                GriefJoiner.mc.playerController.windowClick(GriefJoiner.mc.player.openContainer.windowId, survival, 0, ClickType.QUICK_MOVE, GriefJoiner.mc.player);
                this.timer.reset();
            }
            if ((griefName = this.swap.find("\u0413\u0420\u0418\u0424 #" + ((Float)this.grief.getValue()).intValue() + " (1")) != -1 && this.timer.hasTimeElapsed(((Float)this.delay.getValue()).longValue())) {
                GriefJoiner.mc.playerController.windowClick(GriefJoiner.mc.player.openContainer.windowId, griefName, 0, ClickType.QUICK_MOVE, GriefJoiner.mc.player);
                this.timer.reset();
            }
        } else if (this.mode.getSelected("Mega Grief") && (mega = this.swap.find("\u041c\u0415\u0413\u0410 \u0413\u0420\u0418\u0424")) != -1 && this.timer.hasTimeElapsed(((Float)this.delay.getValue()).longValue())) {
            GriefJoiner.mc.playerController.windowClick(GriefJoiner.mc.player.openContainer.windowId, mega, 0, ClickType.QUICK_MOVE, GriefJoiner.mc.player);
            this.timer.reset();
        }
    }
}
