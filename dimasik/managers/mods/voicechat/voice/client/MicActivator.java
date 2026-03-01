package dimasik.managers.mods.voicechat.voice.client;

import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class MicActivator {
    private boolean activating;
    private int deactivationDelay;
    @Nullable
    private short[] lastBuff;

    public boolean push(short[] audio, Consumer<short[]> audioConsumer) {
        boolean consumedAudio = false;
        boolean aboveThreshold = Utils.isAboveThreshold(audio, VoicechatClient.CLIENT_CONFIG.voiceActivationThreshold.get());
        if (this.activating) {
            if (!aboveThreshold) {
                if (this.deactivationDelay >= VoicechatClient.CLIENT_CONFIG.deactivationDelay.get()) {
                    this.stopActivating();
                } else {
                    audioConsumer.accept(audio);
                    consumedAudio = true;
                    ++this.deactivationDelay;
                }
            } else {
                audioConsumer.accept(audio);
                consumedAudio = true;
            }
        } else if (aboveThreshold) {
            if (this.lastBuff != null) {
                audioConsumer.accept(this.lastBuff);
            }
            audioConsumer.accept(audio);
            consumedAudio = true;
            this.activating = true;
        }
        this.lastBuff = consumedAudio ? null : audio;
        return consumedAudio;
    }

    public void stopActivating() {
        this.activating = false;
        this.deactivationDelay = 0;
        this.lastBuff = null;
    }

    public boolean isActivating() {
        return this.activating;
    }
}
