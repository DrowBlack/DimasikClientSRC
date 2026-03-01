package dimasik.managers.mods.voicechat.plugins.impl.opus;

import de.maxhenkel.opus4j.OpusEncoder;
import dimasik.managers.mods.voicechat.Voicechat;
import dimasik.managers.mods.voicechat.api.opus.OpusDecoder;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoder;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoderMode;
import dimasik.managers.mods.voicechat.intercompatibility.CrossSideManager;
import dimasik.managers.mods.voicechat.plugins.impl.opus.JavaOpusDecoderImpl;
import dimasik.managers.mods.voicechat.plugins.impl.opus.JavaOpusEncoderImpl;
import dimasik.managers.mods.voicechat.plugins.impl.opus.NativeOpusDecoderImpl;
import dimasik.managers.mods.voicechat.plugins.impl.opus.NativeOpusEncoderImpl;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import javax.annotation.Nullable;

public class OpusManager {
    private static boolean nativeOpusCompatible = true;

    public static boolean opusNativeCheck() {
        Voicechat.LOGGER.info("Loading Opus", new Object[0]);
        if (!nativeOpusCompatible || !CrossSideManager.get().useNatives()) {
            return false;
        }
        Boolean success = Utils.createSafe(() -> {
            NativeOpusEncoderImpl encoder = new NativeOpusEncoderImpl(48000, 1, OpusEncoder.Application.VOIP);
            encoder.setMaxPayloadSize(1024);
            byte[] encoded = encoder.encode(new short[960]);
            encoder.resetState();
            encoder.close();
            NativeOpusDecoderImpl decoder = new NativeOpusDecoderImpl(48000, 1);
            decoder.setFrameSize(960);
            decoder.decode(encoded);
            decoder.decodeFec();
            decoder.resetState();
            decoder.close();
            return true;
        }, e -> Voicechat.LOGGER.warn("Failed to load native Opus implementation", e));
        if (success == null || !success.booleanValue()) {
            Voicechat.LOGGER.warn("Failed to load native Opus encoder - Falling back to Java Opus implementation", new Object[0]);
            nativeOpusCompatible = false;
            return false;
        }
        return true;
    }

    @Nullable
    private static OpusEncoder createNativeEncoder(int mtuSize, OpusEncoder.Application application) {
        if (!nativeOpusCompatible) {
            return null;
        }
        try {
            NativeOpusEncoderImpl encoder = new NativeOpusEncoderImpl(48000, 1, application);
            encoder.setMaxPayloadSize(mtuSize);
            return encoder;
        }
        catch (Throwable e) {
            nativeOpusCompatible = false;
            Voicechat.LOGGER.warn("Failed to load native Opus encoder - Falling back to Java Opus implementation", new Object[0]);
            return null;
        }
    }

    public static OpusEncoder createEncoder(OpusEncoderMode mode) {
        OpusEncoder encoder;
        int mtuSize = CrossSideManager.get().getMtuSize();
        OpusEncoder.Application application = OpusEncoder.Application.VOIP;
        if (mode != null) {
            switch (mode) {
                case VOIP: {
                    application = OpusEncoder.Application.VOIP;
                    break;
                }
                case AUDIO: {
                    application = OpusEncoder.Application.AUDIO;
                    break;
                }
                case RESTRICTED_LOWDELAY: {
                    application = OpusEncoder.Application.LOW_DELAY;
                }
            }
        }
        if (CrossSideManager.get().useNatives() && (encoder = OpusManager.createNativeEncoder(mtuSize, application)) != null) {
            return encoder;
        }
        return new JavaOpusEncoderImpl(48000, 960, mtuSize, application);
    }

    @Nullable
    private static OpusDecoder createNativeDecoder() {
        if (!nativeOpusCompatible) {
            return null;
        }
        try {
            NativeOpusDecoderImpl decoder = new NativeOpusDecoderImpl(48000, 1);
            decoder.setFrameSize(960);
            return decoder;
        }
        catch (Throwable e) {
            nativeOpusCompatible = false;
            Voicechat.LOGGER.warn("Failed to load native Opus decoder - Falling back to Java Opus implementation", new Object[0]);
            return null;
        }
    }

    public static OpusDecoder createDecoder() {
        OpusDecoder decoder;
        if (CrossSideManager.get().useNatives() && (decoder = OpusManager.createNativeDecoder()) != null) {
            return decoder;
        }
        return new JavaOpusDecoderImpl(48000, 960);
    }
}
