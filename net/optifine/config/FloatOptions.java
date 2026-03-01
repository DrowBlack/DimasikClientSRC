package net.optifine.config;

import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.optifine.Config;
import net.optifine.Lang;

public class FloatOptions {
    public static ITextComponent getTextComponent(AbstractOption option, double val) {
        if (option == AbstractOption.RENDER_DISTANCE) {
            return option.getGenericValueComponent(new TranslationTextComponent("options.chunks", (int)val));
        }
        if (option == AbstractOption.MIPMAP_LEVELS) {
            if (val >= 4.0) {
                return option.getGenericValueComponent(new TranslationTextComponent("of.general.max"));
            }
            return val == 0.0 ? DialogTexts.getComposedOptionMessage(option.getBaseMessageTranslation(), false) : option.getMessageWithValue((int)val);
        }
        if (option == AbstractOption.BIOME_BLEND_RADIUS) {
            int i = (int)val * 2 + 1;
            return option.getGenericValueComponent(new TranslationTextComponent("options.biomeBlendRadius." + i));
        }
        String s = FloatOptions.getText(option, val);
        return s != null ? new StringTextComponent(s) : null;
    }

    public static String getText(AbstractOption option, double val) {
        String s = I18n.format(option.getResourceKey(), new Object[0]) + ": ";
        if (option == AbstractOption.AO_LEVEL) {
            return val == 0.0 ? s + I18n.format("options.off", new Object[0]) : s + (int)(val * 100.0) + "%";
        }
        if (option == AbstractOption.MIPMAP_TYPE) {
            int k = (int)val;
            switch (k) {
                case 0: {
                    return s + Lang.get("of.options.mipmap.nearest");
                }
                case 1: {
                    return s + Lang.get("of.options.mipmap.linear");
                }
                case 2: {
                    return s + Lang.get("of.options.mipmap.bilinear");
                }
                case 3: {
                    return s + Lang.get("of.options.mipmap.trilinear");
                }
            }
            return s + "of.options.mipmap.nearest";
        }
        if (option == AbstractOption.AA_LEVEL) {
            int j = (int)val;
            Object s1 = "";
            if (j != Config.getAntialiasingLevel()) {
                s1 = " (" + Lang.get("of.general.restart") + ")";
            }
            return j == 0 ? s + Lang.getOff() + (String)s1 : s + j + (String)s1;
        }
        if (option == AbstractOption.AF_LEVEL) {
            int i = (int)val;
            return i == 1 ? s + Lang.getOff() : s + i;
        }
        return null;
    }

    public static boolean supportAdjusting(SliderPercentageOption option) {
        ITextComponent itextcomponent = FloatOptions.getTextComponent(option, 0.0);
        return itextcomponent != null;
    }
}
