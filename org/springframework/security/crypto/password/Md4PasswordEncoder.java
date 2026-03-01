package org.springframework.security.crypto.password;

import java.util.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.Md4;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoderUtils;

@Deprecated
public class Md4PasswordEncoder
implements PasswordEncoder {
    private static final String PREFIX = "{";
    private static final String SUFFIX = "}";
    private StringKeyGenerator saltGenerator = new Base64StringKeyGenerator();
    private boolean encodeHashAsBase64;

    public void setEncodeHashAsBase64(boolean encodeHashAsBase64) {
        this.encodeHashAsBase64 = encodeHashAsBase64;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        String salt = PREFIX + this.saltGenerator.generateKey() + SUFFIX;
        return this.digest(salt, rawPassword);
    }

    private String digest(String salt, CharSequence rawPassword) {
        if (rawPassword == null) {
            rawPassword = "";
        }
        String saltedPassword = rawPassword + salt;
        byte[] saltedPasswordBytes = Utf8.encode(saltedPassword);
        Md4 md4 = new Md4();
        md4.update(saltedPasswordBytes, 0, saltedPasswordBytes.length);
        byte[] digest = md4.digest();
        String encoded = this.encode(digest);
        return salt + encoded;
    }

    private String encode(byte[] digest) {
        if (this.encodeHashAsBase64) {
            return Utf8.decode(Base64.getEncoder().encode(digest));
        }
        return new String(Hex.encode(digest));
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String salt = this.extractSalt(encodedPassword);
        String rawPasswordEncoded = this.digest(salt, rawPassword);
        return PasswordEncoderUtils.equals(encodedPassword.toString(), rawPasswordEncoded);
    }

    private String extractSalt(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(PREFIX);
        if (start != 0) {
            return "";
        }
        int end = prefixEncodedPassword.indexOf(SUFFIX, start);
        if (end < 0) {
            return "";
        }
        return prefixEncodedPassword.substring(start, end + 1);
    }
}
