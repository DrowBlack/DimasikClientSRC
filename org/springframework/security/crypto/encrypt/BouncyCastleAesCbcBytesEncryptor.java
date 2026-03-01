package org.springframework.security.crypto.encrypt;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.springframework.security.crypto.encrypt.BouncyCastleAesBytesEncryptor;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.util.EncodingUtils;

public class BouncyCastleAesCbcBytesEncryptor
extends BouncyCastleAesBytesEncryptor {
    public BouncyCastleAesCbcBytesEncryptor(String password, CharSequence salt) {
        super(password, salt);
    }

    public BouncyCastleAesCbcBytesEncryptor(String password, CharSequence salt, BytesKeyGenerator ivGenerator) {
        super(password, salt, ivGenerator);
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        byte[] iv = this.ivGenerator.generateKey();
        PaddedBufferedBlockCipher blockCipher = new PaddedBufferedBlockCipher((BlockCipher)new CBCBlockCipher((BlockCipher)new AESFastEngine()), (BlockCipherPadding)new PKCS7Padding());
        blockCipher.init(true, (CipherParameters)new ParametersWithIV((CipherParameters)this.secretKey, iv));
        byte[] encrypted = this.process((BufferedBlockCipher)blockCipher, bytes);
        return iv != null ? EncodingUtils.concatenate(iv, encrypted) : encrypted;
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes) {
        byte[] iv = EncodingUtils.subArray(encryptedBytes, 0, this.ivGenerator.getKeyLength());
        encryptedBytes = EncodingUtils.subArray(encryptedBytes, this.ivGenerator.getKeyLength(), encryptedBytes.length);
        PaddedBufferedBlockCipher blockCipher = new PaddedBufferedBlockCipher((BlockCipher)new CBCBlockCipher((BlockCipher)new AESFastEngine()), (BlockCipherPadding)new PKCS7Padding());
        blockCipher.init(false, (CipherParameters)new ParametersWithIV((CipherParameters)this.secretKey, iv));
        return this.process((BufferedBlockCipher)blockCipher, encryptedBytes);
    }

    private byte[] process(BufferedBlockCipher blockCipher, byte[] in) {
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
