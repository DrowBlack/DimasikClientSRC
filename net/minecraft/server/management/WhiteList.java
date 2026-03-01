package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;
import net.minecraft.server.management.WhitelistEntry;

public class WhiteList
extends UserList<GameProfile, WhitelistEntry> {
    public WhiteList(File p_i1132_1_) {
        super(p_i1132_1_);
    }

    @Override
    protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
        return new WhitelistEntry(entryData);
    }

    public boolean isWhitelisted(GameProfile profile) {
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
