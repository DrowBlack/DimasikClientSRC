package dimasik.managers.mods.voicechat.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class UsernameCache {
    private static final ExecutorService SAVE_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("UsernameCacheSaver");
        thread.setDaemon(true);
        return thread;
    });
    private final File file;
    private final Gson gson;
    private Map<UUID, String> names;

    public UsernameCache(File file) {
        this.file = file;
        this.gson = new GsonBuilder().create();
        this.names = new ConcurrentHashMap<UUID, String>();
        this.load();
    }

    public void load() {
        if (!this.file.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(this.file);){
            Type usernamesType = new TypeToken<ConcurrentHashMap<UUID, String>>(){}.getType();
            this.names = (Map)this.gson.fromJson((Reader)reader, usernamesType);
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to load username cache", e);
        }
        if (this.names == null) {
            this.names = new ConcurrentHashMap<UUID, String>();
        }
    }

    public synchronized void save() {
        long time = System.currentTimeMillis();
        this.file.getParentFile().mkdirs();
        Set<UUID> volumeIds = VoicechatClient.VOLUME_CONFIG.getPlayerVolumes().keySet();
        Map<UUID, String> usernamesToSave = this.names.entrySet().stream().filter(entry -> volumeIds.contains(entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Voicechat.LOGGER.debug("Reduced cached usernames to save from {} to {}", this.names.size(), usernamesToSave.size());
        try (FileWriter writer = new FileWriter(this.file);){
            this.gson.toJson(usernamesToSave, (Appendable)writer);
            Voicechat.LOGGER.debug("Saved username cache in {}ms", System.currentTimeMillis() - time);
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to save username cache", e);
        }
    }

    public void saveAsync() {
        SAVE_EXECUTOR_SERVICE.execute(this::save);
    }

    @Nullable
    public String getUsername(UUID uuid) {
        return this.names.get(uuid);
    }

    public boolean has(UUID uuid) {
        return this.names.containsKey(uuid);
    }

    public void updateUsername(UUID uuid, String name) {
        this.names.put(uuid, name);
    }

    public void updateUsernameAndSave(UUID uuid, String name) {
        String oldName = this.names.get(uuid);
        if (!name.equals(oldName)) {
            this.names.put(uuid, name);
            if (VoicechatClient.VOLUME_CONFIG.contains(uuid)) {
                this.saveAsync();
            }
        }
    }
}
