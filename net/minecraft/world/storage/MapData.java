package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DimensionType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapBanner;
import net.minecraft.world.storage.MapDecoration;
import net.minecraft.world.storage.MapFrame;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapData
extends WorldSavedData {
    private static final Logger LOGGER = LogManager.getLogger();
    public int xCenter;
    public int zCenter;
    public RegistryKey<World> dimension;
    public boolean trackingPosition;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors = new byte[16384];
    public boolean locked;
    public final List<MapInfo> playersArrayList = Lists.newArrayList();
    private final Map<PlayerEntity, MapInfo> playersHashMap = Maps.newHashMap();
    private final Map<String, MapBanner> banners = Maps.newHashMap();
    public final Map<String, MapDecoration> mapDecorations = Maps.newLinkedHashMap();
    private final Map<String, MapFrame> frames = Maps.newHashMap();

    public MapData(String mapname) {
        super(mapname);
    }

    public void initData(int x, int z, int scale, boolean trackingPosition, boolean unlimitedTracking, RegistryKey<World> dimension) {
        this.scale = (byte)scale;
        this.calculateMapCenter(x, z, this.scale);
        this.dimension = dimension;
        this.trackingPosition = trackingPosition;
        this.unlimitedTracking = unlimitedTracking;
        this.markDirty();
    }

    public void calculateMapCenter(double x, double z, int mapScale) {
        int i = 128 * (1 << mapScale);
        int j = MathHelper.floor((x + 64.0) / (double)i);
        int k = MathHelper.floor((z + 64.0) / (double)i);
        this.xCenter = j * i + i / 2 - 64;
        this.zCenter = k * i + i / 2 - 64;
    }

    @Override
    public void read(CompoundNBT nbt) {
        this.dimension = DimensionType.decodeWorldKey(new Dynamic<INBT>(NBTDynamicOps.INSTANCE, nbt.get("dimension"))).resultOrPartial(LOGGER::error).orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + String.valueOf(nbt.get("dimension"))));
        this.xCenter = nbt.getInt("xCenter");
        this.zCenter = nbt.getInt("zCenter");
        this.scale = (byte)MathHelper.clamp(nbt.getByte("scale"), 0, 4);
        this.trackingPosition = !nbt.contains("trackingPosition", 1) || nbt.getBoolean("trackingPosition");
        this.unlimitedTracking = nbt.getBoolean("unlimitedTracking");
        this.locked = nbt.getBoolean("locked");
        this.colors = nbt.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        ListNBT listnbt = nbt.getList("banners", 10);
        for (int i = 0; i < listnbt.size(); ++i) {
            MapBanner mapbanner = MapBanner.read(listnbt.getCompound(i));
            this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
            this.updateDecorations(mapbanner.getDecorationType(), null, mapbanner.getMapDecorationId(), mapbanner.getPos().getX(), mapbanner.getPos().getZ(), 180.0, mapbanner.getName());
        }
        ListNBT listnbt1 = nbt.getList("frames", 10);
        for (int j = 0; j < listnbt1.size(); ++j) {
            MapFrame mapframe = MapFrame.read(listnbt1.getCompound(j));
            this.frames.put(mapframe.getFrameName(), mapframe);
            this.updateDecorations(MapDecoration.Type.FRAME, null, "frame-" + mapframe.getEntityId(), mapframe.getPos().getX(), mapframe.getPos().getZ(), mapframe.getRotation(), null);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ResourceLocation.CODEC.encodeStart(NBTDynamicOps.INSTANCE, this.dimension.getLocation()).resultOrPartial(LOGGER::error).ifPresent(dimNBT -> compound.put("dimension", (INBT)dimNBT));
        compound.putInt("xCenter", this.xCenter);
        compound.putInt("zCenter", this.zCenter);
        compound.putByte("scale", this.scale);
        compound.putByteArray("colors", this.colors);
        compound.putBoolean("trackingPosition", this.trackingPosition);
        compound.putBoolean("unlimitedTracking", this.unlimitedTracking);
        compound.putBoolean("locked", this.locked);
        ListNBT listnbt = new ListNBT();
        for (MapBanner mapbanner : this.banners.values()) {
            listnbt.add(mapbanner.write());
        }
        compound.put("banners", listnbt);
        ListNBT listnbt1 = new ListNBT();
        for (MapFrame mapframe : this.frames.values()) {
            listnbt1.add(mapframe.write());
        }
        compound.put("frames", listnbt1);
        return compound;
    }

    public void copyFrom(MapData mapDataIn) {
        this.locked = true;
        this.xCenter = mapDataIn.xCenter;
        this.zCenter = mapDataIn.zCenter;
        this.banners.putAll(mapDataIn.banners);
        this.mapDecorations.putAll(mapDataIn.mapDecorations);
        System.arraycopy(mapDataIn.colors, 0, this.colors, 0, mapDataIn.colors.length);
        this.markDirty();
    }

    public void updateVisiblePlayers(PlayerEntity player, ItemStack mapStack) {
        CompoundNBT compoundnbt;
        if (!this.playersHashMap.containsKey(player)) {
            MapInfo mapdata$mapinfo = new MapInfo(player);
            this.playersHashMap.put(player, mapdata$mapinfo);
            this.playersArrayList.add(mapdata$mapinfo);
        }
        if (!player.inventory.hasItemStack(mapStack)) {
            this.mapDecorations.remove(player.getName().getString());
        }
        for (int i = 0; i < this.playersArrayList.size(); ++i) {
            MapInfo mapdata$mapinfo1 = this.playersArrayList.get(i);
            String s = mapdata$mapinfo1.player.getName().getString();
            if (!mapdata$mapinfo1.player.removed && (mapdata$mapinfo1.player.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
                if (mapStack.isOnItemFrame() || mapdata$mapinfo1.player.world.getDimensionKey() != this.dimension || !this.trackingPosition) continue;
                this.updateDecorations(MapDecoration.Type.PLAYER, mapdata$mapinfo1.player.world, s, mapdata$mapinfo1.player.getPosX(), mapdata$mapinfo1.player.getPosZ(), mapdata$mapinfo1.player.rotationYaw, null);
                continue;
            }
            this.playersHashMap.remove(mapdata$mapinfo1.player);
            this.playersArrayList.remove(mapdata$mapinfo1);
            this.mapDecorations.remove(s);
        }
        if (mapStack.isOnItemFrame() && this.trackingPosition) {
            ItemFrameEntity itemframeentity = mapStack.getItemFrame();
            BlockPos blockpos = itemframeentity.getHangingPosition();
            MapFrame mapframe1 = this.frames.get(MapFrame.getFrameNameWithPos(blockpos));
            if (mapframe1 != null && itemframeentity.getEntityId() != mapframe1.getEntityId() && this.frames.containsKey(mapframe1.getFrameName())) {
                this.mapDecorations.remove("frame-" + mapframe1.getEntityId());
            }
            MapFrame mapframe = new MapFrame(blockpos, itemframeentity.getHorizontalFacing().getHorizontalIndex() * 90, itemframeentity.getEntityId());
            this.updateDecorations(MapDecoration.Type.FRAME, player.world, "frame-" + itemframeentity.getEntityId(), blockpos.getX(), blockpos.getZ(), itemframeentity.getHorizontalFacing().getHorizontalIndex() * 90, null);
            this.frames.put(mapframe.getFrameName(), mapframe);
        }
        if ((compoundnbt = mapStack.getTag()) != null && compoundnbt.contains("Decorations", 9)) {
            ListNBT listnbt = compoundnbt.getList("Decorations", 10);
            for (int j = 0; j < listnbt.size(); ++j) {
                CompoundNBT compoundnbt1 = listnbt.getCompound(j);
                if (this.mapDecorations.containsKey(compoundnbt1.getString("id"))) continue;
                this.updateDecorations(MapDecoration.Type.byIcon(compoundnbt1.getByte("type")), player.world, compoundnbt1.getString("id"), compoundnbt1.getDouble("x"), compoundnbt1.getDouble("z"), compoundnbt1.getDouble("rot"), null);
            }
        }
    }

    public static void addTargetDecoration(ItemStack map, BlockPos target, String decorationName, MapDecoration.Type type) {
        ListNBT listnbt;
        if (map.hasTag() && map.getTag().contains("Decorations", 9)) {
            listnbt = map.getTag().getList("Decorations", 10);
        } else {
            listnbt = new ListNBT();
            map.setTagInfo("Decorations", listnbt);
        }
        CompoundNBT compoundnbt = new CompoundNBT();
        compoundnbt.putByte("type", type.getIcon());
        compoundnbt.putString("id", decorationName);
        compoundnbt.putDouble("x", target.getX());
        compoundnbt.putDouble("z", target.getZ());
        compoundnbt.putDouble("rot", 180.0);
        listnbt.add(compoundnbt);
        if (type.hasMapColor()) {
            CompoundNBT compoundnbt1 = map.getOrCreateChildTag("display");
            compoundnbt1.putInt("MapColor", type.getMapColor());
        }
    }

    private void updateDecorations(MapDecoration.Type type, @Nullable IWorld worldIn, String decorationName, double worldX, double worldZ, double rotationIn, @Nullable ITextComponent name) {
        byte b2;
        int i = 1 << this.scale;
        float f = (float)(worldX - (double)this.xCenter) / (float)i;
        float f1 = (float)(worldZ - (double)this.zCenter) / (float)i;
        byte b0 = (byte)((double)(f * 2.0f) + 0.5);
        byte b1 = (byte)((double)(f1 * 2.0f) + 0.5);
        int j = 63;
        if (f >= -63.0f && f1 >= -63.0f && f <= 63.0f && f1 <= 63.0f) {
            b2 = (byte)((rotationIn += rotationIn < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension == World.THE_NETHER && worldIn != null) {
                int l = (int)(worldIn.getWorldInfo().getDayTime() / 10L);
                b2 = (byte)(l * l * 34187121 + l * 121 >> 15 & 0xF);
            }
        } else {
            if (type != MapDecoration.Type.PLAYER) {
                this.mapDecorations.remove(decorationName);
                return;
            }
            int k = 320;
            if (Math.abs(f) < 320.0f && Math.abs(f1) < 320.0f) {
                type = MapDecoration.Type.PLAYER_OFF_MAP;
            } else {
                if (!this.unlimitedTracking) {
                    this.mapDecorations.remove(decorationName);
                    return;
                }
                type = MapDecoration.Type.PLAYER_OFF_LIMITS;
            }
            b2 = 0;
            if (f <= -63.0f) {
                b0 = -128;
            }
            if (f1 <= -63.0f) {
                b1 = -128;
            }
            if (f >= 63.0f) {
                b0 = 127;
            }
            if (f1 >= 63.0f) {
                b1 = 127;
            }
        }
        this.mapDecorations.put(decorationName, new MapDecoration(type, b0, b1, b2, name));
    }

    @Nullable
    public IPacket<?> getMapPacket(ItemStack mapStack, IBlockReader worldIn, PlayerEntity player) {
        MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
        return mapdata$mapinfo == null ? null : mapdata$mapinfo.getPacket(mapStack);
    }

    public void updateMapData(int x, int y) {
        this.markDirty();
        for (MapInfo mapdata$mapinfo : this.playersArrayList) {
            mapdata$mapinfo.update(x, y);
        }
    }

    public MapInfo getMapInfo(PlayerEntity player) {
        MapInfo mapdata$mapinfo = this.playersHashMap.get(player);
        if (mapdata$mapinfo == null) {
            mapdata$mapinfo = new MapInfo(player);
            this.playersHashMap.put(player, mapdata$mapinfo);
            this.playersArrayList.add(mapdata$mapinfo);
        }
        return mapdata$mapinfo;
    }

    public void tryAddBanner(IWorld world, BlockPos pos) {
        double d0 = (double)pos.getX() + 0.5;
        double d1 = (double)pos.getZ() + 0.5;
        int i = 1 << this.scale;
        double d2 = (d0 - (double)this.xCenter) / (double)i;
        double d3 = (d1 - (double)this.zCenter) / (double)i;
        int j = 63;
        boolean flag = false;
        if (d2 >= -63.0 && d3 >= -63.0 && d2 <= 63.0 && d3 <= 63.0) {
            MapBanner mapbanner = MapBanner.fromWorld(world, pos);
            if (mapbanner == null) {
                return;
            }
            boolean flag1 = true;
            if (this.banners.containsKey(mapbanner.getMapDecorationId()) && this.banners.get(mapbanner.getMapDecorationId()).equals(mapbanner)) {
                this.banners.remove(mapbanner.getMapDecorationId());
                this.mapDecorations.remove(mapbanner.getMapDecorationId());
                flag1 = false;
                flag = true;
            }
            if (flag1) {
                this.banners.put(mapbanner.getMapDecorationId(), mapbanner);
                this.updateDecorations(mapbanner.getDecorationType(), world, mapbanner.getMapDecorationId(), d0, d1, 180.0, mapbanner.getName());
                flag = true;
            }
            if (flag) {
                this.markDirty();
            }
        }
    }

    public void removeStaleBanners(IBlockReader reader, int x, int z) {
        Iterator<MapBanner> iterator = this.banners.values().iterator();
        while (iterator.hasNext()) {
            MapBanner mapbanner1;
            MapBanner mapbanner = iterator.next();
            if (mapbanner.getPos().getX() != x || mapbanner.getPos().getZ() != z || mapbanner.equals(mapbanner1 = MapBanner.fromWorld(reader, mapbanner.getPos()))) continue;
            iterator.remove();
            this.mapDecorations.remove(mapbanner.getMapDecorationId());
        }
    }

    public void removeItemFrame(BlockPos pos, int entityIdIn) {
        this.mapDecorations.remove("frame-" + entityIdIn);
        this.frames.remove(MapFrame.getFrameNameWithPos(pos));
    }

    public class MapInfo {
        public final PlayerEntity player;
        private boolean isDirty = true;
        private int minX;
        private int minY;
        private int maxX = 127;
        private int maxY = 127;
        private int tick;
        public int step;

        public MapInfo(PlayerEntity player) {
            this.player = player;
        }

        @Nullable
        public IPacket<?> getPacket(ItemStack stack) {
            if (this.isDirty) {
                this.isDirty = false;
                return new SMapDataPacket(FilledMapItem.getMapId(stack), MapData.this.scale, MapData.this.trackingPosition, MapData.this.locked, MapData.this.mapDecorations.values(), MapData.this.colors, this.minX, this.minY, this.maxX + 1 - this.minX, this.maxY + 1 - this.minY);
            }
            return this.tick++ % 5 == 0 ? new SMapDataPacket(FilledMapItem.getMapId(stack), MapData.this.scale, MapData.this.trackingPosition, MapData.this.locked, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0) : null;
        }

        public void update(int x, int y) {
            if (this.isDirty) {
                this.minX = Math.min(this.minX, x);
                this.minY = Math.min(this.minY, y);
                this.maxX = Math.max(this.maxX, x);
                this.maxY = Math.max(this.maxY, y);
            } else {
                this.isDirty = true;
                this.minX = x;
                this.minY = y;
                this.maxX = x;
                this.maxY = y;
            }
        }
    }
}
