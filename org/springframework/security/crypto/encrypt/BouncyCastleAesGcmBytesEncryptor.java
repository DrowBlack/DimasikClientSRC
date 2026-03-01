package org.springframework.security.crypto.encrypt;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.springframework.security.crypto.encrypt.BouncyCastleAesBytesEncryptor;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.util.EncodingUtils;

public class BouncyCastleAesGcmBytesEncryptor
extends BouncyCastleAesBytesEncryptor {
    public BouncyCastleAesGcmBytesEncryptor(String password, CharSequence salt) {
        super(password, salt);
    }

    public BouncyCastleAesGcmBytesEncryptor(String password, CharSequence salt, BytesKeyGenerator ivGenerator) {
        super(password, salt, ivGenerator);
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        byte[] iv = this.ivGenerator.generateKey();
        GCMBlockCipher blockCipher = new GCMBlockCipher((BlockCipher)new AESFastEngine());
        blockCipher.init(true, (CipherParameters)new AEADParameters(this.secretKey, 128, iv, null));
        byte[] encrypted = this.process((AEADBlockCipher)blockCipher, bytes);
        return iv != null ? EncodingUtils.concatenate(iv, encrypted) : encrypted;
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes) {
        byte[] iv = EncodingUtils.subArray(encryptedBytes, 0, this.ivGenerator.getKeyLength());
        encryptedBytes = EncodingUtils.subArray(encryptedBytes, this.ivGenerator.getKeyLength(), encryptedBytes.length);
        GCMBlockCipher blockCipher = new GCMBlockCipher((BlockCipher)new AESFastEngine());
        blockCipher.init(false, (CipherParameters)new AEADParameters(this.secretKey, 128, iv, null));
        return this.process((AEADBlockCipher)blockCipher, encryptedBytes);
    }

    private byte[] process(AEADBlockCipher blockCipher, byte[] in) {
        byte[] buf = new byte[blockCipher.getOutputSize(in.length)];
        int bytesWritten = blockCipher.processBytes(in, 0, in.length, buf, 0);
        try {
            bytesWritten += blockCipher.doFinal(buf, bytesWritten);
        }
        catch (InvalidCipherTextException ex) {
            throw new IllegalStateException("unable to encrypt/decrypt", ex);
        }
        if (bytesWritten == buf.length) {
            return buf;
        }
        byte[] out = new byte[bytesWritten];
        System.arraycopy(buf, 0, out, 0, bytesWritten);
        return out;
    }
}
