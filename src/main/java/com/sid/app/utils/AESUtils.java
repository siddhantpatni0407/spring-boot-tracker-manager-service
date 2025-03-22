package com.sid.app.utils;

import com.sid.app.config.AESProperties;
import com.sid.app.entity.EncryptionKey;
import com.sid.app.exception.InvalidEncryptionKeyException;
import com.sid.app.service.EncryptionKeyService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * @author Siddhant Patni
 */
@Component
public class AESUtils {

    @Autowired
    private EncryptionKeyService keyService;

    @Autowired
    private AESProperties aesProperties;

    private SecretKeySpec keySpec;

    @PostConstruct
    private void init() {
        updateKeySpec();
    }

    private void updateKeySpec() {
        EncryptionKey latestKey = keyService.getLatestKey();
        keySpec = new SecretKeySpec(latestKey.getSecretKey().getBytes(), aesProperties.getAlgorithm());
    }

    // Encrypt password using AES
    public String encrypt(String data) {
        try {
            updateKeySpec(); // Ensure the latest key is always used
            Cipher cipher = Cipher.getInstance(aesProperties.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new InvalidEncryptionKeyException("Error encrypting data. Possibly due to an invalid key configuration.", e);
        }
    }

    // Decrypt password using AES
    public String decrypt(String encryptedData) {
        try {
            updateKeySpec();
            Cipher cipher = Cipher.getInstance(aesProperties.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new InvalidEncryptionKeyException("Error decrypting data. Possibly due to an invalid key configuration or wrong key used.", e);
        }
    }

}