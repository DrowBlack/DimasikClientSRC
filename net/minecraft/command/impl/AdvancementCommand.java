package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class AdvancementCommand {
    private static final SuggestionProvider<CommandSource> SUGGEST_ADVANCEMENTS = (p_198206_0_, p_198206_1_) -> {
        Collection<Advancement> collection = ((CommandSource)p_198206_0_.getSource()).getServer().getAdvancementManager().getAllAdvancements();
        return ISuggestionProvider.func_212476_a(collection.stream().map(Advancement::getId), p_198206_1_);
    };

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("advancement").requires(p_198205_0_ -> p_198205_0_.hasPermissionLevel(2))).then(Commands.literal("grant").then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)Commands.literal("only").then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198202_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198202_0_.getSource(), EntityArgument.getPlayers(p_198202_0_, "targets"), Action.GRANT, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198202_0_, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198209_0_, p_198209_1_) -> ISuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_198209_0_, "advancement").getCriteria().keySet(), p_198209_1_)).executes(p_198212_0_ -> AdvancementCommand.updateCriterion((CommandSource)p_198212_0_.getSource(), EntityArgument.getPlayers(p_198212_0_, "targets"), Action.GRANT, ResourceLocationArgument.getAdvancement(p_198212_0_, "advancement"), StringArgumentType.getString(p_198212_0_, "criterion"))))))).then(Commands.literal("from").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198215_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198215_0_.getSource(), EntityArgument.getPlayers(p_198215_0_, "targets"), Action.GRANT, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198215_0_, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198204_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198204_0_.getSource(), EntityArgument.getPlayers(p_198204_0_, "targets"), Action.GRANT, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198204_0_, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198211_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198211_0_.getSource(), EntityArgument.getPlayers(p_198211_0_, "targets"), Action.GRANT, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198211_0_, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes(p_198217_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198217_0_.getSource(), EntityArgument.getPlayers(p_198217_0_, "targets"), Action.GRANT, ((CommandSource)p_198217_0_.getSource()).getServer().getAdvancementManager().getAllAdvancements())))))).then(Commands.literal("revoke").then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)Commands.literal("only").then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198198_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198198_0_.getSource(), EntityArgument.getPlayers(p_198198_0_, "targets"), Action.REVOKE, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198198_0_, "advancement"), Mode.ONLY)))).then(Commands.argument("criterion", StringArgumentType.greedyString()).suggests((p_198210_0_, p_198210_1_) -> ISuggestionProvider.suggest(ResourceLocationArgument.getAdvancement(p_198210_0_, "advancement").getCriteria().keySet(), p_198210_1_)).executes(p_198200_0_ -> AdvancementCommand.updateCriterion((CommandSource)p_198200_0_.getSource(), EntityArgument.getPlayers(p_198200_0_, "targets"), Action.REVOKE, ResourceLocationArgument.getAdvancement(p_198200_0_, "advancement"), StringArgumentType.getString(p_198200_0_, "criterion"))))))).then(Commands.literal("from").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198208_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198208_0_.getSource(), EntityArgument.getPlayers(p_198208_0_, "targets"), Action.REVOKE, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198208_0_, "advancement"), Mode.FROM)))))).then(Commands.literal("until").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198201_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198201_0_.getSource(), EntityArgument.getPlayers(p_198201_0_, "targets"), Action.REVOKE, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198201_0_, "advancement"), Mode.UNTIL)))))).then(Commands.literal("through").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("advancement", ResourceLocationArgument.resourceLocation()).suggests(SUGGEST_ADVANCEMENTS).executes(p_198197_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198197_0_.getSource(), EntityArgument.getPlayers(p_198197_0_, "targets"), Action.REVOKE, AdvancementCommand.getMatchingAdvancements(ResourceLocationArgument.getAdvancement(p_198197_0_, "advancement"), Mode.THROUGH)))))).then(Commands.literal("everything").executes(p_198213_0_ -> AdvancementCommand.forEachAdvancement((CommandSource)p_198213_0_.getSource(), EntityArgument.getPlayers(p_198213_0_, "targets"), Action.REVOKE, ((CommandSource)p_198213_0_.getSource()).getServer().getAdvancementManager().getAllAdvancements()))))));
    }

    private static int forEachAdvancement(CommandSource source, Collection<ServerPlayerEntity> targets, Action action, Collection<Advancement> advancements) {
        int i = 0;
        for (ServerPlayerEntity serverplayerentity : targets) {
            i += action.applyToAdvancements(serverplayerentity, advancements);
        }
        if (i == 0) {
            if (advancements.size() == 1) {
                if (targets.size() == 1) {
                    throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".one.to.one.failure", advancements.iterator().next().getDisplayText(), targets.iterator().next().getDisplayName()));
                }
                throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".one.to.many.failure", advancements.iterator().next().getDisplayText(), targets.size()));
            }
            if (targets.size() == 1) {
                throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".many.to.one.failure", advancements.size(), targets.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".many.to.many.failure", advancements.size(), targets.size()));
        }
        if (advancements.size() == 1) {
            if (targets.size() == 1) {
                source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".one.to.one.success", advancements.iterator().next().getDisplayText(), targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".one.to.many.success", advancements.iterator().next().getDisplayText(), targets.size()), true);
            }
        } else if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".many.to.one.success", advancements.size(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".many.to.many.success", advancements.size(), targets.size()), true);
        }
        return i;
    }

    private static int updateCriterion(CommandSource source, Collection<ServerPlayerEntity> targets, Action action, Advancement advancementIn, String criterionName) {
        int i = 0;
        if (!advancementIn.getCriteria().containsKey(criterionName)) {
            throw new CommandException(new TranslationTextComponent("commands.advancement.criterionNotFound", advancementIn.getDisplayText(), criterionName));
        }
        for (ServerPlayerEntity serverplayerentity : targets) {
            if (!action.applyToCriterion(serverplayerentity, advancementIn, criterionName)) continue;
            ++i;
        }
        if (i == 0) {
            if (targets.size() == 1) {
                throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".criterion.to.one.failure", criterionName, advancementIn.getDisplayText(), targets.iterator().next().getDisplayName()));
            }
            throw new CommandException(new TranslationTextComponent(action.getPrefix() + ".criterion.to.many.failure", criterionName, advancementIn.getDisplayText(), targets.size()));
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".criterion.to.one.success", criterionName, advancementIn.getDisplayText(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent(action.getPrefix() + ".criterion.to.many.success", criterionName, advancementIn.getDisplayText(), targets.size()), true);
        }
        return i;
    }

    private static List<Advancement> getMatchingAdvancements(Advancement advancementIn, Mode mode) {
        ArrayList<Advancement> list = Lists.newArrayList();
        if (mode.includesParents) {
            for (Advancement advancement = advancementIn.getParent(); advancement != null; advancement = advancement.getParent()) {
                list.add(advancement);
            }
        }
        list.add(advancementIn);
        if (mode.includesChildren) {
            AdvancementCommand.addAllChildren(advancementIn, list);
        }
        return list;
    }

    private static void addAllChildren(Advancement advancementIn, List<Advancement> list) {
        for (Advancement advancement : advancementIn.getChildren()) {
            list.add(advancement);
            AdvancementCommand.addAllChildren(advancement, list);
        }
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    static enum Action {
        GRANT("grant"){

            @Override
            protected boolean applyToAdvancement(ServerPlayerEntity player, Advancement advancementIn) {
                AdvancementProgress advancementprogress = player.getAdvancements().getProgress(advancementIn);
                if (advancementprogress.isDone()) {
                    return false;
                }
                for (String s : advancementprogress.getRemaningCriteria()) {
                    player.getAdvancements().grantCriterion(advancementIn, s);
                }
                return true;
            }

            @Override
            protected boolean applyToCriterion(ServerPlayerEntity player, Advancement advancementIn, String criterionName) {
                return player.getAdvancements().grantCriterion(advancementIn, criterionName);
            }
        }
        ,
        REVOKE("revoke"){

            @Override
            protected boolean applyToAdvancement(ServerPlayerEntity player, Advancement advancementIn) {
                AdvancementProgress advancementprogress = player.getAdvancements().getProgress(advancementIn);
                if (!advancementprogress.hasProgress()) {
                    return false;
                }
                for (String s : advancementprogress.getCompletedCriteria()) {
                    player.getAdvancements().revokeCriterion(advancementIn, s);
                }
                return true;
            }

            @Override
            protected boolean applyToCriterion(ServerPlayerEntity player, Advancement advancementIn, String criterionName) {
                return player.getAdvancements().revokeCriterion(advancementIn, criterionName);
            }
        };

        private final String prefix;

        private Action(String name) {
            this.prefix = "commands.advancement." + name;
        }

        public int applyToAdvancements(ServerPlayerEntity player, Iterable<Advancement> advancements) {
            int i = 0;
            for (Advancement advancement : advancements) {
                if (!this.applyToAdvancement(player, advancement)) continue;
                ++i;
            }
            return i;
        }

        protected abstract boolean applyToAdvancement(ServerPlayerEntity var1, Advancement var2);

        protected abstract boolean applyToCriterion(ServerPlayerEntity var1, Advancement var2, String var3);

        protected String getPrefix() {
            return this.prefix;
        }
    }

    static enum Mode {
        ONLY(false, false),
        THROUGH(true, true),
        FROM(false, true),
        UNTIL(true, false),
        EVERYTHING(true, true);

        private final boolean includesParents;
        private final boolean includesChildren;

        private Mode(boolean includesParentsIn, boolean includesChildrenIn) {
            this.includesParents = includesParentsIn;
            this.includesChildren = includesChildrenIn;
        }
    }
}
