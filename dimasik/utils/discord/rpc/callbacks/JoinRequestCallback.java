package dimasik.utils.discord.rpc.callbacks;

import com.sun.jna.Callback;
import dimasik.utils.discord.rpc.utils.DiscordUser;

public interface JoinRequestCallback
extends Callback {
    public void apply(DiscordUser var1);
}
