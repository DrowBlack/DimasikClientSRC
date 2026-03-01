package dimasik.modules.misc;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.friend.FriendManagers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.modules.player.ItemsCooldown;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.player.MoveUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

public class ClickAction
extends Module {
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Pearl", true), new MultiOptionValue("Friend", true));
    private final BindOption pearlBind = new BindOption("Pearl Key", -1).visible(() -> this.elements.getSelected("Pearl"));
    private final BindOption friendBind = new BindOption("Friend Key", -1).visible(() -> this.elements.getSelected("Friend"));
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Legit"), new SelectOptionValue("Rage")).visible(() -> this.elements.getSelected("Pearl"));
    private final SwapHelpers swap = new SwapHelpers();
    private boolean friend;
    private boolean pearl;
    private long delay = -1L;
    private int oldSlot = -1;
    private final EventListener<EventInput> input = this::input;
    private final EventListener<EventUpdate> update = this::update;

    public ClickAction() {
        super("ClickAction", Category.MISC);
        this.settings(this.elements, this.mode, this.pearlBind, this.friendBind);
    }

    public void input(EventInput event) {
        this.pearl = event.getKey() == this.pearlBind.getKey() && !ClickAction.mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL) && this.elements.getSelected("Pearl");
        this.friend = event.getKey() == this.friendBind.getKey() && this.elements.getSelected("Friend");
    }

    public void update(EventUpdate event) {
        this.friendController();
        this.pearlController();
    }

    private void friendController() {
        Entity entity;
        FriendManagers friends = Load.getInstance().getHooks().getFriendManagers();
        if (this.friend && (entity = ClickAction.mc.pointedEntity) instanceof PlayerEntity) {
            PlayerEntity entity2 = (PlayerEntity)entity;
            if (friends.is(entity2.getName().getString())) {
                friends.remove(entity2.getName().getString());
                ChatUtils.addClientMessage("\u0423\u0434\u0430\u043b\u0438\u043b \u0434\u0440\u0443\u0433\u0430 \u0441 \u0438\u043c\u0435\u043d\u0435\u043c: " + entity2.getName().getString() + "!");
            } else {
                friends.add(entity2.getName().getString());
                ChatUtils.addClientMessage("\u0414\u043e\u0431\u0430\u0432\u0438\u043b \u0434\u0440\u0443\u0433\u0430 \u0441 \u0438\u043c\u0435\u043d\u0435\u043c: " + entity2.getName().getString() + "!");
            }
            this.friend = false;
        }
    }

    private void pearlController() {
        block21: {
            boolean isPvp;
            ItemsCooldown.ItemEnum itemEnum;
            ItemsCooldown cooldown;
            int slot;
            block22: {
                block23: {
                    slot = this.swap.find(Items.ENDER_PEARL);
                    cooldown = Load.getInstance().getHooks().getModuleManagers().getItemsCooldown();
                    itemEnum = ItemsCooldown.ItemEnum.getItemEnum(Items.ENDER_PEARL);
                    boolean bl = isPvp = cooldown.isPvpMode() || (Boolean)cooldown.getOnlyPvp().getValue() == false;
                    if (slot < 0) break block21;
                    if (!this.mode.getSelected("Rage")) break block22;
                    if (!this.pearl || !this.swap.haveHotBar(Items.ENDER_PEARL)) break block23;
                    if (slot > 44 || slot < 36) break block22;
                    ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot - 36));
                    ClickAction.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(ClickAction.mc.player.inventory.currentItem));
                    if (cooldown.isToggled() && itemEnum != null && cooldown.isCurrentItem(itemEnum) && isPvp) {
                        cooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
                    }
                    this.pearl = false;
                    break block22;
                }
                if (this.pearl) {
                    for (int i = 0; i < 36; ++i) {
                        if (ClickAction.mc.player.inventory.getStackInSlot(i).getItem() != Items.ENDER_PEARL) continue;
                        ClickAction.mc.playerController.windowClick(0, i, ClickAction.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ClickAction.mc.player);
                        ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(ClickAction.mc.player.inventory.currentItem % 8 + 1));
                        ClickAction.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(ClickAction.mc.player.inventory.currentItem));
                        ClickAction.mc.playerController.windowClick(0, i, ClickAction.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ClickAction.mc.player);
                        if (cooldown.isToggled() && itemEnum != null && cooldown.isCurrentItem(itemEnum) && isPvp) {
                            cooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
                        }
                        this.pearl = false;
                        break;
                    }
                }
            }
            if (this.mode.getSelected("Legit")) {
                boolean isKeyPressed;
                if (this.pearl && this.swap.haveHotBar(Items.ENDER_PEARL)) {
                    ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(slot - 36));
                    ClickAction.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                    ClickAction.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                    this.delay = System.currentTimeMillis() + 250L;
                    if (cooldown.isToggled() && itemEnum != null && cooldown.isCurrentItem(itemEnum) && isPvp) {
                        cooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
                    }
                    this.pearl = false;
                } else if (this.pearl) {
                    KeyBinding[] pressedKeys;
                    for (KeyBinding keyBinding : pressedKeys = new KeyBinding[]{ClickAction.mc.gameSettings.keyBindForward, ClickAction.mc.gameSettings.keyBindBack, ClickAction.mc.gameSettings.keyBindLeft, ClickAction.mc.gameSettings.keyBindRight, ClickAction.mc.gameSettings.keyBindJump, ClickAction.mc.gameSettings.keyBindSprint}) {
                        keyBinding.setPressed(false);
                        Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
                    }
                    if (!MoveUtils.isMoving()) {
                        ClickAction.mc.playerController.windowClick(0, slot, ClickAction.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ClickAction.mc.player);
                        ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(ClickAction.mc.player.inventory.currentItem % 8 + 1));
                        ClickAction.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
                        ClickAction.mc.player.connection.sendPacket(new CAnimateHandPacket(Hand.MAIN_HAND));
                        this.oldSlot = slot;
                        this.delay = System.currentTimeMillis() + 250L;
                        if (ClickAction.mc.currentScreen == null) {
                            ClickAction.mc.player.connection.sendPacket(new CCloseWindowPacket());
                            for (KeyBinding keyBinding : pressedKeys) {
                                isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                                keyBinding.setPressed(isKeyPressed);
                            }
                        }
                        Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
                        if (cooldown.isToggled() && itemEnum != null && cooldown.isCurrentItem(itemEnum) && isPvp) {
                            cooldown.lastUseItemTime.put(itemEnum.getItem(), System.currentTimeMillis());
                        }
                        this.pearl = false;
                    }
                }
                if (this.delay >= 0L && System.currentTimeMillis() >= this.delay) {
                    if (this.oldSlot != -1) {
                        KeyBinding[] pressedKeys = new KeyBinding[]{ClickAction.mc.gameSettings.keyBindForward, ClickAction.mc.gameSettings.keyBindBack, ClickAction.mc.gameSettings.keyBindLeft, ClickAction.mc.gameSettings.keyBindRight, ClickAction.mc.gameSettings.keyBindJump, ClickAction.mc.gameSettings.keyBindSprint};
                        for (KeyBinding keyBinding : pressedKeys) {
                            keyBinding.setPressed(false);
                            Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
                        }
                        if (!MoveUtils.isMoving()) {
                            ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(ClickAction.mc.player.inventory.currentItem));
                            ClickAction.mc.playerController.windowClick(0, this.oldSlot, ClickAction.mc.player.inventory.currentItem % 8 + 1, ClickType.SWAP, ClickAction.mc.player);
                            if (ClickAction.mc.currentScreen == null) {
                                ClickAction.mc.player.connection.sendPacket(new CCloseWindowPacket());
                                for (KeyBinding keyBinding : pressedKeys) {
                                    isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                                    keyBinding.setPressed(isKeyPressed);
                                }
                            }
                            Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
                            this.oldSlot = -1;
                            this.delay = -1L;
                        }
                    } else {
                        ClickAction.mc.player.connection.sendPacket(new CHeldItemChangePacket(ClickAction.mc.player.inventory.currentItem));
                        this.delay = -1L;
                    }
                }
            }
        }
    }
}
