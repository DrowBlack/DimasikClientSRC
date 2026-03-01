package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.MinMaxBoundsWrapped;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntitySelectorParser;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;

public class EntityOptions {
    private static final Map<String, OptionHandler> REGISTRY = Maps.newHashMap();
    public static final DynamicCommandExceptionType UNKNOWN_ENTITY_OPTION = new DynamicCommandExceptionType(p_208752_0_ -> new TranslationTextComponent("argument.entity.options.unknown", p_208752_0_));
    public static final DynamicCommandExceptionType INAPPLICABLE_ENTITY_OPTION = new DynamicCommandExceptionType(p_208726_0_ -> new TranslationTextComponent("argument.entity.options.inapplicable", p_208726_0_));
    public static final SimpleCommandExceptionType NEGATIVE_DISTANCE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.distance.negative"));
    public static final SimpleCommandExceptionType NEGATIVE_LEVEL = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.level.negative"));
    public static final SimpleCommandExceptionType NONPOSITIVE_LIMIT = new SimpleCommandExceptionType(new TranslationTextComponent("argument.entity.options.limit.toosmall"));
    public static final DynamicCommandExceptionType INVALID_SORT = new DynamicCommandExceptionType(p_208749_0_ -> new TranslationTextComponent("argument.entity.options.sort.irreversible", p_208749_0_));
    public static final DynamicCommandExceptionType INVALID_GAME_MODE = new DynamicCommandExceptionType(p_208740_0_ -> new TranslationTextComponent("argument.entity.options.mode.invalid", p_208740_0_));
    public static final DynamicCommandExceptionType INVALID_ENTITY_TYPE = new DynamicCommandExceptionType(p_208758_0_ -> new TranslationTextComponent("argument.entity.options.type.invalid", p_208758_0_));

    private static void register(String id, IFilter handler, Predicate<EntitySelectorParser> p_202024_2_, ITextComponent tooltip) {
        REGISTRY.put(id, new OptionHandler(handler, p_202024_2_, tooltip));
    }

    public static void registerOptions() {
        if (REGISTRY.isEmpty()) {
            EntityOptions.register("name", p_197440_0_ -> {
                int i = p_197440_0_.getReader().getCursor();
                boolean flag = p_197440_0_.shouldInvertValue();
                String s = p_197440_0_.getReader().readString();
                if (p_197440_0_.hasNameNotEquals() && !flag) {
                    p_197440_0_.getReader().setCursor(i);
                    throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197440_0_.getReader(), "name");
                }
                if (flag) {
                    p_197440_0_.setHasNameNotEquals(true);
                } else {
                    p_197440_0_.setHasNameEquals(true);
                }
                p_197440_0_.addFilter(p_197446_2_ -> p_197446_2_.getName().getString().equals(s) != flag);
            }, p_202016_0_ -> !p_202016_0_.hasNameEquals(), new TranslationTextComponent("argument.entity.options.name.description"));
            EntityOptions.register("distance", p_197439_0_ -> {
                int i = p_197439_0_.getReader().getCursor();
                MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromReader(p_197439_0_.getReader());
                if (minmaxbounds$floatbound.getMin() != null && ((Float)minmaxbounds$floatbound.getMin()).floatValue() < 0.0f || minmaxbounds$floatbound.getMax() != null && ((Float)minmaxbounds$floatbound.getMax()).floatValue() < 0.0f) {
                    p_197439_0_.getReader().setCursor(i);
                    throw NEGATIVE_DISTANCE.createWithContext(p_197439_0_.getReader());
                }
                p_197439_0_.setDistance(minmaxbounds$floatbound);
                p_197439_0_.setCurrentWorldOnly();
            }, p_202020_0_ -> p_202020_0_.getDistance().isUnbounded(), new TranslationTextComponent("argument.entity.options.distance.description"));
            EntityOptions.register("level", p_197438_0_ -> {
                int i = p_197438_0_.getReader().getCursor();
                MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(p_197438_0_.getReader());
                if (minmaxbounds$intbound.getMin() != null && (Integer)minmaxbounds$intbound.getMin() < 0 || minmaxbounds$intbound.getMax() != null && (Integer)minmaxbounds$intbound.getMax() < 0) {
                    p_197438_0_.getReader().setCursor(i);
                    throw NEGATIVE_LEVEL.createWithContext(p_197438_0_.getReader());
                }
                p_197438_0_.setLevel(minmaxbounds$intbound);
                p_197438_0_.setIncludeNonPlayers(false);
            }, p_202019_0_ -> p_202019_0_.getLevel().isUnbounded(), new TranslationTextComponent("argument.entity.options.level.description"));
            EntityOptions.register("x", p_197437_0_ -> {
                p_197437_0_.setCurrentWorldOnly();
                p_197437_0_.setX(p_197437_0_.getReader().readDouble());
            }, p_202022_0_ -> p_202022_0_.getX() == null, new TranslationTextComponent("argument.entity.options.x.description"));
            EntityOptions.register("y", p_197442_0_ -> {
                p_197442_0_.setCurrentWorldOnly();
                p_197442_0_.setY(p_197442_0_.getReader().readDouble());
            }, p_202021_0_ -> p_202021_0_.getY() == null, new TranslationTextComponent("argument.entity.options.y.description"));
            EntityOptions.register("z", p_197464_0_ -> {
                p_197464_0_.setCurrentWorldOnly();
                p_197464_0_.setZ(p_197464_0_.getReader().readDouble());
            }, p_202029_0_ -> p_202029_0_.getZ() == null, new TranslationTextComponent("argument.entity.options.z.description"));
            EntityOptions.register("dx", p_197460_0_ -> {
                p_197460_0_.setCurrentWorldOnly();
                p_197460_0_.setDx(p_197460_0_.getReader().readDouble());
            }, p_202027_0_ -> p_202027_0_.getDx() == null, new TranslationTextComponent("argument.entity.options.dx.description"));
            EntityOptions.register("dy", p_197463_0_ -> {
                p_197463_0_.setCurrentWorldOnly();
                p_197463_0_.setDy(p_197463_0_.getReader().readDouble());
            }, p_202026_0_ -> p_202026_0_.getDy() == null, new TranslationTextComponent("argument.entity.options.dy.description"));
            EntityOptions.register("dz", p_197458_0_ -> {
                p_197458_0_.setCurrentWorldOnly();
                p_197458_0_.setDz(p_197458_0_.getReader().readDouble());
            }, p_202030_0_ -> p_202030_0_.getDz() == null, new TranslationTextComponent("argument.entity.options.dz.description"));
            EntityOptions.register("x_rotation", p_197462_0_ -> p_197462_0_.setXRotation(MinMaxBoundsWrapped.fromReader(p_197462_0_.getReader(), true, MathHelper::wrapDegrees)), p_202028_0_ -> p_202028_0_.getXRotation() == MinMaxBoundsWrapped.UNBOUNDED, new TranslationTextComponent("argument.entity.options.x_rotation.description"));
            EntityOptions.register("y_rotation", p_197461_0_ -> p_197461_0_.setYRotation(MinMaxBoundsWrapped.fromReader(p_197461_0_.getReader(), true, MathHelper::wrapDegrees)), p_202036_0_ -> p_202036_0_.getYRotation() == MinMaxBoundsWrapped.UNBOUNDED, new TranslationTextComponent("argument.entity.options.y_rotation.description"));
            EntityOptions.register("limit", p_197456_0_ -> {
                int i = p_197456_0_.getReader().getCursor();
                int j = p_197456_0_.getReader().readInt();
                if (j < 1) {
                    p_197456_0_.getReader().setCursor(i);
                    throw NONPOSITIVE_LIMIT.createWithContext(p_197456_0_.getReader());
                }
                p_197456_0_.setLimit(j);
                p_197456_0_.setLimited(true);
            }, p_202035_0_ -> !p_202035_0_.isCurrentEntity() && !p_202035_0_.isLimited(), new TranslationTextComponent("argument.entity.options.limit.description"));
            EntityOptions.register("sort", p_197455_0_ -> {
                int i = p_197455_0_.getReader().getCursor();
                String s = p_197455_0_.getReader().readUnquotedString();
                p_197455_0_.setSuggestionHandler((p_202056_0_, p_202056_1_) -> ISuggestionProvider.suggest(Arrays.asList("nearest", "furthest", "random", "arbitrary"), p_202056_0_));
                int b0 = -1;
                switch (s.hashCode()) {
                    case -938285885: {
                        if (!s.equals("random")) break;
                        b0 = 2;
                        break;
                    }
                    case 1510793967: {
                        if (!s.equals("furthest")) break;
                        b0 = 1;
                        break;
                    }
                    case 1780188658: {
                        if (!s.equals("arbitrary")) break;
                        b0 = 3;
                        break;
                    }
                    case 1825779806: {
                        if (!s.equals("nearest")) break;
                        b0 = 0;
                    }
                }
                p_197455_0_.setSorter(switch (b0) {
                    case 0 -> EntitySelectorParser.NEAREST;
                    case 1 -> EntitySelectorParser.FURTHEST;
                    case 2 -> EntitySelectorParser.RANDOM;
                    case 3 -> EntitySelectorParser.ARBITRARY;
                    default -> {
                        p_197455_0_.getReader().setCursor(i);
                        throw INVALID_SORT.createWithContext(p_197455_0_.getReader(), s);
                    }
                });
                p_197455_0_.setSorted(true);
            }, p_202043_0_ -> !p_202043_0_.isCurrentEntity() && !p_202043_0_.isSorted(), new TranslationTextComponent("argument.entity.options.sort.description"));
            EntityOptions.register("gamemode", p_197452_0_ -> {
                p_197452_0_.setSuggestionHandler((p_202018_1_, p_202018_2_) -> {
                    String s1 = p_202018_1_.getRemaining().toLowerCase(Locale.ROOT);
                    boolean flag1 = !p_197452_0_.hasGamemodeNotEquals();
                    boolean flag2 = true;
                    if (!s1.isEmpty()) {
                        if (s1.charAt(0) == '!') {
                            flag1 = false;
                            s1 = s1.substring(1);
                        } else {
                            flag2 = false;
                        }
                    }
                    for (GameType gametype1 : GameType.values()) {
                        if (gametype1 == GameType.NOT_SET || !gametype1.getName().toLowerCase(Locale.ROOT).startsWith(s1)) continue;
                        if (flag2) {
                            p_202018_1_.suggest("!" + gametype1.getName());
                        }
                        if (!flag1) continue;
                        p_202018_1_.suggest(gametype1.getName());
                    }
                    return p_202018_1_.buildFuture();
                });
                int i = p_197452_0_.getReader().getCursor();
                boolean flag = p_197452_0_.shouldInvertValue();
                if (p_197452_0_.hasGamemodeNotEquals() && !flag) {
                    p_197452_0_.getReader().setCursor(i);
                    throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197452_0_.getReader(), "gamemode");
                }
                String s = p_197452_0_.getReader().readUnquotedString();
                GameType gametype = GameType.parseGameTypeWithDefault(s, GameType.NOT_SET);
                if (gametype == GameType.NOT_SET) {
                    p_197452_0_.getReader().setCursor(i);
                    throw INVALID_GAME_MODE.createWithContext(p_197452_0_.getReader(), s);
                }
                p_197452_0_.setIncludeNonPlayers(false);
                p_197452_0_.addFilter(p_202055_2_ -> {
                    if (!(p_202055_2_ instanceof ServerPlayerEntity)) {
                        return false;
                    }
                    GameType gametype1 = ((ServerPlayerEntity)p_202055_2_).interactionManager.getGameType();
                    return flag ? gametype1 != gametype : gametype1 == gametype;
                });
                if (flag) {
                    p_197452_0_.setHasGamemodeNotEquals(true);
                } else {
                    p_197452_0_.setHasGamemodeEquals(true);
                }
            }, p_202048_0_ -> !p_202048_0_.hasGamemodeEquals(), new TranslationTextComponent("argument.entity.options.gamemode.description"));
            EntityOptions.register("team", p_197449_0_ -> {
                boolean flag = p_197449_0_.shouldInvertValue();
                String s = p_197449_0_.getReader().readUnquotedString();
                p_197449_0_.addFilter(p_197454_2_ -> {
                    if (!(p_197454_2_ instanceof LivingEntity)) {
                        return false;
                    }
                    Team team = p_197454_2_.getTeam();
                    String s1 = team == null ? "" : team.getName();
                    return s1.equals(s) != flag;
                });
                if (flag) {
                    p_197449_0_.setHasTeamNotEquals(true);
                } else {
                    p_197449_0_.setHasTeamEquals(true);
                }
            }, p_202038_0_ -> !p_202038_0_.hasTeamEquals(), new TranslationTextComponent("argument.entity.options.team.description"));
            EntityOptions.register("type", p_197447_0_ -> {
                p_197447_0_.setSuggestionHandler((p_202052_1_, p_202052_2_) -> {
                    ISuggestionProvider.suggestIterable(Registry.ENTITY_TYPE.keySet(), p_202052_1_, String.valueOf('!'));
                    ISuggestionProvider.suggestIterable(EntityTypeTags.getCollection().getRegisteredTags(), p_202052_1_, "!#");
                    if (!p_197447_0_.isTypeLimitedInversely()) {
                        ISuggestionProvider.suggestIterable(Registry.ENTITY_TYPE.keySet(), p_202052_1_);
                        ISuggestionProvider.suggestIterable(EntityTypeTags.getCollection().getRegisteredTags(), p_202052_1_, String.valueOf('#'));
                    }
                    return p_202052_1_.buildFuture();
                });
                int i = p_197447_0_.getReader().getCursor();
                boolean flag = p_197447_0_.shouldInvertValue();
                if (p_197447_0_.isTypeLimitedInversely() && !flag) {
                    p_197447_0_.getReader().setCursor(i);
                    throw INAPPLICABLE_ENTITY_OPTION.createWithContext(p_197447_0_.getReader(), "type");
                }
                if (flag) {
                    p_197447_0_.setTypeLimitedInversely();
                }
                if (p_197447_0_.func_218115_f()) {
                    ResourceLocation resourcelocation = ResourceLocation.read(p_197447_0_.getReader());
                    p_197447_0_.addFilter(p_239573_2_ -> p_239573_2_.getServer().func_244266_aF().getEntityTypeTags().getTagByID(resourcelocation).contains(p_239573_2_.getType()) != flag);
                } else {
                    ResourceLocation resourcelocation1 = ResourceLocation.read(p_197447_0_.getReader());
                    EntityType<?> entitytype = Registry.ENTITY_TYPE.getOptional(resourcelocation1).orElseThrow(() -> {
                        p_197447_0_.getReader().setCursor(i);
                        return INVALID_ENTITY_TYPE.createWithContext(p_197447_0_.getReader(), resourcelocation1.toString());
                    });
                    if (Objects.equals(EntityType.PLAYER, entitytype) && !flag) {
                        p_197447_0_.setIncludeNonPlayers(false);
                    }
                    p_197447_0_.addFilter(p_202057_2_ -> Objects.equals(entitytype, p_202057_2_.getType()) != flag);
                    if (!flag) {
                        p_197447_0_.func_218114_a(entitytype);
                    }
                }
            }, p_202047_0_ -> !p_202047_0_.isTypeLimited(), new TranslationTextComponent("argument.entity.options.type.description"));
            EntityOptions.register("tag", p_197448_0_ -> {
                boolean flag = p_197448_0_.shouldInvertValue();
                String s = p_197448_0_.getReader().readUnquotedString();
                p_197448_0_.addFilter(p_197466_2_ -> {
                    if ("".equals(s)) {
                        return p_197466_2_.getTags().isEmpty() != flag;
                    }
                    return p_197466_2_.getTags().contains(s) != flag;
                });
            }, p_202041_0_ -> true, new TranslationTextComponent("argument.entity.options.tag.description"));
            EntityOptions.register("nbt", p_197450_0_ -> {
                boolean flag = p_197450_0_.shouldInvertValue();
                CompoundNBT compoundnbt = new JsonToNBT(p_197450_0_.getReader()).readStruct();
                p_197450_0_.addFilter(p_197443_2_ -> {
                    ItemStack itemstack;
                    CompoundNBT compoundnbt1 = p_197443_2_.writeWithoutTypeId(new CompoundNBT());
                    if (p_197443_2_ instanceof ServerPlayerEntity && !(itemstack = ((ServerPlayerEntity)p_197443_2_).inventory.getCurrentItem()).isEmpty()) {
                        compoundnbt1.put("SelectedItem", itemstack.write(new CompoundNBT()));
                    }
                    return NBTUtil.areNBTEquals(compoundnbt, compoundnbt1, true) != flag;
                });
            }, p_202046_0_ -> true, new TranslationTextComponent("argument.entity.options.nbt.description"));
            EntityOptions.register("scores", p_197457_0_ -> {
                StringReader stringreader = p_197457_0_.getReader();
                HashMap<String, MinMaxBounds.IntBound> map = Maps.newHashMap();
                stringreader.expect('{');
                stringreader.skipWhitespace();
                while (stringreader.canRead() && stringreader.peek() != '}') {
                    stringreader.skipWhitespace();
                    String s = stringreader.readUnquotedString();
                    stringreader.skipWhitespace();
                    stringreader.expect('=');
                    stringreader.skipWhitespace();
                    MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromReader(stringreader);
                    map.put(s, minmaxbounds$intbound);
                    stringreader.skipWhitespace();
                    if (!stringreader.canRead() || stringreader.peek() != ',') continue;
                    stringreader.skip();
                }
                stringreader.expect('}');
                if (!map.isEmpty()) {
                    p_197457_0_.addFilter(p_197465_1_ -> {
                        ServerScoreboard scoreboard = p_197465_1_.getServer().getScoreboard();
                        String s1 = p_197465_1_.getScoreboardName();
                        for (Map.Entry entry : map.entrySet()) {
                            ScoreObjective scoreobjective = scoreboard.getObjective((String)entry.getKey());
                            if (scoreobjective == null) {
                                return false;
                            }
                            if (!scoreboard.entityHasObjective(s1, scoreobjective)) {
                                return false;
                            }
                            Score score = scoreboard.getOrCreateScore(s1, scoreobjective);
                            int i = score.getScorePoints();
                            if (((MinMaxBounds.IntBound)entry.getValue()).test(i)) continue;
                            return false;
                        }
                        return true;
                    });
                }
                p_197457_0_.setHasScores(true);
            }, p_202033_0_ -> !p_202033_0_.hasScores(), new TranslationTextComponent("argument.entity.options.scores.description"));
            EntityOptions.register("advancements", p_197453_0_ -> {
                StringReader stringreader = p_197453_0_.getReader();
                HashMap<ResourceLocation, Predicate<AdvancementProgress>> map = Maps.newHashMap();
                stringreader.expect('{');
                stringreader.skipWhitespace();
                while (stringreader.canRead() && stringreader.peek() != '}') {
                    stringreader.skipWhitespace();
                    ResourceLocation resourcelocation = ResourceLocation.read(stringreader);
                    stringreader.skipWhitespace();
                    stringreader.expect('=');
                    stringreader.skipWhitespace();
                    if (stringreader.canRead() && stringreader.peek() == '{') {
                        HashMap<String, Predicate<CriterionProgress>> map1 = Maps.newHashMap();
                        stringreader.skipWhitespace();
                        stringreader.expect('{');
                        stringreader.skipWhitespace();
                        while (stringreader.canRead() && stringreader.peek() != '}') {
                            stringreader.skipWhitespace();
                            String s = stringreader.readUnquotedString();
                            stringreader.skipWhitespace();
                            stringreader.expect('=');
                            stringreader.skipWhitespace();
                            boolean flag1 = stringreader.readBoolean();
                            map1.put(s, p_197444_1_ -> p_197444_1_.isObtained() == flag1);
                            stringreader.skipWhitespace();
                            if (!stringreader.canRead() || stringreader.peek() != ',') continue;
                            stringreader.skip();
                        }
                        stringreader.skipWhitespace();
                        stringreader.expect('}');
                        stringreader.skipWhitespace();
                        map.put(resourcelocation, p_197435_1_ -> {
                            for (Map.Entry entry : map1.entrySet()) {
                                CriterionProgress criterionprogress = p_197435_1_.getCriterionProgress((String)entry.getKey());
                                if (criterionprogress != null && ((Predicate)entry.getValue()).test(criterionprogress)) continue;
                                return false;
                            }
                            return true;
                        });
                    } else {
                        boolean flag = stringreader.readBoolean();
                        map.put(resourcelocation, p_197451_1_ -> p_197451_1_.isDone() == flag);
                    }
                    stringreader.skipWhitespace();
                    if (!stringreader.canRead() || stringreader.peek() != ',') continue;
                    stringreader.skip();
                }
                stringreader.expect('}');
                if (!map.isEmpty()) {
                    p_197453_0_.addFilter(p_197441_1_ -> {
                        if (!(p_197441_1_ instanceof ServerPlayerEntity)) {
                            return false;
                        }
                        ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_197441_1_;
                        PlayerAdvancements playeradvancements = serverplayerentity.getAdvancements();
                        AdvancementManager advancementmanager = serverplayerentity.getServer().getAdvancementManager();
                        for (Map.Entry entry : map.entrySet()) {
                            Advancement advancement = advancementmanager.getAdvancement((ResourceLocation)entry.getKey());
                            if (advancement != null && ((Predicate)entry.getValue()).test(playeradvancements.getProgress(advancement))) continue;
                            return false;
                        }
                        return true;
                    });
                    p_197453_0_.setIncludeNonPlayers(false);
                }
                p_197453_0_.setHasAdvancements(true);
            }, p_202032_0_ -> !p_202032_0_.hasAdvancements(), new TranslationTextComponent("argument.entity.options.advancements.description"));
            EntityOptions.register("predicate", p_229367_0_ -> {
                boolean flag = p_229367_0_.shouldInvertValue();
                ResourceLocation resourcelocation = ResourceLocation.read(p_229367_0_.getReader());
                p_229367_0_.addFilter(p_229366_2_ -> {
                    if (!(p_229366_2_.world instanceof ServerWorld)) {
                        return false;
                    }
                    ServerWorld serverworld = (ServerWorld)p_229366_2_.world;
                    ILootCondition ilootcondition = serverworld.getServer().func_229736_aP_().func_227517_a_(resourcelocation);
                    if (ilootcondition == null) {
                        return false;
                    }
                    LootContext lootcontext = new LootContext.Builder(serverworld).withParameter(LootParameters.THIS_ENTITY, p_229366_2_).withParameter(LootParameters.field_237457_g_, p_229366_2_.getPositionVec()).build(LootParameterSets.SELECTOR);
                    return flag ^ ilootcondition.test(lootcontext);
                });
            }, p_229365_0_ -> true, new TranslationTextComponent("argument.entity.options.predicate.description"));
        }
    }

    public static IFilter get(EntitySelectorParser parser, String id, int cursor) throws CommandSyntaxException {
        OptionHandler entityoptions$optionhandler = REGISTRY.get(id);
        if (entityoptions$optionhandler != null) {
            if (entityoptions$optionhandler.canHandle.test(parser)) {
                return entityoptions$optionhandler.handler;
            }
            throw INAPPLICABLE_ENTITY_OPTION.createWithContext(parser.getReader(), id);
        }
        parser.getReader().setCursor(cursor);
        throw UNKNOWN_ENTITY_OPTION.createWithContext(parser.getReader(), id);
    }

    public static void suggestOptions(EntitySelectorParser parser, SuggestionsBuilder builder) {
        String s = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (Map.Entry<String, OptionHandler> entry : REGISTRY.entrySet()) {
            if (!entry.getValue().canHandle.test(parser) || !entry.getKey().toLowerCase(Locale.ROOT).startsWith(s)) continue;
            builder.suggest(entry.getKey() + "=", (Message)entry.getValue().tooltip);
        }
    }

    static class OptionHandler {
        public final IFilter handler;
        public final Predicate<EntitySelectorParser> canHandle;
        public final ITextComponent tooltip;

        private OptionHandler(IFilter handlerIn, Predicate<EntitySelectorParser> p_i48717_2_, ITextComponent tooltipIn) {
            this.handler = handlerIn;
            this.canHandle = p_i48717_2_;
            this.tooltip = tooltipIn;
        }
    }

    public static interface IFilter {
        public void handle(EntitySelectorParser var1) throws CommandSyntaxException;
    }
}
