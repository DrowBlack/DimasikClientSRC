package dimasik.managers.mods.voicechat.api.audiochannel;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.audiochannel.AudioChannel;

public interface LocationalAudioChannel
extends AudioChannel {
    public void updateLocation(Position var1);

    public Position getLocation();

    public float getDistance();

    public void setDistance(float var1);
}
