package org.springframework.security.crypto.password;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.util.EncodingUtils;

public class Pbkdf2PasswordEncoder
implements PasswordEncoder {
    private static final int DEFAULT_SALT_LENGTH = 16;
    private static final SecretKeyFactoryAlgorithm DEFAULT_ALGORITHM = SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256;
    private static final int DEFAULT_HASH_WIDTH = 256;
    private static final int DEFAULT_ITERATIONS = 310000;
    private final BytesKeyGenerator saltGenerator;
    private final byte[] secret;
    private final int iterations;
    private String algorithm = DEFAULT_ALGORITHM.name();
    private int hashWidth = 256;
    private boolean overrideHashWidth = true;
    private boolean encodeHashAsBase64;

    @Deprecated
    public Pbkdf2PasswordEncoder(CharSequence secret, int saltLength, int iterations, int hashWidth) {
        this.secret = Utf8.encode(secret);
        this.saltGenerator = KeyGenerators.secureRandom(saltLength);
        this.iterations = iterations;
        this.hashWidth = hashWidth;
        this.algorithm = SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1.name();
        this.overrideHashWidth = false;
    }

    public Pbkdf2PasswordEncoder(CharSequence secret, int saltLength, int iterations, SecretKeyFactoryAlgorithm secretKeyFactoryAlgorithm) {
        this.secret = Utf8.encode(secret);
        this.saltGenerator = KeyGenerators.secureRandom(saltLength);
        this.iterations = iterations;
        this.setAlgorithm(secretKeyFactoryAlgorithm);
    }

    @Deprecated
    public static Pbkdf2PasswordEncoder defaultsForSpringSecurity_v5_5() {
        return new Pbkdf2PasswordEncoder((CharSequence)"", 8, 185000, 256);
    }

    public static Pbkdf2PasswordEncoder defaultsForSpringSecurity_v5_8() {
        return new Pbkdf2PasswordEncoder((CharSequence)"", 16, 310000, DEFAULT_ALGORITHM);
    }

    public void setAlgorithm(SecretKeyFactoryAlgorithm secretKeyFactoryAlgorithm) {
        if (secretKeyFactoryAlgorithm == null) {
            throw new IllegalArgumentException("secretKeyFactoryAlgorithm cannot be null");
        }
        String algorithmName = secretKeyFactoryAlgorithm.name();
        try {
            SecretKeyFactory.getInstance(algorithmName);
            this.algorithm = algorithmName;
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Invalid algorithm '" + algorithmName + "'.", ex);
        }
        if (this.overrideHashWidth) {
            this.hashWidth = SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA1.equals((Object)secretKeyFactoryAlgorithm) ? 160 : (SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256.equals((Object)secretKeyFactoryAlgorithm) ? 256 : 512);
        }
    }

    public void setEncodeHashAsBase64(boolean encodeHashAsBase64) {
        this.encodeHashAsBase64 = encodeHashAsBase64;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = this.saltGenerator.generateKey();
        byte[] encoded = this.encode(rawPassword, salt);
        return this.encode(encoded);
    }

    private String encode(byte[] bytes) {
        if (this.encodeHashAsBase64) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        return String.valueOf(Hex.encode(bytes));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        byte[] digested = this.decode(encodedPassword);
        byte[] salt = EncodingUtils.subArray(digested, 0, this.saltGenerator.getKeyLength());
        return MessageDigest.isEqual(digested, this.encode(rawPassword, salt));
    }

    private byte[] decode(String encodedBytes) {
        if (this.encodeHashAsBase64) {
            return Base64.getDecoder().decode(encodedBytes);
        }
        return Hex.decode(encodedBytes);
    }

    private byte[] encode(CharSequence rawPassword, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(rawPassword.toString().toCharArray(), EncodingUtils.concatenate(salt, this.secret), this.iterations, this.hashWidth);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(this.algorithm);
            return EncodingUtils.concatenate(salt, skf.generateSecret(spec).getEncoded());
        }
        catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Could not create hash", ex);
        }
    }

    public static enum SecretKeyFactoryAlgorithm {
        PBKDF2WithHmacSHA1,
        PBKDF2WithHmacSHA256,
        PBKDF2WithHmacSHA512;

    }
}
