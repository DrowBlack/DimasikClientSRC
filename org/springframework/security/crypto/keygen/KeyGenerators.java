package org.springframework.security.crypto.keygen;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.HexEncodingStringKeyGenerator;
import org.springframework.security.crypto.keygen.SecureRandomBytesKeyGenerator;
import org.springframework.security.crypto.keygen.SharedKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

public final class KeyGenerators {
    private KeyGenerators() {
    }

    public static BytesKeyGenerator secureRandom() {
        return new SecureRandomBytesKeyGenerator();
    }

    public static BytesKeyGenerator secureRandom(int keyLength) {
        return new SecureRandomBytesKeyGenerator(keyLength);
    }

    public static BytesKeyGenerator shared(int keyLength) {
        return new SharedKeyGenerator(KeyGenerators.secureRandom(keyLength).generateKey());
    }

    public static StringKeyGenerator string() {
        return new HexEncodingStringKeyGenerator(KeyGenerators.secureRandom());
    }
}
