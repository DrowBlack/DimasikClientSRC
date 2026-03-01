package org.springframework.security.crypto.password;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoderUtils;

@Deprecated
public class LdapShaPasswordEncoder
implements PasswordEncoder {
    private static final int SHA_LENGTH = 20;
    private static final String SSHA_PREFIX = "{SSHA}";
    private static final String SSHA_PREFIX_LC = "{SSHA}".toLowerCase();
    private static final String SHA_PREFIX = "{SHA}";
    private static final String SHA_PREFIX_LC = "{SHA}".toLowerCase();
    private BytesKeyGenerator saltGenerator;
    private boolean forceLowerCasePrefix;

    public LdapShaPasswordEncoder() {
        this(KeyGenerators.secureRandom());
    }

    public LdapShaPasswordEncoder(BytesKeyGenerator saltGenerator) {
        if (saltGenerator == null) {
            throw new IllegalArgumentException("saltGenerator cannot be null");
        }
        this.saltGenerator = saltGenerator;
    }

    private byte[] combineHashAndSalt(byte[] hash, byte[] salt) {
        if (salt == null) {
            return hash;
        }
        byte[] hashAndSalt = new byte[hash.length + salt.length];
        System.arraycopy(hash, 0, hashAndSalt, 0, hash.length);
        System.arraycopy(salt, 0, hashAndSalt, hash.length, salt.length);
        return hashAndSalt;
    }

    @Override
    public String encode(CharSequence rawPass) {
        byte[] salt = this.saltGenerator.generateKey();
        return this.encode(rawPass, salt);
    }

    private String encode(CharSequence rawPassword, byte[] salt) {
        MessageDigest sha = this.getSha(rawPassword);
        if (salt != null) {
            sha.update(salt);
        }
        byte[] hash = this.combineHashAndSalt(sha.digest(), salt);
        String prefix = this.getPrefix(salt);
        return prefix + Utf8.decode(Base64.getEncoder().encode(hash));
    }

    private MessageDigest getSha(CharSequence rawPassword) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA");
            sha.update(Utf8.encode(rawPassword));
            return sha;
        }
        catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("No SHA implementation available!");
        }
    }

    private String getPrefix(byte[] salt) {
        if (salt == null || salt.length == 0) {
            return this.forceLowerCasePrefix ? SHA_PREFIX_LC : SHA_PREFIX;
        }
        return this.forceLowerCasePrefix ? SSHA_PREFIX_LC : SSHA_PREFIX;
    }

    private byte[] extractSalt(String encPass) {
        String encPassNoLabel = encPass.substring(6);
        byte[] hashAndSalt = Base64.getDecoder().decode(encPassNoLabel.getBytes());
        int saltLength = hashAndSalt.length - 20;
        byte[] salt = new byte[saltLength];
        System.arraycopy(hashAndSalt, 20, salt, 0, saltLength);
        return salt;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return this.matches(rawPassword != null ? rawPassword.toString() : null, encodedPassword);
    }

    private boolean matches(String rawPassword, String encodedPassword) {
        String prefix = this.extractPrefix(encodedPassword);
        if (prefix == null) {
            return PasswordEncoderUtils.equals(encodedPassword, rawPassword);
        }
        byte[] salt = this.getSalt(encodedPassword, prefix);
        int startOfHash = prefix.length();
        String encodedRawPass = this.encode(rawPassword, salt).substring(startOfHash);
        return PasswordEncoderUtils.equals(encodedRawPass, encodedPassword.substring(startOfHash));
    }

    private byte[] getSalt(String encodedPassword, String prefix) {
        if (prefix.equals(SSHA_PREFIX) || prefix.equals(SSHA_PREFIX_LC)) {
            return this.extractSalt(encodedPassword);
        }
        if (!prefix.equals(SHA_PREFIX) && !prefix.equals(SHA_PREFIX_LC)) {
            throw new IllegalArgumentException("Unsupported password prefix '" + prefix + "'");
        }
        return null;
    }

    private String extractPrefix(String encPass) {
        if (!encPass.startsWith("{")) {
            return null;
        }
        int secondBrace = encPass.lastIndexOf(125);
        if (secondBrace < 0) {
            throw new IllegalArgumentException("Couldn't find closing brace for SHA prefix");
        }
        return encPass.substring(0, secondBrace + 1);
    }

    public void setForceLowerCasePrefix(boolean forceLowerCasePrefix) {
        this.forceLowerCasePrefix = forceLowerCasePrefix;
    }
}
