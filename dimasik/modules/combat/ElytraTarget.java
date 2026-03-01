package dimasik.modules.combat;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.modules.combat.Aura;
import dimasik.utils.time.TimerUtils;
import lombok.Generated;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.Hand;

public class ElytraTarget
extends Module {
    public final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Default"), new SelectOptionValue("CakeWorld"));
    public final CheckboxOption target = new CheckboxOption("Predict", false);
    public final SliderOption distance = new SliderOption("Predict Distance", 1.5f, 1.0f, 5.0f).increment(0.1f).visible(this.target::getValue);
    private final CheckboxOption xorys = new CheckboxOption("Eating Horus", true).visible(() -> !this.mode.getSelected("CakeWorld"));
    private final CheckboxOption box = new CheckboxOption("Draw Box", true);
    private final CheckboxOption autofireworks = new CheckboxOption("AutoFireworks", false);
    private final SliderOption delayFireWorks = new SliderOption("Delay FireWorks", 500.0f, 50.0f, 3000.0f).increment(50.0f).visible(() -> (Boolean)this.autofireworks.getValue());
    private final SliderOption delay = new SliderOption("Delay", 100.0f, 50.0f, 300.0f).increment(50.0f).visible(() -> this.mode.getSelected("CakeWorld"));
    boolean non = false;
    int lastslot = 0;
    public float speedtop = 0.0f;
    public boolean activatedbooster;
    TimerUtils stopWatch = new TimerUtils();
    public boolean isEating;
    private boolean makeBoost;
    private final SwapHelpers swaps = new SwapHelpers();
    private final TimerUtils timerUtils = new TimerUtils();
    private final EventListener<EventReceivePacket> receive = this::test;
    private final EventListener<EventUpdate> update = this::update;
    protected boolean isStart;

    public ElytraTarget() {
        super("ElytraTarget", Category.COMBAT);
        this.settings(this.mode, this.target, this.distance, this.xorys, this.box, this.autofireworks, this.delayFireWorks);
    }

    public void test(EventReceivePacket eventPacket) {
        this.activatedbooster = eventPacket.getPacket() instanceof SPlayerPositionLookPacket;
    }

    public void update(EventUpdate eventUpdate) {
        Aura aura;
        if (((Boolean)this.autofireworks.getValue()).booleanValue()) {
            this.autoFireWorks();
        }
        if (this.stopWatch.isReached(40L) && ElytraTarget.mc.player.isElytraFlying()) {
            if (!this.activatedbooster) {
                this.speedtop += 1.0E-4f;
            }
            if (ElytraTarget.mc.player.ticksExisted % 10 == 0) {
                this.speedtop -= 5.0E-4f;
            }
            if (this.speedtop > 0.0011f) {
                this.speedtop = 0.0f;
            }
            if (this.activatedbooster) {
                this.speedtop = 0.0f;
            }
            this.stopWatch.reset();
        }
        if (!this.non) {
            this.lastslot = ElytraTarget.mc.player.inventory.currentItem;
        }
        if ((aura = Load.getInstance().getHooks().getModuleManagers().getAura()).getTarget() != null && ((Boolean)this.xorys.getValue()).booleanValue() && aura.getTarget().getHeldItemMainhand().getItem() == Items.CHORUS_FRUIT && aura.getTarget().isHandActive() && ElytraTarget.mc.player.isElytraFlying() && ElytraTarget.doesHotbarHaveItem(Items.CHORUS_FRUIT)) {
            this.useItem();
            this.startEating();
            this.non = true;
        } else if (this.isEating) {
            this.stopEating();
            ElytraTarget.mc.player.inventory.currentItem = this.lastslot;
            this.non = false;
        }
    }

    private void useItem() {
        ElytraTarget.mc.player.inventory.currentItem = this.getSlotInInventoryOrHotbar(Items.CHORUS_FRUIT, true);
    }

    private void startEating() {
        if (!ElytraTarget.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            ElytraTarget.mc.gameSettings.keyBindUseItem.setPressed(true);
            this.isEating = true;
        }
    }

    protected void autoFireWorks() {
        Aura aura = Load.getInstance().getHooks().getModuleManagers().getAura();
        if (ElytraTarget.mc.player.isElytraFlying()) {
            this.useFirework();
            if (this.timerUtils.hasTimeElapsed(((Float)this.delayFireWorks.getValue()).intValue()) && aura.getTarget() != null) {
                this.makeBoost = true;
                this.timerUtils.reset();
            }
        }
    }

    private void stopEating() {
        ElytraTarget.mc.gameSettings.keyBindUseItem.setPressed(false);
        this.isEating = false;
    }

    public int getSlotInInventoryOrHotbar(Item item, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        int finalSlot = -1;
        for (int i = firstSlot; i < lastSlot; ++i) {
            if (ElytraTarget.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            finalSlot = i;
        }
        return finalSlot;
    }

    public static boolean doesHotbarHaveItem(Item item) {
        for (int i = 0; i < 9; ++i) {
            ElytraTarget.mc.player.inventory.getStackInSlot(i);
            if (ElytraTarget.mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return true;
        }
        return false;
    }

    public void useFirework() {
        block4: {
            block6: {
                block5: {
                    boolean elytra;
                    boolean bl = elytra = ElytraTarget.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA;
                    if (ElytraTarget.mc.currentScreen != null || !elytra) break block4;
                    int fireworkSlot = this.swaps.find(Items.FIREWORK_ROCKET);
                    if (ElytraTarget.mc.player.isHandActive() && this.makeBoost && fireworkSlot != -1) {
                        ElytraTarget.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraTarget.mc.player);
                        ElytraTarget.mc.playerController.processRightClick(ElytraTarget.mc.player, ElytraTarget.mc.world, Hand.OFF_HAND);
                        ElytraTarget.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraTarget.mc.player);
                        this.makeBoost = false;
                        return;
                    }
                    if (!this.swaps.haveHotBar(Items.FIREWORK_ROCKET) || !this.makeBoost) break block5;
                    int slot = this.swaps.find(Items.FIREWORK_ROCKET);
                    if (slot > 44 || slot < 36) break block6;
                    ElytraTarget.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot - 36));
                    ElytraTarget.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    ElytraTarget.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraTarget.mc.player.inventory.currentItem));
                    break block6;
                }
                if (this.makeBoost) {
                    for (int i = 0; i < 36; ++i) {
                        if (ElytraTarget.mc.player.inventory.getStackInSlot(i).getItem() != Items.FIREWORK_ROCKET) continue;
                        if (ElytraTarget.mc.player.isHandActive()) {
                            ElytraTarget.mc.playerController.windowClick(0, i, 40, ClickType.SWAP, ElytraTarget.mc.player);
                            ElytraTarget.mc.playerController.processRightClick(ElytraTarget.mc.player, ElytraTarget.mc.world, Hand.OFF_HAND);
                            ElytraTarget.mc.playerController.windowClick(0, i, 40, ClickType.SWAP, ElytraTarget.mc.player);
                            break;
                        }
                        ElytraTarget.mc.playerController.windowClick(0, i, ElytraTarget.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTarget.mc.player);
                        ElytraTarget.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraTarget.mc.player.inventory.currentItem % 8 + 1));
                        ElytraTarget.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        ElytraTarget.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraTarget.mc.player.inventory.currentItem));
                        ElytraTarget.mc.playerController.windowClick(0, i, ElytraTarget.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraTarget.mc.player);
                        break;
                    }
                }
            }
            this.makeBoost = false;
        }
    }

    @Override
    public void onEnabled() {
        if (((Boolean)this.autofireworks.getValue()).booleanValue()) {
            this.isStart = true;
        }
    }

    @Override
    public void onDisabled() {
        if (((Boolean)this.autofireworks.getValue()).booleanValue()) {
            this.isStart = false;
        }
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }

    @Generated
    public CheckboxOption getTarget() {
        return this.target;
    }

    @Generated
    public SliderOption getDistance() {
        return this.distance;
    }

    @Generated
    public CheckboxOption getXorys() {
        return this.xorys;
    }

    @Generated
    public CheckboxOption getBox() {
        return this.box;
    }

    @Generated
    public CheckboxOption getAutofireworks() {
        return this.autofireworks;
    }

    @Generated
    public SliderOption getDelayFireWorks() {
        return this.delayFireWorks;
    }

    @Generated
    public SliderOption getDelay() {
        return this.delay;
    }

    @Generated
    public boolean isNon() {
        return this.non;
    }

    @Generated
    public int getLastslot() {
        return this.lastslot;
    }

    @Generated
    public float getSpeedtop() {
        return this.speedtop;
    }

    @Generated
    public boolean isActivatedbooster() {
        return this.activatedbooster;
    }

    @Generated
    public TimerUtils getStopWatch() {
        return this.stopWatch;
    }

    @Generated
    public boolean isEating() {
        return this.isEating;
    }

    @Generated
    public boolean isMakeBoost() {
        return this.makeBoost;
    }

    @Generated
    public SwapHelpers getSwaps() {
        return this.swaps;
    }

    @Generated
    public TimerUtils getTimerUtils() {
        return this.timerUtils;
    }

    @Generated
    public EventListener<EventReceivePacket> getReceive() {
        return this.receive;
    }

    @Generated
    public EventListener<EventUpdate> getUpdate() {
        return this.update;
    }

    @Generated
    public boolean isStart() {
        return this.isStart;
    }
}
