package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.EnchantmentScreen;
import net.minecraft.client.gui.screen.GrindstoneScreen;
import net.minecraft.client.gui.screen.HopperScreen;
import net.minecraft.client.gui.screen.LecternScreen;
import net.minecraft.client.gui.screen.LoomScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.AnvilScreen;
import net.minecraft.client.gui.screen.inventory.BeaconScreen;
import net.minecraft.client.gui.screen.inventory.BlastFurnaceScreen;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screen.inventory.CartographyTableScreen;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.DispenserScreen;
import net.minecraft.client.gui.screen.inventory.FurnaceScreen;
import net.minecraft.client.gui.screen.inventory.MerchantScreen;
import net.minecraft.client.gui.screen.inventory.ShulkerBoxScreen;
import net.minecraft.client.gui.screen.inventory.SmithingTableScreen;
import net.minecraft.client.gui.screen.inventory.SmokerScreen;
import net.minecraft.client.gui.screen.inventory.StonecutterScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScreenManager {
    private static final Logger LOG = LogManager.getLogger();
    private static final Map<ContainerType<?>, IScreenFactory<?, ?>> FACTORIES = Maps.newHashMap();

    public static <T extends Container> void openScreen(@Nullable ContainerType<T> type, Minecraft mc, int windowId, ITextComponent title) {
        if (type == null) {
            LOG.warn("Trying to open invalid screen with name: {}", (Object)title.getString());
        } else {
            IScreenFactory<T, ?> iscreenfactory = ScreenManager.getFactory(type);
            if (iscreenfactory == null) {
                LOG.warn("Failed to create screen for menu type: {}", (Object)Registry.MENU.getKey(type));
            } else {
                iscreenfactory.createScreen(title, type, mc, windowId);
            }
        }
    }

    @Nullable
    private static <T extends Container> IScreenFactory<T, ?> getFactory(ContainerType<T> type) {
        return FACTORIES.get(type);
    }

    private static <M extends Container, U extends Screen> void registerFactory(ContainerType<? extends M> type, IScreenFactory<M, U> factory) {
        IScreenFactory<M, U> iscreenfactory = FACTORIES.put(type, factory);
        if (iscreenfactory != null) {
            throw new IllegalStateException("Duplicate registration for " + String.valueOf(Registry.MENU.getKey(type)));
        }
    }

    public static boolean isMissingScreen() {
        boolean flag = false;
        for (ContainerType containerType : Registry.MENU) {
            if (FACTORIES.containsKey(containerType)) continue;
            LOG.debug("Menu {} has no matching screen", (Object)Registry.MENU.getKey(containerType));
            flag = true;
        }
        return flag;
    }

    static {
        ScreenManager.registerFactory(ContainerType.GENERIC_9X1, ChestScreen::new);
        ScreenManager.registerFactory(ContainerType.GENERIC_9X2, ChestScreen::new);
        ScreenManager.registerFactory(ContainerType.GENERIC_9X3, ChestScreen::new);
        ScreenManager.registerFactory(ContainerType.GENERIC_9X4, ChestScreen::new);
        ScreenManager.registerFactory(ContainerType.GENERIC_9X5, ChestScreen::new);
        ScreenManager.registerFactory(ContainerType.GENERIC_9X6, ChestScreen::new);
        ScreenManager.registerFactory(ContainerType.GENERIC_3X3, DispenserScreen::new);
        ScreenManager.registerFactory(ContainerType.ANVIL, AnvilScreen::new);
        ScreenManager.registerFactory(ContainerType.BEACON, BeaconScreen::new);
        ScreenManager.registerFactory(ContainerType.BLAST_FURNACE, BlastFurnaceScreen::new);
        ScreenManager.registerFactory(ContainerType.BREWING_STAND, BrewingStandScreen::new);
        ScreenManager.registerFactory(ContainerType.CRAFTING, CraftingScreen::new);
        ScreenManager.registerFactory(ContainerType.ENCHANTMENT, EnchantmentScreen::new);
        ScreenManager.registerFactory(ContainerType.FURNACE, FurnaceScreen::new);
        ScreenManager.registerFactory(ContainerType.GRINDSTONE, GrindstoneScreen::new);
        ScreenManager.registerFactory(ContainerType.HOPPER, HopperScreen::new);
        ScreenManager.registerFactory(ContainerType.LECTERN, LecternScreen::new);
        ScreenManager.registerFactory(ContainerType.LOOM, LoomScreen::new);
        ScreenManager.registerFactory(ContainerType.MERCHANT, MerchantScreen::new);
        ScreenManager.registerFactory(ContainerType.SHULKER_BOX, ShulkerBoxScreen::new);
        ScreenManager.registerFactory(ContainerType.SMITHING, SmithingTableScreen::new);
        ScreenManager.registerFactory(ContainerType.SMOKER, SmokerScreen::new);
        ScreenManager.registerFactory(ContainerType.CARTOGRAPHY_TABLE, CartographyTableScreen::new);
        ScreenManager.registerFactory(ContainerType.STONECUTTER, StonecutterScreen::new);
    }

    static interface IScreenFactory<T extends Container, U extends Screen> {
        default public void createScreen(ITextComponent title, ContainerType<T> type, Minecraft mc, int windowId) {
            U u = this.create(type.create(windowId, mc.player.inventory), mc.player.inventory, title);
            mc.player.openContainer = ((IHasContainer)u).getContainer();
            mc.displayGuiScreen((Screen)u);
        }

        public U create(T var1, PlayerInventory var2, ITextComponent var3);
    }
}
