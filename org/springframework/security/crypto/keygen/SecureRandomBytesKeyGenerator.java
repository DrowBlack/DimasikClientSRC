package org.springframework.security.crypto.keygen;

import java.security.SecureRandom;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;

final class SecureRandomBytesKeyGenerator
implements BytesKeyGenerator {
    private static final int DEFAULT_KEY_LENGTH = 8;
    private final SecureRandom random = new SecureRandom();
    private final int keyLength;

    SecureRandomBytesKeyGenerator() {
        this(8);
    }

    SecureRandomBytesKeyGenerator(int keyLength) {
        this.keyLength = keyLength;
    }

    @Override
    public int getKeyLength() {
        return this.keyLength;
    }

    @Override
    public byte[] generateKey() {
        byte[] bytes = new byte[this.keyLength];
        this.random.nextBytes(bytes);
        return bytes;
    }
}
