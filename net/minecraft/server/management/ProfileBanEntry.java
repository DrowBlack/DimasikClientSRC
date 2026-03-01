package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.management.BanEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ProfileBanEntry
extends BanEntry<GameProfile> {
    public ProfileBanEntry(GameProfile profile) {
        this(profile, (Date)null, (String)null, (Date)null, (String)null);
    }

    public ProfileBanEntry(GameProfile profile, @Nullable Date startDate, @Nullable String banner, @Nullable Date endDate, @Nullable String banReason) {
        super(profile, startDate, banner, endDate, banReason);
    }

    public ProfileBanEntry(JsonObject json) {
        super(ProfileBanEntry.toGameProfile(json), json);
    }

    @Override
    protected void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("uuid", ((GameProfile)this.getValue()).getId() == null ? "" : ((GameProfile)this.getValue()).getId().toString());
            data.addProperty("name", ((GameProfile)this.getValue()).getName());
            super.onSerialization(data);
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        GameProfile gameprofile = (GameProfile)this.getValue();
        return new StringTextComponent(gameprofile.getName() != null ? gameprofile.getName() : Objects.toString(gameprofile.getId(), "(Unknown)"));
    }

    private static GameProfile toGameProfile(JsonObject json) {
        if (json.has("uuid") && json.has("name")) {
            UUID uuid;
            String s = json.get("uuid").getAsString();
            try {
                uuid = UUID.fromString(s);
            }
            catch (Throwable throwable) {
                return null;
            }
            return new GameProfile(uuid, json.get("name").getAsString());
        }
        return null;
    }
}
