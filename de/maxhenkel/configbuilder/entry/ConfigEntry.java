package de.maxhenkel.configbuilder.entry;

import de.maxhenkel.configbuilder.Config;

public interface ConfigEntry<T> {
    public ConfigEntry<T> comment(String ... var1);

    public String[] getComments();

    public T get();

    public ConfigEntry<T> set(T var1);

    public String getKey();

    public ConfigEntry<T> reset();

    public ConfigEntry<T> save();

    public ConfigEntry<T> saveSync();

    public T getDefault();

    public Config getConfig();
}
