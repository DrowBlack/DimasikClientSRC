package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.management.UserListEntry;

public class OpEntry
extends UserListEntry<GameProfile> {
    private final int permissionLevel;
    private final boolean bypassesPlayerLimit;

    public OpEntry(GameProfile player, int permissionLevelIn, boolean bypassesPlayerLimitIn) {
        super(player);
        this.permissionLevel = permissionLevelIn;
        this.bypassesPlayerLimit = bypassesPlayerLimitIn;
    }

    public OpEntry(JsonObject p_i1150_1_) {
        super(OpEntry.constructProfile(p_i1150_1_));
        this.permissionLevel = p_i1150_1_.has("level") ? p_i1150_1_.get("level").getAsInt() : 0;
        this.bypassesPlayerLimit = p_i1150_1_.has("bypassesPlayerLimit") && p_i1150_1_.get("bypassesPlayerLimit").getAsBoolean();
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public boolean bypassesPlayerLimit() {
        return this.bypassesPlayerLimit;
    }

    @Override
    protected void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile)this.getValue()).getName());
            data.addProperty("level", this.permissionLevel);
            data.addProperty("bypassesPlayerLimit", this.bypassesPlayerLimit);
        }
    }

    private static GameProfile constructProfile(JsonObject p_152643_0_) {
        if (p_152643_0_.has("uuid") && p_152643_0_.has("name")) {
            UUID uuid;
            String s = p_152643_0_.get("uuid").getAsString();
            try {
                uuid = UUID.fromString(s);
            }
            catch (Throwable throwable) {
                return null;
            }
            return new GameProfile(uuid, p_152643_0_.get("name").getAsString());
        }
        return null;
    }
}
