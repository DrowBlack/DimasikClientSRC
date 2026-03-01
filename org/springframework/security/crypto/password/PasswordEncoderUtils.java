package org.springframework.security.crypto.password;

import java.security.MessageDigest;
import org.springframework.security.crypto.codec.Utf8;

final class PasswordEncoderUtils {
    private PasswordEncoderUtils() {
    }

    static boolean equals(String expected, String actual) {
        byte[] expectedBytes = PasswordEncoderUtils.bytesUtf8(expected);
        byte[] actualBytes = PasswordEncoderUtils.bytesUtf8(actual);
        return MessageDigest.isEqual(expectedBytes, actualBytes);
    }

    private static byte[] bytesUtf8(String s) {
        return s != null ? Utf8.encode(s) : null;
    }
}
