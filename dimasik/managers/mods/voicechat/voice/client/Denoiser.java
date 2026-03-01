package dimasik.managers.mods.voicechat.voice.client;

import de.maxhenkel.rnnoise4j.UnknownPlatformException;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.intercompatibility.CrossSideManager;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.io.IOException;
import javax.annotation.Nullable;

public class Denoiser
extends de.maxhenkel.rnnoise4j.Denoiser {
    private Denoiser() throws IOException, UnknownPlatformException {
    }

    @Nullable
    public static Denoiser createDenoiser() {
        if (!CrossSideManager.get().useNatives()) {
            return null;
        }
        return Utils.createSafe(Denoiser::new, e -> Voicechat.LOGGER.warn("Failed to load RNNoise", e));
    }
}
