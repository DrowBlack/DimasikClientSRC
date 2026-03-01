package net.optifine.expr;

import net.optifine.expr.FunctionType;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionBool;

public class FunctionBool
implements IExpressionBool {
    private FunctionType type;
    private IExpression[] arguments;

    public FunctionBool(FunctionType type, IExpression[] arguments) {
        this.type = type;
        this.arguments = arguments;
    }

    @Override
    public boolean eval() {
        return this.type.evalBool(this.arguments);
    }

    public String toString() {
        return String.valueOf((Object)this.type) + "()";
    }
}
