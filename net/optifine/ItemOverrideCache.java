package net.optifine;

import com.google.common.primitives.Floats;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.renderer.model.ItemOverride;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.ItemOverrideProperty;
import net.optifine.reflect.Reflector;
import net.optifine.util.CompoundKey;

public class ItemOverrideCache {
    private ItemOverrideProperty[] itemOverrideProperties;
    private Map<CompoundKey, Integer> mapModelIndexes = new HashMap<CompoundKey, Integer>();
    public static final Integer INDEX_NONE = new Integer(-1);

    public ItemOverrideCache(ItemOverrideProperty[] itemOverrideProperties) {
        this.itemOverrideProperties = itemOverrideProperties;
    }

    public Integer getModelIndex(ItemStack stack, ClientWorld world, LivingEntity entity) {
        CompoundKey compoundkey = this.getValueKey(stack, world, entity);
        return compoundkey == null ? null : this.mapModelIndexes.get(compoundkey);
    }

    public void putModelIndex(ItemStack stack, ClientWorld world, LivingEntity entity, Integer index) {
        CompoundKey compoundkey = this.getValueKey(stack, world, entity);
        if (compoundkey != null) {
            this.mapModelIndexes.put(compoundkey, index);
        }
    }

    private CompoundKey getValueKey(ItemStack stack, ClientWorld world, LivingEntity entity) {
        Object[] ainteger = new Integer[this.itemOverrideProperties.length];
        for (int i = 0; i < ainteger.length; ++i) {
            Integer integer = this.itemOverrideProperties[i].getValueIndex(stack, world, entity);
            if (integer == null) {
                return null;
            }
            ainteger[i] = integer;
        }
        return new CompoundKey(ainteger);
    }

    public static ItemOverrideCache make(List<ItemOverride> overrides) {
        if (overrides.isEmpty()) {
            return null;
        }
        if (!Reflector.ItemOverride_mapResourceValues.exists()) {
            return null;
        }
        LinkedHashMap<ResourceLocation, HashSet<Float>> map = new LinkedHashMap<ResourceLocation, HashSet<Float>>();
        for (ItemOverride itemOverride : overrides) {
            Map map1 = (Map)Reflector.getFieldValue(itemOverride, Reflector.ItemOverride_mapResourceValues);
            for (ResourceLocation resourcelocation : map1.keySet()) {
                Float f = (Float)map1.get(resourcelocation);
                if (f == null) continue;
                HashSet<Float> set = (HashSet<Float>)map.get(resourcelocation);
                if (set == null) {
                    set = new HashSet<Float>();
                    map.put(resourcelocation, set);
                }
                set.add(f);
            }
        }
        ArrayList<ItemOverrideProperty> list = new ArrayList<ItemOverrideProperty>();
        for (ResourceLocation resourcelocation1 : map.keySet()) {
            Set set1 = (Set)map.get(resourcelocation1);
            float[] afloat = Floats.toArray(set1);
            ItemOverrideProperty itemoverrideproperty = new ItemOverrideProperty(resourcelocation1, afloat);
            list.add(itemoverrideproperty);
        }
        ItemOverrideProperty[] itemOverridePropertyArray = list.toArray(new ItemOverrideProperty[list.size()]);
        ItemOverrideCache itemoverridecache = new ItemOverrideCache(itemOverridePropertyArray);
        ItemOverrideCache.logCache(itemOverridePropertyArray, overrides);
        return itemoverridecache;
    }

    private static void logCache(ItemOverrideProperty[] props, List<ItemOverride> overrides) {
        StringBuffer stringbuffer = new StringBuffer();
        for (int i = 0; i < props.length; ++i) {
            ItemOverrideProperty itemoverrideproperty = props[i];
            if (stringbuffer.length() > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(String.valueOf(itemoverrideproperty.getLocation()) + "=" + itemoverrideproperty.getValues().length);
        }
        if (overrides.size() > 0) {
            stringbuffer.append(" -> " + String.valueOf(overrides.get(0).getLocation()) + " ...");
        }
        Config.dbg("ItemOverrideCache: " + stringbuffer.toString());
    }

    public String toString() {
        return "properties: " + this.itemOverrideProperties.length + ", modelIndexes: " + this.mapModelIndexes.size();
    }
}
