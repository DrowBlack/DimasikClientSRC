package org.springframework.security.crypto.scrypt;

import java.security.MessageDigest;
import java.util.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.crypto.generators.SCrypt;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SCryptPasswordEncoder
implements PasswordEncoder {
    private static final int DEFAULT_CPU_COST = 65536;
    private static final int DEFAULT_MEMORY_COST = 8;
    private static final int DEFAULT_PARALLELISM = 1;
    private static final int DEFAULT_KEY_LENGTH = 32;
    private static final int DEFAULT_SALT_LENGTH = 16;
    private final Log logger = LogFactory.getLog(this.getClass());
    private final int cpuCost;
    private final int memoryCost;
    private final int parallelization;
    private final int keyLength;
    private final BytesKeyGenerator saltGenerator;

    public SCryptPasswordEncoder(int cpuCost, int memoryCost, int parallelization, int keyLength, int saltLength) {
        if (cpuCost <= 1) {
            throw new IllegalArgumentException("Cpu cost parameter must be > 1.");
        }
        if (memoryCost == 1 && cpuCost > 65536) {
            throw new IllegalArgumentException("Cpu cost parameter must be > 1 and < 65536.");
        }
        if (memoryCost < 1) {
            throw new IllegalArgumentException("Memory cost must be >= 1.");
        }
        int maxParallel = Integer.MAX_VALUE / (128 * memoryCost * 8);
        if (parallelization < 1 || parallelization > maxParallel) {
            throw new IllegalArgumentException("Parallelisation parameter p must be >= 1 and <= " + maxParallel + " (based on block size r of " + memoryCost + ")");
        }
        if (keyLength < 1 || keyLength > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Key length must be >= 1 and <= 2147483647");
        }
        if (saltLength < 1 || saltLength > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Salt length must be >= 1 and <= 2147483647");
        }
        this.cpuCost = cpuCost;
        this.memoryCost = memoryCost;
        this.parallelization = parallelization;
        this.keyLength = keyLength;
        this.saltGenerator = KeyGenerators.secureRandom(saltLength);
    }

    @Deprecated
    public static SCryptPasswordEncoder defaultsForSpringSecurity_v4_1() {
        return new SCryptPasswordEncoder(16384, 8, 1, 32, 64);
    }

    public static SCryptPasswordEncoder defaultsForSpringSecurity_v5_8() {
        return new SCryptPasswordEncoder(65536, 8, 1, 32, 16);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        return this.digest(rawPassword, this.saltGenerator.generateKey());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.length() < this.keyLength) {
            this.logger.warn("Empty encoded password");
            return false;
        }
        return this.decodeAndCheckMatches(rawPassword, encodedPassword);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        String[] parts = encodedPassword.split("\\$");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Encoded password does not look like SCrypt: " + encodedPassword);
        }
        long params = Long.parseLong(parts[1], 16);
        int cpuCost = (int)Math.pow(2.0, params >> 16 & 0xFFFFL);
        int memoryCost = (int)params >> 8 & 0xFF;
        int parallelization = (int)params & 0xFF;
        return cpuCost < this.cpuCost || memoryCost < this.memoryCost || parallelization < this.parallelization;
    }

    private boolean decodeAndCheckMatches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split("\\$");
        if (parts.length != 4) {
            return false;
        }
        long params = Long.parseLong(parts[1], 16);
        byte[] salt = this.decodePart(parts[2]);
        byte[] derived = this.decodePart(parts[3]);
        int cpuCost = (int)Math.pow(2.0, params >> 16 & 0xFFFFL);
        int memoryCost = (int)params >> 8 & 0xFF;
        int parallelization = (int)params & 0xFF;
        byte[] generated = SCrypt.generate((byte[])Utf8.encode(rawPassword), (byte[])salt, (int)cpuCost, (int)memoryCost, (int)parallelization, (int)this.keyLength);
        return MessageDigest.isEqual(derived, generated);
    }

    private String digest(CharSequence rawPassword, byte[] salt) {
        byte[] derived = SCrypt.generate((byte[])Utf8.encode(rawPassword), (byte[])salt, (int)this.cpuCost, (int)this.memoryCost, (int)this.parallelization, (int)this.keyLength);
        String params = Long.toString((int)(Math.log(this.cpuCost) / Math.log(2.0)) << 16 | this.memoryCost << 8 | this.parallelization, 16);
        StringBuilder sb = new StringBuilder((salt.length + derived.length) * 2);
        sb.append("$").append(params).append('$');
        sb.append(this.encodePart(salt)).append('$');
        sb.append(this.encodePart(derived));
        return sb.toString();
    }

    private byte[] decodePart(String part) {
        return Base64.getDecoder().decode(Utf8.encode(part));
    }

    private String encodePart(byte[] part) {
        return Utf8.decode(Base64.getEncoder().encode(part));
    }
}
