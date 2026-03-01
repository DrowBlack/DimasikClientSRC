package org.lwjgl.openal;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.openal.AL;
import org.lwjgl.system.Checks;
import org.lwjgl.system.JNI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class SOFTBufferSamples {
    public static final int AL_MONO8_SOFT = 4352;
    public static final int AL_MONO16_SOFT = 4353;
    public static final int AL_MONO32F_SOFT = 65552;
    public static final int AL_STEREO8_SOFT = 4354;
    public static final int AL_STEREO16_SOFT = 4355;
    public static final int AL_STEREO32F_SOFT = 65553;
    public static final int AL_QUAD8_SOFT = 4612;
    public static final int AL_QUAD16_SOFT = 4613;
    public static final int AL_QUAD32F_SOFT = 4614;
    public static final int AL_REAR8_SOFT = 4615;
    public static final int AL_REAR16_SOFT = 4616;
    public static final int AL_REAR32F_SOFT = 4617;
    public static final int AL_5POINT1_8_SOFT = 4618;
    public static final int AL_5POINT1_16_SOFT = 4619;
    public static final int AL_5POINT1_32F_SOFT = 4620;
    public static final int AL_6POINT1_8_SOFT = 4621;
    public static final int AL_6POINT1_16_SOFT = 4622;
    public static final int AL_6POINT1_32F_SOFT = 4623;
    public static final int AL_7POINT1_8_SOFT = 4624;
    public static final int AL_7POINT1_16_SOFT = 4625;
    public static final int AL_7POINT1_32F_SOFT = 4626;
    public static final int AL_MONO_SOFT = 5376;
    public static final int AL_STEREO_SOFT = 5377;
    public static final int AL_QUAD_SOFT = 5378;
    public static final int AL_REAR_SOFT = 5379;
    public static final int AL_5POINT1_SOFT = 5380;
    public static final int AL_6POINT1_SOFT = 5381;
    public static final int AL_7POINT1_SOFT = 5382;
    public static final int AL_BYTE_SOFT = 5120;
    public static final int AL_UNSIGNED_BYTE_SOFT = 5121;
    public static final int AL_SHORT_SOFT = 5122;
    public static final int AL_UNSIGNED_SHORT_SOFT = 5123;
    public static final int AL_INT_SOFT = 5124;
    public static final int AL_UNSIGNED_INT_SOFT = 5125;
    public static final int AL_FLOAT_SOFT = 5126;
    public static final int AL_DOUBLE_SOFT = 5127;
    public static final int AL_BYTE3_SOFT = 5128;
    public static final int AL_UNSIGNED_BYTE3_SOFT = 5129;
    public static final int AL_INTERNAL_FORMAT_SOFT = 8200;
    public static final int AL_BYTE_LENGTH_SOFT = 8201;
    public static final int AL_SAMPLE_LENGTH_SOFT = 8202;
    public static final int AL_SEC_LENGTH_SOFT = 8203;
    public static final int AL_BYTE_RW_OFFSETS_SOFT = 4145;
    public static final int AL_SAMPLE_RW_OFFSETS_SOFT = 4146;

    protected SOFTBufferSamples() {
        throw new UnsupportedOperationException();
    }

    public static void nalBufferSamplesSOFT(int buffer, int samplerate, int internalformat, int samples, int channels, int type, long data) {
        long __functionAddress = AL.getICD().alBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, samplerate, internalformat, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") ByteBuffer data) {
        SOFTBufferSamples.nalBufferSamplesSOFT(buffer, samplerate, internalformat, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") ShortBuffer data) {
        SOFTBufferSamples.nalBufferSamplesSOFT(buffer, samplerate, internalformat, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") IntBuffer data) {
        SOFTBufferSamples.nalBufferSamplesSOFT(buffer, samplerate, internalformat, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") FloatBuffer data) {
        SOFTBufferSamples.nalBufferSamplesSOFT(buffer, samplerate, internalformat, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") DoubleBuffer data) {
        SOFTBufferSamples.nalBufferSamplesSOFT(buffer, samplerate, internalformat, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void nalBufferSubSamplesSOFT(int buffer, int offset, int samples, int channels, int type, long data) {
        long __functionAddress = AL.getICD().alBufferSubSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") ByteBuffer data) {
        SOFTBufferSamples.nalBufferSubSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") ShortBuffer data) {
        SOFTBufferSamples.nalBufferSubSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") IntBuffer data) {
        SOFTBufferSamples.nalBufferSubSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") FloatBuffer data) {
        SOFTBufferSamples.nalBufferSubSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") DoubleBuffer data) {
        SOFTBufferSamples.nalBufferSubSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void nalGetBufferSamplesSOFT(int buffer, int offset, int samples, int channels, int type, long data) {
        long __functionAddress = AL.getICD().alGetBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") ByteBuffer data) {
        SOFTBufferSamples.nalGetBufferSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") ShortBuffer data) {
        SOFTBufferSamples.nalGetBufferSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") IntBuffer data) {
        SOFTBufferSamples.nalGetBufferSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") FloatBuffer data) {
        SOFTBufferSamples.nalGetBufferSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") DoubleBuffer data) {
        SOFTBufferSamples.nalGetBufferSamplesSOFT(buffer, offset, samples, channels, type, MemoryUtil.memAddress(data));
    }

    @NativeType(value="ALboolean")
    public static boolean alIsBufferFormatSupportedSOFT(@NativeType(value="ALenum") int format) {
        long __functionAddress = AL.getICD().alIsBufferFormatSupportedSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        return JNI.invokeZ(format, __functionAddress);
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") short[] data) {
        long __functionAddress = AL.getICD().alBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, samplerate, internalformat, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") int[] data) {
        long __functionAddress = AL.getICD().alBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, samplerate, internalformat, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") float[] data) {
        long __functionAddress = AL.getICD().alBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, samplerate, internalformat, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALuint") int samplerate, @NativeType(value="ALenum") int internalformat, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") double[] data) {
        long __functionAddress = AL.getICD().alBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, samplerate, internalformat, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") short[] data) {
        long __functionAddress = AL.getICD().alBufferSubSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") int[] data) {
        long __functionAddress = AL.getICD().alBufferSubSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") float[] data) {
        long __functionAddress = AL.getICD().alBufferSubSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alBufferSubSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid const *") double[] data) {
        long __functionAddress = AL.getICD().alBufferSubSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") short[] data) {
        long __functionAddress = AL.getICD().alGetBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") int[] data) {
        long __functionAddress = AL.getICD().alGetBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") float[] data) {
        long __functionAddress = AL.getICD().alGetBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }

    public static void alGetBufferSamplesSOFT(@NativeType(value="ALuint") int buffer, @NativeType(value="ALsizei") int offset, @NativeType(value="ALsizei") int samples, @NativeType(value="ALenum") int channels, @NativeType(value="ALenum") int type, @NativeType(value="ALvoid *") double[] data) {
        long __functionAddress = AL.getICD().alGetBufferSamplesSOFT;
        if (Checks.CHECKS) {
            Checks.check(__functionAddress);
        }
        JNI.invokePV(buffer, offset, samples, channels, type, data, __functionAddress);
    }
}
