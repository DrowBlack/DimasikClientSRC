package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SayCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("say").requires(p_198627_0_ -> p_198627_0_.hasPermissionLevel(2))).then(Commands.argument("message", MessageArgument.message()).executes(p_198626_0_ -> {
            ITextComponent itextcomponent = MessageArgument.getMessage(p_198626_0_, "message");
            TranslationTextComponent translationtextcomponent = new TranslationTextComponent("chat.type.announcement", ((CommandSource)p_198626_0_.getSource()).getDisplayName(), itextcomponent);
            Entity entity = ((CommandSource)p_198626_0_.getSource()).getEntity();
            if (entity != null) {
                ((CommandSource)p_198626_0_.getSource()).getServer().getPlayerList().func_232641_a_(translationtextcomponent, ChatType.CHAT, entity.getUniqueID());
            } else {
                ((CommandSource)p_198626_0_.getSource()).getServer().getPlayerList().func_232641_a_(translationtextcomponent, ChatType.SYSTEM, Util.DUMMY_UUID);
            }
            return 1;
        })));
    }
}
