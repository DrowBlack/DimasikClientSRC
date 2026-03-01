package net.minecraft.entity.merchant;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public interface IMerchant {
    public void setCustomer(@Nullable PlayerEntity var1);

    @Nullable
    public PlayerEntity getCustomer();

    public MerchantOffers getOffers();

    public void setClientSideOffers(@Nullable MerchantOffers var1);

    public void onTrade(MerchantOffer var1);

    public void verifySellingItem(ItemStack var1);

    public World getWorld();

    public int getXp();

    public void setXP(int var1);

    public boolean hasXPBar();

    public SoundEvent getYesSound();

    default public boolean canRestockTrades() {
        return false;
    }

    default public void openMerchantContainer(PlayerEntity player, ITextComponent displayName, int level) {
        MerchantOffers merchantoffers;
        OptionalInt optionalint = player.openContainer(new SimpleNamedContainerProvider((id, playerInventory, player2) -> new MerchantContainer(id, playerInventory, this), displayName));
        if (optionalint.isPresent() && !(merchantoffers = this.getOffers()).isEmpty()) {
            player.openMerchantContainer(optionalint.getAsInt(), merchantoffers, level, this.getXp(), this.hasXPBar(), this.canRestockTrades());
        }
    }
}
