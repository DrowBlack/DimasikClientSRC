package net.optifine.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.optifine.Config;

public class EntityUtils {
    private static final Map<EntityType, Integer> mapIdByType = new HashMap<EntityType, Integer>();
    private static final Map<String, Integer> mapIdByLocation = new HashMap<String, Integer>();
    private static final Map<String, Integer> mapIdByName = new HashMap<String, Integer>();

    public static int getEntityIdByClass(Entity entity) {
        return entity == null ? -1 : EntityUtils.getEntityIdByType(entity.getType());
    }

    public static int getEntityIdByType(EntityType type) {
        Integer integer = mapIdByType.get(type);
        return integer == null ? -1 : integer;
    }

    public static int getEntityIdByLocation(String locStr) {
        Integer integer = mapIdByLocation.get(locStr);
        return integer == null ? -1 : integer;
    }

    public static int getEntityIdByName(String name) {
        Integer integer = mapIdByName.get(name = name.toLowerCase(Locale.ROOT));
        return integer == null ? -1 : integer;
    }

    static {
        for (EntityType entityType : Registry.ENTITY_TYPE) {
            int i = Registry.ENTITY_TYPE.getId(entityType);
            ResourceLocation resourcelocation = Registry.ENTITY_TYPE.getKey(entityType);
            String s = resourcelocation.toString();
            String s1 = resourcelocation.getPath();
            if (mapIdByType.containsKey(entityType)) {
                Config.warn("Duplicate entity type: " + String.valueOf(entityType) + ", id1: " + String.valueOf(mapIdByType.get(entityType)) + ", id2: " + i);
            }
            if (mapIdByLocation.containsKey(s)) {
                Config.warn("Duplicate entity location: " + s + ", id1: " + String.valueOf(mapIdByLocation.get(s)) + ", id2: " + i);
            }
            if (mapIdByName.containsKey(s)) {
                Config.warn("Duplicate entity name: " + s1 + ", id1: " + String.valueOf(mapIdByName.get(s1)) + ", id2: " + i);
            }
            mapIdByType.put(entityType, i);
            mapIdByLocation.put(s, i);
            mapIdByName.put(s1, i);
        }
    }
}
