package dimasik.managers.client;

import dimasik.Load;
import dimasik.events.main.chat.EventTranslate;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.config.main.ClientConfig;
import dimasik.managers.hook.main.ModuleManagers;
import dimasik.managers.module.Module;
import dimasik.modules.misc.UnHook;
import dimasik.utils.math.RandomNumberUtils;
import java.io.File;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.client.GameConfiguration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.StringUtils;
import net.optifine.shaders.Shaders;

public final class ClientManagers
implements IFastAccess {
    private static final RandomNumberUtils randomNumber = new RandomNumberUtils(1000.0f, 9999.0f, 1.0f);
    private static String random = "";
    private static boolean unHook = false;
    private static String language = "eng";
    private static boolean pvpMode;
    private static UUID uuid;
    public static final File files;
    private static boolean changed;

    public static void changeLanguage(String name) {
        ClientManagers.setLanguage(name);
        EventTranslate eventTranslate = new EventTranslate();
        Load.getInstance().getEvents().call(eventTranslate);
        Load.getInstance().getUiScreen().translate();
        Load.getInstance().getHooks().getDraggableController().translate();
    }

    public static void update() {
        ClientManagers.getRandomNumber().generate();
        random = String.format("%.0f", Float.valueOf(ClientManagers.getRandomNumber().getCurrent()));
    }

    public static void updateBossInfo(SUpdateBossInfoPacket packet) {
        if (packet.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            if (StringUtils.stripControlCodes(packet.getName().getString()).toLowerCase().contains("pvp")) {
                pvpMode = true;
                uuid = packet.getUniqueId();
            }
        } else if (packet.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE && packet.getUniqueId().equals(uuid)) {
            pvpMode = false;
        }
    }

    public static void unhook() {
        ClientManagers.setUnHook(false);
        ModuleManagers modules = Load.getInstance().getHooks().getModuleManagers();
        for (Module module : modules) {
            if (!((UnHook)modules.findClass(UnHook.class)).getBackup().contains(module)) continue;
            module.toggle();
        }
        Minecraft.getInstance().fileResourcepacks = GameConfiguration.gameConfiguration.folderInfo.resourcePacksDir;
        Shaders.shaderPacksDir = new File(Minecraft.getInstance().gameDir, "shaderpacks");
        ((ClientConfig)Load.getInstance().getHooks().getConfigManagers().findClass(ClientConfig.class)).fastSave();
    }

    public static boolean isConnectedToServer(String ip) {
        return mc.getCurrentServerData() != null && ClientManagers.mc.getCurrentServerData().serverIP != null && ClientManagers.mc.getCurrentServerData().serverIP.contains(ip);
    }

    public static boolean isConnect44BPSServer() {
        return mc.getCurrentServerData() != null && ClientManagers.mc.getCurrentServerData().serverIP != null && ClientManagers.mc.getCurrentServerData().serverIP.contains("bravo");
    }

    public static boolean isConnect46BPSServer() {
        return mc.getCurrentServerData() != null && ClientManagers.mc.getCurrentServerData().serverIP != null && (ClientManagers.mc.getCurrentServerData().serverIP.contains("cakeworld") || ClientManagers.mc.getCurrentServerData().serverIP.contains("freakworld") || ClientManagers.mc.getCurrentServerData().serverIP.contains("slimeworld"));
    }

    public static boolean isConnect50BPSServer() {
        return mc.getCurrentServerData() != null && ClientManagers.mc.getCurrentServerData().serverIP != null && ClientManagers.mc.getCurrentServerData().serverIP.contains("lonygrief");
    }

    public static boolean isHolyWorld() {
        return ClientManagers.isConnectedToServer("holyworld");
    }

    public static boolean isReallyWorld() {
        return ClientManagers.isConnectedToServer("reallyworld");
    }

    public static boolean isFuntime() {
        return ClientManagers.isConnectedToServer("funtime");
    }

    public static boolean cake() {
        return ClientManagers.isConnectedToServer("cakeworld");
    }

    @Generated
    private ClientManagers() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Generated
    public static RandomNumberUtils getRandomNumber() {
        return randomNumber;
    }

    @Generated
    public static String getRandom() {
        return random;
    }

    @Generated
    public static void setRandom(String random) {
        ClientManagers.random = random;
    }

    @Generated
    public static void setUnHook(boolean unHook) {
        ClientManagers.unHook = unHook;
    }

    @Generated
    public static boolean isUnHook() {
        return unHook;
    }

    @Generated
    public static String getLanguage() {
        return language;
    }

    @Generated
    public static void setLanguage(String language) {
        ClientManagers.language = language;
    }

    @Generated
    public static void setPvpMode(boolean pvpMode) {
        ClientManagers.pvpMode = pvpMode;
    }

    @Generated
    public static boolean isPvpMode() {
        return pvpMode;
    }

    @Generated
    public static UUID getUuid() {
        return uuid;
    }

    @Generated
    public static void setUuid(UUID uuid) {
        ClientManagers.uuid = uuid;
    }

    @Generated
    public static File getFiles() {
        return files;
    }

    @Generated
    public static boolean isChanged() {
        return changed;
    }

    @Generated
    public static void setChanged(boolean changed) {
        ClientManagers.changed = changed;
    }

    static {
        files = new File(String.valueOf(Minecraft.getInstance().gameDir));
        changed = false;
    }
}
