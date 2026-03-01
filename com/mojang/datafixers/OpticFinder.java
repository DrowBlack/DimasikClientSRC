package com.mojang.datafixers;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import javax.annotation.Nullable;

public interface OpticFinder<FT> {
    public Type<FT> type();

    public <A, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findType(Type<A> var1, Type<FR> var2, boolean var3);

    default public <A> Either<TypedOptic<A, ?, FT, FT>, Type.FieldNotFoundException> findType(Type<A> containerType, boolean recurse) {
        return this.findType(containerType, this.type(), recurse);
    }

    default public <GT> OpticFinder<FT> inField(final @Nullable String name, final Type<GT> type) {
        final OpticFinder outer = this;
        return new OpticFinder<FT>(){

            @Override
            public Type<FT> type() {
                return outer.type();
            }

            @Override
            public <A, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findType(Type<A> containerType, Type<FR> resultType, boolean recurse) {
                Either secondOptic = outer.findType(type, resultType, recurse);
                return secondOptic.map(l -> this.cap(containerType, (TypedOptic)l, recurse), Either::right);
            }

            private <A, FR, GR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> cap(Type<A> containterType, TypedOptic<GT, GR, FT, FR> l1, boolean recurse) {
                Either first = DSL.fieldFinder(name, type).findType(containterType, l1.tType(), recurse);
                return first.mapLeft(l -> l.compose(l1));
            }
        };
    }
}
