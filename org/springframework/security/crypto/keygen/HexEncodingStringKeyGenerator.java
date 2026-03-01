package org.springframework.security.crypto.keygen;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

final class HexEncodingStringKeyGenerator
implements StringKeyGenerator {
    private final BytesKeyGenerator keyGenerator;

    HexEncodingStringKeyGenerator(BytesKeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @Override
    public String generateKey() {
        return new String(Hex.encode(this.keyGenerator.generateKey()));
    }
}
