package net.minecraft.command.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class SpreadPlayersCommand {
    private static final Dynamic4CommandExceptionType SPREAD_TEAMS_FAILED = new Dynamic4CommandExceptionType((p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_) -> new TranslationTextComponent("commands.spreadplayers.failed.teams", p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_));
    private static final Dynamic4CommandExceptionType SPREAD_ENTITIES_FAILED = new Dynamic4CommandExceptionType((p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_) -> new TranslationTextComponent("commands.spreadplayers.failed.entities", p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_));

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spreadplayers").requires(p_198721_0_ -> p_198721_0_.hasPermissionLevel(2))).then(Commands.argument("center", Vec2Argument.vec2()).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0f)).then((ArgumentBuilder<CommandSource, ?>)((RequiredArgumentBuilder)Commands.argument("maxRange", FloatArgumentType.floatArg(1.0f)).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("respectTeams", BoolArgumentType.bool()).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("targets", EntityArgument.entities()).executes(p_198718_0_ -> SpreadPlayersCommand.func_241070_a_((CommandSource)p_198718_0_.getSource(), Vec2Argument.getVec2f(p_198718_0_, "center"), FloatArgumentType.getFloat(p_198718_0_, "spreadDistance"), FloatArgumentType.getFloat(p_198718_0_, "maxRange"), 256, BoolArgumentType.getBool(p_198718_0_, "respectTeams"), EntityArgument.getEntities(p_198718_0_, "targets")))))).then(Commands.literal("under").then((ArgumentBuilder<CommandSource, ?>)Commands.argument("maxHeight", IntegerArgumentType.integer(0)).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("respectTeams", BoolArgumentType.bool()).then((ArgumentBuilder<CommandSource, ?>)Commands.argument("targets", EntityArgument.entities()).executes(p_241069_0_ -> SpreadPlayersCommand.func_241070_a_((CommandSource)p_241069_0_.getSource(), Vec2Argument.getVec2f(p_241069_0_, "center"), FloatArgumentType.getFloat(p_241069_0_, "spreadDistance"), FloatArgumentType.getFloat(p_241069_0_, "maxRange"), IntegerArgumentType.getInteger(p_241069_0_, "maxHeight"), BoolArgumentType.getBool(p_241069_0_, "respectTeams"), EntityArgument.getEntities(p_241069_0_, "targets")))))))))));
    }

    private static int func_241070_a_(CommandSource p_241070_0_, Vector2f p_241070_1_, float p_241070_2_, float p_241070_3_, int p_241070_4_, boolean p_241070_5_, Collection<? extends Entity> p_241070_6_) throws CommandSyntaxException {
        Random random = new Random();
        double d0 = p_241070_1_.x - p_241070_3_;
        double d1 = p_241070_1_.y - p_241070_3_;
        double d2 = p_241070_1_.x + p_241070_3_;
        double d3 = p_241070_1_.y + p_241070_3_;
        Position[] aspreadplayerscommand$position = SpreadPlayersCommand.getPositions(random, p_241070_5_ ? SpreadPlayersCommand.getNumberOfTeams(p_241070_6_) : p_241070_6_.size(), d0, d1, d2, d3);
        SpreadPlayersCommand.func_241071_a_(p_241070_1_, p_241070_2_, p_241070_0_.getWorld(), random, d0, d1, d2, d3, p_241070_4_, aspreadplayerscommand$position, p_241070_5_);
        double d4 = SpreadPlayersCommand.func_241072_a_(p_241070_6_, p_241070_0_.getWorld(), aspreadplayerscommand$position, p_241070_4_, p_241070_5_);
        p_241070_0_.sendFeedback(new TranslationTextComponent("commands.spreadplayers.success." + (p_241070_5_ ? "teams" : "entities"), aspreadplayerscommand$position.length, Float.valueOf(p_241070_1_.x), Float.valueOf(p_241070_1_.y), String.format(Locale.ROOT, "%.2f", d4)), true);
        return aspreadplayerscommand$position.length;
    }

    private static int getNumberOfTeams(Collection<? extends Entity> entities) {
        HashSet<Team> set = Sets.newHashSet();
        for (Entity entity : entities) {
            if (entity instanceof PlayerEntity) {
                set.add(entity.getTeam());
                continue;
            }
            set.add(null);
        }
        return set.size();
    }

    private static void func_241071_a_(Vector2f p_241071_0_, double p_241071_1_, ServerWorld p_241071_3_, Random p_241071_4_, double p_241071_5_, double p_241071_7_, double p_241071_9_, double p_241071_11_, int p_241071_13_, Position[] p_241071_14_, boolean p_241071_15_) throws CommandSyntaxException {
        int i;
        boolean flag = true;
        double d0 = 3.4028234663852886E38;
        for (i = 0; i < 10000 && flag; ++i) {
            flag = false;
            d0 = 3.4028234663852886E38;
            for (int j = 0; j < p_241071_14_.length; ++j) {
                Position spreadplayerscommand$position = p_241071_14_[j];
                int k = 0;
                Position spreadplayerscommand$position1 = new Position();
                for (int l = 0; l < p_241071_14_.length; ++l) {
                    if (j == l) continue;
                    Position spreadplayerscommand$position2 = p_241071_14_[l];
                    double d1 = spreadplayerscommand$position.getDistance(spreadplayerscommand$position2);
                    d0 = Math.min(d1, d0);
                    if (!(d1 < p_241071_1_)) continue;
                    ++k;
                    spreadplayerscommand$position1.x += spreadplayerscommand$position2.x - spreadplayerscommand$position.x;
                    spreadplayerscommand$position1.z += spreadplayerscommand$position2.z - spreadplayerscommand$position.z;
                }
                if (k > 0) {
                    spreadplayerscommand$position1.x /= (double)k;
                    spreadplayerscommand$position1.z /= (double)k;
                    double d2 = spreadplayerscommand$position1.getMagnitude();
                    if (d2 > 0.0) {
                        spreadplayerscommand$position1.normalize();
                        spreadplayerscommand$position.subtract(spreadplayerscommand$position1);
                    } else {
                        spreadplayerscommand$position.computeCoords(p_241071_4_, p_241071_5_, p_241071_7_, p_241071_9_, p_241071_11_);
                    }
                    flag = true;
                }
                if (!spreadplayerscommand$position.clampWithinRange(p_241071_5_, p_241071_7_, p_241071_9_, p_241071_11_)) continue;
                flag = true;
            }
            if (flag) continue;
            for (Position spreadplayerscommand$position3 : p_241071_14_) {
                if (spreadplayerscommand$position3.func_241074_b_(p_241071_3_, p_241071_13_)) continue;
                spreadplayerscommand$position3.computeCoords(p_241071_4_, p_241071_5_, p_241071_7_, p_241071_9_, p_241071_11_);
                flag = true;
            }
        }
        if (d0 == 3.4028234663852886E38) {
            d0 = 0.0;
        }
        if (i >= 10000) {
            if (p_241071_15_) {
                throw SPREAD_TEAMS_FAILED.create(p_241071_14_.length, Float.valueOf(p_241071_0_.x), Float.valueOf(p_241071_0_.y), String.format(Locale.ROOT, "%.2f", d0));
            }
            throw SPREAD_ENTITIES_FAILED.create(p_241071_14_.length, Float.valueOf(p_241071_0_.x), Float.valueOf(p_241071_0_.y), String.format(Locale.ROOT, "%.2f", d0));
        }
    }

    private static double func_241072_a_(Collection<? extends Entity> p_241072_0_, ServerWorld p_241072_1_, Position[] p_241072_2_, int p_241072_3_, boolean p_241072_4_) {
        double d0 = 0.0;
        int i = 0;
        HashMap<Team, Position> map = Maps.newHashMap();
        for (Entity entity : p_241072_0_) {
            Position spreadplayerscommand$position;
            if (p_241072_4_) {
                Team team;
                Team team2 = team = entity instanceof PlayerEntity ? entity.getTeam() : null;
                if (!map.containsKey(team)) {
                    map.put(team, p_241072_2_[i++]);
                }
                spreadplayerscommand$position = (Position)map.get(team);
            } else {
                spreadplayerscommand$position = p_241072_2_[i++];
            }
            entity.teleportKeepLoaded((double)MathHelper.floor(spreadplayerscommand$position.x) + 0.5, spreadplayerscommand$position.getHighestNonAirBlock(p_241072_1_, p_241072_3_), (double)MathHelper.floor(spreadplayerscommand$position.z) + 0.5);
            double d2 = Double.MAX_VALUE;
            for (Position spreadplayerscommand$position1 : p_241072_2_) {
                if (spreadplayerscommand$position == spreadplayerscommand$position1) continue;
                double d1 = spreadplayerscommand$position.getDistance(spreadplayerscommand$position1);
                d2 = Math.min(d1, d2);
            }
            d0 += d2;
        }
        return p_241072_0_.size() < 2 ? 0.0 : d0 / (double)p_241072_0_.size();
    }

    private static Position[] getPositions(Random random, int count, double minX, double minZ, double maxX, double maxZ) {
        Position[] aspreadplayerscommand$position = new Position[count];
        for (int i = 0; i < aspreadplayerscommand$position.length; ++i) {
            Position spreadplayerscommand$position = new Position();
            spreadplayerscommand$position.computeCoords(random, minX, minZ, maxX, maxZ);
            aspreadplayerscommand$position[i] = spreadplayerscommand$position;
        }
        return aspreadplayerscommand$position;
    }

    static class Position {
        private double x;
        private double z;

        Position() {
        }

        double getDistance(Position other) {
            double d0 = this.x - other.x;
            double d1 = this.z - other.z;
            return Math.sqrt(d0 * d0 + d1 * d1);
        }

        void normalize() {
            double d0 = this.getMagnitude();
            this.x /= d0;
            this.z /= d0;
        }

        float getMagnitude() {
            return MathHelper.sqrt(this.x * this.x + this.z * this.z);
        }

        public void subtract(Position other) {
            this.x -= other.x;
            this.z -= other.z;
        }

        public boolean clampWithinRange(double minX, double minZ, double maxX, double maxZ) {
            boolean flag = false;
            if (this.x < minX) {
                this.x = minX;
                flag = true;
            } else if (this.x > maxX) {
                this.x = maxX;
                flag = true;
            }
            if (this.z < minZ) {
                this.z = minZ;
                flag = true;
            } else if (this.z > maxZ) {
                this.z = maxZ;
                flag = true;
            }
            return flag;
        }

        public int getHighestNonAirBlock(IBlockReader worldIn, int p_198710_2_) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.x, (double)(p_198710_2_ + 1), this.z);
            boolean flag = worldIn.getBlockState(blockpos$mutable).isAir();
            blockpos$mutable.move(Direction.DOWN);
            boolean flag1 = worldIn.getBlockState(blockpos$mutable).isAir();
            while (blockpos$mutable.getY() > 0) {
                blockpos$mutable.move(Direction.DOWN);
                boolean flag2 = worldIn.getBlockState(blockpos$mutable).isAir();
                if (!flag2 && flag1 && flag) {
                    return blockpos$mutable.getY() + 1;
                }
                flag = flag1;
                flag1 = flag2;
            }
            return p_198710_2_ + 1;
        }

        public boolean func_241074_b_(IBlockReader p_241074_1_, int p_241074_2_) {
            BlockPos blockpos = new BlockPos(this.x, (double)(this.getHighestNonAirBlock(p_241074_1_, p_241074_2_) - 1), this.z);
            BlockState blockstate = p_241074_1_.getBlockState(blockpos);
            Material material = blockstate.getMaterial();
            return blockpos.getY() < p_241074_2_ && !material.isLiquid() && material != Material.FIRE;
        }

        public void computeCoords(Random random, double minX, double minZ, double maxX, double maZx) {
            this.x = MathHelper.nextDouble(random, minX, maxX);
            this.z = MathHelper.nextDouble(random, minZ, maZx);
        }
    }
}
