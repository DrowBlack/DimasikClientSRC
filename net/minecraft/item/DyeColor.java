package net.minecraft.item;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.optifine.reflect.Reflector;

public enum DyeColor implements IStringSerializable
{
    WHITE(0, "white", 0xF9FFFE, MaterialColor.SNOW, 0xF0F0F0, 0xFFFFFF),
    ORANGE(1, "orange", 16351261, MaterialColor.ADOBE, 15435844, 16738335),
    MAGENTA(2, "magenta", 13061821, MaterialColor.MAGENTA, 12801229, 0xFF00FF),
    LIGHT_BLUE(3, "light_blue", 3847130, MaterialColor.LIGHT_BLUE, 6719955, 10141901),
    YELLOW(4, "yellow", 16701501, MaterialColor.YELLOW, 14602026, 0xFFFF00),
    LIME(5, "lime", 8439583, MaterialColor.LIME, 4312372, 0xBFFF00),
    PINK(6, "pink", 15961002, MaterialColor.PINK, 14188952, 16738740),
    GRAY(7, "gray", 4673362, MaterialColor.GRAY, 0x434343, 0x808080),
    LIGHT_GRAY(8, "light_gray", 0x9D9D97, MaterialColor.LIGHT_GRAY, 0xABABAB, 0xD3D3D3),
    CYAN(9, "cyan", 1481884, MaterialColor.CYAN, 2651799, 65535),
    PURPLE(10, "purple", 8991416, MaterialColor.PURPLE, 8073150, 10494192),
    BLUE(11, "blue", 3949738, MaterialColor.BLUE, 2437522, 255),
    BROWN(12, "brown", 8606770, MaterialColor.BROWN, 5320730, 9127187),
    GREEN(13, "green", 6192150, MaterialColor.GREEN, 3887386, 65280),
    RED(14, "red", 11546150, MaterialColor.RED, 11743532, 0xFF0000),
    BLACK(15, "black", 0x1D1D21, MaterialColor.BLACK, 0x1E1B1B, 0);

    private static final DyeColor[] VALUES;
    private static final Int2ObjectOpenHashMap<DyeColor> BY_FIREWORK_COLOR;
    private final int id;
    private final String translationKey;
    private final MaterialColor mapColor;
    private final int colorValue;
    private final int swappedColorValue;
    private float[] colorComponentValues;
    private final int fireworkColor;
    private final Tags.IOptionalNamedTag<Item> tag;
    private final int textColor;

    private DyeColor(int idIn, String translationKeyIn, int colorValueIn, MaterialColor mapColorIn, int fireworkColorIn, int textColorIn) {
        this.id = idIn;
        this.translationKey = translationKeyIn;
        this.colorValue = colorValueIn;
        this.mapColor = mapColorIn;
        this.textColor = textColorIn;
        int i = (colorValueIn & 0xFF0000) >> 16;
        int j = (colorValueIn & 0xFF00) >> 8;
        int k = (colorValueIn & 0xFF) >> 0;
        this.swappedColorValue = k << 16 | j << 8 | i << 0;
        this.tag = (Tags.IOptionalNamedTag)Reflector.ForgeItemTags_createOptional.call((Object)new ResourceLocation("forge", "dyes/" + translationKeyIn));
        this.colorComponentValues = new float[]{(float)i / 255.0f, (float)j / 255.0f, (float)k / 255.0f};
        this.fireworkColor = fireworkColorIn;
    }

    public int getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return this.translationKey;
    }

    public float[] getColorComponentValues() {
        return this.colorComponentValues;
    }

    public MaterialColor getMapColor() {
        return this.mapColor;
    }

    public int getFireworkColor() {
        return this.fireworkColor;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public static DyeColor byId(int colorId) {
        if (colorId < 0 || colorId >= VALUES.length) {
            colorId = 0;
        }
        return VALUES[colorId];
    }

    public static DyeColor byTranslationKey(String translationKeyIn, DyeColor fallback) {
        for (DyeColor dyecolor : DyeColor.values()) {
            if (!dyecolor.translationKey.equals(translationKeyIn)) continue;
            return dyecolor;
        }
        return fallback;
    }

    @Nullable
    public static DyeColor byFireworkColor(int fireworkColorIn) {
        return BY_FIREWORK_COLOR.get(fireworkColorIn);
    }

    public String toString() {
        return this.translationKey;
    }

    @Override
    public String getString() {
        return this.translationKey;
    }

    public void setColorComponentValues(float[] p_setColorComponentValues_1_) {
        this.colorComponentValues = p_setColorComponentValues_1_;
    }

    public int getColorValue() {
        return this.colorValue;
    }

    public Tags.IOptionalNamedTag<Item> getTag() {
        return this.tag;
    }

    @Nullable
    public static DyeColor getColor(ItemStack p_getColor_0_) {
        if (p_getColor_0_.getItem() instanceof DyeItem) {
            return ((DyeItem)p_getColor_0_.getItem()).getDyeColor();
        }
        for (DyeColor dyecolor : VALUES) {
            if (!p_getColor_0_.getItem().isIn(dyecolor.getTag())) continue;
            return dyecolor;
        }
        return null;
    }

    static {
        VALUES = (DyeColor[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).toArray(DyeColor[]::new);
        BY_FIREWORK_COLOR = new Int2ObjectOpenHashMap<DyeColor>(Arrays.stream(DyeColor.values()).collect(Collectors.toMap(p_lambda$static$1_0_ -> p_lambda$static$1_0_.fireworkColor, p_lambda$static$2_0_ -> p_lambda$static$2_0_)));
    }
}
