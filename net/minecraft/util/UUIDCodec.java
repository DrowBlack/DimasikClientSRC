package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.util.Util;

public final class UUIDCodec {
    public static final Codec<UUID> CODEC = Codec.INT_STREAM.comapFlatMap(p_239778_0_ -> Util.validateIntStreamSize(p_239778_0_, 4).map(UUIDCodec::decodeUUID), p_239780_0_ -> Arrays.stream(UUIDCodec.encodeUUID(p_239780_0_)));

    public static UUID decodeUUID(int[] bits) {
        return new UUID((long)bits[0] << 32 | (long)bits[1] & 0xFFFFFFFFL, (long)bits[2] << 32 | (long)bits[3] & 0xFFFFFFFFL);
    }

    public static int[] encodeUUID(UUID uuid) {
        long i = uuid.getMostSignificantBits();
        long j = uuid.getLeastSignificantBits();
        return UUIDCodec.encodeBits(i, j);
    }

    private static int[] encodeBits(long most, long least) {
        return new int[]{(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
    }
}
