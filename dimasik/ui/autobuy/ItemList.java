package dimasik.ui.autobuy;

import java.util.HashMap;
import lombok.Generated;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;

public final class ItemList {
    private final ItemStack items;
    private final int price;
    private final int countStack;
    private final HashMap<Enchantment, Integer> enchanments;
    private final boolean fake;
    private final boolean donate;

    @Generated
    public ItemList(ItemStack items, int price, int countStack, HashMap<Enchantment, Integer> enchanments, boolean fake, boolean donate) {
        this.items = items;
        this.price = price;
        this.countStack = countStack;
        this.enchanments = enchanments;
        this.fake = fake;
        this.donate = donate;
    }

    @Generated
    public ItemStack getItems() {
        return this.items;
    }

    @Generated
    public int getPrice() {
        return this.price;
    }

    @Generated
    public int getCountStack() {
        return this.countStack;
    }

    @Generated
    public HashMap<Enchantment, Integer> getEnchanments() {
        return this.enchanments;
    }

    @Generated
    public boolean isFake() {
        return this.fake;
    }

    @Generated
    public boolean isDonate() {
        return this.donate;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemList)) {
            return false;
        }
        ItemList other = (ItemList)o;
        if (this.getPrice() != other.getPrice()) {
            return false;
        }
        if (this.getCountStack() != other.getCountStack()) {
            return false;
        }
        if (this.isFake() != other.isFake()) {
            return false;
        }
        if (this.isDonate() != other.isDonate()) {
            return false;
        }
        ItemStack this$items = this.getItems();
        ItemStack other$items = other.getItems();
        if (this$items == null ? other$items != null : !this$items.equals(other$items)) {
            return false;
        }
        HashMap<Enchantment, Integer> this$enchanments = this.getEnchanments();
        HashMap<Enchantment, Integer> other$enchanments = other.getEnchanments();
        return !(this$enchanments == null ? other$enchanments != null : !((Object)this$enchanments).equals(other$enchanments));
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPrice();
        result = result * 59 + this.getCountStack();
        result = result * 59 + (this.isFake() ? 79 : 97);
        result = result * 59 + (this.isDonate() ? 79 : 97);
        ItemStack $items = this.getItems();
        result = result * 59 + ($items == null ? 43 : $items.hashCode());
        HashMap<Enchantment, Integer> $enchanments = this.getEnchanments();
        result = result * 59 + ($enchanments == null ? 43 : ((Object)$enchanments).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ItemList(items=" + String.valueOf(this.getItems()) + ", price=" + this.getPrice() + ", countStack=" + this.getCountStack() + ", enchanments=" + String.valueOf(this.getEnchanments()) + ", fake=" + this.isFake() + ", donate=" + this.isDonate() + ")";
    }
}
