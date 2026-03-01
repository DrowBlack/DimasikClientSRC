package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ItemParser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemPredicateArgument
implements ArgumentType<IResult> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "#stick", "#stick{foo=bar}");
    private static final DynamicCommandExceptionType UNKNOWN_TAG = new DynamicCommandExceptionType(tag -> new TranslationTextComponent("arguments.item.tag.unknown", tag));

    public static ItemPredicateArgument itemPredicate() {
        return new ItemPredicateArgument();
    }

    @Override
    public IResult parse(StringReader p_parse_1_) throws CommandSyntaxException {
        ItemParser itemparser = new ItemParser(p_parse_1_, true).parse();
        if (itemparser.getItem() != null) {
            ItemPredicate itempredicateargument$itempredicate = new ItemPredicate(itemparser.getItem(), itemparser.getNbt());
            return context -> itempredicateargument$itempredicate;
        }
        ResourceLocation resourcelocation = itemparser.getTag();
        return context -> {
            ITag<Item> itag = ((CommandSource)context.getSource()).getServer().func_244266_aF().getItemTags().get(resourcelocation);
            if (itag == null) {
                throw UNKNOWN_TAG.create(resourcelocation.toString());
            }
            return new TagPredicate(itag, itemparser.getNbt());
        };
    }

    public static Predicate<ItemStack> getItemPredicate(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, IResult.class).create(context);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
        StringReader stringreader = new StringReader(p_listSuggestions_2_.getInput());
        stringreader.setCursor(p_listSuggestions_2_.getStart());
        ItemParser itemparser = new ItemParser(stringreader, true);
        try {
            itemparser.parse();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return itemparser.fillSuggestions(p_listSuggestions_2_, ItemTags.getCollection());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    static class ItemPredicate
    implements Predicate<ItemStack> {
        private final Item item;
        @Nullable
        private final CompoundNBT nbt;

        public ItemPredicate(Item itemIn, @Nullable CompoundNBT nbtIn) {
            this.item = itemIn;
            this.nbt = nbtIn;
        }

        @Override
        public boolean test(ItemStack p_test_1_) {
            return p_test_1_.getItem() == this.item && NBTUtil.areNBTEquals(this.nbt, p_test_1_.getTag(), true);
        }
    }

    public static interface IResult {
        public Predicate<ItemStack> create(CommandContext<CommandSource> var1) throws CommandSyntaxException;
    }

    static class TagPredicate
    implements Predicate<ItemStack> {
        private final ITag<Item> tag;
        @Nullable
        private final CompoundNBT nbt;

        public TagPredicate(ITag<Item> tagIn, @Nullable CompoundNBT nbtIn) {
            this.tag = tagIn;
            this.nbt = nbtIn;
        }

        @Override
        public boolean test(ItemStack p_test_1_) {
            return this.tag.contains(p_test_1_.getItem()) && NBTUtil.areNBTEquals(this.nbt, p_test_1_.getTag(), true);
        }
    }
}
