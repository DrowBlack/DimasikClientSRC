package net.minecraft.command.impl;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TranslationTextComponent;

public class KillCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kill").requires(p_198521_0_ -> p_198521_0_.hasPermissionLevel(2))).executes(p_198520_0_ -> KillCommand.killEntities((CommandSource)p_198520_0_.getSource(), ImmutableList.of(((CommandSource)p_198520_0_.getSource()).assertIsEntity())))).then(Commands.argument("targets", EntityArgument.entities()).executes(p_229810_0_ -> KillCommand.killEntities((CommandSource)p_229810_0_.getSource(), EntityArgument.getEntities(p_229810_0_, "targets")))));
    }

    private static int killEntities(CommandSource source, Collection<? extends Entity> targets) {
        for (Entity entity : targets) {
            entity.onKillCommand();
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.kill.success.single", targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.kill.success.multiple", targets.size()), true);
        }
        return targets.size();
    }
}
