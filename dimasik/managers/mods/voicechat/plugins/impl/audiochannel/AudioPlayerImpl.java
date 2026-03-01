package dimasik.managers.mods.voicechat.plugins.impl.audiochannel;

import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.audiochannel.AudioChannel;
import dimasik.managers.mods.voicechat.api.audiochannel.AudioPlayer;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoder;
import dimasik.managers.mods.voicechat.debug.VoicechatUncaughtExceptionHandler;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AudioPlayerImpl
extends Thread
implements AudioPlayer {
    private static final long FRAME_SIZE_NS = 20000000L;
    private final AudioChannel audioChannel;
    private final OpusEncoder encoder;
    private final Supplier<short[]> audioSupplier;
    private boolean started;
    @Nullable
    private Runnable onStopped;

    public AudioPlayerImpl(AudioChannel audioChannel, @Nonnull OpusEncoder encoder, Supplier<short[]> audioSupplier) {
        this.audioChannel = audioChannel;
        this.encoder = encoder;
        this.audioSupplier = audioSupplier;
        this.setDaemon(true);
        this.setName("AudioPlayer-" + String.valueOf(audioChannel.getId()));
        this.setUncaughtExceptionHandler(new VoicechatUncaughtExceptionHandler());
    }

    @Override
    public void startPlaying() {
        if (this.started) {
            return;
        }
        this.start();
        this.started = true;
    }

    @Override
    public void stopPlaying() {
        this.interrupt();
    }

    @Override
    public boolean isStarted() {
        return this.started;
    }

    @Override
    public boolean isPlaying() {
        return this.isAlive();
    }

    @Override
    public boolean isStopped() {
        return this.started && !this.isAlive();
    }

    @Override
    public void setOnStopped(Runnable onStopped) {
        this.onStopped = onStopped;
    }

    @Override
    public void run() {
        short[] frame;
        int framePosition = 0;
        long startTime = System.nanoTime();
        while ((frame = this.audioSupplier.get()) != null) {
            if (frame.length != 960) {
                Voicechat.LOGGER.error("Got invalid audio frame size {}!={}", frame.length, 960);
                break;
            }
            this.audioChannel.send(this.encoder.encode(frame));
            long waitTimestamp = startTime + (long)(++framePosition) * 20000000L;
            long waitNanos = waitTimestamp - System.nanoTime();
            try {
                if (waitNanos <= 0L) continue;
                Thread.sleep(waitNanos / 1000000L, (int)(waitNanos % 1000000L));
            }
            catch (InterruptedException e) {
                break;
            }
        }
        this.encoder.close();
        this.audioChannel.flush();
        if (this.onStopped != null) {
            this.onStopped.run();
        }
    }
}
