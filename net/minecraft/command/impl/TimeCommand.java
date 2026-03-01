package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class TimeCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("time").requires(p_198828_0_ -> p_198828_0_.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("set").then((ArgumentBuilder<CommandSource, ?>)Commands.literal("day").executes(p_198832_0_ -> TimeCommand.setTime((CommandSource)p_198832_0_.getSource(), 1000)))).then(Commands.literal("noon").executes(p_198825_0_ -> TimeCommand.setTime((CommandSource)p_198825_0_.getSource(), 6000)))).then(Commands.literal("night").executes(p_198822_0_ -> TimeCommand.setTime((CommandSource)p_198822_0_.getSource(), 13000)))).then(Commands.literal("midnight").executes(p_200563_0_ -> TimeCommand.setTime((CommandSource)p_200563_0_.getSource(), 18000)))).then(Commands.argument("time", TimeArgument.func_218091_a()).executes(p_200564_0_ -> TimeCommand.setTime((CommandSource)p_200564_0_.getSource(), IntegerArgumentType.getInteger(p_200564_0_, "time")))))).then(Commands.literal("add").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("time", TimeArgument.func_218091_a()).executes(p_198830_0_ -> TimeCommand.addTime((CommandSource)p_198830_0_.getSource(), IntegerArgumentType.getInteger(p_198830_0_, "time")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("query").then((ArgumentBuilder<CommandSource, ?>)Commands.literal("daytime").executes(p_198827_0_ -> TimeCommand.sendQueryResults((CommandSource)p_198827_0_.getSource(), TimeCommand.getDayTime(((CommandSource)p_198827_0_.getSource()).getWorld()))))).then(Commands.literal("gametime").executes(p_198821_0_ -> TimeCommand.sendQueryResults((CommandSource)p_198821_0_.getSource(), (int)(((CommandSource)p_198821_0_.getSource()).getWorld().getGameTime() % Integer.MAX_VALUE))))).then(Commands.literal("day").executes(p_198831_0_ -> TimeCommand.sendQueryResults((CommandSource)p_198831_0_.getSource(), (int)(((CommandSource)p_198831_0_.getSource()).getWorld().getDayTime() / 24000L % Integer.MAX_VALUE))))));
    }

    private static int getDayTime(ServerWorld worldIn) {
        return (int)(worldIn.getDayTime() % 24000L);
    }

    private static int sendQueryResults(CommandSource source, int time) {
        source.sendFeedback(new TranslationTextComponent("commands.time.query", time), false);
        return time;
    }

    public static int setTime(CommandSource source, int time) {
        for (ServerWorld serverworld : source.getServer().getWorlds()) {
            serverworld.func_241114_a_(time);
        }
        source.sendFeedback(new TranslationTextComponent("commands.time.set", time), true);
        return TimeCommand.getDayTime(source.getWorld());
    }

    public static int addTime(CommandSource source, int amount) {
        for (ServerWorld serverworld : source.getServer().getWorlds()) {
            serverworld.func_241114_a_(serverworld.getDayTime() + (long)amount);
        }
        int i = TimeCommand.getDayTime(source.getWorld());
        source.sendFeedback(new TranslationTextComponent("commands.time.set", i), true);
        return i;
    }
}
