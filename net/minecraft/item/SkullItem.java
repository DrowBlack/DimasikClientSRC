package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

public class SkullItem
extends WallOrFloorItem {
    public SkullItem(Block floorBlockIn, Block wallBlockIn, Item.Properties builder) {
        super(floorBlockIn, wallBlockIn, builder);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
            CompoundNBT compoundnbt1;
            String s = null;
            CompoundNBT compoundnbt = stack.getTag();
            if (compoundnbt.contains("SkullOwner", 8)) {
                s = compoundnbt.getString("SkullOwner");
            } else if (compoundnbt.contains("SkullOwner", 10) && (compoundnbt1 = compoundnbt.getCompound("SkullOwner")).contains("Name", 8)) {
                s = compoundnbt1.getString("Name");
            }
            if (s != null) {
                return new TranslationTextComponent(this.getTranslationKey() + ".named", s);
            }
        }
        return super.getDisplayName(stack);
    }

    @Override
    public boolean updateItemStackNBT(CompoundNBT nbt) {
        super.updateItemStackNBT(nbt);
        if (nbt.contains("SkullOwner", 8) && !StringUtils.isBlank(nbt.getString("SkullOwner"))) {
            GameProfile gameprofile = new GameProfile(null, nbt.getString("SkullOwner"));
            gameprofile = SkullTileEntity.updateGameProfile(gameprofile);
            nbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
            return true;
        }
        return false;
    }
}
