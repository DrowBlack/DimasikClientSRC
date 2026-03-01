package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public final class Ingredient
implements Predicate<ItemStack> {
    public static final Ingredient EMPTY = new Ingredient(Stream.empty());
    private final IItemList[] acceptedItems;
    private ItemStack[] matchingStacks;
    private IntList matchingStacksPacked;

    private Ingredient(Stream<? extends IItemList> itemLists) {
        this.acceptedItems = (IItemList[])itemLists.toArray(IItemList[]::new);
    }

    public ItemStack[] getMatchingStacks() {
        this.determineMatchingStacks();
        return this.matchingStacks;
    }

    private void determineMatchingStacks() {
        if (this.matchingStacks == null) {
            this.matchingStacks = (ItemStack[])Arrays.stream(this.acceptedItems).flatMap(ingredientList -> ingredientList.getStacks().stream()).distinct().toArray(ItemStack[]::new);
        }
    }

    @Override
    public boolean test(@Nullable ItemStack p_test_1_) {
        if (p_test_1_ == null) {
            return false;
        }
        this.determineMatchingStacks();
        if (this.matchingStacks.length == 0) {
            return p_test_1_.isEmpty();
        }
        for (ItemStack itemstack : this.matchingStacks) {
            if (itemstack.getItem() != p_test_1_.getItem()) continue;
            return true;
        }
        return false;
    }

    public IntList getValidItemStacksPacked() {
        if (this.matchingStacksPacked == null) {
            this.determineMatchingStacks();
            this.matchingStacksPacked = new IntArrayList(this.matchingStacks.length);
            for (ItemStack itemstack : this.matchingStacks) {
                this.matchingStacksPacked.add(RecipeItemHelper.pack(itemstack));
            }
            this.matchingStacksPacked.sort(IntComparators.NATURAL_COMPARATOR);
        }
        return this.matchingStacksPacked;
    }

    public void write(PacketBuffer buffer) {
        this.determineMatchingStacks();
        buffer.writeVarInt(this.matchingStacks.length);
        for (int i = 0; i < this.matchingStacks.length; ++i) {
            buffer.writeItemStack(this.matchingStacks[i]);
        }
    }

    public JsonElement serialize() {
        if (this.acceptedItems.length == 1) {
            return this.acceptedItems[0].serialize();
        }
        JsonArray jsonarray = new JsonArray();
        for (IItemList ingredient$iitemlist : this.acceptedItems) {
            jsonarray.add(ingredient$iitemlist.serialize());
        }
        return jsonarray;
    }

    public boolean hasNoMatchingItems() {
        return !(this.acceptedItems.length != 0 || this.matchingStacks != null && this.matchingStacks.length != 0 || this.matchingStacksPacked != null && !this.matchingStacksPacked.isEmpty());
    }

    private static Ingredient fromItemListStream(Stream<? extends IItemList> stream) {
        Ingredient ingredient = new Ingredient(stream);
        return ingredient.acceptedItems.length == 0 ? EMPTY : ingredient;
    }

    public static Ingredient fromItems(IItemProvider ... itemsIn) {
        return Ingredient.fromStacks(Arrays.stream(itemsIn).map(ItemStack::new));
    }

    public static Ingredient fromStacks(ItemStack ... stacks) {
        return Ingredient.fromStacks(Arrays.stream(stacks));
    }

    public static Ingredient fromStacks(Stream<ItemStack> stacks) {
        return Ingredient.fromItemListStream(stacks.filter(stack -> !stack.isEmpty()).map(stack -> new SingleItemList((ItemStack)stack)));
    }

    public static Ingredient fromTag(ITag<Item> tagIn) {
        return Ingredient.fromItemListStream(Stream.of(new TagList(tagIn)));
    }

    public static Ingredient read(PacketBuffer buffer) {
        int i = buffer.readVarInt();
        return Ingredient.fromItemListStream(Stream.generate(() -> new SingleItemList(buffer.readItemStack())).limit(i));
    }

    public static Ingredient deserialize(@Nullable JsonElement json) {
        if (json != null && !json.isJsonNull()) {
            if (json.isJsonObject()) {
                return Ingredient.fromItemListStream(Stream.of(Ingredient.deserializeItemList(json.getAsJsonObject())));
            }
            if (json.isJsonArray()) {
                JsonArray jsonarray = json.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                }
                return Ingredient.fromItemListStream(StreamSupport.stream(jsonarray.spliterator(), false).map(element -> Ingredient.deserializeItemList(JSONUtils.getJsonObject(element, "item"))));
            }
            throw new JsonSyntaxException("Expected item to be object or array of objects");
        }
        throw new JsonSyntaxException("Item cannot be null");
    }

    private static IItemList deserializeItemList(JsonObject json) {
        if (json.has("item") && json.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        }
        if (json.has("item")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(json, "item"));
            Item item = Registry.ITEM.getOptional(resourcelocation1).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + String.valueOf(resourcelocation1) + "'"));
            return new SingleItemList(new ItemStack(item));
        }
        if (json.has("tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "tag"));
            ITag<Item> itag = TagCollectionManager.getManager().getItemTags().get(resourcelocation);
            if (itag == null) {
                throw new JsonSyntaxException("Unknown item tag '" + String.valueOf(resourcelocation) + "'");
            }
            return new TagList(itag);
        }
        throw new JsonParseException("An ingredient entry needs either a tag or an item");
    }

    static interface IItemList {
        public Collection<ItemStack> getStacks();

        public JsonObject serialize();
    }

    static class TagList
    implements IItemList {
        private final ITag<Item> tag;

        private TagList(ITag<Item> tagIn) {
            this.tag = tagIn;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            ArrayList<ItemStack> list = Lists.newArrayList();
            for (Item item : this.tag.getAllElements()) {
                list.add(new ItemStack(item));
            }
            return list;
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", TagCollectionManager.getManager().getItemTags().getValidatedIdFromTag(this.tag).toString());
            return jsonobject;
        }
    }

    static class SingleItemList
    implements IItemList {
        private final ItemStack stack;

        private SingleItemList(ItemStack stackIn) {
            this.stack = stackIn;
        }

        @Override
        public Collection<ItemStack> getStacks() {
            return Collections.singleton(this.stack);
        }

        @Override
        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.stack.getItem()).toString());
            return jsonobject;
        }
    }
}
