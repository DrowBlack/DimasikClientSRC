package org.springframework.security.crypto.password;

public interface PasswordEncoder {
    public String encode(CharSequence var1);

    public boolean matches(CharSequence var1, String var2);

    default public boolean upgradeEncoding(String encodedPassword) {
        return false;
    }
}
