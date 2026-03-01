package dimasik.managers.mods.voicechat.plugins.impl.audiosender;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.audiosender.AudioSender;
import dimasik.managers.mods.voicechat.voice.common.MicPacket;
import dimasik.managers.mods.voicechat.voice.server.Server;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AudioSenderImpl
implements AudioSender {
    private static final Map<UUID, AudioSenderImpl> AUDIO_SENDERS = new HashMap<UUID, AudioSenderImpl>();
    private final UUID uuid;
    private boolean whispering;
    private long nextSequenceNumber;

    public AudioSenderImpl(UUID uuid) {
        this.uuid = uuid;
    }

    public static boolean registerAudioSender(AudioSenderImpl audioSender) {
        if (Voicechat.SERVER.isCompatible(audioSender.uuid)) {
            return false;
        }
        if (AUDIO_SENDERS.containsKey(audioSender.uuid)) {
            return false;
        }
        AUDIO_SENDERS.put(audioSender.uuid, audioSender);
        return true;
    }

    public static boolean unregisterAudioSender(AudioSenderImpl audioSender) {
        return AUDIO_SENDERS.remove(audioSender.uuid) != null;
    }

    @Override
    public AudioSender whispering(boolean whispering) {
        this.whispering = whispering;
        return this;
    }

    @Override
    public boolean isWhispering() {
        return this.whispering;
    }

    @Override
    public AudioSender sequenceNumber(long sequenceNumber) {
        if (sequenceNumber < 0L) {
            throw new IllegalArgumentException("Sequence number must be positive");
        }
        this.nextSequenceNumber = sequenceNumber;
        return this;
    }

    @Override
    public boolean canSend() {
        return !Voicechat.SERVER.isCompatible(this.uuid) && AUDIO_SENDERS.get(this.uuid) == this;
    }

    @Override
    public boolean send(byte[] opusEncodedAudioData) {
        return this.sendMicrophonePacket(opusEncodedAudioData);
    }

    @Override
    public boolean reset() {
        return this.sendMicrophonePacket(new byte[0]);
    }

    public boolean sendMicrophonePacket(byte[] data) {
        if (data == null) {
            throw new IllegalStateException("opusEncodedData is not set");
        }
        if (!this.canSend()) {
            return false;
        }
        Server server = Voicechat.SERVER.getServer();
        if (server == null) {
            return true;
        }
        try {
            MicPacket packet = new MicPacket(data, data.length > 0 && this.whispering, this.nextSequenceNumber++);
            if (data.length <= 0) {
                this.nextSequenceNumber = 0L;
            }
            server.onMicPacket(this.uuid, packet);
        }
        catch (Exception e) {
            Voicechat.LOGGER.error("Failed to send audio", e);
        }
        return true;
    }
}
