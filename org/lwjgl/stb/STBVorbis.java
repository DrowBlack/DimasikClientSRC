package org.lwjgl.stb;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import javax.annotation.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.LibSTB;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisComment;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class STBVorbis {
    public static final int VORBIS__no_error = 0;
    public static final int VORBIS_need_more_data = 1;
    public static final int VORBIS_invalid_api_mixing = 2;
    public static final int VORBIS_outofmem = 3;
    public static final int VORBIS_feature_not_supported = 4;
    public static final int VORBIS_too_many_channels = 5;
    public static final int VORBIS_file_open_failure = 6;
    public static final int VORBIS_seek_without_length = 7;
    public static final int VORBIS_unexpected_eof = 10;
    public static final int VORBIS_seek_invalid = 11;
    public static final int VORBIS_invalid_setup = 20;
    public static final int VORBIS_invalid_stream = 21;
    public static final int VORBIS_missing_capture_pattern = 30;
    public static final int VORBIS_invalid_stream_structure_version = 31;
    public static final int VORBIS_continued_packet_flag_invalid = 32;
    public static final int VORBIS_incorrect_stream_serial_number = 33;
    public static final int VORBIS_invalid_first_page = 34;
    public static final int VORBIS_bad_packet_type = 35;
    public static final int VORBIS_cant_find_last_page = 36;
    public static final int VORBIS_seek_failed = 37;
    public static final int VORBIS_ogg_skeleton_not_supported = 38;

    protected STBVorbis() {
        throw new UnsupportedOperationException();
    }

    public static native void nstb_vorbis_get_info(long var0, long var2);

    @NativeType(value="stb_vorbis_info")
    public static STBVorbisInfo stb_vorbis_get_info(@NativeType(value="stb_vorbis *") long f, @NativeType(value="stb_vorbis_info") STBVorbisInfo __result) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        STBVorbis.nstb_vorbis_get_info(f, __result.address());
        return __result;
    }

    public static native void nstb_vorbis_get_comment(long var0, long var2);

    @NativeType(value="stb_vorbis_comment")
    public static STBVorbisComment stb_vorbis_get_comment(@NativeType(value="stb_vorbis *") long f, @NativeType(value="stb_vorbis_comment") STBVorbisComment __result) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        STBVorbis.nstb_vorbis_get_comment(f, __result.address());
        return __result;
    }

    public static native int nstb_vorbis_get_error(long var0);

    public static int stb_vorbis_get_error(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_error(f);
    }

    public static native void nstb_vorbis_close(long var0);

    public static void stb_vorbis_close(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        STBVorbis.nstb_vorbis_close(f);
    }

    public static native int nstb_vorbis_get_sample_offset(long var0);

    public static int stb_vorbis_get_sample_offset(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_sample_offset(f);
    }

    public static native int nstb_vorbis_get_file_offset(long var0);

    @NativeType(value="unsigned int")
    public static int stb_vorbis_get_file_offset(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_file_offset(f);
    }

    public static native long nstb_vorbis_open_pushdata(long var0, int var2, long var3, long var5, long var7);

    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_pushdata(@NativeType(value="unsigned char const *") ByteBuffer datablock, @NativeType(value="int *") IntBuffer datablock_memory_consumed_in_bytes, @NativeType(value="int *") IntBuffer error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)datablock_memory_consumed_in_bytes, 1);
            Checks.check((Buffer)error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        return STBVorbis.nstb_vorbis_open_pushdata(MemoryUtil.memAddress(datablock), datablock.remaining(), MemoryUtil.memAddress(datablock_memory_consumed_in_bytes), MemoryUtil.memAddress(error), MemoryUtil.memAddressSafe(alloc_buffer));
    }

    public static native int nstb_vorbis_decode_frame_pushdata(long var0, long var2, int var4, long var5, long var7, long var9);

    public static int stb_vorbis_decode_frame_pushdata(@NativeType(value="stb_vorbis *") long f, @NativeType(value="unsigned char const *") ByteBuffer datablock, @Nullable @NativeType(value="int *") IntBuffer channels, @NativeType(value="float ***") PointerBuffer output, @NativeType(value="int *") IntBuffer samples) {
        if (Checks.CHECKS) {
            Checks.check(f);
            Checks.checkSafe((Buffer)channels, 1);
            Checks.check(output, 1);
            Checks.check((Buffer)samples, 1);
        }
        return STBVorbis.nstb_vorbis_decode_frame_pushdata(f, MemoryUtil.memAddress(datablock), datablock.remaining(), MemoryUtil.memAddressSafe(channels), MemoryUtil.memAddress(output), MemoryUtil.memAddress(samples));
    }

    public static native void nstb_vorbis_flush_pushdata(long var0);

    public static void stb_vorbis_flush_pushdata(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        STBVorbis.nstb_vorbis_flush_pushdata(f);
    }

    public static native int nstb_vorbis_decode_filename(long var0, long var2, long var4, long var6);

    public static int stb_vorbis_decode_filename(@NativeType(value="char const *") ByteBuffer filename, @NativeType(value="int *") IntBuffer channels, @NativeType(value="int *") IntBuffer sample_rate, @NativeType(value="short **") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check((Buffer)channels, 1);
            Checks.check((Buffer)sample_rate, 1);
            Checks.check(output, 1);
        }
        return STBVorbis.nstb_vorbis_decode_filename(MemoryUtil.memAddress(filename), MemoryUtil.memAddress(channels), MemoryUtil.memAddress(sample_rate), MemoryUtil.memAddress(output));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int stb_vorbis_decode_filename(@NativeType(value="char const *") CharSequence filename, @NativeType(value="int *") IntBuffer channels, @NativeType(value="int *") IntBuffer sample_rate, @NativeType(value="short **") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)channels, 1);
            Checks.check((Buffer)sample_rate, 1);
            Checks.check(output, 1);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nASCII(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            int n = STBVorbis.nstb_vorbis_decode_filename(filenameEncoded, MemoryUtil.memAddress(channels), MemoryUtil.memAddress(sample_rate), MemoryUtil.memAddress(output));
            return n;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    @NativeType(value="int")
    public static ShortBuffer stb_vorbis_decode_filename(@NativeType(value="char const *") CharSequence filename, @NativeType(value="int *") IntBuffer channels, @NativeType(value="int *") IntBuffer sample_rate) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)channels, 1);
            Checks.check((Buffer)sample_rate, 1);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nASCII(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            PointerBuffer output = stack.pointers(0L);
            int __result = STBVorbis.nstb_vorbis_decode_filename(filenameEncoded, MemoryUtil.memAddress(channels), MemoryUtil.memAddress(sample_rate), MemoryUtil.memAddress(output));
            ShortBuffer shortBuffer = MemoryUtil.memShortBufferSafe(output.get(0), __result * channels.get(0));
            return shortBuffer;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstb_vorbis_decode_memory(long var0, int var2, long var3, long var5, long var7);

    public static int stb_vorbis_decode_memory(@NativeType(value="unsigned char const *") ByteBuffer mem, @NativeType(value="int *") IntBuffer channels, @NativeType(value="int *") IntBuffer sample_rate, @NativeType(value="short **") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)channels, 1);
            Checks.check((Buffer)sample_rate, 1);
            Checks.check(output, 1);
        }
        return STBVorbis.nstb_vorbis_decode_memory(MemoryUtil.memAddress(mem), mem.remaining(), MemoryUtil.memAddress(channels), MemoryUtil.memAddress(sample_rate), MemoryUtil.memAddress(output));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    @NativeType(value="int")
    public static ShortBuffer stb_vorbis_decode_memory(@NativeType(value="unsigned char const *") ByteBuffer mem, @NativeType(value="int *") IntBuffer channels, @NativeType(value="int *") IntBuffer sample_rate) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)channels, 1);
            Checks.check((Buffer)sample_rate, 1);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            PointerBuffer output = stack.pointers(0L);
            int __result = STBVorbis.nstb_vorbis_decode_memory(MemoryUtil.memAddress(mem), mem.remaining(), MemoryUtil.memAddress(channels), MemoryUtil.memAddress(sample_rate), MemoryUtil.memAddress(output));
            ShortBuffer shortBuffer = MemoryUtil.memShortBufferSafe(output.get(0), __result * channels.get(0));
            return shortBuffer;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native long nstb_vorbis_open_memory(long var0, int var2, long var3, long var5);

    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_memory(@NativeType(value="unsigned char const *") ByteBuffer mem, @NativeType(value="int *") IntBuffer error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        return STBVorbis.nstb_vorbis_open_memory(MemoryUtil.memAddress(mem), mem.remaining(), MemoryUtil.memAddress(error), MemoryUtil.memAddressSafe(alloc_buffer));
    }

    public static native long nstb_vorbis_open_filename(long var0, long var2, long var4);

    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_filename(@NativeType(value="char const *") ByteBuffer filename, @NativeType(value="int *") IntBuffer error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check((Buffer)error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        return STBVorbis.nstb_vorbis_open_filename(MemoryUtil.memAddress(filename), MemoryUtil.memAddress(error), MemoryUtil.memAddressSafe(alloc_buffer));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_filename(@NativeType(value="char const *") CharSequence filename, @NativeType(value="int *") IntBuffer error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.check((Buffer)error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nASCII(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            long l = STBVorbis.nstb_vorbis_open_filename(filenameEncoded, MemoryUtil.memAddress(error), MemoryUtil.memAddressSafe(alloc_buffer));
            return l;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstb_vorbis_seek_frame(long var0, int var2);

    @NativeType(value="int")
    public static boolean stb_vorbis_seek_frame(@NativeType(value="stb_vorbis *") long f, @NativeType(value="unsigned int") int sample_number) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_seek_frame(f, sample_number) != 0;
    }

    public static native int nstb_vorbis_seek(long var0, int var2);

    @NativeType(value="int")
    public static boolean stb_vorbis_seek(@NativeType(value="stb_vorbis *") long f, @NativeType(value="unsigned int") int sample_number) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_seek(f, sample_number) != 0;
    }

    public static native int nstb_vorbis_seek_start(long var0);

    @NativeType(value="int")
    public static boolean stb_vorbis_seek_start(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_seek_start(f) != 0;
    }

    public static native int nstb_vorbis_stream_length_in_samples(long var0);

    @NativeType(value="unsigned int")
    public static int stb_vorbis_stream_length_in_samples(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_stream_length_in_samples(f);
    }

    public static native float nstb_vorbis_stream_length_in_seconds(long var0);

    public static float stb_vorbis_stream_length_in_seconds(@NativeType(value="stb_vorbis *") long f) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_stream_length_in_seconds(f);
    }

    public static native int nstb_vorbis_get_frame_float(long var0, long var2, long var4);

    public static int stb_vorbis_get_frame_float(@NativeType(value="stb_vorbis *") long f, @Nullable @NativeType(value="int *") IntBuffer channels, @NativeType(value="float ***") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.check(f);
            Checks.checkSafe((Buffer)channels, 1);
            Checks.check(output, 1);
        }
        return STBVorbis.nstb_vorbis_get_frame_float(f, MemoryUtil.memAddressSafe(channels), MemoryUtil.memAddress(output));
    }

    public static native int nstb_vorbis_get_frame_short(long var0, int var2, long var3, int var5);

    public static int stb_vorbis_get_frame_short(@NativeType(value="stb_vorbis *") long f, @NativeType(value="short **") PointerBuffer buffer, int num_samples) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_frame_short(f, buffer.remaining(), MemoryUtil.memAddress(buffer), num_samples);
    }

    public static native int nstb_vorbis_get_frame_short_interleaved(long var0, int var2, long var3, int var5);

    public static int stb_vorbis_get_frame_short_interleaved(@NativeType(value="stb_vorbis *") long f, int num_c, @NativeType(value="short *") ShortBuffer buffer) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_frame_short_interleaved(f, num_c, MemoryUtil.memAddress(buffer), buffer.remaining());
    }

    public static native int nstb_vorbis_get_samples_float(long var0, int var2, long var3, int var5);

    public static int stb_vorbis_get_samples_float(@NativeType(value="stb_vorbis *") long f, @NativeType(value="float **") PointerBuffer buffer, int num_samples) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_samples_float(f, buffer.remaining(), MemoryUtil.memAddress(buffer), num_samples);
    }

    public static native int nstb_vorbis_get_samples_float_interleaved(long var0, int var2, long var3, int var5);

    public static int stb_vorbis_get_samples_float_interleaved(@NativeType(value="stb_vorbis *") long f, int channels, @NativeType(value="float *") FloatBuffer buffer) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_samples_float_interleaved(f, channels, MemoryUtil.memAddress(buffer), buffer.remaining());
    }

    public static native int nstb_vorbis_get_samples_short(long var0, int var2, long var3, int var5);

    public static int stb_vorbis_get_samples_short(@NativeType(value="stb_vorbis *") long f, @NativeType(value="short **") PointerBuffer buffer, int num_samples) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_samples_short(f, buffer.remaining(), MemoryUtil.memAddress(buffer), num_samples);
    }

    public static native int nstb_vorbis_get_samples_short_interleaved(long var0, int var2, long var3, int var5);

    public static int stb_vorbis_get_samples_short_interleaved(@NativeType(value="stb_vorbis *") long f, int channels, @NativeType(value="short *") ShortBuffer buffer) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_samples_short_interleaved(f, channels, MemoryUtil.memAddress(buffer), buffer.remaining());
    }

    public static native long nstb_vorbis_open_pushdata(long var0, int var2, int[] var3, int[] var4, long var5);

    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_pushdata(@NativeType(value="unsigned char const *") ByteBuffer datablock, @NativeType(value="int *") int[] datablock_memory_consumed_in_bytes, @NativeType(value="int *") int[] error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.check(datablock_memory_consumed_in_bytes, 1);
            Checks.check(error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        return STBVorbis.nstb_vorbis_open_pushdata(MemoryUtil.memAddress(datablock), datablock.remaining(), datablock_memory_consumed_in_bytes, error, MemoryUtil.memAddressSafe(alloc_buffer));
    }

    public static native int nstb_vorbis_decode_frame_pushdata(long var0, long var2, int var4, int[] var5, long var6, int[] var8);

    public static int stb_vorbis_decode_frame_pushdata(@NativeType(value="stb_vorbis *") long f, @NativeType(value="unsigned char const *") ByteBuffer datablock, @Nullable @NativeType(value="int *") int[] channels, @NativeType(value="float ***") PointerBuffer output, @NativeType(value="int *") int[] samples) {
        if (Checks.CHECKS) {
            Checks.check(f);
            Checks.checkSafe(channels, 1);
            Checks.check(output, 1);
            Checks.check(samples, 1);
        }
        return STBVorbis.nstb_vorbis_decode_frame_pushdata(f, MemoryUtil.memAddress(datablock), datablock.remaining(), channels, MemoryUtil.memAddress(output), samples);
    }

    public static native int nstb_vorbis_decode_filename(long var0, int[] var2, int[] var3, long var4);

    public static int stb_vorbis_decode_filename(@NativeType(value="char const *") ByteBuffer filename, @NativeType(value="int *") int[] channels, @NativeType(value="int *") int[] sample_rate, @NativeType(value="short **") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check(channels, 1);
            Checks.check(sample_rate, 1);
            Checks.check(output, 1);
        }
        return STBVorbis.nstb_vorbis_decode_filename(MemoryUtil.memAddress(filename), channels, sample_rate, MemoryUtil.memAddress(output));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static int stb_vorbis_decode_filename(@NativeType(value="char const *") CharSequence filename, @NativeType(value="int *") int[] channels, @NativeType(value="int *") int[] sample_rate, @NativeType(value="short **") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.check(channels, 1);
            Checks.check(sample_rate, 1);
            Checks.check(output, 1);
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nASCII(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            int n = STBVorbis.nstb_vorbis_decode_filename(filenameEncoded, channels, sample_rate, MemoryUtil.memAddress(output));
            return n;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstb_vorbis_decode_memory(long var0, int var2, int[] var3, int[] var4, long var5);

    public static int stb_vorbis_decode_memory(@NativeType(value="unsigned char const *") ByteBuffer mem, @NativeType(value="int *") int[] channels, @NativeType(value="int *") int[] sample_rate, @NativeType(value="short **") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.check(channels, 1);
            Checks.check(sample_rate, 1);
            Checks.check(output, 1);
        }
        return STBVorbis.nstb_vorbis_decode_memory(MemoryUtil.memAddress(mem), mem.remaining(), channels, sample_rate, MemoryUtil.memAddress(output));
    }

    public static native long nstb_vorbis_open_memory(long var0, int var2, int[] var3, long var4);

    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_memory(@NativeType(value="unsigned char const *") ByteBuffer mem, @NativeType(value="int *") int[] error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.check(error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        return STBVorbis.nstb_vorbis_open_memory(MemoryUtil.memAddress(mem), mem.remaining(), error, MemoryUtil.memAddressSafe(alloc_buffer));
    }

    public static native long nstb_vorbis_open_filename(long var0, int[] var2, long var3);

    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_filename(@NativeType(value="char const *") ByteBuffer filename, @NativeType(value="int *") int[] error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.checkNT1(filename);
            Checks.check(error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        return STBVorbis.nstb_vorbis_open_filename(MemoryUtil.memAddress(filename), error, MemoryUtil.memAddressSafe(alloc_buffer));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NativeType(value="stb_vorbis *")
    public static long stb_vorbis_open_filename(@NativeType(value="char const *") CharSequence filename, @NativeType(value="int *") int[] error, @Nullable @NativeType(value="stb_vorbis_alloc const *") STBVorbisAlloc alloc_buffer) {
        if (Checks.CHECKS) {
            Checks.check(error, 1);
            if (alloc_buffer != null) {
                STBVorbisAlloc.validate(alloc_buffer.address());
            }
        }
        MemoryStack stack = MemoryStack.stackGet();
        int stackPointer = stack.getPointer();
        try {
            stack.nASCII(filename, true);
            long filenameEncoded = stack.getPointerAddress();
            long l = STBVorbis.nstb_vorbis_open_filename(filenameEncoded, error, MemoryUtil.memAddressSafe(alloc_buffer));
            return l;
        }
        finally {
            stack.setPointer(stackPointer);
        }
    }

    public static native int nstb_vorbis_get_frame_float(long var0, int[] var2, long var3);

    public static int stb_vorbis_get_frame_float(@NativeType(value="stb_vorbis *") long f, @Nullable @NativeType(value="int *") int[] channels, @NativeType(value="float ***") PointerBuffer output) {
        if (Checks.CHECKS) {
            Checks.check(f);
            Checks.checkSafe(channels, 1);
            Checks.check(output, 1);
        }
        return STBVorbis.nstb_vorbis_get_frame_float(f, channels, MemoryUtil.memAddress(output));
    }

    public static native int nstb_vorbis_get_frame_short_interleaved(long var0, int var2, short[] var3, int var4);

    public static int stb_vorbis_get_frame_short_interleaved(@NativeType(value="stb_vorbis *") long f, int num_c, @NativeType(value="short *") short[] buffer) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_frame_short_interleaved(f, num_c, buffer, buffer.length);
    }

    public static native int nstb_vorbis_get_samples_float_interleaved(long var0, int var2, float[] var3, int var4);

    public static int stb_vorbis_get_samples_float_interleaved(@NativeType(value="stb_vorbis *") long f, int channels, @NativeType(value="float *") float[] buffer) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_samples_float_interleaved(f, channels, buffer, buffer.length);
    }

    public static native int nstb_vorbis_get_samples_short_interleaved(long var0, int var2, short[] var3, int var4);

    public static int stb_vorbis_get_samples_short_interleaved(@NativeType(value="stb_vorbis *") long f, int channels, @NativeType(value="short *") short[] buffer) {
        if (Checks.CHECKS) {
            Checks.check(f);
        }
        return STBVorbis.nstb_vorbis_get_samples_short_interleaved(f, channels, buffer, buffer.length);
    }

    static {
        LibSTB.initialize();
    }
}
