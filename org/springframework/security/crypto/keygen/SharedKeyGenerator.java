package org.springframework.security.crypto.keygen;

import org.springframework.security.crypto.keygen.BytesKeyGenerator;

final class SharedKeyGenerator
implements BytesKeyGenerator {
    private byte[] sharedKey;

    SharedKeyGenerator(byte[] sharedKey) {
        this.sharedKey = sharedKey;
    }

    @Override
    public int getKeyLength() {
        return this.sharedKey.length;
    }

    @Override
    public byte[] generateKey() {
        return this.sharedKey;
    }
}
