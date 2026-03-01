package dimasik.managers.mods.voicechat.voice.client;

import com.mojang.authlib.GameProfile;
import de.maxhenkel.lame4j.ShortArrayBuffer;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.VoicechatClient;
import dimasik.managers.mods.voicechat.api.mp3.Mp3Encoder;
import dimasik.managers.mods.voicechat.intercompatibility.CommonCompatibilityManager;
import dimasik.managers.mods.voicechat.plugins.impl.mp3.Mp3EncoderImpl;
import dimasik.managers.mods.voicechat.voice.common.NamedThreadPoolFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.io.FileUtils;

public class AudioRecorder {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    private static final int MP3_BITRATE = 320;
    private final long timestamp;
    private final Path location;
    private final GameProfile ownProfile;
    private final Map<UUID, AudioChunk> chunks;
    private final Map<UUID, EncoderData> encoders;
    private final AudioFormat stereoFormat;
    private final ExecutorService threadPool;

    public AudioRecorder(Path location, long timestamp) {
        this.timestamp = timestamp;
        this.location = location;
        location.toFile().mkdirs();
        this.chunks = new ConcurrentHashMap<UUID, AudioChunk>();
        this.encoders = new ConcurrentHashMap<UUID, EncoderData>();
        this.ownProfile = Minecraft.getInstance().player.getGameProfile();
        this.stereoFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 48000.0f, 16, 2, 4, 48000.0f, false);
        this.threadPool = Executors.newSingleThreadExecutor(NamedThreadPoolFactory.create("AudioRecorderThread"));
    }

    public static AudioRecorder create() {
        long timestamp = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        String recordingDestination = VoicechatClient.CLIENT_CONFIG.recordingDestination.get();
        Path location = recordingDestination.trim().isEmpty() ? CommonCompatibilityManager.INSTANCE.getGameDirectory().resolve("voicechat_recordings").resolve(FORMAT.format(cal.getTime())) : Paths.get(recordingDestination, new String[0]).resolve(FORMAT.format(cal.getTime()));
        return new AudioRecorder(location, timestamp);
    }

    public Path getLocation() {
        return this.location;
    }

    public long getStartTime() {
        return this.timestamp;
    }

    public int getRecordedPlayerCount() {
        return this.encoders.size();
    }

    public String getDuration() {
        return this.getDuration(System.currentTimeMillis());
    }

    public String getDuration(long currentTime) {
        long duration = currentTime - this.timestamp;
        SimpleDateFormat fmt = new SimpleDateFormat(":mm:ss");
        fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return duration / 3600000L + fmt.format(new Date(duration));
    }

    public String getStorage() {
        return this.getStorage(System.currentTimeMillis());
    }

    public String getStorage(long currentTime) {
        long durationSeconds = (currentTime - this.timestamp) / 1000L;
        long size = durationSeconds * 320L * 1000L / 8L * (long)this.getRecordedPlayerCount();
        return FileUtils.byteCountToDisplaySize(size);
    }

    private String lookupName(UUID uuid) {
        if (uuid.equals(this.ownProfile.getId())) {
            return this.ownProfile.getName();
        }
        String username = VoicechatClient.USERNAME_CACHE.getUsername(uuid);
        if (username == null) {
            return "system-" + String.valueOf(uuid);
        }
        return username;
    }

    public void appendChunk(UUID uuid, long chunkTimestamp, short[] data) throws IOException {
        if (data.length <= 0) {
            this.flushChunkThreaded(uuid);
            return;
        }
        if (!this.encoders.containsKey(uuid)) {
            Mp3Encoder encoder = Mp3EncoderImpl.createEncoder(this.stereoFormat, 320, VoicechatClient.CLIENT_CONFIG.recordingQuality.get(), Files.newOutputStream(this.location.resolve(this.lookupName(uuid) + ".mp3"), StandardOpenOption.CREATE_NEW));
            this.encoders.put(uuid, new EncoderData(encoder, this.timestamp));
            if (encoder == null) {
                throw new IOException("Failed to load mp3 encoder");
            }
        }
        AudioChunk chunk = this.getChunk(uuid, chunkTimestamp);
        long passedTime = chunkTimestamp - chunk.endTimestamp;
        long threshold = (long)VoicechatClient.CLIENT_CONFIG.outputBufferSize.get().intValue() * 20L;
        if (passedTime < threshold && chunk.getDuration() < 60000L) {
            chunk.add(data, chunkTimestamp);
        } else {
            this.flushChunkThreaded(uuid);
            chunk = this.getChunk(uuid, chunkTimestamp);
            chunk.add(data, chunkTimestamp);
        }
    }

    private void writeChunk(UUID playerUUID, AudioChunk chunk) throws IOException {
        short[] silence;
        EncoderData encoderData = this.encoders.get(playerUUID);
        if (encoderData == null) {
            Voicechat.LOGGER.error("Failed to find recording data for {}", playerUUID);
            return;
        }
        if (encoderData.encoder == null) {
            return;
        }
        long relativeTime = chunk.timestamp - encoderData.lastTimestamp;
        if (relativeTime < -100L) {
            Voicechat.LOGGER.warn("Audio snippet {} overlaps more than 100ms with previous snippet.", chunk.timestamp);
            return;
        }
        if (relativeTime < -20L) {
            Voicechat.LOGGER.warn("Audio {} overlaps with previous snippet.", chunk.timestamp);
        }
        if (relativeTime < 0L) {
            relativeTime = 0L;
        }
        int silenceShorts = (int)(relativeTime * (long)this.getSamplesPerMs() * (long)this.stereoFormat.getChannels());
        int tenSeconds = (int)this.stereoFormat.getSampleRate() * this.stereoFormat.getChannels() * 10;
        int insertedSilence = 0;
        if (silenceShorts > tenSeconds) {
            silence = new short[tenSeconds];
            while (insertedSilence + tenSeconds < silenceShorts) {
                encoderData.encoder.encode(silence);
                insertedSilence += tenSeconds;
            }
        }
        silence = new short[silenceShorts - insertedSilence];
        encoderData.encoder.encode(silence);
        short[] audio = chunk.getData();
        encoderData.encoder.encode(audio);
        encoderData.lastTimestamp = chunk.timestamp + (long)this.getAudioTimeMillis(audio.length);
    }

    public void flushChunkThreaded(UUID playerUUID) {
        AudioChunk chunk = this.getAndRemoveChunk(playerUUID);
        if (chunk == null) {
            return;
        }
        this.threadPool.execute(() -> {
            try {
                this.writeChunk(playerUUID, chunk);
            }
            catch (IOException e) {
                Voicechat.LOGGER.error("Failed to save audio chunk for {}", playerUUID, e);
            }
        });
    }

    @Nullable
    private AudioChunk getAndRemoveChunk(UUID playerUUID) {
        return this.chunks.remove(playerUUID);
    }

    private AudioChunk getChunk(UUID uuid, long timestamp) {
        if (!this.chunks.containsKey(uuid)) {
            AudioChunk chunk = new AudioChunk(timestamp);
            this.chunks.put(uuid, chunk);
            return chunk;
        }
        return this.chunks.get(uuid);
    }

    private void flush() throws IOException {
        for (Map.Entry<UUID, AudioChunk> chunk : this.chunks.entrySet()) {
            this.writeChunk(chunk.getKey(), chunk.getValue());
        }
    }

    public void close() {
        if (this.threadPool.isShutdown()) {
            throw new IllegalStateException("Recorder already closed");
        }
        this.threadPool.shutdown();
    }

    public void saveAndClose() {
        this.save();
        this.close();
    }

    private void save() {
        this.threadPool.execute(() -> {
            this.send(new TranslationTextComponent("message.voicechat.processing_recording_session"));
            try {
                IOException error = null;
                this.sendProgress(0.0f);
                try {
                    this.flush();
                    this.sendProgress(0.5f);
                }
                catch (IOException e) {
                    error = e;
                }
                for (EncoderData encoderData : this.encoders.values()) {
                    if (encoderData.encoder != null) {
                        try {
                            encoderData.encoder.close();
                        }
                        catch (IOException e) {
                            error = e;
                        }
                        continue;
                    }
                    error = new IOException("Failed to load mp3 encoder");
                }
                if (error != null) {
                    throw error;
                }
                this.sendProgress(1.0f);
            }
            catch (Exception e) {
                Voicechat.LOGGER.error("Failed to save recording session", e);
                this.send(new TranslationTextComponent("message.voicechat.save_session_failed", e.getMessage()));
            }
        });
    }

    private void sendProgress(float progress) {
        this.send(new TranslationTextComponent("message.voicechat.processing_progress", new StringTextComponent(String.valueOf((int)(progress * 100.0f))).mergeStyle(TextFormatting.GRAY)));
    }

    private void send(ITextComponent msg) {
        Minecraft mc = Minecraft.getInstance();
        ClientPlayerEntity player = mc.player;
        if (player != null && mc.world != null) {
            player.sendMessage(msg, Util.DUMMY_UUID);
        } else {
            Voicechat.LOGGER.info("{}", msg.getString());
        }
    }

    private int getAudioTimeMillis(int audioShortLength) {
        return audioShortLength / this.stereoFormat.getChannels() / this.getSamplesPerMs();
    }

    private int getSamplesPerMs() {
        return (int)this.stereoFormat.getSampleRate() / 1000;
    }

    private static class EncoderData {
        @Nullable
        private final Mp3Encoder encoder;
        private long lastTimestamp;

        public EncoderData(@Nullable Mp3Encoder encoder, long lastTimestamp) {
            this.encoder = encoder;
            this.lastTimestamp = lastTimestamp;
        }
    }

    private class AudioChunk {
        private final long timestamp;
        private final ShortArrayBuffer buffer;
        private long endTimestamp;

        public AudioChunk(long timestamp) {
            this.timestamp = timestamp;
            this.endTimestamp = timestamp;
            this.buffer = new ShortArrayBuffer();
        }

        public void add(short[] data, long timestamp) throws IOException {
            this.buffer.writeShorts(data);
            this.endTimestamp = timestamp + this.getDuration(data.length);
        }

        private long getDuration(int length) {
            long l = (long)length * 1000L / (long)AudioRecorder.this.stereoFormat.getChannels();
            return (long)((double)l / (double)AudioRecorder.this.stereoFormat.getSampleRate());
        }

        public short[] getData() {
            return this.buffer.toShortArray();
        }

        public long getDuration() {
            return this.endTimestamp - this.timestamp;
        }
    }
}
