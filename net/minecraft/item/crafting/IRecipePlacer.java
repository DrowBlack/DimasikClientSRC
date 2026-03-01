package net.minecraft.item.crafting;

import java.util.Iterator;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.math.MathHelper;

public interface IRecipePlacer<T> {
    default public void placeRecipe(int width, int height, int outputSlot, IRecipe<?> recipe, Iterator<T> ingredients, int maxAmount) {
        int i = width;
        int j = height;
        if (recipe instanceof ShapedRecipe) {
            ShapedRecipe shapedrecipe = (ShapedRecipe)recipe;
            i = shapedrecipe.getWidth();
            j = shapedrecipe.getHeight();
        }
        int k1 = 0;
        block0: for (int k = 0; k < height; ++k) {
            if (k1 == outputSlot) {
                ++k1;
            }
            boolean flag = (float)j < (float)height / 2.0f;
            int l = MathHelper.floor((float)height / 2.0f - (float)j / 2.0f);
            if (flag && l > k) {
                k1 += width;
                ++k;
            }
            for (int i1 = 0; i1 < width; ++i1) {
                boolean flag1;
                if (!ingredients.hasNext()) {
                    return;
                }
                flag = (float)i < (float)width / 2.0f;
                l = MathHelper.floor((float)width / 2.0f - (float)i / 2.0f);
                int j1 = i;
                boolean bl = flag1 = i1 < i;
                if (flag) {
                    j1 = l + i;
                    boolean bl2 = flag1 = l <= i1 && i1 < l + i;
                }
                if (flag1) {
                    this.setSlotContents(ingredients, k1, maxAmount, k, i1);
                } else if (j1 == i1) {
                    k1 += width - i1;
                    continue block0;
                }
                ++k1;
            }
        }
    }

    public void setSlotContents(Iterator<T> var1, int var2, int var3, int var4, int var5);
}
