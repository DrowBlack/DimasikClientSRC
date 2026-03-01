package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DownloadingTexture;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

public class SkinManager {
    private final TextureManager textureManager;
    private final File skinCacheDir;
    private final MinecraftSessionService sessionService;
    private final LoadingCache<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> skinCacheLoader;

    public SkinManager(TextureManager textureManagerInstance, File skinCacheDirectory, final MinecraftSessionService sessionService) {
        this.textureManager = textureManagerInstance;
        this.skinCacheDir = skinCacheDirectory;
        this.sessionService = sessionService;
        this.skinCacheLoader = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS).build(new CacheLoader<String, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>>(){

            @Override
            public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(String p_load_1_) {
                GameProfile gameprofile = new GameProfile(null, "dummy_mcdummyface");
                gameprofile.getProperties().put("textures", new Property("textures", p_load_1_, ""));
                try {
                    return sessionService.getTextures(gameprofile, false);
                }
                catch (Throwable throwable) {
                    return ImmutableMap.of();
                }
            }
        });
    }

    public ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type textureType) {
        return this.loadSkin(profileTexture, textureType, null);
    }

    private ResourceLocation loadSkin(MinecraftProfileTexture profileTexture, MinecraftProfileTexture.Type textureType, @Nullable ISkinAvailableCallback skinAvailableCallback) {
        String s = Hashing.sha1().hashUnencodedChars(profileTexture.getHash()).toString();
        ResourceLocation resourcelocation = new ResourceLocation("skins/" + s);
        Texture texture = this.textureManager.getTexture(resourcelocation);
        if (texture != null) {
            if (skinAvailableCallback != null) {
                skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
            }
        } else {
            File file1 = new File(this.skinCacheDir, s.length() > 2 ? s.substring(0, 2) : "xx");
            File file2 = new File(file1, s);
            DownloadingTexture downloadingtexture = new DownloadingTexture(file2, profileTexture.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), textureType == MinecraftProfileTexture.Type.SKIN, () -> {
                if (skinAvailableCallback != null) {
                    skinAvailableCallback.onSkinTextureAvailable(textureType, resourcelocation, profileTexture);
                }
            });
            this.textureManager.loadTexture(resourcelocation, downloadingtexture);
        }
        return resourcelocation;
    }

    public void loadProfileTextures(GameProfile profile, ISkinAvailableCallback skinAvailableCallback, boolean requireSecure) {
        Runnable runnable = () -> {
            HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Maps.newHashMap();
            try {
                map.putAll(this.sessionService.getTextures(profile, requireSecure));
            }
            catch (InsecureTextureException insecureTextureException) {
                // empty catch block
            }
            if (map.isEmpty()) {
                profile.getProperties().clear();
                if (profile.getId().equals(Minecraft.getInstance().getSession().getProfile().getId())) {
                    profile.getProperties().putAll(Minecraft.getInstance().getProfileProperties());
                    map.putAll(this.sessionService.getTextures(profile, false));
                } else {
                    this.sessionService.fillProfileProperties(profile, requireSecure);
                    try {
                        map.putAll(this.sessionService.getTextures(profile, requireSecure));
                    }
                    catch (InsecureTextureException insecureTextureException) {
                        // empty catch block
                    }
                }
            }
            Minecraft.getInstance().execute(() -> RenderSystem.recordRenderCall(() -> ImmutableList.of(MinecraftProfileTexture.Type.SKIN, MinecraftProfileTexture.Type.CAPE).forEach(p_229296_3_ -> {
                if (map.containsKey(p_229296_3_)) {
                    this.loadSkin((MinecraftProfileTexture)map.get(p_229296_3_), (MinecraftProfileTexture.Type)((Object)((Object)((Object)((Object)p_229296_3_)))), skinAvailableCallback);
                }
            })));
        };
        Util.getServerExecutor().execute(runnable);
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile) {
        Property property = Iterables.getFirst(profile.getProperties().get("textures"), null);
        return property == null ? ImmutableMap.of() : this.skinCacheLoader.getUnchecked(property.getValue());
    }

    public static interface ISkinAvailableCallback {
        public void onSkinTextureAvailable(MinecraftProfileTexture.Type var1, ResourceLocation var2, MinecraftProfileTexture var3);
    }
}
