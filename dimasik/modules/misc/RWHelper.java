package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.player.EventMouseButtonPress;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.player.W1oxUebok;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

public class RWHelper
extends Module {
    private final BindOption dropCoords = new BindOption("Drop Coords", -1);
    private final BindOption useAntiPolet = new BindOption("Anti Fly", -1);
    private final W1oxUebok scriptConstructor = W1oxUebok.create();
    private final SwapHelpers.Hand3 handUtil = new SwapHelpers.Hand3();
    private final SwapHelpers swapHelpers = new SwapHelpers();
    private long delay;
    private int oldItem = -1;
    private final EventListener<EventInput> key = this::onKey;
    private final EventListener<EventMouseButtonPress> onMouse = this::onMouse;
    private final EventListener<EventReceivePacket> packet = this::packet;
    private final EventListener<EventUpdate> update = this::update;

    public RWHelper() {
        super("RWHelper", Category.MISC);
        this.settings(this.useAntiPolet, this.dropCoords);
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
        this.delay = 0L;
    }

    public void onKey(EventInput event) {
        if (event.getKey() == Integer.valueOf(this.dropCoords.getKey()).intValue()) {
            RWHelper.mc.player.sendChatMessage("! " + (int)RWHelper.mc.player.getPosX() + ", " + (int)RWHelper.mc.player.getPosY() + ", " + (int)RWHelper.mc.player.getPosZ() + " \u0445\u0435\u043b\u043f\u0430\u043d\u0438\u0442\u0435 \u043f\u043e\u0436\u0430\u043b\u0443\u0439\u0441\u0442\u0430!");
        }
        if (event.getKey() == Integer.valueOf(this.useAntiPolet.getKey()).intValue()) {
            this.findInventory();
        }
    }

    public void onMouse(EventMouseButtonPress event) {
        if (event.getButton() == Integer.valueOf(this.useAntiPolet.getKey()).intValue()) {
            this.findInventory();
        }
    }

    private void packet(EventReceivePacket eventPacket) {
        this.handUtil.onEventPacket(eventPacket);
    }

    public void update(EventUpdate eventUpdate) {
        this.scriptConstructor.update();
        this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
    }

    public void useItemFromInventory(ItemType itemType) {
        this.scriptConstructor.cleanup().addStep(0, () -> {
            switch (itemType) {
                case Vedma: {
                    this.findAndUseItem("\u0437\u0435\u043b\u044c\u0435 \u0432\u0435\u0434\u044c\u043c\u044b", Items.SPLASH_POTION);
                }
            }
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
        });
    }

    private void findInventory() {
        int Firework = SwapHelpers.getItemSlot(Items.FIREWORK_STAR);
        boolean handNotNull = !(RWHelper.mc.player.getHeldItemOffhand().getItem() instanceof AirItem);
        new Thread(() -> {
            try {
                if (Firework >= 0) {
                    RWHelper.mc.playerController.windowClick(0, Firework, 40, ClickType.SWAP, RWHelper.mc.player);
                    RWHelper.mc.gameSettings.keyBindSneak.setPressed(true);
                    ChatUtils.addClientMessage("Use anti fly");
                    if (handNotNull && this.oldItem == -1) {
                        this.oldItem = Firework;
                    }
                }
                Thread.sleep(50L);
                if (RWHelper.mc.player.getHeldItemOffhand().getItem() == Items.FIREWORK_STAR) {
                    if (this.oldItem == -1) {
                        return;
                    }
                    RWHelper.mc.playerController.windowClick(0, this.oldItem, 40, ClickType.SWAP, RWHelper.mc.player);
                    this.oldItem = -1;
                    RWHelper.mc.gameSettings.keyBindSneak.setPressed(false);
                }
            }
            catch (Exception var4) {
                var4.printStackTrace();
            }
        }).start();
    }

    private void findAndUseItem(String name, Item netheriteScrap) {
        int hbSlot = this.getItemForName(name, true);
        int invSlot = this.getItemForName(name, false);
        if (!(invSlot == -1 && hbSlot == -1 || RWHelper.mc.player.getCooldownTracker().hasCooldown(netheriteScrap))) {
            int old = RWHelper.mc.player.inventory.currentItem;
            int slot = this.findAndTrowItem(hbSlot, invSlot);
            if (slot > 8) {
                RWHelper.mc.playerController.pickItem(slot);
            }
            if (SwapHelpers.findEmptySlot(true) != -1 && RWHelper.mc.player.inventory.currentItem != old) {
                RWHelper.mc.player.inventory.currentItem = old;
            }
        }
    }

    private int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            this.handUtil.setOriginalSlot(RWHelper.mc.player.inventory.currentItem);
            RWHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            RWHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            RWHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return hbSlot;
        }
        if (invSlot != -1) {
            this.handUtil.setOriginalSlot(RWHelper.mc.player.inventory.currentItem);
            RWHelper.mc.playerController.pickItem(invSlot);
            RWHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            RWHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }

    private int getItemForName(String name, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; ++i) {
            String displayName;
            ItemStack itemStack = RWHelper.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AirItem || (displayName = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString())) == null || !displayName.toLowerCase().contains(name)) continue;
            return i;
        }
        return -1;
    }

    public static final class ItemType
    extends Enum<ItemType> {
        public static final /* enum */ ItemType Aura = new ItemType();
        public static final /* enum */ ItemType Vedma = new ItemType();
        private static final /* synthetic */ ItemType[] $VALUES;

        public static ItemType[] values() {
            return (ItemType[])$VALUES.clone();
        }

        public static ItemType valueOf(String name) {
            return Enum.valueOf(ItemType.class, name);
        }

        private static ItemType[] $values() {
            return new ItemType[]{Aura, Vedma};
        }

        private static /* synthetic */ ItemType[] $values$() {
            return new ItemType[]{Aura, Vedma};
        }

        static {
            $VALUES = ItemType.$values$();
        }
    }
}
