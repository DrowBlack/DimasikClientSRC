package org.springframework.security.crypto.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class Digester {
    private final String algorithm;
    private int iterations;

    Digester(String algorithm, int iterations) {
        Digester.createDigest(algorithm);
        this.algorithm = algorithm;
        this.setIterations(iterations);
    }

    byte[] digest(byte[] value) {
        MessageDigest messageDigest = Digester.createDigest(this.algorithm);
        for (int i = 0; i < this.iterations; ++i) {
            value = messageDigest.digest(value);
        }
        return value;
    }

    void setIterations(int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("Iterations value must be greater than zero");
        }
        this.iterations = iterations;
    }

    private static MessageDigest createDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No such hashing algorithm", ex);
        }
    }
}
