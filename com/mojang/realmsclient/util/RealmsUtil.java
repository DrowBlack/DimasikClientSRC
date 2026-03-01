package com.mojang.realmsclient.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;

public class RealmsUtil {
    private static final YggdrasilAuthenticationService field_225195_b = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy());
    private static final MinecraftSessionService field_225196_c = field_225195_b.createMinecraftSessionService();
    public static LoadingCache<String, GameProfile> field_225194_a = CacheBuilder.newBuilder().expireAfterWrite(60L, TimeUnit.MINUTES).build(new CacheLoader<String, GameProfile>(){

        @Override
        public GameProfile load(String p_load_1_) throws Exception {
            GameProfile gameprofile = field_225196_c.fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(p_load_1_), null), false);
            if (gameprofile == null) {
                throw new Exception("Couldn't get profile");
            }
            return gameprofile;
        }
    });

    public static String func_225193_a(String p_225193_0_) throws Exception {
        GameProfile gameprofile = field_225194_a.get(p_225193_0_);
        return gameprofile.getName();
    }

    public static Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> func_225191_b(String p_225191_0_) {
        try {
            GameProfile gameprofile = field_225194_a.get(p_225191_0_);
            return field_225196_c.getTextures(gameprofile, false);
        }
        catch (Exception exception) {
            return Maps.newHashMap();
        }
    }

    public static String func_225192_a(long p_225192_0_) {
        if (p_225192_0_ < 0L) {
            return "right now";
        }
        long i = p_225192_0_ / 1000L;
        if (i < 60L) {
            return (String)(i == 1L ? "1 second" : i + " seconds") + " ago";
        }
        if (i < 3600L) {
            long l = i / 60L;
            return (String)(l == 1L ? "1 minute" : l + " minutes") + " ago";
        }
        if (i < 86400L) {
            long k = i / 3600L;
            return (String)(k == 1L ? "1 hour" : k + " hours") + " ago";
        }
        long j = i / 86400L;
        return (String)(j == 1L ? "1 day" : j + " days") + " ago";
    }

    public static String func_238105_a_(Date p_238105_0_) {
        return RealmsUtil.func_225192_a(System.currentTimeMillis() - p_238105_0_.getTime());
    }
}
