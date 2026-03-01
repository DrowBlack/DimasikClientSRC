package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;

public class JSONBlendingMode {
    private static JSONBlendingMode lastApplied;
    private final int srcColorFactor;
    private final int srcAlphaFactor;
    private final int destColorFactor;
    private final int destAlphaFactor;
    private final int blendFunction;
    private final boolean separateBlend;
    private final boolean opaque;

    private JSONBlendingMode(boolean separateBlendIn, boolean opaqueIn, int srcColorFactorIn, int destColorFactorIn, int srcAlphaFactorIn, int destAlphaFactorIn, int blendFunctionIn) {
        this.separateBlend = separateBlendIn;
        this.srcColorFactor = srcColorFactorIn;
        this.destColorFactor = destColorFactorIn;
        this.srcAlphaFactor = srcAlphaFactorIn;
        this.destAlphaFactor = destAlphaFactorIn;
        this.opaque = opaqueIn;
        this.blendFunction = blendFunctionIn;
    }

    public JSONBlendingMode() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public JSONBlendingMode(int srcFactor, int dstFactor, int blendFunctionIn) {
        this(false, false, srcFactor, dstFactor, srcFactor, dstFactor, blendFunctionIn);
    }

    public JSONBlendingMode(int srcColorFactorIn, int destColorFactorIn, int srcAlphaFactorIn, int destAlphaFactorIn, int blendFunctionIn) {
        this(true, false, srcColorFactorIn, destColorFactorIn, srcAlphaFactorIn, destAlphaFactorIn, blendFunctionIn);
    }

    public void apply() {
        if (!this.equals(lastApplied)) {
            if (lastApplied == null || this.opaque != lastApplied.isOpaque()) {
                lastApplied = this;
                if (this.opaque) {
                    RenderSystem.disableBlend();
                    return;
                }
                RenderSystem.enableBlend();
            }
            RenderSystem.blendEquation(this.blendFunction);
            if (this.separateBlend) {
                RenderSystem.blendFuncSeparate(this.srcColorFactor, this.destColorFactor, this.srcAlphaFactor, this.destAlphaFactor);
            } else {
                RenderSystem.blendFunc(this.srcColorFactor, this.destColorFactor);
            }
        }
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        }
        if (!(p_equals_1_ instanceof JSONBlendingMode)) {
            return false;
        }
        JSONBlendingMode jsonblendingmode = (JSONBlendingMode)p_equals_1_;
        if (this.blendFunction != jsonblendingmode.blendFunction) {
            return false;
        }
        if (this.destAlphaFactor != jsonblendingmode.destAlphaFactor) {
            return false;
        }
        if (this.destColorFactor != jsonblendingmode.destColorFactor) {
            return false;
        }
        if (this.opaque != jsonblendingmode.opaque) {
            return false;
        }
        if (this.separateBlend != jsonblendingmode.separateBlend) {
            return false;
        }
        if (this.srcAlphaFactor != jsonblendingmode.srcAlphaFactor) {
            return false;
        }
        return this.srcColorFactor == jsonblendingmode.srcColorFactor;
    }

    public int hashCode() {
        int i = this.srcColorFactor;
        i = 31 * i + this.srcAlphaFactor;
        i = 31 * i + this.destColorFactor;
        i = 31 * i + this.destAlphaFactor;
        i = 31 * i + this.blendFunction;
        i = 31 * i + (this.separateBlend ? 1 : 0);
        return 31 * i + (this.opaque ? 1 : 0);
    }

    public boolean isOpaque() {
        return this.opaque;
    }

    public static int stringToBlendFunction(String funcName) {
        String s = funcName.trim().toLowerCase(Locale.ROOT);
        if ("add".equals(s)) {
            return 32774;
        }
        if ("subtract".equals(s)) {
            return 32778;
        }
        if ("reversesubtract".equals(s)) {
            return 32779;
        }
        if ("reverse_subtract".equals(s)) {
            return 32779;
        }
        if ("min".equals(s)) {
            return 32775;
        }
        return "max".equals(s) ? 32776 : 32774;
    }

    public static int stringToBlendFactor(String factorName) {
        String s = factorName.trim().toLowerCase(Locale.ROOT);
        s = s.replaceAll("_", "");
        s = s.replaceAll("one", "1");
        s = s.replaceAll("zero", "0");
        if ("0".equals(s = s.replaceAll("minus", "-"))) {
            return 0;
        }
        if ("1".equals(s)) {
            return 1;
        }
        if ("srccolor".equals(s)) {
            return 768;
        }
        if ("1-srccolor".equals(s)) {
            return 769;
        }
        if ("dstcolor".equals(s)) {
            return 774;
        }
        if ("1-dstcolor".equals(s)) {
            return 775;
        }
        if ("srcalpha".equals(s)) {
            return 770;
        }
        if ("1-srcalpha".equals(s)) {
            return 771;
        }
        if ("dstalpha".equals(s)) {
            return 772;
        }
        return "1-dstalpha".equals(s) ? 773 : -1;
    }
}
