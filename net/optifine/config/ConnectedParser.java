package net.optifine.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.optifine.Config;
import net.optifine.ConnectedProperties;
import net.optifine.config.BiomeId;
import net.optifine.config.INameGetter;
import net.optifine.config.MatchBlock;
import net.optifine.config.MatchProfession;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeInt;
import net.optifine.config.RangeListInt;
import net.optifine.config.Weather;
import net.optifine.util.BiomeUtils;
import net.optifine.util.BlockUtils;
import net.optifine.util.EntityTypeUtils;
import net.optifine.util.ItemUtils;

public class ConnectedParser {
    private String context = null;
    public static final MatchProfession[] PROFESSIONS_INVALID = new MatchProfession[0];
    public static final DyeColor[] DYE_COLORS_INVALID = new DyeColor[0];
    private static Map<ResourceLocation, BiomeId> MAP_BIOMES_COMPACT = null;
    private static final INameGetter<Enum> NAME_GETTER_ENUM = new INameGetter<Enum>(){

        @Override
        public String getName(Enum en) {
            return en.name();
        }
    };
    private static final INameGetter<DyeColor> NAME_GETTER_DYE_COLOR = new INameGetter<DyeColor>(){

        @Override
        public String getName(DyeColor col) {
            return col.getString();
        }
    };

    public ConnectedParser(String context) {
        this.context = context;
    }

    public String parseName(String path) {
        int j;
        String s = path;
        int i = path.lastIndexOf(47);
        if (i >= 0) {
            s = path.substring(i + 1);
        }
        if ((j = s.lastIndexOf(46)) >= 0) {
            s = s.substring(0, j);
        }
        return s;
    }

    public String parseBasePath(String path) {
        int i = path.lastIndexOf(47);
        return i < 0 ? "" : path.substring(0, i);
    }

    public MatchBlock[] parseMatchBlocks(String propMatchBlocks) {
        if (propMatchBlocks == null) {
            return null;
        }
        ArrayList<MatchBlock> list = new ArrayList<MatchBlock>();
        String[] astring = Config.tokenize(propMatchBlocks, " ");
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            MatchBlock[] amatchblock = this.parseMatchBlock(s);
            if (amatchblock == null) continue;
            list.addAll(Arrays.asList(amatchblock));
        }
        return list.toArray(new MatchBlock[list.size()]);
    }

    public BlockState parseBlockState(String str, BlockState def) {
        MatchBlock[] amatchblock = this.parseMatchBlock(str);
        if (amatchblock == null) {
            return def;
        }
        if (amatchblock.length != 1) {
            return def;
        }
        MatchBlock matchblock = amatchblock[0];
        int i = matchblock.getBlockId();
        Block block = Registry.BLOCK.getByValue(i);
        return block.getDefaultState();
    }

    public MatchBlock[] parseMatchBlock(String blockStr) {
        if (blockStr == null) {
            return null;
        }
        if ((blockStr = blockStr.trim()).length() <= 0) {
            return null;
        }
        String[] astring = Config.tokenize(blockStr, ":");
        String s = "minecraft";
        int i = 0;
        if (astring.length > 1 && this.isFullBlockName(astring)) {
            s = astring[0];
            i = 1;
        } else {
            s = "minecraft";
            i = 0;
        }
        String s1 = astring[i];
        String[] astring1 = Arrays.copyOfRange(astring, i + 1, astring.length);
        Block[] ablock = this.parseBlockPart(s, s1);
        if (ablock == null) {
            return null;
        }
        MatchBlock[] amatchblock = new MatchBlock[ablock.length];
        for (int j = 0; j < ablock.length; ++j) {
            MatchBlock matchblock;
            Block block = ablock[j];
            int k = Registry.BLOCK.getId(block);
            int[] aint = null;
            if (astring1.length > 0 && (aint = this.parseBlockMetadatas(block, astring1)) == null) {
                return null;
            }
            amatchblock[j] = matchblock = new MatchBlock(k, aint);
        }
        return amatchblock;
    }

    public boolean isFullBlockName(String[] parts) {
        if (parts.length <= 1) {
            return false;
        }
        String s = parts[1];
        if (s.length() < 1) {
            return false;
        }
        return !s.contains("=");
    }

    public boolean startsWithDigit(String str) {
        if (str == null) {
            return false;
        }
        if (str.length() < 1) {
            return false;
        }
        char c0 = str.charAt(0);
        return Character.isDigit(c0);
    }

    public Block[] parseBlockPart(String domain, String blockPart) {
        String s = domain + ":" + blockPart;
        ResourceLocation resourcelocation = new ResourceLocation(s);
        Block block = BlockUtils.getBlock(resourcelocation);
        if (block == null) {
            this.warn("Block not found for name: " + s);
            return null;
        }
        return new Block[]{block};
    }

    public int[] parseBlockMetadatas(Block block, String[] params) {
        if (params.length <= 0) {
            return null;
        }
        BlockState blockstate = block.getDefaultState();
        Collection<Property> collection = blockstate.getProperties();
        HashMap<Property, List<Comparable>> map = new HashMap<Property, List<Comparable>>();
        for (int i = 0; i < params.length; ++i) {
            String s = params[i];
            if (s.length() <= 0) continue;
            String[] astring = Config.tokenize(s, "=");
            if (astring.length != 2) {
                this.warn("Invalid block property: " + s);
                return null;
            }
            String s1 = astring[0];
            String s2 = astring[1];
            Property property = ConnectedProperties.getProperty(s1, collection);
            if (property == null) {
                this.warn("Property not found: " + s1 + ", block: " + String.valueOf(block));
                return null;
            }
            ArrayList<Comparable> list = (ArrayList<Comparable>)map.get(s1);
            if (list == null) {
                list = new ArrayList<Comparable>();
                map.put(property, list);
            }
            String[] astring1 = Config.tokenize(s2, ",");
            for (int j = 0; j < astring1.length; ++j) {
                String s3 = astring1[j];
                Comparable comparable = ConnectedParser.parsePropertyValue(property, s3);
                if (comparable == null) {
                    this.warn("Property value not found: " + s3 + ", property: " + s1 + ", block: " + String.valueOf(block));
                    return null;
                }
                list.add(comparable);
            }
        }
        if (map.isEmpty()) {
            return null;
        }
        ArrayList<Integer> list1 = new ArrayList<Integer>();
        int k = BlockUtils.getMetadataCount(block);
        for (int l = 0; l < k; ++l) {
            try {
                BlockState blockstate1 = BlockUtils.getBlockState(block, l);
                if (!this.matchState(blockstate1, map)) continue;
                list1.add(l);
                continue;
            }
            catch (IllegalArgumentException blockstate1) {
                // empty catch block
            }
        }
        if (list1.size() == k) {
            return null;
        }
        int[] aint = new int[list1.size()];
        for (int i1 = 0; i1 < aint.length; ++i1) {
            aint[i1] = (Integer)list1.get(i1);
        }
        return aint;
    }

    public static Comparable parsePropertyValue(Property prop, String valStr) {
        Class oclass = prop.getValueClass();
        Comparable comparable = ConnectedParser.parseValue(valStr, oclass);
        if (comparable == null) {
            Collection collection = prop.getAllowedValues();
            comparable = ConnectedParser.getPropertyValue(valStr, collection);
        }
        return comparable;
    }

    public static Comparable getPropertyValue(String value, Collection propertyValues) {
        for (Comparable comparable : (Set)propertyValues) {
            if (!ConnectedParser.getValueName(comparable).equals(value)) continue;
            return comparable;
        }
        return null;
    }

    private static Object getValueName(Comparable obj) {
        if (obj instanceof IStringSerializable) {
            IStringSerializable istringserializable = (IStringSerializable)((Object)obj);
            return istringserializable.getString();
        }
        return obj.toString();
    }

    public static Comparable parseValue(String str, Class cls) {
        if (cls == String.class) {
            return str;
        }
        if (cls == Boolean.class) {
            return Boolean.valueOf(str);
        }
        if (cls == Float.class) {
            return Float.valueOf(str);
        }
        if (cls == Double.class) {
            return Double.valueOf(str);
        }
        if (cls == Integer.class) {
            return Integer.valueOf(str);
        }
        return cls == Long.class ? Long.valueOf(str) : null;
    }

    public boolean matchState(BlockState bs, Map<Property, List<Comparable>> mapPropValues) {
        for (Property property : mapPropValues.keySet()) {
            List<Comparable> list = mapPropValues.get(property);
            Object comparable = bs.get(property);
            if (comparable == null) {
                return false;
            }
            if (list.contains(comparable)) continue;
            return false;
        }
        return true;
    }

    public BiomeId[] parseBiomes(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim();
        boolean flag = false;
        if (str.startsWith("!")) {
            flag = true;
            str = str.substring(1);
        }
        String[] astring = Config.tokenize(str, " ");
        List<BiomeId> list = new ArrayList<BiomeId>();
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            BiomeId biomeid = this.getBiomeId(s);
            if (biomeid == null) {
                this.warn("Biome not found: " + s);
                continue;
            }
            list.add(biomeid);
        }
        if (flag) {
            HashSet<ResourceLocation> set = new HashSet<ResourceLocation>(BiomeUtils.getLocations());
            for (BiomeId biomeid1 : list) {
                set.remove(biomeid1.getResourceLocation());
            }
            list = BiomeUtils.getBiomeIds(set);
        }
        return list.toArray(new BiomeId[list.size()]);
    }

    public BiomeId getBiomeId(String biomeName) {
        ResourceLocation resourcelocation = new ResourceLocation(biomeName = biomeName.toLowerCase());
        BiomeId biomeid = BiomeUtils.getBiomeId(resourcelocation);
        if (biomeid != null) {
            return biomeid;
        }
        String s = biomeName.replace(" ", "").replace("_", "");
        ResourceLocation resourcelocation1 = new ResourceLocation(s);
        if (MAP_BIOMES_COMPACT == null) {
            MAP_BIOMES_COMPACT = new HashMap<ResourceLocation, BiomeId>();
            for (ResourceLocation resourcelocation2 : BiomeUtils.getLocations()) {
                BiomeId biomeid1 = BiomeUtils.getBiomeId(resourcelocation2);
                if (biomeid1 == null) continue;
                String s1 = resourcelocation2.getPath().replace(" ", "").replace("_", "").toLowerCase();
                ResourceLocation resourcelocation3 = new ResourceLocation(resourcelocation2.getNamespace(), s1);
                MAP_BIOMES_COMPACT.put(resourcelocation3, biomeid1);
            }
        }
        return (biomeid = MAP_BIOMES_COMPACT.get(resourcelocation1)) != null ? biomeid : null;
    }

    public int parseInt(String str, int defVal) {
        if (str == null) {
            return defVal;
        }
        int i = Config.parseInt(str = str.trim(), -1);
        if (i < 0) {
            this.warn("Invalid number: " + str);
            return defVal;
        }
        return i;
    }

    public int[] parseIntList(String str) {
        if (str == null) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList<Integer>();
        String[] astring = Config.tokenize(str, " ,");
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            if (s.contains("-")) {
                String[] astring1 = Config.tokenize(s, "-");
                if (astring1.length != 2) {
                    this.warn("Invalid interval: " + s + ", when parsing: " + str);
                    continue;
                }
                int k = Config.parseInt(astring1[0], -1);
                int l = Config.parseInt(astring1[1], -1);
                if (k >= 0 && l >= 0 && k <= l) {
                    for (int i1 = k; i1 <= l; ++i1) {
                        list.add(i1);
                    }
                    continue;
                }
                this.warn("Invalid interval: " + s + ", when parsing: " + str);
                continue;
            }
            int j = Config.parseInt(s, -1);
            if (j < 0) {
                this.warn("Invalid number: " + s + ", when parsing: " + str);
                continue;
            }
            list.add(j);
        }
        int[] aint = new int[list.size()];
        for (int j1 = 0; j1 < aint.length; ++j1) {
            aint[j1] = (Integer)list.get(j1);
        }
        return aint;
    }

    public boolean[] parseFaces(String str, boolean[] defVal) {
        if (str == null) {
            return defVal;
        }
        EnumSet<Direction> enumset = EnumSet.allOf(Direction.class);
        String[] astring = Config.tokenize(str, " ,");
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            if (s.equals("sides")) {
                enumset.add(Direction.NORTH);
                enumset.add(Direction.SOUTH);
                enumset.add(Direction.WEST);
                enumset.add(Direction.EAST);
                continue;
            }
            if (s.equals("all")) {
                enumset.addAll(Arrays.asList(Direction.VALUES));
                continue;
            }
            Direction direction = this.parseFace(s);
            if (direction == null) continue;
            enumset.add(direction);
        }
        boolean[] aboolean = new boolean[Direction.VALUES.length];
        for (int j = 0; j < aboolean.length; ++j) {
            aboolean[j] = enumset.contains(Direction.VALUES[j]);
        }
        return aboolean;
    }

    public Direction parseFace(String str) {
        if (!(str = str.toLowerCase()).equals("bottom") && !str.equals("down")) {
            if (!str.equals("top") && !str.equals("up")) {
                if (str.equals("north")) {
                    return Direction.NORTH;
                }
                if (str.equals("south")) {
                    return Direction.SOUTH;
                }
                if (str.equals("east")) {
                    return Direction.EAST;
                }
                if (str.equals("west")) {
                    return Direction.WEST;
                }
                Config.warn("Unknown face: " + str);
                return null;
            }
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    public void dbg(String str) {
        Config.dbg(this.context + ": " + str);
    }

    public void warn(String str) {
        Config.warn(this.context + ": " + str);
    }

    public RangeListInt parseRangeListInt(String str) {
        if (str == null) {
            return null;
        }
        RangeListInt rangelistint = new RangeListInt();
        String[] astring = Config.tokenize(str, " ,");
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            RangeInt rangeint = this.parseRangeInt(s);
            if (rangeint == null) {
                return null;
            }
            rangelistint.addRange(rangeint);
        }
        return rangelistint;
    }

    private RangeInt parseRangeInt(String str) {
        if (str == null) {
            return null;
        }
        if (str.indexOf(45) >= 0) {
            String[] astring = Config.tokenize(str, "-");
            if (astring.length != 2) {
                this.warn("Invalid range: " + str);
                return null;
            }
            int j = Config.parseInt(astring[0], -1);
            int k = Config.parseInt(astring[1], -1);
            if (j >= 0 && k >= 0) {
                return new RangeInt(j, k);
            }
            this.warn("Invalid range: " + str);
            return null;
        }
        int i = Config.parseInt(str, -1);
        if (i < 0) {
            this.warn("Invalid integer: " + str);
            return null;
        }
        return new RangeInt(i, i);
    }

    public boolean parseBoolean(String str, boolean defVal) {
        if (str == null) {
            return defVal;
        }
        String s = str.toLowerCase().trim();
        if (s.equals("true")) {
            return true;
        }
        if (s.equals("false")) {
            return false;
        }
        this.warn("Invalid boolean: " + str);
        return defVal;
    }

    public Boolean parseBooleanObject(String str) {
        if (str == null) {
            return null;
        }
        String s = str.toLowerCase().trim();
        if (s.equals("true")) {
            return Boolean.TRUE;
        }
        if (s.equals("false")) {
            return Boolean.FALSE;
        }
        this.warn("Invalid boolean: " + str);
        return null;
    }

    public static int parseColor(String str, int defVal) {
        if (str == null) {
            return defVal;
        }
        str = str.trim();
        try {
            return Integer.parseInt(str, 16) & 0xFFFFFF;
        }
        catch (NumberFormatException numberformatexception) {
            return defVal;
        }
    }

    public static int parseColor4(String str, int defVal) {
        if (str == null) {
            return defVal;
        }
        str = str.trim();
        try {
            return (int)(Long.parseLong(str, 16) & 0xFFFFFFFFFFFFFFFFL);
        }
        catch (NumberFormatException numberformatexception) {
            return defVal;
        }
    }

    public RenderType parseBlockRenderLayer(String str, RenderType def) {
        if (str == null) {
            return def;
        }
        str = str.toLowerCase().trim();
        RenderType[] arendertype = RenderType.CHUNK_RENDER_TYPES;
        for (int i = 0; i < arendertype.length; ++i) {
            RenderType rendertype = arendertype[i];
            if (!str.equals(rendertype.getName().toLowerCase())) continue;
            return rendertype;
        }
        return def;
    }

    public <T> T parseObject(String str, T[] objs, INameGetter nameGetter, String property) {
        if (str == null) {
            return null;
        }
        String s = str.toLowerCase().trim();
        for (int i = 0; i < objs.length; ++i) {
            T t = objs[i];
            String s1 = nameGetter.getName(t);
            if (s1 == null || !s1.toLowerCase().equals(s)) continue;
            return t;
        }
        this.warn("Invalid " + property + ": " + str);
        return null;
    }

    public <T> T[] parseObjects(String str, T[] objs, INameGetter nameGetter, String property, T[] errValue) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase().trim();
        String[] astring = Config.tokenize(str, " ");
        Object[] at = (Object[])Array.newInstance(objs.getClass().getComponentType(), astring.length);
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            T t = this.parseObject(s, objs, nameGetter, property);
            if (t == null) {
                return errValue;
            }
            at[i] = t;
        }
        return at;
    }

    public Enum parseEnum(String str, Enum[] enums, String property) {
        return this.parseObject(str, enums, NAME_GETTER_ENUM, property);
    }

    public Enum[] parseEnums(String str, Enum[] enums, String property, Enum[] errValue) {
        return this.parseObjects(str, enums, NAME_GETTER_ENUM, property, errValue);
    }

    public DyeColor[] parseDyeColors(String str, String property, DyeColor[] errValue) {
        return this.parseObjects(str, DyeColor.values(), NAME_GETTER_DYE_COLOR, property, errValue);
    }

    public Weather[] parseWeather(String str, String property, Weather[] errValue) {
        return this.parseObjects(str, Weather.values(), NAME_GETTER_ENUM, property, errValue);
    }

    public NbtTagValue parseNbtTagValue(String path, String value) {
        return path != null && value != null ? new NbtTagValue(path, value) : null;
    }

    public MatchProfession[] parseProfessions(String profStr) {
        if (profStr == null) {
            return null;
        }
        ArrayList<MatchProfession> list = new ArrayList<MatchProfession>();
        String[] astring = Config.tokenize(profStr, " ");
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            MatchProfession matchprofession = this.parseProfession(s);
            if (matchprofession == null) {
                this.warn("Invalid profession: " + s);
                return PROFESSIONS_INVALID;
            }
            list.add(matchprofession);
        }
        return list.isEmpty() ? null : list.toArray(new MatchProfession[list.size()]);
    }

    private MatchProfession parseProfession(String str) {
        VillagerProfession villagerprofession;
        String s = str;
        String s1 = null;
        int i = str.lastIndexOf(58);
        if (i >= 0) {
            String s2 = str.substring(0, i);
            String s3 = str.substring(i + 1);
            if (s3.isEmpty() || s3.matches("[0-9].*")) {
                s = s2;
                s1 = s3;
            }
        }
        if ((villagerprofession = this.parseVillagerProfession(s)) == null) {
            return null;
        }
        int[] aint = this.parseIntList(s1);
        return new MatchProfession(villagerprofession, aint);
    }

    private VillagerProfession parseVillagerProfession(String str) {
        if (str == null) {
            return null;
        }
        DefaultedRegistry<VillagerProfession> registry = Registry.VILLAGER_PROFESSION;
        ResourceLocation resourcelocation = new ResourceLocation(str = str.toLowerCase());
        return !((Registry)registry).containsKey(resourcelocation) ? null : (VillagerProfession)((Registry)registry).getOrDefault(resourcelocation);
    }

    public int[] parseItems(String str) {
        str = str.trim();
        TreeSet<Integer> set = new TreeSet<Integer>();
        String[] astring = Config.tokenize(str, " ");
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            ResourceLocation resourcelocation = new ResourceLocation(s);
            Item item = ItemUtils.getItem(resourcelocation);
            if (item == null) {
                this.warn("Item not found: " + s);
                continue;
            }
            int j = ItemUtils.getId(item);
            if (j < 0) {
                this.warn("Item has no ID: " + String.valueOf(item) + ", name: " + s);
                continue;
            }
            set.add(new Integer(j));
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        return Config.toPrimitive(ainteger);
    }

    public int[] parseEntities(String str) {
        str = str.trim();
        TreeSet<Integer> set = new TreeSet<Integer>();
        String[] astring = Config.tokenize(str, " ");
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            ResourceLocation resourcelocation = new ResourceLocation(s);
            EntityType entitytype = EntityTypeUtils.getEntityType(resourcelocation);
            if (entitytype == null) {
                this.warn("Entity not found: " + s);
                continue;
            }
            int j = Registry.ENTITY_TYPE.getId(entitytype);
            if (j < 0) {
                this.warn("Entity has no ID: " + String.valueOf(entitytype) + ", name: " + s);
                continue;
            }
            set.add(new Integer(j));
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        return Config.toPrimitive(ainteger);
    }
}
