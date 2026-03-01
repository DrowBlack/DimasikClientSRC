package net.optifine.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpUtils;
import net.optifine.player.PlayerConfiguration;
import net.optifine.player.PlayerItemModel;
import net.optifine.player.PlayerItemParser;
import net.optifine.util.Json;

public class PlayerConfigurationParser {
    private String player = null;
    public static final String CONFIG_ITEMS = "items";
    public static final String ITEM_TYPE = "type";
    public static final String ITEM_ACTIVE = "active";

    public PlayerConfigurationParser(String player) {
        this.player = player;
    }

    public PlayerConfiguration parsePlayerConfiguration(JsonElement je) {
        if (je == null) {
            throw new JsonParseException("JSON object is null, player: " + this.player);
        }
        JsonObject jsonobject = (JsonObject)je;
        PlayerConfiguration playerconfiguration = new PlayerConfiguration();
        JsonArray jsonarray = (JsonArray)jsonobject.get(CONFIG_ITEMS);
        if (jsonarray != null) {
            for (int i = 0; i < jsonarray.size(); ++i) {
                PlayerItemModel playeritemmodel;
                JsonObject jsonobject1 = (JsonObject)jsonarray.get(i);
                boolean flag = Json.getBoolean(jsonobject1, ITEM_ACTIVE, true);
                if (!flag) continue;
                String s = Json.getString(jsonobject1, ITEM_TYPE);
                if (s == null) {
                    Config.warn("Item type is null, player: " + this.player);
                    continue;
                }
                Object s1 = Json.getString(jsonobject1, "model");
                if (s1 == null) {
                    s1 = "items/" + s + "/model.cfg";
                }
                if ((playeritemmodel = this.downloadModel((String)s1)) == null) continue;
                if (!playeritemmodel.isUsePlayerTexture()) {
                    NativeImage nativeimage;
                    Object s2 = Json.getString(jsonobject1, "texture");
                    if (s2 == null) {
                        s2 = "items/" + s + "/users/" + this.player + ".png";
                    }
                    if ((nativeimage = this.downloadTextureImage((String)s2)) == null) continue;
                    playeritemmodel.setTextureImage(nativeimage);
                    ResourceLocation resourcelocation = new ResourceLocation("optifine.net", (String)s2);
                    playeritemmodel.setTextureLocation(resourcelocation);
                }
                playerconfiguration.addPlayerItemModel(playeritemmodel);
            }
        }
        return playerconfiguration;
    }

    private NativeImage downloadTextureImage(String texturePath) {
        String s = HttpUtils.getPlayerItemsUrl() + "/" + texturePath;
        try {
            byte[] abyte = HttpPipeline.get(s, Minecraft.getInstance().getProxy());
            return NativeImage.read(new ByteArrayInputStream(abyte));
        }
        catch (IOException ioexception) {
            Config.warn("Error loading item texture " + texturePath + ": " + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            return null;
        }
    }

    private PlayerItemModel downloadModel(String modelPath) {
        String s = HttpUtils.getPlayerItemsUrl() + "/" + modelPath;
        try {
            byte[] abyte = HttpPipeline.get(s, Minecraft.getInstance().getProxy());
            String s1 = new String(abyte, "ASCII");
            JsonParser jsonparser = new JsonParser();
            JsonObject jsonobject = (JsonObject)jsonparser.parse(s1);
            return PlayerItemParser.parseItemModel(jsonobject);
        }
        catch (Exception exception) {
            Config.warn("Error loading item model " + modelPath + ": " + exception.getClass().getName() + ": " + exception.getMessage());
            return null;
        }
    }
}
