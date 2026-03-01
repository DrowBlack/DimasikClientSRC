package dimasik.utils.discord.rpc;

import dimasik.Load;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.managers.client.ClientManagers;
import dimasik.utils.discord.rpc.utils.DiscordEventHandlers;
import dimasik.utils.discord.rpc.utils.DiscordRPC;
import dimasik.utils.discord.rpc.utils.DiscordRichPresence;
import dimasik.utils.discord.rpc.utils.RPCButton;
import lombok.Generated;
import ru.dreamix.class2native.CompileNativeCalls;
import ru.dreamix.protection.NativeAPI;

public class DiscordManager
implements IFastAccess {
    private DiscordDaemonThread discordDaemonThread;
    private long APPLICATION_ID;
    private boolean running;
    private long startTimestamp;
    private String image;
    private String telegram;
    private String discord;

    public DiscordManager() {
        this.init();
    }

    @CompileNativeCalls
    private void cppInit() {
        this.discordDaemonThread = new DiscordDaemonThread();
        this.APPLICATION_ID = 1370788642234175508L;
        this.running = true;
        this.image = "https://s14.gifyu.com/images/bsufQ.gif";
        this.telegram = "https://t.me/dimasikdlc";
        this.discord = "https://discord.com/invite/ng2ShcwR5t";
    }

    public String serverOrSingle() {
        return "Menu";
    }

    public String smallImages() {
        if (mc.getCurrentServerData() != null && ClientManagers.isReallyWorld()) {
            return "https://s4.gifyu.com/images/bsSAB.png";
        }
        if (mc.getCurrentServerData() != null && ClientManagers.isHolyWorld()) {
            return "https://s14.gifyu.com/images/bsnZ1.png";
        }
        if (mc.getCurrentServerData() != null && ClientManagers.isFuntime()) {
            return "https://s14.gifyu.com/images/bsnF7.png";
        }
        if (mc.getCurrentServerData() != null && ClientManagers.cake()) {
            return "https://s4.gifyu.com/images/bsSAB.png";
        }
        return "";
    }

    public String opisanie() {
        if (mc.getCurrentServerData() != null) {
            ClientManagers.isConnectedToServer("metahvh");
        }
        return "https://dimasclient.fun/";
    }

    @CompileNativeCalls
    public void init() {
        this.cppInit();
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();
        DiscordRPC.INSTANCE.Discord_Initialize(String.valueOf(this.APPLICATION_ID), handlers, true, "");
        this.startTimestamp = System.currentTimeMillis() / 1000L;
        builder.setStartTimestamp(this.startTimestamp);
        builder.setDetails("Playing: " + this.serverOrSingle());
        builder.setLargeImage(this.image, this.opisanie());
        builder.setSmallImage(this.smallImages());
        builder.setButtons(RPCButton.create("Telegram", this.telegram), RPCButton.create("Discord", this.discord));
        DiscordRPC.INSTANCE.Discord_UpdatePresence(builder.build());
        this.discordDaemonThread.start();
    }

    public void updateRPC() {
        DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder();
        builder.setStartTimestamp(this.startTimestamp);
        builder.setDetails("Playing: " + this.serverOrSingle());
        builder.setState("User: " + NativeAPI.getUserName() + " | Uid: " + NativeAPI.getUserIdentifier());
        builder.setLargeImage(this.image, this.opisanie());
        builder.setSmallImage(this.smallImages());
        builder.setButtons(RPCButton.create("Telegram", this.telegram), RPCButton.create("Discord", this.discord));
        DiscordRPC.INSTANCE.Discord_UpdatePresence(builder.build());
    }

    public void stopRPC() {
        DiscordRPC.INSTANCE.Discord_Shutdown();
        this.discordDaemonThread.interrupt();
        this.running = false;
    }

    @Generated
    public DiscordDaemonThread getDiscordDaemonThread() {
        return this.discordDaemonThread;
    }

    @Generated
    public long getAPPLICATION_ID() {
        return this.APPLICATION_ID;
    }

    @Generated
    public boolean isRunning() {
        return this.running;
    }

    @Generated
    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    @Generated
    public String getImage() {
        return this.image;
    }

    @Generated
    public String getTelegram() {
        return this.telegram;
    }

    @Generated
    public String getDiscord() {
        return this.discord;
    }

    private class DiscordDaemonThread
    extends Thread {
        private DiscordDaemonThread() {
        }

        @Override
        public void run() {
            this.setName("Discord-RPC");
            try {
                while (Load.getInstance().getDiscordManager().isRunning()) {
                    DiscordManager.this.updateRPC();
                    DiscordRPC.INSTANCE.Discord_RunCallbacks();
                    Thread.sleep(1000L);
                }
            }
            catch (Exception e) {
                DiscordManager.this.stopRPC();
            }
            super.run();
        }
    }
}
