package net.minecraft.world.gen.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.util.Util;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.AddBambooForestLayer;
import net.minecraft.world.gen.layer.AddIslandLayer;
import net.minecraft.world.gen.layer.AddMushroomIslandLayer;
import net.minecraft.world.gen.layer.AddSnowLayer;
import net.minecraft.world.gen.layer.BiomeLayer;
import net.minecraft.world.gen.layer.DeepOceanLayer;
import net.minecraft.world.gen.layer.EdgeBiomeLayer;
import net.minecraft.world.gen.layer.EdgeLayer;
import net.minecraft.world.gen.layer.HillsLayer;
import net.minecraft.world.gen.layer.IslandLayer;
import net.minecraft.world.gen.layer.Layer;
import net.minecraft.world.gen.layer.MixOceansLayer;
import net.minecraft.world.gen.layer.MixRiverLayer;
import net.minecraft.world.gen.layer.OceanLayer;
import net.minecraft.world.gen.layer.RareBiomeLayer;
import net.minecraft.world.gen.layer.RemoveTooMuchOceanLayer;
import net.minecraft.world.gen.layer.RiverLayer;
import net.minecraft.world.gen.layer.ShoreLayer;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.StartRiverLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public class LayerUtil {
    private static final Int2IntMap field_242937_a = Util.make(new Int2IntOpenHashMap(), p_242938_0_ -> {
        LayerUtil.func_242939_a(p_242938_0_, Type.BEACH, 16);
        LayerUtil.func_242939_a(p_242938_0_, Type.BEACH, 26);
        LayerUtil.func_242939_a(p_242938_0_, Type.DESERT, 2);
        LayerUtil.func_242939_a(p_242938_0_, Type.DESERT, 17);
        LayerUtil.func_242939_a(p_242938_0_, Type.DESERT, 130);
        LayerUtil.func_242939_a(p_242938_0_, Type.EXTREME_HILLS, 131);
        LayerUtil.func_242939_a(p_242938_0_, Type.EXTREME_HILLS, 162);
        LayerUtil.func_242939_a(p_242938_0_, Type.EXTREME_HILLS, 20);
        LayerUtil.func_242939_a(p_242938_0_, Type.EXTREME_HILLS, 3);
        LayerUtil.func_242939_a(p_242938_0_, Type.EXTREME_HILLS, 34);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 27);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 28);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 29);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 157);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 132);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 4);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 155);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 156);
        LayerUtil.func_242939_a(p_242938_0_, Type.FOREST, 18);
        LayerUtil.func_242939_a(p_242938_0_, Type.ICY, 140);
        LayerUtil.func_242939_a(p_242938_0_, Type.ICY, 13);
        LayerUtil.func_242939_a(p_242938_0_, Type.ICY, 12);
        LayerUtil.func_242939_a(p_242938_0_, Type.JUNGLE, 168);
        LayerUtil.func_242939_a(p_242938_0_, Type.JUNGLE, 169);
        LayerUtil.func_242939_a(p_242938_0_, Type.JUNGLE, 21);
        LayerUtil.func_242939_a(p_242938_0_, Type.JUNGLE, 23);
        LayerUtil.func_242939_a(p_242938_0_, Type.JUNGLE, 22);
        LayerUtil.func_242939_a(p_242938_0_, Type.JUNGLE, 149);
        LayerUtil.func_242939_a(p_242938_0_, Type.JUNGLE, 151);
        LayerUtil.func_242939_a(p_242938_0_, Type.MESA, 37);
        LayerUtil.func_242939_a(p_242938_0_, Type.MESA, 165);
        LayerUtil.func_242939_a(p_242938_0_, Type.MESA, 167);
        LayerUtil.func_242939_a(p_242938_0_, Type.MESA, 166);
        LayerUtil.func_242939_a(p_242938_0_, Type.BADLANDS_PLATEAU, 39);
        LayerUtil.func_242939_a(p_242938_0_, Type.BADLANDS_PLATEAU, 38);
        LayerUtil.func_242939_a(p_242938_0_, Type.MUSHROOM, 14);
        LayerUtil.func_242939_a(p_242938_0_, Type.MUSHROOM, 15);
        LayerUtil.func_242939_a(p_242938_0_, Type.NONE, 25);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 46);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 49);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 50);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 48);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 24);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 47);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 10);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 45);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 0);
        LayerUtil.func_242939_a(p_242938_0_, Type.OCEAN, 44);
        LayerUtil.func_242939_a(p_242938_0_, Type.PLAINS, 1);
        LayerUtil.func_242939_a(p_242938_0_, Type.PLAINS, 129);
        LayerUtil.func_242939_a(p_242938_0_, Type.RIVER, 11);
        LayerUtil.func_242939_a(p_242938_0_, Type.RIVER, 7);
        LayerUtil.func_242939_a(p_242938_0_, Type.SAVANNA, 35);
        LayerUtil.func_242939_a(p_242938_0_, Type.SAVANNA, 36);
        LayerUtil.func_242939_a(p_242938_0_, Type.SAVANNA, 163);
        LayerUtil.func_242939_a(p_242938_0_, Type.SAVANNA, 164);
        LayerUtil.func_242939_a(p_242938_0_, Type.SWAMP, 6);
        LayerUtil.func_242939_a(p_242938_0_, Type.SWAMP, 134);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 160);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 161);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 32);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 33);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 30);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 31);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 158);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 5);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 19);
        LayerUtil.func_242939_a(p_242938_0_, Type.TAIGA, 133);
    });

    private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> repeat(long seed, IAreaTransformer1 parent, IAreaFactory<T> p_202829_3_, int count, LongFunction<C> contextFactory) {
        IAreaFactory<T> iareafactory = p_202829_3_;
        for (int i = 0; i < count; ++i) {
            iareafactory = parent.apply((IExtendedNoiseRandom)contextFactory.apply(seed + (long)i), iareafactory);
        }
        return iareafactory;
    }

    private static <T extends IArea, C extends IExtendedNoiseRandom<T>> IAreaFactory<T> func_237216_a_(boolean p_237216_0_, int p_237216_1_, int p_237216_2_, LongFunction<C> p_237216_3_) {
        IAreaFactory iareafactory = IslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1L));
        iareafactory = ZoomLayer.FUZZY.apply((IExtendedNoiseRandom)p_237216_3_.apply(2000L), iareafactory);
        iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1L), iareafactory);
        iareafactory = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_237216_3_.apply(2001L), iareafactory);
        iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(2L), iareafactory);
        iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(50L), iareafactory);
        iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(70L), iareafactory);
        iareafactory = RemoveTooMuchOceanLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(2L), iareafactory);
        IAreaFactory iareafactory1 = OceanLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(2L));
        iareafactory1 = LayerUtil.repeat(2001L, ZoomLayer.NORMAL, iareafactory1, 6, p_237216_3_);
        iareafactory = AddSnowLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(2L), iareafactory);
        iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(3L), iareafactory);
        iareafactory = EdgeLayer.CoolWarm.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(2L), iareafactory);
        iareafactory = EdgeLayer.HeatIce.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(2L), iareafactory);
        iareafactory = EdgeLayer.Special.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(3L), iareafactory);
        iareafactory = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_237216_3_.apply(2002L), iareafactory);
        iareafactory = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_237216_3_.apply(2003L), iareafactory);
        iareafactory = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(4L), iareafactory);
        iareafactory = AddMushroomIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(5L), iareafactory);
        iareafactory = DeepOceanLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(4L), iareafactory);
        iareafactory = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_237216_3_);
        IAreaFactory lvt_6_1_ = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, iareafactory, 0, p_237216_3_);
        lvt_6_1_ = StartRiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(100L), lvt_6_1_);
        IAreaFactory lvt_7_1_ = new BiomeLayer(p_237216_0_).apply((IExtendedNoiseRandom)p_237216_3_.apply(200L), iareafactory);
        lvt_7_1_ = AddBambooForestLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1001L), lvt_7_1_);
        lvt_7_1_ = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, lvt_7_1_, 2, p_237216_3_);
        lvt_7_1_ = EdgeBiomeLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1000L), lvt_7_1_);
        IAreaFactory lvt_8_1_ = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, lvt_6_1_, 2, p_237216_3_);
        lvt_7_1_ = HillsLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1000L), lvt_7_1_, lvt_8_1_);
        lvt_6_1_ = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, lvt_6_1_, 2, p_237216_3_);
        lvt_6_1_ = LayerUtil.repeat(1000L, ZoomLayer.NORMAL, lvt_6_1_, p_237216_2_, p_237216_3_);
        lvt_6_1_ = RiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1L), lvt_6_1_);
        lvt_6_1_ = SmoothLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1000L), lvt_6_1_);
        lvt_7_1_ = RareBiomeLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1001L), lvt_7_1_);
        for (int i = 0; i < p_237216_1_; ++i) {
            lvt_7_1_ = ZoomLayer.NORMAL.apply((IExtendedNoiseRandom)p_237216_3_.apply(1000 + i), lvt_7_1_);
            if (i == 0) {
                lvt_7_1_ = AddIslandLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(3L), lvt_7_1_);
            }
            if (i != 1 && p_237216_1_ != 1) continue;
            lvt_7_1_ = ShoreLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1000L), lvt_7_1_);
        }
        lvt_7_1_ = SmoothLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(1000L), lvt_7_1_);
        lvt_7_1_ = MixRiverLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(100L), lvt_7_1_, lvt_6_1_);
        return MixOceansLayer.INSTANCE.apply((IExtendedNoiseRandom)p_237216_3_.apply(100L), lvt_7_1_, iareafactory1);
    }

    public static Layer func_237215_a_(long p_237215_0_, boolean p_237215_2_, int p_237215_3_, int p_237215_4_) {
        int i = 25;
        IAreaFactory<LazyArea> iareafactory = LayerUtil.func_237216_a_(p_237215_2_, p_237215_3_, p_237215_4_, p_227473_2_ -> new LazyAreaLayerContext(25, p_237215_0_, p_227473_2_));
        return new Layer(iareafactory);
    }

    public static boolean areBiomesSimilar(int p_202826_0_, int p_202826_1_) {
        if (p_202826_0_ == p_202826_1_) {
            return true;
        }
        return field_242937_a.get(p_202826_0_) == field_242937_a.get(p_202826_1_);
    }

    private static void func_242939_a(Int2IntOpenHashMap p_242939_0_, Type p_242939_1_, int p_242939_2_) {
        p_242939_0_.put(p_242939_2_, p_242939_1_.ordinal());
    }

    protected static boolean isOcean(int biomeIn) {
        return biomeIn == 44 || biomeIn == 45 || biomeIn == 0 || biomeIn == 46 || biomeIn == 10 || biomeIn == 47 || biomeIn == 48 || biomeIn == 24 || biomeIn == 49 || biomeIn == 50;
    }

    protected static boolean isShallowOcean(int biomeIn) {
        return biomeIn == 44 || biomeIn == 45 || biomeIn == 0 || biomeIn == 46 || biomeIn == 10;
    }

    static enum Type {
        NONE,
        TAIGA,
        EXTREME_HILLS,
        JUNGLE,
        MESA,
        BADLANDS_PLATEAU,
        PLAINS,
        SAVANNA,
        ICY,
        BEACH,
        FOREST,
        OCEAN,
        DESERT,
        RIVER,
        SWAMP,
        MUSHROOM;

    }
}
