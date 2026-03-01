package dimasik.managers.mods.voicechat.api;

import dimasik.managers.mods.voicechat.api.Group;
import dimasik.managers.mods.voicechat.api.ServerPlayer;
import javax.annotation.Nullable;

public interface VoicechatConnection {
    @Nullable
    public Group getGroup();

    public boolean isInGroup();

    public void setGroup(@Nullable Group var1);

    public boolean isConnected();

    public void setConnected(boolean var1);

    public boolean isDisabled();

    public void setDisabled(boolean var1);

    public boolean isInstalled();

    public ServerPlayer getPlayer();
}
