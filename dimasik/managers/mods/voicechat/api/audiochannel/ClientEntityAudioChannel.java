package dimasik.managers.mods.voicechat.api.audiochannel;

import dimasik.managers.mods.voicechat.api.audiochannel.ClientAudioChannel;

public interface ClientEntityAudioChannel
extends ClientAudioChannel {
    public void setWhispering(boolean var1);

    public boolean isWhispering();

    public float getDistance();

    public void setDistance(float var1);
}
