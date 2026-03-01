package net.minecraft.command.impl;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.impl.ReloadCommand;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;

public class DataPackCommand {
    private static final DynamicCommandExceptionType UNKNOWN_DATA_PACK_EXCEPTION = new DynamicCommandExceptionType(p_208808_0_ -> new TranslationTextComponent("commands.datapack.unknown", p_208808_0_));
    private static final DynamicCommandExceptionType ENABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType(p_208818_0_ -> new TranslationTextComponent("commands.datapack.enable.failed", p_208818_0_));
    private static final DynamicCommandExceptionType DISABLE_FAILED_EXCEPTION = new DynamicCommandExceptionType(p_208815_0_ -> new TranslationTextComponent("commands.datapack.disable.failed", p_208815_0_));
    private static final SuggestionProvider<CommandSource> SUGGEST_ENABLED_PACK = (p_198305_0_, p_198305_1_) -> ISuggestionProvider.suggest(((CommandSource)p_198305_0_.getSource()).getServer().getResourcePacks().func_232621_d_().stream().map(StringArgumentType::escapeIfRequired), p_198305_1_);
    private static final SuggestionProvider<CommandSource> field_241028_e_ = (p_241030_0_, p_241030_1_) -> {
        ResourcePackList resourcepacklist = ((CommandSource)p_241030_0_.getSource()).getServer().getResourcePacks();
        Collection<String> collection = resourcepacklist.func_232621_d_();
        return ISuggestionProvider.suggest(resourcepacklist.func_232616_b_().stream().filter(p_241033_1_ -> !collection.contains(p_241033_1_)).map(StringArgumentType::escapeIfRequired), p_241030_1_);
    };

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires(p_198301_0_ -> p_198301_0_.hasPermissionLevel(2))).then(Commands.literal("enable").then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", StringArgumentType.string()).suggests(field_241028_e_).executes(p_198292_0_ -> DataPackCommand.enablePack((CommandSource)p_198292_0_.getSource(), DataPackCommand.parsePackInfo(p_198292_0_, "name", true), (p_198289_0_, p_198289_1_) -> p_198289_1_.getPriority().insert(p_198289_0_, p_198289_1_, p_198304_0_ -> p_198304_0_, false)))).then(Commands.literal("after").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("existing", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes(p_198307_0_ -> DataPackCommand.enablePack((CommandSource)p_198307_0_.getSource(), DataPackCommand.parsePackInfo(p_198307_0_, "name", true), (p_198308_1_, p_198308_2_) -> p_198308_1_.add(p_198308_1_.indexOf(DataPackCommand.parsePackInfo(p_198307_0_, "existing", false)) + 1, p_198308_2_)))))).then(Commands.literal("before").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("existing", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes(p_198311_0_ -> DataPackCommand.enablePack((CommandSource)p_198311_0_.getSource(), DataPackCommand.parsePackInfo(p_198311_0_, "name", true), (p_198302_1_, p_198302_2_) -> p_198302_1_.add(p_198302_1_.indexOf(DataPackCommand.parsePackInfo(p_198311_0_, "existing", false)), p_198302_2_)))))).then(Commands.literal("last").executes(p_198298_0_ -> DataPackCommand.enablePack((CommandSource)p_198298_0_.getSource(), DataPackCommand.parsePackInfo(p_198298_0_, "name", true), List::add)))).then(Commands.literal("first").executes(p_198300_0_ -> DataPackCommand.enablePack((CommandSource)p_198300_0_.getSource(), DataPackCommand.parsePackInfo(p_198300_0_, "name", true), (p_241034_0_, p_241034_1_) -> p_241034_0_.add(0, p_241034_1_))))))).then(Commands.literal("disable").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("name", StringArgumentType.string()).suggests(SUGGEST_ENABLED_PACK).executes(p_198295_0_ -> DataPackCommand.disablePack((CommandSource)p_198295_0_.getSource(), DataPackCommand.parsePackInfo(p_198295_0_, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes(p_198290_0_ -> DataPackCommand.listAllPacks((CommandSource)p_198290_0_.getSource()))).then(Commands.literal("available").executes(p_198288_0_ -> DataPackCommand.listAvailablePacks((CommandSource)p_198288_0_.getSource())))).then(Commands.literal("enabled").executes(p_198309_0_ -> DataPackCommand.listEnabledPacks((CommandSource)p_198309_0_.getSource())))));
    }

    private static int enablePack(CommandSource source, ResourcePackInfo pack, IHandler priorityCallback) throws CommandSyntaxException {
        ResourcePackList resourcepacklist = source.getServer().getResourcePacks();
        ArrayList<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getEnabledPacks());
        priorityCallback.apply(list, pack);
        source.sendFeedback(new TranslationTextComponent("commands.datapack.modify.enable", pack.getChatLink(true)), true);
        ReloadCommand.func_241062_a_(list.stream().map(ResourcePackInfo::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int disablePack(CommandSource source, ResourcePackInfo pack) {
        ResourcePackList resourcepacklist = source.getServer().getResourcePacks();
        ArrayList<ResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getEnabledPacks());
        list.remove(pack);
        source.sendFeedback(new TranslationTextComponent("commands.datapack.modify.disable", pack.getChatLink(true)), true);
        ReloadCommand.func_241062_a_(list.stream().map(ResourcePackInfo::getName).collect(Collectors.toList()), source);
        return list.size();
    }

    private static int listAllPacks(CommandSource source) {
        return DataPackCommand.listEnabledPacks(source) + DataPackCommand.listAvailablePacks(source);
    }

    private static int listAvailablePacks(CommandSource source) {
        ResourcePackList resourcepacklist = source.getServer().getResourcePacks();
        resourcepacklist.reloadPacksFromFinders();
        Collection<ResourcePackInfo> collection = resourcepacklist.getEnabledPacks();
        Collection<ResourcePackInfo> collection1 = resourcepacklist.getAllPacks();
        List list = collection1.stream().filter(p_241032_1_ -> !collection.contains(p_241032_1_)).collect(Collectors.toList());
        if (list.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent("commands.datapack.list.available.none"), false);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.datapack.list.available.success", list.size(), TextComponentUtils.func_240649_b_(list, p_198293_0_ -> p_198293_0_.getChatLink(false))), false);
        }
        return list.size();
    }

    private static int listEnabledPacks(CommandSource source) {
        ResourcePackList resourcepacklist = source.getServer().getResourcePacks();
        resourcepacklist.reloadPacksFromFinders();
        Collection<ResourcePackInfo> collection = resourcepacklist.getEnabledPacks();
        if (collection.isEmpty()) {
            source.sendFeedback(new TranslationTextComponent("commands.datapack.list.enabled.none"), false);
        } else {
            source.sendFeedback(new TranslationTextComponent("commands.datapack.list.enabled.success", collection.size(), TextComponentUtils.func_240649_b_(collection, p_198306_0_ -> p_198306_0_.getChatLink(true))), false);
        }
        return collection.size();
    }

    private static ResourcePackInfo parsePackInfo(CommandContext<CommandSource> context, String name, boolean enabling) throws CommandSyntaxException {
        String s = StringArgumentType.getString(context, name);
        ResourcePackList resourcepacklist = context.getSource().getServer().getResourcePacks();
        ResourcePackInfo resourcepackinfo = resourcepacklist.getPackInfo(s);
        if (resourcepackinfo == null) {
            throw UNKNOWN_DATA_PACK_EXCEPTION.create(s);
        }
        boolean flag = resourcepacklist.getEnabledPacks().contains(resourcepackinfo);
        if (enabling && flag) {
            throw ENABLE_FAILED_EXCEPTION.create(s);
        }
        if (!enabling && !flag) {
            throw DISABLE_FAILED_EXCEPTION.create(s);
        }
        return resourcepackinfo;
    }

    static interface IHandler {
        public void apply(List<ResourcePackInfo> var1, ResourcePackInfo var2) throws CommandSyntaxException;
    }
}
