package dimasik.modules.player;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.events.main.player.EventDeath;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.managers.notification.api.Notification;
import dimasik.managers.notification.api.Pattern;
import dimasik.utils.client.SoundUtils;
import dimasik.utils.player.MoveUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

public class ElytraSwap
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("LegendsGrief"), new SelectOptionValue("FunTime"), new SelectOptionValue("CakeWorld"));
    private final BindOption swap = new BindOption("Swap Key", -1);
    private final BindOption boost = new BindOption("Boost Key", -1);
    private final CheckboxOption auto = new CheckboxOption("Auto Jump", false);
    public final CheckboxOption boosterTest = new CheckboxOption("\u0414\u043e\u043f.\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", true);
    public final CheckboxOption sponsorBoost = new CheckboxOption("\u0414\u043e\u043f.\u0414\u043e\u043f.\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", false).visible(() -> (Boolean)this.booster.getValue());
    private boolean makeSwap;
    private boolean makeBoost;
    int oldSlot = -1;
    private ItemStack currentStack;
    private final SwapHelpers.Hand3 handUtil;
    private long delay = -1L;
    public final CheckboxOption booster = new CheckboxOption("\u0423\u0441\u043a\u043e\u0440\u0435\u043d\u0438\u0435", true);
    public final SliderOption boosty = new SliderOption("ff", 0.05f, 0.0f, 0.3f).increment(0.01f);
    public SliderOption boostx = new SliderOption("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", 0.05f, 0.0f, 0.3f).increment(0.01f);
    private final SwapHelpers swaps = new SwapHelpers();
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventDeath> death = this::lox;
    private final EventListener<EventInput> input = this::input;

    public ElytraSwap() {
        super("ElytraSwap", Category.PLAYER);
        this.settings(this.mode, this.swap, this.boost, this.auto, this.booster, this.boostx, this.boosterTest, this.sponsorBoost);
        this.currentStack = ItemStack.EMPTY;
        this.handUtil = new SwapHelpers.Hand3();
    }

    public void update(EventUpdate eventUpdate) {
        boolean check;
        boolean bl = check = this.swaps.find(Items.ELYTRA) >= 0 || this.oldSlot != -1;
        if (check) {
            if (this.mode.getSelected("FunTime")) {
                this.legendsGrief();
            }
            if (this.mode.getSelected("LegendsGrief")) {
                this.legendsGrief();
            }
            if (this.mode.getSelected("Grim")) {
                this.grim();
            }
            if (this.mode.getSelected("ReallyWorld")) {
                this.reallyWorld();
            }
            if (this.mode.getSelected("CakeWorld")) {
                this.legendsGrief();
            }
        }
        if (ElytraSwap.mc.player.isElytraFlying()) {
            this.useFirework();
        }
        this.currentStack = ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (((Boolean)this.auto.getValue()).booleanValue()) {
            if (ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA && !ElytraSwap.mc.player.isInLava() && !ElytraSwap.mc.player.isInWater() && ElytraSwap.mc.player.isOnGround() && !ElytraSwap.mc.gameSettings.keyBindJump.isKeyDown()) {
                ElytraSwap.mc.player.jump();
            } else if (ElytraItem.isUsable(Items.ELYTRA.getDefaultInstance()) && ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA && !ElytraSwap.mc.player.isElytraFlying() && !ElytraSwap.mc.player.isOnGround()) {
                ElytraSwap.mc.player.startFallFlying();
                ElytraSwap.mc.player.connection.sendPacket(new CEntityActionPacket(ElytraSwap.mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            }
        }
        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    public void lox(EventDeath eventDeath) {
        this.makeSwap = false;
    }

    public void input(EventInput eventInput) {
        boolean flag;
        boolean bl = flag = this.swaps.find(Items.ELYTRA) >= 0 || this.oldSlot >= 0;
        if (eventInput.getKey() == this.swap.getKey() && flag) {
            boolean bl2 = this.makeSwap = !this.makeSwap;
            if (this.makeSwap) {
                SoundUtils.playSound("enable");
                Load.getInstance().getHooks().getNotificationManagers().register(new Notification("Elytra has been swapped", "Swap Info", 1500L, this).setPattern(Pattern.ENABLE));
            } else {
                SoundUtils.playSound("disable");
                Load.getInstance().getHooks().getNotificationManagers().register(new Notification("Chestplate has been swapped", "Swap Info", 1500L, this).setPattern(Pattern.DISABLE));
            }
        }
        if (eventInput.getKey() == this.boost.getKey()) {
            this.makeBoost = !this.makeBoost;
        }
    }

    private void legendsGrief() {
        int slot = this.swaps.find(Items.ELYTRA);
        ItemStack itemStack = ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (this.makeSwap) {
            if (itemStack.getItem() != Items.ELYTRA && slot >= 0) {
                KeyBinding[] pressedKeys = new KeyBinding[]{ElytraSwap.mc.gameSettings.keyBindForward, ElytraSwap.mc.gameSettings.keyBindBack, ElytraSwap.mc.gameSettings.keyBindLeft, ElytraSwap.mc.gameSettings.keyBindRight, ElytraSwap.mc.gameSettings.keyBindJump, ElytraSwap.mc.gameSettings.keyBindSprint};
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                if (!MoveUtils.isMoving()) {
                    if (this.swaps.haveHotBar(Items.ELYTRA)) {
                        ElytraSwap.mc.playerController.windowClick(0, 6, slot % 9, ClickType.SWAP, ElytraSwap.mc.player);
                        this.oldSlot = slot;
                    } else {
                        for (int i = 0; i < 36; ++i) {
                            if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA) continue;
                            ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                            ElytraSwap.mc.playerController.windowClick(0, 6, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                            ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                            this.oldSlot = i;
                            break;
                        }
                    }
                    if (ElytraSwap.mc.currentScreen == null) {
                        ElytraSwap.mc.player.connection.sendPacket(new CCloseWindowPacket());
                        for (KeyBinding keyBinding : pressedKeys) {
                            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                            keyBinding.setPressed(isKeyPressed);
                        }
                    }
                    Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
                }
            }
        } else if (this.oldSlot != -1) {
            KeyBinding[] pressedKeys = new KeyBinding[]{ElytraSwap.mc.gameSettings.keyBindForward, ElytraSwap.mc.gameSettings.keyBindBack, ElytraSwap.mc.gameSettings.keyBindLeft, ElytraSwap.mc.gameSettings.keyBindRight, ElytraSwap.mc.gameSettings.keyBindJump, ElytraSwap.mc.gameSettings.keyBindSprint};
            Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
            for (KeyBinding keyBinding : pressedKeys) {
                keyBinding.setPressed(false);
            }
            if (!MoveUtils.isMoving()) {
                if (this.oldSlot <= 44 && this.oldSlot >= 36) {
                    ElytraSwap.mc.playerController.windowClick(0, 6, this.oldSlot % 9, ClickType.SWAP, ElytraSwap.mc.player);
                } else {
                    for (int i = 0; i < 36; ++i) {
                        if (i != this.oldSlot) continue;
                        ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        ElytraSwap.mc.playerController.windowClick(0, 6, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        break;
                    }
                }
                if (ElytraSwap.mc.currentScreen == null) {
                    ElytraSwap.mc.player.connection.sendPacket(new CCloseWindowPacket());
                }
                this.oldSlot = -1;
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
            }
        }
    }

    private void funTime() {
        int slot = this.swaps.find(Items.ELYTRA);
        ItemStack itemStack = ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (this.makeSwap) {
            if (itemStack.getItem() != Items.ELYTRA && slot >= 0) {
                KeyBinding[] pressedKeys = new KeyBinding[]{ElytraSwap.mc.gameSettings.keyBindForward, ElytraSwap.mc.gameSettings.keyBindBack, ElytraSwap.mc.gameSettings.keyBindLeft, ElytraSwap.mc.gameSettings.keyBindRight, ElytraSwap.mc.gameSettings.keyBindJump, ElytraSwap.mc.gameSettings.keyBindSprint};
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
                for (KeyBinding keyBinding : pressedKeys) {
                    keyBinding.setPressed(false);
                }
                if (!MoveUtils.isMoving()) {
                    ElytraSwap.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                    this.oldSlot = slot;
                    if (ElytraSwap.mc.currentScreen == null) {
                        ElytraSwap.mc.player.connection.sendPacket(new CCloseWindowPacket());
                        for (KeyBinding keyBinding : pressedKeys) {
                            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                            keyBinding.setPressed(isKeyPressed);
                        }
                    }
                    Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
                }
            }
        } else if (this.oldSlot != -1) {
            KeyBinding[] pressedKeys = new KeyBinding[]{ElytraSwap.mc.gameSettings.keyBindForward, ElytraSwap.mc.gameSettings.keyBindBack, ElytraSwap.mc.gameSettings.keyBindLeft, ElytraSwap.mc.gameSettings.keyBindRight, ElytraSwap.mc.gameSettings.keyBindJump, ElytraSwap.mc.gameSettings.keyBindSprint};
            Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
            for (KeyBinding keyBinding : pressedKeys) {
                keyBinding.setPressed(false);
            }
            if (!MoveUtils.isMoving()) {
                ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                ElytraSwap.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                if (ElytraSwap.mc.currentScreen == null) {
                    ElytraSwap.mc.player.connection.sendPacket(new CCloseWindowPacket());
                    for (KeyBinding keyBinding : pressedKeys) {
                        boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                        keyBinding.setPressed(isKeyPressed);
                    }
                }
                this.oldSlot = -1;
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
            }
        }
    }

    private void grim() {
        int slot = this.swaps.find(Items.ELYTRA);
        ItemStack itemStack = ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (this.makeSwap) {
            if (itemStack.getItem() != Items.ELYTRA && slot >= 0) {
                ElytraSwap.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                ElytraSwap.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, ElytraSwap.mc.player);
                this.oldSlot = slot;
            }
        } else if (this.oldSlot != -1) {
            ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
            ElytraSwap.mc.playerController.windowClick(0, this.oldSlot, 0, ClickType.PICKUP, ElytraSwap.mc.player);
            ElytraSwap.mc.playerController.windowClick(0, 6, 0, ClickType.PICKUP, ElytraSwap.mc.player);
            this.oldSlot = -1;
        }
    }

    private void reallyWorld() {
        int slot = this.swaps.find(Items.ELYTRA);
        ItemStack itemStack = ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        if (this.makeSwap) {
            if (itemStack.getItem() != Items.ELYTRA && slot >= 0 && this.oldSlot == -1) {
                if (this.swaps.haveHotBar(Items.ELYTRA)) {
                    ElytraSwap.mc.playerController.windowClick(0, 6, slot % 9, ClickType.SWAP, ElytraSwap.mc.player);
                    this.oldSlot = slot;
                } else {
                    for (int i = 0; i < 36; ++i) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() != Items.ELYTRA) continue;
                        ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        ElytraSwap.mc.playerController.windowClick(0, 6, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        this.oldSlot = i;
                        break;
                    }
                }
            }
        } else if (this.oldSlot != -1) {
            if (this.oldSlot <= 44 && this.oldSlot >= 36) {
                ElytraSwap.mc.playerController.windowClick(0, 6, this.oldSlot % 9, ClickType.SWAP, ElytraSwap.mc.player);
            } else {
                for (int i = 0; i < 36; ++i) {
                    if (i != this.oldSlot) continue;
                    ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, 6, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                    ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                    break;
                }
            }
            this.oldSlot = -1;
        }
    }

    public void useFirework() {
        block5: {
            block7: {
                block9: {
                    block8: {
                        int slot;
                        block6: {
                            boolean elytra;
                            boolean bl = elytra = ElytraSwap.mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST).getItem() == Items.ELYTRA;
                            if (ElytraSwap.mc.currentScreen != null || !elytra) break block5;
                            if (!this.mode.getSelected("LegendsGrief")) break block6;
                            if (!this.swaps.haveHotBar(Items.FIREWORK_ROCKET) || !this.makeBoost) break block7;
                            int slot2 = this.swaps.find(Items.FIREWORK_ROCKET);
                            if (ElytraSwap.mc.player.inventory.currentItem != slot2 - 36) {
                                ElytraSwap.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot2 - 36));
                            }
                            ElytraSwap.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                            ElytraSwap.mc.playerController.pickItem(ElytraSwap.mc.player.inventory.currentItem);
                            break block7;
                        }
                        if (!this.mode.getSelected("FunTime")) break block8;
                        if (!this.swaps.haveHotBar(Items.FIREWORK_ROCKET) || !this.makeBoost || (slot = this.swaps.find(Items.FIREWORK_ROCKET)) > 44 || slot < 36) break block7;
                        ElytraSwap.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot - 36));
                        ElytraSwap.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        ElytraSwap.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                        ElytraSwap.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraSwap.mc.player.inventory.currentItem));
                        break block7;
                    }
                    int fireworkSlot = this.swaps.find(Items.FIREWORK_ROCKET);
                    if (ElytraSwap.mc.player.isHandActive() && this.makeBoost && fireworkSlot != -1) {
                        ElytraSwap.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraSwap.mc.player);
                        ElytraSwap.mc.playerController.processRightClick(ElytraSwap.mc.player, ElytraSwap.mc.world, Hand.OFF_HAND);
                        ElytraSwap.mc.playerController.windowClick(0, fireworkSlot, 40, ClickType.SWAP, ElytraSwap.mc.player);
                        this.makeBoost = false;
                        return;
                    }
                    if (!this.swaps.haveHotBar(Items.FIREWORK_ROCKET) || !this.makeBoost) break block9;
                    int slot = this.swaps.find(Items.FIREWORK_ROCKET);
                    if (slot > 44 || slot < 36) break block7;
                    ElytraSwap.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot - 36));
                    ElytraSwap.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    ElytraSwap.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraSwap.mc.player.inventory.currentItem));
                    break block7;
                }
                if (this.makeBoost) {
                    for (int i = 0; i < 36; ++i) {
                        if (ElytraSwap.mc.player.inventory.getStackInSlot(i).getItem() != Items.FIREWORK_ROCKET) continue;
                        if (ElytraSwap.mc.player.isHandActive()) {
                            ElytraSwap.mc.playerController.windowClick(0, i, 40, ClickType.SWAP, ElytraSwap.mc.player);
                            ElytraSwap.mc.playerController.processRightClick(ElytraSwap.mc.player, ElytraSwap.mc.world, Hand.OFF_HAND);
                            ElytraSwap.mc.playerController.windowClick(0, i, 40, ClickType.SWAP, ElytraSwap.mc.player);
                            break;
                        }
                        ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        ElytraSwap.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraSwap.mc.player.inventory.currentItem % 8 + 1));
                        ElytraSwap.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        ElytraSwap.mc.player.connection.sendPacket(new CHeldItemChangePacket(ElytraSwap.mc.player.inventory.currentItem));
                        ElytraSwap.mc.playerController.windowClick(0, i, ElytraSwap.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ElytraSwap.mc.player);
                        break;
                    }
                }
            }
            this.makeBoost = false;
        }
    }
}
