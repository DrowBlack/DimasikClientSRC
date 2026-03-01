package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ObjectiveArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.util.text.TranslationTextComponent;

public class TriggerCommand {
    private static final SimpleCommandExceptionType NOT_PRIMED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.unprimed"));
    private static final SimpleCommandExceptionType NOT_A_TRIGGER = new SimpleCommandExceptionType(new TranslationTextComponent("commands.trigger.failed.invalid"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)Commands.literal("trigger").then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).suggests((p_198853_0_, p_198853_1_) -> TriggerCommand.suggestTriggers((CommandSource)p_198853_0_.getSource(), p_198853_1_)).executes(p_198854_0_ -> TriggerCommand.incrementTrigger((CommandSource)p_198854_0_.getSource(), TriggerCommand.checkValidTrigger(((CommandSource)p_198854_0_.getSource()).asPlayer(), ObjectiveArgument.getObjective(p_198854_0_, "objective"))))).then(Commands.literal("add").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("value", IntegerArgumentType.integer()).executes(p_198849_0_ -> TriggerCommand.addToTrigger((CommandSource)p_198849_0_.getSource(), TriggerCommand.checkValidTrigger(((CommandSource)p_198849_0_.getSource()).asPlayer(), ObjectiveArgument.getObjective(p_198849_0_, "objective")), IntegerArgumentType.getInteger(p_198849_0_, "value")))))).then(Commands.literal("set").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("value", IntegerArgumentType.integer()).executes(p_198855_0_ -> TriggerCommand.setTrigger((CommandSource)p_198855_0_.getSource(), TriggerCommand.checkValidTrigger(((CommandSource)p_198855_0_.getSource()).asPlayer(), ObjectiveArgument.getObjective(p_198855_0_, "objective")), IntegerArgumentType.getInteger(p_198855_0_, "value")))))));
    }

    public static CompletableFuture<Suggestions> suggestTriggers(CommandSource source, SuggestionsBuilder builder) {
        Entity entity = source.getEntity();
        ArrayList<String> list = Lists.newArrayList();
        if (entity != null) {
            ServerScoreboard scoreboard = source.getServer().getScoreboard();
            String s = entity.getScoreboardName();
            for (ScoreObjective scoreobjective : scoreboard.getScoreObjectives()) {
                Score score;
                if (scoreobjective.getCriteria() != ScoreCriteria.TRIGGER || !scoreboard.entityHasObjective(s, scoreobjective) || (score = scoreboard.getOrCreateScore(s, scoreobjective)).isLocked()) continue;
                list.add(scoreobjective.getName());
            }
        }
        return ISuggestionProvider.suggest(list, builder);
    }

    private static int addToTrigger(CommandSource source, Score objective, int amount) {
        objective.increaseScore(amount);
        source.sendFeedback(new TranslationTextComponent("commands.trigger.add.success", objective.getObjective().func_197890_e(), amount), true);
        return objective.getScorePoints();
    }

    private static int setTrigger(CommandSource source, Score objective, int value) {
        objective.setScorePoints(value);
        source.sendFeedback(new TranslationTextComponent("commands.trigger.set.success", objective.getObjective().func_197890_e(), value), true);
        return value;
    }

    private static int incrementTrigger(CommandSource source, Score objectives) {
        objectives.increaseScore(1);
        source.sendFeedback(new TranslationTextComponent("commands.trigger.simple.success", objectives.getObjective().func_197890_e()), true);
        return objectives.getScorePoints();
    }

    private static Score checkValidTrigger(ServerPlayerEntity player, ScoreObjective objective) throws CommandSyntaxException {
        String s;
        if (objective.getCriteria() != ScoreCriteria.TRIGGER) {
            throw NOT_A_TRIGGER.create();
        }
        Scoreboard scoreboard = player.getWorldScoreboard();
        if (!scoreboard.entityHasObjective(s = player.getScoreboardName(), objective)) {
            throw NOT_PRIMED.create();
        }
        Score score = scoreboard.getOrCreateScore(s, objective);
        if (score.isLocked()) {
            throw NOT_PRIMED.create();
        }
        score.setLocked(true);
        return score;
    }
}
