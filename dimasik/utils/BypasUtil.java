package dimasik.utils;

import dimasik.helpers.interfaces.IFastAccess;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BypasUtil
implements IFastAccess {
    public static BypasUtil instance = new BypasUtil();
    public List<SlowTask> tasks = new ArrayList<SlowTask>();
    public SlowTask lastTask = null;
    KeyBinding[] keybinds;

    public BypasUtil() {
        this.keybinds = new KeyBinding[]{BypasUtil.mc.gameSettings.keyBindForward, BypasUtil.mc.gameSettings.keyBindLeft, BypasUtil.mc.gameSettings.keyBindRight, BypasUtil.mc.gameSettings.keyBindBack, BypasUtil.mc.gameSettings.keyBindJump};
    }

    public void addTask(SlowTask task) {
        this.lastTask = task;
        this.tasks.add(task);
    }

    public void updateSlowTasks() {
        long now = System.currentTimeMillis();
        if (this.lastTask != null) {
            if (now - this.lastTask.time < this.lastTask.duration) {
                for (KeyBinding key : this.keybinds) {
                    key.setPressed(false);
                }
            } else {
                for (KeyBinding key : this.keybinds) {
                    key.setPressed(InputMappings.isKeyDown(mc.getMainWindow().getHandle(), key.getDefault().getKeyCode()));
                }
            }
        }
        for (SlowTask task : this.tasks) {
            if (now - task.time <= task.duration - 20L || task.runnable == null) continue;
            task.runnable.run();
            task.runnable = null;
        }
        Iterator<SlowTask> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
            SlowTask task = iterator.next();
            if (now - task.time <= task.duration) continue;
            iterator.remove();
        }
    }

    public static int searchItem(Item item) {
        for (int i = 0; i < BypasUtil.mc.player.inventory.getSizeInventory(); ++i) {
            if (!BypasUtil.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
            return i;
        }
        return -1;
    }

    public static int searchItemHotbar(Item item) {
        for (int i = 0; i < 9; ++i) {
            if (!BypasUtil.mc.player.inventory.getStackInSlot(i).getItem().equals(item)) continue;
            return i;
        }
        return -1;
    }

    public static int searchItemStack(Predicate<ItemStack> predicate) {
        for (int i = 9; i < 36; ++i) {
            ItemStack stack = BypasUtil.mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || !predicate.test(stack)) continue;
            return i;
        }
        return -1;
    }

    public static int searchHotbarStack(Predicate<ItemStack> predicate) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = BypasUtil.mc.player.inventory.getStackInSlot(i);
            if (stack.isEmpty() || !predicate.test(stack)) continue;
            return i;
        }
        return -1;
    }

    public static int findBestElytraSlot() {
        int bestSlot = -1;
        double bestScore = -1.0;
        for (int slot = 0; slot < 36; ++slot) {
            int currentDamage;
            int maxDurability;
            double durabilityRatio;
            int mending;
            int unbreaking;
            int protection;
            double score;
            ItemStack stack = BypasUtil.mc.player.inventory.getStackInSlot(slot);
            if (stack.getItem() != Items.ELYTRA || !((score = (double)((protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack)) * 100 + (unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack)) * 10 + ((mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack)) > 0 ? 1 : 0)) + (durabilityRatio = (double)((maxDurability = stack.getMaxDamage()) - (currentDamage = stack.getDamage())) / (double)maxDurability) * 10.0) > bestScore)) continue;
            bestScore = score;
            bestSlot = slot;
        }
        return bestSlot;
    }

    public static int findBestChestplateSlot() {
        int bestSlot = -1;
        double bestScore = -1.0;
        for (int slot = 0; slot < 36; ++slot) {
            int currentDamage;
            int maxDurability;
            double durabilityRatio;
            ArmorItem armor;
            ItemStack stack = BypasUtil.mc.player.inventory.getStackInSlot(slot);
            if (!(stack.getItem() instanceof ArmorItem) || (armor = (ArmorItem)stack.getItem()).getEquipmentSlot() != EquipmentSlotType.CHEST) continue;
            int protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
            int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
            int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
            int armorTypePriority = BypasUtil.getChestplatePriority(stack.getItem());
            double score = (double)(armorTypePriority * 10000 + protection * 100 + unbreaking * 10 + (mending > 0 ? 1 : 0)) + (durabilityRatio = (double)((maxDurability = stack.getMaxDamage()) - (currentDamage = stack.getDamage())) / (double)maxDurability) * 10.0;
            if (!(score > bestScore)) continue;
            bestScore = score;
            bestSlot = slot;
        }
        return bestSlot;
    }

    private static int getChestplatePriority(Item item) {
        if (item == Items.NETHERITE_CHESTPLATE) {
            return 6;
        }
        if (item == Items.DIAMOND_CHESTPLATE) {
            return 5;
        }
        if (item == Items.IRON_CHESTPLATE) {
            return 4;
        }
        if (item == Items.CHAINMAIL_CHESTPLATE) {
            return 3;
        }
        if (item == Items.GOLDEN_CHESTPLATE) {
            return 2;
        }
        return item == Items.LEATHER_CHESTPLATE ? 1 : 0;
    }

    public static BypasUtil getInstance() {
        return instance;
    }

    public static class SlowTask {
        public long duration;
        public long time;
        public Runnable runnable;

        public SlowTask(long duration, Runnable runnable) {
            this.duration = duration;
            this.runnable = runnable;
            this.time = System.currentTimeMillis();
        }
    }
}
