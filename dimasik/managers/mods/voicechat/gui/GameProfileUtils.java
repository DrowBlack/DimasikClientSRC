package dimasik.managers.mods.voicechat.gui;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

public class GameProfileUtils {
    private static final Minecraft mc = Minecraft.getInstance();

    public static ResourceLocation getSkin(UUID uuid) {
        ClientPlayNetHandler connection = mc.getConnection();
        if (connection == null) {
            return DefaultPlayerSkin.getDefaultSkin(uuid);
        }
        NetworkPlayerInfo playerInfo = connection.getPlayerInfo(uuid);
        if (playerInfo == null) {
            return DefaultPlayerSkin.getDefaultSkin(uuid);
        }
        return playerInfo.getLocationSkin();
    }
}
