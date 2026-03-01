package dimasik.managers.mods.voicechat.voice.client.microphone;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.voice.client.MicrophoneException;
import dimasik.managers.mods.voicechat.voice.client.microphone.ALMicrophone;
import dimasik.managers.mods.voicechat.voice.client.microphone.JavaxMicrophone;
import dimasik.managers.mods.voicechat.voice.client.microphone.Microphone;
import java.util.List;

public class MicrophoneManager {
    private static boolean fallback;

    public static Microphone createMicrophone() throws MicrophoneException {
        Microphone mic;
        if (fallback || VoicechatClient.CLIENT_CONFIG.javaMicrophoneImplementation.get().booleanValue()) {
            mic = MicrophoneManager.createJavaMicrophone();
        } else {
            try {
                mic = MicrophoneManager.createALMicrophone();
            }
            catch (MicrophoneException e) {
                Voicechat.LOGGER.warn("Failed to use OpenAL microphone implementation", e);
                Voicechat.LOGGER.warn("Falling back to Java microphone implementation", new Object[0]);
                mic = MicrophoneManager.createJavaMicrophone();
                fallback = true;
            }
        }
        return mic;
    }

    private static Microphone createJavaMicrophone() throws MicrophoneException {
        JavaxMicrophone mic = new JavaxMicrophone(48000, 960, VoicechatClient.CLIENT_CONFIG.microphone.get());
        mic.open();
        return mic;
    }

    private static Microphone createALMicrophone() throws MicrophoneException {
        ALMicrophone mic = new ALMicrophone(48000, 960, VoicechatClient.CLIENT_CONFIG.microphone.get());
        mic.open();
        return mic;
    }

    public static List<String> deviceNames() {
        if (fallback || VoicechatClient.CLIENT_CONFIG.javaMicrophoneImplementation.get().booleanValue()) {
            return JavaxMicrophone.getAllMicrophones();
        }
        return ALMicrophone.getAllMicrophones();
    }
}
