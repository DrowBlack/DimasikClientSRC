package org.springframework.security.crypto.password;

import java.security.MessageDigest;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

public abstract class AbstractPasswordEncoder
implements PasswordEncoder {
    private final BytesKeyGenerator saltGenerator = KeyGenerators.secureRandom();

    protected AbstractPasswordEncoder() {
    }

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = this.saltGenerator.generateKey();
        byte[] encoded = this.encodeAndConcatenate(rawPassword, salt);
        return String.valueOf(Hex.encode(encoded));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = Hex.decode(encodedPassword);
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return AbstractPasswordEncoder.matches(digested, this.encodeAndConcatenate(rawPassword, salt));
    }

    protected abstract byte[] encode(CharSequence var1, byte[] var2);

    protected byte[] encodeAndConcatenate(CharSequence rawPassword, byte[] salt) {
        return EncodingUtils.concatenate(salt, this.encode(rawPassword, salt));
    }

    protected static boolean matches(byte[] expected, byte[] actual) {
        return MessageDigest.isEqual(expected, actual);
    }
}
