package net.minecraft.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import net.minecraft.util.Util;

public interface IStatFormatter {
    public static final DecimalFormat DECIMAL_FORMAT = Util.make(new DecimalFormat("########0.00"), p_223254_0_ -> p_223254_0_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    public static final IStatFormatter DEFAULT = NumberFormat.getIntegerInstance(Locale.US)::format;
    public static final IStatFormatter DIVIDE_BY_TEN = p_223256_0_ -> DECIMAL_FORMAT.format((double)p_223256_0_ * 0.1);
    public static final IStatFormatter DISTANCE = p_223255_0_ -> {
        double d0 = (double)p_223255_0_ / 100.0;
        double d1 = d0 / 1000.0;
        if (d1 > 0.5) {
            return DECIMAL_FORMAT.format(d1) + " km";
        }
        return d0 > 0.5 ? DECIMAL_FORMAT.format(d0) + " m" : p_223255_0_ + " cm";
    };
    public static final IStatFormatter TIME = p_223253_0_ -> {
        double d0 = (double)p_223253_0_ / 20.0;
        double d1 = d0 / 60.0;
        double d2 = d1 / 60.0;
        double d3 = d2 / 24.0;
        double d4 = d3 / 365.0;
        if (d4 > 0.5) {
            return DECIMAL_FORMAT.format(d4) + " y";
        }
        if (d3 > 0.5) {
            return DECIMAL_FORMAT.format(d3) + " d";
        }
        if (d2 > 0.5) {
            return DECIMAL_FORMAT.format(d2) + " h";
        }
        return d1 > 0.5 ? DECIMAL_FORMAT.format(d1) + " m" : d0 + " s";
    };

    public String format(int var1);
}
