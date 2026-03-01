package com.mojang.datafixers;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.functions.Functions;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface TypeRewriteRule {
    public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> var1);

    public static TypeRewriteRule nop() {
        return Nop.INSTANCE;
    }

    public static TypeRewriteRule seq(List<TypeRewriteRule> rules) {
        return new Seq(rules);
    }

    public static TypeRewriteRule seq(TypeRewriteRule first, TypeRewriteRule second) {
        if (Objects.equals(first, TypeRewriteRule.nop())) {
            return second;
        }
        if (Objects.equals(second, TypeRewriteRule.nop())) {
            return first;
        }
        return TypeRewriteRule.seq(ImmutableList.of(first, second));
    }

    public static TypeRewriteRule seq(TypeRewriteRule firstRule, TypeRewriteRule ... rules) {
        if (rules.length == 0) {
            return firstRule;
        }
        int lastRule = rules.length - 1;
        TypeRewriteRule tail = rules[lastRule];
        while (lastRule > 0) {
            tail = TypeRewriteRule.seq(rules[--lastRule], tail);
        }
        return TypeRewriteRule.seq(firstRule, tail);
    }

    public static TypeRewriteRule orElse(TypeRewriteRule first, TypeRewriteRule second) {
        return TypeRewriteRule.orElse(first, () -> second);
    }

    public static TypeRewriteRule orElse(TypeRewriteRule first, Supplier<TypeRewriteRule> second) {
        return new OrElse(first, second);
    }

    public static TypeRewriteRule all(TypeRewriteRule rule, boolean recurse, boolean checkIndex) {
        return new All(rule, recurse, checkIndex);
    }

    public static TypeRewriteRule one(TypeRewriteRule rule) {
        return new One(rule);
    }

    public static TypeRewriteRule once(TypeRewriteRule rule) {
        return TypeRewriteRule.orElse(rule, () -> TypeRewriteRule.one(TypeRewriteRule.once(rule)));
    }

    public static TypeRewriteRule checkOnce(TypeRewriteRule rule, Consumer<Type<?>> onFail) {
        return rule;
    }

    public static TypeRewriteRule everywhere(TypeRewriteRule rule, PointFreeRule optimizationRule, boolean recurse, boolean checkIndex) {
        return new Everywhere(rule, optimizationRule, recurse, checkIndex);
    }

    public static <B> TypeRewriteRule ifSame(Type<B> targetType, RewriteResult<B, ?> value) {
        return new IfSame<B>(targetType, value);
    }

    public static class IfSame<B>
    implements TypeRewriteRule {
        private final Type<B> targetType;
        private final RewriteResult<B, ?> value;
        private final int hashCode;

        public IfSame(Type<B> targetType, RewriteResult<B, ?> value) {
            this.targetType = targetType;
            this.value = value;
            this.hashCode = Objects.hash(targetType, value);
        }

        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            return type.ifSame(this.targetType, this.value);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof IfSame)) {
                return false;
            }
            IfSame that = (IfSame)obj;
            return Objects.equals(this.targetType, that.targetType) && Objects.equals(this.value, that.value);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    public static class Everywhere
    implements TypeRewriteRule {
        protected final TypeRewriteRule rule;
        protected final PointFreeRule optimizationRule;
        protected final boolean recurse;
        private final boolean checkIndex;
        private final int hashCode;

        public Everywhere(TypeRewriteRule rule, PointFreeRule optimizationRule, boolean recurse, boolean checkIndex) {
            this.rule = rule;
            this.optimizationRule = optimizationRule;
            this.recurse = recurse;
            this.checkIndex = checkIndex;
            this.hashCode = Objects.hash(rule, optimizationRule, recurse, checkIndex);
        }

        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            return type.everywhere(this.rule, this.optimizationRule, this.recurse, this.checkIndex);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Everywhere)) {
                return false;
            }
            Everywhere that = (Everywhere)obj;
            return Objects.equals(this.rule, that.rule) && Objects.equals(this.optimizationRule, that.optimizationRule) && this.recurse == that.recurse && this.checkIndex == that.checkIndex;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    public static class CheckOnce
    implements TypeRewriteRule {
        private final TypeRewriteRule rule;
        private final Consumer<Type<?>> onFail;

        public CheckOnce(TypeRewriteRule rule, Consumer<Type<?>> onFail) {
            this.rule = rule;
            this.onFail = onFail;
        }

        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            Optional<RewriteResult<A, ?>> result = this.rule.rewrite(type);
            if (!result.isPresent() || Objects.equals(result.get().view.function(), Functions.id())) {
                this.onFail.accept(type);
            }
            return result;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            return o instanceof CheckOnce && Objects.equals(this.rule, ((CheckOnce)o).rule);
        }

        public int hashCode() {
            return Objects.hash(this.rule);
        }
    }

    public static class One
    implements TypeRewriteRule {
        private final TypeRewriteRule rule;

        public One(TypeRewriteRule rule) {
            this.rule = rule;
        }

        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            return type.one(this.rule);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof One)) {
                return false;
            }
            One that = (One)obj;
            return Objects.equals(this.rule, that.rule);
        }

        public int hashCode() {
            return this.rule.hashCode();
        }
    }

    public static class All
    implements TypeRewriteRule {
        private final TypeRewriteRule rule;
        private final boolean recurse;
        private final boolean checkIndex;
        private final int hashCode;

        public All(TypeRewriteRule rule, boolean recurse, boolean checkIndex) {
            this.rule = rule;
            this.recurse = recurse;
            this.checkIndex = checkIndex;
            this.hashCode = Objects.hash(rule, recurse, checkIndex);
        }

        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            return Optional.of(type.all(this.rule, this.recurse, this.checkIndex));
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof All)) {
                return false;
            }
            All that = (All)obj;
            return Objects.equals(this.rule, that.rule) && this.recurse == that.recurse && this.checkIndex == that.checkIndex;
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    public static final class OrElse
    implements TypeRewriteRule {
        protected final TypeRewriteRule first;
        protected final Supplier<TypeRewriteRule> second;
        private final int hashCode;

        public OrElse(TypeRewriteRule first, Supplier<TypeRewriteRule> second) {
            this.first = first;
            this.second = second;
            this.hashCode = Objects.hash(first, second);
        }

        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            Optional<RewriteResult<A, ?>> view = this.first.rewrite(type);
            if (view.isPresent()) {
                return view;
            }
            return this.second.get().rewrite(type);
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof OrElse)) {
                return false;
            }
            OrElse that = (OrElse)obj;
            return Objects.equals(this.first, that.first) && Objects.equals(this.second, that.second);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    public static final class Seq
    implements TypeRewriteRule {
        protected final List<TypeRewriteRule> rules;
        private final int hashCode;

        public Seq(List<TypeRewriteRule> rules) {
            this.rules = ImmutableList.copyOf(rules);
            this.hashCode = this.rules.hashCode();
        }

        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            RewriteResult<A, Object> result = RewriteResult.nop(type);
            for (TypeRewriteRule rule : this.rules) {
                Optional<RewriteResult<A, ?>> newResult = this.cap1(rule, result);
                if (!newResult.isPresent()) {
                    return Optional.empty();
                }
                result = newResult.get();
            }
            return Optional.of(result);
        }

        protected <A, B> Optional<RewriteResult<A, ?>> cap1(TypeRewriteRule rule, RewriteResult<A, B> f) {
            return rule.rewrite(f.view.newType).map(s -> s.compose(f));
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Seq)) {
                return false;
            }
            Seq that = (Seq)obj;
            return Objects.equals(this.rules, that.rules);
        }

        public int hashCode() {
            return this.hashCode;
        }
    }

    public static enum Nop implements TypeRewriteRule,
    Supplier<TypeRewriteRule>
    {
        INSTANCE;


        @Override
        public <A> Optional<RewriteResult<A, ?>> rewrite(Type<A> type) {
            return Optional.of(RewriteResult.nop(type));
        }

        @Override
        public TypeRewriteRule get() {
            return this;
        }
    }
}
