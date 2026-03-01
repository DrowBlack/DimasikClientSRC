package net.minecraft.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameter;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.LootFunctionManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetName
extends LootFunction {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ITextComponent name;
    @Nullable
    private final LootContext.EntityTarget field_215940_d;

    private SetName(ILootCondition[] p_i51218_1_, @Nullable ITextComponent p_i51218_2_, @Nullable LootContext.EntityTarget p_i51218_3_) {
        super(p_i51218_1_);
        this.name = p_i51218_2_;
        this.field_215940_d = p_i51218_3_;
    }

    @Override
    public LootFunctionType getFunctionType() {
        return LootFunctionManager.SET_NAME;
    }

    @Override
    public Set<LootParameter<?>> getRequiredParameters() {
        return this.field_215940_d != null ? ImmutableSet.of(this.field_215940_d.getParameter()) : ImmutableSet.of();
    }

    public static UnaryOperator<ITextComponent> func_215936_a(LootContext p_215936_0_, @Nullable LootContext.EntityTarget p_215936_1_) {
        Entity entity;
        if (p_215936_1_ != null && (entity = p_215936_0_.get(p_215936_1_.getParameter())) != null) {
            CommandSource commandsource = entity.getCommandSource().withPermissionLevel(2);
            return p_215937_2_ -> {
                try {
                    return TextComponentUtils.func_240645_a_(commandsource, p_215937_2_, entity, 0);
                }
                catch (CommandSyntaxException commandsyntaxexception) {
                    LOGGER.warn("Failed to resolve text component", (Throwable)commandsyntaxexception);
                    return p_215937_2_;
                }
            };
        }
        return p_215938_0_ -> p_215938_0_;
    }

    @Override
    public ItemStack doApply(ItemStack stack, LootContext context) {
        if (this.name != null) {
            stack.setDisplayName((ITextComponent)SetName.func_215936_a(context, this.field_215940_d).apply(this.name));
        }
        return stack;
    }

    public static class Serializer
    extends LootFunction.Serializer<SetName> {
        @Override
        public void serialize(JsonObject p_230424_1_, SetName p_230424_2_, JsonSerializationContext p_230424_3_) {
            super.serialize(p_230424_1_, p_230424_2_, p_230424_3_);
            if (p_230424_2_.name != null) {
                p_230424_1_.add("name", ITextComponent.Serializer.toJsonTree(p_230424_2_.name));
            }
            if (p_230424_2_.field_215940_d != null) {
                p_230424_1_.add("entity", p_230424_3_.serialize((Object)p_230424_2_.field_215940_d));
            }
        }

        @Override
        public SetName deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
            IFormattableTextComponent itextcomponent = ITextComponent.Serializer.getComponentFromJson(object.get("name"));
            LootContext.EntityTarget lootcontext$entitytarget = JSONUtils.deserializeClass(object, "entity", null, deserializationContext, LootContext.EntityTarget.class);
            return new SetName(conditionsIn, itextcomponent, lootcontext$entitytarget);
        }
    }
}
