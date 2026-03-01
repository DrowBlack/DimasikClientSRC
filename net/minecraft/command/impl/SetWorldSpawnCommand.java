package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.AngleArgument;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires(p_198704_0_ -> p_198704_0_.hasPermissionLevel(2))).executes(p_198700_0_ -> SetWorldSpawnCommand.setSpawn((CommandSource)p_198700_0_.getSource(), new BlockPos(((CommandSource)p_198700_0_.getSource()).getPos()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes(p_198703_0_ -> SetWorldSpawnCommand.setSpawn((CommandSource)p_198703_0_.getSource(), BlockPosArgument.getBlockPos(p_198703_0_, "pos"), 0.0f))).then(Commands.argument("angle", AngleArgument.func_242991_a()).executes(p_244377_0_ -> SetWorldSpawnCommand.setSpawn((CommandSource)p_244377_0_.getSource(), BlockPosArgument.getBlockPos(p_244377_0_, "pos"), AngleArgument.func_242992_a(p_244377_0_, "angle"))))));
    }

    private static int setSpawn(CommandSource source, BlockPos pos, float p_198701_2_) {
        source.getWorld().func_241124_a__(pos, p_198701_2_);
        source.sendFeedback(new TranslationTextComponent("commands.setworldspawn.success", pos.getX(), pos.getY(), pos.getZ(), Float.valueOf(p_198701_2_)), true);
        return 1;
    }
}
