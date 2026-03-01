package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class WeatherCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires(p_198868_0_ -> p_198868_0_.hasPermissionLevel(2))).then(((LiteralArgumentBuilder)Commands.literal("clear").executes(p_198861_0_ -> WeatherCommand.setClear((CommandSource)p_198861_0_.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(p_198864_0_ -> WeatherCommand.setClear((CommandSource)p_198864_0_.getSource(), IntegerArgumentType.getInteger(p_198864_0_, "duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes(p_198860_0_ -> WeatherCommand.setRain((CommandSource)p_198860_0_.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(p_198866_0_ -> WeatherCommand.setRain((CommandSource)p_198866_0_.getSource(), IntegerArgumentType.getInteger(p_198866_0_, "duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes(p_198859_0_ -> WeatherCommand.setThunder((CommandSource)p_198859_0_.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer(0, 1000000)).executes(p_198867_0_ -> WeatherCommand.setThunder((CommandSource)p_198867_0_.getSource(), IntegerArgumentType.getInteger(p_198867_0_, "duration") * 20)))));
    }

    private static int setClear(CommandSource source, int time) {
        source.getWorld().func_241113_a_(time, 0, false, false);
        source.sendFeedback(new TranslationTextComponent("commands.weather.set.clear"), true);
        return time;
    }

    private static int setRain(CommandSource source, int time) {
        source.getWorld().func_241113_a_(0, time, true, false);
        source.sendFeedback(new TranslationTextComponent("commands.weather.set.rain"), true);
        return time;
    }

    private static int setThunder(CommandSource source, int time) {
        source.getWorld().func_241113_a_(0, time, true, true);
        source.sendFeedback(new TranslationTextComponent("commands.weather.set.thunder"), true);
        return time;
    }
}
