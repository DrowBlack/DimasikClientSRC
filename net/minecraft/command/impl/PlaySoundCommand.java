package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Iterator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlaySoundPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;

public class PlaySoundCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.playsound.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        RequiredArgumentBuilder<CommandSource, ResourceLocation> requiredargumentbuilder = Commands.argument("sound", ResourceLocationArgument.resourceLocation()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);
        for (SoundCategory soundcategory : SoundCategory.values()) {
            requiredargumentbuilder.then(PlaySoundCommand.buildCategorySubcommand(soundcategory));
        }
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires(p_198576_0_ -> p_198576_0_.hasPermissionLevel(2))).then(requiredargumentbuilder));
    }

    private static LiteralArgumentBuilder<CommandSource> buildCategorySubcommand(SoundCategory category) {
        return (LiteralArgumentBuilder)Commands.literal(category.getName()).then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes(p_198575_1_ -> PlaySoundCommand.playSound((CommandSource)p_198575_1_.getSource(), EntityArgument.getPlayers(p_198575_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198575_1_, "sound"), category, ((CommandSource)p_198575_1_.getSource()).getPos(), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes(p_198578_1_ -> PlaySoundCommand.playSound((CommandSource)p_198578_1_.getSource(), EntityArgument.getPlayers(p_198578_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198578_1_, "sound"), category, Vec3Argument.getVec3(p_198578_1_, "pos"), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg(0.0f)).executes(p_198571_1_ -> PlaySoundCommand.playSound((CommandSource)p_198571_1_.getSource(), EntityArgument.getPlayers(p_198571_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198571_1_, "sound"), category, Vec3Argument.getVec3(p_198571_1_, "pos"), p_198571_1_.getArgument("volume", Float.class).floatValue(), 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg(0.0f, 2.0f)).executes(p_198574_1_ -> PlaySoundCommand.playSound((CommandSource)p_198574_1_.getSource(), EntityArgument.getPlayers(p_198574_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198574_1_, "sound"), category, Vec3Argument.getVec3(p_198574_1_, "pos"), p_198574_1_.getArgument("volume", Float.class).floatValue(), p_198574_1_.getArgument("pitch", Float.class).floatValue(), 0.0f))).then(Commands.argument("minVolume", FloatArgumentType.floatArg(0.0f, 1.0f)).executes(p_198570_1_ -> PlaySoundCommand.playSound((CommandSource)p_198570_1_.getSource(), EntityArgument.getPlayers(p_198570_1_, "targets"), ResourceLocationArgument.getResourceLocation(p_198570_1_, "sound"), category, Vec3Argument.getVec3(p_198570_1_, "pos"), p_198570_1_.getArgument("volume", Float.class).floatValue(), p_198570_1_.getArgument("pitch", Float.class).floatValue(), p_198570_1_.getArgument("minVolume", Float.class).floatValue())))))));
    }

    private static int playSound(CommandSource source, Collection<ServerPlayerEntity> targets, ResourceLocation soundIn, SoundCategory category, Vector3d pos, float volume, float pitch, float minVolume) throws CommandSyntaxException {
        double d0 = Math.pow(volume > 1.0f ? (double)(volume * 16.0f) : 16.0, 2.0);
        int i = 0;
        Iterator<ServerPlayerEntity> iterator = targets.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                if (i == 0) {
                    throw FAILED_EXCEPTION.create();
                }
                if (targets.size() == 1) {
                    source.sendFeedback(new TranslationTextComponent("commands.playsound.success.single", soundIn, targets.iterator().next().getDisplayName()), true);
                } else {
                    source.sendFeedback(new TranslationTextComponent("commands.playsound.success.multiple", soundIn, targets.size()), true);
                }
                return i;
            }
            ServerPlayerEntity serverplayerentity = iterator.next();
            double d1 = pos.x - serverplayerentity.getPosX();
            double d2 = pos.y - serverplayerentity.getPosY();
            double d3 = pos.z - serverplayerentity.getPosZ();
            double d4 = d1 * d1 + d2 * d2 + d3 * d3;
            Vector3d vector3d = pos;
            float f = volume;
            if (d4 > d0) {
                if (minVolume <= 0.0f) continue;
                double d5 = MathHelper.sqrt(d4);
                vector3d = new Vector3d(serverplayerentity.getPosX() + d1 / d5 * 2.0, serverplayerentity.getPosY() + d2 / d5 * 2.0, serverplayerentity.getPosZ() + d3 / d5 * 2.0);
                f = minVolume;
            }
            serverplayerentity.connection.sendPacket(new SPlaySoundPacket(soundIn, category, vector3d, f, pitch));
            ++i;
        }
    }
}
