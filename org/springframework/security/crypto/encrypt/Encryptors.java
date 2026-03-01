package org.springframework.security.crypto.encrypt;

import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.HexEncodingTextEncryptor;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

public final class Encryptors {
    private Encryptors() {
    }

    public static BytesEncryptor stronger(CharSequence password, CharSequence salt) {
        return new AesBytesEncryptor(password.toString(), salt, KeyGenerators.secureRandom(16), AesBytesEncryptor.CipherAlgorithm.GCM);
    }

    public static BytesEncryptor standard(CharSequence password, CharSequence salt) {
        return new AesBytesEncryptor(password.toString(), salt, KeyGenerators.secureRandom(16));
    }

    public static TextEncryptor delux(CharSequence password, CharSequence salt) {
        return new HexEncodingTextEncryptor(Encryptors.stronger(password, salt));
    }

    public static TextEncryptor text(CharSequence password, CharSequence salt) {
        return new HexEncodingTextEncryptor(Encryptors.standard(password, salt));
    }

    public static TextEncryptor noOpText() {
        return NoOpTextEncryptor.INSTANCE;
    }

    private static final class NoOpTextEncryptor
    implements TextEncryptor {
        static final TextEncryptor INSTANCE = new NoOpTextEncryptor();

        private NoOpTextEncryptor() {
        }

        @Override
        public String encrypt(String text) {
            return text;
        }

        @Override
        public String decrypt(String encryptedText) {
            return encryptedText;
        }
    }
}
