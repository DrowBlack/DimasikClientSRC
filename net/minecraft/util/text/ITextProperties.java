package net.minecraft.util.text;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.Unit;
import net.minecraft.util.text.Style;

public interface ITextProperties {
    public static final Optional<Unit> field_240650_b_ = Optional.of(Unit.INSTANCE);
    public static final ITextProperties field_240651_c_ = new ITextProperties(){

        @Override
        public <T> Optional<T> getComponent(ITextAcceptor<T> acceptor) {
            return Optional.empty();
        }

        @Override
        public <T> Optional<T> getComponentWithStyle(IStyledTextAcceptor<T> acceptor, Style styleIn) {
            return Optional.empty();
        }
    };

    public <T> Optional<T> getComponent(ITextAcceptor<T> var1);

    public <T> Optional<T> getComponentWithStyle(IStyledTextAcceptor<T> var1, Style var2);

    public static ITextProperties func_240652_a_(final String p_240652_0_) {
        return new ITextProperties(){

            @Override
            public <T> Optional<T> getComponent(ITextAcceptor<T> acceptor) {
                return acceptor.accept(p_240652_0_);
            }

            @Override
            public <T> Optional<T> getComponentWithStyle(IStyledTextAcceptor<T> acceptor, Style styleIn) {
                return acceptor.accept(styleIn, p_240652_0_);
            }
        };
    }

    public static ITextProperties func_240653_a_(final String p_240653_0_, final Style p_240653_1_) {
        return new ITextProperties(){

            @Override
            public <T> Optional<T> getComponent(ITextAcceptor<T> acceptor) {
                return acceptor.accept(p_240653_0_);
            }

            @Override
            public <T> Optional<T> getComponentWithStyle(IStyledTextAcceptor<T> acceptor, Style styleIn) {
                return acceptor.accept(p_240653_1_.mergeStyle(styleIn), p_240653_0_);
            }
        };
    }

    public static ITextProperties func_240655_a_(ITextProperties ... p_240655_0_) {
        return ITextProperties.func_240654_a_(ImmutableList.copyOf(p_240655_0_));
    }

    public static ITextProperties func_240654_a_(final List<ITextProperties> p_240654_0_) {
        return new ITextProperties(){

            @Override
            public <T> Optional<T> getComponent(ITextAcceptor<T> acceptor) {
                for (ITextProperties itextproperties : p_240654_0_) {
                    Optional<T> optional = itextproperties.getComponent(acceptor);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }

            @Override
            public <T> Optional<T> getComponentWithStyle(IStyledTextAcceptor<T> acceptor, Style styleIn) {
                for (ITextProperties itextproperties : p_240654_0_) {
                    Optional<T> optional = itextproperties.getComponentWithStyle(acceptor, styleIn);
                    if (!optional.isPresent()) continue;
                    return optional;
                }
                return Optional.empty();
            }
        };
    }

    default public String getString() {
        StringBuilder stringbuilder = new StringBuilder();
        this.getComponent(p_241754_1_ -> {
            stringbuilder.append(p_241754_1_);
            return Optional.empty();
        });
        return stringbuilder.toString();
    }

    public static interface ITextAcceptor<T> {
        public Optional<T> accept(String var1);
    }

    public static interface IStyledTextAcceptor<T> {
        public Optional<T> accept(Style var1, String var2);
    }
}
