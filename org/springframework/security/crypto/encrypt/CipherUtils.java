package org.springframework.security.crypto.encrypt;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

final class CipherUtils {
    private CipherUtils() {
    }

    static SecretKey newSecretKey(String algorithm, String password) {
        return CipherUtils.newSecretKey(algorithm, new PBEKeySpec(password.toCharArray()));
    }

    static SecretKey newSecretKey(String algorithm, PBEKeySpec keySpec) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(algorithm);
            return factory.generateSecret(keySpec);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Not a valid encryption algorithm", ex);
        }
        catch (InvalidKeySpecException ex) {
            throw new IllegalArgumentException("Not a valid secret key", ex);
        }
    }

    static Cipher newCipher(String algorithm) {
        try {
            return Cipher.getInstance(algorithm);
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalArgumentException("Not a valid encryption algorithm", ex);
        }
        catch (NoSuchPaddingException ex) {
            throw new IllegalStateException("Should not happen", ex);
        }
    }

    static <T extends AlgorithmParameterSpec> T getParameterSpec(Cipher cipher, Class<T> parameterSpecClass) {
        try {
            return cipher.getParameters().getParameterSpec(parameterSpecClass);
        }
        catch (InvalidParameterSpecException ex) {
            throw new IllegalArgumentException("Unable to access parameter", ex);
        }
    }

    static void initCipher(Cipher cipher, int mode, SecretKey secretKey) {
        CipherUtils.initCipher(cipher, mode, secretKey, null);
    }

    static void initCipher(Cipher cipher, int mode, SecretKey secretKey, byte[] salt, int iterationCount) {
        CipherUtils.initCipher(cipher, mode, secretKey, new PBEParameterSpec(salt, iterationCount));
    }

    static void initCipher(Cipher cipher, int mode, SecretKey secretKey, AlgorithmParameterSpec parameterSpec) {
        try {
            if (parameterSpec != null) {
                cipher.init(mode, (Key)secretKey, parameterSpec);
            } else {
                cipher.init(mode, secretKey);
            }
        }
        catch (InvalidKeyException ex) {
            throw new IllegalArgumentException("Unable to initialize due to invalid secret key", ex);
        }
        catch (InvalidAlgorithmParameterException ex) {
            throw new IllegalStateException("Unable to initialize due to invalid decryption parameter spec", ex);
        }
    }

    static byte[] doFinal(Cipher cipher, byte[] input) {
        try {
            return cipher.doFinal(input);
        }
        catch (IllegalBlockSizeException ex) {
            throw new IllegalStateException("Unable to invoke Cipher due to illegal block size", ex);
        }
        catch (BadPaddingException ex) {
            throw new IllegalStateException("Unable to invoke Cipher due to bad padding", ex);
        }
    }
}
