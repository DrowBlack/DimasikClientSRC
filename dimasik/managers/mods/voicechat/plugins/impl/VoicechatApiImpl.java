package dimasik.managers.mods.voicechat.plugins.impl;

import dimasik.managers.mods.voicechat.api.Position;
import dimasik.managers.mods.voicechat.api.ServerLevel;
import dimasik.managers.mods.voicechat.api.ServerPlayer;
import dimasik.managers.mods.voicechat.api.VoicechatApi;
import dimasik.managers.mods.voicechat.api.VolumeCategory;
import dimasik.managers.mods.voicechat.api.audio.AudioConverter;
import dimasik.managers.mods.voicechat.api.mp3.Mp3Decoder;
import dimasik.managers.mods.voicechat.api.mp3.Mp3Encoder;
import dimasik.managers.mods.voicechat.api.opus.OpusDecoder;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoder;
import dimasik.managers.mods.voicechat.api.opus.OpusEncoderMode;
import dimasik.managers.mods.voicechat.plugins.impl.EntityImpl;
import dimasik.managers.mods.voicechat.plugins.impl.PositionImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerLevelImpl;
import dimasik.managers.mods.voicechat.plugins.impl.ServerPlayerImpl;
import dimasik.managers.mods.voicechat.plugins.impl.VolumeCategoryImpl;
import dimasik.managers.mods.voicechat.plugins.impl.audio.AudioConverterImpl;
import dimasik.managers.mods.voicechat.plugins.impl.mp3.Mp3DecoderImpl;
import dimasik.managers.mods.voicechat.plugins.impl.mp3.Mp3EncoderImpl;
import dimasik.managers.mods.voicechat.plugins.impl.opus.OpusManager;
import dimasik.managers.mods.voicechat.voice.common.Utils;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

public abstract class VoicechatApiImpl
implements VoicechatApi {
    private static final AudioConverter AUDIO_CONVERTER = new AudioConverterImpl();

    @Override
    public OpusEncoder createEncoder() {
        return OpusManager.createEncoder(null);
    }

    @Override
    public OpusEncoder createEncoder(OpusEncoderMode mode) {
        return OpusManager.createEncoder(mode);
    }

    @Override
    @Nullable
    public Mp3Encoder createMp3Encoder(AudioFormat audioFormat, int bitrate, int quality, OutputStream outputStream) {
        return Mp3EncoderImpl.createEncoder(audioFormat, bitrate, quality, outputStream);
    }

    @Override
    @Nullable
    public Mp3Decoder createMp3Decoder(InputStream inputStream) {
        return Mp3DecoderImpl.createDecoder(inputStream);
    }

    @Override
    public OpusDecoder createDecoder() {
        return OpusManager.createDecoder();
    }

    @Override
    public AudioConverter getAudioConverter() {
        return AUDIO_CONVERTER;
    }

    @Override
    public dimasik.managers.mods.voicechat.api.Entity fromEntity(Object entity) {
        if (entity instanceof Entity) {
            Entity e = (Entity)entity;
            return new EntityImpl(e);
        }
        throw new IllegalArgumentException("entity is not an instance of Entity");
    }

    @Override
    public ServerLevel fromServerLevel(Object serverLevel) {
        if (serverLevel instanceof ServerWorld) {
            ServerWorld l = (ServerWorld)serverLevel;
            return new ServerLevelImpl(l);
        }
        throw new IllegalArgumentException("serverLevel is not an instance of ServerLevel");
    }

    @Override
    public ServerPlayer fromServerPlayer(Object serverPlayer) {
        if (serverPlayer instanceof ServerPlayerEntity) {
            ServerPlayerEntity p = (ServerPlayerEntity)serverPlayer;
            return new ServerPlayerImpl(p);
        }
        throw new IllegalArgumentException("serverPlayer is not an instance of ServerPlayer");
    }

    @Override
    public Position createPosition(double x, double y, double z) {
        return new PositionImpl(x, y, z);
    }

    @Override
    public VolumeCategory.Builder volumeCategoryBuilder() {
        return new VolumeCategoryImpl.BuilderImpl();
    }

    @Override
    public double getVoiceChatDistance() {
        return Utils.getDefaultDistanceServer();
    }
}
