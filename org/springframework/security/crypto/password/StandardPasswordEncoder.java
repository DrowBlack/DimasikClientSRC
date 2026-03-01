package org.springframework.security.crypto.password;

import java.security.MessageDigest;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.Digester;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

@Deprecated
public final class StandardPasswordEncoder
implements PasswordEncoder {
    private static final int DEFAULT_ITERATIONS = 1024;
    private final Digester digester;
    private final byte[] secret;
    private final BytesKeyGenerator saltGenerator;

    public StandardPasswordEncoder() {
        this("");
    }

    public StandardPasswordEncoder(CharSequence secret) {
        this("SHA-256", secret);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return this.encode(rawPassword, this.saltGenerator.generateKey());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = this.decode(encodedPassword);
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return MessageDigest.isEqual(digested, this.digest(rawPassword, salt));
    }

    private StandardPasswordEncoder(String algorithm, CharSequence secret) {
        this.digester = new Digester(algorithm, 1024);
        this.secret = Utf8.encode(secret);
        this.saltGenerator = KeyGenerators.secureRandom();
    }

    private String encode(CharSequence rawPassword, byte[] salt) {
        byte[] digest = this.digest(rawPassword, salt);
        return new String(Hex.encode(digest));
    }

    private byte[] digest(CharSequence rawPassword, byte[] salt) {
        byte[] digest = this.digester.digest(EncodingUtils.concatenate(salt, this.secret, Utf8.encode(rawPassword)));
        return EncodingUtils.concatenate(salt, digest);
    }

    private byte[] decode(CharSequence encodedPassword) {
        return Hex.decode(encodedPassword);
    }
}
