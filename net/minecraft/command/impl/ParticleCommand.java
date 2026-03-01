package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class ParticleCommand {
    private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.particle.failed"));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("particle").requires(p_198568_0_ -> p_198568_0_.hasPermissionLevel(2))).then(((RequiredArgumentBuilder)Commands.argument("name", ParticleArgument.particle()).executes(p_198562_0_ -> ParticleCommand.spawnParticle((CommandSource)p_198562_0_.getSource(), ParticleArgument.getParticle(p_198562_0_, "name"), ((CommandSource)p_198562_0_.getSource()).getPos(), Vector3d.ZERO, 0.0f, 0, false, ((CommandSource)p_198562_0_.getSource()).getServer().getPlayerList().getPlayers()))).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes(p_201226_0_ -> ParticleCommand.spawnParticle((CommandSource)p_201226_0_.getSource(), ParticleArgument.getParticle(p_201226_0_, "name"), Vec3Argument.getVec3(p_201226_0_, "pos"), Vector3d.ZERO, 0.0f, 0, false, ((CommandSource)p_201226_0_.getSource()).getServer().getPlayerList().getPlayers()))).then(Commands.argument("delta", Vec3Argument.vec3(false)).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("speed", FloatArgumentType.floatArg(0.0f)).then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("count", IntegerArgumentType.integer(0)).executes(p_198565_0_ -> ParticleCommand.spawnParticle((CommandSource)p_198565_0_.getSource(), ParticleArgument.getParticle(p_198565_0_, "name"), Vec3Argument.getVec3(p_198565_0_, "pos"), Vec3Argument.getVec3(p_198565_0_, "delta"), FloatArgumentType.getFloat(p_198565_0_, "speed"), IntegerArgumentType.getInteger(p_198565_0_, "count"), false, ((CommandSource)p_198565_0_.getSource()).getServer().getPlayerList().getPlayers()))).then(((LiteralArgumentBuilder)Commands.literal("force").executes(p_198561_0_ -> ParticleCommand.spawnParticle((CommandSource)p_198561_0_.getSource(), ParticleArgument.getParticle(p_198561_0_, "name"), Vec3Argument.getVec3(p_198561_0_, "pos"), Vec3Argument.getVec3(p_198561_0_, "delta"), FloatArgumentType.getFloat(p_198561_0_, "speed"), IntegerArgumentType.getInteger(p_198561_0_, "count"), true, ((CommandSource)p_198561_0_.getSource()).getServer().getPlayerList().getPlayers()))).then(Commands.argument("viewers", EntityArgument.players()).executes(p_198566_0_ -> ParticleCommand.spawnParticle((CommandSource)p_198566_0_.getSource(), ParticleArgument.getParticle(p_198566_0_, "name"), Vec3Argument.getVec3(p_198566_0_, "pos"), Vec3Argument.getVec3(p_198566_0_, "delta"), FloatArgumentType.getFloat(p_198566_0_, "speed"), IntegerArgumentType.getInteger(p_198566_0_, "count"), true, EntityArgument.getPlayers(p_198566_0_, "viewers")))))).then(((LiteralArgumentBuilder)Commands.literal("normal").executes(p_198560_0_ -> ParticleCommand.spawnParticle((CommandSource)p_198560_0_.getSource(), ParticleArgument.getParticle(p_198560_0_, "name"), Vec3Argument.getVec3(p_198560_0_, "pos"), Vec3Argument.getVec3(p_198560_0_, "delta"), FloatArgumentType.getFloat(p_198560_0_, "speed"), IntegerArgumentType.getInteger(p_198560_0_, "count"), false, ((CommandSource)p_198560_0_.getSource()).getServer().getPlayerList().getPlayers()))).then(Commands.argument("viewers", EntityArgument.players()).executes(p_198567_0_ -> ParticleCommand.spawnParticle((CommandSource)p_198567_0_.getSource(), ParticleArgument.getParticle(p_198567_0_, "name"), Vec3Argument.getVec3(p_198567_0_, "pos"), Vec3Argument.getVec3(p_198567_0_, "delta"), FloatArgumentType.getFloat(p_198567_0_, "speed"), IntegerArgumentType.getInteger(p_198567_0_, "count"), false, EntityArgument.getPlayers(p_198567_0_, "viewers")))))))))));
    }

    private static int spawnParticle(CommandSource source, IParticleData particleData, Vector3d pos, Vector3d delta, float speed, int count, boolean force, Collection<ServerPlayerEntity> viewers) throws CommandSyntaxException {
        int i = 0;
        for (ServerPlayerEntity serverplayerentity : viewers) {
            if (!source.getWorld().spawnParticle(serverplayerentity, particleData, force, pos.x, pos.y, pos.z, count, delta.x, delta.y, delta.z, speed)) continue;
            ++i;
        }
        if (i == 0) {
            throw FAILED_EXCEPTION.create();
        }
        source.sendFeedback(new TranslationTextComponent("commands.particle.success", Registry.PARTICLE_TYPE.getKey(particleData.getType()).toString()), true);
        return i;
    }
}
