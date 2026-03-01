package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.server.management.UserListEntry;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class UserList<K, V extends UserListEntry<K>> {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final Gson field_232645_b_ = new GsonBuilder().setPrettyPrinting().create();
    private final File saveFile;
    private final Map<String, V> values = Maps.newHashMap();

    public UserList(File saveFile) {
        this.saveFile = saveFile;
    }

    public File getSaveFile() {
        return this.saveFile;
    }

    public void addEntry(V entry) {
        this.values.put(this.getObjectKey(((UserListEntry)entry).getValue()), entry);
        try {
            this.writeChanges();
        }
        catch (IOException ioexception) {
            LOGGER.warn("Could not save the list after adding a user.", (Throwable)ioexception);
        }
    }

    @Nullable
    public V getEntry(K obj) {
        this.removeExpired();
        return (V)((UserListEntry)this.values.get(this.getObjectKey(obj)));
    }

    public void removeEntry(K entry) {
        this.values.remove(this.getObjectKey(entry));
        try {
            this.writeChanges();
        }
        catch (IOException ioexception) {
            LOGGER.warn("Could not save the list after removing a user.", (Throwable)ioexception);
        }
    }

    public void removeEntry(UserListEntry<K> p_199042_1_) {
        this.removeEntry(p_199042_1_.getValue());
    }

    public String[] getKeys() {
        return this.values.keySet().toArray(new String[this.values.size()]);
    }

    public boolean isEmpty() {
        return this.values.size() < 1;
    }

    protected String getObjectKey(K obj) {
        return obj.toString();
    }

    protected boolean hasEntry(K entry) {
        return this.values.containsKey(this.getObjectKey(entry));
    }

    private void removeExpired() {
        ArrayList<Object> list = Lists.newArrayList();
        for (UserListEntry v : this.values.values()) {
            if (!v.hasBanExpired()) continue;
            list.add(v.getValue());
        }
        for (Object k : list) {
            this.values.remove(this.getObjectKey(k));
        }
    }

    protected abstract UserListEntry<K> createEntry(JsonObject var1);

    public Collection<V> getEntries() {
        return this.values.values();
    }

    public void writeChanges() throws IOException {
        JsonArray jsonarray = new JsonArray();
        this.values.values().stream().map(p_232646_0_ -> Util.make(new JsonObject(), p_232646_0_::onSerialization)).forEach(jsonarray::add);
        try (BufferedWriter bufferedwriter = Files.newWriter(this.saveFile, StandardCharsets.UTF_8);){
            field_232645_b_.toJson((JsonElement)jsonarray, (Appendable)bufferedwriter);
        }
    }

    public void readSavedFile() throws IOException {
        if (this.saveFile.exists()) {
            try (BufferedReader bufferedreader = Files.newReader(this.saveFile, StandardCharsets.UTF_8);){
                JsonArray jsonarray = field_232645_b_.fromJson((Reader)bufferedreader, JsonArray.class);
                this.values.clear();
                for (JsonElement jsonelement : jsonarray) {
                    JsonObject jsonobject = JSONUtils.getJsonObject(jsonelement, "entry");
                    UserListEntry<K> userlistentry = this.createEntry(jsonobject);
                    if (userlistentry.getValue() == null) continue;
                    this.values.put(this.getObjectKey(userlistentry.getValue()), userlistentry);
                }
            }
        }
    }
}
