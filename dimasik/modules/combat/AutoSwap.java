package dimasik.modules.combat;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.helpers.render.ColorHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.utils.player.MoveUtils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class AutoSwap
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("FunTime"));
    private final SelectOption item = new SelectOption("First Item", 0, new SelectOptionValue("Ball"), new SelectOptionValue("Golden Apple"), new SelectOptionValue("Shield"), new SelectOptionValue("Talisman"));
    private final SelectOption item2 = new SelectOption("Second Item", 0, new SelectOptionValue("Ball"), new SelectOptionValue("Golden Apple"), new SelectOptionValue("Shield"), new SelectOptionValue("Talisman"));
    private final BindOption bind = new BindOption("Key", 0);
    private final SwapHelpers swaps = new SwapHelpers();
    private boolean swap;
    private boolean hand;
    private ITextComponent swapHudComponent;
    private long delay1;
    private final Animation anim = new Animation();
    private static final int delay = 2000;
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventInput> input = this::input;
    private final EventListener<EventRender2D.Post> render2d = this::render;

    public AutoSwap() {
        super("AutoSwap", Category.COMBAT);
        this.settings(this.mode, this.item, this.item2, this.bind);
    }

    public void update(EventUpdate event) {
        long now;
        if (this.swap && this.hand) {
            if (this.item.getSelected("Ball")) {
                this.swap(Items.PLAYER_HEAD);
            }
            if (this.item.getSelected("Golden Apple")) {
                this.swap(Items.GOLDEN_APPLE);
            }
            if (this.item.getSelected("Talisman")) {
                this.tal();
            }
            if (this.item.getSelected("Shield")) {
                this.swap(Items.SHIELD);
            }
            this.hand = false;
        }
        if (this.swap) {
            if (this.item2.getSelected("Ball")) {
                this.swap(Items.PLAYER_HEAD);
            }
            if (this.item2.getSelected("Golden Apple")) {
                this.swap(Items.GOLDEN_APPLE);
            }
            if (this.item2.getSelected("Talisman")) {
                this.tal();
            }
            if (this.item2.getSelected("Shield")) {
                this.swap(Items.SHIELD);
            }
            this.hand = true;
        }
        boolean update = (now = System.currentTimeMillis()) <= this.delay1;
        this.anim.update(update);
    }

    public void input(EventInput event) {
        this.swap = event.getKey() == this.bind.getKey();
    }

    public void render(EventRender2D.Post event) {
        this.anim.animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, AutoSwap.mc.getTimer().renderPartialTicks);
        int screenWidth = event.getMainWindow().getScaledWidth();
        int screenHeight = event.getMainWindow().getScaledHeight();
        int textWidth = AutoSwap.mc.fontRenderer.getStringPropertyWidth(this.swapHudComponent);
        float x = (float)((double)(screenWidth - textWidth) / 2.0);
        float y = screenHeight - 120;
        if (this.anim.getAnimationValue() > 0.1f) {
            AutoSwap.mc.fontRenderer.func_243248_b(event.getMatrixStack(), this.swapHudComponent, x, y, ColorHelpers.getColorWithAlpha(-1, 255.0f * this.anim.getAnimationValue()));
        }
    }

    private void swap(Item item) {
        int slot;
        if (this.mode.getSelected("ReallyWorld")) {
            slot = this.swaps.find(item);
            if (slot != -1) {
                ITextComponent itemName = this.getItemDisplayComponent(slot);
                AutoSwap.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 40, ClickType.SWAP, AutoSwap.mc.player);
                this.getetextwithname(itemName);
            }
            this.swap = false;
        }
        if (this.mode.getSelected("FunTime")) {
            KeyBinding[] pressedKeys;
            slot = this.swaps.find(item);
            for (KeyBinding keyBinding : pressedKeys = new KeyBinding[]{AutoSwap.mc.gameSettings.keyBindForward, AutoSwap.mc.gameSettings.keyBindBack, AutoSwap.mc.gameSettings.keyBindLeft, AutoSwap.mc.gameSettings.keyBindRight, AutoSwap.mc.gameSettings.keyBindJump, AutoSwap.mc.gameSettings.keyBindSprint}) {
                keyBinding.setPressed(false);
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
            }
            if (slot != -1 && !MoveUtils.isMoving()) {
                ITextComponent itemName = this.getItemDisplayComponent(slot);
                AutoSwap.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 40, ClickType.SWAP, AutoSwap.mc.player);
                this.getetextwithname(itemName);
                if (AutoSwap.mc.currentScreen == null) {
                    AutoSwap.mc.player.connection.sendPacket(new CCloseWindowPacket());
                    for (KeyBinding keyBinding : pressedKeys) {
                        boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                        keyBinding.setPressed(isKeyPressed);
                    }
                }
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
                this.swap = false;
            }
        }
    }

    private void tal() {
        int slot;
        if (this.mode.getSelected("ReallyWorld")) {
            slot = this.swaps.findtal();
            if (slot != -1) {
                ITextComponent itemName = this.getItemDisplayComponent(slot);
                AutoSwap.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 40, ClickType.SWAP, AutoSwap.mc.player);
                this.getetextwithname(itemName);
            }
            this.swap = false;
        }
        if (this.mode.getSelected("FunTime")) {
            KeyBinding[] pressedKeys;
            slot = this.swaps.findtal();
            for (KeyBinding keyBinding : pressedKeys = new KeyBinding[]{AutoSwap.mc.gameSettings.keyBindForward, AutoSwap.mc.gameSettings.keyBindBack, AutoSwap.mc.gameSettings.keyBindLeft, AutoSwap.mc.gameSettings.keyBindRight, AutoSwap.mc.gameSettings.keyBindJump, AutoSwap.mc.gameSettings.keyBindSprint}) {
                keyBinding.setPressed(false);
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = false;
            }
            if (slot != -1 && !MoveUtils.isMoving()) {
                ITextComponent itemName = this.getItemDisplayComponent(slot);
                AutoSwap.mc.playerController.windowClick(0, slot < 9 ? slot + 36 : slot, 40, ClickType.SWAP, AutoSwap.mc.player);
                this.getetextwithname(itemName);
                if (AutoSwap.mc.currentScreen == null) {
                    AutoSwap.mc.player.connection.sendPacket(new CCloseWindowPacket());
                    for (KeyBinding keyBinding : pressedKeys) {
                        boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                        keyBinding.setPressed(isKeyPressed);
                    }
                }
                Load.getInstance().getHooks().getModuleManagers().getGuiMove().update = true;
                this.swap = false;
            }
        }
    }

    private ITextComponent getItemDisplayComponent(int slot) {
        int invIndex = slot >= 36 && slot <= 44 ? slot - 36 : slot;
        ItemStack stack = AutoSwap.mc.player.inventory.getStackInSlot(invIndex);
        return stack.getDisplayName();
    }

    private void getetextwithname(ITextComponent itemName) {
        IFormattableTextComponent prefix = new StringTextComponent("\u0421\u0432\u0430\u043f\u043d\u0443\u043b \u043d\u0430 ").mergeStyle(TextFormatting.WHITE);
        StringTextComponent combined = new StringTextComponent("");
        combined.append(prefix).append(itemName.deepCopy());
        this.swapHudComponent = combined;
        this.delay1 = System.currentTimeMillis() + 2000L;
    }
}
