package dimasik.modules.combat;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.utils.player.MoveUtils;
import dimasik.utils.time.TimerUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class AutoArmor
extends Module {
    final SliderOption delay = new SliderOption("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430", 100.0f, 0.0f, 1000.0f).increment(1.0f);
    final TimerUtils timerUtils = new TimerUtils();
    private final EventListener<EventUpdate> update = this::update;

    public AutoArmor() {
        super("AutoArmor", Category.COMBAT);
        this.settings(this.delay);
    }

    public void update(EventUpdate event) {
        int i;
        if (MoveUtils.isMoving()) {
            return;
        }
        PlayerInventory inventoryPlayer = AutoArmor.mc.player.inventory;
        int[] bestIndexes = new int[4];
        int[] bestValues = new int[4];
        for (i = 0; i < 4; ++i) {
            Item item;
            bestIndexes[i] = -1;
            ItemStack stack = inventoryPlayer.armorItemInSlot(i);
            if (!this.isItemValid(stack) || !((item = stack.getItem()) instanceof ArmorItem)) continue;
            ArmorItem armorItem = (ArmorItem)item;
            bestValues[i] = this.calculateArmorValue(armorItem, stack);
        }
        for (i = 0; i < 36; ++i) {
            Item item;
            ItemStack stack = inventoryPlayer.getStackInSlot(i);
            if (!this.isItemValid(stack) || !((item = stack.getItem()) instanceof ArmorItem)) continue;
            ArmorItem armorItem = (ArmorItem)item;
            int armorTypeIndex = armorItem.getSlot().getIndex();
            int value = this.calculateArmorValue(armorItem, stack);
            if (value <= bestValues[armorTypeIndex]) continue;
            bestIndexes[armorTypeIndex] = i;
            bestValues[armorTypeIndex] = value;
        }
        ArrayList<Integer> randomIndexes = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3));
        Collections.shuffle(randomIndexes);
        for (int index : randomIndexes) {
            int bestIndex = bestIndexes[index];
            if (bestIndex == -1 || this.isItemValid(inventoryPlayer.armorItemInSlot(index)) && inventoryPlayer.getFirstEmptyStack() == -1) continue;
            if (bestIndex < 9) {
                bestIndex += 36;
            }
            if (!this.timerUtils.isReached(((Float)this.delay.getValue()).longValue())) break;
            ItemStack armorItemStack = inventoryPlayer.armorItemInSlot(index);
            if (this.isItemValid(armorItemStack)) {
                AutoArmor.mc.playerController.windowClick(0, 8 - index, 0, ClickType.QUICK_MOVE, AutoArmor.mc.player);
            }
            AutoArmor.mc.playerController.windowClick(0, bestIndex, 0, ClickType.QUICK_MOVE, AutoArmor.mc.player);
            this.timerUtils.reset();
            break;
        }
    }

    private boolean isItemValid(ItemStack stack) {
        return stack != null && !stack.isEmpty();
    }

    private int calculateArmorValue(ArmorItem armor, ItemStack stack) {
        int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
        IArmorMaterial armorMaterial = armor.getArmorMaterial();
        int damageReductionAmount = armorMaterial.getDamageReductionAmount(armor.getEquipmentSlot());
        return armor.getDamageReduceAmount() * 20 + protectionLevel * 12 + (int)(armor.getToughness() * 2.0f) + damageReductionAmount * 5 >> 3;
    }
}
