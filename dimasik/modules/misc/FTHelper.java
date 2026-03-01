package dimasik.modules.misc;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventInput;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.helpers.module.swap.SwapHelpers;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.BindOption;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.utils.client.ChatUtils;
import dimasik.utils.player.W1oxUebok;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;

public class FTHelper
extends Module {
    private final CheckboxOption autoWay = new CheckboxOption("\u0410\u0432\u0442\u043e \u043c\u0435\u0442\u043a\u0430", true);
    private final BindOption desorit = new BindOption("\u0414\u0435\u0437\u043e\u0440\u0438\u043d\u0442\u0430\u0446\u0438\u044f", -1);
    private final BindOption trapa = new BindOption("\u0422\u0440\u0430\u043f\u043a\u0430", -1);
    private final BindOption plast = new BindOption("\u041f\u043b\u0430\u0441\u0442", -1);
    private final BindOption pil = new BindOption("\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", -1);
    private final BindOption snejok = new BindOption("\u0421\u043d\u0435\u0436\u043e\u043a", -1);
    private final BindOption aura = new BindOption("\u0411\u043e\u0436\u044c\u044f \u0410\u0443\u0440\u0430", -1);
    private final W1oxUebok scriptConstructor = W1oxUebok.create();
    private final SwapHelpers.Hand3 handUtil = new SwapHelpers.Hand3();
    private final SwapHelpers swapHelpers = new SwapHelpers();
    long delay;
    boolean disorientationThrow;
    boolean trapka;
    boolean plasttr;
    boolean piltr;
    boolean snejoktr;
    boolean auratr;
    private int originalSlot = -1;
    private final EventListener<EventReceivePacket> input = this::onPacket;
    private final EventListener<EventInput> event = this::onKey;
    private final EventListener<EventUpdate> event2 = this::update;

    public FTHelper() {
        super("FTHelper", Category.MISC);
        this.settings(this.autoWay, this.desorit, this.pil, this.plast, this.trapa, this.snejok, this.aura);
    }

    public void onKey(EventInput event) {
        if (event.getKey() == this.desorit.getKey()) {
            this.disorientationThrow = true;
        }
        if (event.getKey() == this.trapa.getKey()) {
            this.trapka = true;
        }
        if (event.getKey() == this.plast.getKey()) {
            this.plasttr = true;
        }
        if (event.getKey() == this.pil.getKey()) {
            this.piltr = true;
        }
        if (event.getKey() == this.snejok.getKey()) {
            this.snejoktr = true;
        }
        if (event.getKey() == this.aura.getKey()) {
            this.auratr = true;
        }
    }

    private void update(EventUpdate eventPacket) {
        int slot;
        int invSlot;
        int hbSlot;
        if (this.disorientationThrow) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForId(Items.ENDER_EYE, true);
            invSlot = this.getItemForId(Items.ENDER_EYE, false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430!");
                this.disorientationThrow = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE)) {
                ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u044e \u0434\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044e!");
                this.originalSlot = FTHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
            } else {
                ChatUtils.addClientMessage("\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f \u0432 \u043a\u0434!");
            }
            this.disorientationThrow = false;
        }
        if (this.trapka) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForId(Items.NETHERITE_SCRAP, true);
            invSlot = this.getItemForId(Items.NETHERITE_SCRAP, false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u0422\u0440\u0430\u043f\u043a\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430!");
                this.trapka = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP)) {
                ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u044e \u0442\u0440\u0430\u043f\u043a\u0443!");
                this.originalSlot = FTHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
            } else {
                ChatUtils.addClientMessage("\u0422\u0440\u0430\u043f\u043a\u0430 \u0432 \u043a\u0434!");
            }
            this.trapka = false;
        }
        if (this.plasttr) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForId(Items.DRIED_KELP, true);
            invSlot = this.getItemForId(Items.DRIED_KELP, false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u041f\u043b\u0430\u0441\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
                this.plasttr = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP)) {
                ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u044e \u043f\u043b\u0430\u0441\u0442!");
                this.originalSlot = FTHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
            } else {
                ChatUtils.addClientMessage("\u041f\u043b\u0430\u0441\u0442 \u0432 \u043a\u0434!");
            }
            this.plasttr = false;
        }
        if (this.snejoktr) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForId(Items.SNOWBALL, true);
            invSlot = this.getItemForId(Items.SNOWBALL, false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u0421\u043d\u0435\u0436\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d!");
                this.snejoktr = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.SNOWBALL)) {
                ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u044e \u0441\u043d\u0435\u0436\u043e\u043a!");
                this.originalSlot = FTHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
            } else {
                ChatUtils.addClientMessage("\u0421\u043d\u0435\u0436\u043e\u043a \u0432 \u043a\u0434!");
            }
            this.snejoktr = false;
        }
        if (this.piltr) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForId(Items.SUGAR, true);
            invSlot = this.getItemForId(Items.SUGAR, false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430!");
                this.piltr = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.SUGAR)) {
                ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u044e \u044f\u0432\u043d\u0443\u044e \u043f\u044b\u043b\u044c!");
                this.originalSlot = FTHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
            } else {
                ChatUtils.addClientMessage("\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c \u0432 \u043a\u0434!");
            }
            this.piltr = false;
        }
        if (this.auratr) {
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            hbSlot = this.getItemForId(Items.PHANTOM_MEMBRANE, true);
            invSlot = this.getItemForId(Items.PHANTOM_MEMBRANE, false);
            if (invSlot == -1 && hbSlot == -1) {
                ChatUtils.addClientMessage("\u0411\u043e\u0436\u044c\u044f \u0430\u0443\u0440\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430!");
                this.auratr = false;
                return;
            }
            if (!FTHelper.mc.player.getCooldownTracker().hasCooldown(Items.PHANTOM_MEMBRANE)) {
                ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u044e \u0431\u043e\u0436\u044c\u044e \u0430\u0443\u0440\u0443!");
                this.originalSlot = FTHelper.mc.player.inventory.currentItem;
                slot = this.findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    FTHelper.mc.playerController.pickItem(slot);
                }
            } else {
                ChatUtils.addClientMessage("\u0411\u043e\u0436\u044c\u044f \u0430\u0443\u0440\u0430 \u0432 \u043a\u0434!");
            }
            this.auratr = false;
        }
        if (this.originalSlot != -1 && System.currentTimeMillis() - this.delay > 500L) {
            FTHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(this.originalSlot));
            this.originalSlot = -1;
        }
    }

    public void onPacket(EventReceivePacket e) {
        IPacket<?> iPacket;
        if (FTHelper.mc.player == null || FTHelper.mc.world == null) {
            return;
        }
        if (((Boolean)this.autoWay.getValue()).booleanValue() && (iPacket = e.getPacket()) instanceof SChatPacket) {
            SChatPacket p = (SChatPacket)iPacket;
            String raw = p.getChatComponent().getString().toLowerCase(Locale.ROOT);
            if (raw.contains("\u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u0445") && raw.contains("\u2550\u2550\u2566\u2550\u2550\u2566\u2550\u2550\u2566\u2550\u2550\u2557") && ClientManagers.isConnectedToServer("funtime")) {
                String coords = FTHelper.extractCoordinates(raw, Type.EVENT);
                FTHelper.mc.player.sendChatMessage(".way add \u0418\u0432\u0435\u043d\u0442 " + coords);
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GREEN) + "\u041f\u043e\u0441\u0442\u0430\u0432\u0438\u043b \u0442\u043e\u0447\u043a\u0443 \"\u0418\u0432\u0435\u043d\u0442\" \u043d\u0430 " + coords);
            } else if (raw.contains("\u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b") && raw.contains("||") && ClientManagers.isConnectedToServer("funtime")) {
                String coords = FTHelper.extractCoordinates(raw, Type.EVENT);
                FTHelper.mc.player.sendChatMessage(".way add \u0418\u0432\u0435\u043d\u0442 " + coords);
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GREEN) + "\u041f\u043e\u0441\u0442\u0430\u0432\u0438\u043b \u0442\u043e\u0447\u043a\u0443 \"\u0418\u0432\u0435\u043d\u0442\" \u043d\u0430 " + coords);
            }
        }
        this.handUtil.onEventPacket(e);
    }

    public static String extractCoordinates(String input, Type type) {
        Pattern pattern;
        if (type == Type.EVENT) {
            pattern = Pattern.compile("\\[(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\]");
        } else if (type == Type.DEATH) {
            pattern = Pattern.compile("\\[(-?\\d+\\.\\d+),\\s*(-?\\d+\\.\\d+),\\s*(-?\\d+\\.\\d+)\\]");
        } else {
            return null;
        }
        Matcher matcher = pattern.matcher(input);
        if (matcher.find() && type == Type.EVENT) {
            return matcher.group(1) + " " + matcher.group(3);
        }
        return null;
    }

    private int getItemForName(String name, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; ++i) {
            String displayName;
            ItemStack itemStack = FTHelper.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AirItem || (displayName = TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName().getString())) == null || !displayName.toLowerCase().contains(name)) continue;
            return i;
        }
        return -1;
    }

    private int getItemForId(Item item, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; ++i) {
            ItemStack itemStack = FTHelper.mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AirItem || itemStack.getItem() != item) continue;
            return i;
        }
        return -1;
    }

    private int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            this.handUtil.setOriginalSlot(FTHelper.mc.player.inventory.currentItem);
            FTHelper.mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            FTHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            FTHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return hbSlot;
        }
        if (invSlot != -1) {
            this.handUtil.setOriginalSlot(FTHelper.mc.player.inventory.currentItem);
            FTHelper.mc.playerController.pickItem(invSlot);
            FTHelper.mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            FTHelper.mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }

    public static enum Type {
        EVENT,
        DEATH;

    }
}
