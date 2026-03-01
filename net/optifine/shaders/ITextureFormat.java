package net.optifine.shaders;

import net.optifine.Config;
import net.optifine.config.ConfigUtils;
import net.optifine.shaders.ShadersTextureType;
import net.optifine.shaders.TextureFormatLabPbr;
import net.optifine.texture.IColorBlender;

public interface ITextureFormat {
    public IColorBlender getColorBlender(ShadersTextureType var1);

    public boolean isTextureBlend(ShadersTextureType var1);

    public String getMacroName();

    public String getMacroVersion();

    public static ITextureFormat readConfiguration() {
        if (!Config.isShaders()) {
            return null;
        }
        String s = ConfigUtils.readString("optifine/texture.properties", "format");
        if (s != null) {
            String s2;
            String[] astring = Config.tokenize(s, "/");
            String s1 = astring[0];
            String string = s2 = astring.length > 1 ? astring[1] : null;
            if (s1.equals("lab-pbr")) {
                return new TextureFormatLabPbr(s2);
            }
            Config.warn("Unknown texture format: " + s);
            return null;
        }
        return null;
    }
}
