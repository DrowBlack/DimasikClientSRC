package dimasik.managers.mods.voicechat.plugins.impl.events;

import dimasik.managers.mods.voicechat.api.events.MergeClientSoundEvent;
import dimasik.managers.mods.voicechat.plugins.impl.events.ClientEventImpl;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class MergeClientSoundEventImpl
extends ClientEventImpl
implements MergeClientSoundEvent {
    @Nullable
    private List<short[]> audioToMerge;

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public void mergeAudio(short[] audio) {
        if (this.audioToMerge == null) {
            this.audioToMerge = new ArrayList<short[]>();
        }
        this.audioToMerge.add(audio);
    }

    @Nullable
    public List<short[]> getAudioToMerge() {
        return this.audioToMerge;
    }
}
