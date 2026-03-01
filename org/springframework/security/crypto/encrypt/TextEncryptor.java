package org.springframework.security.crypto.encrypt;

public interface TextEncryptor {
    public String encrypt(String var1);

    public String decrypt(String var1);
}
