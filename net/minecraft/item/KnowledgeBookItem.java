package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Optional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class KnowledgeBookItem
extends Item {
    private static final Logger LOGGER = LogManager.getLogger();

    public KnowledgeBookItem(Item.Properties builder) {
        super(builder);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        CompoundNBT compoundnbt = itemstack.getTag();
        if (!playerIn.abilities.isCreativeMode) {
            playerIn.setHeldItem(handIn, ItemStack.EMPTY);
        }
        if (compoundnbt != null && compoundnbt.contains("Recipes", 9)) {
            if (!worldIn.isRemote) {
                ListNBT listnbt = compoundnbt.getList("Recipes", 8);
                ArrayList<IRecipe<?>> list = Lists.newArrayList();
                RecipeManager recipemanager = worldIn.getServer().getRecipeManager();
                for (int i = 0; i < listnbt.size(); ++i) {
                    String s = listnbt.getString(i);
                    Optional<IRecipe<?>> optional = recipemanager.getRecipe(new ResourceLocation(s));
                    if (!optional.isPresent()) {
                        LOGGER.error("Invalid recipe: {}", (Object)s);
                        return ActionResult.resultFail(itemstack);
                    }
                    list.add(optional.get());
                }
                playerIn.unlockRecipes(list);
                playerIn.addStat(Stats.ITEM_USED.get(this));
            }
            return ActionResult.func_233538_a_(itemstack, worldIn.isRemote());
        }
        LOGGER.error("Tag not valid: {}", (Object)compoundnbt);
        return ActionResult.resultFail(itemstack);
    }
}
