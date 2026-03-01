package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.util.datafix.TypeReferences;

public class EntityArmorAndHeld
extends DataFix {
    public EntityArmorAndHeld(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    public TypeRewriteRule makeRule() {
        return this.cap(this.getInputSchema().getTypeRaw(TypeReferences.ITEM_STACK));
    }

    private <IS> TypeRewriteRule cap(Type<IS> p_206323_1_) {
        Type<Pair<Either<IS, Unit>, Dynamic<?>>> type = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(p_206323_1_))), DSL.remainderType());
        Type type1 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(p_206323_1_))), DSL.optional(DSL.field("HandItems", DSL.list(p_206323_1_))), DSL.remainderType());
        OpticFinder opticfinder = DSL.typeFinder(type);
        OpticFinder opticfinder1 = DSL.fieldFinder("Equipment", DSL.list(p_206323_1_));
        return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(TypeReferences.ENTITY), this.getOutputSchema().getType(TypeReferences.ENTITY), (Typed<?> p_207448_4_) -> {
            Either<Object, Unit> either = Either.right(DSL.unit());
            Either<Object, Unit> either1 = Either.right(DSL.unit());
            Dynamic dynamic = p_207448_4_.getOrCreate(DSL.remainderFinder());
            Optional optional = p_207448_4_.getOptional(opticfinder1);
            if (optional.isPresent()) {
                List list = (List)optional.get();
                Object is = p_206323_1_.read(dynamic.emptyMap()).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created empty itemstack.")).getFirst();
                if (!list.isEmpty()) {
                    either = Either.left(Lists.newArrayList(list.get(0), is));
                }
                if (list.size() > 1) {
                    ArrayList<Object> list1 = Lists.newArrayList(is, is, is, is);
                    for (int i = 1; i < Math.min(list.size(), 5); ++i) {
                        list1.set(i - 1, list.get(i));
                    }
                    either1 = Either.left(list1);
                }
            }
            Dynamic dynamic2 = dynamic;
            Optional<Stream<Dynamic<?>>> optional1 = dynamic.get("DropChances").asStreamOpt().result();
            if (optional1.isPresent()) {
                Iterator iterator = Stream.concat(optional1.get(), Stream.generate(() -> dynamic2.createInt(0))).iterator();
                float f = ((Dynamic)iterator.next()).asFloat(0.0f);
                if (!dynamic.get("HandDropChances").result().isPresent()) {
                    Dynamic dynamic1 = dynamic.createList(Stream.of(Float.valueOf(f), Float.valueOf(0.0f)).map(dynamic::createFloat));
                    dynamic = dynamic.set("HandDropChances", dynamic1);
                }
                if (!dynamic.get("ArmorDropChances").result().isPresent()) {
                    Dynamic dynamic3 = dynamic.createList(Stream.of(Float.valueOf(((Dynamic)iterator.next()).asFloat(0.0f)), Float.valueOf(((Dynamic)iterator.next()).asFloat(0.0f)), Float.valueOf(((Dynamic)iterator.next()).asFloat(0.0f)), Float.valueOf(((Dynamic)iterator.next()).asFloat(0.0f))).map(dynamic::createFloat));
                    dynamic = dynamic.set("ArmorDropChances", dynamic3);
                }
                dynamic = dynamic.remove("DropChances");
            }
            return p_207448_4_.set(opticfinder, type1, Pair.of(either, Pair.of(either1, dynamic)));
        });
    }
}
