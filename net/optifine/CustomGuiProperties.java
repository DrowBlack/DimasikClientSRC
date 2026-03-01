package net.optifine;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.DropperTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.optifine.Config;
import net.optifine.CustomGuis;
import net.optifine.config.BiomeId;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchProfession;
import net.optifine.config.Matches;
import net.optifine.config.NbtTagValue;
import net.optifine.config.RangeListInt;
import net.optifine.util.StrUtils;
import net.optifine.util.TextureUtils;

public class CustomGuiProperties {
    private String fileName = null;
    private String basePath = null;
    private EnumContainer container = null;
    private Map<ResourceLocation, ResourceLocation> textureLocations = null;
    private NbtTagValue nbtName = null;
    private BiomeId[] biomes = null;
    private RangeListInt heights = null;
    private Boolean large = null;
    private Boolean trapped = null;
    private Boolean christmas = null;
    private Boolean ender = null;
    private RangeListInt levels = null;
    private MatchProfession[] professions = null;
    private EnumVariant[] variants = null;
    private DyeColor[] colors = null;
    private static final EnumVariant[] VARIANTS_HORSE = new EnumVariant[]{EnumVariant.HORSE, EnumVariant.DONKEY, EnumVariant.MULE, EnumVariant.LLAMA};
    private static final EnumVariant[] VARIANTS_DISPENSER = new EnumVariant[]{EnumVariant.DISPENSER, EnumVariant.DROPPER};
    private static final EnumVariant[] VARIANTS_INVALID = new EnumVariant[0];
    private static final DyeColor[] COLORS_INVALID = new DyeColor[0];
    private static final ResourceLocation ANVIL_GUI_TEXTURE = new ResourceLocation("textures/gui/container/anvil.png");
    private static final ResourceLocation BEACON_GUI_TEXTURE = new ResourceLocation("textures/gui/container/beacon.png");
    private static final ResourceLocation BREWING_STAND_GUI_TEXTURE = new ResourceLocation("textures/gui/container/brewing_stand.png");
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/crafting_table.png");
    private static final ResourceLocation HORSE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/horse.png");
    private static final ResourceLocation DISPENSER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/dispenser.png");
    private static final ResourceLocation ENCHANTMENT_TABLE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation FURNACE_GUI_TEXTURE = new ResourceLocation("textures/gui/container/furnace.png");
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
    private static final ResourceLocation INVENTORY_GUI_TEXTURE = new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation SHULKER_BOX_GUI_TEXTURE = new ResourceLocation("textures/gui/container/shulker_box.png");
    private static final ResourceLocation VILLAGER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager2.png");

    public CustomGuiProperties(Properties props, String path) {
        ConnectedParser connectedparser = new ConnectedParser("CustomGuis");
        this.fileName = connectedparser.parseName(path);
        this.basePath = connectedparser.parseBasePath(path);
        this.container = (EnumContainer)connectedparser.parseEnum(props.getProperty("container"), EnumContainer.values(), "container");
        this.textureLocations = CustomGuiProperties.parseTextureLocations(props, "texture", this.container, "textures/gui/", this.basePath);
        this.nbtName = connectedparser.parseNbtTagValue("name", props.getProperty("name"));
        this.biomes = connectedparser.parseBiomes(props.getProperty("biomes"));
        this.heights = connectedparser.parseRangeListInt(props.getProperty("heights"));
        this.large = connectedparser.parseBooleanObject(props.getProperty("large"));
        this.trapped = connectedparser.parseBooleanObject(props.getProperty("trapped"));
        this.christmas = connectedparser.parseBooleanObject(props.getProperty("christmas"));
        this.ender = connectedparser.parseBooleanObject(props.getProperty("ender"));
        this.levels = connectedparser.parseRangeListInt(props.getProperty("levels"));
        this.professions = connectedparser.parseProfessions(props.getProperty("professions"));
        Enum[] acustomguiproperties$enumvariant = CustomGuiProperties.getContainerVariants(this.container);
        this.variants = (EnumVariant[])connectedparser.parseEnums(props.getProperty("variants"), acustomguiproperties$enumvariant, "variants", VARIANTS_INVALID);
        this.colors = CustomGuiProperties.parseEnumDyeColors(props.getProperty("colors"));
    }

    private static EnumVariant[] getContainerVariants(EnumContainer cont) {
        if (cont == EnumContainer.HORSE) {
            return VARIANTS_HORSE;
        }
        return cont == EnumContainer.DISPENSER ? VARIANTS_DISPENSER : new EnumVariant[]{};
    }

    private static DyeColor[] parseEnumDyeColors(String str) {
        if (str == null) {
            return null;
        }
        str = str.toLowerCase();
        String[] astring = Config.tokenize(str, " ");
        DyeColor[] adyecolor = new DyeColor[astring.length];
        for (int i = 0; i < astring.length; ++i) {
            String s = astring[i];
            DyeColor dyecolor = CustomGuiProperties.parseEnumDyeColor(s);
            if (dyecolor == null) {
                CustomGuiProperties.warn("Invalid color: " + s);
                return COLORS_INVALID;
            }
            adyecolor[i] = dyecolor;
        }
        return adyecolor;
    }

    private static DyeColor parseEnumDyeColor(String str) {
        if (str == null) {
            return null;
        }
        DyeColor[] adyecolor = DyeColor.values();
        for (int i = 0; i < adyecolor.length; ++i) {
            DyeColor dyecolor = adyecolor[i];
            if (dyecolor.getString().equals(str)) {
                return dyecolor;
            }
            if (!dyecolor.getTranslationKey().equals(str)) continue;
            return dyecolor;
        }
        return null;
    }

    private static ResourceLocation parseTextureLocation(String str, String basePath) {
        if (str == null) {
            return null;
        }
        Object s = TextureUtils.fixResourcePath(str = str.trim(), basePath);
        if (!((String)s).endsWith(".png")) {
            s = (String)s + ".png";
        }
        return new ResourceLocation(basePath + "/" + (String)s);
    }

    private static Map<ResourceLocation, ResourceLocation> parseTextureLocations(Properties props, String property, EnumContainer container, String pathPrefix, String basePath) {
        HashMap<ResourceLocation, ResourceLocation> map = new HashMap<ResourceLocation, ResourceLocation>();
        String s = props.getProperty(property);
        if (s != null) {
            ResourceLocation resourcelocation = CustomGuiProperties.getGuiTextureLocation(container);
            ResourceLocation resourcelocation1 = CustomGuiProperties.parseTextureLocation(s, basePath);
            if (resourcelocation != null && resourcelocation1 != null) {
                map.put(resourcelocation, resourcelocation1);
            }
        }
        String s5 = property + ".";
        for (String string : props.keySet()) {
            if (!string.startsWith(s5)) continue;
            String s2 = string.substring(s5.length());
            s2 = s2.replace('\\', '/');
            s2 = StrUtils.removePrefixSuffix(s2, "/", ".png");
            String s3 = pathPrefix + s2 + ".png";
            String s4 = props.getProperty(string);
            ResourceLocation resourcelocation2 = new ResourceLocation(s3);
            ResourceLocation resourcelocation3 = CustomGuiProperties.parseTextureLocation(s4, basePath);
            map.put(resourcelocation2, resourcelocation3);
        }
        return map;
    }

    private static ResourceLocation getGuiTextureLocation(EnumContainer container) {
        if (container == null) {
            return null;
        }
        switch (container) {
            case ANVIL: {
                return ANVIL_GUI_TEXTURE;
            }
            case BEACON: {
                return BEACON_GUI_TEXTURE;
            }
            case BREWING_STAND: {
                return BREWING_STAND_GUI_TEXTURE;
            }
            case CHEST: {
                return CHEST_GUI_TEXTURE;
            }
            case CRAFTING: {
                return CRAFTING_TABLE_GUI_TEXTURE;
            }
            case CREATIVE: {
                return null;
            }
            case DISPENSER: {
                return DISPENSER_GUI_TEXTURE;
            }
            case ENCHANTMENT: {
                return ENCHANTMENT_TABLE_GUI_TEXTURE;
            }
            case FURNACE: {
                return FURNACE_GUI_TEXTURE;
            }
            case HOPPER: {
                return HOPPER_GUI_TEXTURE;
            }
            case HORSE: {
                return HORSE_GUI_TEXTURE;
            }
            case INVENTORY: {
                return INVENTORY_GUI_TEXTURE;
            }
            case SHULKER_BOX: {
                return SHULKER_BOX_GUI_TEXTURE;
            }
            case VILLAGER: {
                return VILLAGER_GUI_TEXTURE;
            }
        }
        return null;
    }

    public boolean isValid(String path) {
        if (this.fileName != null && this.fileName.length() > 0) {
            if (this.basePath == null) {
                CustomGuiProperties.warn("No base path found: " + path);
                return false;
            }
            if (this.container == null) {
                CustomGuiProperties.warn("No container found: " + path);
                return false;
            }
            if (this.textureLocations.isEmpty()) {
                CustomGuiProperties.warn("No texture found: " + path);
                return false;
            }
            if (this.professions == ConnectedParser.PROFESSIONS_INVALID) {
                CustomGuiProperties.warn("Invalid professions or careers: " + path);
                return false;
            }
            if (this.variants == VARIANTS_INVALID) {
                CustomGuiProperties.warn("Invalid variants: " + path);
                return false;
            }
            if (this.colors == COLORS_INVALID) {
                CustomGuiProperties.warn("Invalid colors: " + path);
                return false;
            }
            return true;
        }
        CustomGuiProperties.warn("No name found: " + path);
        return false;
    }

    private static void warn(String str) {
        Config.warn("[CustomGuis] " + str);
    }

    private boolean matchesGeneral(EnumContainer ec, BlockPos pos, IWorldReader blockAccess) {
        Biome biome;
        if (this.container != ec) {
            return false;
        }
        if (this.biomes != null && !Matches.biome(biome = blockAccess.getBiome(pos), this.biomes)) {
            return false;
        }
        return this.heights == null || this.heights.isInRange(pos.getY());
    }

    public boolean matchesPos(EnumContainer ec, BlockPos pos, IWorldReader blockAccess, Screen screen) {
        String s;
        if (!this.matchesGeneral(ec, pos, blockAccess)) {
            return false;
        }
        if (this.nbtName != null && !this.nbtName.matchesValue(s = CustomGuiProperties.getName(screen))) {
            return false;
        }
        switch (ec) {
            case BEACON: {
                return this.matchesBeacon(pos, blockAccess);
            }
            case CHEST: {
                return this.matchesChest(pos, blockAccess);
            }
            case DISPENSER: {
                return this.matchesDispenser(pos, blockAccess);
            }
            case SHULKER_BOX: {
                return this.matchesShulker(pos, blockAccess);
            }
        }
        return true;
    }

    public static String getName(Screen screen) {
        ITextComponent itextcomponent = screen.getTitle();
        return itextcomponent == null ? null : itextcomponent.getUnformattedComponentText();
    }

    private boolean matchesBeacon(BlockPos pos, IBlockDisplayReader blockAccess) {
        int i;
        TileEntity tileentity = blockAccess.getTileEntity(pos);
        if (!(tileentity instanceof BeaconTileEntity)) {
            return false;
        }
        BeaconTileEntity beacontileentity = (BeaconTileEntity)tileentity;
        return this.levels == null || this.levels.isInRange(i = beacontileentity.getLevels());
    }

    private boolean matchesChest(BlockPos pos, IBlockDisplayReader blockAccess) {
        TileEntity tileentity = blockAccess.getTileEntity(pos);
        if (tileentity instanceof ChestTileEntity) {
            ChestTileEntity chesttileentity = (ChestTileEntity)tileentity;
            return this.matchesChest(chesttileentity, pos, blockAccess);
        }
        if (tileentity instanceof EnderChestTileEntity) {
            EnderChestTileEntity enderchesttileentity = (EnderChestTileEntity)tileentity;
            return this.matchesEnderChest(enderchesttileentity, pos, blockAccess);
        }
        return false;
    }

    private boolean matchesChest(ChestTileEntity tec, BlockPos pos, IBlockDisplayReader blockAccess) {
        BlockState blockstate = blockAccess.getBlockState(pos);
        ChestType chesttype = blockstate.hasProperty(ChestBlock.TYPE) ? blockstate.get(ChestBlock.TYPE) : ChestType.SINGLE;
        boolean flag = chesttype != ChestType.SINGLE;
        boolean flag1 = tec instanceof TrappedChestTileEntity;
        boolean flag2 = CustomGuis.isChristmas;
        boolean flag3 = false;
        return this.matchesChest(flag, flag1, flag2, flag3);
    }

    private boolean matchesEnderChest(EnderChestTileEntity teec, BlockPos pos, IBlockDisplayReader blockAccess) {
        return this.matchesChest(false, false, false, true);
    }

    private boolean matchesChest(boolean isLarge, boolean isTrapped, boolean isChristmas, boolean isEnder) {
        if (this.large != null && this.large != isLarge) {
            return false;
        }
        if (this.trapped != null && this.trapped != isTrapped) {
            return false;
        }
        if (this.christmas != null && this.christmas != isChristmas) {
            return false;
        }
        return this.ender == null || this.ender == isEnder;
    }

    private boolean matchesDispenser(BlockPos pos, IBlockDisplayReader blockAccess) {
        EnumVariant customguiproperties$enumvariant;
        TileEntity tileentity = blockAccess.getTileEntity(pos);
        if (!(tileentity instanceof DispenserTileEntity)) {
            return false;
        }
        DispenserTileEntity dispensertileentity = (DispenserTileEntity)tileentity;
        return this.variants == null || Config.equalsOne((Object)(customguiproperties$enumvariant = this.getDispenserVariant(dispensertileentity)), (Object[])this.variants);
    }

    private EnumVariant getDispenserVariant(DispenserTileEntity ted) {
        return ted instanceof DropperTileEntity ? EnumVariant.DROPPER : EnumVariant.DISPENSER;
    }

    private boolean matchesShulker(BlockPos pos, IBlockDisplayReader blockAccess) {
        DyeColor dyecolor;
        TileEntity tileentity = blockAccess.getTileEntity(pos);
        if (!(tileentity instanceof ShulkerBoxTileEntity)) {
            return false;
        }
        ShulkerBoxTileEntity shulkerboxtileentity = (ShulkerBoxTileEntity)tileentity;
        return this.colors == null || Config.equalsOne(dyecolor = shulkerboxtileentity.getColor(), this.colors);
    }

    public boolean matchesEntity(EnumContainer ec, Entity entity, IWorldReader blockAccess) {
        String s;
        if (!this.matchesGeneral(ec, entity.getPosition(), blockAccess)) {
            return false;
        }
        if (this.nbtName != null && !this.nbtName.matchesValue(s = entity.getScoreboardName())) {
            return false;
        }
        switch (ec) {
            case HORSE: {
                return this.matchesHorse(entity, blockAccess);
            }
            case VILLAGER: {
                return this.matchesVillager(entity, blockAccess);
            }
        }
        return true;
    }

    private boolean matchesVillager(Entity entity, IBlockDisplayReader blockAccess) {
        int i;
        VillagerData villagerdata;
        VillagerProfession villagerprofession;
        if (!(entity instanceof VillagerEntity)) {
            return false;
        }
        VillagerEntity villagerentity = (VillagerEntity)entity;
        return this.professions == null || MatchProfession.matchesOne(villagerprofession = (villagerdata = villagerentity.getVillagerData()).getProfession(), i = villagerdata.getLevel(), this.professions);
    }

    private boolean matchesHorse(Entity entity, IBlockDisplayReader blockAccess) {
        LlamaEntity llamaentity;
        DyeColor dyecolor;
        EnumVariant customguiproperties$enumvariant;
        if (!(entity instanceof AbstractHorseEntity)) {
            return false;
        }
        AbstractHorseEntity abstracthorseentity = (AbstractHorseEntity)entity;
        if (this.variants != null && !Config.equalsOne((Object)(customguiproperties$enumvariant = this.getHorseVariant(abstracthorseentity)), (Object[])this.variants)) {
            return false;
        }
        return this.colors == null || !(abstracthorseentity instanceof LlamaEntity) || Config.equalsOne(dyecolor = (llamaentity = (LlamaEntity)abstracthorseentity).getColor(), this.colors);
    }

    private EnumVariant getHorseVariant(AbstractHorseEntity entity) {
        if (entity instanceof HorseEntity) {
            return EnumVariant.HORSE;
        }
        if (entity instanceof DonkeyEntity) {
            return EnumVariant.DONKEY;
        }
        if (entity instanceof MuleEntity) {
            return EnumVariant.MULE;
        }
        return entity instanceof LlamaEntity ? EnumVariant.LLAMA : null;
    }

    public EnumContainer getContainer() {
        return this.container;
    }

    public ResourceLocation getTextureLocation(ResourceLocation loc) {
        ResourceLocation resourcelocation = this.textureLocations.get(loc);
        return resourcelocation == null ? loc : resourcelocation;
    }

    public String toString() {
        return "name: " + this.fileName + ", container: " + String.valueOf((Object)this.container) + ", textures: " + String.valueOf(this.textureLocations);
    }

    public static enum EnumContainer {
        ANVIL,
        BEACON,
        BREWING_STAND,
        CHEST,
        CRAFTING,
        DISPENSER,
        ENCHANTMENT,
        FURNACE,
        HOPPER,
        HORSE,
        VILLAGER,
        SHULKER_BOX,
        CREATIVE,
        INVENTORY;

    }

    private static enum EnumVariant {
        HORSE,
        DONKEY,
        MULE,
        LLAMA,
        DISPENSER,
        DROPPER;

    }
}
