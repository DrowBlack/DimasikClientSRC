package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType SET_POINTS_INVALID_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.experience.set.points.invalid"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> literalcommandnode = dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("experience").requires(p_198442_0_ -> p_198442_0_.hasPermissionLevel(2))).then(Commands.literal("add").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer()).executes(p_198445_0_ -> ExperienceCommand.addExperience((CommandSource)p_198445_0_.getSource(), EntityArgument.getPlayers(p_198445_0_, "targets"), IntegerArgumentType.getInteger(p_198445_0_, "amount"), Type.POINTS))).then(Commands.literal("points").executes(p_198447_0_ -> ExperienceCommand.addExperience((CommandSource)p_198447_0_.getSource(), EntityArgument.getPlayers(p_198447_0_, "targets"), IntegerArgumentType.getInteger(p_198447_0_, "amount"), Type.POINTS)))).then(Commands.literal("levels").executes(p_198436_0_ -> ExperienceCommand.addExperience((CommandSource)p_198436_0_.getSource(), EntityArgument.getPlayers(p_198436_0_, "targets"), IntegerArgumentType.getInteger(p_198436_0_, "amount"), Type.LEVELS))))))).then(Commands.literal("set").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer(0)).executes(p_198439_0_ -> ExperienceCommand.setExperience((CommandSource)p_198439_0_.getSource(), EntityArgument.getPlayers(p_198439_0_, "targets"), IntegerArgumentType.getInteger(p_198439_0_, "amount"), Type.POINTS))).then(Commands.literal("points").executes(p_198444_0_ -> ExperienceCommand.setExperience((CommandSource)p_198444_0_.getSource(), EntityArgument.getPlayers(p_198444_0_, "targets"), IntegerArgumentType.getInteger(p_198444_0_, "amount"), Type.POINTS)))).then(Commands.literal("levels").executes(p_198440_0_ -> ExperienceCommand.setExperience((CommandSource)p_198440_0_.getSource(), EntityArgument.getPlayers(p_198440_0_, "targets"), IntegerArgumentType.getInteger(p_198440_0_, "amount"), Type.LEVELS))))))).then(Commands.literal("query").then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.player()).then((ArgumentBuilder<CommandSource, ?>)Commands.literal("points").executes(p_198435_0_ -> ExperienceCommand.queryExperience((CommandSource)p_198435_0_.getSource(), EntityArgument.getPlayer(p_198435_0_, "targets"), Type.POINTS)))).then(Commands.literal("levels").executes(p_198446_0_ -> ExperienceCommand.queryExperience((CommandSource)p_198446_0_.getSource(), EntityArgument.getPlayer(p_198446_0_, "targets"), Type.LEVELS))))));
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("xp").requires(p_198441_0_ -> p_198441_0_.hasPermissionLevel(2))).redirect(literalcommandnode));
    }

    private static int queryExperience(CommandSource source, ServerPlayerEntity player, Type type) {
        int i = type.xpGetter.applyAsInt(player);
        source.sendFeedback(new TranslationTextComponent("commands.experience.query." + type.name, player.getDisplayName(), i), false);
        return i;
    }

    private static int addExperience(CommandSource source, Collection<? extends ServerPlayerEntity> targets, int amount, Type type) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            type.xpAdder.accept(serverPlayerEntity, amount);
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.experience.add." + type.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.experience.add." + type.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
    }

    private static int setExperience(CommandSource source, Collection<? extends ServerPlayerEntity> targets, int amount, Type type) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            if (!type.xpSetter.test(serverPlayerEntity, amount)) continue;
            ++i;
        }
        if (i == 0) {
            throw SET_POINTS_INVALID_EXCEPTION.create();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.experience.set." + type.name + ".success.single", amount, targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.experience.set." + type.name + ".success.multiple", amount, targets.size()), true);
        }
        return targets.size();
    }

    static enum Type {
        POINTS("points", PlayerEntity::giveExperiencePoints, (p_198424_0_, p_198424_1_) -> {
            if (p_198424_1_ >= p_198424_0_.xpBarCap()) {
                return false;
            }
            p_198424_0_.func_195394_a((int)p_198424_1_);
            return true;
        }, p_198422_0_ -> MathHelper.floor(p_198422_0_.experience * (float)p_198422_0_.xpBarCap())),
        LEVELS("levels", ServerPlayerEntity::addExperienceLevel, (p_198425_0_, p_198425_1_) -> {
            p_198425_0_.setExperienceLevel((int)p_198425_1_);
            return true;
        }, p_198427_0_ -> p_198427_0_.experienceLevel);

        public final BiConsumer<ServerPlayerEntity, Integer> xpAdder;
        public final BiPredicate<ServerPlayerEntity, Integer> xpSetter;
        public final String name;
        private final ToIntFunction<ServerPlayerEntity> xpGetter;

        private Type(String nameIn, BiConsumer<ServerPlayerEntity, Integer> xpAdderIn, BiPredicate<ServerPlayerEntity, Integer> xpSetterIn, ToIntFunction<ServerPlayerEntity> xpGetterIn) {
            this.xpAdder = xpAdderIn;
            this.name = nameIn;
            this.xpSetter = xpSetterIn;
            this.xpGetter = xpGetterIn;
        }
    }
}
