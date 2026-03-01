package net.optifine;

import com.mojang.blaze3d.platform.GlStateManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemModelGenerator;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import net.optifine.Config;
import net.optifine.CustomItemProperties;
import net.optifine.CustomItemsComparator;
import net.optifine.config.NbtTagValue;
import net.optifine.render.Blender;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.EnchantmentUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class CustomItems {
    private static CustomItemProperties[][] itemProperties = null;
    private static CustomItemProperties[][] enchantmentProperties = null;
    private static Map mapPotionIds = null;
    private static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
    private static boolean useGlint = true;
    private static boolean renderOffHand = false;
    public static final int MASK_POTION_SPLASH = 16384;
    public static final int MASK_POTION_NAME = 63;
    public static final int MASK_POTION_EXTENDED = 64;
    public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
    public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
    public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
    public static final String DEFAULT_TEXTURE_OVERLAY = "item/potion_overlay";
    public static final String DEFAULT_TEXTURE_SPLASH = "item/potion_bottle_splash";
    public static final String DEFAULT_TEXTURE_DRINKABLE = "item/potion_bottle_drinkable";
    private static final int[][] EMPTY_INT2_ARRAY = new int[0][];
    private static final Map<String, Integer> mapPotionDamages = CustomItems.makeMapPotionDamages();
    private static final String TYPE_POTION_NORMAL = "normal";
    private static final String TYPE_POTION_SPLASH = "splash";
    private static final String TYPE_POTION_LINGER = "linger";

    public static void update() {
        itemProperties = null;
        enchantmentProperties = null;
        useGlint = true;
        if (Config.isCustomItems()) {
            CustomItems.readCitProperties("optifine/cit.properties");
            IResourcePack[] airesourcepack = Config.getResourcePacks();
            for (int i = airesourcepack.length - 1; i >= 0; --i) {
                IResourcePack iresourcepack = airesourcepack[i];
                CustomItems.update(iresourcepack);
            }
            CustomItems.update(Config.getDefaultResourcePack());
            if (itemProperties.length <= 0) {
                itemProperties = null;
            }
            if (enchantmentProperties.length <= 0) {
                enchantmentProperties = null;
            }
        }
    }

    private static void readCitProperties(String fileName) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(fileName);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            Config.dbg("CustomItems: Loading " + fileName);
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            useGlint = Config.parseBoolean(properties.getProperty("useGlint"), true);
        }
        catch (FileNotFoundException filenotfoundexception) {
            return;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static void update(IResourcePack rp) {
        Object[] astring = ResUtils.collectFiles(rp, "optifine/cit/", ".properties", (String[])null);
        Map<String, CustomItemProperties> map = CustomItems.makeAutoImageProperties(rp);
        if (map.size() > 0) {
            Set<String> set = map.keySet();
            Object[] astring1 = set.toArray(new String[set.size()]);
            astring = (String[])Config.addObjectsToArray(astring, astring1);
        }
        Arrays.sort(astring);
        List<List<CustomItemProperties>> list = CustomItems.makePropertyList(itemProperties);
        List<List<CustomItemProperties>> list1 = CustomItems.makePropertyList(enchantmentProperties);
        for (int i = 0; i < astring.length; ++i) {
            Object s = astring[i];
            Config.dbg("CustomItems: " + (String)s);
            try {
                CustomItemProperties customitemproperties = null;
                if (map.containsKey(s)) {
                    customitemproperties = map.get(s);
                }
                if (customitemproperties == null) {
                    ResourceLocation resourcelocation = new ResourceLocation((String)s);
                    InputStream inputstream = rp.getResourceStream(ResourcePackType.CLIENT_RESOURCES, resourcelocation);
                    if (inputstream == null) {
                        Config.warn("CustomItems file not found: " + (String)s);
                        continue;
                    }
                    PropertiesOrdered properties = new PropertiesOrdered();
                    properties.load(inputstream);
                    inputstream.close();
                    customitemproperties = new CustomItemProperties(properties, (String)s);
                }
                if (!customitemproperties.isValid((String)s)) continue;
                CustomItems.addToItemList(customitemproperties, list);
                CustomItems.addToEnchantmentList(customitemproperties, list1);
                continue;
            }
            catch (FileNotFoundException filenotfoundexception) {
                Config.warn("CustomItems file not found: " + (String)s);
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        itemProperties = CustomItems.propertyListToArray(list);
        enchantmentProperties = CustomItems.propertyListToArray(list1);
        Comparator comparator = CustomItems.getPropertiesComparator();
        for (int j = 0; j < itemProperties.length; ++j) {
            CustomItemProperties[] acustomitemproperties = itemProperties[j];
            if (acustomitemproperties == null) continue;
            Arrays.sort(acustomitemproperties, comparator);
        }
        for (int k = 0; k < enchantmentProperties.length; ++k) {
            CustomItemProperties[] acustomitemproperties1 = enchantmentProperties[k];
            if (acustomitemproperties1 == null) continue;
            Arrays.sort(acustomitemproperties1, comparator);
        }
    }

    private static Comparator getPropertiesComparator() {
        return new Comparator(){

            public int compare(Object o1, Object o2) {
                CustomItemProperties customitemproperties = (CustomItemProperties)o1;
                CustomItemProperties customitemproperties1 = (CustomItemProperties)o2;
                if (customitemproperties.layer != customitemproperties1.layer) {
                    return customitemproperties.layer - customitemproperties1.layer;
                }
                if (customitemproperties.weight != customitemproperties1.weight) {
                    return customitemproperties1.weight - customitemproperties.weight;
                }
                return !customitemproperties.basePath.equals(customitemproperties1.basePath) ? customitemproperties.basePath.compareTo(customitemproperties1.basePath) : customitemproperties.name.compareTo(customitemproperties1.name);
            }
        };
    }

    public static void updateIcons(AtlasTexture textureMap) {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            customitemproperties.updateIcons(textureMap);
        }
    }

    public static void refreshIcons(AtlasTexture textureMap) {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            customitemproperties.refreshIcons(textureMap);
        }
    }

    public static void loadModels(ModelBakery modelBakery) {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            customitemproperties.loadModels(modelBakery);
        }
    }

    public static void updateModels() {
        for (CustomItemProperties customitemproperties : CustomItems.getAllProperties()) {
            if (customitemproperties.type != 1) continue;
            AtlasTexture atlastexture = Config.getTextureMap();
            customitemproperties.updateModelTexture(atlastexture, itemModelGenerator);
            customitemproperties.updateModelsFull();
        }
    }

    private static List<CustomItemProperties> getAllProperties() {
        ArrayList<CustomItemProperties> list = new ArrayList<CustomItemProperties>();
        CustomItems.addAll(itemProperties, list);
        CustomItems.addAll(enchantmentProperties, list);
        return list;
    }

    private static void addAll(CustomItemProperties[][] cipsArr, List<CustomItemProperties> list) {
        if (cipsArr != null) {
            for (int i = 0; i < cipsArr.length; ++i) {
                CustomItemProperties[] acustomitemproperties = cipsArr[i];
                if (acustomitemproperties == null) continue;
                for (int j = 0; j < acustomitemproperties.length; ++j) {
                    CustomItemProperties customitemproperties = acustomitemproperties[j];
                    if (customitemproperties == null) continue;
                    list.add(customitemproperties);
                }
            }
        }
    }

    private static Map<String, CustomItemProperties> makeAutoImageProperties(IResourcePack rp) {
        HashMap<String, CustomItemProperties> map = new HashMap<String, CustomItemProperties>();
        map.putAll(CustomItems.makePotionImageProperties(rp, TYPE_POTION_NORMAL, Registry.ITEM.getKey(Items.POTION)));
        map.putAll(CustomItems.makePotionImageProperties(rp, TYPE_POTION_SPLASH, Registry.ITEM.getKey(Items.SPLASH_POTION)));
        map.putAll(CustomItems.makePotionImageProperties(rp, TYPE_POTION_LINGER, Registry.ITEM.getKey(Items.LINGERING_POTION)));
        return map;
    }

    private static Map<String, CustomItemProperties> makePotionImageProperties(IResourcePack rp, String type, ResourceLocation itemId) {
        HashMap<String, CustomItemProperties> map = new HashMap<String, CustomItemProperties>();
        String s = type + "/";
        String[] astring = new String[]{"optifine/cit/potion/" + s, "optifine/cit/Potion/" + s};
        String[] astring1 = new String[]{".png"};
        String[] astring2 = ResUtils.collectFiles(rp, astring, astring1);
        for (int i = 0; i < astring2.length; ++i) {
            String s1 = astring2[i];
            String name = StrUtils.removePrefixSuffix(s1, astring, astring1);
            Properties properties = CustomItems.makePotionProperties(name, type, itemId, s1);
            if (properties == null) continue;
            String s3 = StrUtils.removeSuffix(s1, astring1) + ".properties";
            CustomItemProperties customitemproperties = new CustomItemProperties(properties, s3);
            map.put(s3, customitemproperties);
        }
        return map;
    }

    private static Properties makePotionProperties(String name, String type, ResourceLocation itemId, String path) {
        if (StrUtils.endsWith(name, new String[]{"_n", "_s"})) {
            return null;
        }
        if (name.equals("empty") && type.equals(TYPE_POTION_NORMAL)) {
            itemId = Registry.ITEM.getKey(Items.GLASS_BOTTLE);
            PropertiesOrdered properties = new PropertiesOrdered();
            ((Properties)properties).put("type", "item");
            ((Properties)properties).put("items", itemId.toString());
            return properties;
        }
        int[] aint = (int[])CustomItems.getMapPotionIds().get(name);
        if (aint == null) {
            Config.warn("Potion not found for image: " + path);
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < aint.length; ++i) {
            int j = aint[i];
            if (type.equals(TYPE_POTION_SPLASH)) {
                j |= 0x4000;
            }
            if (i > 0) {
                stringbuffer.append(" ");
            }
            stringbuffer.append(j);
        }
        int k = 16447;
        if (name.equals("water") || name.equals("mundane")) {
            k |= 0x40;
        }
        PropertiesOrdered properties1 = new PropertiesOrdered();
        ((Properties)properties1).put("type", "item");
        ((Properties)properties1).put("items", itemId.toString());
        ((Properties)properties1).put("damage", stringbuffer.toString());
        ((Properties)properties1).put("damageMask", "" + k);
        if (type.equals(TYPE_POTION_SPLASH)) {
            ((Properties)properties1).put(KEY_TEXTURE_SPLASH, name);
        } else {
            ((Properties)properties1).put(KEY_TEXTURE_DRINKABLE, name);
        }
        return properties1;
    }

    private static Map getMapPotionIds() {
        if (mapPotionIds == null) {
            mapPotionIds = new LinkedHashMap();
            mapPotionIds.put("water", CustomItems.getPotionId(0, 0));
            mapPotionIds.put("awkward", CustomItems.getPotionId(0, 1));
            mapPotionIds.put("thick", CustomItems.getPotionId(0, 2));
            mapPotionIds.put("potent", CustomItems.getPotionId(0, 3));
            mapPotionIds.put("regeneration", CustomItems.getPotionIds(1));
            mapPotionIds.put("movespeed", CustomItems.getPotionIds(2));
            mapPotionIds.put("fireresistance", CustomItems.getPotionIds(3));
            mapPotionIds.put("poison", CustomItems.getPotionIds(4));
            mapPotionIds.put("heal", CustomItems.getPotionIds(5));
            mapPotionIds.put("nightvision", CustomItems.getPotionIds(6));
            mapPotionIds.put("clear", CustomItems.getPotionId(7, 0));
            mapPotionIds.put("bungling", CustomItems.getPotionId(7, 1));
            mapPotionIds.put("charming", CustomItems.getPotionId(7, 2));
            mapPotionIds.put("rank", CustomItems.getPotionId(7, 3));
            mapPotionIds.put("weakness", CustomItems.getPotionIds(8));
            mapPotionIds.put("damageboost", CustomItems.getPotionIds(9));
            mapPotionIds.put("moveslowdown", CustomItems.getPotionIds(10));
            mapPotionIds.put("leaping", CustomItems.getPotionIds(11));
            mapPotionIds.put("harm", CustomItems.getPotionIds(12));
            mapPotionIds.put("waterbreathing", CustomItems.getPotionIds(13));
            mapPotionIds.put("invisibility", CustomItems.getPotionIds(14));
            mapPotionIds.put("thin", CustomItems.getPotionId(15, 0));
            mapPotionIds.put("debonair", CustomItems.getPotionId(15, 1));
            mapPotionIds.put("sparkling", CustomItems.getPotionId(15, 2));
            mapPotionIds.put("stinky", CustomItems.getPotionId(15, 3));
            mapPotionIds.put("mundane", CustomItems.getPotionId(0, 4));
            mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
            mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
            mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
            mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
            mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
            mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
            mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
            mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
        }
        return mapPotionIds;
    }

    private static int[] getPotionIds(int baseId) {
        return new int[]{baseId, baseId + 16, baseId + 32, baseId + 48};
    }

    private static int[] getPotionId(int baseId, int subId) {
        return new int[]{baseId + subId * 16};
    }

    private static int getPotionNameDamage(String name) {
        String s = "effect." + name;
        for (ResourceLocation resourcelocation : Registry.EFFECTS.keySet()) {
            Effect effect;
            String s1;
            if (!Registry.EFFECTS.containsKey(resourcelocation) || !s.equals(s1 = (effect = Registry.EFFECTS.getOrDefault(resourcelocation)).getName())) continue;
            return Effect.getId(effect);
        }
        return -1;
    }

    private static List<List<CustomItemProperties>> makePropertyList(CustomItemProperties[][] propsArr) {
        ArrayList<List<CustomItemProperties>> list = new ArrayList<List<CustomItemProperties>>();
        if (propsArr != null) {
            for (int i = 0; i < propsArr.length; ++i) {
                CustomItemProperties[] acustomitemproperties = propsArr[i];
                ArrayList<CustomItemProperties> list1 = null;
                if (acustomitemproperties != null) {
                    list1 = new ArrayList<CustomItemProperties>(Arrays.asList(acustomitemproperties));
                }
                list.add(list1);
            }
        }
        return list;
    }

    private static CustomItemProperties[][] propertyListToArray(List list) {
        CustomItemProperties[][] acustomitemproperties = new CustomItemProperties[list.size()][];
        for (int i = 0; i < list.size(); ++i) {
            List lista = (List)list.get(i);
            if (lista == null) continue;
            CustomItemProperties[] acustomitemproperties1 = lista.toArray(new CustomItemProperties[lista.size()]);
            Arrays.sort(acustomitemproperties1, new CustomItemsComparator());
            acustomitemproperties[i] = acustomitemproperties1;
        }
        return acustomitemproperties;
    }

    private static void addToItemList(CustomItemProperties cp, List<List<CustomItemProperties>> itemList) {
        if (cp.items != null) {
            for (int i = 0; i < cp.items.length; ++i) {
                int j = cp.items[i];
                if (j <= 0) {
                    Config.warn("Invalid item ID: " + j);
                    continue;
                }
                CustomItems.addToList(cp, itemList, j);
            }
        }
    }

    private static void addToEnchantmentList(CustomItemProperties cp, List<List<CustomItemProperties>> enchantmentList) {
        if (cp.type == 2 && cp.enchantmentIds != null) {
            int i = CustomItems.getMaxEnchantmentId() + 1;
            for (int j = 0; j < i; ++j) {
                if (!Config.equalsOne(j, cp.enchantmentIds)) continue;
                CustomItems.addToList(cp, enchantmentList, j);
            }
        }
    }

    private static int getMaxEnchantmentId() {
        int i = 0;
        Enchantment enchantment;
        while ((enchantment = (Enchantment)Registry.ENCHANTMENT.getByValue(i)) != null) {
            ++i;
        }
        return i;
    }

    private static void addToList(CustomItemProperties cp, List<List<CustomItemProperties>> list, int id) {
        while (id >= list.size()) {
            list.add(null);
        }
        List<CustomItemProperties> lista = list.get(id);
        if (lista == null) {
            lista = new ArrayList<CustomItemProperties>();
            list.set(id, lista);
        }
        lista.add(cp);
    }

    public static IBakedModel getCustomItemModel(ItemStack itemStack, IBakedModel model, ResourceLocation modelLocation, boolean fullModel) {
        if (!fullModel && model.isGui3d()) {
            return model;
        }
        if (itemProperties == null) {
            return model;
        }
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(itemStack, 1);
        if (customitemproperties == null) {
            return model;
        }
        IBakedModel ibakedmodel = customitemproperties.getBakedModel(modelLocation, fullModel);
        return ibakedmodel != null ? ibakedmodel : model;
    }

    public static ResourceLocation getCustomArmorTexture(ItemStack itemStack, EquipmentSlotType slot, String overlay, ResourceLocation locArmor) {
        if (itemProperties == null) {
            return locArmor;
        }
        ResourceLocation resourcelocation = CustomItems.getCustomArmorLocation(itemStack, slot, overlay);
        return resourcelocation == null ? locArmor : resourcelocation;
    }

    private static ResourceLocation getCustomArmorLocation(ItemStack itemStack, EquipmentSlotType slot, String overlay) {
        String s1;
        ResourceLocation resourcelocation;
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(itemStack, 3);
        if (customitemproperties == null) {
            return null;
        }
        if (customitemproperties.mapTextureLocations == null) {
            return customitemproperties.textureLocation;
        }
        Item item = itemStack.getItem();
        if (!(item instanceof ArmorItem)) {
            return null;
        }
        ArmorItem armoritem = (ArmorItem)item;
        String s = armoritem.getArmorMaterial().getName();
        int i = slot == EquipmentSlotType.LEGS ? 2 : 1;
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("texture.");
        stringbuffer.append(s);
        stringbuffer.append("_layer_");
        stringbuffer.append(i);
        if (overlay != null) {
            stringbuffer.append("_");
            stringbuffer.append(overlay);
        }
        return (resourcelocation = (ResourceLocation)customitemproperties.mapTextureLocations.get(s1 = stringbuffer.toString())) == null ? customitemproperties.textureLocation : resourcelocation;
    }

    public static ResourceLocation getCustomElytraTexture(ItemStack itemStack, ResourceLocation locElytra) {
        if (itemProperties == null) {
            return locElytra;
        }
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(itemStack, 4);
        if (customitemproperties == null) {
            return locElytra;
        }
        return customitemproperties.textureLocation == null ? locElytra : customitemproperties.textureLocation;
    }

    private static CustomItemProperties getCustomItemProperties(ItemStack itemStack, int type) {
        CustomItemProperties[] acustomitemproperties1;
        CustomItemProperties[][] acustomitemproperties = itemProperties;
        if (acustomitemproperties == null) {
            return null;
        }
        if (itemStack == null) {
            return null;
        }
        Item item = itemStack.getItem();
        int i = Item.getIdFromItem(item);
        if (i >= 0 && i < acustomitemproperties.length && (acustomitemproperties1 = acustomitemproperties[i]) != null) {
            for (int j = 0; j < acustomitemproperties1.length; ++j) {
                CustomItemProperties customitemproperties = acustomitemproperties1[j];
                if (customitemproperties.type != type || !CustomItems.matchesProperties(customitemproperties, itemStack, null)) continue;
                return customitemproperties;
            }
        }
        return null;
    }

    private static boolean matchesProperties(CustomItemProperties cip, ItemStack itemStack, int[][] enchantmentIdLevels) {
        Item item = itemStack.getItem();
        if (cip.damage != null) {
            int i = CustomItems.getItemStackDamage(itemStack);
            if (i < 0) {
                return false;
            }
            if (cip.damageMask != 0) {
                i &= cip.damageMask;
            }
            if (cip.damagePercent) {
                int j = item.getMaxDamage();
                i = (int)((double)(i * 100) / (double)j);
            }
            if (!cip.damage.isInRange(i)) {
                return false;
            }
        }
        if (cip.stackSize != null && !cip.stackSize.isInRange(itemStack.getCount())) {
            return false;
        }
        int[][] aint = enchantmentIdLevels;
        if (cip.enchantmentIds != null) {
            if (enchantmentIdLevels == null) {
                aint = CustomItems.getEnchantmentIdLevels(itemStack);
            }
            boolean flag = false;
            for (int k = 0; k < aint.length; ++k) {
                int l = aint[k][0];
                if (!Config.equalsOne(l, cip.enchantmentIds)) continue;
                flag = true;
                break;
            }
            if (!flag) {
                return false;
            }
        }
        if (cip.enchantmentLevels != null) {
            if (aint == null) {
                aint = CustomItems.getEnchantmentIdLevels(itemStack);
            }
            boolean flag1 = false;
            for (int i1 = 0; i1 < aint.length; ++i1) {
                int k1 = aint[i1][1];
                if (!cip.enchantmentLevels.isInRange(k1)) continue;
                flag1 = true;
                break;
            }
            if (!flag1) {
                return false;
            }
        }
        if (cip.nbtTagValues != null) {
            CompoundNBT compoundnbt = itemStack.getTag();
            for (int j1 = 0; j1 < cip.nbtTagValues.length; ++j1) {
                NbtTagValue nbttagvalue = cip.nbtTagValues[j1];
                if (nbttagvalue.matches(compoundnbt)) continue;
                return false;
            }
        }
        if (cip.hand != 0) {
            if (cip.hand == 1 && renderOffHand) {
                return false;
            }
            if (cip.hand == 2 && !renderOffHand) {
                return false;
            }
        }
        return true;
    }

    private static int getItemStackDamage(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item instanceof PotionItem ? CustomItems.getPotionDamage(itemStack) : itemStack.getDamage();
    }

    private static int getPotionDamage(ItemStack itemStack) {
        CompoundNBT compoundnbt = itemStack.getTag();
        if (compoundnbt == null) {
            return 0;
        }
        String s = compoundnbt.getString("Potion");
        if (s != null && !s.equals("")) {
            Integer integer = mapPotionDamages.get(s);
            if (integer == null) {
                return -1;
            }
            int i = integer;
            if (itemStack.getItem() == Items.SPLASH_POTION) {
                i |= 0x4000;
            }
            return i;
        }
        return 0;
    }

    private static Map<String, Integer> makeMapPotionDamages() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        CustomItems.addPotion("water", 0, false, map);
        CustomItems.addPotion("awkward", 16, false, map);
        CustomItems.addPotion("thick", 32, false, map);
        CustomItems.addPotion("mundane", 64, false, map);
        CustomItems.addPotion("regeneration", 1, true, map);
        CustomItems.addPotion("swiftness", 2, true, map);
        CustomItems.addPotion("fire_resistance", 3, true, map);
        CustomItems.addPotion("poison", 4, true, map);
        CustomItems.addPotion("healing", 5, true, map);
        CustomItems.addPotion("night_vision", 6, true, map);
        CustomItems.addPotion("weakness", 8, true, map);
        CustomItems.addPotion("strength", 9, true, map);
        CustomItems.addPotion("slowness", 10, true, map);
        CustomItems.addPotion("leaping", 11, true, map);
        CustomItems.addPotion("harming", 12, true, map);
        CustomItems.addPotion("water_breathing", 13, true, map);
        CustomItems.addPotion("invisibility", 14, true, map);
        return map;
    }

    private static void addPotion(String name, int value, boolean extended, Map<String, Integer> map) {
        if (extended) {
            value |= 0x2000;
        }
        map.put("minecraft:" + name, value);
        if (extended) {
            int i = value | 0x20;
            map.put("minecraft:strong_" + name, i);
            int j = value | 0x40;
            map.put("minecraft:long_" + name, j);
        }
    }

    private static int[][] getEnchantmentIdLevels(ItemStack itemStack) {
        ListNBT listnbt1;
        Item item = itemStack.getItem();
        if (item == Items.ENCHANTED_BOOK) {
            EnchantedBookItem enchantedbookitem = (EnchantedBookItem)Items.ENCHANTED_BOOK;
            listnbt1 = EnchantedBookItem.getEnchantments(itemStack);
        } else {
            listnbt1 = itemStack.getEnchantmentTagList();
        }
        ListNBT listnbt = listnbt1;
        if (listnbt != null && listnbt.size() > 0) {
            int[][] aint = new int[listnbt.size()][2];
            for (int i = 0; i < listnbt.size(); ++i) {
                int k;
                CompoundNBT compoundnbt = listnbt.getCompound(i);
                String s = compoundnbt.getString("id");
                int j = compoundnbt.getInt("lvl");
                Enchantment enchantment = EnchantmentUtils.getEnchantment(s);
                if (enchantment == null) continue;
                aint[i][0] = k = Registry.ENCHANTMENT.getId(enchantment);
                aint[i][1] = j;
            }
            return aint;
        }
        return EMPTY_INT2_ARRAY;
    }

    public static boolean renderCustomEffect(ItemRenderer renderItem, ItemStack itemStack, IBakedModel model) {
        CustomItemProperties[][] acustomitemproperties = enchantmentProperties;
        if (acustomitemproperties == null) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(itemStack);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        for (int i = 0; i < aint.length; ++i) {
            CustomItemProperties[] acustomitemproperties1;
            int j = aint[i][0];
            if (j < 0 || j >= acustomitemproperties.length || (acustomitemproperties1 = acustomitemproperties[j]) == null) continue;
            for (int k = 0; k < acustomitemproperties1.length; ++k) {
                CustomItemProperties customitemproperties = acustomitemproperties1[k];
                if (set == null) {
                    set = new HashSet<Integer>();
                }
                if (!set.add(j) || !CustomItems.matchesProperties(customitemproperties, itemStack, aint) || customitemproperties.textureLocation == null) continue;
                texturemanager.bindTexture(customitemproperties.textureLocation);
                float f = customitemproperties.getTextureWidth(texturemanager);
                if (!flag) {
                    flag = true;
                    GlStateManager.depthMask(false);
                    GlStateManager.depthFunc(514);
                    GlStateManager.disableLighting();
                    GlStateManager.matrixMode(5890);
                }
                Blender.setupBlend(customitemproperties.blend, 1.0f);
                GlStateManager.pushMatrix();
                GlStateManager.scalef(f, f, f);
                float f1 = customitemproperties.speed * (float)(Util.milliTime() % 3000L) / 3000.0f / 8.0f;
                GlStateManager.translatef(f1, 0.0f, 0.0f);
                GlStateManager.rotatef(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                GlStateManager.popMatrix();
            }
        }
        if (flag) {
            GlStateManager.enableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            texturemanager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        }
        return flag;
    }

    public static boolean renderCustomArmorEffect(LivingEntity entity, ItemStack itemStack, EntityModel model, float limbSwing, float prevLimbSwing, float partialTicks, float timeLimbSwing, float yaw, float pitch, float scale) {
        CustomItemProperties[][] acustomitemproperties = enchantmentProperties;
        if (acustomitemproperties == null) {
            return false;
        }
        if (Config.isShaders() && Shaders.isShadowPass) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(itemStack);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        for (int i = 0; i < aint.length; ++i) {
            CustomItemProperties[] acustomitemproperties1;
            int j = aint[i][0];
            if (j < 0 || j >= acustomitemproperties.length || (acustomitemproperties1 = acustomitemproperties[j]) == null) continue;
            for (int k = 0; k < acustomitemproperties1.length; ++k) {
                CustomItemProperties customitemproperties = acustomitemproperties1[k];
                if (set == null) {
                    set = new HashSet<Integer>();
                }
                if (!set.add(j) || !CustomItems.matchesProperties(customitemproperties, itemStack, aint) || customitemproperties.textureLocation == null) continue;
                texturemanager.bindTexture(customitemproperties.textureLocation);
                float f = customitemproperties.getTextureWidth(texturemanager);
                if (!flag) {
                    flag = true;
                    if (Config.isShaders()) {
                        ShadersRender.renderEnchantedGlintBegin();
                    }
                    GlStateManager.enableBlend();
                    GlStateManager.depthFunc(514);
                    GlStateManager.depthMask(false);
                }
                Blender.setupBlend(customitemproperties.blend, 1.0f);
                GlStateManager.disableLighting();
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.rotatef(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                float f1 = f / 8.0f;
                GlStateManager.scalef(f1, f1 / 2.0f, f1);
                float f2 = customitemproperties.speed * (float)(Util.milliTime() % 3000L) / 3000.0f / 8.0f;
                GlStateManager.translatef(0.0f, f2, 0.0f);
                GlStateManager.matrixMode(5888);
            }
        }
        if (flag) {
            GlStateManager.enableAlphaTest();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }
        return flag;
    }

    public static boolean isUseGlint() {
        return useGlint;
    }

    public static void setRenderOffHand(boolean renderOffHand) {
        CustomItems.renderOffHand = renderOffHand;
    }
}
