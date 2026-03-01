package com.mojang.datafixers;

import com.google.common.collect.Maps;
import com.mojang.datafixers.FieldFinder;
import com.mojang.datafixers.NamedChoiceFinder;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Func;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.constant.EmptyPart;
import com.mojang.datafixers.types.constant.EmptyPartPassthrough;
import com.mojang.datafixers.types.templates.Check;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.datafixers.types.templates.Const;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.types.templates.Named;
import com.mojang.datafixers.types.templates.Product;
import com.mojang.datafixers.types.templates.RecursivePoint;
import com.mojang.datafixers.types.templates.Sum;
import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Triple;

public interface DSL {
    public static Type<Boolean> bool() {
        return Instances.BOOL_TYPE;
    }

    public static Type<Integer> intType() {
        return Instances.INT_TYPE;
    }

    public static Type<Long> longType() {
        return Instances.LONG_TYPE;
    }

    public static Type<Byte> byteType() {
        return Instances.BYTE_TYPE;
    }

    public static Type<Short> shortType() {
        return Instances.SHORT_TYPE;
    }

    public static Type<Float> floatType() {
        return Instances.FLOAT_TYPE;
    }

    public static Type<Double> doubleType() {
        return Instances.DOUBLE_TYPE;
    }

    public static Type<String> string() {
        return Instances.STRING_TYPE;
    }

    public static TypeTemplate emptyPart() {
        return DSL.constType(Instances.EMPTY_PART);
    }

    public static Type<Unit> emptyPartType() {
        return Instances.EMPTY_PART;
    }

    public static TypeTemplate remainder() {
        return DSL.constType(Instances.EMPTY_PASSTHROUGH);
    }

    public static Type<Dynamic<?>> remainderType() {
        return Instances.EMPTY_PASSTHROUGH;
    }

    public static TypeTemplate check(String name, int index, TypeTemplate element) {
        return new Check(name, index, element);
    }

    public static TypeTemplate compoundList(TypeTemplate element) {
        return DSL.compoundList(DSL.constType(DSL.string()), element);
    }

    public static <V> CompoundList.CompoundListType<String, V> compoundList(Type<V> value) {
        return DSL.compoundList(DSL.string(), value);
    }

    public static TypeTemplate compoundList(TypeTemplate key, TypeTemplate element) {
        return DSL.and((TypeTemplate)new CompoundList(key, element), DSL.remainder());
    }

    public static <K, V> CompoundList.CompoundListType<K, V> compoundList(Type<K> key, Type<V> value) {
        return new CompoundList.CompoundListType<K, V>(key, value);
    }

    public static TypeTemplate constType(Type<?> type) {
        return new Const(type);
    }

    public static TypeTemplate hook(TypeTemplate template, Hook.HookFunction preRead, Hook.HookFunction postWrite) {
        return new Hook(template, preRead, postWrite);
    }

    public static <A> Type<A> hook(Type<A> type, Hook.HookFunction preRead, Hook.HookFunction postWrite) {
        return new Hook.HookType<A>(type, preRead, postWrite);
    }

    public static TypeTemplate list(TypeTemplate element) {
        return new List(element);
    }

    public static <A> List.ListType<A> list(Type<A> first) {
        return new List.ListType<A>(first);
    }

    public static TypeTemplate named(String name, TypeTemplate element) {
        return new Named(name, element);
    }

    public static <A> Type<Pair<String, A>> named(String name, Type<A> element) {
        return new Named.NamedType<A>(name, element);
    }

    public static TypeTemplate and(TypeTemplate first, TypeTemplate second) {
        return new Product(first, second);
    }

    public static TypeTemplate and(TypeTemplate first, TypeTemplate ... rest) {
        if (rest.length == 0) {
            return first;
        }
        TypeTemplate result = rest[rest.length - 1];
        for (int i = rest.length - 2; i >= 0; --i) {
            result = DSL.and(rest[i], result);
        }
        return DSL.and(first, result);
    }

    public static TypeTemplate allWithRemainder(TypeTemplate first, TypeTemplate ... rest) {
        return DSL.and(first, ArrayUtils.add(rest, DSL.remainder()));
    }

    public static <F, G> Type<Pair<F, G>> and(Type<F> first, Type<G> second) {
        return new Product.ProductType<F, G>(first, second);
    }

    public static <F, G, H> Type<Pair<F, Pair<G, H>>> and(Type<F> first, Type<G> second, Type<H> third) {
        return DSL.and(first, DSL.and(second, third));
    }

    public static <F, G, H, I> Type<Pair<F, Pair<G, Pair<H, I>>>> and(Type<F> first, Type<G> second, Type<H> third, Type<I> forth) {
        return DSL.and(first, DSL.and(second, DSL.and(third, forth)));
    }

    public static TypeTemplate id(int index) {
        return new RecursivePoint(index);
    }

    public static TypeTemplate or(TypeTemplate left, TypeTemplate right) {
        return new Sum(left, right);
    }

    public static <F, G> Type<Either<F, G>> or(Type<F> first, Type<G> second) {
        return new Sum.SumType<F, G>(first, second);
    }

    public static TypeTemplate field(String name, TypeTemplate element) {
        return new Tag(name, element);
    }

    public static <A> Tag.TagType<A> field(String name, Type<A> element) {
        return new Tag.TagType<A>(name, element);
    }

    public static <K> TaggedChoice<K> taggedChoice(String name, Type<K> keyType, Map<K, TypeTemplate> templates) {
        return new TaggedChoice<K>(name, keyType, templates);
    }

    public static <K> TaggedChoice<K> taggedChoiceLazy(String name, Type<K> keyType, Map<K, Supplier<TypeTemplate>> templates) {
        return DSL.taggedChoice(name, keyType, templates.entrySet().stream().map(e -> Pair.of(e.getKey(), ((Supplier)e.getValue()).get())).collect(Pair.toMap()));
    }

    public static <K> Type<Pair<K, ?>> taggedChoiceType(String name, Type<K> keyType, Map<K, ? extends Type<?>> types) {
        return Instances.TAGGED_CHOICE_TYPE_CACHE.computeIfAbsent(Triple.of(name, keyType, types), k -> new TaggedChoice.TaggedChoiceType((String)k.getLeft(), (Type)k.getMiddle(), (Map)k.getRight()));
    }

    public static <A, B> Type<Function<A, B>> func(Type<A> input, Type<B> output) {
        return new Func<A, B>(input, output);
    }

    public static <A> Type<Either<A, Unit>> optional(Type<A> type) {
        return DSL.or(type, DSL.emptyPartType());
    }

    public static TypeTemplate optional(TypeTemplate value) {
        return DSL.or(value, DSL.emptyPart());
    }

    public static TypeTemplate fields(String name1, TypeTemplate element1) {
        return DSL.allWithRemainder(DSL.field(name1, element1), new TypeTemplate[0]);
    }

    public static TypeTemplate fields(String name1, TypeTemplate element1, String name2, TypeTemplate element2) {
        return DSL.allWithRemainder(DSL.field(name1, element1), DSL.field(name2, element2));
    }

    public static TypeTemplate fields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3) {
        return DSL.allWithRemainder(DSL.field(name1, element1), DSL.field(name2, element2), DSL.field(name3, element3));
    }

    public static TypeTemplate fields(String name, TypeTemplate element, TypeTemplate rest) {
        return DSL.and(DSL.field(name, element), rest);
    }

    public static TypeTemplate fields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, TypeTemplate rest) {
        return DSL.and(DSL.field(name1, element1), DSL.field(name2, element2), rest);
    }

    public static TypeTemplate fields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3, TypeTemplate rest) {
        return DSL.and(DSL.field(name1, element1), DSL.field(name2, element2), DSL.field(name3, element3), rest);
    }

    public static TypeTemplate optionalFields(String name, TypeTemplate element) {
        return DSL.allWithRemainder(DSL.optional(DSL.field(name, element)), new TypeTemplate[0]);
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2) {
        return DSL.allWithRemainder(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)));
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3) {
        return DSL.allWithRemainder(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)), DSL.optional(DSL.field(name3, element3)));
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3, String name4, TypeTemplate element4) {
        return DSL.allWithRemainder(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)), DSL.optional(DSL.field(name3, element3)), DSL.optional(DSL.field(name4, element4)));
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3, String name4, TypeTemplate element4, String name5, TypeTemplate element5) {
        return DSL.allWithRemainder(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)), DSL.optional(DSL.field(name3, element3)), DSL.optional(DSL.field(name4, element4)), DSL.optional(DSL.field(name5, element5)));
    }

    public static TypeTemplate optionalFields(String name, TypeTemplate element, TypeTemplate rest) {
        return DSL.and(DSL.optional(DSL.field(name, element)), rest);
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, TypeTemplate rest) {
        return DSL.and(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)), rest);
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3, TypeTemplate rest) {
        return DSL.and(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)), DSL.optional(DSL.field(name3, element3)), rest);
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3, String name4, TypeTemplate element4, TypeTemplate rest) {
        return DSL.and(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)), DSL.optional(DSL.field(name3, element3)), DSL.optional(DSL.field(name4, element4)), rest);
    }

    public static TypeTemplate optionalFields(String name1, TypeTemplate element1, String name2, TypeTemplate element2, String name3, TypeTemplate element3, String name4, TypeTemplate element4, String name5, TypeTemplate element5, TypeTemplate rest) {
        return DSL.and(DSL.optional(DSL.field(name1, element1)), DSL.optional(DSL.field(name2, element2)), DSL.optional(DSL.field(name3, element3)), DSL.optional(DSL.field(name4, element4)), DSL.optional(DSL.field(name5, element5)), rest);
    }

    public static OpticFinder<Dynamic<?>> remainderFinder() {
        return Instances.REMAINDER_FINDER;
    }

    public static <FT> OpticFinder<FT> typeFinder(Type<FT> type) {
        return new FieldFinder<FT>(null, type);
    }

    public static <FT> OpticFinder<FT> fieldFinder(String name, Type<FT> type) {
        return new FieldFinder<FT>(name, type);
    }

    public static <FT> OpticFinder<FT> namedChoice(String name, Type<FT> type) {
        return new NamedChoiceFinder<FT>(name, type);
    }

    public static Unit unit() {
        return Unit.INSTANCE;
    }

    public static final class Instances {
        private static final Type<Boolean> BOOL_TYPE = new Const.PrimitiveType<Boolean>(Codec.BOOL);
        private static final Type<Integer> INT_TYPE = new Const.PrimitiveType<Integer>(Codec.INT);
        private static final Type<Long> LONG_TYPE = new Const.PrimitiveType<Long>(Codec.LONG);
        private static final Type<Byte> BYTE_TYPE = new Const.PrimitiveType<Byte>(Codec.BYTE);
        private static final Type<Short> SHORT_TYPE = new Const.PrimitiveType<Short>(Codec.SHORT);
        private static final Type<Float> FLOAT_TYPE = new Const.PrimitiveType<Float>(Codec.FLOAT);
        private static final Type<Double> DOUBLE_TYPE = new Const.PrimitiveType<Double>(Codec.DOUBLE);
        private static final Type<String> STRING_TYPE = new Const.PrimitiveType<String>(Codec.STRING);
        private static final Type<Unit> EMPTY_PART = new EmptyPart();
        private static final Type<Dynamic<?>> EMPTY_PASSTHROUGH = new EmptyPartPassthrough();
        private static final OpticFinder<Dynamic<?>> REMAINDER_FINDER = DSL.remainderType().finder();
        private static final Map<Triple<String, Type<?>, Map<?, ? extends Type<?>>>, Type<? extends Pair<?, ?>>> TAGGED_CHOICE_TYPE_CACHE = Maps.newConcurrentMap();
    }

    public static interface TypeReference {
        public String typeName();

        default public TypeTemplate in(Schema schema) {
            return schema.id(this.typeName());
        }
    }
}
