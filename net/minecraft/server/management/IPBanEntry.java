package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.server.management.BanEntry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class IPBanEntry
extends BanEntry<String> {
    public IPBanEntry(String valueIn) {
        this(valueIn, (Date)null, (String)null, (Date)null, (String)null);
    }

    public IPBanEntry(String valueIn, @Nullable Date startDate, @Nullable String banner, @Nullable Date endDate, @Nullable String banReason) {
        super(valueIn, startDate, banner, endDate, banReason);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent((String)this.getValue());
    }

    public IPBanEntry(JsonObject json) {
        super(IPBanEntry.getIPFromJson(json), json);
    }

    private static String getIPFromJson(JsonObject json) {
        return json.has("ip") ? json.get("ip").getAsString() : null;
    }

    @Override
    protected void onSerialization(JsonObject data) {
        if (this.getValue() != null) {
            data.addProperty("ip", (String)this.getValue());
            super.onSerialization(data);
        }
    }
}
