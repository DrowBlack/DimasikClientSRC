package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DynamicLike;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<RuleKey<?>, RuleType<?>> GAME_RULES = Maps.newTreeMap(Comparator.comparing(key -> key.gameRuleName));
    public static final RuleKey<BooleanValue> DO_FIRE_TICK = GameRules.register("doFireTick", Category.UPDATES, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> MOB_GRIEFING = GameRules.register("mobGriefing", Category.MOBS, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> KEEP_INVENTORY = GameRules.register("keepInventory", Category.PLAYER, BooleanValue.create(false));
    public static final RuleKey<BooleanValue> DO_MOB_SPAWNING = GameRules.register("doMobSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_MOB_LOOT = GameRules.register("doMobLoot", Category.DROPS, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_TILE_DROPS = GameRules.register("doTileDrops", Category.DROPS, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_ENTITY_DROPS = GameRules.register("doEntityDrops", Category.DROPS, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> COMMAND_BLOCK_OUTPUT = GameRules.register("commandBlockOutput", Category.CHAT, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> NATURAL_REGENERATION = GameRules.register("naturalRegeneration", Category.PLAYER, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_DAYLIGHT_CYCLE = GameRules.register("doDaylightCycle", Category.UPDATES, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> LOG_ADMIN_COMMANDS = GameRules.register("logAdminCommands", Category.CHAT, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> SHOW_DEATH_MESSAGES = GameRules.register("showDeathMessages", Category.CHAT, BooleanValue.create(true));
    public static final RuleKey<IntegerValue> RANDOM_TICK_SPEED = GameRules.register("randomTickSpeed", Category.UPDATES, IntegerValue.create(3));
    public static final RuleKey<BooleanValue> SEND_COMMAND_FEEDBACK = GameRules.register("sendCommandFeedback", Category.CHAT, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> REDUCED_DEBUG_INFO = GameRules.register("reducedDebugInfo", Category.MISC, BooleanValue.create(false, (server, value) -> {
        byte b0 = (byte)(value.get() ? 22 : 23);
        for (ServerPlayerEntity serverplayerentity : server.getPlayerList().getPlayers()) {
            serverplayerentity.connection.sendPacket(new SEntityStatusPacket(serverplayerentity, b0));
        }
    }));
    public static final RuleKey<BooleanValue> SPECTATORS_GENERATE_CHUNKS = GameRules.register("spectatorsGenerateChunks", Category.PLAYER, BooleanValue.create(true));
    public static final RuleKey<IntegerValue> SPAWN_RADIUS = GameRules.register("spawnRadius", Category.PLAYER, IntegerValue.create(10));
    public static final RuleKey<BooleanValue> DISABLE_ELYTRA_MOVEMENT_CHECK = GameRules.register("disableElytraMovementCheck", Category.PLAYER, BooleanValue.create(false));
    public static final RuleKey<IntegerValue> MAX_ENTITY_CRAMMING = GameRules.register("maxEntityCramming", Category.MOBS, IntegerValue.create(24));
    public static final RuleKey<BooleanValue> DO_WEATHER_CYCLE = GameRules.register("doWeatherCycle", Category.UPDATES, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_LIMITED_CRAFTING = GameRules.register("doLimitedCrafting", Category.PLAYER, BooleanValue.create(false));
    public static final RuleKey<IntegerValue> MAX_COMMAND_CHAIN_LENGTH = GameRules.register("maxCommandChainLength", Category.MISC, IntegerValue.create(65536));
    public static final RuleKey<BooleanValue> ANNOUNCE_ADVANCEMENTS = GameRules.register("announceAdvancements", Category.CHAT, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DISABLE_RAIDS = GameRules.register("disableRaids", Category.MOBS, BooleanValue.create(false));
    public static final RuleKey<BooleanValue> DO_INSOMNIA = GameRules.register("doInsomnia", Category.SPAWNING, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_IMMEDIATE_RESPAWN = GameRules.register("doImmediateRespawn", Category.PLAYER, BooleanValue.create(false, (server, value) -> {
        for (ServerPlayerEntity serverplayerentity : server.getPlayerList().getPlayers()) {
            serverplayerentity.connection.sendPacket(new SChangeGameStatePacket(SChangeGameStatePacket.field_241775_l_, value.get() ? 1.0f : 0.0f));
        }
    }));
    public static final RuleKey<BooleanValue> DROWNING_DAMAGE = GameRules.register("drowningDamage", Category.PLAYER, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> FALL_DAMAGE = GameRules.register("fallDamage", Category.PLAYER, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> FIRE_DAMAGE = GameRules.register("fireDamage", Category.PLAYER, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_PATROL_SPAWNING = GameRules.register("doPatrolSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> DO_TRADER_SPAWNING = GameRules.register("doTraderSpawning", Category.SPAWNING, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> FORGIVE_DEAD_PLAYERS = GameRules.register("forgiveDeadPlayers", Category.MOBS, BooleanValue.create(true));
    public static final RuleKey<BooleanValue> UNIVERSAL_ANGER = GameRules.register("universalAnger", Category.MOBS, BooleanValue.create(false));
    private final Map<RuleKey<?>, RuleValue<?>> rules;

    private static <T extends RuleValue<T>> RuleKey<T> register(String name, Category category, RuleType<T> type) {
        RuleKey rulekey = new RuleKey(name, category);
        RuleType<T> ruletype = GAME_RULES.put(rulekey, type);
        if (ruletype != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        }
        return rulekey;
    }

    public GameRules(DynamicLike<?> dynamic) {
        this();
        this.decode(dynamic);
    }

    public GameRules() {
        this.rules = GAME_RULES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((RuleType)entry.getValue()).createValue()));
    }

    private GameRules(Map<RuleKey<?>, RuleValue<?>> keyToValueMap) {
        this.rules = keyToValueMap;
    }

    public <T extends RuleValue<T>> T get(RuleKey<T> key) {
        return (T)this.rules.get(key);
    }

    public CompoundNBT write() {
        CompoundNBT compoundnbt = new CompoundNBT();
        this.rules.forEach((key, value) -> compoundnbt.putString(key.gameRuleName, value.stringValue()));
        return compoundnbt;
    }

    private void decode(DynamicLike<?> dynamic) {
        this.rules.forEach((key, value) -> dynamic.get(key.gameRuleName).asString().result().ifPresent(value::setStringValue));
    }

    public GameRules clone() {
        return new GameRules((Map)this.rules.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, entry -> ((RuleValue)entry.getValue()).clone())));
    }

    public static void visitAll(IRuleEntryVisitor visitor) {
        GAME_RULES.forEach((key, type) -> GameRules.visit(visitor, key, type));
    }

    private static <T extends RuleValue<T>> void visit(IRuleEntryVisitor visitor, RuleKey<?> key, RuleType<?> type) {
        visitor.visit(key, type);
        type.visitRule(visitor, key);
    }

    public void func_234899_a_(GameRules rules, @Nullable MinecraftServer server) {
        rules.rules.keySet().forEach(key -> this.getValue((RuleKey)key, rules, server));
    }

    private <T extends RuleValue<T>> void getValue(RuleKey<T> key, GameRules rules, @Nullable MinecraftServer server) {
        T t = rules.get(key);
        ((RuleValue)this.get(key)).changeValue(t, server);
    }

    public boolean getBoolean(RuleKey<BooleanValue> key) {
        return this.get(key).get();
    }

    public int getInt(RuleKey<IntegerValue> key) {
        return this.get(key).get();
    }

    public static final class RuleKey<T extends RuleValue<T>> {
        private final String gameRuleName;
        private final Category category;

        public RuleKey(String gameRuleName, Category category) {
            this.gameRuleName = gameRuleName;
            this.category = category;
        }

        public String toString() {
            return this.gameRuleName;
        }

        public boolean equals(Object p_equals_1_) {
            if (this == p_equals_1_) {
                return true;
            }
            return p_equals_1_ instanceof RuleKey && ((RuleKey)p_equals_1_).gameRuleName.equals(this.gameRuleName);
        }

        public int hashCode() {
            return this.gameRuleName.hashCode();
        }

        public String getName() {
            return this.gameRuleName;
        }

        public String getLocaleString() {
            return "gamerule." + this.gameRuleName;
        }

        public Category getCategory() {
            return this.category;
        }
    }

    public static enum Category {
        PLAYER("gamerule.category.player"),
        MOBS("gamerule.category.mobs"),
        SPAWNING("gamerule.category.spawning"),
        DROPS("gamerule.category.drops"),
        UPDATES("gamerule.category.updates"),
        CHAT("gamerule.category.chat"),
        MISC("gamerule.category.misc");

        private final String localeString;

        private Category(String localeString) {
            this.localeString = localeString;
        }

        public String getLocaleString() {
            return this.localeString;
        }
    }

    public static class RuleType<T extends RuleValue<T>> {
        private final Supplier<ArgumentType<?>> argTypeSupplier;
        private final Function<RuleType<T>, T> valueCreator;
        private final BiConsumer<MinecraftServer, T> changeListener;
        private final IRule<T> rule;

        private RuleType(Supplier<ArgumentType<?>> argTypeSupplier, Function<RuleType<T>, T> valueCreator, BiConsumer<MinecraftServer, T> changeListener, IRule<T> rule) {
            this.argTypeSupplier = argTypeSupplier;
            this.valueCreator = valueCreator;
            this.changeListener = changeListener;
            this.rule = rule;
        }

        public RequiredArgumentBuilder<CommandSource, ?> createArgument(String name) {
            return Commands.argument(name, this.argTypeSupplier.get());
        }

        public T createValue() {
            return (T)((RuleValue)this.valueCreator.apply(this));
        }

        public void visitRule(IRuleEntryVisitor visitor, RuleKey<T> key) {
            this.rule.call(visitor, key, this);
        }
    }

    public static abstract class RuleValue<T extends RuleValue<T>> {
        protected final RuleType<T> type;

        public RuleValue(RuleType<T> type) {
            this.type = type;
        }

        protected abstract void updateValue0(CommandContext<CommandSource> var1, String var2);

        public void updateValue(CommandContext<CommandSource> context, String paramName) {
            this.updateValue0(context, paramName);
            this.notifyChange(context.getSource().getServer());
        }

        protected void notifyChange(@Nullable MinecraftServer server) {
            if (server != null) {
                this.type.changeListener.accept(server, (MinecraftServer)this.getValue());
            }
        }

        protected abstract void setStringValue(String var1);

        public abstract String stringValue();

        public String toString() {
            return this.stringValue();
        }

        public abstract int intValue();

        protected abstract T getValue();

        protected abstract T clone();

        public abstract void changeValue(T var1, @Nullable MinecraftServer var2);
    }

    public static interface IRuleEntryVisitor {
        default public <T extends RuleValue<T>> void visit(RuleKey<T> key, RuleType<T> type) {
        }

        default public void changeBoolean(RuleKey<BooleanValue> value1, RuleType<BooleanValue> value2) {
        }

        default public void changeInteger(RuleKey<IntegerValue> value1, RuleType<IntegerValue> value2) {
        }
    }

    public static class BooleanValue
    extends RuleValue<BooleanValue> {
        private boolean value;

        private static RuleType<BooleanValue> create(boolean defaultValue, BiConsumer<MinecraftServer, BooleanValue> changeListener) {
            return new RuleType<BooleanValue>(BoolArgumentType::bool, type -> new BooleanValue((RuleType<BooleanValue>)type, defaultValue), changeListener, IRuleEntryVisitor::changeBoolean);
        }

        private static RuleType<BooleanValue> create(boolean defaultValue) {
            return BooleanValue.create(defaultValue, (server, value) -> {});
        }

        public BooleanValue(RuleType<BooleanValue> type, boolean defaultValue) {
            super(type);
            this.value = defaultValue;
        }

        @Override
        protected void updateValue0(CommandContext<CommandSource> context, String paramName) {
            this.value = BoolArgumentType.getBool(context, paramName);
        }

        public boolean get() {
            return this.value;
        }

        public void set(boolean valueIn, @Nullable MinecraftServer server) {
            this.value = valueIn;
            this.notifyChange(server);
        }

        @Override
        public String stringValue() {
            return Boolean.toString(this.value);
        }

        @Override
        protected void setStringValue(String valueIn) {
            this.value = Boolean.parseBoolean(valueIn);
        }

        @Override
        public int intValue() {
            return this.value ? 1 : 0;
        }

        @Override
        protected BooleanValue getValue() {
            return this;
        }

        @Override
        protected BooleanValue clone() {
            return new BooleanValue(this.type, this.value);
        }

        @Override
        public void changeValue(BooleanValue value, @Nullable MinecraftServer server) {
            this.value = value.value;
            this.notifyChange(server);
        }
    }

    public static class IntegerValue
    extends RuleValue<IntegerValue> {
        private int value;

        private static RuleType<IntegerValue> create(int defaultValue, BiConsumer<MinecraftServer, IntegerValue> changeListener) {
            return new RuleType<IntegerValue>(IntegerArgumentType::integer, type -> new IntegerValue((RuleType<IntegerValue>)type, defaultValue), changeListener, IRuleEntryVisitor::changeInteger);
        }

        private static RuleType<IntegerValue> create(int defaultValue) {
            return IntegerValue.create(defaultValue, (server, value) -> {});
        }

        public IntegerValue(RuleType<IntegerValue> type, int defaultValue) {
            super(type);
            this.value = defaultValue;
        }

        @Override
        protected void updateValue0(CommandContext<CommandSource> context, String paramName) {
            this.value = IntegerArgumentType.getInteger(context, paramName);
        }

        public int get() {
            return this.value;
        }

        @Override
        public String stringValue() {
            return Integer.toString(this.value);
        }

        @Override
        protected void setStringValue(String valueIn) {
            this.value = IntegerValue.parseInt(valueIn);
        }

        public boolean parseIntValue(String name) {
            try {
                this.value = Integer.parseInt(name);
                return true;
            }
            catch (NumberFormatException numberformatexception) {
                return false;
            }
        }

        private static int parseInt(String strValue) {
            if (!strValue.isEmpty()) {
                try {
                    return Integer.parseInt(strValue);
                }
                catch (NumberFormatException numberformatexception) {
                    LOGGER.warn("Failed to parse integer {}", (Object)strValue);
                }
            }
            return 0;
        }

        @Override
        public int intValue() {
            return this.value;
        }

        @Override
        protected IntegerValue getValue() {
            return this;
        }

        @Override
        protected IntegerValue clone() {
            return new IntegerValue(this.type, this.value);
        }

        @Override
        public void changeValue(IntegerValue value, @Nullable MinecraftServer server) {
            this.value = value.value;
            this.notifyChange(server);
        }
    }

    static interface IRule<T extends RuleValue<T>> {
        public void call(IRuleEntryVisitor var1, RuleKey<T> var2, RuleType<T> var3);
    }
}
