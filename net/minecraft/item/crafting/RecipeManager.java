package net.minecraft.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RecipeManager
extends JsonReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();
    private Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes = ImmutableMap.of();
    private boolean someRecipesErrored;

    public RecipeManager() {
        super(GSON, "recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        this.someRecipesErrored = false;
        HashMap<IRecipeType, ImmutableMap.Builder> map = Maps.newHashMap();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
            ResourceLocation resourcelocation = entry.getKey();
            try {
                IRecipe<?> irecipe = RecipeManager.deserializeRecipe(resourcelocation, JSONUtils.getJsonObject(entry.getValue(), "top element"));
                map.computeIfAbsent(irecipe.getType(), recipeType -> ImmutableMap.builder()).put(resourcelocation, irecipe);
            }
            catch (JsonParseException | IllegalArgumentException jsonparseexception) {
                LOGGER.error("Parsing error loading recipe {}", (Object)resourcelocation, (Object)jsonparseexception);
            }
        }
        this.recipes = map.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, recipeEntry -> ((ImmutableMap.Builder)recipeEntry.getValue()).build()));
        LOGGER.info("Loaded {} recipes", (Object)map.size());
    }

    public <C extends IInventory, T extends IRecipe<C>> Optional<T> getRecipe(IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn) {
        return this.getRecipes(recipeTypeIn).values().stream().flatMap(recipe -> Util.streamOptional(recipeTypeIn.matches(recipe, worldIn, inventoryIn))).findFirst();
    }

    public <C extends IInventory, T extends IRecipe<C>> List<T> getRecipesForType(IRecipeType<T> recipeType) {
        return this.getRecipes(recipeType).values().stream().map(recipe -> recipe).collect(Collectors.toList());
    }

    public <C extends IInventory, T extends IRecipe<C>> List<T> getRecipes(IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn) {
        return this.getRecipes(recipeTypeIn).values().stream().flatMap(recipe -> Util.streamOptional(recipeTypeIn.matches(recipe, worldIn, inventoryIn))).sorted(Comparator.comparing(recipe -> recipe.getRecipeOutput().getTranslationKey())).collect(Collectors.toList());
    }

    private <C extends IInventory, T extends IRecipe<C>> Map<ResourceLocation, IRecipe<C>> getRecipes(IRecipeType<T> recipeTypeIn) {
        return this.recipes.getOrDefault(recipeTypeIn, Collections.emptyMap());
    }

    public <C extends IInventory, T extends IRecipe<C>> NonNullList<ItemStack> getRecipeNonNull(IRecipeType<T> recipeTypeIn, C inventoryIn, World worldIn) {
        Optional<T> optional = this.getRecipe(recipeTypeIn, inventoryIn, worldIn);
        if (optional.isPresent()) {
            return ((IRecipe)optional.get()).getRemainingItems(inventoryIn);
        }
        NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inventoryIn.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i) {
            nonnulllist.set(i, inventoryIn.getStackInSlot(i));
        }
        return nonnulllist;
    }

    public Optional<? extends IRecipe<?>> getRecipe(ResourceLocation recipeId) {
        return this.recipes.values().stream().map(recipeMap -> (IRecipe)recipeMap.get(recipeId)).filter(Objects::nonNull).findFirst();
    }

    public Collection<IRecipe<?>> getRecipes() {
        return this.recipes.values().stream().flatMap(recipeMap -> recipeMap.values().stream()).collect(Collectors.toSet());
    }

    public Stream<ResourceLocation> getKeys() {
        return this.recipes.values().stream().flatMap(recipeMap -> recipeMap.keySet().stream());
    }

    public static IRecipe<?> deserializeRecipe(ResourceLocation recipeId, JsonObject json) {
        String s = JSONUtils.getString(json, "type");
        return Registry.RECIPE_SERIALIZER.getOptional(new ResourceLocation(s)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + s + "'")).read(recipeId, json);
    }

    public void deserializeRecipes(Iterable<IRecipe<?>> recipes) {
        this.someRecipesErrored = false;
        HashMap map = Maps.newHashMap();
        recipes.forEach(recipe -> {
            Map map1 = map.computeIfAbsent(recipe.getType(), recipeType -> Maps.newHashMap());
            IRecipe irecipe = map1.put(recipe.getId(), recipe);
            if (irecipe != null) {
                throw new IllegalStateException("Duplicate recipe ignored with ID " + String.valueOf(recipe.getId()));
            }
        });
        this.recipes = ImmutableMap.copyOf(map);
    }
}
