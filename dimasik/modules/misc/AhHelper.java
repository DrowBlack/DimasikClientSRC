package dimasik.modules.misc;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class AhHelper
extends Module {
    public CheckboxOption three = new CheckboxOption("\u041f\u043e\u0434\u0441\u0432\u0435\u0447\u0438\u0432\u0430\u0442\u044c 3 \u0441\u043b\u043e\u0442\u0430", true);
    private boolean price = false;
    float x = 0.0f;
    float y = 0.0f;
    float x2 = 0.0f;
    float y2 = 0.0f;
    float x3 = 0.0f;
    float y3 = 0.0f;
    private final EventListener<EventUpdate> update = this::update;

    public AhHelper() {
        super("AucHelper", Category.MISC);
        this.settings(this.three);
    }

    public void update(EventUpdate update) {
        Screen var3 = AhHelper.mc.currentScreen;
        if (var3 instanceof ChestScreen) {
            ChestScreen e = (ChestScreen)var3;
            if (!e.getTitle().getString().contains("\u0410\u0443\u043a\u0446\u0438\u043e\u043d") && !e.getTitle().getString().contains("\u041f\u043e\u0438\u0441\u043a:")) {
                this.setX(0.0f);
                this.setX2(0.0f);
                this.setX3(0.0f);
                this.price = false;
            } else {
                Object container = e.getContainer();
                Slot slot1 = null;
                Slot slot2 = null;
                Slot slot3 = null;
                int fsPrice = Integer.MAX_VALUE;
                int medPrice = Integer.MAX_VALUE;
                int thPrice = Integer.MAX_VALUE;
                boolean b = false;
                for (Slot slot : ((Container)container).inventorySlots) {
                    if (slot.slotNumber > 44) continue;
                    int currentPrice = this.extractPriceFromStack(slot.getStack());
                    if (currentPrice != -1 && currentPrice < fsPrice) {
                        fsPrice = currentPrice;
                        slot1 = slot;
                    }
                    if (((Boolean)this.three.getValue()).booleanValue()) {
                        if (currentPrice != -1 && currentPrice < medPrice && currentPrice > fsPrice) {
                            medPrice = currentPrice;
                            slot2 = slot;
                        }
                        if (currentPrice == -1 || currentPrice >= thPrice || currentPrice <= medPrice) continue;
                        thPrice = currentPrice;
                        slot3 = slot;
                        continue;
                    }
                    this.setX2(0.0f);
                    this.setX3(0.0f);
                }
                if (slot1 != null) {
                    this.setX(slot1.xPos);
                    this.setY(slot1.yPos);
                }
                if (slot2 != null) {
                    this.setX2(slot2.xPos);
                    this.setY2(slot2.yPos);
                }
                if (slot3 != null) {
                    this.setX3(slot3.xPos);
                    this.setY3(slot3.yPos);
                }
            }
        } else {
            this.setX(0.0f);
            this.setX2(0.0f);
            this.setX3(0.0f);
        }
    }

    protected int extractPriceFromStack(ItemStack stack) {
        CompoundNBT display;
        CompoundNBT tag = stack.getTag();
        if (tag != null && tag.contains("display", 10) && (display = tag.getCompound("display")).contains("Lore", 9)) {
            ListNBT lore = display.getList("Lore", 8);
            for (int j = 0; j < lore.size(); ++j) {
                JsonArray array;
                JsonObject object = new JsonParser().parse(lore.getString(j)).getAsJsonObject();
                if (!object.has("extra") || (array = object.getAsJsonArray("extra")).size() <= 2) continue;
                JsonObject title = array.get(1).getAsJsonObject();
                if (title.get("text").getAsString().trim().toLowerCase().contains("\u0446\u0435\u043da")) {
                    String line = array.get(2).getAsJsonObject().get("text").getAsString().trim().substring(1).replaceAll(",", "");
                    try {
                        int totalPrice = Integer.parseInt(line);
                        int count = stack.getCount();
                        return totalPrice / count;
                    }
                    catch (NumberFormatException e) {
                        e.printStackTrace();
                        return -1;
                    }
                }
                String loreLine = lore.getString(j);
                if (!loreLine.toLowerCase().contains("\u0446\u0435\u043d\u0430")) continue;
                String priceText = loreLine.replaceAll("[^0-9]", "");
                try {
                    int totalPrice = Integer.parseInt(priceText);
                    int count = stack.getCount();
                    return totalPrice / count;
                }
                catch (NumberFormatException e) {
                    e.printStackTrace();
                    return -1;
                }
            }
        }
        return -1;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getX2() {
        return this.x2;
    }

    public float getY2() {
        return this.y2;
    }

    public float getX3() {
        return this.x3;
    }

    public float getY3() {
        return this.y3;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    public void setX3(float x3) {
        this.x3 = x3;
    }

    public void setY3(float y3) {
        this.y3 = y3;
    }
}
