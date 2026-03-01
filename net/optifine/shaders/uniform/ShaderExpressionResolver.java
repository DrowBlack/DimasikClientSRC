package net.optifine.shaders.uniform;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;
import net.optifine.shaders.SMCLog;
import net.optifine.shaders.uniform.ShaderParameterBool;
import net.optifine.shaders.uniform.ShaderParameterFloat;
import net.optifine.shaders.uniform.ShaderParameterIndexed;
import net.optifine.util.BiomeUtils;

public class ShaderExpressionResolver
implements IExpressionResolver {
    private Map<String, IExpression> mapExpressions = new HashMap<String, IExpression>();

    public ShaderExpressionResolver(Map<String, IExpression> map) {
        this.registerExpressions();
        for (String s : map.keySet()) {
            IExpression iexpression = map.get(s);
            this.registerExpression(s, iexpression);
        }
    }

    private void registerExpressions() {
        ShaderParameterFloat[] ashaderparameterfloat = ShaderParameterFloat.values();
        for (int i = 0; i < ashaderparameterfloat.length; ++i) {
            ShaderParameterFloat shaderparameterfloat = ashaderparameterfloat[i];
            this.addParameterFloat(this.mapExpressions, shaderparameterfloat);
        }
        ShaderParameterBool[] ashaderparameterbool = ShaderParameterBool.values();
        for (int k = 0; k < ashaderparameterbool.length; ++k) {
            ShaderParameterBool shaderparameterbool = ashaderparameterbool[k];
            this.mapExpressions.put(shaderparameterbool.getName(), shaderparameterbool);
        }
        for (ResourceLocation resourcelocation : BiomeUtils.getLocations()) {
            Object s = resourcelocation.getPath().trim();
            s = "BIOME_" + ((String)s).toUpperCase().replace(' ', '_');
            int j = BiomeUtils.getId(resourcelocation);
            ConstantFloat iexpression = new ConstantFloat(j);
            this.registerExpression((String)s, iexpression);
        }
        Biome.Category[] abiome$category = Biome.Category.values();
        for (int l = 0; l < abiome$category.length; ++l) {
            Biome.Category biome$category = abiome$category[l];
            String s1 = "CAT_" + biome$category.getString().toUpperCase();
            ConstantFloat iexpression1 = new ConstantFloat(l);
            this.registerExpression(s1, iexpression1);
        }
        Biome.RainType[] abiome$raintype = Biome.RainType.values();
        for (int i1 = 0; i1 < abiome$raintype.length; ++i1) {
            Biome.RainType biome$raintype = abiome$raintype[i1];
            String s2 = "PPT_" + biome$raintype.getString().toUpperCase();
            ConstantFloat iexpression2 = new ConstantFloat(i1);
            this.registerExpression(s2, iexpression2);
        }
    }

    private void addParameterFloat(Map<String, IExpression> map, ShaderParameterFloat spf) {
        String[] astring = spf.getIndexNames1();
        if (astring == null) {
            map.put(spf.getName(), new ShaderParameterIndexed(spf));
        } else {
            for (int i = 0; i < astring.length; ++i) {
                String s = astring[i];
                String[] astring1 = spf.getIndexNames2();
                if (astring1 == null) {
                    map.put(spf.getName() + "." + s, new ShaderParameterIndexed(spf, i));
                    continue;
                }
                for (int j = 0; j < astring1.length; ++j) {
                    String s1 = astring1[j];
                    map.put(spf.getName() + "." + s + "." + s1, new ShaderParameterIndexed(spf, i, j));
                }
            }
        }
    }

    public boolean registerExpression(String name, IExpression expr) {
        if (this.mapExpressions.containsKey(name)) {
            SMCLog.warning("Expression already defined: " + name);
            return false;
        }
        this.mapExpressions.put(name, expr);
        return true;
    }

    @Override
    public IExpression getExpression(String name) {
        return this.mapExpressions.get(name);
    }

    public boolean hasExpression(String name) {
        return this.mapExpressions.containsKey(name);
    }
}
