package dimasik.utils.discord.rpc.utils;

import com.sun.jna.Library;
import com.sun.jna.Native;
import dimasik.utils.discord.rpc.utils.DiscordEventHandlers;
import dimasik.utils.discord.rpc.utils.DiscordRichPresence;

public interface DiscordRPC
extends Library {
    public static final DiscordRPC INSTANCE = Native.loadLibrary("discord-rpc", DiscordRPC.class);

    public void Discord_UpdateHandlers(DiscordEventHandlers var1);

    public void Discord_UpdatePresence(DiscordRichPresence var1);

    public void Discord_Respond(String var1, int var2);

    public void Discord_Register(String var1, String var2);

    public void Discord_Shutdown();

    public void Discord_UpdateConnection();

    public void Discord_RegisterSteamGame(String var1, String var2);

    public void Discord_RunCallbacks();

    public void Discord_Initialize(String var1, DiscordEventHandlers var2, boolean var3, String var4);

    public void Discord_ClearPresence();

    public static enum DiscordReply {
        NO(0),
        IGNORE(2),
        YES(1);

        public final int reply;

        private DiscordReply(int reply) {
            this.reply = reply;
        }

        private static DiscordReply[] getReplies() {
            return new DiscordReply[]{NO, YES, IGNORE};
        }
    }
}
