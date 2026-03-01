package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListEntry;

public class OpList
extends UserList<GameProfile, OpEntry> {
    public OpList(File saveFile) {
        super(saveFile);
    }

    @Override
    protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
        return new OpEntry(entryData);
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

    public boolean bypassesPlayerLimit(GameProfile profile) {
        OpEntry opentry = (OpEntry)this.getEntry(profile);
        return opentry != null ? opentry.bypassesPlayerLimit() : false;
    }

    @Override
    protected String getObjectKey(GameProfile obj) {
        return obj.getId().toString();
    }
}
