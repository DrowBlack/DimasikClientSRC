package net.minecraft.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public class BannerItem
extends WallOrFloorItem {
    public BannerItem(Block p_i48529_1_, Block p_i48529_2_, Item.Properties builder) {
        super(p_i48529_1_, p_i48529_2_, builder);
        Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_1_);
        Validate.isInstanceOf(AbstractBannerBlock.class, p_i48529_2_);
    }

    public static void appendHoverTextFromTileEntityTag(ItemStack stack, List<ITextComponent> p_185054_1_) {
        CompoundNBT compoundnbt = stack.getChildTag("BlockEntityTag");
        if (compoundnbt != null && compoundnbt.contains("Patterns")) {
            ListNBT listnbt = compoundnbt.getList("Patterns", 10);
            for (int i = 0; i < listnbt.size() && i < 6; ++i) {
                CompoundNBT compoundnbt1 = listnbt.getCompound(i);
                DyeColor dyecolor = DyeColor.byId(compoundnbt1.getInt("Color"));
                BannerPattern bannerpattern = BannerPattern.byHash(compoundnbt1.getString("Pattern"));
                if (bannerpattern == null) continue;
                p_185054_1_.add(new TranslationTextComponent("block.minecraft.banner." + bannerpattern.getFileName() + "." + dyecolor.getTranslationKey()).mergeStyle(TextFormatting.GRAY));
            }
        }
    }

    public DyeColor getColor() {
        return ((AbstractBannerBlock)this.getBlock()).getColor();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        BannerItem.appendHoverTextFromTileEntityTag(stack, tooltip);
    }
}
