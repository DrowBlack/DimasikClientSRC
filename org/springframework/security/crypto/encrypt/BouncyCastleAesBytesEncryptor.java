package org.springframework.security.crypto.encrypt;

import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

abstract class BouncyCastleAesBytesEncryptor
implements BytesEncryptor {
    final KeyParameter secretKey;
    final BytesKeyGenerator ivGenerator;

    BouncyCastleAesBytesEncryptor(String password, CharSequence salt) {
        this(password, salt, KeyGenerators.secureRandom(16));
    }

    BouncyCastleAesBytesEncryptor(String password, CharSequence salt, BytesKeyGenerator ivGenerator) {
        if (ivGenerator.getKeyLength() != 16) {
            throw new IllegalArgumentException("ivGenerator key length != block size 16");
        }
        this.ivGenerator = ivGenerator;
        PKCS5S2ParametersGenerator keyGenerator = new PKCS5S2ParametersGenerator();
        byte[] pkcs12PasswordBytes = PBEParametersGenerator.PKCS5PasswordToUTF8Bytes((char[])password.toCharArray());
        keyGenerator.init(pkcs12PasswordBytes, Hex.decode(salt), 1024);
        this.secretKey = (KeyParameter)keyGenerator.generateDerivedParameters(256);
    }
}
