package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.NamespacedSchema;
import net.minecraft.util.datafix.TypeReferences;

public class BannerItemColor
extends DataFix {
    public BannerItemColor(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(TypeReferences.ITEM_NAME.typeName(), NamespacedSchema.func_233457_a_()));
        OpticFinder<?> opticfinder1 = type.findField("tag");
        OpticFinder<?> opticfinder2 = opticfinder1.type().findField("BlockEntityTag");
        return this.fixTypeEverywhereTyped("ItemBannerColorFix", type, p_207466_3_ -> {
            Optional optional = p_207466_3_.getOptional(opticfinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:banner")) {
                Typed<Dynamic<?>> typed;
                Optional optional2;
                Dynamic dynamic = p_207466_3_.get(DSL.remainderFinder());
                Optional optional1 = p_207466_3_.getOptionalTyped(opticfinder1);
                if (optional1.isPresent() && (optional2 = (typed = optional1.get()).getOptionalTyped(opticfinder2)).isPresent()) {
                    Typed<Dynamic<?>> typed1 = optional2.get();
                    Dynamic<?> dynamic1 = typed.get(DSL.remainderFinder());
                    Dynamic<?> dynamic2 = typed1.getOrCreate(DSL.remainderFinder());
                    if (dynamic2.get("Base").asNumber().result().isPresent()) {
                        Dynamic dynamic4;
                        Dynamic dynamic3;
                        dynamic = dynamic.set("Damage", dynamic.createShort((short)(dynamic2.get("Base").asInt(0) & 0xF)));
                        Optional<Dynamic<?>> optional3 = dynamic1.get("display").result();
                        if (optional3.isPresent() && Objects.equals(dynamic3 = optional3.get(), dynamic4 = dynamic3.createMap(ImmutableMap.of(dynamic3.createString("Lore"), dynamic3.createList(Stream.of(dynamic3.createString("(+NBT"))))))) {
                            return p_207466_3_.set(DSL.remainderFinder(), dynamic);
                        }
                        dynamic2.remove("Base");
                        return p_207466_3_.set(DSL.remainderFinder(), dynamic).set(opticfinder1, typed.set(opticfinder2, typed1.set(DSL.remainderFinder(), dynamic2)));
                    }
                }
                return p_207466_3_.set(DSL.remainderFinder(), dynamic);
            }
            return p_207466_3_;
        });
    }
}
