package org.springframework.security.crypto.argon2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.security.crypto.argon2.Argon2EncodingUtils;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Argon2PasswordEncoder
implements PasswordEncoder {
    private static final int DEFAULT_SALT_LENGTH = 16;
    private static final int DEFAULT_HASH_LENGTH = 32;
    private static final int DEFAULT_PARALLELISM = 1;
    private static final int DEFAULT_MEMORY = 16384;
    private static final int DEFAULT_ITERATIONS = 2;
    private final Log logger = LogFactory.getLog(this.getClass());
    private final int hashLength;
    private final int parallelism;
    private final int memory;
    private final int iterations;
    private final BytesKeyGenerator saltGenerator;

    public Argon2PasswordEncoder(int saltLength, int hashLength, int parallelism, int memory, int iterations) {
        this.hashLength = hashLength;
        this.parallelism = parallelism;
        this.memory = memory;
        this.iterations = iterations;
        this.saltGenerator = KeyGenerators.secureRandom(saltLength);
    }

    @Deprecated
    public static Argon2PasswordEncoder defaultsForSpringSecurity_v5_2() {
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }

    public static Argon2PasswordEncoder defaultsForSpringSecurity_v5_8() {
        return new Argon2PasswordEncoder(16, 32, 1, 16384, 2);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = this.saltGenerator.generateKey();
        byte[] hash = new byte[this.hashLength];
        Argon2Parameters params = new Argon2Parameters.Builder(2).withSalt(salt).withParallelism(this.parallelism).withMemoryAsKB(this.memory).withIterations(this.iterations).build();
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(params);
        generator.generateBytes(rawPassword.toString().toCharArray(), hash);
        return Argon2EncodingUtils.encode(hash, params);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        Argon2EncodingUtils.Argon2Hash decoded;
        if (encodedPassword == null) {
            this.logger.warn("password hash is null");
            return false;
        }
        try {
            decoded = Argon2EncodingUtils.decode(encodedPassword);
        }
        catch (IllegalArgumentException ex) {
            this.logger.warn("Malformed password hash", ex);
            return false;
        }
        byte[] hashBytes = new byte[decoded.getHash().length];
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(decoded.getParameters());
        generator.generateBytes(rawPassword.toString().toCharArray(), hashBytes);
        return Argon2PasswordEncoder.constantTimeArrayEquals(decoded.getHash(), hashBytes);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() == 0) {
            this.logger.warn("password hash is null");
            return false;
        }
        Argon2Parameters parameters = Argon2EncodingUtils.decode(encodedPassword).getParameters();
        return parameters.getMemory() < this.memory || parameters.getIterations() < this.iterations;
    }

    private static boolean constantTimeArrayEquals(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < expected.length; ++i) {
            result |= expected[i] ^ actual[i];
        }
        return result == 0;
    }
}
