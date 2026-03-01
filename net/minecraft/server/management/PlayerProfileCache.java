package net.minecraft.server.management;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerProfileCache {
    private static final Logger field_242114_a = LogManager.getLogger();
    private static boolean onlineMode;
    private final Map<String, ProfileEntry> usernameToProfileEntryMap = Maps.newConcurrentMap();
    private final Map<UUID, ProfileEntry> uuidToProfileEntryMap = Maps.newConcurrentMap();
    private final GameProfileRepository profileRepo;
    private final Gson gson = new GsonBuilder().create();
    private final File usercacheFile;
    private final AtomicLong field_242115_h = new AtomicLong();

    public PlayerProfileCache(GameProfileRepository profileRepoIn, File usercacheFileIn) {
        this.profileRepo = profileRepoIn;
        this.usercacheFile = usercacheFileIn;
        Lists.reverse(this.func_242116_a()).forEach(this::func_242118_a);
    }

    private void func_242118_a(ProfileEntry p_242118_1_) {
        UUID uuid;
        GameProfile gameprofile = p_242118_1_.getGameProfile();
        p_242118_1_.func_242126_a(this.func_242123_d());
        String s = gameprofile.getName();
        if (s != null) {
            this.usernameToProfileEntryMap.put(s.toLowerCase(Locale.ROOT), p_242118_1_);
        }
        if ((uuid = gameprofile.getId()) != null) {
            this.uuidToProfileEntryMap.put(uuid, p_242118_1_);
        }
    }

    @Nullable
    private static GameProfile lookupProfile(GameProfileRepository profileRepoIn, String name) {
        final AtomicReference atomicreference = new AtomicReference();
        ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback(){

            @Override
            public void onProfileLookupSucceeded(GameProfile p_onProfileLookupSucceeded_1_) {
                atomicreference.set(p_onProfileLookupSucceeded_1_);
            }

            @Override
            public void onProfileLookupFailed(GameProfile p_onProfileLookupFailed_1_, Exception p_onProfileLookupFailed_2_) {
                atomicreference.set(null);
            }
        };
        profileRepoIn.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, profilelookupcallback);
        GameProfile gameprofile = (GameProfile)atomicreference.get();
        if (!PlayerProfileCache.isOnlineMode() && gameprofile == null) {
            UUID uuid = PlayerEntity.getUUID(new GameProfile(null, name));
            gameprofile = new GameProfile(uuid, name);
        }
        return gameprofile;
    }

    public static void setOnlineMode(boolean onlineModeIn) {
        onlineMode = onlineModeIn;
    }

    private static boolean isOnlineMode() {
        return onlineMode;
    }

    public void addEntry(GameProfile gameProfile) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(2, 1);
        Date date = calendar.getTime();
        ProfileEntry playerprofilecache$profileentry = new ProfileEntry(gameProfile, date);
        this.func_242118_a(playerprofilecache$profileentry);
        this.save();
    }

    private long func_242123_d() {
        return this.field_242115_h.incrementAndGet();
    }

    @Nullable
    public GameProfile getGameProfileForUsername(String username) {
        GameProfile gameprofile;
        String s = username.toLowerCase(Locale.ROOT);
        ProfileEntry playerprofilecache$profileentry = this.usernameToProfileEntryMap.get(s);
        boolean flag = false;
        if (playerprofilecache$profileentry != null && new Date().getTime() >= playerprofilecache$profileentry.expirationDate.getTime()) {
            this.uuidToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getId());
            this.usernameToProfileEntryMap.remove(playerprofilecache$profileentry.getGameProfile().getName().toLowerCase(Locale.ROOT));
            flag = true;
            playerprofilecache$profileentry = null;
        }
        if (playerprofilecache$profileentry != null) {
            playerprofilecache$profileentry.func_242126_a(this.func_242123_d());
            gameprofile = playerprofilecache$profileentry.getGameProfile();
        } else {
            gameprofile = PlayerProfileCache.lookupProfile(this.profileRepo, s);
            if (gameprofile != null) {
                this.addEntry(gameprofile);
                flag = false;
            }
        }
        if (flag) {
            this.save();
        }
        return gameprofile;
    }

    @Nullable
    public GameProfile getProfileByUUID(UUID uuid) {
        ProfileEntry playerprofilecache$profileentry = this.uuidToProfileEntryMap.get(uuid);
        if (playerprofilecache$profileentry == null) {
            return null;
        }
        playerprofilecache$profileentry.func_242126_a(this.func_242123_d());
        return playerprofilecache$profileentry.getGameProfile();
    }

    private static DateFormat func_242124_e() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public List<ProfileEntry> func_242116_a() {
        ArrayList<ProfileEntry> list = Lists.newArrayList();
        try (BufferedReader reader2222 = Files.newReader(this.usercacheFile, StandardCharsets.UTF_8);){
            JsonArray jsonarray = this.gson.fromJson((Reader)reader2222, JsonArray.class);
            if (jsonarray == null) {
                ArrayList<ProfileEntry> arrayList = list;
                return arrayList;
            }
            DateFormat dateformat = PlayerProfileCache.func_242124_e();
            jsonarray.forEach(p_242122_2_ -> {
                ProfileEntry playerprofilecache$profileentry = PlayerProfileCache.func_242121_a(p_242122_2_, dateformat);
                if (playerprofilecache$profileentry != null) {
                    list.add(playerprofilecache$profileentry);
                }
            });
            return list;
        }
        catch (FileNotFoundException reader2222) {
            return list;
        }
        catch (JsonParseException | IOException ioexception) {
            field_242114_a.warn("Failed to load profile cache {}", (Object)this.usercacheFile, (Object)ioexception);
        }
        return list;
    }

    public void save() {
        JsonArray jsonarray = new JsonArray();
        DateFormat dateformat = PlayerProfileCache.func_242124_e();
        this.func_242117_a(1000).forEach(p_242120_2_ -> jsonarray.add(PlayerProfileCache.func_242119_a(p_242120_2_, dateformat)));
        String s = this.gson.toJson(jsonarray);
        try (BufferedWriter writer = Files.newWriter(this.usercacheFile, StandardCharsets.UTF_8);){
            writer.write(s);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private Stream<ProfileEntry> func_242117_a(int p_242117_1_) {
        return ImmutableList.copyOf(this.uuidToProfileEntryMap.values()).stream().sorted(Comparator.comparing(ProfileEntry::func_242128_c).reversed()).limit(p_242117_1_);
    }

    private static JsonElement func_242119_a(ProfileEntry p_242119_0_, DateFormat p_242119_1_) {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("name", p_242119_0_.getGameProfile().getName());
        UUID uuid = p_242119_0_.getGameProfile().getId();
        jsonobject.addProperty("uuid", uuid == null ? "" : uuid.toString());
        jsonobject.addProperty("expiresOn", p_242119_1_.format(p_242119_0_.getExpirationDate()));
        return jsonobject;
    }

    @Nullable
    private static ProfileEntry func_242121_a(JsonElement p_242121_0_, DateFormat p_242121_1_) {
        if (p_242121_0_.isJsonObject()) {
            JsonObject jsonobject = p_242121_0_.getAsJsonObject();
            JsonElement jsonelement = jsonobject.get("name");
            JsonElement jsonelement1 = jsonobject.get("uuid");
            JsonElement jsonelement2 = jsonobject.get("expiresOn");
            if (jsonelement != null && jsonelement1 != null) {
                String s = jsonelement1.getAsString();
                String s1 = jsonelement.getAsString();
                Date date = null;
                if (jsonelement2 != null) {
                    try {
                        date = p_242121_1_.parse(jsonelement2.getAsString());
                    }
                    catch (ParseException parseException) {
                        // empty catch block
                    }
                }
                if (s1 != null && s != null && date != null) {
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(s);
                    }
                    catch (Throwable throwable) {
                        return null;
                    }
                    return new ProfileEntry(new GameProfile(uuid, s1), date);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    static class ProfileEntry {
        private final GameProfile gameProfile;
        private final Date expirationDate;
        private volatile long field_242125_c;

        private ProfileEntry(GameProfile p_i241888_1_, Date p_i241888_2_) {
            this.gameProfile = p_i241888_1_;
            this.expirationDate = p_i241888_2_;
        }

        public GameProfile getGameProfile() {
            return this.gameProfile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void func_242126_a(long p_242126_1_) {
            this.field_242125_c = p_242126_1_;
        }

        public long func_242128_c() {
            return this.field_242125_c;
        }
    }
}
