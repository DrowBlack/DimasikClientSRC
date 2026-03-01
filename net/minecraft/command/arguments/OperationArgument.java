package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class OperationArgument
implements ArgumentType<IOperation> {
    private static final Collection<String> EXAMPLES = Arrays.asList("=", ">", "<");
    private static final SimpleCommandExceptionType OPERATION_INVALID = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.operation.invalid"));
    private static final SimpleCommandExceptionType OPERATION_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(new TranslationTextComponent("arguments.operation.div0"));

    public static OperationArgument operation() {
        return new OperationArgument();
    }

    public static IOperation getOperation(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, IOperation.class);
    }

    @Override
    public IOperation parse(StringReader p_parse_1_) throws CommandSyntaxException {
        if (!p_parse_1_.canRead()) {
            throw OPERATION_INVALID.create();
        }
        int i = p_parse_1_.getCursor();
        while (p_parse_1_.canRead() && p_parse_1_.peek() != ' ') {
            p_parse_1_.skip();
        }
        return OperationArgument.parseOperation(p_parse_1_.getString().substring(i, p_parse_1_.getCursor()));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
        return ISuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, p_listSuggestions_2_);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static IOperation parseOperation(String name) throws CommandSyntaxException {
        return name.equals("><") ? (p_197175_0_, p_197175_1_) -> {
            int i = p_197175_0_.getScorePoints();
            p_197175_0_.setScorePoints(p_197175_1_.getScorePoints());
            p_197175_1_.setScorePoints(i);
        } : OperationArgument.parseOperation0(name);
    }

    private static IIntOperation parseOperation0(String name) throws CommandSyntaxException {
        int b0 = -1;
        switch (name.hashCode()) {
            case 60: {
                if (!name.equals("<")) break;
                b0 = 6;
                break;
            }
            case 61: {
                if (!name.equals("=")) break;
                b0 = 0;
                break;
            }
            case 62: {
                if (!name.equals(">")) break;
                b0 = 7;
                break;
            }
            case 1208: {
                if (!name.equals("%=")) break;
                b0 = 5;
                break;
            }
            case 1363: {
                if (!name.equals("*=")) break;
                b0 = 3;
                break;
            }
            case 1394: {
                if (!name.equals("+=")) break;
                b0 = 1;
                break;
            }
            case 1456: {
                if (!name.equals("-=")) break;
                b0 = 2;
                break;
            }
            case 1518: {
                if (!name.equals("/=")) break;
                b0 = 4;
            }
        }
        switch (b0) {
            case 0: {
                return (p_197174_0_, p_197174_1_) -> p_197174_1_;
            }
            case 1: {
                return (p_197176_0_, p_197176_1_) -> p_197176_0_ + p_197176_1_;
            }
            case 2: {
                return (p_197183_0_, p_197183_1_) -> p_197183_0_ - p_197183_1_;
            }
            case 3: {
                return (p_197173_0_, p_197173_1_) -> p_197173_0_ * p_197173_1_;
            }
            case 4: {
                return (p_197178_0_, p_197178_1_) -> {
                    if (p_197178_1_ == 0) {
                        throw OPERATION_DIVIDE_BY_ZERO.create();
                    }
                    return MathHelper.intFloorDiv(p_197178_0_, p_197178_1_);
                };
            }
            case 5: {
                return (p_197181_0_, p_197181_1_) -> {
                    if (p_197181_1_ == 0) {
                        throw OPERATION_DIVIDE_BY_ZERO.create();
                    }
                    return MathHelper.normalizeAngle(p_197181_0_, p_197181_1_);
                };
            }
            case 6: {
                return Math::min;
            }
            case 7: {
                return Math::max;
            }
        }
        throw OPERATION_INVALID.create();
    }

    @FunctionalInterface
    public static interface IOperation {
        public void apply(Score var1, Score var2) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface IIntOperation
    extends IOperation {
        public int apply(int var1, int var2) throws CommandSyntaxException;

        @Override
        default public void apply(Score p_apply_1_, Score p_apply_2_) throws CommandSyntaxException {
            p_apply_1_.setScorePoints(this.apply(p_apply_1_.getScorePoints(), p_apply_2_.getScorePoints()));
        }
    }
}
