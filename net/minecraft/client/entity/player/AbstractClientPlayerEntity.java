package net.minecraft.client.entity.player;

import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.mods.cape.wavecapes.CapeHolder;
import dimasik.managers.mods.cape.wavecapes.sim.StickSimulation;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.ShoulderRidingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.optifine.Config;
import net.optifine.player.CapeUtils;
import net.optifine.player.PlayerConfigurations;
import net.optifine.reflect.Reflector;

public abstract class AbstractClientPlayerEntity
extends PlayerEntity
implements CapeHolder {
    private NetworkPlayerInfo playerInfo;
    public float rotateElytraX;
    public float rotateElytraY;
    public float rotateElytraZ;
    public final ClientWorld worldClient;
    private ResourceLocation locationOfCape = null;
    private long reloadCapeTimeMs = 0L;
    private boolean elytraOfCape = false;
    private String nameClear = null;
    public ShoulderRidingEntity entityShoulderLeft;
    public ShoulderRidingEntity entityShoulderRight;
    private final StickSimulation stickSimulation;
    public float capeRotateX;
    public float capeRotateY;
    public float capeRotateZ;
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    public AbstractClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, world.func_239140_u_(), world.func_243489_v(), profile);
        this.worldClient = world;
        this.nameClear = profile.getName();
        if (this.nameClear != null && !this.nameClear.isEmpty()) {
            this.nameClear = StringUtils.stripControlCodes(this.nameClear);
        }
        CapeUtils.downloadCape(this);
        PlayerConfigurations.getPlayerConfiguration(this);
        this.stickSimulation = new StickSimulation();
    }

    @Override
    public boolean isSpectator() {
        NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        NetworkPlayerInfo networkplayerinfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
    }

    public boolean hasPlayerInfo() {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    protected NetworkPlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getInstance().getConnection().getPlayerInfo(this.getUniqueID());
        }
        return this.playerInfo;
    }

    public boolean hasSkin() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
    }

    public boolean hasCustomCape() {
        AbstractClientPlayerEntity abstractClientPlayerEntity = this;
        if (abstractClientPlayerEntity instanceof ClientPlayerEntity) {
            ClientPlayerEntity player = (ClientPlayerEntity)abstractClientPlayerEntity;
            return player == IFastAccess.mc.player;
        }
        return false;
    }

    @Override
    protected void updateCape() {
        if (this.hasCustomCape()) {
            this.simulate(this);
        }
        super.updateCape();
    }

    public ResourceLocation getLocationSkin() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
    }

    @Nullable
    public ResourceLocation getLocationCape() {
        if (this.hasCustomCape()) {
            return new ResourceLocation("main/textures/images/cape.png");
        }
        if (!Config.isShowCapes()) {
            return null;
        }
        if (this.reloadCapeTimeMs != 0L && System.currentTimeMillis() > this.reloadCapeTimeMs) {
            CapeUtils.reloadCape(this);
            this.reloadCapeTimeMs = 0L;
        }
        if (this.locationOfCape != null) {
            return this.locationOfCape;
        }
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
    }

    public boolean isPlayerInfoSet() {
        return this.getPlayerInfo() != null;
    }

    @Nullable
    public ResourceLocation getLocationElytra() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
    }

    public static DownloadingTexture getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
        TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
        Texture texture = texturemanager.getTexture(resourceLocationIn);
        if (texture == null) {
            texture = new DownloadingTexture(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)), DefaultPlayerSkin.getDefaultSkin(AbstractClientPlayerEntity.getOfflineUUID(username)), true, null);
            texturemanager.loadTexture(resourceLocationIn, texture);
        }
        return (DownloadingTexture)texture;
    }

    public static ResourceLocation getLocationSkin(String username) {
        return new ResourceLocation("skins/" + String.valueOf(Hashing.sha1().hashUnencodedChars(StringUtils.stripControlCodes(username))));
    }

    public String getSkinType() {
        NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
    }

    public float getFovModifier() {
        float f = 1.0f;
        if (this.abilities.isFlying) {
            f *= 1.1f;
        }
        f = (float)((double)f * ((this.getAttributeValue(Attributes.MOVEMENT_SPEED) / (double)this.abilities.getWalkSpeed() + 1.0) / 2.0));
        if (this.abilities.getWalkSpeed() == 0.0f || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0f;
        }
        if (this.isHandActive() && this.getActiveItemStack().getItem() instanceof BowItem) {
            int i = this.getItemInUseMaxCount();
            float f1 = (float)i / 20.0f;
            f1 = f1 > 1.0f ? 1.0f : (f1 *= f1);
            f *= 1.0f - f1 * 0.15f;
        }
        return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, Float.valueOf(f)) : MathHelper.lerp(Minecraft.getInstance().gameSettings.fovScaleEffect, 1.0f, f);
    }

    public String getNameClear() {
        return this.nameClear;
    }

    public ResourceLocation getLocationOfCape() {
        return this.locationOfCape;
    }

    public void setLocationOfCape(ResourceLocation p_setLocationOfCape_1_) {
        this.locationOfCape = p_setLocationOfCape_1_;
    }

    @Override
    public StickSimulation getSimulation() {
        return this.stickSimulation;
    }

    public boolean hasElytraCape() {
        ResourceLocation resourcelocation = this.getLocationCape();
        if (resourcelocation == null) {
            return false;
        }
        return resourcelocation == this.locationOfCape ? this.elytraOfCape : true;
    }

    public void setElytraOfCape(boolean p_setElytraOfCape_1_) {
        this.elytraOfCape = p_setElytraOfCape_1_;
    }

    public boolean isElytraOfCape() {
        return this.elytraOfCape;
    }

    public long getReloadCapeTimeMs() {
        return this.reloadCapeTimeMs;
    }

    public void setReloadCapeTimeMs(long p_setReloadCapeTimeMs_1_) {
        this.reloadCapeTimeMs = p_setReloadCapeTimeMs_1_;
    }
}
