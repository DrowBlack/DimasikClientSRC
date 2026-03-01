package net.optifine.shaders.config;

import net.optifine.expr.IExpressionBool;
import net.optifine.shaders.config.ShaderOptionSwitch;

public class ExpressionShaderOptionSwitch
implements IExpressionBool {
    private ShaderOptionSwitch shaderOption;

    public ExpressionShaderOptionSwitch(ShaderOptionSwitch shaderOption) {
        this.shaderOption = shaderOption;
    }

    @Override
    public boolean eval() {
        return ShaderOptionSwitch.isTrue(this.shaderOption.getValue());
    }

    public String toString() {
        return String.valueOf(this.shaderOption);
    }
}
