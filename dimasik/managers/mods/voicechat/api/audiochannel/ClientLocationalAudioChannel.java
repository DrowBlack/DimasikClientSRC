package dimasik.managers.mods.voicechat.api.audiochannel;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.audiochannel.ClientAudioChannel;

public interface ClientLocationalAudioChannel
extends ClientAudioChannel {
    public Position getLocation();

    public void setLocation(Position var1);

    public float getDistance();

    public void setDistance(float var1);
}
