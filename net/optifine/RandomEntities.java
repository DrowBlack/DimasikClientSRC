package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ShoulderRidingEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.IRandomEntity;
import net.optifine.RandomEntity;
import net.optifine.RandomEntityProperties;
import net.optifine.RandomTileEntity;
import net.optifine.reflect.ReflectorRaw;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;

public class RandomEntities {
    private static Map<String, RandomEntityProperties> mapProperties = new HashMap<String, RandomEntityProperties>();
    private static boolean active = false;
    private static WorldRenderer renderGlobal;
    private static RandomEntity randomEntity;
    private static TileEntityRendererDispatcher tileEntityRendererDispatcher;
    private static RandomTileEntity randomTileEntity;
    private static boolean working;
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_PROPERTIES = ".properties";
    public static final String PREFIX_TEXTURES_ENTITY = "textures/entity/";
    public static final String PREFIX_TEXTURES_PAINTING = "textures/painting/";
    public static final String PREFIX_TEXTURES = "textures/";
    public static final String PREFIX_OPTIFINE_RANDOM = "optifine/random/";
    public static final String PREFIX_OPTIFINE_MOB = "optifine/mob/";
    private static final String[] DEPENDANT_SUFFIXES;
    private static final String PREFIX_DYNAMIC_TEXTURE_HORSE = "horse/";
    private static final String[] HORSE_TEXTURES;
    private static final String[] HORSE_TEXTURES_ABBR;

    public static void entityLoaded(Entity entity, World world) {
        if (world != null) {
            EntityDataManager entitydatamanager = entity.getDataManager();
            entitydatamanager.spawnPosition = entity.getPosition();
            entitydatamanager.spawnBiome = world.getBiome(entitydatamanager.spawnPosition);
            if (entity instanceof ShoulderRidingEntity) {
                ShoulderRidingEntity shoulderridingentity = (ShoulderRidingEntity)entity;
                RandomEntities.checkEntityShoulder(shoulderridingentity, false);
            }
        }
    }

    public static void entityUnloaded(Entity entity, World world) {
        if (entity instanceof ShoulderRidingEntity) {
            ShoulderRidingEntity shoulderridingentity = (ShoulderRidingEntity)entity;
            RandomEntities.checkEntityShoulder(shoulderridingentity, true);
        }
    }

    private static void checkEntityShoulder(ShoulderRidingEntity entity, boolean attach) {
        LivingEntity livingentity = entity.getOwner();
        if (livingentity == null) {
            livingentity = Config.getMinecraft().player;
        }
        if (livingentity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)livingentity;
            UUID uuid = entity.getUniqueID();
            if (attach) {
                CompoundNBT compoundnbt1;
                CompoundNBT compoundnbt = abstractclientplayerentity.getLeftShoulderEntity();
                if (compoundnbt != null && compoundnbt.contains("UUID") && Config.equals(compoundnbt.getUniqueId("UUID"), uuid)) {
                    abstractclientplayerentity.entityShoulderLeft = entity;
                }
                if ((compoundnbt1 = abstractclientplayerentity.getRightShoulderEntity()) != null && compoundnbt1.contains("UUID") && Config.equals(compoundnbt1.getUniqueId("UUID"), uuid)) {
                    abstractclientplayerentity.entityShoulderRight = entity;
                }
            } else {
                EntityDataManager entitydatamanager = entity.getDataManager();
                if (abstractclientplayerentity.entityShoulderLeft != null && Config.equals(abstractclientplayerentity.entityShoulderLeft.getUniqueID(), uuid)) {
                    EntityDataManager entitydatamanager1 = abstractclientplayerentity.entityShoulderLeft.getDataManager();
                    entitydatamanager.spawnPosition = entitydatamanager1.spawnPosition;
                    entitydatamanager.spawnBiome = entitydatamanager1.spawnBiome;
                    abstractclientplayerentity.entityShoulderLeft = null;
                }
                if (abstractclientplayerentity.entityShoulderRight != null && Config.equals(abstractclientplayerentity.entityShoulderRight.getUniqueID(), uuid)) {
                    EntityDataManager entitydatamanager2 = abstractclientplayerentity.entityShoulderRight.getDataManager();
                    entitydatamanager.spawnPosition = entitydatamanager2.spawnPosition;
                    entitydatamanager.spawnBiome = entitydatamanager2.spawnBiome;
                    abstractclientplayerentity.entityShoulderRight = null;
                }
            }
        }
    }

    public static void worldChanged(World oldWorld, World newWorld) {
        if (newWorld instanceof ClientWorld) {
            ClientWorld clientworld = (ClientWorld)newWorld;
            for (Entity entity : clientworld.getAllEntities()) {
                RandomEntities.entityLoaded(entity, newWorld);
            }
        }
        randomEntity.setEntity(null);
        randomTileEntity.setTileEntity(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ResourceLocation getTextureLocation(ResourceLocation loc) {
        ResourceLocation name;
        if (!active) {
            return loc;
        }
        if (working) {
            return loc;
        }
        try {
            working = true;
            IRandomEntity irandomentity = RandomEntities.getRandomEntityRendered();
            if (irandomentity != null) {
                String s = loc.getPath();
                if (s.startsWith(PREFIX_DYNAMIC_TEXTURE_HORSE)) {
                    s = RandomEntities.getHorseTexturePath(s, PREFIX_DYNAMIC_TEXTURE_HORSE.length());
                }
                if (!s.startsWith(PREFIX_TEXTURES_ENTITY) && !s.startsWith(PREFIX_TEXTURES_PAINTING)) {
                    ResourceLocation resourceLocation = loc;
                    return resourceLocation;
                }
                RandomEntityProperties randomentityproperties = mapProperties.get(s);
                if (randomentityproperties == null) {
                    ResourceLocation resourceLocation = loc;
                    return resourceLocation;
                }
                ResourceLocation resourceLocation = randomentityproperties.getTextureLocation(loc, irandomentity);
                return resourceLocation;
            }
            name = loc;
        }
        finally {
            working = false;
        }
        return name;
    }

    private static String getHorseTexturePath(String path, int pos) {
        if (HORSE_TEXTURES != null && HORSE_TEXTURES_ABBR != null) {
            for (int i = 0; i < HORSE_TEXTURES_ABBR.length; ++i) {
                String s = HORSE_TEXTURES_ABBR[i];
                if (!path.startsWith(s, pos)) continue;
                return HORSE_TEXTURES[i];
            }
            return path;
        }
        return path;
    }

    public static IRandomEntity getRandomEntityRendered() {
        if (RandomEntities.renderGlobal.renderedEntity != null) {
            randomEntity.setEntity(RandomEntities.renderGlobal.renderedEntity);
            return randomEntity;
        }
        TileEntityRendererDispatcher tileentityrendererdispatcher = tileEntityRendererDispatcher;
        if (TileEntityRendererDispatcher.tileEntityRendered != null) {
            tileentityrendererdispatcher = tileEntityRendererDispatcher;
            TileEntity tileentity = TileEntityRendererDispatcher.tileEntityRendered;
            if (tileentity.getWorld() != null) {
                randomTileEntity.setTileEntity(tileentity);
                return randomTileEntity;
            }
        }
        return null;
    }

    private static RandomEntityProperties makeProperties(ResourceLocation loc, boolean optifine) {
        RandomEntityProperties randomentityproperties;
        String s = loc.getPath();
        ResourceLocation resourcelocation = RandomEntities.getLocationProperties(loc, optifine);
        if (resourcelocation != null && (randomentityproperties = RandomEntities.parseProperties(resourcelocation, loc)) != null) {
            return randomentityproperties;
        }
        ResourceLocation[] aresourcelocation = RandomEntities.getLocationsVariants(loc, optifine);
        return aresourcelocation == null ? null : new RandomEntityProperties(s, aresourcelocation);
    }

    private static RandomEntityProperties parseProperties(ResourceLocation propLoc, ResourceLocation resLoc) {
        try {
            String s = propLoc.getPath();
            RandomEntities.dbg(resLoc.getPath() + ", properties: " + s);
            InputStream inputstream = Config.getResourceStream(propLoc);
            if (inputstream == null) {
                RandomEntities.warn("Properties not found: " + s);
                return null;
            }
            PropertiesOrdered properties = new PropertiesOrdered();
            properties.load(inputstream);
            inputstream.close();
            RandomEntityProperties randomentityproperties = new RandomEntityProperties(properties, s, resLoc);
            return !randomentityproperties.isValid(s) ? null : randomentityproperties;
        }
        catch (FileNotFoundException filenotfoundexception) {
            RandomEntities.warn("File not found: " + resLoc.getPath());
            return null;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
            return null;
        }
    }

    private static ResourceLocation getLocationProperties(ResourceLocation loc, boolean optifine) {
        String s1;
        String s2;
        String s3;
        ResourceLocation resourcelocation = RandomEntities.getLocationRandom(loc, optifine);
        if (resourcelocation == null) {
            return null;
        }
        String s = resourcelocation.getNamespace();
        ResourceLocation resourcelocation1 = new ResourceLocation(s, s3 = (s2 = StrUtils.removeSuffix(s1 = resourcelocation.getPath(), SUFFIX_PNG)) + SUFFIX_PROPERTIES);
        if (Config.hasResource(resourcelocation1)) {
            return resourcelocation1;
        }
        String s4 = RandomEntities.getParentTexturePath(s2);
        if (s4 == null) {
            return null;
        }
        ResourceLocation resourcelocation2 = new ResourceLocation(s, s4 + SUFFIX_PROPERTIES);
        return Config.hasResource(resourcelocation2) ? resourcelocation2 : null;
    }

    protected static ResourceLocation getLocationRandom(ResourceLocation loc, boolean optifine) {
        String s = loc.getNamespace();
        String s1 = loc.getPath();
        String s2 = PREFIX_TEXTURES;
        String s3 = PREFIX_OPTIFINE_RANDOM;
        if (optifine) {
            s2 = PREFIX_TEXTURES_ENTITY;
            s3 = PREFIX_OPTIFINE_MOB;
        }
        if (!s1.startsWith(s2)) {
            return null;
        }
        String s4 = StrUtils.replacePrefix(s1, s2, s3);
        return new ResourceLocation(s, s4);
    }

    private static String getPathBase(String pathRandom) {
        if (pathRandom.startsWith(PREFIX_OPTIFINE_RANDOM)) {
            return StrUtils.replacePrefix(pathRandom, PREFIX_OPTIFINE_RANDOM, PREFIX_TEXTURES);
        }
        return pathRandom.startsWith(PREFIX_OPTIFINE_MOB) ? StrUtils.replacePrefix(pathRandom, PREFIX_OPTIFINE_MOB, PREFIX_TEXTURES_ENTITY) : null;
    }

    protected static ResourceLocation getLocationIndexed(ResourceLocation loc, int index) {
        if (loc == null) {
            return null;
        }
        String s = loc.getPath();
        int i = s.lastIndexOf(46);
        if (i < 0) {
            return null;
        }
        String s1 = s.substring(0, i);
        String s2 = s.substring(i);
        String s3 = s1 + index + s2;
        return new ResourceLocation(loc.getNamespace(), s3);
    }

    private static String getParentTexturePath(String path) {
        for (int i = 0; i < DEPENDANT_SUFFIXES.length; ++i) {
            String s = DEPENDANT_SUFFIXES[i];
            if (!path.endsWith(s)) continue;
            return StrUtils.removeSuffix(path, s);
        }
        return null;
    }

    private static ResourceLocation[] getLocationsVariants(ResourceLocation loc, boolean optifine) {
        ArrayList<ResourceLocation> list = new ArrayList<ResourceLocation>();
        list.add(loc);
        ResourceLocation resourcelocation = RandomEntities.getLocationRandom(loc, optifine);
        if (resourcelocation == null) {
            return null;
        }
        for (int i = 1; i < list.size() + 10; ++i) {
            int j = i + 1;
            ResourceLocation resourcelocation1 = RandomEntities.getLocationIndexed(resourcelocation, j);
            if (!Config.hasResource(resourcelocation1)) continue;
            list.add(resourcelocation1);
        }
        if (list.size() <= 1) {
            return null;
        }
        ResourceLocation[] aresourcelocation = list.toArray(new ResourceLocation[list.size()]);
        RandomEntities.dbg(loc.getPath() + ", variants: " + aresourcelocation.length);
        return aresourcelocation;
    }

    public static void update() {
        mapProperties.clear();
        active = false;
        if (Config.isRandomEntities()) {
            RandomEntities.initialize();
        }
    }

    private static void initialize() {
        renderGlobal = Config.getRenderGlobal();
        tileEntityRendererDispatcher = TileEntityRendererDispatcher.instance;
        String[] astring = new String[]{PREFIX_OPTIFINE_RANDOM, PREFIX_OPTIFINE_MOB};
        String[] astring1 = new String[]{SUFFIX_PNG, SUFFIX_PROPERTIES};
        String[] astring2 = ResUtils.collectFiles(astring, astring1);
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < astring2.length; ++i) {
            RandomEntityProperties randomentityproperties;
            Object s = astring2[i];
            s = StrUtils.removeSuffix((String)s, astring1);
            s = StrUtils.trimTrailing((String)s, "0123456789");
            String s1 = RandomEntities.getPathBase((String)(s = (String)s + SUFFIX_PNG));
            if (set.contains(s1)) continue;
            set.add(s1);
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            if (!Config.hasResource(resourcelocation) || (randomentityproperties = mapProperties.get(s1)) != null) continue;
            randomentityproperties = RandomEntities.makeProperties(resourcelocation, false);
            if (randomentityproperties == null) {
                randomentityproperties = RandomEntities.makeProperties(resourcelocation, true);
            }
            if (randomentityproperties == null) continue;
            mapProperties.put(s1, randomentityproperties);
        }
        active = !mapProperties.isEmpty();
    }

    public static void dbg(String str) {
        Config.dbg("RandomEntities: " + str);
    }

    public static void warn(String str) {
        Config.warn("RandomEntities: " + str);
    }

    static {
        randomEntity = new RandomEntity();
        randomTileEntity = new RandomTileEntity();
        working = false;
        DEPENDANT_SUFFIXES = new String[]{"_armor", "_eyes", "_exploding", "_shooting", "_fur", "_eyes", "_invulnerable", "_angry", "_tame", "_collar"};
        HORSE_TEXTURES = (String[])ReflectorRaw.getFieldValue(null, HorseEntity.class, String[].class, 0);
        HORSE_TEXTURES_ABBR = (String[])ReflectorRaw.getFieldValue(null, HorseEntity.class, String[].class, 1);
    }
}
