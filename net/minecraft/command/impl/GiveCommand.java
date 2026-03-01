package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TranslationTextComponent;

public class GiveCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("give").requires(p_198496_0_ -> p_198496_0_.hasPermissionLevel(2))).then(Commands.argument("targets", EntityArgument.players()).then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes(p_198493_0_ -> GiveCommand.giveItem((CommandSource)p_198493_0_.getSource(), ItemArgument.getItem(p_198493_0_, "item"), EntityArgument.getPlayers(p_198493_0_, "targets"), 1))).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes(p_198495_0_ -> GiveCommand.giveItem((CommandSource)p_198495_0_.getSource(), ItemArgument.getItem(p_198495_0_, "item"), EntityArgument.getPlayers(p_198495_0_, "targets"), IntegerArgumentType.getInteger(p_198495_0_, "count")))))));
    }

    private static int giveItem(CommandSource source, ItemInput itemIn, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
        for (ServerPlayerEntity serverplayerentity : targets) {
            int i = count;
            while (i > 0) {
                int j = Math.min(itemIn.getItem().getMaxStackSize(), i);
                i -= j;
                ItemStack itemstack = itemIn.createStack(j, false);
                boolean flag = serverplayerentity.inventory.addItemStackToInventory(itemstack);
                if (flag && itemstack.isEmpty()) {
                    itemstack.setCount(1);
                    ItemEntity itementity1 = serverplayerentity.dropItem(itemstack, false);
                    if (itementity1 != null) {
                        itementity1.makeFakeItem();
                    }
                    serverplayerentity.world.playSound(null, serverplayerentity.getPosX(), serverplayerentity.getPosY(), serverplayerentity.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverplayerentity.getRNG().nextFloat() - serverplayerentity.getRNG().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                    serverplayerentity.container.detectAndSendChanges();
                    continue;
                }
                ItemEntity itementity = serverplayerentity.dropItem(itemstack, false);
                if (itementity == null) continue;
                itementity.setNoPickupDelay();
                itementity.setOwnerId(serverplayerentity.getUniqueID());
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(new TranslationTextComponent("commands.give.success.single", count, itemIn.createStack(count, false).getTextComponent(), targets.iterator().next().getDisplayName()), true);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.give.success.single", count, itemIn.createStack(count, false).getTextComponent(), targets.size()), true);
        }
        return targets.size();
    }
}
