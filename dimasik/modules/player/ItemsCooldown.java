package dimasik.modules.player;

import dimasik.events.api.EventListener;
import dimasik.events.main.packet.EventReceivePacket;
import dimasik.events.main.player.EventCooldown;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Generated;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.StringUtils;

public class ItemsCooldown
extends Module {
    public HashMap<Item, Long> lastUseItemTime = new HashMap();
    private static final MultiOption items = new MultiOption("Items", new MultiOptionValue("Golden Apple", true), new MultiOptionValue("Pearl", true), new MultiOptionValue("Chorus", true));
    private static final SliderOption appleCooldown = new SliderOption("Golden Apple Cooldown", 4.35f, 1.0f, 10.0f).increment(0.05f).visible(() -> items.getSelected("Golden Apple"));
    private static final SliderOption pearlCooldown = new SliderOption("Pearl Cooldown", 14.05f, 1.0f, 15.0f).increment(0.05f).visible(() -> items.getSelected("Golden Apple"));
    private static final SliderOption chorusCooldown = new SliderOption("Chorus Cooldown", 2.3f, 1.0f, 10.0f).increment(0.05f).visible(() -> items.getSelected("Golden Apple"));
    private final CheckboxOption onlyPvp = new CheckboxOption("Only Pvp", true);
    private boolean pvpMode = false;
    private UUID uuid;
    private final EventListener<EventCooldown> cooldown = this::cooldown;
    private final EventListener<EventReceivePacket> receive = this::receive;

    public ItemsCooldown() {
        super("ItemsCooldown", Category.PLAYER);
        this.settings(items, appleCooldown, pearlCooldown, chorusCooldown, this.onlyPvp);
    }

    private void cooldown(EventCooldown event) {
        ArrayList<Item> itemsToRemove = new ArrayList<Item>();
        for (Map.Entry<Item, Long> entry : this.lastUseItemTime.entrySet()) {
            float timeSetting;
            ItemEnum itemEnum = ItemEnum.getItemEnum(entry.getKey());
            if (itemEnum == null || event.itemStack != itemEnum.getItem() || !itemEnum.getActive().get().booleanValue()) continue;
            long time = System.currentTimeMillis() - entry.getValue();
            if ((float)time < (timeSetting = itemEnum.getTime().get().floatValue() * 1000.0f)) {
                event.setCooldown((float)time / timeSetting);
                continue;
            }
            itemsToRemove.add(itemEnum.getItem());
        }
        itemsToRemove.forEach(this.lastUseItemTime::remove);
    }

    public void receive(EventReceivePacket event) {
        IPacket<?> iPacket = event.getPacket();
        if (iPacket instanceof SUpdateBossInfoPacket) {
            SUpdateBossInfoPacket packet = (SUpdateBossInfoPacket)iPacket;
            if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
                if (StringUtils.stripControlCodes(packet.getName().getString()).toLowerCase().contains("pvp")) {
                    this.pvpMode = true;
                    this.uuid = packet.getUniqueId();
                }
            } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE && packet.getUniqueId().equals(this.uuid)) {
                this.pvpMode = false;
            }
        }
    }

    public boolean isCurrentItem(ItemEnum item) {
        if (!item.getActive().get().booleanValue()) {
            return false;
        }
        return item.getActive().get() != false && Arrays.stream(ItemEnum.values()).anyMatch(e -> e == item);
    }

    @Generated
    public CheckboxOption getOnlyPvp() {
        return this.onlyPvp;
    }

    @Generated
    public boolean isPvpMode() {
        return this.pvpMode;
    }

    public static enum ItemEnum {
        GOLDEN_APPLE(Items.GOLDEN_APPLE, () -> items.getSelected("Golden Apple"), appleCooldown::getValue),
        CHORUS(Items.CHORUS_FRUIT, () -> items.getSelected("Chorus"), chorusCooldown::getValue),
        PEARL(Items.ENDER_PEARL, () -> items.getSelected("Pearl"), pearlCooldown::getValue);

        private final Item item;
        private final Supplier<Boolean> active;
        private final Supplier<Float> time;

        private ItemEnum(Item item, Supplier<Boolean> active, Supplier<Float> time) {
            this.item = item;
            this.active = active;
            this.time = time;
        }

        public static ItemEnum getItemEnum(Item item) {
            return Arrays.stream(ItemEnum.values()).filter(e -> e.getItem() == item).findFirst().orElse(null);
        }

        @Generated
        public Item getItem() {
            return this.item;
        }

        @Generated
        public Supplier<Boolean> getActive() {
            return this.active;
        }

        @Generated
        public Supplier<Float> getTime() {
            return this.time;
        }
    }
}
