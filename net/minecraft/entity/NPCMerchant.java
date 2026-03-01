package net.minecraft.entity;

import javax.annotation.Nullable;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class NPCMerchant
implements IMerchant {
    private final MerchantInventory merchantInventory;
    private final PlayerEntity customer;
    private MerchantOffers offers = new MerchantOffers();
    private int xp;

    public NPCMerchant(PlayerEntity player) {
        this.customer = player;
        this.merchantInventory = new MerchantInventory(this);
    }

    @Override
    @Nullable
    public PlayerEntity getCustomer() {
        return this.customer;
    }

    @Override
    public void setCustomer(@Nullable PlayerEntity player) {
    }

    @Override
    public MerchantOffers getOffers() {
        return this.offers;
    }

    @Override
    public void setClientSideOffers(@Nullable MerchantOffers offers) {
        this.offers = offers;
    }

    @Override
    public void onTrade(MerchantOffer offer) {
        offer.increaseUses();
    }

    @Override
    public void verifySellingItem(ItemStack stack) {
    }

    @Override
    public World getWorld() {
        return this.customer.world;
    }

    @Override
    public int getXp() {
        return this.xp;
    }

    @Override
    public void setXP(int xpIn) {
        this.xp = xpIn;
    }

    @Override
    public boolean hasXPBar() {
        return true;
    }

    @Override
    public SoundEvent getYesSound() {
        return SoundEvents.ENTITY_VILLAGER_YES;
    }
}
