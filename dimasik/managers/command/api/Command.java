package dimasik.managers.command.api;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import dimasik.helpers.interfaces.IFastAccess;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.command.ISuggestionProvider;

public abstract class Command
implements IFastAccess {
    protected final int SINGLE_SUCCESS = 1;
    private final List<String> name;
    private final String desk;

    public Command(String desk, String ... name) {
        this.name = Arrays.asList(name);
        this.desk = desk;
    }

    public LiteralArgumentBuilder<ISuggestionProvider> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    protected <T> RequiredArgumentBuilder<ISuggestionProvider, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    public abstract void build(LiteralArgumentBuilder<ISuggestionProvider> var1);

    public abstract void run(String[] var1) throws Exception;

    public abstract void error();

    @Generated
    public int getSINGLE_SUCCESS() {
        return this.SINGLE_SUCCESS;
    }

    @Generated
    public List<String> getName() {
        return this.name;
    }

    @Generated
    public String getDesk() {
        return this.desk;
    }
}
