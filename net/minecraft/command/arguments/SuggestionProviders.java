package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class SuggestionProviders {
    private static final Map<ResourceLocation, SuggestionProvider<ISuggestionProvider>> REGISTRY = Maps.newHashMap();
    private static final ResourceLocation ASK_SERVER_ID = new ResourceLocation("ask_server");
    public static final SuggestionProvider<ISuggestionProvider> ASK_SERVER = SuggestionProviders.register(ASK_SERVER_ID, (p_197500_0_, p_197500_1_) -> ((ISuggestionProvider)p_197500_0_.getSource()).getSuggestionsFromServer(p_197500_0_, p_197500_1_));
    public static final SuggestionProvider<CommandSource> ALL_RECIPES = SuggestionProviders.register(new ResourceLocation("all_recipes"), (p_197501_0_, p_197501_1_) -> ISuggestionProvider.func_212476_a(((ISuggestionProvider)p_197501_0_.getSource()).getRecipeResourceLocations(), p_197501_1_));
    public static final SuggestionProvider<CommandSource> AVAILABLE_SOUNDS = SuggestionProviders.register(new ResourceLocation("available_sounds"), (p_197495_0_, p_197495_1_) -> ISuggestionProvider.suggestIterable(((ISuggestionProvider)p_197495_0_.getSource()).getSoundResourceLocations(), p_197495_1_));
    public static final SuggestionProvider<CommandSource> field_239574_d_ = SuggestionProviders.register(new ResourceLocation("available_biomes"), (p_239577_0_, p_239577_1_) -> ISuggestionProvider.suggestIterable(((ISuggestionProvider)p_239577_0_.getSource()).func_241861_q().getRegistry(Registry.BIOME_KEY).keySet(), p_239577_1_));
    public static final SuggestionProvider<CommandSource> SUMMONABLE_ENTITIES = SuggestionProviders.register(new ResourceLocation("summonable_entities"), (p_201210_0_, p_201210_1_) -> ISuggestionProvider.func_201725_a(Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable), p_201210_1_, EntityType::getKey, p_201209_0_ -> new TranslationTextComponent(Util.makeTranslationKey("entity", EntityType.getKey(p_201209_0_)))));

    public static <S extends ISuggestionProvider> SuggestionProvider<S> register(ResourceLocation id, SuggestionProvider<ISuggestionProvider> provider) {
        if (REGISTRY.containsKey(id)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + String.valueOf(id));
        }
        REGISTRY.put(id, provider);
        return new Wrapper(id, provider);
    }

    public static SuggestionProvider<ISuggestionProvider> get(ResourceLocation id) {
        return REGISTRY.getOrDefault(id, ASK_SERVER);
    }

    public static ResourceLocation getId(SuggestionProvider<ISuggestionProvider> provider) {
        return provider instanceof Wrapper ? ((Wrapper)provider).id : ASK_SERVER_ID;
    }

    public static SuggestionProvider<ISuggestionProvider> ensureKnown(SuggestionProvider<ISuggestionProvider> provider) {
        return provider instanceof Wrapper ? provider : ASK_SERVER;
    }

    public static class Wrapper
    implements SuggestionProvider<ISuggestionProvider> {
        private final SuggestionProvider<ISuggestionProvider> provider;
        private final ResourceLocation id;

        public Wrapper(ResourceLocation idIn, SuggestionProvider<ISuggestionProvider> providerIn) {
            this.provider = providerIn;
            this.id = idIn;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ISuggestionProvider> p_getSuggestions_1_, SuggestionsBuilder p_getSuggestions_2_) throws CommandSyntaxException {
            return this.provider.getSuggestions(p_getSuggestions_1_, p_getSuggestions_2_);
        }
    }
}
