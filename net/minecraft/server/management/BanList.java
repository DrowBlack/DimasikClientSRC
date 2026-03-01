package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.management.ProfileBanEntry;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;

public class BanList
extends UserList<GameProfile, ProfileBanEntry> {
    public BanList(File bansFile) {
        super(bansFile);
    }

    @Override
    protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
        return new ProfileBanEntry(entryData);
    }

    public boolean isBanned(GameProfile profile) {
        return this.hasEntry(profile);
    }

    @Override
    public String[] getKeys() {
        String[] astring = new String[this.getEntries().size()];
        int i = 0;
        for (UserListEntry userlistentry : this.getEntries()) {
            astring[i++] = ((GameProfile)userlistentry.getValue()).getName();
        }
        return astring;
    }

    @Override
    protected String getObjectKey(GameProfile obj) {
        return obj.getId().toString();
    }
}
