package net.optifine.entity.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntityType;
import net.optifine.Config;
import net.optifine.entity.model.ModelAdapter;
import net.optifine.entity.model.ModelAdapterArmorStand;
import net.optifine.entity.model.ModelAdapterBanner;
import net.optifine.entity.model.ModelAdapterBat;
import net.optifine.entity.model.ModelAdapterBed;
import net.optifine.entity.model.ModelAdapterBee;
import net.optifine.entity.model.ModelAdapterBell;
import net.optifine.entity.model.ModelAdapterBlaze;
import net.optifine.entity.model.ModelAdapterBoat;
import net.optifine.entity.model.ModelAdapterBook;
import net.optifine.entity.model.ModelAdapterBookLectern;
import net.optifine.entity.model.ModelAdapterCat;
import net.optifine.entity.model.ModelAdapterCaveSpider;
import net.optifine.entity.model.ModelAdapterChest;
import net.optifine.entity.model.ModelAdapterChestLarge;
import net.optifine.entity.model.ModelAdapterChicken;
import net.optifine.entity.model.ModelAdapterCod;
import net.optifine.entity.model.ModelAdapterConduit;
import net.optifine.entity.model.ModelAdapterCow;
import net.optifine.entity.model.ModelAdapterCreeper;
import net.optifine.entity.model.ModelAdapterDolphin;
import net.optifine.entity.model.ModelAdapterDonkey;
import net.optifine.entity.model.ModelAdapterDragon;
import net.optifine.entity.model.ModelAdapterDrowned;
import net.optifine.entity.model.ModelAdapterElderGuardian;
import net.optifine.entity.model.ModelAdapterEnderChest;
import net.optifine.entity.model.ModelAdapterEnderCrystal;
import net.optifine.entity.model.ModelAdapterEnderman;
import net.optifine.entity.model.ModelAdapterEndermite;
import net.optifine.entity.model.ModelAdapterEvoker;
import net.optifine.entity.model.ModelAdapterEvokerFangs;
import net.optifine.entity.model.ModelAdapterFox;
import net.optifine.entity.model.ModelAdapterGhast;
import net.optifine.entity.model.ModelAdapterGiant;
import net.optifine.entity.model.ModelAdapterGuardian;
import net.optifine.entity.model.ModelAdapterHeadCreeper;
import net.optifine.entity.model.ModelAdapterHeadDragon;
import net.optifine.entity.model.ModelAdapterHeadPlayer;
import net.optifine.entity.model.ModelAdapterHeadSkeleton;
import net.optifine.entity.model.ModelAdapterHeadWitherSkeleton;
import net.optifine.entity.model.ModelAdapterHeadZombie;
import net.optifine.entity.model.ModelAdapterHoglin;
import net.optifine.entity.model.ModelAdapterHorse;
import net.optifine.entity.model.ModelAdapterHusk;
import net.optifine.entity.model.ModelAdapterIllusioner;
import net.optifine.entity.model.ModelAdapterIronGolem;
import net.optifine.entity.model.ModelAdapterLeadKnot;
import net.optifine.entity.model.ModelAdapterLlama;
import net.optifine.entity.model.ModelAdapterLlamaDecor;
import net.optifine.entity.model.ModelAdapterLlamaSpit;
import net.optifine.entity.model.ModelAdapterMagmaCube;
import net.optifine.entity.model.ModelAdapterMinecart;
import net.optifine.entity.model.ModelAdapterMinecartChest;
import net.optifine.entity.model.ModelAdapterMinecartCommandBlock;
import net.optifine.entity.model.ModelAdapterMinecartFurnace;
import net.optifine.entity.model.ModelAdapterMinecartHopper;
import net.optifine.entity.model.ModelAdapterMinecartMobSpawner;
import net.optifine.entity.model.ModelAdapterMinecartTnt;
import net.optifine.entity.model.ModelAdapterMooshroom;
import net.optifine.entity.model.ModelAdapterMule;
import net.optifine.entity.model.ModelAdapterOcelot;
import net.optifine.entity.model.ModelAdapterPanda;
import net.optifine.entity.model.ModelAdapterParrot;
import net.optifine.entity.model.ModelAdapterPhantom;
import net.optifine.entity.model.ModelAdapterPig;
import net.optifine.entity.model.ModelAdapterPiglin;
import net.optifine.entity.model.ModelAdapterPiglinBrute;
import net.optifine.entity.model.ModelAdapterPillager;
import net.optifine.entity.model.ModelAdapterPolarBear;
import net.optifine.entity.model.ModelAdapterPufferFishBig;
import net.optifine.entity.model.ModelAdapterPufferFishMedium;
import net.optifine.entity.model.ModelAdapterPufferFishSmall;
import net.optifine.entity.model.ModelAdapterRabbit;
import net.optifine.entity.model.ModelAdapterRavager;
import net.optifine.entity.model.ModelAdapterSalmon;
import net.optifine.entity.model.ModelAdapterSheep;
import net.optifine.entity.model.ModelAdapterSheepWool;
import net.optifine.entity.model.ModelAdapterShulker;
import net.optifine.entity.model.ModelAdapterShulkerBox;
import net.optifine.entity.model.ModelAdapterShulkerBullet;
import net.optifine.entity.model.ModelAdapterSign;
import net.optifine.entity.model.ModelAdapterSilverfish;
import net.optifine.entity.model.ModelAdapterSkeleton;
import net.optifine.entity.model.ModelAdapterSkeletonHorse;
import net.optifine.entity.model.ModelAdapterSlime;
import net.optifine.entity.model.ModelAdapterSnowman;
import net.optifine.entity.model.ModelAdapterSpider;
import net.optifine.entity.model.ModelAdapterSquid;
import net.optifine.entity.model.ModelAdapterStray;
import net.optifine.entity.model.ModelAdapterStrider;
import net.optifine.entity.model.ModelAdapterTraderLlama;
import net.optifine.entity.model.ModelAdapterTrappedChest;
import net.optifine.entity.model.ModelAdapterTrappedChestLarge;
import net.optifine.entity.model.ModelAdapterTrident;
import net.optifine.entity.model.ModelAdapterTropicalFishA;
import net.optifine.entity.model.ModelAdapterTropicalFishB;
import net.optifine.entity.model.ModelAdapterTurtle;
import net.optifine.entity.model.ModelAdapterVex;
import net.optifine.entity.model.ModelAdapterVillager;
import net.optifine.entity.model.ModelAdapterVindicator;
import net.optifine.entity.model.ModelAdapterWanderingTrader;
import net.optifine.entity.model.ModelAdapterWitch;
import net.optifine.entity.model.ModelAdapterWither;
import net.optifine.entity.model.ModelAdapterWitherSkeleton;
import net.optifine.entity.model.ModelAdapterWitherSkull;
import net.optifine.entity.model.ModelAdapterWolf;
import net.optifine.entity.model.ModelAdapterZoglin;
import net.optifine.entity.model.ModelAdapterZombie;
import net.optifine.entity.model.ModelAdapterZombieHorse;
import net.optifine.entity.model.ModelAdapterZombieVillager;
import net.optifine.entity.model.ModelAdapterZombifiedPiglin;
import net.optifine.util.Either;

public class CustomModelRegistry {
    private static Map<String, ModelAdapter> mapModelAdapters = CustomModelRegistry.makeMapModelAdapters();

    private static Map<String, ModelAdapter> makeMapModelAdapters() {
        LinkedHashMap<String, ModelAdapter> map = new LinkedHashMap<String, ModelAdapter>();
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterArmorStand());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBat());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBee());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBlaze());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBoat());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCaveSpider());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterChicken());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCat());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCow());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCod());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterCreeper());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterDragon());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterDonkey());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterDolphin());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterDrowned());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterElderGuardian());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEnderCrystal());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEnderman());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEndermite());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEvoker());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEvokerFangs());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterFox());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterGhast());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterGiant());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterGuardian());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHoglin());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHorse());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHusk());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterIllusioner());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterIronGolem());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterLeadKnot());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterLlama());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterLlamaSpit());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMagmaCube());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecart());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartChest());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartCommandBlock());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartFurnace());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartHopper());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartTnt());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMinecartMobSpawner());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMooshroom());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterMule());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterOcelot());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPanda());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterParrot());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPhantom());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPig());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPiglin());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPiglinBrute());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPolarBear());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPillager());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPufferFishBig());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPufferFishMedium());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterPufferFishSmall());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterRabbit());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterRavager());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSalmon());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSheep());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterShulker());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterShulkerBullet());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSilverfish());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSkeleton());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSkeletonHorse());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSlime());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSnowman());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSpider());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSquid());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterStray());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterStrider());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterTraderLlama());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterTrident());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterTropicalFishA());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterTropicalFishB());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterTurtle());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterVex());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterVillager());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterVindicator());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWanderingTrader());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWitch());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWither());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWitherSkeleton());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWitherSkull());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterWolf());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZoglin());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZombie());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZombieHorse());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZombieVillager());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterZombifiedPiglin());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSheepWool());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterLlamaDecor());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBanner());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBed());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBell());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBook());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterBookLectern());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterChest());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterChestLarge());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterConduit());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterEnderChest());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadCreeper());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadDragon());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadPlayer());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadSkeleton());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadWitherSkeleton());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterHeadZombie());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterShulkerBox());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterSign());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterTrappedChest());
        CustomModelRegistry.addModelAdapter(map, new ModelAdapterTrappedChestLarge());
        return map;
    }

    private static void addModelAdapter(Map<String, ModelAdapter> map, ModelAdapter modelAdapter) {
        CustomModelRegistry.addModelAdapter(map, modelAdapter, modelAdapter.getName());
        String[] astring = modelAdapter.getAliases();
        if (astring != null) {
            for (int i = 0; i < astring.length; ++i) {
                String s = astring[i];
                CustomModelRegistry.addModelAdapter(map, modelAdapter, s);
            }
        }
        Model model = modelAdapter.makeModel();
        String[] astring1 = modelAdapter.getModelRendererNames();
        for (int j = 0; j < astring1.length; ++j) {
            String s1 = astring1[j];
            ModelRenderer modelrenderer = modelAdapter.getModelRenderer(model, s1);
            if (modelrenderer != null) continue;
            Config.warn("Model renderer not found, model: " + modelAdapter.getName() + ", name: " + s1);
        }
    }

    private static void addModelAdapter(Map<String, ModelAdapter> map, ModelAdapter modelAdapter, String name) {
        if (map.containsKey(name)) {
            Object s = "?";
            Either<EntityType, TileEntityType> either = modelAdapter.getType();
            if (either.getLeft().isPresent()) {
                s = either.getLeft().get().getTranslationKey();
            }
            if (either.getRight().isPresent()) {
                s = String.valueOf(TileEntityType.getId(either.getRight().get()));
            }
            Config.warn("Model adapter already registered for id: " + name + ", type: " + (String)s);
        }
        map.put(name, modelAdapter);
    }

    public static ModelAdapter getModelAdapter(String name) {
        return mapModelAdapters.get(name);
    }

    public static String[] getModelNames() {
        Set<String> set = mapModelAdapters.keySet();
        return set.toArray(new String[set.size()]);
    }
}
