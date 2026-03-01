package com.mojang.datafixers;

import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypedOptic;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.Tag;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.datafixers.util.Either;
import java.util.Objects;

final class NamedChoiceFinder<FT>
implements OpticFinder<FT> {
    private final String name;
    private final Type<FT> type;

    public NamedChoiceFinder(String name, Type<FT> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public Type<FT> type() {
        return this.type;
    }

    @Override
    public <A, FR> Either<TypedOptic<A, ?, FT, FR>, Type.FieldNotFoundException> findType(Type<A> containerType, Type<FR> resultType, boolean recurse) {
        return containerType.findTypeCached(this.type, resultType, new Matcher<FT, FR>(this.name, this.type, resultType), recurse);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NamedChoiceFinder)) {
            return false;
        }
        NamedChoiceFinder that = (NamedChoiceFinder)o;
        return Objects.equals(this.name, that.name) && Objects.equals(this.type, that.type);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.type);
    }

    private static class Matcher<FT, FR>
    implements Type.TypeMatcher<FT, FR> {
        private final Type<FR> resultType;
        private final String name;
        private final Type<FT> type;

        public Matcher(String name, Type<FT> type, Type<FR> resultType) {
            this.resultType = resultType;
            this.name = name;
            this.type = type;
        }

        @Override
        public <S> Either<TypedOptic<S, ?, FT, FR>, Type.FieldNotFoundException> match(Type<S> targetType) {
            if (targetType instanceof TaggedChoice.TaggedChoiceType) {
                TaggedChoice.TaggedChoiceType choiceType = (TaggedChoice.TaggedChoiceType)targetType;
                Type<?> elementType = choiceType.types().get(this.name);
                if (elementType != null) {
                    if (!Objects.equals(this.type, elementType)) {
                        return Either.right(new Type.FieldNotFoundException(String.format("Type error for choice type \"%s\": expected type: %s, actual type: %s)", this.name, targetType, elementType)));
                    }
                    return Either.left(TypedOptic.tagged(choiceType, this.name, this.type, this.resultType));
                }
                return Either.right(new Type.Continue());
            }
            if (targetType instanceof Tag.TagType) {
                return Either.right(new Type.FieldNotFoundException("in tag"));
            }
            return Either.right(new Type.Continue());
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Matcher matcher = (Matcher)o;
            return Objects.equals(this.resultType, matcher.resultType) && Objects.equals(this.name, matcher.name) && Objects.equals(this.type, matcher.type);
        }

        public int hashCode() {
            return Objects.hash(this.resultType, this.name, this.type);
        }
    }
}
